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

	private int weiNum;
	static int counterOfWei;

	public Wei() {
		weiNum = counterOfWei;
		counterOfWei++;
	} // end constructor

	public Wei(Stone s) {
		this.addToWei(s);
	} // end constructor

	public int getWeiIndex() {
		return weiNum;
	}

	public void addToWei(Stone s) {
		// have to be the same color stone
		if (!this.contains(s)) {
			s.setWei(this);
			this.add(s);
		} // end if

	} // end addStone()

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

	public void yanDetector(Board b) {

		int xMin;
		int xMax;
		int yMin = Integer.MAX_VALUE;
		int yMax = Integer.MIN_VALUE;

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
				Stone s = b.getStone(p);

				if ((s.getColor() != 1) && (s.getColor() != 0)
						&& (s.getColor() != 3)) {

					// if chain is empty,add it no matter what
					// if chain is not empty, check if it already became chain
					if (s.getChain() != null) {
						if (!s.getChain().isYan()) {
							s.setColor(3);
							b.addStone(s);
						}
					} else {
						s.setColor(3);
						b.addStone(s);
					}
				}
			}
		}
	}

} // end class
