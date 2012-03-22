import java.awt.Point;
import java.util.Random;

//legal move
//1) [empty]
//2) not surrounded
//3) "double jeopardy" (sukuru) - you take one piece and then opponent takes your piece

//illegal move
//1) surrounded
//2) not empty

/**
 * GamePlay - place stones on board for both human and machine players.
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <steinz08@gmail.com>
 * 
 */
public class GamePlay {

	private Point location;
	private Point point;
	private Point intersection;
	private int[] horizontals;
	private int[] verticals;
	private int color;
	private boolean gameOver = false;
	private boolean justPassed = false;
	private String mode;
	private int size;

	// private boolean huiti = false;

	public GamePlay(String m, int s) {
		this.color = 0;
		this.mode = m;
		this.size = s;
		this.location = new Point(0, this.size - 1);
		this.point = new Point(0, 0);
		this.intersection = new Point(0, 0);
		this.horizontals = new int[s];
		this.verticals = new int[s];
	} // end constructor

	/**
	 * Create new GamePlay object given full parameter set for object variables.
	 * 
	 * @param m
	 *            game mode
	 * @param s
	 *            size of GoBoard
	 * @param l
	 *            location
	 * @param p
	 *            point
	 * @param i
	 *            intersection
	 * @param v
	 *            verticals
	 * @param h
	 *            horizontals
	 * @param gb
	 *            goBoard
	 * @param c
	 *            color
	 * @param g
	 *            game
	 */
	public GamePlay(String m, int s, Point l, Point p, Point i, int[] h,
			int[] v, int c) {
		this.mode = m;
		this.size = s;
		this.location = l;
		this.point = p;
		this.intersection = i;
		this.horizontals = h;
		this.verticals = v;
		this.color = c;
	} // end constructor

	private int getStoneColor(Board b, int x, int y) {
		Stone s = new Stone();
		Point p = new Point(x, y);
		if (b.getStone(p) != null)
			s = b.getStone(p);
		int color = 1;
		if (s != null)
			color = s.getColor();
		return color;
	} // end getStoneColor()

