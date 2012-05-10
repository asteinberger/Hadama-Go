import java.awt.Point;

/**
 * Stone.java - Used to keep track of pieces on the board
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <steinz08@gmail.com>
 */

public class Stone implements Comparable<Stone>, Cloneable {

	private static StoneComparator sComp = new StoneComparator();

	// color -2 represents grey (Yan).
	// color -1 represents empty
	// color 0 represent black player
	// color 1 represent white player
	// color 3 represent black territory
	// color 4 represent white territory

	private int color;
	private int value = 0;
	private int belongto;
	private boolean isYan = false;
	private boolean isZhenYan = false;
	private boolean isJiaYan = false;

	private Stone next = null;
	private Chain chain = null;
	private Wei wei = null;
	private Point location = new Point(0, 0);

	// initial an empty Stone for deleting purpose
	public Stone() {
		this.color = -1;
		this.belongto = -1;
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

	// we are able to clone the stone()
	public Object clone() {
		Stone o = null;
		try {
			o = (Stone) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

	public int[] checkQi(Stone[][] b) {

		int x = this.location.x;
		int y = this.location.y;
		int size = b.length;

		// { top, right, bottom, left, top-left, top-right, bottom-right,
		// bottom-left }
		int[] result = { -1, -1, -1, -1, -1, -1, -1, -1 };

		// top
		if ((y < size - 1) && (b[x][y + 1] != null))
			result[0] = b[x][y + 1].getColor();
		// right
		if ((x < size - 1) && (b[x + 1][y] != null))
			result[1] = b[x + 1][y].getColor();
		// bottom
		if ((y > 0) && (b[x][y - 1] != null))
			result[2] = b[x][y - 1].getColor();
		// left
		if ((x > 0) && (b[x - 1][y] != null))
			result[3] = b[x - 1][y].getColor();

		// top-left
		if ((y < size - 1) && (x > 0) && (b[x - 1][y + 1] != null))
			result[4] = b[x - 1][y + 1].getColor();
		// top-right
		if ((y < size - 1) && (x < size - 1) && (b[x + 1][y + 1] != null))
			result[5] = b[x + 1][y + 1].getColor();
		// bottom-right
		if ((y > 0) && (x < size - 1) && (b[x + 1][y - 1] != null))
			result[6] = b[x + 1][y - 1].getColor();
		// bottom-left
		if ((y > 0) && (x > 0) && (b[x - 1][y - 1] != null))
			result[7] = b[x - 1][y - 1].getColor();

		return result;
	} // end checkQi()

	public Wei[] checkWeis(Stone[][] b) {

		int x = this.location.x;
		int y = this.location.y;
		int size = b.length;

		// { top, right, bottom, left, top-left, top-right, bottom-right,
		// bottom-left, center }
		Wei[] result = { null, null, null, null, null, null, null, null, null };

		// top
		if ((y < size - 1) && (b[x][y + 1] != null))
			result[0] = b[x][y + 1].getWei();
		// right
		if ((x < size - 1) && (b[x + 1][y] != null))
			result[1] = b[x + 1][y].getWei();
		// bottom
		if ((y > 0) && (b[x][y - 1] != null))
			result[2] = b[x][y - 1].getWei();
		// left
		if ((x > 0) && (b[x - 1][y] != null))
			result[3] = b[x - 1][y].getWei();

		// top-left
		if ((y < size - 1) && (x > 0) && (b[x - 1][y + 1] != null))
			result[4] = b[x - 1][y + 1].getWei();
		// top-right
		if ((x < size - 1) && (y < size - 1) && (b[x + 1][y + 1] != null))
			result[5] = b[x + 1][y + 1].getWei();
		// bottom-right
		if ((y > 0) && (x < size - 1) && (b[x + 1][y - 1] != null))
			result[6] = b[x + 1][y - 1].getWei();
		// bottom-left
		if ((y > 0) && (x > 0) && (b[x - 1][y - 1] != null))
			result[7] = b[x - 1][y - 1].getWei();

		// center
		if ((b[x][y] != null))
			result[8] = b[x][y].getWei();

		return result;

	}

	public Chain[] checkChains(Stone[][] b) {

		int x = this.location.x;
		int y = this.location.y;

		int size = b.length;

		// { top, right, bottom, left, top-left, top-right, bottom-right,center
		// bottom-left }
		Chain[] result = { null, null, null, null, null, null, null, null, null };

		// top
		if ((y < size - 1) && (b[x][y + 1] != null))
			result[0] = b[x][y + 1].getChain();
		// right
		if ((x < size - 1) && (b[x + 1][y] != null))
			result[1] = b[x + 1][y].getChain();
		// bottom
		if ((y > 0) && (b[x][y - 1] != null))
			result[2] = b[x][y - 1].getChain();
		// left
		if ((x > 0) && (b[x - 1][y] != null))
			result[3] = b[x - 1][y].getChain();

		// top-left
		if ((y < size - 1) && (x > 0) && (b[x - 1][y + 1] != null))
			result[4] = b[x - 1][y + 1].getChain();
		// top-right
		if ((x < size - 1) && (y < size - 1) && (b[x + 1][y + 1] != null))
			result[5] = b[x + 1][y + 1].getChain();
		// bottom-right
		if ((y > 0) && (x < size - 1) && (b[x + 1][y - 1] != null))
			result[6] = b[x + 1][y - 1].getChain();
		// bottom-left
		if ((y > 0) && (x > 0) && (b[x - 1][y - 1] != null))
			result[7] = b[x - 1][y - 1].getChain();
		// center
		if (b[x][y] != null)
			result[8] = b[x][y].getChain();
		return result;
	} // end checkChains()

	public int[] checkQiforYan(Stone[][] b) {

		// -1 represents empty
		// 0 represent black player stone
		// 1 represent white player stone
		// 2 represent the same Yan
		// 5 represent outside the board
		int x = this.location.x;
		int y = this.location.y;
		int size = b.length;

		// { top, right, bottom, left, top-left, top-right, bottom-right,
		// bottom-left }
		int[] result = { -1, -1, -1, -1, -1, -1, -1, -1 };

		// top
		if (y < size - 1) {
			if (b[x][y + 1] != null) {

				if (!b[x][y + 1].isYan) {
					result[0] = b[x][y + 1].getColor();
				} else {
					if (b[x][y + 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						result[0] = 2;
					} else {
						result[0] = -1;
					}
				}
			}

		} else {
			result[0] = 5;

		}
		// right
		if (x < size - 1) {
			if (b[x + 1][y] != null) {
				if (!b[x + 1][y].isYan) {
					result[1] = b[x + 1][y].getColor();
				} else {

					if (b[x + 1][y].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						result[1] = 2;
					} else {
						result[1] = -1;
					}

				}
			}
		} else {
			result[1] = 5;
		}
		// bottom
		if (y > 0) {
			if (b[x][y - 1] != null) {
				if (!b[x][y - 1].isYan) {
					result[2] = b[x][y - 1].getColor();
				} else {

					if (b[x][y - 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						result[2] = 2;
					} else {
						result[2] = -1;
					}

				}
			}

		} else {
			result[2] = 5;
		}
		// left
		if (x > 0) {
			if (b[x - 1][y] != null) {
				if (!b[x - 1][y].isYan) {
					result[3] = b[x - 1][y].getColor();
				} else {

					if (b[x - 1][y].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						result[3] = 2;
					} else {
						result[3] = -1;
					}

				}
			}
		} else {
			result[3] = 5;
		}

		// top-left
		if ((y < size - 1) && (x > 0)) {
			if (b[x - 1][y + 1] != null) {
				if (!b[x - 1][y + 1].isYan) {
					result[4] = b[x - 1][y + 1].getColor();
				} else {

					if (b[x - 1][y + 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						result[4] = 2;
					} else {
						result[4] = -1;
					}
				}
			}
		} else {
			result[4] = 5;
		}
		// top-right
		if ((y < size - 1) && (x < size - 1)) {
			if (b[x + 1][y + 1] != null) {
				if (!b[x + 1][y + 1].isYan) {
					result[5] = b[x + 1][y + 1].getColor();
				} else {

					if (b[x + 1][y + 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						result[5] = 2;
					} else {
						result[5] = -1;
					}
				}
			}
		} else {

			result[5] = 5;
		}

		// bottom-right
		if ((y > 0) && (x < size - 1)) {
			if (b[x + 1][y - 1] != null) {
				if (!b[x + 1][y - 1].isYan) {
					result[6] = b[x + 1][y - 1].getColor();
				} else {
					if (b[x + 1][y - 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						result[6] = 2;
					} else {
						result[6] = -1;
					}
				}
			}
		} else {
			result[6] = 5;
		}

		// bottom-left
		if ((y > 0) && (x > 0)) {
			if (b[x - 1][y - 1] != null) {
				if (!b[x - 1][y - 1].isYan) {
					result[7] = b[x - 1][y - 1].getColor();
				} else {
					if (b[x - 1][y - 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						result[7] = 2;
					} else {
						result[7] = -1;
					}
				}
			}
		} else {
			result[7] = 5;
		}

		return result;
	} // end checkQi()

	public int getColor() {
		return this.color;
	} // end getColor()

	public void setColor(int pc) {
		this.color = pc;
	} // end setColor()

	public int getValue() {
		return this.value;
	} // end getValue()

	public void setValue(int value) {
		this.value = value;
	} // end setValue()

	public Stone getNext() {
		return this.next;
	} // end getNext()

	public void setNext(Stone next) {
		this.next = next;
	} // end setNext()

	public Chain getChain() {
		return this.chain;
	} // end getChain()

	public void setChain(Chain chain) {
		this.chain = chain;
	} // end setChain()

	public Wei getWei() {
		return this.wei;
	} // end getChain()

	public void setWei(Wei wei) {
		this.wei = wei;
	} // end setChain()

	public Point getLocation() {
		return this.location;
	} // end getLocation()

	public void setLocation(Point location) {
		this.location = location;
	} // end setLocation()

	public boolean isYan() {
		return this.isYan;
	} // end is()

	public void setYan(boolean isYan) {
		this.isYan = isYan;
	} // end set()

	public boolean isZhenYan() {
		return this.isZhenYan;
	} // end isZhenYan()0

	public void setZhenYan(boolean isZhenyan) {
		this.isZhenYan = isZhenyan;
	} // end setZhenYan()

	public boolean isJiaYan() {
		return isJiaYan;
	} // end isJiaYan()

	public void setJiaYan(boolean isJiayan) {
		this.isJiaYan = isJiayan;
	} // setJiaYan

	public boolean equals(Stone s) {
		boolean isEqual = sComp.equals(this, s);
		return isEqual;
	} // end equals()

	public static StoneComparator getsComp() {
		return sComp;
	} // end getsComp()

	public static void setsComp(StoneComparator sComp) {
		Stone.sComp = sComp;
	} // end setsComp()

	public void setBelongto(int belongto) {
		this.belongto = belongto;
	}

	public int getBelongto() {
		return this.belongto;
	}

	@Override
	public int compareTo(Stone s) {
		int compare = sComp.compare(this, s);
		return compare;
	} // end compareTo()

	@Override
	public String toString() {
		return "[Stone Color=" + this.color + ", value=" + this.value
				+ ", Belongto=" + this.belongto + ", isYan=" + this.isYan
				+ ", isZhenYan=" + this.isZhenYan + ", isJiaYan="
				+ this.isJiaYan + ", location= (" + this.location.x + ","
				+ this.location.y + ")]";
	} // end toString()

} // end class
