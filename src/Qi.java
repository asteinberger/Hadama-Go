import java.awt.Point;

/**
 * Qi.java - Liberties or open intersections horizontally or vertically adjacent
 * to a chain.
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
public class Qi implements Comparable<Qi> {

	private static QiComparator qComp = new QiComparator();

	private int x;
	private int y;

	public Qi(Point p, int s) {
		this.x = p.x;
		this.y = p.y;
	} // end Qi()

	public int getX() {
		return this.x;
	} // end getX()

	public void setX(int x) {
		this.x = x;
	} // end setX()

	public int getY() {
		return this.y;
	} // end getY()

	public void setY(int y) {
		this.y = y;
	} // end setY()

	public static QiComparator getqComp() {
		return Qi.qComp;
	} // end getqComp()

	public static void setqComp(QiComparator qComp) {
		Qi.qComp = qComp;
	} // end setqComp()

	public int compareTo(Qi q) {
		int compare = qComp.compare(this, q);
		return compare;
	} // end compareTo()

	public boolean equals(Qi q) {
		boolean isEqual = qComp.equals(this, q);
		return isEqual;
	} // end equals()

	public Point getPosition() {
		Point p = new Point(this.x, this.y);
		return p;
	}

	@Override
	public String toString() {
		return "Qi [x=" + x + ", y=" + y + "]";
	} // end toString()

} // end class
