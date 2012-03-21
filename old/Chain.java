import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Head Node. Tail Node. Check Qis in order: top, right, bottom, left. If you
 * add stone of same color horiz/vert adjacent to chain, have tail of chain
 * point to new stone and set new stone as tail. If chain of same color
 * horiz/vert adjacent to another chain, tail of first chain links to head of
 * second chain, then we add all nodes of second chain to first chain.
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <steinz08@gmail.com>
 * 
 */
public class Chain extends TreeSet<Stone> {

	/**
	 * Version ID required for Hadama Go.
	 */
	private static final long serialVersionUID = 4781863948394812333L;

	/**
	 * Qis is an ArrayList of Points whose coordinates are those of the goboard
	 * position of the chain's liberties. Liberties of a chain are positions on
	 * the goboard that are horizontally or vertically adjacent to the chain and
	 * are not occupied by an active stone.
	 */
	private TreeSet<Qi> qis = new TreeSet<Qi>();
	private int boardSize;
	private Board board;
	private boolean isYan = false;
	private boolean isZhenYan = false;
	private boolean isJiaYan = false;
	/**
	 * chainNum gives each chain a unique number id.
	 */
	private int chainNum;

	/**
	 * counter counts how many chains have been made since the beginning of the
	 * game.
	 */
	private static int counterOfChain;

	/**
	 * Chain constructor. When two stones of the same color are adjacent
	 * horizontally or vertically, they make a chain. The Chain class is an
	 * ArrayList of Stones that form a chain. In Board.java, addStone() will
	 * create a chain for a single stone if that stone is placed on the goboard
	 * and has all four liberties.
	 */
	public Chain(Board b) {
		this.boardSize = b.getSize();
		this.board = b;
		this.chainNum = Chain.counterOfChain;
		Chain.counterOfChain++;
	} // end constructor

	/**
	 * Chain constructor. When two stones of the same color are adjacent
	 * horizontally or vertically, they make a chain. The Chain class is an
	 * ArrayList of Stones that form a chain. In Board.java, addStone() will
	 * create a chain for a single stone if that stone is placed on the goboard
	 * and has all four liberties.
	 * 
	 * @param s
	 *            the size of the goboard the chain belongs to
	 */
	public Chain(Board b, Stone s) {
		this.addToChain(s);
		this.boardSize = b.getSize();
		this.board = b;
	} // end constructor

