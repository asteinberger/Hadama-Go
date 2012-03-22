import java.awt.Point;
import java.util.Comparator;

public class StoneComparator implements Comparator<Stone> {

	private int size;

	public StoneComparator(int s) {
		this.size = s;
	} // end constructor

	@Override
	public int compare(Stone o1, Stone o2) {

		Point p1 = o1.getLocation();
		Point p2 = o2.getLocation();

		int val1 = this.size * p1.x + p1.y;
		int val2 = this.size * p2.x + p2.y;
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

		int val1 = 10 * p1.x + p1.y;
		int val2 = 10 * p2.x + p2.y;
		int compare = val1 - val2;

		if (compare == 0)
			return true;
		else
			return false;

	} // end equals()
	
	public int getSize() {
		return this.size;
	} // end getSize()

	public void setSize(int size) {
		this.size = size;
	} // end setSize()

} // end class
