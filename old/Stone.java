import java.awt.Point;

/**
 * Stone.java - Used to keep track of pieces on the board
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <steinz08@gmail.com>
 */

public class Stone implements Comparable<Stone> {

	/**
	 * Warning! I had to hard code the 9x9 board size for the StoneComparator.
	 * This could cause bugs in the future!
	 */
	private static StoneComparator sComp = new StoneComparator(9);

	// color -2 represents grey (Yan).
	// color -1 represents empty
	// color 0 represents black player
	// color 1 represents white player
	// color 3 represents yan chain created by a wei
	private int color = 0;
	private int value = 0;
	private boolean isYan = false;
	private boolean isZhenYan = false;
	private boolean isJiaYan = false;
	private Chain chain = null;
	private Wei wei = null;
	private Point location = new Point(0, 0);
	private int belongto;

	// initial an empty Stone for deleting purpose
	public Stone() {
		this.color = -1;
		this.belongto = -1;
	} // end constructor

	public Stone(int c) {
		this.color = c;
		this.belongto = c;
	} // end constructor

	public Stone(int c, Point l) {
		this.color = c;
		this.location = l;
		this.belongto = c;
	} // end constructor

	public Stone(Point l) {
		this.location = l;
		this.color = -1;
		this.belongto = -1;
	} // end constructor

	public int[] checkQi(Board b) {

		// { top, right, bottom, left, top-left, top-right, bottom-right,
		// bottom-left }
		int[] result = { -1, -1, -1, -1, -1, -1, -1, -1 };
		int x = this.location.x;
		int y = this.location.y;
		int size = b.getSize();
		Stone[][] ss = b.getStones();

		// top
		if ((y < size - 1) && (ss[x][y + 1] != null))
			result[0] = ss[x][y + 1].getColor();
		// right
		if ((x < size - 1) && (ss[x + 1][y] != null))
			result[1] = ss[x + 1][y].getColor();
		// bottom
		if ((y > 0) && (ss[x][y - 1] != null))
			result[2] = ss[x][y - 1].getColor();
		// left
		if ((x > 0) && (ss[x - 1][y] != null))
			result[3] = ss[x - 1][y].getColor();

		// top-left
		if ((y < size - 1) && (x > 0) && (ss[x - 1][y + 1] != null))
			result[4] = ss[x - 1][y + 1].getColor();
		// top-right
		if ((y < size - 1) && (x < size - 1) && (ss[x + 1][y + 1] != null))
			result[5] = ss[x + 1][y + 1].getColor();
		// bottom-right
		if ((y > 0) && (x < size - 1) && (ss[x + 1][y - 1] != null))
			result[6] = ss[x + 1][y - 1].getColor();
		// bottom-left
		if ((y > 0) && (x > 0) && (ss[x - 1][y - 1] != null))
			result[7] = ss[x - 1][y - 1].getColor();

		return result;

	} // end checkQi()

	public Wei[] checkWeis(Board b) {

		int x = this.location.x;
		int y = this.location.y;

		int size = b.getSize();
		Stone[][] ss = b.getStones();

		// { top, right, bottom, left, top-left, top-right, bottom-right,
		// bottom-left }
		Wei[] result = { null, null, null, null, null, null, null, null };

		// top
		if ((y < size - 1) && (ss[x][y + 1] != null))
			result[0] = ss[x][y + 1].getWei();
		// right
		if ((x < size - 1) && (ss[x + 1][y] != null))
			result[1] = ss[x + 1][y].getWei();
		// bottom
		if ((y > 0) && (ss[x][y - 1] != null))
			result[2] = ss[x][y - 1].getWei();
		// left
		if ((x > 0) && (ss[x - 1][y] != null))
			result[3] = ss[x - 1][y].getWei();

		// top-left
		if ((y < size - 1) && (x > 0) && (ss[x - 1][y + 1] != null))
			result[4] = ss[x - 1][y + 1].getWei();
		// top-right
		if ((x < size - 1) && (y < size - 1) && (ss[x + 1][y + 1] != null))
			result[5] = ss[x + 1][y + 1].getWei();
		// bottom-right
		if ((y > 0) && (x < size - 1) && (ss[x + 1][y - 1] != null))
			result[6] = ss[x + 1][y - 1].getWei();
		// bottom-left
		if ((y > 0) && (x > 0) && (ss[x - 1][y - 1] != null))
			result[7] = ss[x - 1][y - 1].getWei();

		return result;

	} // end checkWeis()

