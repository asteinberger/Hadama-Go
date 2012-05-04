import java.awt.Point;
import java.util.Comparator;

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
