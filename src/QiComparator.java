import java.awt.Point;
import java.util.Comparator;

public class QiComparator implements Comparator<Qi> {

	@Override
	public int compare(Qi o1, Qi o2) {

		Point p1 = new Point(o1.getX(), o1.getY());
		Point p2 = new Point(o2.getX(), o2.getY());

		int val1 = o1.getBoardSize() * p1.x + p1.y;
		int val2 = o2.getBoardSize() * p2.x + p2.y;
		int compare = val1 - val2;

		if (compare < 0)
			return -1;
		else if (compare == 0)
			return 0;
		else
			return 1;

	} // end compare()

	public boolean equals(Qi o1, Qi o2) {

		Point p1 = new Point(o1.getX(), o1.getY());
		Point p2 = new Point(o2.getX(), o2.getY());

		int val1 = o1.getBoardSize() * p1.x + p1.y;
		int val2 = o2.getBoardSize() * p2.x + p2.y;
		int compare = val1 - val2;

		if (compare == 0)
			return true;
		else
			return false;

	} // end equals()

} // end class