	// c = player stone color
	public void placePiece(Board b, int c) {

		Point p = new Point(this.location.x, this.size - this.location.y - 1);
		Stone s = b.getStone(p);
		s.setLocation(p);

		if (s.getChain() != null) {

			// if stone's color is 3, you can put there any time
			if (s.getColor() == 3) {
				if (s.getColor() != 0 && s.getColor() != 1) {
					Point a = new Point(this.location.x, size - this.location.y
							- 1);
					Stone st = new Stone(c, a); // 0 is black 1 is white
					b.addStone(st);
					this.justPassed = false;
					if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
						if (c == 0) {
							this.moveOpponent(b, 1);
						} else {
							this.moveOpponent(b, 0);
						} // end if
					} else {
						this.togglePlayer(c);
					} // end if
				}

			}

			// if it is Yan and only one empty place left
			if (s.getChain().size() == 1) {

				Chain[] lc = s.checkChains(b);
				int TiziNum = 0;
				int HuoziNum = 0;
				int TiziStoneNum = 0;
				for (int i = 0; i < 4; i++) {
					if (lc[i] != null) {

						if (lc[i].first().getColor() != c
								&& lc[i].getQis().size() == 1) {
							TiziNum++;
							TiziStoneNum = lc[i].size();
						}

						if (lc[i].first().getColor() == c
								&& lc[i].getQis().size() > 1) {
							HuoziNum++;

						}
					}

				}

				if (TiziNum > 0 && s.getColor() != 0 && s.getColor() != 1) {

					if (!b.getLastTiziPosition().equals(p)) {

						Point a = new Point(this.location.x, size
								- this.location.y - 1);
						Stone st = new Stone(c, a);
						b.addStone(st);
						this.justPassed = false;
						if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
							if (c == 0) {
								this.moveOpponent(b, 1);
							} else {
								this.moveOpponent(b, 0);
							} // end if
						} else {
							this.togglePlayer(c);
						}
					} else {
						if (TiziStoneNum == 2) {
							Point a = new Point(this.location.x, size
									- this.location.y - 1);
							Stone st = new Stone(c, a);
							b.addStone(st);
							this.justPassed = false;
							if (this.mode.equals("HvC")
									|| this.mode.equals("CvH")) {
								if (c == 0) {
									this.moveOpponent(b, 1);
								} else {
									this.moveOpponent(b, 0);
								} // end if
							} else {
								this.togglePlayer(c);
							}

						}

					}

				}// end if
					// else it is illgal move

				if (HuoziNum > 0 && s.getColor() != 0 && s.getColor() != 1) {
					Point a = new Point(this.location.x, size - this.location.y
							- 1);
					Stone st = new Stone(c, a);
					b.addStone(st);
					this.justPassed = false;
					if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
						if (c == 0) {
							this.moveOpponent(b, 1);
						} else {
							this.moveOpponent(b, 0);
						} // end if
					} else {
						this.togglePlayer(c);
					}
				}// end if
					// else it is illgal move

			} // end if
			else {
				if (s.getColor() != 0 && s.getColor() != 1) {
					Point a = new Point(this.location.x, size - this.location.y
							- 1);
					Stone st = new Stone(c, a);
					b.addStone(st);
					this.justPassed = false;
					if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
						if (c == 0) {
							this.moveOpponent(b, 1);
						} else {
							this.moveOpponent(b, 0);
						} // end if
					} else {
						this.togglePlayer(c);
					} // end if
				}
			}
		} else {
			if (s.getColor() != 0 && s.getColor() != 1) {
				Point a = new Point(this.location.x, size - this.location.y - 1);
				Stone st = new Stone(c, a);
				b.addStone(st);
				this.justPassed = false;
				if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
					if (c == 0) {
						this.moveOpponent(b, 1);
					} else {
						this.moveOpponent(b, 0);
					} // end if
				} else {
					this.togglePlayer(c);
				} // end if
			}
		}

	} // end placePiece()

	protected void togglePlayer(int p) {
		if (p == 0) {
			this.color = 1;
		} else {
			this.color = 0;
		} // end if
	} // end togglePlayer()

	// c = opponent stone color
	public void moveOpponent(Board b, int c) {

		if (!this.gameOver) {

			int oppX = 0;
			int oppY = 0;
			boolean freeSpace = false;
			int color = 0;

			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					color = this.getStoneColor(b, i, j);
					Point p = new Point(i, j);
					boolean isYan = b.getStone(p).isYan();
					if (!isYan) {
						freeSpace = true;
					} // end if
				} // end for
			} // end for

			if (freeSpace) {

				boolean isYan = false;
				int size = 0;

				do {

					Random rand = new Random(System.currentTimeMillis());
					oppX = rand.nextInt(this.size);
					oppY = rand.nextInt(this.size);

					int randPass = rand.nextInt(999);
					if (randPass == 900) {
						if (this.justPassed) {
							this.gameOver = true;
							this.gameOver();
							return;
						} else {
							this.justPassed = true;
							return;
						} // end if
					} else {
						this.justPassed = false;
					} // end if

					Point p = new Point(oppX, oppY);
					color = this.getStoneColor(b, oppX, oppY);
					isYan = b.getStone(p).isYan();
					size = b.getStone(p).getChain().size();

				} while (!(isYan && (this.color != color))
						|| !(isYan && (size == 0)) || (color == 0)
						|| (color == 1));
				Point a = new Point(oppX, oppY);
				Stone s = new Stone(c, a);
				b.addStone(s);

			} // end if

		} else {
			this.gameOver();
		} // else if

	} // end moveOpponent()

	public void forfeit(int p) {
		System.out.println("Player " + Integer.toString(p) + " forfeits!");
		this.gameOver();
	} // end forfeit()

	public void gameOver() {
		System.out.println("Player " + Integer.toString(this.color)
				+ " ended game!");
	} // end forfeit()

	// getters and setters
	public Point getCurrLocation() {
		return this.location;
	} // end getCurrLocation()

	public void setCurrLocation(Point currLocation) {
		this.location = currLocation;
	} // end setCurrLocation()

	public Point getPoint() {
		return this.point;
	} // end getPoint()

	public void setPoint(Point point) {
		this.point = point;
	} // end setPoint()

	public Point getIntersection() {
		return this.intersection;
	} // end getIntersection()

	public void setIntersection(Point intersect) {
		this.intersection = intersect;
	} // end setIntersection()

	public int[] getHorizontals() {
		return this.horizontals;
	} // end getHorizontals()

	public void setHorizontals(int[] horiz) {
		this.horizontals = horiz;
	} // end setHorizontals()

	public int[] getVerticals() {
		return this.verticals;
	} // end getVerticals()

	public void setVerticals(int[] vert) {
		this.verticals = vert;
	} // end setVerticals()

	public int getPlayer() {
		return this.color;
	} // end getPlayer()

	public void setPlayer(int player) {
		this.color = player;
	} // end setPlayer()

	public boolean isGameOver() {
		return this.gameOver;
	} // end isGameOver()

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	} // end setGameOver()

	public boolean isJustPassed() {
		return this.justPassed;
	} // end isJustPassed()

	public void setJustPassed(boolean justPassed) {
		this.justPassed = justPassed;
	} // end setJustPassed()

} // end class
