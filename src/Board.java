import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import javax.media.opengl.GL;

/**
 * Used to keep track of pieces to remove from board.
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <steinz08@gmail.com>
 */

public class Board {

	private Stone[][] stones;
	private ArrayList<Chain> chains = new ArrayList<Chain>();
	private ArrayList<Wei> weis = new ArrayList<Wei>();
	private Point lastTiziPosition;
	private int lastTiziNum;
	private int komi;
	private GamePlay gamePlay;
	private Point location;
	private String mode;

	/**
	 * size is the height or width of the goboard. Traditionally, Go is played
	 * on a 19 by 19 sized board. Other standard dimensions include 13 by 13 and
	 * 9 by 9. Hard coded in HadamaGo.java you can find the size of the goboard
	 * displayed on the GUI. If the goboard is created from an artificial
	 * intelligence or machine learning algorithm, then the size of the goboard
	 * must be obtained from this class.
	 */
	private int size;

	/**
	 * Create new Board instance to keep track of states of go game.
	 * 
	 * @param s
	 *            size of board (e.g., 9x9 vs 13x13 vs 19x19)
	 */
	public Board(int s, String m) {
		this.size = s;
		this.mode = m;
		this.stones = new Stone[s][s];
		this.gamePlay = new GamePlay(this, m, s);
	} // end constructor

	/**
	 * Add stone to game board. assume we have location, color, value for its
	 * newest position on board.
	 * 
	 * @param s
	 *            stone to add to board
	 */
	public void addStone(Stone s, Move m) {

		this.lastTiziPosition = new Point(-1, -1);
		this.lastTiziNum = 0;

		int x = s.getLocation().x;
		int y = s.getLocation().y;

		// if this position is Yan, then remove the Stone from the Yan
		// If Yan is occupied, then remove it from the ChainList
		if (this.stones[x][y] != null) {

			// *****for chain color = 3 **************
			// maybe become a bug for the future
			Stone a = this.stones[x][y].getChain().first();
			Point p = a.getLocation();

			if (this.stones[p.x][p.y].getColor() == 3)
				this.stones[p.x][p.y].getChain().updateChains(s, m);
			else
				this.stones[x][y].getChain().removeStone(s, m);

			if (this.stones[x][y] != null) {
				if (this.stones[x][y].getChain().size() == 0) {
					m.removeChain(this.stones[x][y].getChain());
					this.chains.remove(this.getRealChainIndex(this.stones[x][y]
							.getChain().getChainIndex()));
				} // end if
			} // end if

		} // end if

		this.stones[x][y] = s;

		// l is the liberties for Stone s
		int[] l = s.checkQi();

		// lc is the surrounding chains for Stone s
		Chain[] lc = s.checkChains();

		Chain c = new Chain(this);
		c.addToChain(s, m);
		this.chains.add(c);
		m.addChainStone(c, s);

		Wei[] lw = s.checkWeis();
		Wei w = new Wei(this);

		w.addToWei(s, m);
		if (s.getColor() != 3) {
			this.weis.add(w);
			m.addWeiStone(w, s);
		} // end if

		// ************Add stone into chain************************
		// check for friendly chain above
		if (l[0] == s.getColor()) {

			Iterator<Stone> it = lc[0].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				c.addToChain(next, m);
			} // end while

			if ((lc[0].getChainIndex() != c.getChainIndex())
					&& (this.getRealChainIndex(lc[0].getChainIndex()) != -1)) {
				m.removeChain(lc[0]);
				this.chains
						.remove(this.getRealChainIndex(lc[0].getChainIndex()));
			} // end if

		} // end if

		// check for friendly chain to right
		if (l[1] == s.getColor()) {

			Iterator<Stone> it = lc[1].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				c.addToChain(next, m);
			} // end while

