/**
 * Board.java - Keeps track of stone interactions on the go game board.
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <steinz08@gmail.com>
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;

public class Board {

	/**
	 * In go the game board is a square grid on which stones are placed on the
	 * intersections of grid lines. Stones can have various "colors" like black
	 * (0) and white (1), but sometimes they're colors are non-visible values
	 * like -2 for yan (empty stones inside of a chain), -1 for empty
	 * intersections, 3 for black territory on the board and 4 for white
	 * territory.
	 */
	private Stone[][] stones;

	/**
	 * Stones that are horizontally or vertically adjacent on the game board
	 * form chains. In go, creating chains is advantageous at times because a
	 * chain is harder to capture than a single stone. Chains have liberties or
	 * qis, which are the open intersections horizontally or vertically adjacent
	 * to the chain's stones. A chain is captured by an enemy when its liberties
	 * are all taken away. Chains also have yan, which are open intersections
	 * inside a chain. With two true yan, a chain is living and cannot be
	 * capture by enemy stones.
	 */
	private ArrayList<Chain> chains = new ArrayList<Chain>();

	/**
	 * TODO: Needs comment
	 */
	private ArrayList<Wei> weis = new ArrayList<Wei>();

	/**
	 * TODO: Needs comment
	 */
	private Stack<Move> moves = new Stack<Move>();

	/**
	 * TODO: Needs comment
	 */
	private Point lastTiziPosition;

	/**
	 * TODO: Needs comment
	 */
	private int lastNumTizi;

	/**
	 * Since the player with black stones has first move advantage, the white
	 * player gets compensation or komi of 2.5 extra points in the game.
	 */
	private double komi = 2.5;

	/**
	 * Keep track of intersections where it's not legal to place black stones on
	 * the game board.
	 */
	private ArrayList<Point> illegalMovesforBlack = new ArrayList<Point>();

	/**
	 * Keep track of intersections where it's not legal to place white stones on
	 * the game board.
	 */
	private ArrayList<Point> illegalMovesforWhite = new ArrayList<Point>();

	/**
	 * size is the height or width of the game board. Traditionally, Go is
	 * played on a 19 by 19 sized board. Other standard dimensions include 13 by
	 * 13 and 9 by 9. Hard coded in HadamaGo.java you can find the size of the
	 * board displayed on the GUI. If the board is created from an artificial
	 * intelligence or machine learning algorithm, then its size must be
	 * obtained from this class.
	 */
	private int size;

	/**
	 * Create new Board instance to keep track of states of go game.
	 * 
	 * @param s
	 *            size of board (e.g., 9x9 vs 13x13 vs 19x19)
	 */
	public Board(int s) {
		this.size = s;
		this.stones = new Stone[s][s];
	} // end constructor

	/**
	 * Add stone to game board. assume we have location, color, value for its
	 * newest position on board.
	 * 
	 * @param s
	 *            stone to add to board
	 */
	public ArrayList<Chain> addStone(Stone s) {

		// TODO: comment
		this.lastTiziPosition = new Point(-1, -1);
		this.lastNumTizi = 0;

		// get location of stone on game board. The origin of the board is at
		// the bottom left corner. X increases to the right, and y increases
		// above.
		int x = s.getLocation().x;
		int y = s.getLocation().y;

		// if this position is Yan, then remove the Stone from the Yan
		// If Yan is occupied, then remove it from the ChainList
		if (this.stones[x][y] != null) {

			// ***** for chain color = 3 **************
			Point p = new Point(0, 0);

			// System.out.println(":::::::::::::::");
			// System.out.println(this.board[x][y]);
			// System.out.println(this.board[x][y].getChain());
			// System.out.println(this.board[x][y].getChain().first());
			// System.out.println(":::::::::::::::");

			// get location of first stone in the chain at location (x,y)
			if ((this.stones[x][y] != null)
					&& (this.stones[x][y].getChain() != null)
					&& !this.stones[x][y].getChain().isEmpty()) {
				// maybe become a bug for the future
				Stone a = this.stones[x][y].getChain().first();
				p = a.getLocation();
			} // end if

			// if the color of the chain's first stone is 3 or 4 (where 3 is
			// black territory and 4 is white territory) then update the chains
			// around that chain. otherwise, remove that stone from the chain.
			if ((this.stones[p.x][p.y] != null)
					&& ((this.stones[p.x][p.y].getColor() == 3) || (this.stones[p.x][p.y]
							.getColor() == 4)))
				this.stones[p.x][p.y].getChain().updateChains(s);
			else
				this.stones[x][y].getChain().removeStone(s);

			// remove the chain if it no longer has any stones in it.
			if ((this.stones[x][y] != null)
					&& (this.stones[x][y].getChain().size() == 0)) {
				int index = this.realChainIndex(this.stones[x][y].getChain()
						.getChainIndex());
				if (index > -1)
					this.chains.remove(index);
			} // end if

		} // end if

		// place stone on game board
		this.stones[x][y] = s;

		// l contains the qi (or yan or liberties) for Stone s at (x,y).
		int[] l = s.checkQi(this.stones);

		// lc is the surrounding chains for Stone s
		Chain[] lc = s.checkChains(this.stones);

		// add stone s to new chain, then store new chain in chains list.
		Chain c = new Chain(this);
		c.addToChain(s, this.stones);
		this.chains.add(c);

		// lw contains the weis (or stones diagonally adjacent to each other)
		// for stone s at (x,y).
		Wei[] lw = s.checkWeis(this.stones);

		// add stone to new wei, then store new wei in weis list.
		Wei w = new Wei(this);
		w.addToWei(s);
		if ((s.getColor() != 3) && (s.getColor() != 4))
			this.weis.add(w);

		// ************Add stone into chain************************
		// check for a friendly chain horizontally or vertically adjacent to
		// stone s at (x,y)
		for (int i = 0; i < 4; i++) {

			if ((l[i] == s.getColor()) && (lc[i] != null)) {

				// if chain adjacent is smaller than chain containing stone
				// s, then move the stones from the adjacent chain to the
				// original chain. otherwise, move all the stones in the
				// original stone's chain to the chain adjacent.
				if (lc[i].size() < c.size()) {

					Iterator<Stone> it = lc[i].iterator();
					while (it.hasNext()) {
						Stone next = it.next();
						c.addToChain(next, this.stones);
					} // end while

					if ((lc[i].getChainIndex() != c.getChainIndex())
							&& (this.realChainIndex(lc[i].getChainIndex()) != -1))
						this.chains.remove(this.realChainIndex(lc[i]
								.getChainIndex()));

				} else {

					Iterator<Stone> it = c.iterator();
					while (it.hasNext()) {
						Stone next = it.next();
						lc[i].addToChain(next, this.stones);
					} // end while

					if ((lc[i].getChainIndex() != c.getChainIndex())
							&& (this.realChainIndex(c.getChainIndex()) != -1))
						this.chains.remove(this.realChainIndex(c
								.getChainIndex()));

					c = lc[i];

				} // end if

			} // end if

		} // end for

		// TODO: comment
		int[] cIndex = { -1, -1, -1, -1 };

		boolean isEqual = false;

		// TODO: comment
		for (int i = 0; i < 4; i++) {

			if (lc[i] != null) {

				// get chain index of adjacent chain
				cIndex[i] = lc[i].getChainIndex();

				// check if chain index isn't the same as a previously
				// discovered one
				if (i > 0)
					isEqual = isEqual || (cIndex[i] == cIndex[i - 1]);

				// recheck chains adjacent to stone s if chain index is equal to
				// a previous one, or the chain is yan
				if (lc[i].isYan() && !isEqual)
					lc[i].recheckChains(s);

			} // end if

		} // end for

		// TODO: comment
		for (int i = 4; i < 8; i++) {
			if ((lc[i] != null) && (l[i] == -1) && (lc[i].isYan()))
				lc[i].realYanDetector();
		} // end for

		// ************Add stone into Wei************************
		// check for a friendly wei horizontally, vertically or diagonally
		// adjacent to stone s at (x,y)
		for (int i = 0; i < 8; i++) {

			if ((l[i] == s.getColor()) && (lw[i] != null)) {

				// if wei adjacent is smaller than wei containing stone
				// s, then move the stones from the adjacent wei to the
				// original wei. otherwise, move all the stones in the
				// original stone's wei to the wei adjacent.
				if (lw[i].size() < w.size()) {

					Iterator<Stone> it = lw[i].iterator();
					while (it.hasNext()) {
						Stone next = it.next();
						w.addToWei(next);
					} // end while

					if ((lw[i].getWeiIndex() != w.getWeiIndex())
							&& (this.realWeiIndex(lw[i].getWeiIndex()) != -1))
						this.weis
								.remove(this.realWeiIndex(lw[i].getWeiIndex()));

				} else {

					Iterator<Stone> it = w.iterator();
					while (it.hasNext()) {
						Stone next = it.next();
						lw[i].addToWei(next);
					} // end while

					if ((lw[i].getWeiIndex() != w.getWeiIndex())
							&& (this.realWeiIndex(w.getWeiIndex()) != -1))
						this.weis.remove(this.realWeiIndex(w.getWeiIndex()));

					w = lw[i];

				} // end if

			} // end if

		} // end for

		// detect if wei contains Yan. If so, turn them into Yan and add into
		// Chain list.
		// if stone color = 3 or 4, trade it as empty, so that we do not call
		// yanDetector() and tizi()
		ArrayList<Chain> removedChains = new ArrayList<Chain>();
		if ((s.getColor() != 3) && (s.getColor() != 4)) {
			w.yanDetector();
			removedChains = this.tizi();
		} // end if

		// run illegal Move Detector to find out illegal moves for both players
		this.illegalMovesforBlack = new ArrayList<Point>();
		this.illegalMovesforWhite = new ArrayList<Point>();
		this.illegalMoveDetector();

		// return list of removed chains
		return removedChains;

	} // end addStone()

	/**
	 * TODO: comment
	 * 
	 * @return list of chains removed during tizi
	 */
	public ArrayList<Chain> tizi() {

		// setup list of chains removed during tizi
		ArrayList<Chain> removedChains = new ArrayList<Chain>();

		// check each chain, if chain's Qis equals to zero, change them into Yan
		for (int i = 0; i < this.chains.size(); i++) {

			// get chain in chain list
			Chain c = this.chains.get(i);

			// recheck qis in current chain
			c.recheckQis();

			// get first stone in current chain, and its location and wei
			Stone s = c.first();
			Point p = s.getLocation();
			Wei w = s.getWei();

			// if chain c's Qis equals to zero , turn all the stone into Qis
			if ((c.getQis().size() == 0) && (!c.isYan())) {

				// TODO: comment
				Chain copyC = new Chain(this);
				Iterator<Stone> it3 = c.iterator();
				Stone firstStone = c.first();
				if (firstStone.getColor() != 4 && firstStone.getColor() != 3) {
					// make a copy of chain
					while (it3.hasNext()) {
						Stone next = it3.next();
						copyC.add((Stone) next.clone());
					} // end while
					removedChains.add(copyC);
				} // end if

				int r = 0;
				Iterator<Stone> it2 = c.iterator();

				// TODO: comment
				while (it2.hasNext()) {

					Stone next = it2.next();

					// TODO: comment
					if ((next.getWei() != null)
							&& (this.realWeiIndex(next.getWei().getWeiIndex()) != -1)) {
						if (w.size() == 0) {
							this.weis.remove(this.realWeiIndex(next.getWei()
									.getWeiIndex()));
						} else if (w.size() == 1) {
							w.remove(next);
							this.weis.remove(this.realWeiIndex(next.getWei()
									.getWeiIndex()));
						} else {
							w.remove(next);
						} // end if
					} // end if

					// TODO: comment
					if (next.getColor() == 1)
						next.setBelongto(0);
					else if (next.getColor() == 0)
						next.setBelongto(1);
					else if (next.getColor() == 3)
						next.setBelongto(1);
					else if (next.getColor() == 4)
						next.setBelongto(0);

					// TODO: comment
					if ((next.getColor() != 3) && (next.getColor() != 4)) {
						this.lastTiziPosition = next.getLocation();
						r++;
					} // end if

					// TODO: comment
					next.setColor(-1);
					next.setYan(true);

					// TODO: comment
					s = next;
					p = s.getLocation();
					this.stones[p.x][p.y] = s;

					// Add Qis back to surrounding stones
					Point top = new Point(p.x, p.y + 1);
					Point right = new Point(p.x + 1, p.y);
					Point bottom = new Point(p.x, p.y - 1);
					Point left = new Point(p.x - 1, p.y);

					// TODO: comment
					Stone stTop = new Stone();
					Stone stRight = new Stone();
					Stone stBottom = new Stone();
					Stone stLeft = new Stone();

					// TODO: comment
					if (top.y < size) {
						stTop = this.stones[top.x][top.y];
						if (stTop != null)
							stTop.getChain().addBackQis(stTop, s);
					} // end if

					// TODO: comment
					if ((right.x < size) && (!stRight.isYan())) {
						stRight = this.stones[right.x][right.y];
						if (stRight != null)
							stRight.getChain().addBackQis(stRight, s);
					} // end if

					// TODO: comment
					if (bottom.y > -1) {
						stBottom = this.stones[bottom.x][bottom.y];
						if (stBottom != null)
							stBottom.getChain().addBackQis(stBottom, s);
					} // end if

					// TODO: comment
					if (left.x > -1) {
						stLeft = this.stones[left.x][left.y];
						if (stLeft != null)
							stLeft.getChain().addBackQis(stLeft, s);
					} // end if

				} // end while

				// TODO: comment
				this.lastNumTizi = r;

				// TODO: comment
				c.setYan(true);

				// clean the Qis for the Yan
				TreeSet<Qi> empty = new TreeSet<Qi>();
				c.setQis(empty);
				c.realYanDetector();

			} // end if

		} // end for

		// return list of chains removed during tizi
		return removedChains;

	} // end tizi()

	/**
	 * legal move 1) [empty] 2) not surrounded 3) "double jeopardy" (sukuru) -
	 * you take one piece and then opponent takes your piece
	 * 
	 * illegal move 1) surrounded 2) not empty
	 * */
	public void illegalMoveDetector() {

		// TODO: comment
		this.illegalMovesforBlack = new ArrayList<Point>();
		this.illegalMovesforWhite = new ArrayList<Point>();

		// TODO: comment
		for (int i = 0; i < chains.size(); i++) {

			// TODO: comment
			Chain c = chains.get(i);
			if (c.isYan()) {

				// TODO: comment
				if (c.size() == 1) {

					// TODO: comment
					Stone first = c.first();
					Point p = first.getLocation();
					Chain[] lc = first.checkChains(this.stones);

					// TODO: comment
					int numTiziWhite = 0;
					int numHuoziBlack = 0;
					int numTiziBlack = 0;
					int numHuoziWhite = 0;
					int numStoneTiziBlack = 0;
					int numStoneTiziWhite = 0;

					// TODO: comment
					for (int j = 0; j < 4; j++) {

						// TODO: comment
						if ((lc[j] != null) && (!lc[j].isEmpty())) {

							// System.out.println("!!!!!!!!!!!!!!!!");
							// System.out.println(lc[j]);
							// System.out.println(lc[j].first());
							// System.out.println(lc[j].first().getColor());
							// System.out.println(lc[j].getQis());
							// System.out.println(lc[j].getQis().size());

							// case 1
							// TODO: comment
							if ((lc[j].first().getColor() == 0)
									&& (lc[j].getQis().size() == 1)) {
								numTiziWhite++;
								numStoneTiziWhite = lc[j].size();
							} // end if

							// case 2
							// TODO: comment
							if ((lc[j].first().getColor() == 1)
									&& (lc[j].getQis().size() == 1)) {
								numTiziBlack++;
								numStoneTiziBlack = lc[j].size();
							} // end if

							// case 3
							// TODO: comment
							if ((lc[j].first().getColor() == 0)
									&& (lc[j].getQis().size() > 1))
								numHuoziWhite++;

							// case 4
							// TODO: comment
							if ((lc[j].first().getColor() == 1)
									&& (lc[j].getQis().size() > 1))
								numHuoziBlack++;

						} // end if

					} // end for

					// TODO: comment
					if ((numTiziWhite == 0) && (numHuoziWhite > 1)
							&& (numHuoziBlack == 0))
						this.illegalMovesforWhite.add(p);

					// TODO: comment
					if ((numTiziBlack == 0) && (numHuoziBlack > 1)
							&& (numHuoziWhite == 0))
						this.illegalMovesforBlack.add(p);

					// TODO: comment
					if ((numTiziWhite == 1) && (numHuoziWhite > 1)
							&& (getLastTiziPosition().equals(p))
							&& (numStoneTiziWhite != 2))
						this.illegalMovesforWhite.add(p);

					// TODO: comment
					if ((numTiziBlack == 1) && (numHuoziBlack > 1)
							&& (getLastTiziPosition().equals(p))
							&& (numStoneTiziBlack != 2))
						this.illegalMovesforBlack.add(p);

				} // end if

			} // end if

			// it is not Yan
			else {

				// TODO: comment
				Iterator<Stone> it = c.iterator();
				while (it.hasNext()) {

					Stone next = it.next();
					int color = next.getColor();
					Point p = next.getLocation();

					if (color == 0 || color == 1) {
						this.illegalMovesforWhite.add(p);
						this.illegalMovesforBlack.add(p);
					} // end if

				} // end while

			} // end if

		} // end for

	} // end illegalMoveDetector()

	/**
	 * Get real index of chain at found index
	 * 
	 * @param indexFound
	 * @return
	 */
	public int realChainIndex(int indexFound) {
		int result = 0;
		for (int i = 0; i < this.chains.size(); i++) {
			if (indexFound == this.chains.get(i).getChainIndex())
				return result;
			else
				result++;
		} // end for
			// did not find the result
		return -1;
	} // end realChainIndex()

	/**
	 * Get real index of wei at found index
	 * 
	 * @param indexFound
	 * @return
	 */
	public int realWeiIndex(int indexFound) {
		int result = 0;
		for (int i = 0; i < weis.size(); i++) {
			if (indexFound == weis.get(i).getWeiIndex())
				return result;
			else
				result++;
		} // end for
			// did not find the result
		return -1;
	} // end rrealWeiIndex()

	public double[] getScores() {
		double[] scores = { 0.0f, 0.0f };
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {
				if (this.stones[i][j] != null) {
					if (this.stones[i][j].getBelongto() == 0)
						scores[0]++;
					else if (this.stones[i][j].getBelongto() == 1)
						scores[1]++;
				} // end if
			} // end for
		} // end for
		scores[1] += this.komi;
		return scores;
	} // end scores

	public void printScores() {

		double scoreBlack = 0;
		double scoreWhite = 0;
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {
				if (this.stones[i][j] != null) {
					if (this.stones[i][j].getBelongto() == 0)
						scoreBlack++;
					else if (this.stones[i][j].getBelongto() == 1)
						scoreWhite++;
				} // end if
			} // end for
		} // end for

		scoreWhite = scoreWhite + komi;
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("Score:");
		System.out.println("Komi =" + komi);
		System.out.println("Black Player =" + scoreBlack);
		System.out.println("White Player =" + scoreWhite);

		if (scoreBlack > scoreWhite)
			System.out.println("Bloack Player won this game!");
		else
			System.out.println("White Player won this game!");

		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

	} // end printScores()

	public void printIllegalMoves() {
		System.out.println("++++++++++++++++++++++++++++++++++++");
		System.out.println("Illegal Moves for Black Players:");
		for (int i = 0; i < this.illegalMovesforBlack.size(); i++)
			System.out.println(this.illegalMovesforBlack.get(i));
		System.out.println("Illegal Moves for White Players:");
		for (int i = 0; i < this.illegalMovesforWhite.size(); i++)
			System.out.println(this.illegalMovesforWhite.get(i));
		System.out.println("++++++++++++++++++++++++++++++++++++");
	} // end printIllegalMoves()

	public void printMoves() {
		System.out.println("========================");
		Iterator<Move> it = this.moves.iterator();
		while (it.hasNext()) {
			Move next = it.next();
			System.out.println(next.toString());
			System.out.println("========================");
		} // end while
	} // end printMoves()

	public void printChains() {
		System.out.println("" + this.chains.size() + " chains");
		for (int i = 0; i < this.chains.size(); i++) {
			System.out.println("??????????????????????");
			Chain c = this.chains.get(i);
			System.out.println("Chain " + c.getChainIndex() + ": ["
					+ "isYan = " + c.isYan() + " isZhenYan = " + c.isZhenYan()
					+ " isJiaYan = " + c.isJiaYan() + "]");
			System.out.println(c.toString().replaceAll("],", "]\n"));
			System.out.println("Total Stone #: " + Integer.toString(c.size()));
			System.out.println(c.getQis().toString().replaceAll("],", "]\n"));
			System.out.println("Total Qi #: "
					+ Integer.toString(c.getQis().size()));
		} // end for
		System.out.println("??????????????????????");
	} // end printChains()

	public void printWeis() {
		System.out.println("" + this.weis.size() + " wei");
		for (int i = 0; i < this.weis.size(); i++) {
			System.out.println("*************");
			Wei w = this.weis.get(i);
			System.out.println("Wei " + w.getWeiIndex());
			System.out.println(w.toString().replaceAll("],", "]\n"));
			System.out.println("Total Stone #: " + Integer.toString(w.size()));
		} // end for
		System.out.println("*************");
	} // end printChains()

	public void printBoard() {
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {
				if (this.stones[i][j] != null) {
					if ((this.stones[i][j].getColor() == 0)
							|| (this.stones[i][j].getColor() == 1)
							|| (this.stones[i][j].getColor() == 3)
							|| (this.stones[i][j].getColor() == 4))
						System.out.print("[+" + this.stones[i][j].getColor()
								+ "]");
					else
						System.out.print("[" + this.stones[i][j].getColor()
								+ "]");
				} else
					System.out.print("[**]");
				System.out.print(" ");
			} // end for
			System.out.println(" ");
		} // end for
		System.out.println();
	} // end printBoard()

	public Stone getStone(Point p) {
		Stone result;
		// TODO: this may cause problems!
		if ((p.x > -1) && (p.x < this.size) && (p.y > -1) && (p.y < this.size)) {
			result = this.stones[p.x][p.y];
			if (result == null)
				result = new Stone(p);
		} else
			result = new Stone(p);
		return result;
	} // end getStone()

	public ArrayList<Chain> getChains() {
		return this.chains;
	} // end getChains()

	public void setChains(ArrayList<Chain> c) {
		this.chains = c;
	} // end setChains()

	public ArrayList<Point> getIllegalMovesforBlack() {
		return this.illegalMovesforBlack;
	} // end getChains()

	public void setIllegalMovesforBlack(ArrayList<Point> ib) {
		this.illegalMovesforBlack = ib;
	} // end setChains()

	public ArrayList<Point> getIllegalMovesforWhite() {
		return this.illegalMovesforWhite;
	} // end getChains()

	public void setIllegalMovesforWhite(ArrayList<Point> iw) {
		this.illegalMovesforWhite = iw;
	} // end setChains()

	public ArrayList<Wei> getWeis() {
		return this.weis;
	} // end getWeis()

	public void setWeis(ArrayList<Wei> w) {
		this.weis = w;
	} // end setWeis()

	public Stone[][] getBoard() {
		return this.stones;
	} // end getBoard()

	public void setBoard(Stone[][] b) {
		this.stones = b;
	} // end getBoard()

	public int getSize() {
		return this.size;
	} // end getSize()

	public void setSize(int size) {
		this.size = size;
	} // end setSize()

	public Stack<Move> getMoves() {
		return this.moves;
	} // end getMoves()

	public void setMoves(Stack<Move> moves) {
		this.moves = moves;
	} // end setMoves()

	public Point getLastTiziPosition() {
		return this.lastTiziPosition;
	} // end getLastTiziPosition()

	public void setLastTiziPosition(Point lastTiziPosition) {
		this.lastTiziPosition = lastTiziPosition;
	} // end setLastTiziPosition()

	public int getLastTiziNum() {
		return this.lastNumTizi;
	} // end getLastTiziNum

	public void setLastTiziNum(int lastTiziNum) {
		this.lastNumTizi = lastTiziNum;
	} // end setLastTiziNum()

} // end class
