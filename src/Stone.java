import java.awt.Point;

/**
 * Stone.java - Used to keep track of pieces on the board
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <adam@akmaz.io>
 */

public class Stone implements Comparable<Stone>, Cloneable {

	private static StoneComparator comparator = new StoneComparator();

	// color -2 represents grey (Yan).
	// color -1 represents empty
	// color 0 represent black player
	// color 1 represent white player
	// color 3 represent black territory
	// color 4 represent white territory

	private Player player;
	private int value = 0;
	private Player belongsTo;
	private boolean isYan = false;
	private boolean isZhenYan = false;
	private boolean isJiaYan = false;

	private Stone nextStone = null;
	private Chain chain = null;
	private Wei wei = null;
	private Point location = new Point(0, 0);

	// initial an empty Stone for deleting purpose
	public Stone() {
		this.player = Player.NOT_A_PLAYER;
		this.belongsTo = Player.NOT_A_PLAYER;
	} // end constructor

	public Stone(Player player, Point location) {
		this.player = player;
		this.location = location;
		this.belongsTo = player;
	} // end constructor

	public Stone(Point location) {
		this.location = location;
		this.player = Player.NOT_A_PLAYER;
		this.belongsTo = Player.NOT_A_PLAYER;
	} // end constructor

	// we are able to clone the stone()
	public Object clone() {
		Stone stone = null;
		try {
			stone = (Stone) super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			cloneNotSupportedException.printStackTrace();
		}
		return stone;
	}

	public Player[] checkQi(Stone[][] stonesOnTheBoard) {

		int x = this.location.x;
		int y = this.location.y;
		int boardSize = stonesOnTheBoard.length;

		// { top, right, bottom, left, top-left, top-right, bottom-right,
		// bottom-left }
		Player[] qis = {
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER
		};

		// top
		if ((y < boardSize - 1) && (stonesOnTheBoard[x][y + 1] != null))
			qis[0] = stonesOnTheBoard[x][y + 1].getPlayer();
		// right
		if ((x < boardSize - 1) && (stonesOnTheBoard[x + 1][y] != null))
			qis[1] = stonesOnTheBoard[x + 1][y].getPlayer();
		// bottom
		if ((y > 0) && (stonesOnTheBoard[x][y - 1] != null))
			qis[2] = stonesOnTheBoard[x][y - 1].getPlayer();
		// left
		if ((x > 0) && (stonesOnTheBoard[x - 1][y] != null))
			qis[3] = stonesOnTheBoard[x - 1][y].getPlayer();

		// top-left
		if ((y < boardSize - 1) && (x > 0) && (stonesOnTheBoard[x - 1][y + 1] != null))
			qis[4] = stonesOnTheBoard[x - 1][y + 1].getPlayer();
		// top-right
		if ((y < boardSize - 1) && (x < boardSize - 1) && (stonesOnTheBoard[x + 1][y + 1] != null))
			qis[5] = stonesOnTheBoard[x + 1][y + 1].getPlayer();
		// bottom-right
		if ((y > 0) && (x < boardSize - 1) && (stonesOnTheBoard[x + 1][y - 1] != null))
			qis[6] = stonesOnTheBoard[x + 1][y - 1].getPlayer();
		// bottom-left
		if ((y > 0) && (x > 0) && (stonesOnTheBoard[x - 1][y - 1] != null))
			qis[7] = stonesOnTheBoard[x - 1][y - 1].getPlayer();

		return qis;
	} // end checkQi()

	public Wei[] checkWeis(Stone[][] stonesOnTheBoard) {

		int x = this.location.x;
		int y = this.location.y;
		int size = stonesOnTheBoard.length;

		// { top, right, bottom, left, top-left, top-right, bottom-right,
		// bottom-left, center }
		Wei[] weis = { null, null, null, null, null, null, null, null, null };

		// top
		if ((y < size - 1) && (stonesOnTheBoard[x][y + 1] != null))
			weis[0] = stonesOnTheBoard[x][y + 1].getWei();
		// right
		if ((x < size - 1) && (stonesOnTheBoard[x + 1][y] != null))
			weis[1] = stonesOnTheBoard[x + 1][y].getWei();
		// bottom
		if ((y > 0) && (stonesOnTheBoard[x][y - 1] != null))
			weis[2] = stonesOnTheBoard[x][y - 1].getWei();
		// left
		if ((x > 0) && (stonesOnTheBoard[x - 1][y] != null))
			weis[3] = stonesOnTheBoard[x - 1][y].getWei();

		// top-left
		if ((y < size - 1) && (x > 0) && (stonesOnTheBoard[x - 1][y + 1] != null))
			weis[4] = stonesOnTheBoard[x - 1][y + 1].getWei();
		// top-right
		if ((x < size - 1) && (y < size - 1) && (stonesOnTheBoard[x + 1][y + 1] != null))
			weis[5] = stonesOnTheBoard[x + 1][y + 1].getWei();
		// bottom-right
		if ((y > 0) && (x < size - 1) && (stonesOnTheBoard[x + 1][y - 1] != null))
			weis[6] = stonesOnTheBoard[x + 1][y - 1].getWei();
		// bottom-left
		if ((y > 0) && (x > 0) && (stonesOnTheBoard[x - 1][y - 1] != null))
			weis[7] = stonesOnTheBoard[x - 1][y - 1].getWei();

		// center
		if ((stonesOnTheBoard[x][y] != null))
			weis[8] = stonesOnTheBoard[x][y].getWei();

		return weis;

	}

