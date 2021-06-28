import java.awt.Point;
import java.util.Comparator;

public class QiComparator implements Comparator<Qi> {

	@Override
	public int compare(Qi firstQi, Qi secondQi) {

		Point firstPoint = new Point(firstQi.getX(), firstQi.getY());
		Point secondPoint = new Point(secondQi.getX(), secondQi.getY());

		int firstValue = 20 * firstPoint.x + firstPoint.y;
		int secondValue = 20 * secondPoint.x + secondPoint.y;
		int comparison = firstValue - secondValue;

		if (comparison < 0)
			return -1;
		else if (comparison == 0)
			return 0;
		else
			return 1;

	} // end compare()

} // end class