	/**
	 * Add a stone to the chain. This only works if the stone being added is the
	 * same color as the rest of the stones in the chain.
	 * 
	 * @param s
	 *            stone to add to chain
	 */
	public void addToChain(Stone s) {

		// have to be the same color stone
		if (!this.contains(s)) {

			Board b = s.getBoard();
			Point p = s.getLocation();
			s.setChain(this);
			this.add(s);

			Point top = new Point(p.x, p.y + 1);
			Point right = new Point(p.x + 1, p.y);
			Point bottom = new Point(p.x, p.y - 1);
			Point left = new Point(p.x - 1, p.y);

			Qi qTop = new Qi(top, this.boardSize);
			Qi qRight = new Qi(right, this.boardSize);
			Qi qBottom = new Qi(bottom, this.boardSize);
			Qi qLeft = new Qi(left, this.boardSize);
			Qi qP = new Qi(p, this.boardSize);

			Stone stTop = new Stone(b, top);
			Stone stRight = new Stone(b, right);
			Stone stBottom = new Stone(b, bottom);
			Stone stLeft = new Stone(b, left);

			if (top.y < this.boardSize)
				stTop = this.board.getStone(top);

			if (right.x < this.boardSize)
				stRight = this.board.getStone(right);

			if (bottom.y > -1)
				stBottom = this.board.getStone(bottom);

			if (left.x > -1)
				stLeft = this.board.getStone(left);

			this.qis.remove(qP);

			if ((stTop.getColor() == -1) && (!this.qis.contains(qTop))
					&& (p.y < this.boardSize - 1)) {
				this.qis.add(qTop);
			} else if (stTop.getChain() != null) {
				TreeSet<Qi> q = stTop.getChain().getQis();
				if (s.getColor() != 3)
					q.remove(qP);
				stTop.getChain().setQis(qis);
			} // end if

			if ((stRight.getColor() == -1) && (!this.qis.contains(qRight))
					&& (p.x < this.boardSize - 1)) {
				this.qis.add(qRight);
			} else if (stRight.getChain() != null) {
				TreeSet<Qi> q = stRight.getChain().getQis();
				if (s.getColor() != 3)
					q.remove(qP);
				stRight.getChain().setQis(q);
			} // end if

			if ((stBottom.getColor() == -1) && (!this.qis.contains(qBottom))
					&& (p.y > 0)) {
				this.qis.add(qBottom);
			} else if (stBottom.getChain() != null) {
				TreeSet<Qi> q = stBottom.getChain().getQis();
				if (s.getColor() != 3)
					q.remove(qP);
				stBottom.getChain().setQis(q);
			} // end if

			if ((stLeft.getColor() == -1) && (!this.qis.contains(qLeft))
					&& (p.x > 0)) {
				this.qis.add(qLeft);
			} else if (stLeft.getChain() != null) {
				TreeSet<Qi> q = stLeft.getChain().getQis();
				if (s.getColor() != 3)
					q.remove(qP);
				stLeft.getChain().setQis(q);
			} // end if

			// if the adding stone's color is 1 or 2
			// then trade color 3 as a empty space
			if (s.getColor() == 1 || s.getColor() == 0) {

				if ((stTop.getColor() == 3) && (!this.qis.contains(qTop))
						&& (p.y < this.boardSize - 1))
					this.qis.add(qTop);

				if ((stRight.getColor() == 3) && (!this.qis.contains(qRight))
						&& (p.x < this.boardSize - 1))
					this.qis.add(qRight);

				if ((stBottom.getColor() == 3) && (!this.qis.contains(qBottom))
						&& (p.y > 0))
					this.qis.add(qBottom);

				if ((stLeft.getColor() == 3) && (!this.qis.contains(qLeft))
						&& (p.x > 0))
					this.qis.add(qLeft);

			} // end if

		} // end if

	} // end addStone()

	public void updateChains(Stone s) {

		// remove the stone not belong to the chain anymore
		Chain c = new Chain(this.board);
		c = this;
		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone s1 = it.next();
			Point p = s1.getLocation();
			this.board.getStones()[p.x][p.y] = null;
		} // end while

		this.board
				.removeChain(this.board.getRealChainIndex(this.getChainNum()));
		c.removeStone(s);