	public Chain[] checkChains(Board b) {

		int x = this.location.x;
		int y = this.location.y;

		int size = b.getSize();
		Stone[][] ss = b.getStones();

		// { top, right, bottom, left, top-left, top-right, bottom-right,
		// bottom-left }
		Chain[] result = { null, null, null, null, null, null, null, null };

		// top
		if ((y < size - 1) && (ss[x][y + 1] != null))
			result[0] = ss[x][y + 1].getChain();
		// right
		if ((x < size - 1) && (ss[x + 1][y] != null))
			result[1] = ss[x + 1][y].getChain();
		// bottom
		if ((y > 0) && (ss[x][y - 1] != null))
			result[2] = ss[x][y - 1].getChain();
		// left
		if ((x > 0) && (ss[x - 1][y] != null))
			result[3] = ss[x - 1][y].getChain();
		// top-left
		if ((y < size - 1) && (x > 0) && (ss[x - 1][y + 1] != null))
			result[4] = ss[x - 1][y + 1].getChain();
		// top-right
		if ((x < size - 1) && (y < size - 1) && (ss[x + 1][y + 1] != null))
			result[5] = ss[x + 1][y + 1].getChain();
		// bottom-right
		if ((y > 0) && (x < size - 1) && (ss[x + 1][y - 1] != null))
			result[6] = ss[x + 1][y - 1].getChain();
		// bottom-left
		if ((y > 0) && (x > 0) && (ss[x - 1][y - 1] != null))
			result[7] = ss[x - 1][y - 1].getChain();

		return result;

	} // end checkChains()

	public int[] checkQiforYan(Board b) {

		// { top, right, bottom, left, top-left, top-right, bottom-right,
		// bottom-left }
		int[] result = { -1, -1, -1, -1, -1, -1, -1, -1 };
		int x = this.location.x;
		int y = this.location.y;
		int size = b.getSize();
		Stone[][] ss = b.getStones();

		// top
		if ((y < size - 1) && (ss != null) && (ss[x][y + 1] != null)) {
			if (!ss[x][y + 1].isYan())
				result[0] = ss[x][y + 1].getColor();
			else if (ss[x][y + 1].getChain().getChainIndex() == this.getChain()
					.getChainIndex())
				result[0] = 2;
			else
				result[0] = -1;
		} else
			result[0] = 5;

		// right
		if ((x < size - 1) && (ss != null) && (ss[x + 1][y] != null)) {
			if (!ss[x + 1][y].isYan())
				result[1] = ss[x + 1][y].getColor();
			else if (ss[x + 1][y].getChain().getChainIndex() == this.getChain()
					.getChainIndex())
				result[1] = 2;
			else
				result[1] = -1;
		} else
			result[1] = 5;

		// bottom
		if ((y > 0) && (ss != null) && (ss[x][y - 1] != null)) {
			if ((ss[x][y - 1] != null) && (!ss[x][y - 1].isYan()))
				result[2] = ss[x][y - 1].getColor();
			else if (ss[x][y - 1].getChain().getChainIndex() == this.getChain()
					.getChainIndex())
				result[2] = 2;
			else
				result[2] = -1;
		} else
			result[2] = 5;

		// left
		if ((x > 0) && (ss != null) && (ss[x - 1][y] != null)) {
			if (!ss[x - 1][y].isYan())
				result[3] = ss[x - 1][y].getColor();
			else if (ss[x - 1][y].getChain().getChainIndex() == this.getChain()
					.getChainIndex())
				result[3] = 2;
			else
				result[3] = -1;
		} else
			result[3] = 5;

		// top-left
		if ((y < size - 1) && (x > 0) && (ss != null)
				&& (ss[x - 1][y + 1] != null)) {
			if ((ss[x - 1][y + 1] != null) && (!ss[x - 1][y + 1].isYan()))
				result[4] = ss[x - 1][y + 1].getColor();
			else if (ss[x - 1][y + 1].getChain().getChainIndex() == this
					.getChain().getChainIndex())
				result[4] = 2;
			else
				result[4] = -1;
		} else
			result[4] = 5;

		// top-right
		if ((y < size - 1) && (x < size - 1) && (ss != null)
				&& (ss[x + 1][y + 1] != null)) {
			if ((ss[x + 1][y + 1] != null) && (!ss[x + 1][y + 1].isYan()))
				result[5] = ss[x + 1][y + 1].getColor();
			else if (ss[x + 1][y + 1].getChain().getChainIndex() == this
					.getChain().getChainIndex())
				result[5] = 2;
			else
				result[5] = -1;
		} else
			result[5] = 5;

		// bottom-right
		if ((y > 0) && (x < size - 1) && (ss != null)
				&& (ss[x + 1][y - 1] != null)) {
			if ((ss[x + 1][y - 1] != null) && (!ss[x + 1][y - 1].isYan()))
				result[6] = ss[x + 1][y - 1].getColor();
			else if (ss[x + 1][y - 1].getChain().getChainIndex() == this
					.getChain().getChainIndex())
				result[6] = 2;
			else
				result[6] = -1;
		} else
			result[6] = 5;

		// bottom-left
		if ((y > 0) && (x > 0) && (ss != null) && (ss[x - 1][y - 1] != null)) {
			if ((ss[x - 1][y - 1] != null) && (!ss[x - 1][y - 1].isYan()))
				result[7] = ss[x - 1][y - 1].getColor();
			else if (ss[x - 1][y - 1].getChain().getChainIndex() == this
					.getChain().getChainIndex())
				result[7] = 2;
			else
				result[7] = -1;
		} else
			result[7] = 5;

		return result;

	} // end checkQiforYan()

