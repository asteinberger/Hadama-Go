import java.awt.Point;
import java.util.Comparator;

/**
 * StoneComparator.java - Compare stones by their (x,y) location so that
 * structures can sort stones in order of x and then y ascending.
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
public class StoneComparator implements Comparator<Stone> {

	@Override
	public int compare(Stone o1, Stone o2) {

		Point p1 = o1.getLocation();
		Point p2 = o2.getLocation();

		int val1 = 20 * p1.x + p1.y;
		int val2 = 20 * p2.x + p2.y;
		int compare = val1 - val2;

		if (compare < 0)
			return -1;
		else if (compare == 0)
			return 0;
		else
			return 1;

	} // end compare()

	public boolean equals(Stone o1, Stone o2) {

		Point p1 = o1.getLocation();
		Point p2 = o2.getLocation();

		int val1 = 20 * p1.x + p1.y;
		int val2 = 20 * p2.x + p2.y;
		int compare = val1 - val2;

		if (compare == 0)
			return true;
		else
			return false;

	} // end equals()

} // end class
