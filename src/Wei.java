import java.awt.*;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Wei - Friendly stones that are horizontally, vertically or diagonally
 * adjacent to one another form a wei.
 * 
 * This file is part of Hadama Go.
 * 
 * Hadama Go is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Hadama Go is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Hadama Go. If not, see http://www.gnu.org/licenses/.
 * 
 * @author Haoran Ma (mahaoran1020@gmail.com), Adam Steinberger
 *         (steinz08@gmail.com)
 */
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

	public Wei(Board b, Stone h) {
		this.addToWei(h);
		this.goboard = b;

	} // end constructor

	public void addToWei(Stone s) {
		// have to be the same color stone
		if (!this.contains(s)) {
			s.setWei(this);
			this.add(s);

		} // end if

	} // end addStone()

	// Remove the Stone from WeiList
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