	public int getEnemyColor() {
		int result = -1;
		if (this.color == 0)
			result = 1;
		else if (this.color == 1)
			result = 0;
		return result;
	} // end getEnemyColor()

	public boolean equals(Stone s) {
		return Stone.sComp.equals(this, s);
	} // end equals()

	@Override
	public int compareTo(Stone s) {
		int compare = Stone.sComp.compare(this, s);
		return compare;
	} // end compareTo()

	@Override
	public String toString() {
		return "Stone [color=" + this.color + ", value=" + this.value
				+ ", isYan=" + this.isYan + ", isZhenYan=" + this.isZhenYan
				+ ", isJiaYan=" + this.isJiaYan + ", location=" + this.location
				+ ", belongto=" + this.belongto + "]";
	} // end toString()

	/*
	 * getters and setters
	 */
	public int getColor() {
		return this.color;
	} // end getColor()

	public void setColor(int c) {
		this.color = c;
	} // end setColor()

	public int getValue() {
		return this.value;
	} // end getValue()

	public void setValue(int v) {
		this.value = v;
	} // end setValue()

	public Chain getChain() {
		return this.chain;
	} // end getChain()

	public void setChain(Chain c) {
		this.chain = c;
	} // end setChain()

	public Wei getWei() {
		return this.wei;
	} // end getChain()

	public void setWei(Wei w) {
		this.wei = w;
	} // end setChain()

	public Point getLocation() {
		return this.location;
	} // end getLocation()

	public void setLocation(Point l) {
		this.location = l;
	} // end setLocation()

	public boolean isYan() {
		return this.isYan;
	} // end is()

	public void setYan(boolean y) {
		this.isYan = y;
	} // end set()

	public boolean isZhenYan() {
		return this.isZhenYan;
	} // end isZhenYan()

	public void setZhenYan(boolean zy) {
		this.isZhenYan = zy;
	} // end setZhenYan()

	public boolean isJiaYan() {
		return isJiaYan;
	} // end isJiaYan()

	public void setJiaYan(boolean jy) {
		this.isJiaYan = jy;
	} // setJiaYan

	public static StoneComparator getsComp() {
		return Stone.sComp;
	} // end getsComp()

	public static void setsComp(StoneComparator sComp) {
		Stone.sComp = sComp;
	} // end setsComp()

	public int getBelongto() {
		return this.belongto;
	} // end getBelongto()

	public void setBelongto(int belongto) {
		this.belongto = belongto;
	} // end setBelongto()

} // end class
