import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
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
	private Board goboard;
	private int color;
	private boolean gameOver = false;
	private boolean justPassed = false;
	public String mode;
	private int size;
	private boolean addMovealready = false;
	private MiniMax miniMax;

	// private boolean huiti = false;

	public GamePlay(String m, int s) {
		this.color = 0;
		this.mode = m;
		this.size = s;
		this.location = new Point(0, this.size - 1);
		this.point = new Point(0, 0);
		this.intersection = new Point(0, 0);
		this.horizontals = new int[this.size];
		this.verticals = new int[this.size];
		this.goboard = new Board(this.size);
		this.miniMax = new MiniMax(s);
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
		Stone s = new Stone();
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

		Point p = new Point(this.location.x, size - this.location.y - 1);
		Stone s = b.getStone(p);
		this.goboard = b;
		ArrayList<Chain> removedChains = new ArrayList<Chain>();
		if (s.getChain() != null) {

			// Condition one --- if stone's color is 3 or 4, you can put there
			// any time
			if (s.getColor() == 3 || s.getColor() == 4) {
				if (s.getColor() != 0 && s.getColor() != 1) {
					Point a = new Point(this.location.x, size - this.location.y
							- 1);
					Stone st = new Stone(c, a); // 0 is black 1 is white
												// removedChains =
					removedChains = this.goboard.addStone(st);
					this.justPassed = false;
					if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
						if (c == 0)
							this.miniMax.makeBestMoveForMin(this);
						else
							this.miniMax.makeBestMoveForMax(this);
						this.togglePlayer(c);
					} // end if

					Stone newst = (Stone) st.clone();
					Move newMove = new Move(newst, removedChains);
					if (this.addMovealready == false) {
						this.goboard.moves.push(newMove);
						this.addMovealready = true;
					}
				}

			}

			// Condition two --- if it is Yan and only one empty place left
			if (s.getChain().size() == 1) {

				Chain[] lc = s.checkChains(b.getBoard());
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

					if (!this.goboard.LastTiziPosition.equals(p)) {

						Point a = new Point(this.location.x, size
								- this.location.y - 1);
						Stone st = new Stone(c, a); // 0 is black 1 is white
						removedChains = this.goboard.addStone(st);
						this.justPassed = false;
						if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
							if (c == 0)
								this.miniMax.makeBestMoveForMin(this);
							else
								this.miniMax.makeBestMoveForMax(this);
						} else
							this.togglePlayer(c);

						Stone newst = (Stone) st.clone();
						Move newMove = new Move(newst, removedChains);
						if (this.addMovealready == false) {
							this.goboard.moves.push(newMove);
							this.addMovealready = true;
						}

					} else {
						if (TiziStoneNum == 2) {
							Point a = new Point(this.location.x, size
									- this.location.y - 1);
							Stone st = new Stone(c, a); // 0 is black 1 is
														// white
							removedChains = this.goboard.addStone(st);
							this.justPassed = false;
							if (this.mode.equals("HvC")
									|| this.mode.equals("CvH")) {
								if (c == 0)
									this.miniMax.makeBestMoveForMin(this);
								else
									this.miniMax.makeBestMoveForMax(this);
							} else
								this.togglePlayer(c);

							Stone newst = (Stone) st.clone();
							Move newMove = new Move(newst, removedChains);
							if (this.addMovealready == false) {
								this.goboard.moves.push(newMove);
								this.addMovealready = true;
							}
						}
					}

				}// end if
					// else it is illgal move

				if (HuoziNum > 0 && s.getColor() != 0 && s.getColor() != 1) {
					Point a = new Point(this.location.x, size - this.location.y
							- 1);
					Stone st = new Stone(c, a); // 0 is black 1 is white
					removedChains = this.goboard.addStone(st);
					this.justPassed = false;
					if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
						if (c == 0)
							this.miniMax.makeBestMoveForMin(this);
						else
							this.miniMax.makeBestMoveForMax(this);
					} else
						this.togglePlayer(c);

					Stone newst = (Stone) st.clone();
					Move newMove = new Move(newst, removedChains);
					if (this.addMovealready == false) {
						this.goboard.moves.push(newMove);
						this.addMovealready = true;
					}// end if
				}// end if

				// else it is illgal move

			} // end if
			else {
				if (s.getColor() != 0 && s.getColor() != 1) {
					Point a = new Point(this.location.x, size - this.location.y
							- 1);
					Stone st = new Stone(c, a); // 0 is black 1 is white
					removedChains = this.goboard.addStone(st);
					this.justPassed = false;
					if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
						if (c == 0)
							this.miniMax.makeBestMoveForMin(this);
						else
							this.miniMax.makeBestMoveForMax(this);
					} else
						this.togglePlayer(c);

					Stone newst = (Stone) st.clone();
					Move newMove = new Move(newst, removedChains);
					if (this.addMovealready == false) {
						this.goboard.moves.push(newMove);
						this.addMovealready = true;
					}// end if
				}// end if
			}// end else
		} else {

			if (s.getColor() != 0 && s.getColor() != 1) {
				Point a = new Point(this.location.x, size - this.location.y - 1);
				Stone st = new Stone(c, a); // 0 is black 1 is white

				removedChains = this.goboard.addStone(st);
				this.justPassed = false;
				if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
					if (c == 0)
						this.miniMax.makeBestMoveForMin(this);
					else
						this.miniMax.makeBestMoveForMax(this);
				} else
					this.togglePlayer(c);

				Stone newst = (Stone) st.clone();
				Move newMove = new Move(newst, removedChains);
				if (this.addMovealready == false) {
					this.goboard.moves.push(newMove);
					this.addMovealready = true;
				}// end if
			}// end if

		}// end else

		this.addMovealready = false;

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
				Stone s = new Stone(c, a); // 0 is black 1 is white
				this.goboard.addStone(s);

			} // end if

		} else {
			this.gameOver();
		} // else if

	} // end moveOpponent()

	public void undoMove(Board b) {

		Move m = b.moves.pop();
		Stone s = m.getAddedStone();
		Point location = s.getLocation();
		Stone news = b.getStone(location);
		Chain newc = news.getChain();

		// update this stone's chain, if the size is 1 which we want to move
		// from the chains
		if (newc.size() == 1) {
			b.chains.remove(b.realChainIndex(news.getChain().getChainIndex()));
		}

		// remove the stone from the board
		b.getBoard()[location.x][location.y] = null;

		// update the surrounding qis

		Chain[] lc = news.checkChains(b.getBoard());

		int c0 = 0;
		int c1 = 0;
		int c2 = 0;
		int c3 = 0;
		if (lc[0] != null) {
			c0 = lc[0].getChainIndex();
		}

		if (lc[1] != null) {
			c1 = lc[1].getChainIndex();
		}
		if (lc[2] != null) {
			c2 = lc[2].getChainIndex();
		}
		if (lc[3] != null) {
			c3 = lc[3].getChainIndex();

		}

		if (lc[0] != null) {
			if (lc[0].first().getColor() != -1) {

				// if they are different color
				if (lc[0].first().getColor() != s.getColor()) {
					lc[0].deeplyrecheckQis();
				}
				// if they are same color
				else {
					lc[0].updateChains(s);
				}
			}
		}
		if (lc[1] != null && c0 != c1 && lc[1].size() != 0) {
			if (lc[1].first().getColor() != -1) {
				// if they are different color
				if (lc[1].first().getColor() != s.getColor()) {
					lc[1].deeplyrecheckQis();
				}
				// if they are same color
				else {
					if (b.realChainIndex(lc[1].getChainIndex()) != -1)
						lc[1].updateChains(s);
				}
			}
		}
		if (lc[2] != null && c2 != c0 && c2 != c1 && lc[2].size() != 0) {
			if (lc[2].first().getColor() != -1) {
				// if they are different color
				if (lc[2].first().getColor() != s.getColor()) {
					lc[2].deeplyrecheckQis();
				}
				// if they are same color
				else {
					if (b.realChainIndex(lc[2].getChainIndex()) != -1)
						lc[2].updateChains(s);
				}
			}
		}
		if (lc[3] != null && c3 != c0 && c3 != c1 && c3 != c2
				&& lc[3].size() != 0) {
			if (lc[3].first().getColor() != -1) {
				// if they are different color
				if (lc[3].first().getColor() != s.getColor()) {
					lc[3].deeplyrecheckQis();
				}
				// if they are same color
				else {
					if (b.realChainIndex(lc[3].getChainIndex()) != -1)
						lc[3].updateChains(s);
				}
			}
		}

		// update Wei
		Wei newW = news.getWei();
		if (newW.size() == 1) {

			// problem is here
			b.weis.remove(b.realWeiIndex(news.getWei().getWeiIndex()));
		} else {
			b.weis.get(b.realWeiIndex(news.getWei().getWeiIndex())).remove(s);

		}

		// Add the removed stones back
		ArrayList<Chain> removedChain = m.getRemovedChain();
		for (int i = 0; i < removedChain.size(); i++) {
			Chain c = removedChain.get(i);
			Iterator<Stone> it = c.iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				b.addStone(next);
			}
		}// end for

		// I want to change the player color
		this.justPassed = false;
		if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
			if (s.getColor() == 0)
				this.miniMax.makeBestMoveForMin(this);
			else
				this.miniMax.makeBestMoveForMax(this);
		} else
			this.togglePlayer(s.getColor());

	}// end undoMove()

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
	} // end setHorizontals()getCounter()

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

	public MiniMax getMiniMax() {
		return miniMax;
	}

	public void setMiniMax(MiniMax miniMax) {
		this.miniMax = miniMax;
	}

} // end class
