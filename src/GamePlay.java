import java.awt.Point;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.TreeSet;

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
	private Board goboard;
	private int color;
	private boolean gameOver = false;
	private boolean justPassed = false;
	private String mode;
	private int size;
	private Stack<Move> moves = new Stack<Move>();

	// private boolean huiti = false;

	public GamePlay(Board b, String m, int s) {
		this.color = 0;
		this.mode = m;
		this.size = s;
		this.location = new Point(0, this.size - 1);
		this.point = new Point(0, 0);
		this.intersection = new Point(0, 0);
		this.horizontals = new int[this.size];
		this.verticals = new int[this.size];
		this.goboard = b;
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
			int[] v, Board g, int c) {
		this.mode = m;
		this.size = s;
		this.location = l;
		this.point = p;
		this.intersection = i;
		this.horizontals = h;
		this.verticals = v;
		this.goboard = g;
		this.color = c;
	} // end constructor

	private int getStoneColor(Board b, int x, int y) {
		Stone s = new Stone(b);
		Point p = new Point(x, y);
		if (this.goboard.getStone(p) != null) {
			s = this.goboard.getStone(p);
		} // end if
		int color = 1;
		if (s != null) {
			color = s.getColor();
		} // end if
		return color;
	} // end getStoneColor()

	// c = player stone color
	public void placePiece(Board b, int c) {

		Move m = new Move(b, c);
		TreeSet<Stone> removed = new TreeSet<Stone>();

		Point p = new Point(this.location.x, size - this.location.y - 1);
		Stone s = b.getStone(p);
		s.setLocation(p);
		removed.add(s);
		this.goboard = b;

		if (s.getChain() != null) {

			// if stone's color is 3, you can put there any time
			if (s.getColor() == 3) {
				if (s.getColor() != 0 && s.getColor() != 1) {
					Point a = new Point(this.location.x, size - this.location.y
							- 1);
					Stone st = new Stone(b, c, a); // 0 is black 1 is white
					this.goboard.addStone(st, m);
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

				Chain[] lc = s.checkChains();
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

					if (!this.goboard.getLastTiziPosition().equals(p)) {

						Point a = new Point(this.location.x, size
								- this.location.y - 1);
						Stone st = new Stone(b, c, a);
						this.goboard.addStone(st, m);
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
							Stone st = new Stone(b, c, a);
							this.goboard.addStone(st, m);
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
					Stone st = new Stone(b, c, a);
					this.goboard.addStone(st, m);
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
					Stone st = new Stone(b, c, a);
					this.goboard.addStone(st, m);
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
				Stone st = new Stone(b, c, a);
				this.goboard.addStone(st, m);
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

		m.removeStones(removed);
		this.moves.add(m);

	} // end placePiece()

	public boolean undoMove(Board gb) {

		this.goboard.printBoard();

		if (!this.moves.isEmpty()) {

			this.goboard = gb;
			Move m = this.moves.pop();
			Move mTrash = new Move(gb);

			// Iterator<Chain> it2 = m.getAddedChains().iterator();
			// while (it2.hasNext()) {
			// Chain next = it2.next();
			// System.out.println(next);
			// Iterator<Stone> it = next.iterator();
			// while (it.hasNext()) {
			// Stone next2 = it.next();
			// Point next2Loc = next2.getLocation();
			// this.goboard.removeStone(next2Loc, mTrash);
			// } // end while
			// this.goboard.printBoard();
			// } // end while
			//
			// it2 = m.getRemovedChains().iterator();
			// while (it2.hasNext()) {
			// Chain next = it2.next();
			// System.out.println(next);
			// Iterator<Stone> it = next.iterator();
			// while (it.hasNext()) {
			// Stone next2 = it.next();
			// this.goboard.addStone(next2, mTrash);
			// } // end while
			// this.goboard.printBoard();
			// } // end while
			//
			// Iterator<Wei> it3 = m.getAddedWeis().iterator();
			// while (it3.hasNext()) {
			// Wei next = it3.next();
			// System.out.println(next);
			// Iterator<Stone> it = next.iterator();
			// while (it.hasNext()) {
			// Stone next2 = it.next();
			// Point next2Loc = next2.getLocation();
			// this.goboard.removeStone(next2Loc, mTrash);
			// } // end while
			// this.goboard.printBoard();
			// } // end while
			//
			// it3 = m.getRemovedWeis().iterator();
			// while (it3.hasNext()) {
			// Wei next = it3.next();
			// System.out.println(next);
			// Iterator<Stone> it = next.iterator();
			// while (it.hasNext()) {
			// Stone next2 = it.next();
			// this.goboard.addStone(next2, mTrash);
			// } // end while
			// this.goboard.printBoard();
			// } // end while
			//
			// Iterator<Map.Entry<Stone, Chain>> it4 = m.getAddedToChain()
			// .entrySet().iterator();
			// while (it4.hasNext()) {
			// Map.Entry<Stone, Chain> next = it4.next();
			// System.out.println(next);
			// Stone s = next.getKey();
			// Chain c = next.getValue();
			// c.removeStone(s, mTrash);
			// this.goboard.printBoard();
			// } // end while
			//
			// it4 = m.getRemovedFromChain().entrySet().iterator();
			// while (it4.hasNext()) {
			// Map.Entry<Stone, Chain> next = it4.next();
			// System.out.println(next);
			// Stone s = next.getKey();
			// Chain c = next.getValue();
			// c.addToChain(s, mTrash);
			// this.goboard.printBoard();
			// } // end while
			//
			// Iterator<Map.Entry<Qi, Chain>> it5 = m.getAddedToChainQi()
			// .entrySet().iterator();
			// while (it5.hasNext()) {
			// Map.Entry<Qi, Chain> next = it5.next();
			// System.out.println(next);
			// Qi q = next.getKey();
			// Chain c = next.getValue();
			// c.getQis().remove(q);
			// this.goboard.printBoard();
			// } // end while
			//
			// it5 = m.getRemovedFromChainQi().entrySet().iterator();
			// while (it5.hasNext()) {
			// Map.Entry<Qi, Chain> next = it5.next();
			// System.out.println(next);
			// Qi q = next.getKey();
			// Chain c = next.getValue();
			// c.getQis().add(q);
			// this.goboard.printBoard();
			// } // end while
			//
			// Iterator<Map.Entry<Stone, Wei>> it6 =
			// m.getAddedToWei().entrySet()
			// .iterator();
			// while (it6.hasNext()) {
			// Map.Entry<Stone, Wei> next = it6.next();
			// System.out.println(next);
			// Stone s = next.getKey();
			// Wei w = next.getValue();
			// w.removeStone(s, mTrash);
			// this.goboard.printBoard();
			// } // end while
			//
			// it6 = m.getRemovedFromWei().entrySet().iterator();
			// while (it6.hasNext()) {
			// Map.Entry<Stone, Wei> next = it6.next();
			// System.out.println(next);
			// Stone s = next.getKey();
			// Wei w = next.getValue();
			// w.addToWei(s, mTrash);
			// this.goboard.printBoard();
			// } // end while

			Iterator<Stone> it = m.getAdded().iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				Point nextLoc = next.getLocation();
				this.goboard.removeStone(nextLoc, mTrash);
				this.goboard.printBoard();
			} // end while

			it = m.getRemoved().iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				this.goboard.addStone(next, mTrash);
				this.goboard.printBoard();
			} // end while

			this.goboard.printBoard();

			return true;

		} // end if

		return false;

	} // end undoMove()

	protected void togglePlayer(int p) {
		if (p == 0) {
			this.color = 1;
		} else {
			this.color = 0;
		} // end if
	} // end togglePlayer()

	// c = opponent stone color
	public void moveOpponent(Board b, int c) {

		Move m = new Move(this.goboard);

		if (!this.gameOver) {

			int oppX = 0;
			int oppY = 0;
			boolean freeSpace = false;
			int color = 0;

			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					color = this.getStoneColor(b, i, j);
					Point p = new Point(i, j);
					boolean isYan = this.goboard.getStone(p).isYan();
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
					oppX = rand.nextInt(9);
					oppY = rand.nextInt(9);

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
					isYan = this.goboard.getStone(p).isYan();
					size = this.goboard.getStone(p).getChain().size();

				} while (!(isYan && (this.color != color))
						|| !(isYan && (size == 0)) || (color == 0)
						|| (color == 1));
				Point a = new Point(oppX, oppY);
				Stone s = new Stone(b, c, a);
				this.goboard.addStone(s, m);

			} // end if

		} else {
			this.gameOver();
		} // else if

		this.moves.add(m);

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

	public Board getGoboard() {
		return this.goboard;
	} // end getGoboard()

	public void setGoboard(Board goboard) {
		this.goboard = goboard;
	} // end setGoboard()

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

	public Stack<Move> getMoves() {
		return moves;
	}

	public void setMoves(Stack<Move> moves) {
		this.moves = moves;
	}

} // end class