	public Chain[] checkChains(Stone[][] stonesOnTheBoard) {

		int x = this.location.x;
		int y = this.location.y;

		int boardSize = stonesOnTheBoard.length;

		// { top, right, bottom, left, top-left, top-right, bottom-right,center
		// bottom-left }
		Chain[] chains = { null, null, null, null, null, null, null, null, null };

		// top
		if ((y < boardSize - 1) && (stonesOnTheBoard[x][y + 1] != null))
			chains[0] = stonesOnTheBoard[x][y + 1].getChain();
		// right
		if ((x < boardSize - 1) && (stonesOnTheBoard[x + 1][y] != null))
			chains[1] = stonesOnTheBoard[x + 1][y].getChain();
		// bottom
		if ((y > 0) && (stonesOnTheBoard[x][y - 1] != null))
			chains[2] = stonesOnTheBoard[x][y - 1].getChain();
		// left
		if ((x > 0) && (stonesOnTheBoard[x - 1][y] != null))
			chains[3] = stonesOnTheBoard[x - 1][y].getChain();

		// top-left
		if ((y < boardSize - 1) && (x > 0) && (stonesOnTheBoard[x - 1][y + 1] != null))
			chains[4] = stonesOnTheBoard[x - 1][y + 1].getChain();
		// top-right
		if ((x < boardSize - 1) && (y < boardSize - 1) && (stonesOnTheBoard[x + 1][y + 1] != null))
			chains[5] = stonesOnTheBoard[x + 1][y + 1].getChain();
		// bottom-right
		if ((y > 0) && (x < boardSize - 1) && (stonesOnTheBoard[x + 1][y - 1] != null))
			chains[6] = stonesOnTheBoard[x + 1][y - 1].getChain();
		// bottom-left
		if ((y > 0) && (x > 0) && (stonesOnTheBoard[x - 1][y - 1] != null))
			chains[7] = stonesOnTheBoard[x - 1][y - 1].getChain();
		// center
		if (stonesOnTheBoard[x][y] != null)
			chains[8] = stonesOnTheBoard[x][y].getChain();
		return chains;
	} // end checkChains()

