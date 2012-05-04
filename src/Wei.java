import java.awt.*;
import java.util.Iterator;
import java.util.TreeSet;

public class Wei extends TreeSet<Stone> {
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

	private Board goboard;
	private int weiNum;
	static int counterOfWei;

	public Wei(Board b) {
		this.goboard = b;
		weiNum = counterOfWei;
		counterOfWei++;

	} // end constructor

	public void addToWei(Stone s) {
		// have to be the same color stone
		if (!this.contains(s)) {
			s.setWei(this);
			this.add(s);
		} // end if
	} // end addToWei()

	// Check if the stones inside Wei, if the stone not belong to this Wei
	// anymore then remove it from the Wei
	public void recheckStones() {

		TreeSet<Stone> toRemove = new TreeSet<Stone>();
		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone s = it.next();

			if (s.getColor() == -1)
				toRemove.add(s);
		} // end while

		Iterator<Stone> it2 = toRemove.iterator();
		while (it2.hasNext()) {
			Stone s = it2.next();
			this.remove(s);
		} // end while

	} // end checkStones()

	public void updateWeis(Stone currentS) {
		// remove the stone not belong to the chain anymore
		Wei w = new Wei(this.goboard);
		w = this;

		Iterator<Stone> it = this.iterator();
		while (it.hasNext()) {
			Stone s = it.next();
			Point p = s.getLocation();
			this.goboard.getBoard()[p.x][p.y] = null;
			this.goboard.getChains().remove(s.getChain());
		}

		this.goboard.getWeis().remove(this);

		// Add back all stones back to board
		w.remove(currentS);
		Iterator<Stone> it2 = w.iterator();
		while (it2.hasNext()) {
			Stone s = it2.next();
			this.goboard.addStone(s);
		}

	} // end checkQis()

	public void yanDetector() {
		int xMin;
		int xMax;
		int yMin = Integer.MAX_VALUE;
		int yMax = Integer.MIN_VALUE;
		int color = this.first().getColor();
		xMin = this.first().getLocation().x;
		xMax = this.last().getLocation().x;

		Iterator<Stone> findY = this.iterator();
		while (findY.hasNext()) {

			Stone s = findY.next();
			int y = s.getLocation().y;
			if (y >= yMax) {
				yMax = y;
			}
			if (y <= yMin) {
				yMin = y;
			}
		}

		if (xMax == 8)
			xMax++;
		if (yMax == 8)
			yMax++;
		if (xMin == 0)
			xMin--;
		if (yMin == 0)
			yMin--;

		for (int i = yMin + 1; i < yMax; i++) {

			for (int j = xMin + 1; j < xMax; j++) {

				Point p = new Point(j, i);
				Stone s = this.goboard.getStone(p);

				if ((s.getColor() != 1) && (s.getColor() != 0)
						&& (s.getColor() != 3) && (s.getColor() != 4)) {
					// if chain is empty,add it no matter what
					// if chain is not empty, check if it already became chain
					if (s.getChain() != null) {
						if (!s.getChain().isYan()) {
							if (color == 1) {
								s.setColor(3);
								this.goboard.addStone(s);
							} else {
								s.setColor(4);
								this.goboard.addStone(s);
							}
						}
					} else {
						if (color == 1) {
							s.setColor(3);
							this.goboard.addStone(s);
						} else {
							s.setColor(4);
							this.goboard.addStone(s);
						}
					}
				}
			}
		}
	}

	public int getWeiIndex() {
		return weiNum;
	}
} // end class
