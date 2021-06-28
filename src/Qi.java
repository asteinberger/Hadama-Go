import java.awt.Point;

public class Qi implements Comparable<Qi> {

	private static QiComparator qiComparator = new QiComparator();

	private int x;
	private int y;

	public Qi(Point point) {
		this.x = point.x;
		this.y = point.y;
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

	public int compareTo(Qi q) {
		int compare = qiComparator.compare(this, q);
		return compare;
	} // end compareTo()

	@Override
	public String toString() {
		return "(" + x + "," +y+")" ;
	} // end toString()

} // end class
