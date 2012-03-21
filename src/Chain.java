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
	public Chain(Board b, Stone h, Move m) {
		this.addToChain(h, m);
		boardSize = b.getSize();
		this.goboard = b;

	} // end constructor

	public int getChainIndex() {
		return chainNum;
	}

	/**
	 * Add a stone to the chain. This only works if the stone being added is the
	 * same color as the rest of the stones in the chain.
	 * 
	 * @param s
	 *            stone to add to chain
	 */
	public void addToChain(Stone s, Move m) {

		// have to be the same color stone
		if (!this.contains(s)) {
			Board b = s.getBoard();
			Point p = s.getLocation();
			m.removeChainStone(s.getChain(), s);
			s.setChain(this);
			this.add(s);
			m.addChainStone(this, s);

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
				stTop = this.goboard.getStone(top);

			if (right.x < this.boardSize)
				stRight = this.goboard.getStone(right);

			if (bottom.y > -1)
				stBottom = this.goboard.getStone(bottom);

			if (left.x > -1)
				stLeft = this.goboard.getStone(left);

			this.Qis.remove(qP);
			m.removeChainQi(this, qP);

			if ((stTop.getColor() == -1) && (!this.Qis.contains(qTop))
					&& (p.y < this.boardSize - 1)) {
				this.Qis.add(qTop);
				m.addChainQi(this, qTop);
			} else {

				if (stTop.getChain() != null) {
					TreeSet<Qi> qis = stTop.getChain().getQis();
					if (s.getColor() != 3) {
						qis.remove(qP);
						m.removeChainQi(stTop.getChain(), qP);
					}
					stTop.getChain().setQis(qis);
				}
			}

			if ((stRight.getColor() == -1) && (!this.Qis.contains(qRight))
					&& (p.x < this.boardSize - 1)) {
				this.Qis.add(qRight);
				m.addChainQi(this, qRight);
			} else {
				if (stRight.getChain() != null) {
					TreeSet<Qi> qis = stRight.getChain().getQis();
					if (s.getColor() != 3) {
						qis.remove(qP);
						m.removeChainQi(stRight.getChain(), qP);
					}
					stRight.getChain().setQis(qis);
				}
			}

			if ((stBottom.getColor() == -1) && (!this.Qis.contains(qBottom))
					&& (p.y > 0)) {
				this.Qis.add(qBottom);
				m.addChainQi(this, qBottom);
			} else {
				if (stBottom.getChain() != null) {
					TreeSet<Qi> qis = stBottom.getChain().getQis();
					if (s.getColor() != 3) {
						qis.remove(qP);
						m.removeChainQi(stBottom.getChain(), qP);
					}
					stBottom.getChain().setQis(qis);
				}
			}

			if ((stLeft.getColor() == -1) && (!this.Qis.contains(qLeft))
					&& (p.x > 0)) {
				this.Qis.add(qLeft);
				m.addChainQi(this, qLeft);
			} else {
				if (stLeft.getChain() != null) {

					TreeSet<Qi> qis = stLeft.getChain().getQis();
					if (s.getColor() != 3) {
						qis.remove(qP);
						m.removeChainQi(stLeft.getChain(), qP);
					}
					stLeft.getChain().setQis(qis);

				}
			}

			// if the adding stone's color is 1 or 2
			// then trade color 3 as a empty space
			if (s.getColor() == 1 || s.getColor() == 0) {

				if ((stTop.getColor() == 3) && (!this.Qis.contains(qTop))
						&& (p.y < this.boardSize - 1)) {
					this.Qis.add(qTop);
					m.addChainQi(this, qTop);
				}

				if ((stRight.getColor() == 3) && (!this.Qis.contains(qRight))
						&& (p.x < this.boardSize - 1)) {
					this.Qis.add(qRight);
					m.addChainQi(this, qRight);
				}

				if ((stBottom.getColor() == 3) && (!this.Qis.contains(qBottom))
						&& (p.y > 0)) {
					this.Qis.add(qBottom);
					m.addChainQi(this, qBottom);
				}

				if ((stLeft.getColor() == 3) && (!this.Qis.contains(qLeft))
						&& (p.x > 0)) {
					this.Qis.add(qLeft);
					m.addChainQi(this, qLeft);
				}

			} // end if

		}
	} // end addStone()

	public void addBackQis(Stone s, Stone yan, Move m) {

		TreeSet<Qi> q = s.getChain().getQis();
		Qi q2 = new Qi(yan.getLocation(), this.boardSize);
		if ((s.getColor() != -1) && (!q.contains(q2))) {
			m.addChainQi(this, q2);
			q.add(q2);
		}

	} // end addBackQis()

	public void recheckQis(Move m) {

		TreeSet<Qi> toRemove = new TreeSet<Qi>();
		Iterator<Qi> it = this.Qis.iterator();
		while (it.hasNext()) {
			Qi q = it.next();
			Point p = new Point(q.getX(), q.getY());
			Stone s = this.goboard.getStone(p);
			if ((s.getColor() == 1) || (s.getColor() == 0)) {
				m.removeChainQi(this, q);
				toRemove.add(q);
			}
		} // end while

		Iterator<Qi> it2 = toRemove.iterator();
		while (it2.hasNext()) {
			Qi q = it2.next();
			this.Qis.remove(q);
		} // end while

	} // end checkQis()

	public void updateChains(Stone currentS, Move m) {

		// remove the stone not belong to the chain anymore
		Chain c = new Chain(this.goboard);
		c = this;
		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone s = it.next();
			Point p = s.getLocation();
			m.removeChainStone(c, s);
			this.goboard.getStones()[p.x][p.y] = null;
		}

		m.removeChain(c);
		this.goboard.removeChain(this.goboard.getRealChainIndex(this
				.getChainIndex()));
		// Add back all stones back to board

		m.removeChainStone(c, currentS);
		c.removeStone(currentS, m);
		Iterator<Stone> it2 = c.iterator();
		while (it2.hasNext()) {
			Stone s = it2.next();
			this.goboard.addStone(s, m);
		}

	} // end checkQis()

	public void recheckChains(Stone currentS, Move m) {

		// remove the stone not belong to the chain anymore
		Chain c = new Chain(this.goboard);
		c = this;
		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone s = it.next();
			Point p = s.getLocation();
			m.removeChainStone(c, s);
			this.goboard.getStones()[p.x][p.y] = null;
		}

		m.removeChain(c);
		this.goboard.removeChain(this.goboard.getRealChainIndex(this
				.getChainIndex()));
		// Add back all stones back to board

		m.removeChainStone(c, currentS);
		c.removeStone(currentS, m);
		Iterator<Stone> it2 = c.iterator();
		while (it2.hasNext()) {
			Stone s = it2.next();
			s.setColor(3);
			this.goboard.addStone(s, m);
		}

	} // end checkQis()

	public void realYandetector(Move m) {
		// the pass in chain is Yan
		Iterator<Stone> it = this.iterator();
		int totalnumofYanjiao = 0;
		int totalnumofEdge = 0;
		while (it.hasNext()) {

			Stone s = it.next();
			int[] l = s.checkQiforYan(this.goboard);
			int numOfYanjiao = 0;
			int numOfEdge = 0;

			for (int i = 0; i < 8; i++) {

				if (l[i] == -1 || l[i] == 3) {
					numOfYanjiao++;
				}
				if (l[i] == 4) {
					numOfEdge++;

				}
			}

			totalnumofYanjiao += numOfYanjiao;
			totalnumofEdge += numOfEdge;
		}

		// System.out.println(totalnumofYanjiao);
		// System.out.println(totalnumofEdge);

		if (totalnumofYanjiao <= 1) {

			if (totalnumofEdge > 0 && totalnumofYanjiao == 0) {
				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					m.removeChainStone(this, s);
					s.setZhenYan(true);
					s.setJiaYan(false);
					m.addChainStone(this, s);
				}

				m.removeChain(this);
				this.setZhenYan(true);
				this.setJiaYan(false);
				m.addChain(this);

			}
			if (totalnumofEdge == 0 && totalnumofYanjiao <= 1) {
				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					m.removeChainStone(this, s);
					s.setZhenYan(true);
					s.setJiaYan(false);
					m.addChainStone(this, s);
				}

				m.removeChain(this);
				this.setZhenYan(true);
				this.setJiaYan(false);
				m.addChain(this);
			}
			if (totalnumofEdge > 0 && totalnumofYanjiao == 1) {

				Iterator<Stone> it2 = this.iterator();
				while (it2.hasNext()) {
					Stone s = it2.next();
					m.removeChainStone(this, s);
					s.setJiaYan(true);
					s.setZhenYan(false);
					m.addChainStone(this, s);
				}
				m.removeChain(this);
				this.setJiaYan(true);
				this.setZhenYan(false);
				m.addChain(this);

			}

		} else {

			Iterator<Stone> it2 = this.iterator();
			while (it2.hasNext()) {
				Stone s = it2.next();
				m.removeChainStone(this, s);
				s.setJiaYan(true);
				s.setZhenYan(false);
				m.addChainStone(this, s);
			}
			m.removeChain(this);
			this.setJiaYan(true);
			this.setZhenYan(false);
			m.addChain(this);
		}
	}

	public int getToggledColor(int color) {
		if (color == 0) {
			return 1;
		} else {
			return 0;
		} // end if
	} // end getToggledColor()

	// Remove the Stone from ChainList
	public void removeStone(Stone s, Move m) {

		Point p = s.getLocation();

		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone next = it.next();
			if (next.getLocation().equals(p)) {
				m.removeChainStone(this, next);
				this.remove(next);
				break;
			}
		} // end while
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
