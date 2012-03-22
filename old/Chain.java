import java.awt.*;
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
	 * Version ID required for ArrayLists.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Qis is an ArrayList of Points whose coordinates are those of the goboard
	 * position of the chain's liberties. Liberties of a chain are positions on
	 * the goboard that are horizontally or vertically adjacent to the chain and
	 * are not occupied by an active stone.
	 */
	private TreeSet<Qi> qis = new TreeSet<Qi>();
	private boolean isYan = false;
	private boolean isZhenYan = false;
	private boolean isJiaYan = false;
	/**
	 * chainNum gives each chain a unique number id.
	 */
	private int chainNum;
	private static int counterOfChain;

	/**
	 * Chain constructor. When two stones of the same color are adjacent
	 * horizontally or vertically, they make a chain. The Chain class is an
	 * ArrayList of Stones that form a chain. In Board.java, addStone() will
	 * create a chain for a single stone if that stone is placed on the goboard
	 * and has all four liberties.
	 */
	public Chain() {
		chainNum = counterOfChain;
		counterOfChain++;
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
		this.addToChain(b, s);
	} // end constructor

	public int getChainIndex() {
		return chainNum;
	} // end getChainIndex()

	/**
	 * Add a stone to the chain. This only works if the stone being added is the
	 * same color as the rest of the stones in the chain.
	 * 
	 * @param s
	 *            stone to add to chain
	 */
	public void addToChain(Board b, Stone s) {

		// have to be the same color stone
		if (!this.contains(s)) {

			Point p = s.getLocation();
			s.setChain(this);
			this.add(s);

			Point top = new Point(p.x, p.y + 1);
			Point right = new Point(p.x + 1, p.y);
			Point bottom = new Point(p.x, p.y - 1);
			Point left = new Point(p.x - 1, p.y);

			Qi qTop = new Qi(top, b.getSize());
			Qi qRight = new Qi(right, b.getSize());
			Qi qBottom = new Qi(bottom, b.getSize());
			Qi qLeft = new Qi(left, b.getSize());
			Qi qP = new Qi(p, b.getSize());

			Stone stTop = new Stone(top);
			Stone stRight = new Stone(right);
			Stone stBottom = new Stone(bottom);
			Stone stLeft = new Stone(left);

			if (top.y < b.getSize())
				stTop = b.getStone(top);

			if (right.x < b.getSize())
				stRight = b.getStone(right);

			if (bottom.y > -1)
				stBottom = b.getStone(bottom);

			if (left.x > -1)
				stLeft = b.getStone(left);

			this.qis.remove(qP);

			if ((stTop.getColor() == -1) && (!this.qis.contains(qTop))
					&& (p.y < b.getSize() - 1)) {
				this.qis.add(qTop);
			} else if (stTop.getChain() != null) {
				TreeSet<Qi> qis = stTop.getChain().getQis();
				if (s.getColor() != 3)
					qis.remove(qP);
				stTop.getChain().setQis(qis);
			} // end if

			if ((stRight.getColor() == -1) && (!this.qis.contains(qRight))
					&& (p.x < b.getSize() - 1)) {
				this.qis.add(qRight);
			} else if (stRight.getChain() != null) {
				TreeSet<Qi> qis = stRight.getChain().getQis();
				if (s.getColor() != 3)
					qis.remove(qP);
				stRight.getChain().setQis(qis);
			} // end if

			if ((stBottom.getColor() == -1) && (!this.qis.contains(qBottom))
					&& (p.y > 0)) {
				this.qis.add(qBottom);
			} else if (stBottom.getChain() != null) {
				TreeSet<Qi> qis = stBottom.getChain().getQis();
				if (s.getColor() != 3)
					qis.remove(qP);
				stBottom.getChain().setQis(qis);
			} // end if

			if ((stLeft.getColor() == -1) && (!this.qis.contains(qLeft))
					&& (p.x > 0)) {
				this.qis.add(qLeft);
			} else if (stLeft.getChain() != null) {
				TreeSet<Qi> qis = stLeft.getChain().getQis();
				if (s.getColor() != 3)
					qis.remove(qP);
				stLeft.getChain().setQis(qis);
			} // end if

			// if the adding stone's color is 1 or 2
			// then trade color 3 as a empty space
			if ((s.getColor() == 1) || (s.getColor() == 0)) {

				if ((stTop.getColor() == 3) && (!this.qis.contains(qTop))
						&& (p.y < b.getSize() - 1))
					this.qis.add(qTop);

				if ((stRight.getColor() == 3) && (!this.qis.contains(qRight))
						&& (p.x < b.getSize() - 1))
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

	public void addBackQis(Board b, Stone s, Stone yan) {
		TreeSet<Qi> q = s.getChain().getQis();
		Qi q2 = new Qi(yan.getLocation(), b.getSize());
		if ((s.getColor() != -1) && (!q.contains(q2)))
			q.add(q2);
	} // end addBackQis()

	public void recheckQis(Board b) {

		TreeSet<Qi> toRemove = new TreeSet<Qi>();
		Iterator<Qi> it = this.qis.iterator();
		while (it.hasNext()) {
			Qi q = it.next();
			Point p = new Point(q.getX(), q.getY());
			Stone s = b.getStone(p);
			if ((s.getColor() == 1) || (s.getColor() == 0))
				toRemove.add(q);
		} // end while

		Iterator<Qi> it2 = toRemove.iterator();
		while (it2.hasNext()) {
			Qi q = it2.next();
			this.qis.remove(q);
		} // end while

	} // end recheckQis()

	public void updateChains(Board b, Stone s) {

		// remove the stone not belong to the chain anymore
		Chain c = new Chain();
		c = this;
		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone s2 = it.next();
			Point p = s2.getLocation();
			b.getStones()[p.x][p.y] = null;
		} // end while

		b.removeChain(b.getRealChainIndex(this.getChainIndex()));
		// Add back all stones back to board

		c.removeStone(s);
		Iterator<Stone> it2 = c.iterator();
		while (it2.hasNext()) {
			Stone s2 = it2.next();
			b.addStone(s2);
		} // end while

	} // end updateChains()

	public void recheckChains(Board b, Stone s) {

		// remove the stone not belong to the chain anymore
		Chain c = new Chain();
		c = this;
		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone s2 = it.next();
			Point p = s2.getLocation();
			b.getStones()[p.x][p.y] = null;
		} // end while

		b.removeChain(b.getRealChainIndex(this.getChainIndex()));
		// Add back all stones back to board

		c.removeStone(s);
		Iterator<Stone> it2 = c.iterator();
		while (it2.hasNext()) {
			Stone s2 = it2.next();
			s2.setColor(3);
			b.addStone(s2);
		} // end while

	} // end checkQis()

	public void realYanDetector(Board b) {

		// the pass in chain is Yan
		Iterator<Stone> it = this.iterator();
		int totalYanJiao = 0;
		int totalEdge = 0;

		while (it.hasNext()) {

			Stone s = it.next();
			int[] l = s.checkQiforYan(b);
			int numYanJiao = 0;
			int numEdge = 0;

			for (int i = 0; i < 8; i++) {
				if (l[i] == -1 || l[i] == 3)
					numYanJiao++;
				if (l[i] == 4)
					numEdge++;
			} // end for

			totalYanJiao += numYanJiao;
			totalEdge += numEdge;

		} // end while

		if (totalYanJiao <= 1) {

			if (totalEdge > 0 && totalYanJiao == 0) {

				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					s.setZhenYan(true);
					s.setJiaYan(false);
				} // end while

				this.setZhenYan(true);
				this.setJiaYan(false);

			} // end if

			if (totalEdge == 0 && totalYanJiao <= 1) {

				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					s.setZhenYan(true);
					s.setJiaYan(false);
				} // end while

				this.setZhenYan(true);
				this.setJiaYan(false);

			} // end if

			if (totalEdge > 0 && totalYanJiao == 1) {

				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					s.setJiaYan(true);
					s.setZhenYan(false);
				} // end while

				this.setJiaYan(true);
				this.setZhenYan(false);

			} // end while

		} else {

			Iterator<Stone> it2 = this.iterator();
			while (it2.hasNext()) {
				Stone s = it2.next();
				s.setJiaYan(true);
				s.setZhenYan(false);
			} // end while

			this.setJiaYan(true);
			this.setZhenYan(false);
		} // end else

	} // end realYanDetector()

	public int getToggledColor(int color) {
		if (color == 0)
			return 1;
		else
			return 0;
	} // end getToggledColor()

	// Remove the Stone from ChainList
	public void removeStone(Stone s) {

		Point p = s.getLocation();

		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone next = it.next();
			if (next.getLocation().equals(p)) {
				this.remove(next);
				break;
			} // end if
		} // end while

	} // end removeStone()

	public TreeSet<Qi> getQis() {
		return this.qis;
	} // end getQis()

	public void setQis(TreeSet<Qi> Qis) {
		this.qis = Qis;
	} // end setQis()

	public boolean isYan() {
		return this.isYan;
	} // end isQin()

	public void setYan(boolean isYan) {
		this.isYan = isYan;
	} // end setQin()

	public boolean isZhenYan() {
		return this.isZhenYan;
	} // end isZhenYan()

	public boolean isJiaYan() {
		return this.isJiaYan;
	} // end isJiaYan()

	public void setZhenYan(boolean isZhenyan) {
		this.isZhenYan = isZhenyan;
	} // end setZhenYan()

	public void setJiaYan(boolean isJiayan) {
		this.isJiaYan = isJiayan;
	} // end setJiaYan()

} // end class
