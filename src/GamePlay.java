import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

/**
 * GamePlay - place stones on board for both human and machine players.
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <steinz08@gmail.com>
 * 
 */
public class GamePlay implements Cloneable {

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
	private MiniMax miniMax;
	private Point LastMovePostion;
	private boolean isLearning = false;

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

	// c = player stone color
	public void placePiece(Board b, int c) throws CloneNotSupportedException, InterruptedException {

		Point p = new Point(this.location.x, size - this.location.y - 1);
		this.goboard = b;
		ArrayList<Chain> removedChains = new ArrayList<Chain>();

		if (c == 0) {

			if (!b.getIllegalMovesforBlack().contains(p)) {

				Point a = new Point(this.location.x, size - this.location.y - 1);
				Stone st = new Stone(c, a); // 0 is black 1 is white
				removedChains = this.goboard.addStone(st);
				this.justPassed = false;
				if ((this.mode.equals("HvC") || this.mode.equals("CvH"))
						&& !isLearning) {
					if (c == 0)
						// this.moveOpponent(b, 1);
						this.miniMax
								.makeBestMoveForMin((GamePlay) this.clone());
					else
						// this.moveOpponent(b, 0);
						this.miniMax
								.makeBestMoveForMax((GamePlay) this.clone());
				} else if (!isLearning)
					this.togglePlayer(c);

				Stone newst = (Stone) st.clone();
				Move newMove = new Move(newst, removedChains,
						this.LastMovePostion, false);

				LastMovePostion = p;
				this.goboard.getMoves().push(newMove);
			}

		}

		if (c == 1) {

			if (!b.getIllegalMovesforWhite().contains(p)) {

				Point a = new Point(this.location.x, size - this.location.y - 1);
				Stone st = new Stone(c, a); // 0 is black 1 is white
				removedChains = this.goboard.addStone(st);
				this.justPassed = false;
				if ((this.mode.equals("HvC") || this.mode.equals("CvH"))
						&& !isLearning) {
					if (c == 0)
						// this.moveOpponent(b, 1);
						this.miniMax
								.makeBestMoveForMin((GamePlay) this.clone());
					else
						// this.moveOpponent(b, 0);
						this.miniMax
								.makeBestMoveForMax((GamePlay) this.clone());
				} else if (!isLearning)
					this.togglePlayer(c);

				Stone newst = (Stone) st.clone();
				Move newMove = new Move(newst, removedChains,
						this.LastMovePostion, false);

				LastMovePostion = p;
				this.goboard.getMoves().push(newMove);

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

	public void newGame() {
		this.goboard.setBoard(new Stone[this.size][this.size]);
		this.goboard.setChains(new ArrayList<Chain>());
		this.goboard.setMoves(new Stack<Move>());
		this.goboard.setWeis(new ArrayList<Wei>());
		this.goboard.setIllegalMovesforBlack(new ArrayList<Point>());
		this.goboard.setIllegalMovesforWhite(new ArrayList<Point>());
		this.LastMovePostion = null;
	} // end newGame()

	public void moveOpponent(Board b, int c) {

		if (!this.gameOver) {

			Point p = new Point(0, 0);

			Random rand = new Random(System.currentTimeMillis());
			int oppX = rand.nextInt(9);
			int oppY = rand.nextInt(9);
			p = new Point(oppX, oppY);

			Point a = new Point(oppX, oppY);
			Stone s = new Stone(c, a); // 0 is black 1 is white
			this.goboard.addStone(s);

		} else {
			this.gameOver();
		} // else if

	} // end moveOpponent()

	public void undoMove(Board b) throws CloneNotSupportedException, InterruptedException {
		if (b.getMoves().size() == 0) {
			System.out.println("No move has been made!");
		} else {
			Move m = b.getMoves().pop();
			boolean ip = m.getisPass();
			Stone s = m.getAddedStone();
			if (!ip) {
				Point location = s.getLocation();
				Stone news = b.getStone(location);
				Chain newc = news.getChain();

				// update this stone's chain, if the size is 1 which we want to
				// move
				// from the chains
				if (newc.size() == 1) {
					b.getChains().remove(
							b.realChainIndex(news.getChain().getChainIndex()));
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
					b.getWeis().remove(
							b.realWeiIndex(news.getWei().getWeiIndex()));
				} else {
					b.getWeis()
							.get(b.realWeiIndex(news.getWei().getWeiIndex()))
							.remove(s);

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

				if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
					if (s.getColor() == 0)
						this.miniMax.makeBestMoveForMin(this);
					else
						this.miniMax.makeBestMoveForMax(this);
				} else
					this.togglePlayer(s.getColor());

				// set the last Move icon
				Point lm = m.getlastMove();
				this.LastMovePostion = lm;

			}// end if
			else {

				if (this.mode.equals("HvC") || this.mode.equals("CvH")) {
					if (s.getColor() == 0)
						this.miniMax.makeBestMoveForMin(this);
					else
						this.miniMax.makeBestMoveForMax(this);
				} else
					this.togglePlayer(s.getColor());

				// this is a bug
				this.justPassed = false;

			}// end else

		}// end else

		Move testm = b.getMoves().peek();
		boolean nextip = testm.getisPass();
		if (nextip) {
			this.justPassed = true;
		}

		this.goboard.illegalMoveDetector();
	}// end undoMove()

	public void forfeit(int p) {
		if (p == 0) {
			System.out.println("Black Player Forfeits!");
		} else {
			System.out.println("White Player Forfeits!");
		}
		this.gameOver();
	} // end forfeit()

	public void gameOver() {

		if (this.color == 0) {
			System.out.println("Black Playerer Ended Game!");
		} else {
			System.out.println("White Player Ended Game!");
		}

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

	public boolean getJustPassed() {
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

	public Point getlastMovePosition() {
		return this.LastMovePostion;
	}

	public boolean isLearning() {
		return isLearning;
	}

	public void setLearning(boolean isLearning) {
		this.isLearning = isLearning;
	}

} // end class