		// Add back all stones back to board
		Iterator<Stone> it2 = c.iterator();
		while (it2.hasNext()) {
			Stone s2 = it2.next();
			this.board.addStone(s2);
		} // end while

	} // end updateChains()

	public void recheckQis() {

		TreeSet<Qi> toRemove = new TreeSet<Qi>();

		Iterator<Qi> it = this.qis.iterator();
		while (it.hasNext()) {
			Qi q = it.next();
			Point p = new Point(q.getX(), q.getY());
			Stone s = this.board.getStone(p);
			if ((s.getColor() == 1) || (s.getColor() == 0))
				toRemove.add(q);
		} // end while

		Iterator<Qi> it2 = toRemove.iterator();
		while (it2.hasNext()) {
			Qi q = it2.next();
			this.qis.remove(q);
		} // end while

	} // end recheckQis()

	public void addBackQis(Stone s, Stone yan) {
		TreeSet<Qi> q = s.getChain().getQis();
		Qi q2 = new Qi(yan.getLocation(), this.boardSize);
		if ((s.getColor() != -1) && (!q.contains(q2)))
			q.add(q2);
	} // end addBackQis()

	public void recheckChains(Stone s) {

		// remove the stone not belong to the chain anymore
		Chain c = new Chain(this.board);
		c = this;
		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone s1 = it.next();
			Point p = s1.getLocation();
			this.board.getStones()[p.x][p.y] = null;
		} // end while

		this.board
				.removeChain(this.board.getRealChainIndex(this.getChainNum()));
		c.removeStone(s);

		// Add back all stones back to board
		Iterator<Stone> it2 = c.iterator();
		while (it2.hasNext()) {
			Stone s2 = it2.next();
			s2.setColor(3);
			this.board.addStone(s2);
		} // end while

	} // end recheckChains()

	public void realYanDetector() {

		// the pass in chain is Yan
		int totalNumYanJiao = 0;
		int totalNumEdge = 0;

		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {

			Stone s = it.next();
			int[] l = s.checkQiforYan(this.board);
			int numYanJiao = 0;
			int numEdge = 0;

			for (int i = 0; i < 8; i++) {
				if ((l[i] == -1) || (l[i] == 3))
					numYanJiao++;
				if (l[i] == 4)
					numEdge++;
			} // end for

			totalNumYanJiao += numYanJiao;
			totalNumEdge += numEdge;

		} // end while

		if (totalNumYanJiao <= 1) {

			if ((totalNumEdge > 0) && (totalNumYanJiao == 0)) {

				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					s.setZhenYan(true);
					s.setJiaYan(false);
				} // end while

				this.setZhenYan(true);
				this.setJiaYan(false);

			} // end if

			if ((totalNumEdge == 0) && (totalNumYanJiao <= 1)) {

				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					s.setZhenYan(true);
					s.setJiaYan(false);
				} // end while

				this.setZhenYan(true);
				this.setJiaYan(false);

			} // end if

			if ((totalNumEdge > 0) && (totalNumYanJiao == 1)) {

				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					s.setJiaYan(true);
					s.setZhenYan(false);
				} // end while

				this.setJiaYan(true);
				this.setZhenYan(false);

			} // end if

		} else {

			Iterator<Stone> it2 = this.iterator();
			while (it2.hasNext()) {
				Stone s = it2.next();
				s.setJiaYan(true);
				s.setZhenYan(false);
			} // end while

			this.setJiaYan(true);
			this.setZhenYan(false);

		} // end if

	} // end realYanDetector()

	public int getEnemyColor(int color) {
		if (color == 0) {
			return 1;
		} else {
			return 0;
		} // end if
	} // end getEnemyColor()

	/**
	 * Remove the Stone from ChainList
	 * 
	 * @param s
	 *            stone to remove from chain list
	 */
	public void removeStone(Stone s) {

		ArrayList<Stone> toRemove = new ArrayList<Stone>();
		Point p = s.getLocation();

		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone next = it.next();
			if (next.getLocation().equals(p))
				toRemove.add(next);
		} // end while

		Iterator<Stone> it2 = toRemove.iterator();
		while (it2.hasNext()) {
			Stone next = it2.next();
			this.remove(next);
		} // end while

	} // end removeStone()

	/*
	 * getters and setters
	 */
	public TreeSet<Qi> getQis() {
		return this.qis;
	} // end getQis()

	public void setQis(TreeSet<Qi> qs) {
		this.qis = qs;
	} // end setQis()

	public boolean isYan() {
		return this.isYan;
	} // end isYan()

	public void setYan(boolean y) {
		this.isYan = y;
	} // end setYan()

	public boolean isZhenYan() {
		return this.isZhenYan;
	} // end isZhenYan()

	public boolean isJiaYan() {
		return this.isJiaYan;
	} // end isJiaYan()

	public void setZhenYan(boolean zy) {
		this.isZhenYan = zy;
	} // end setZhenYan()

	public void setJiaYan(boolean jy) {
		this.isJiaYan = jy;
	} // end setJiaYan()

	public int getBoardSize() {
		return this.boardSize;
	} // end getBoardSize()

	public void setBoardSize(int bs) {
		this.boardSize = bs;
	} // end setBoardSize()

	public Board getBoard() {
		return this.board;
	} // end getBoard()

	public void setBoard(Board b) {
		this.board = b;
	} // end setBoard()

	public int getChainNum() {
		return this.chainNum;
	} // end getChainNum()

	public void setChainNum(int cn) {
		this.chainNum = cn;
	} // end setChainNum()

	public static int getCounterOfChain() {
		return Chain.counterOfChain;
	} // end getCounterOfChain()

	public static void setCounterOfChain(int cc) {
		Chain.counterOfChain = cc;
	} // end setCounterOfChain()

	public static long getSerialversionuid() {
		return Chain.serialVersionUID;
	} // end getSerialversionuid()

} // end class
