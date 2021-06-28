import java.awt.Point;
import java.util.Comparator;

public class StoneComparator implements Comparator<Stone> {

	@Override
	public int compare(Stone firstStone, Stone secondStone) {

		Point firstPoint = firstStone.getLocation();
		Point secondPoint = secondStone.getLocation();

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
