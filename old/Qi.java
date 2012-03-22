import java.awt.Point;

public class Qi implements Comparable<Qi> {

	private static QiComparator qComp = new QiComparator();

	private int x;
	private int y;


	private int boardSize;

	public Qi(int s) {
		this.x = 0;
		this.y = 0;
		this.boardSize = s;
	} // end Qi()

	public Qi(Point p, int s) {
		this.x = p.x;
		this.y = p.y;
		this.boardSize = s;
	} // end Qi()

	public Qi(int x, int y, int s) {
		this.x = x;
		this.y = y;
		this.boardSize = s;
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

	public int getBoardSize() {
		return this.boardSize;
	} // end getBoardSize()

	public void setBoardSize(int boardSize) {
		this.boardSize = boardSize;
	} // end setBoardSize()
	
	public Point getPosition(){
		Point p = new Point(this.x, this.y);
		return p;
	}

	@Override
	public String toString() {
		return "Qi [x=" + x + ", y=" + y + ", boardSize=" + boardSize + "]";
	} // end toString()

} // end class
