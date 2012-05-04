import java.awt.Point;

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
	
	public Point getPosition(){
		Point p = new Point(this.x, this.y);
		return p;
	}

	@Override
	public String toString() {
		return "(" + x + "," +y+")" ;
	} // end toString()

} // end class