	public Player[] checkQiForYan(Stone[][] stonesOnTheBoard) {

		// -1 represents empty
		// 0 represent black player stone
		// 1 represent white player stone
		// 2 represent the same Yan
		// 5 represent outside the board
		int x = this.location.x;
		int y = this.location.y;
		int boardSize = stonesOnTheBoard.length;

		// { top, right, bottom, left, top-left, top-right, bottom-right,
		// bottom-left }
		Player[] yans = {
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER,
				Player.NOT_A_PLAYER
		};

		// top
		if (y < boardSize - 1) {
			if (stonesOnTheBoard[x][y + 1] != null) {
				if (!stonesOnTheBoard[x][y + 1].isYan) {
					yans[0] = stonesOnTheBoard[x][y + 1].getPlayer();
				} else {
					if (stonesOnTheBoard[x][y + 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						yans[0] = Player.THE_SAME_YAN;
					} else {
						yans[0] = Player.NOT_A_PLAYER;
					}
				}
			}
		} else {
			yans[0] = Player.OUTSIDE_THE_BOARD;
		}
		// right
		if (x < boardSize - 1) {
			if (stonesOnTheBoard[x + 1][y] != null) {
				if (!stonesOnTheBoard[x + 1][y].isYan) {
					yans[1] = stonesOnTheBoard[x + 1][y].getPlayer();
				} else {

					if (stonesOnTheBoard[x + 1][y].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						yans[1] = Player.THE_SAME_YAN;
					} else {
						yans[1] = Player.NOT_A_PLAYER;
					}

				}
			}
		} else {
			yans[1] = Player.OUTSIDE_THE_BOARD;
		}
		// bottom
		if (y > 0) {
			if (stonesOnTheBoard[x][y - 1] != null) {
				if (!stonesOnTheBoard[x][y - 1].isYan) {
					yans[2] = stonesOnTheBoard[x][y - 1].getPlayer();
				} else {
					if (stonesOnTheBoard[x][y - 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						yans[2] = Player.THE_SAME_YAN;
					} else {
						yans[2] = Player.NOT_A_PLAYER;
					}
				}
			}

		} else {
			yans[2] = Player.OUTSIDE_THE_BOARD;
		}
		// left
		if (x > 0) {
			if (stonesOnTheBoard[x - 1][y] != null) {
				if (!stonesOnTheBoard[x - 1][y].isYan) {
					yans[3] = stonesOnTheBoard[x - 1][y].getPlayer();
				} else {
					if (stonesOnTheBoard[x - 1][y].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						yans[3] = Player.THE_SAME_YAN;
					} else {
						yans[3] = Player.NOT_A_PLAYER;
					}
				}
			}
		} else {
			yans[3] = Player.OUTSIDE_THE_BOARD;
		}

		// top-left
		if ((y < boardSize - 1) && (x > 0)) {
			if (stonesOnTheBoard[x - 1][y + 1] != null) {
				if (!stonesOnTheBoard[x - 1][y + 1].isYan) {
					yans[4] = stonesOnTheBoard[x - 1][y + 1].getPlayer();
				} else {

					if (stonesOnTheBoard[x - 1][y + 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						yans[4] = Player.THE_SAME_YAN;
					} else {
						yans[4] = Player.NOT_A_PLAYER;
					}
				}
			}
		} else {
			yans[4] = Player.OUTSIDE_THE_BOARD;
		}
		// top-right
		if ((y < boardSize - 1) && (x < boardSize - 1)) {
			if (stonesOnTheBoard[x + 1][y + 1] != null) {
				if (!stonesOnTheBoard[x + 1][y + 1].isYan) {
					yans[5] = stonesOnTheBoard[x + 1][y + 1].getPlayer();
				} else {
					if (stonesOnTheBoard[x + 1][y + 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						yans[5] = Player.THE_SAME_YAN;
					} else {
						yans[5] = Player.NOT_A_PLAYER;
					}
				}
			}
		} else {
			yans[5] = Player.OUTSIDE_THE_BOARD;
		}

		// bottom-right
		if ((y > 0) && (x < boardSize - 1)) {
			if (stonesOnTheBoard[x + 1][y - 1] != null) {
				if (!stonesOnTheBoard[x + 1][y - 1].isYan) {
					yans[6] = stonesOnTheBoard[x + 1][y - 1].getPlayer();
				} else {
					if (stonesOnTheBoard[x + 1][y - 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						yans[6] = Player.THE_SAME_YAN;
					} else {
						yans[6] = Player.NOT_A_PLAYER;
					}
				}
			}
		} else {
			yans[6] = Player.OUTSIDE_THE_BOARD;
		}

		// bottom-left
		if ((y > 0) && (x > 0)) {
			if (stonesOnTheBoard[x - 1][y - 1] != null) {
				if (!stonesOnTheBoard[x - 1][y - 1].isYan) {
					yans[7] = stonesOnTheBoard[x - 1][y - 1].getPlayer();
				} else {
					if (stonesOnTheBoard[x - 1][y - 1].getChain().getChainIndex() == this
							.getChain().getChainIndex()) {
						yans[7] = Player.THE_SAME_YAN;
					} else {
						yans[7] = Player.NOT_A_PLAYER;
					}
				}
			}
		} else {
			yans[7] = Player.OUTSIDE_THE_BOARD;
		}

		return yans;
	} // end checkQi()

	public Player getPlayer() {
		return this.player;
	} // end getColor()

	public void setPlayer(Player player) {
		this.player = player;
	} // end setColor()

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

	public boolean isYan() {
		return this.isYan;
	} // end is()

	public void setYan(boolean isYan) {
		this.isYan = isYan;
	} // end set()

	public boolean isZhenYan() {
		return this.isZhenYan;
	} // end isZhenYan()0

	public void setZhenYan(boolean isZhenYan) {
		this.isZhenYan = isZhenYan;
	} // end setZhenYan()

	public boolean isJiaYan() {
		return isJiaYan;
	} // end isJiaYan()

	public void setJiaYan(boolean isJiaYan) {
		this.isJiaYan = isJiaYan;
	} // setJiaYan

	public void setBelongsTo(Player belongsTo) {
		this.belongsTo = belongsTo;
	}

	public Player getBelongsTo() {
		return this.belongsTo;
	}

	@Override
	public int compareTo(Stone stone) {
		int compare = comparator.compare(this, stone);
		return compare;
	} // end compareTo()

	@Override
	public String toString() {
		return "[Stone Color=" + this.player + ", value=" + this.value
				+ ", belongsTo=" + this.belongsTo + ", isYan=" + this.isYan
				+ ", isZhenYan=" + this.isZhenYan + ", isJiaYan="
				+ this.isJiaYan + ", location= (" + this.location.x + ","
				+ this.location.y + ")]";
	} // end toString()

} // end class
