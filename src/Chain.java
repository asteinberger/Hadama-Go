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
	private TreeSet<Qi> Qis = new TreeSet<Qi>();
	private int boardSize;
	private Board goboard;
	private boolean isYan = false;
	private boolean isZhenYan = false;
	private boolean isJiaYan = false;
	/**
	 * chainNum gives each chain a unique number id.
	 */
	private int chainNum;
	static int counterOfChain;

	/**
	 * Chain constructor. When two stones of the same color are adjacent
	 * horizontally or vertically, they make a chain. The Chain class is an
	 * ArrayList of Stones that form a chain. In Board.java, addStone() will
	 * create a chain for a single stone if that stone is placed on the goboard
	 * and has all four liberties.
	 */
	public Chain(Board b) {

		boardSize = b.getSize();
		this.goboard = b;
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
	public Chain(Board b, Stone h) {
		this.addToChain(h, b.getBoard());
		boardSize = b.getSize();
		this.goboard = b;

	} // end constructor

	/**
	 * Add a stone to the chain. This only works if the stone being added is the
	 * same color as the rest of the stones in the chain.
	 * 
	 * @param s
	 *            stone to add to chain
	 */
	public void addToChain(Stone s, Stone[][] b) {

		// have to be the same color stone
		if (!this.contains(s)) {
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

			Stone stTop = new Stone(top);
			Stone stRight = new Stone(right);
			Stone stBottom = new Stone(bottom);
			Stone stLeft = new Stone(left);

			if (top.y < this.boardSize)
				stTop = this.goboard.getStone(top);

			if (right.x < this.boardSize)
				stRight = this.goboard.getStone(right);

			if (bottom.y > -1)
				stBottom = this.goboard.getStone(bottom);

			if (left.x > -1)
				stLeft = this.goboard.getStone(left);

			this.Qis.remove(qP);

			if ((stTop.getColor() == -1) && (!this.Qis.contains(qTop))
					&& (p.y < this.boardSize - 1)) {
				this.Qis.add(qTop);
			} else {

				if (stTop.getChain() != null) {
					TreeSet<Qi> qis = stTop.getChain().getQis();
					if (s.getColor() != 3 && s.getColor() != 4) {
						qis.remove(qP);
					}
					stTop.getChain().setQis(qis);
				}
			}

			if ((stRight.getColor() == -1) && (!this.Qis.contains(qRight))
					&& (p.x < this.boardSize - 1)) {
				this.Qis.add(qRight);
			} else {
				if (stRight.getChain() != null) {
					TreeSet<Qi> qis = stRight.getChain().getQis();
					if (s.getColor() != 3 && s.getColor() != 4) {
						qis.remove(qP);
					}
					stRight.getChain().setQis(qis);
				}
			}

			if ((stBottom.getColor() == -1) && (!this.Qis.contains(qBottom))
					&& (p.y > 0)) {
				this.Qis.add(qBottom);
			} else {
				if (stBottom.getChain() != null) {
					TreeSet<Qi> qis = stBottom.getChain().getQis();
					if (s.getColor() != 3 && s.getColor() != 4) {
						qis.remove(qP);
					}
					stBottom.getChain().setQis(qis);
				}
			}

			if ((stLeft.getColor() == -1) && (!this.Qis.contains(qLeft))
					&& (p.x > 0)) {
				this.Qis.add(qLeft);
			} else {
				if (stLeft.getChain() != null) {

					TreeSet<Qi> qis = stLeft.getChain().getQis();
					if (s.getColor() != 3 && s.getColor() != 4) {
						qis.remove(qP);
					}
					stLeft.getChain().setQis(qis);

				}
			}

			// if the adding stone's color is 1 or 2
			// then trade color 3 and 4 as a empty space
			if (s.getColor() == 1 || s.getColor() == 0) {

				if ((stTop.getColor() == 3 || stTop.getColor() == 4)
						&& (!this.Qis.contains(qTop))
						&& (p.y < this.boardSize - 1)) {
					this.Qis.add(qTop);
				}

				if ((stRight.getColor() == 3 || stRight.getColor() == 4)
						&& (!this.Qis.contains(qRight))
						&& (p.x < this.boardSize - 1)) {
					this.Qis.add(qRight);
				}

				if ((stBottom.getColor() == 3 || stBottom.getColor() == 4)
						&& (!this.Qis.contains(qBottom)) && (p.y > 0)) {
					this.Qis.add(qBottom);
				}

				if ((stLeft.getColor() == 3 || stLeft.getColor() == 4)
						&& (!this.Qis.contains(qLeft)) && (p.x > 0)) {
					this.Qis.add(qLeft);
				}

			} // end if

		}
	} // end addStone()

	public void addBackQis(Stone s, Stone yan) {

		TreeSet<Qi> q = s.getChain().getQis();
		Qi q2 = new Qi(yan.getLocation(), this.boardSize);
		if ((s.getColor() != -1) && (!q.contains(q2)))
			q.add(q2);

	} // end addBackQis()

	// We only go through the Qi List and update Qis based on it
	public void recheckQis() {

		TreeSet<Qi> toRemove = new TreeSet<Qi>();
		Iterator<Qi> it = this.Qis.iterator();
		while (it.hasNext()) {
			Qi q = it.next();

			// we have to make sure that x, y did not go out of board
			Point p = new Point(q.getX(), q.getY());
			Stone s = this.goboard.getStone(p);

			if ((s.getColor() == 1) || (s.getColor() == 0)) {
				toRemove.add(q);
			}
		} // end while

		Iterator<Qi> it2 = toRemove.iterator();
		while (it2.hasNext()) {
			Qi q = it2.next();
			this.Qis.remove(q);
		} // end while

	} // end checkQis()

	// We go through each stone inside the chain and check its Qis
	public void deeplyrecheckQis() {

		TreeSet<Qi> newQi = new TreeSet<Qi>();
		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {

			Stone next = it.next();
			int[] l = next.checkQi(goboard.getBoard());
			Point lp = next.getLocation();

			// chain above
			if (l[0] == -1 || l[0] == 3 || l[0] == 4) {
				if (lp.y < this.boardSize - 1) {
					Point p = new Point(lp.x, lp.y + 1);
					Qi q = new Qi(p, this.boardSize);
					if (!newQi.contains(q)) {
						newQi.add(q);
					}
				}

			}// end if

			// chain to right
			if (l[1] == -1 || l[1] == 3 || l[1] == 4) {
				if (lp.x < this.boardSize - 1) {
					Point p = new Point(lp.x + 1, lp.y);
					Qi q = new Qi(p, this.boardSize);
					if (!newQi.contains(q)) {
						newQi.add(q);
					}
				}
			} // end if

			// chain below
			if (l[2] == -1 || l[2] == 3 || l[2] == 4) {
				if (lp.y > 0) {
					Point p = new Point(lp.x, lp.y - 1);
					Qi q = new Qi(p, this.boardSize);
					if (!newQi.contains(q)) {
						newQi.add(q);
					}
				}
			} // end if

			// chain to left
			if (l[3] == -1 || l[3] == 3 || l[3] == 4) {
				if (lp.x > 0) {
					Point p = new Point(lp.x - 1, lp.y);
					Qi q = new Qi(p, this.boardSize);
					if (!newQi.contains(q)) {
						newQi.add(q);
					}
				}

			} // end if
		} // end while

		this.Qis = newQi;
	} // end checkQis()

	public void updateChains(Stone currentS) {

		// remove the stone not belong to the chain anymore
		Chain c = new Chain(this.goboard);
		c = this;

		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone s = it.next();
			Point p = s.getLocation();
			this.goboard.getBoard()[p.x][p.y] = null;
		}

		this.goboard.getChains().remove(
				this.goboard.realChainIndex(this.getChainIndex()));

		// Add back all stones back to board
		c.removeStone(currentS);
		Iterator<Stone> it2 = c.iterator();
		while (it2.hasNext()) {
			Stone s = it2.next();
			this.goboard.addStone(s);
		}

	} // end checkQis()

	public void recheckChains(Stone currentS) {

		// remove the stone not belong to the chain anymore
		Chain c = new Chain(this.goboard);
		c = this;

		int color = -1;
		if (!c.isEmpty())
			color = c.first().getBelongto();

		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone s = it.next();
			Point p = s.getLocation();
			this.goboard.getBoard()[p.x][p.y] = null;
		}

		int index = this.goboard.realChainIndex(this.getChainIndex());
		if (index > -1)
			this.goboard.getChains().remove(index);
		// Add back all stones back to board

		c.removeStone(currentS);
		Iterator<Stone> it2 = c.iterator();
		while (it2.hasNext()) {
			Stone s = it2.next();
			if (color == 0) {
				s.setColor(4);
			} else {
				s.setColor(3);
			}

			this.goboard.addStone(s);
		}

	} // end checkQis()

	public void realYandetector() {
		// the pass in chain is Yan
		Iterator<Stone> it = this.iterator();
		int totalnumofYanjiao = 0;
		int totalnumofEdge = 0;
		while (it.hasNext()) {

			Stone s = it.next();
			int[] l = s.checkQiforYan(this.goboard.getBoard());
			int numOfYanjiao = 0;
			int numOfEdge = 0;

			for (int i = 0; i < 8; i++) {

				if (l[i] == -1 || l[i] == 3 || l[i] == 4) {
					numOfYanjiao++;
				}
				if (l[i] == 5) {
					numOfEdge++;

				}
			}

			totalnumofYanjiao += numOfYanjiao;
			totalnumofEdge += numOfEdge;
		}

		if (totalnumofYanjiao <= 1) {

			if (totalnumofEdge > 0 && totalnumofYanjiao == 0) {
				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					s.setZhenYan(true);
					s.setJiaYan(false);
				}

				this.setZhenYan(true);
				this.setJiaYan(false);

			}
			if (totalnumofEdge == 0 && totalnumofYanjiao <= 1) {
				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					s.setZhenYan(true);
					s.setJiaYan(false);
				}

				this.setZhenYan(true);
				this.setJiaYan(false);
			}
			if (totalnumofEdge > 0 && totalnumofYanjiao == 1) {

				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					s.setJiaYan(true);
					s.setZhenYan(false);
				}
				this.setJiaYan(true);
				this.setZhenYan(false);
			}

		} else {

			Iterator<Stone> it2 = this.iterator();
			while (it2.hasNext()) {
				Stone s = it2.next();
				s.setJiaYan(true);
				s.setZhenYan(false);
			}
			this.setJiaYan(true);
			this.setZhenYan(false);
		}
	}

	// Remove the Stone from ChainList
	public void removeStone(Stone s) {

		Point p = s.getLocation();

		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone next = it.next();
			if (next.getLocation().equals(p)) {
				this.remove(next);
				break;
			}
		} // end while
	}

	public int getChainIndex() {
		return chainNum;
	}

	public TreeSet<Qi> getQis() {
		return this.Qis;
	} // end getQis()

	public void setQis(TreeSet<Qi> Qis) {
		this.Qis = Qis;
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

	public void setZhenYan(boolean isZhenyan) {
		this.isZhenYan = isZhenyan;
	} // end setZhenYan()

	public boolean isJiaYan() {
		return this.isJiaYan;
	} // end isJiaYan()

	public void setJiaYan(boolean isJiayan) {
		this.isJiaYan = isJiayan;
	} // end setJiaYan()

} // end class