			if ((lc[1].getChainIndex() != c.getChainIndex())
					&& (this.getRealChainIndex(lc[1].getChainIndex()) != -1)) {
				m.removeChain(lc[1]);
				this.chains
						.remove(this.getRealChainIndex(lc[1].getChainIndex()));
			} // end if

		} // end if

		// check for friendly chain below
		if (l[2] == s.getColor()) {

			Iterator<Stone> it = lc[2].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				c.addToChain(next, m);
			} // end while

			if ((lc[2].getChainIndex() != c.getChainIndex())
					&& (this.getRealChainIndex(lc[2].getChainIndex()) != -1)) {
				m.removeChain(lc[2]);
				this.chains
						.remove(this.getRealChainIndex(lc[2].getChainIndex()));
			} // end if

		} // end if

		// check for friendly chain to left
		if (l[3] == s.getColor()) {

			Iterator<Stone> it = lc[3].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				c.addToChain(next, m);
			} // end while

			if ((lc[3].getChainIndex() != c.getChainIndex())
					&& (this.getRealChainIndex(lc[3].getChainIndex()) != -1)) {
				m.removeChain(lc[3]);
				this.chains
						.remove(this.getRealChainIndex(lc[3].getChainIndex()));
			} // end if

		} // end if

		int c0 = -1;
		int c1 = -1;
		int c2 = -1;
		int c3 = -1;

		if (lc[0] != null) {
			c0 = lc[0].getChainIndex();
			if (lc[0].isYan())
				lc[0].recheckChains(s, m);
		} // end if

		if (lc[1] != null) {
			c1 = lc[1].getChainIndex();
			if ((lc[1].isYan()) && (c0 != c1))
				lc[1].recheckChains(s, m);
		} // end if

		if (lc[2] != null) {
			c2 = lc[2].getChainIndex();
			if ((lc[2].isYan()) && (c2 != c0) && (c2 != c1))
				lc[2].recheckChains(s, m);
		} // end if

		if (lc[3] != null) {
			c3 = lc[3].getChainIndex();
			if ((lc[3].isYan()) && (c3 != c0) && (c3 != c1) && (c3 != c2))
				lc[3].recheckChains(s, m);
		} // end if

		// chain top-left
		if ((lc[4] != null) && (l[4] == -1) && (lc[4].isYan()))
			lc[4].realYandetector(m);

		// chain top-right
		if ((lc[5] != null) && (l[5] == -1) && (lc[5].isYan()))
			lc[5].realYandetector(m);

		// chain bottom-right
		if ((lc[6] != null) && (l[6] == -1) && (lc[6].isYan()))
			lc[6].realYandetector(m);

		// chain bottom-left
		if ((lc[7] != null) && (l[7] == -1) && (lc[7].isYan()))
			lc[7].realYandetector(m);

		// ************Add stone into Wei************************
		// check for friendly wei above
		if (l[0] == s.getColor()) {

			Iterator<Stone> it = lw[0].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next, m);
			} // end while

			if ((lw[0].getWeiIndex() != w.getWeiIndex())
					&& (this.getRealWeiIndex(lw[0].getWeiIndex()) != -1)) {
				m.removeWei(lw[0]);
				this.weis.remove(this.getRealWeiIndex(lw[0].getWeiIndex()));
			} // end if

		} // end if

		// check for friendly wei to right
		if (l[1] == s.getColor()) {

			Iterator<Stone> it = lw[1].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next, m);
			} // end while

			if ((lw[1].getWeiIndex() != w.getWeiIndex())
					&& (this.getRealWeiIndex(lw[1].getWeiIndex()) != -1)) {
				m.removeWei(lw[1]);
				this.weis.remove(this.getRealWeiIndex(lw[1].getWeiIndex()));
			} // end if

		} // end if

		// check for friendly wei below
		if (l[2] == s.getColor()) {

			Iterator<Stone> it = lw[2].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next, m);
			} // end while

			if ((lw[2].getWeiIndex() != w.getWeiIndex())
					&& (this.getRealWeiIndex(lw[2].getWeiIndex()) != -1)) {
				m.removeWei(lw[2]);
				this.weis.remove(this.getRealWeiIndex(lw[2].getWeiIndex()));
			} // end if

		} // end if

		// check for friendly chain to left
		if (l[3] == s.getColor()) {

			Iterator<Stone> it = lw[3].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next, m);
			} // end while

			if ((lw[3].getWeiIndex() != w.getWeiIndex())
					&& (this.getRealWeiIndex(lw[3].getWeiIndex()) != -1)) {
				m.removeWei(lw[3]);
				this.weis.remove(this.getRealWeiIndex(lw[3].getWeiIndex()));
			} // end if

		} // end if

		// check for friendly wei to top-left
		if (l[4] == s.getColor()) {

			Iterator<Stone> it = lw[4].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next, m);
			} // end while

			if ((lw[4].getWeiIndex() != w.getWeiIndex())
					&& (this.getRealWeiIndex(lw[4].getWeiIndex()) != -1)) {
				m.removeWei(lw[4]);
				this.weis.remove(this.getRealWeiIndex(lw[4].getWeiIndex()));
			} // end if

		}// end if

		// check for friendly wei to top-right
		if (l[5] == s.getColor()) {

			Iterator<Stone> it = lw[5].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next, m);
			} // end while

			if ((lw[5].getWeiIndex() != w.getWeiIndex())
					&& (this.getRealWeiIndex(lw[5].getWeiIndex()) != -1)) {
				m.removeWei(lw[5]);
				this.weis.remove(this.getRealWeiIndex(lw[5].getWeiIndex()));
			} // end if

		} // end if

		// check for friendly wei to bottom-right
		if (l[6] == s.getColor()) {

			Iterator<Stone> it = lw[6].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next, m);
			} // end while

			if ((lw[6].getWeiIndex() != w.getWeiIndex())
					&& (this.getRealWeiIndex(lw[6].getWeiIndex()) != -1)) {
				m.removeWei(lw[6]);
				this.weis.remove(this.getRealWeiIndex(lw[6].getWeiIndex()));
			} // end if

		} // end if

		// check for friendly wei to bottom-left
		if (l[7] == s.getColor()) {

			Iterator<Stone> it = lw[7].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next, m);
			} // end while

			if ((lw[7].getWeiIndex() != w.getWeiIndex())
					&& (this.getRealWeiIndex(lw[7].getWeiIndex()) != -1)) {
				m.removeWei(lw[7]);
				this.weis.remove(this.getRealWeiIndex(lw[7].getWeiIndex()));
			} // end if

		} // end if

		// detect if wei contains Yan. If so, turn them into Yan and add into
		// Chain list.

		// if stone color = 3, trade it as empty, so that we do not call
		// yanDetector() and tizi()
		if (s.getColor() != 3) {
			w.yanDetector(m);
			this.tizi(m);
		} // end if

	} // end addStone()

	public Board removeStone(Point p, Move m) {
		Stone s = this.stones[p.x][p.y];
		m.removeStone(s);
		this.stones[p.x][p.y] = null;
		this.printBoard();
		return this;
	} // end removeStone()

	// clean chain to take away dead stones
	public void tizi(Move m) {

		// check each chain, if chain's Qis equals to zero, change them into Yan
		for (int i = 0; i < this.chains.size(); i++) {

			Chain c = this.chains.get(i);
			c.recheckQis(m);

			Stone s = c.first();
			Point p = s.getLocation();

			// if chain c's Qis equals to zero , turn all the stone into Qis
			if ((c.getQis().size() == 0) && (!c.isYan())) {

				int r = 0;
				Iterator<Stone> it2 = c.iterator();

				while (it2.hasNext()) {

					Stone next = it2.next();
					if (next.getColor() != 3) {
						this.lastTiziPosition = next.getLocation();
						r++;
					} // end if

					m.removeStone(next);
					next.setColor(-1);
					next.setYan(true);

					s = next;
					p = s.getLocation();
					this.stones[p.x][p.y] = s;
					m.addStone(next);

					// Add Qis back to surrounding stones
					Point top = new Point(p.x, p.y + 1);
					Point right = new Point(p.x + 1, p.y);
					Point bottom = new Point(p.x, p.y - 1);
					Point left = new Point(p.x - 1, p.y);

					Stone stTop = new Stone(this, top);
					Stone stRight = new Stone(this, right);
					Stone stBottom = new Stone(this, bottom);
					Stone stLeft = new Stone(this, left);

					if (top.y < this.size) {
						stTop = this.getStone(top);
						if (stTop.getChain() != null)
							stTop.getChain().addBackQis(stTop, s, m);
					} // end if

					if ((right.x < this.size) && (!stRight.isYan())) {
						stRight = this.getStone(right);
						if (stRight.getChain() != null)
							stRight.getChain().addBackQis(stRight, s, m);
					} // end if

					if (bottom.y > -1) {
						stBottom = this.getStone(bottom);
						if (stBottom.getChain() != null)
							stBottom.getChain().addBackQis(stBottom, s, m);
					} // end if

					if (left.x > -1) {
						stLeft = this.getStone(left);
						if (stLeft.getChain() != null)
							stLeft.getChain().addBackQis(stLeft, s, m);
					} // end if

				} // end while

				this.lastTiziNum = r;
				c.setYan(true);

				// clean the Qis for the Yan
				Iterator<Qi> it3 = c.getQis().iterator();
				while (it3.hasNext()) {
					Qi q = it3.next();
					m.removeChainQi(c, q);
				} // end while
				TreeSet<Qi> empty = new TreeSet<Qi>();
				c.setQis(empty);

				// RealYandetector
				c.realYandetector(m);

			} // end if

		} // end for

	} // end tizi()

	public void newGame() {
		this.stones = new Stone[this.size][this.size];
		this.chains = new ArrayList<Chain>();
		this.weis = new ArrayList<Wei>();
	} // end newGame()

	public Stone getStone(Point p) {
		Stone result = this.stones[p.x][p.y];
		if (result == null)
			result = new Stone(this, p);
		return result;
	} // end getStone()

	public int getRealChainIndex(int indexFound) {
		int result = 0;
		for (int i = 0; i < this.chains.size(); i++) {
			if (indexFound == this.chains.get(i).getChainIndex())
				return result;
			else
				result++;
		} // end for
		return -1;
	} // end getRealChainIndex()

	public int getRealWeiIndex(int indexFound) {
		int result = 0;
		for (int i = 0; i < this.weis.size(); i++) {
			if (indexFound == this.weis.get(i).getWeiIndex())
				return result;
			else
				result++;
		} // end for
		return -1;
	} // end getRealWeiIndex()

	public void printChains() {
		System.out.println("" + this.chains.size() + " chains");
		for (int i = 0; i < this.chains.size(); i++) {
			System.out.println("*************");
			Chain c = this.chains.get(i);
			System.out.println("Chain " + c.getChainIndex() + ": " + "isYan = "
					+ c.isYan() + " isZhenYan = " + c.isZhenYan()
					+ " isJiaYan = " + c.isJiaYan());
			System.out.println(c.toString().replaceAll("], ", "]\n"));
			System.out.println("Stone: " + Integer.toString(c.size()));
			System.out.println(c.getQis().toString().replaceAll("], ", "]\n"));
			System.out.println("liberties: "
					+ Integer.toString(c.getQis().size()));
		} // end for
		System.out.println("=================");
	} // end printChains()

	public void printWeis() {
		System.out.println("" + this.weis.size() + " wei");
		for (int i = 0; i < this.weis.size(); i++) {
			System.out.println("*************");
			Wei w = this.weis.get(i);
			System.out.println("Wei " + w.getWeiIndex());
			System.out.println(w.toString().replaceAll("], ", "]\n"));
			System.out.println("Stone: " + Integer.toString(w.size()));
		} // end for
		System.out.println("=================");
	} // end printWeis()

	public void printBoard() {
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {
				if (this.stones[i][j] != null) {
					if ((this.stones[i][j].getColor() == 0)
							|| (this.stones[i][j].getColor() == 1)
							|| (this.stones[i][j].getColor() == 3))
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

	/*
	 * getters and setters
	 */
	public void removeChain(int index) {
		this.chains.remove(index);
	} // end removeChain()

	public ArrayList<Chain> getChains() {
		return this.chains;
	} // end getStones()

	public void setChains(ArrayList<Chain> c) {
		this.chains = c;
	} // end setStones()

	public ArrayList<Wei> getWeis() {
		return this.weis;
	} // end getWeis()

	public void setWeis(ArrayList<Wei> w) {
		this.weis = w;
	} // end setWeis()

	public Stone[][] getStones() {
		return this.stones;
	} // end getStones()

	public void setStones(Stone[][] s) {
		this.stones = s;
	} // end setStones()

	public int getSize() {
		return this.size;
	} // end getSize()

	public void setSize(int size) {
		this.size = size;
	} // end setSize()

	public Point getLastTiziPosition() {
		return this.lastTiziPosition;
	} // end getLastTiziPosition

	public void setLastTiziPosition(Point lastTiziPosition) {
		this.lastTiziPosition = lastTiziPosition;
	} // end setLastTiziPosition()

	public int getLastTiziNum() {
		return this.lastTiziNum;
	} // end getLastTiziNum()

	public void setLastTiziNum(int lastTiziNum) {
		this.lastTiziNum = lastTiziNum;
	} // end setLastTiziNum()

	public int getKomi() {
		return this.komi;
	} // end getKomi()

	public void setKomi(int komi) {
		this.komi = komi;
	} // end setKomi()

	public GamePlay getGamePlay() {
		return gamePlay;
	}

	public void setGamePlay(GamePlay gamePlay) {
		this.gamePlay = gamePlay;
	}

	public Point getLocation() {
		return this.location;
	}

	public void setLocation(Point l) {
		this.location = l;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

} // end class
