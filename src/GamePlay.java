import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;
import java.util.TreeSet;

/**
 * GamePlay - place stones on board for both human and machine players.
 * 
 * Have player play human vs neural network.
 * 
 * For human vs neural network, don't train, get weights instead.
 * 
 * Also, get all legal moves, make each legal move, get state value, undo move.
 * 
 * Choose legal move with best state value.
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
	private Point LastMovePostion;
	private Point LastTiziPosition;
	private int LastTiziNum;
	private int alphaBetaDepth = 2;
	private AlphaBeta ab = new AlphaBeta();
	private NetworkTrainer netTrain = new NetworkTrainer(162, 40, 2, 0.0001,
			0.9f, 0.7f, 50000, 0.0001);
	private NeuralNetwork network;

	// private boolean huiti = false;

	public GamePlay(String m, int s) throws Exception {
		this.color = -1;
		this.mode = m;
		this.size = s;
		this.location = new Point(0, this.size - 1);
		this.point = new Point(0, 0);
		this.intersection = new Point(0, 0);
		this.horizontals = new int[this.size];
		this.verticals = new int[this.size];
		this.goboard = new Board(this.size);
		this.network = new NeuralNetwork(162, 40, 2);
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
	 * 
	 * @throws Exception
	 */
	public GamePlay(String m, int s, Point l, Point p, Point i, int[] h,
			int[] v, Board g, int c) throws Exception {
		this.mode = m;
		this.size = s;
		this.location = l;
		this.point = p;
		this.intersection = i;
		this.horizontals = h;
		this.verticals = v;
		this.goboard = g;
		this.color = c;
		this.network = new NeuralNetwork(162, 40, 2);
	} // end constructor

	// c = player stone color
	public void placePiece(Board b, int c) throws Exception {

		this.color = c;
		Point p = new Point(this.location.x, size - this.location.y - 1);
		this.goboard = b;

		if (this.mode.equals("HvH")) {
			if (c == 0) {
				if (!this.goboard.getIllegalMovesforBlack().contains(p)) {

					Point a = new Point(this.location.x, size - this.location.y
							- 1);
					Stone st = new Stone(this.color, a); // 0 is black 1 is
															// white
					ArrayList<Chain> removedChains = this.goboard.addStone(st);
					this.justPassed = false;
					// change player color
					this.togglePlayer(this.color);
					// store for undo move
					Stone newst = (Stone) st.clone();
					Move newMove = new Move(newst, removedChains,
							this.LastMovePostion, false, this.LastTiziPosition,
							this.LastTiziNum);
					this.LastTiziNum = this.goboard.getLastTiziNum();
					this.LastTiziPosition = this.goboard.getLastTiziPosition();
					LastMovePostion = p;
					this.goboard.getMoves().push(newMove);
				}
			}

			if (c == 1) {

				if (!this.goboard.getIllegalMovesforWhite().contains(p)) {

					Point a = new Point(this.location.x, size - this.location.y
							- 1);
					Stone st = new Stone(this.color, a); // 0 is black 1 is
															// white
					ArrayList<Chain> removedChains = this.goboard.addStone(st);
					this.justPassed = false;
					// change player color
					this.togglePlayer(this.color);
					// store for undo move
					Stone newst = (Stone) st.clone();
					Move newMove = new Move(newst, removedChains,
							this.LastMovePostion, false, this.LastTiziPosition,
							this.LastTiziNum);
					this.LastTiziNum = this.goboard.getLastTiziNum();
					this.LastTiziPosition = this.goboard.getLastTiziPosition();
					LastMovePostion = p;

					this.goboard.getMoves().push(newMove);
				}
			}
		} // end mode HvH

		else if (this.mode.equals("HvC")) {

			// Human Move first, that is Black
			if (!this.goboard.getIllegalMovesforBlack().contains(p)) {

				Point a = new Point(this.location.x, size - this.location.y - 1);
				Stone st = new Stone(this.color, a);
				ArrayList<Chain> removedChains = this.goboard.addStone(st);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.color);
				// store for undo move
				Stone newst = (Stone) st.clone();
				Move newMove = new Move(newst, removedChains,
						this.LastMovePostion, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goboard.getLastTiziNum();
				this.LastTiziPosition = this.goboard.getLastTiziPosition();
				LastMovePostion = p;
				this.goboard.getMoves().push(newMove);

				// alpha-beta player turn, that is white
				Point pAI = ab.AlphaBetaSearch(this.color, this,
						this.alphaBetaDepth);
				Stone stAI = new Stone(this.color, pAI);
				ArrayList<Chain> removedChainsAI = this.goboard.addStone(stAI);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.color);
				// store for undo move
				Stone newstAI = (Stone) stAI.clone();
				Move newMoveAI = new Move(newstAI, removedChainsAI,
						this.LastMovePostion, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goboard.getLastTiziNum();
				this.LastTiziPosition = this.goboard.getLastTiziPosition();
				LastMovePostion = pAI;
				this.goboard.getMoves().push(newMoveAI);
			}
		} else if (this.mode.equals("Train")) {

			// Human Move first, that is Black
			if (!this.goboard.getIllegalMovesforBlack().contains(p)) {

				Point a = new Point(this.location.x, size - this.location.y - 1);
				Stone st = new Stone(this.color, a);
				ArrayList<Chain> removedChains = this.goboard.addStone(st);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.color);
				// store for undo move
				Stone newst = (Stone) st.clone();
				Move newMove = new Move(newst, removedChains,
						this.LastMovePostion, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goboard.getLastTiziNum();
				this.LastTiziPosition = this.goboard.getLastTiziPosition();
				LastMovePostion = p;
				this.goboard.getMoves().push(newMove);

				for (int i = 0; i < 100; i++) {

					Random random = new Random(System.currentTimeMillis());
					ArrayList<Point> legalmoves = this
							.findLegalMoves(this.color);

					if (legalmoves.size() <= 0) {
						this.togglePlayer(this.color);
						continue;
					} // end if

					int index = random.nextInt(legalmoves.size());
					Point pAI = legalmoves.get(index);
					Stone stAI = new Stone(this.color, pAI);
					ArrayList<Chain> removedChainsAI = this.goboard
							.addStone(stAI);
					this.justPassed = false;
					// change player color
					this.togglePlayer(this.color);
					// store for undo move
					Stone newstAI = (Stone) stAI.clone();
					Move newMoveAI = new Move(newstAI, removedChainsAI,
							this.LastMovePostion, false, this.LastTiziPosition,
							this.LastTiziNum);
					this.LastTiziNum = this.goboard.getLastTiziNum();
					this.LastTiziPosition = this.goboard.getLastTiziPosition();
					LastMovePostion = pAI;
					this.goboard.getMoves().push(newMoveAI);

				} // end for

				this.gameOver();
				this.newGame();

				this.placePiece(this.goboard, this.color);

			} // end if

		} // end if

		else if (this.mode.equals("RLvH") || this.mode.equals("HvRL")) {

		}

	}// end placePiece()

	/**
	 * We find all legalmoves for both players
	 * 
	 * @param color
	 * @return ArrayList<Point>
	 */
	private ArrayList<Point> findLegalMoves(int color) {

		int size = this.goboard.getSize();
		if (color == 0) {
			ArrayList<Point> illegalMovesBlack = this.goboard
					.getIllegalMovesforBlack();
			ArrayList<Point> legalMovesBlack = new ArrayList<Point>();

			for (int row = 0; row < size; row++) {
				for (int col = 0; col < size; col++) {
					Point p = new Point(col, row);
					if (!illegalMovesBlack.contains(p))
						legalMovesBlack.add(p);
				}
			}
			return legalMovesBlack;

		} else {

			ArrayList<Point> illegalMovesWhite = this.goboard
					.getIllegalMovesforWhite();
			ArrayList<Point> legalMovesWhite = new ArrayList<Point>();
			for (int row = 0; row < size; row++) {
				for (int col = 0; col < size; col++) {
					Point p = new Point(col, row);
					if (!illegalMovesWhite.contains(p)) {
						legalMovesWhite.add(p);
					}
				}
			}

			return legalMovesWhite;
		}

	} // end findLegalMoves()

	public void placePieceAlphaBeta(Board b, int c) {
		this.color = c;
		Point p = new Point(this.location.x, size - this.location.y - 1);
		this.goboard = b;

		if (c == 0) {
			if (!this.goboard.getIllegalMovesforBlack().contains(p)) {

				Point a = new Point(this.location.x, size - this.location.y - 1);
				Stone st = new Stone(this.color, a); // 0 is black 1 is
														// white
				ArrayList<Chain> removedChains = this.goboard.addStone(st);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.color);
				// store for undo move
				Stone newst = (Stone) st.clone();
				Move newMove = new Move(newst, removedChains,
						this.LastMovePostion, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goboard.getLastTiziNum();
				this.LastTiziPosition = this.goboard.getLastTiziPosition();
				LastMovePostion = p;
				this.goboard.getMoves().push(newMove);
			}
		}

		if (c == 1) {

			if (!this.goboard.getIllegalMovesforWhite().contains(p)) {

				Point a = new Point(this.location.x, size - this.location.y - 1);
				Stone st = new Stone(this.color, a); // 0 is black 1 is
														// white
				ArrayList<Chain> removedChains = this.goboard.addStone(st);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.color);
				// store for undo move
				Stone newst = (Stone) st.clone();
				Move newMove = new Move(newst, removedChains,
						this.LastMovePostion, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goboard.getLastTiziNum();
				this.LastTiziPosition = this.goboard.getLastTiziPosition();
				LastMovePostion = p;

				this.goboard.getMoves().push(newMove);
			}
		}
	}// end placePieceAlphaBeta

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
		this.LastTiziNum = 0;
		this.LastTiziPosition = null;
	} // end newGame()

	public void undoMove(Board b) {
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
				Chain[] lc = news.checkChains(b.getBoard());
				Wei[] lw = news.checkWeis(b.getBoard());

				// update this stone's chain, if the size is 1 which we want to
				// move
				// from the chains
				if (newc.size() == 1) {
					b.getChains().remove(news.getChain());
				}

				// Step 1: Remove the stone from the board
				b.getBoard()[location.x][location.y] = null;

				// Step 2: Update the surrounding Stones
				// If they have different colors
				ArrayList<Chain> chainList = new ArrayList<Chain>();
				for (int i = 0; i < 4; i++) {
					if (lc[i] != null && !lc[i].isEmpty()) {
						if ((lc[i].first().getColor() != -1)
								&& (lc[i].first().getColor() != s.getColor())
								&& !chainList.contains(lc[i])) {
							chainList.add(lc[i]);
						}
					}
				}

				for (int j = 0; j < chainList.size(); j++) {
					chainList.get(j).deeplyrecheckQis();
				}

				// If they have the same colors
				ArrayList<Wei> weiList = new ArrayList<Wei>();
				for (int i = 0; i < 8; i++) {
					if (lw[i] != null && !lw[i].isEmpty()) {
						if ((lw[i].first().getColor() != -1)
								&& (lw[i].first().getColor() == s.getColor())
								&& !weiList.contains(lw[i])) {
							weiList.add(lw[i]);
						}
					}
				}

				if (weiList.size() == 0) {
					b.getWeis().remove(lw[8]);
				} else {
					for (int j = 0; j < weiList.size(); j++) {
						weiList.get(j).updateWeis(s);
					}

				}

				// Step 3: Add the removed stones back
				ArrayList<Chain> removedChain = m.getRemovedChain();
				for (int i = 0; i < removedChain.size(); i++) {
					Chain c = removedChain.get(i);
					Iterator<Stone> it = c.iterator();
					while (it.hasNext()) {
						Stone next = it.next();
						b.addStone(next);
					}
				}// end for

				// Step 4: Update Yans
				Stone news1 = b.getStone(location);
				Chain[] lc1 = news1.checkChains(b.getBoard());

				Chain list = s.getChain();
				list.clear();
				list.setYan(true);
				TreeSet<Qi> qis = new TreeSet<Qi>();
				list.setQis(qis);
				Stone stone = new Stone();
				boolean control = false;

				ArrayList<Chain> yanList = new ArrayList<Chain>();
				for (int i = 0; i < 4; i++) {
					if (lc1[i] != null && !yanList.contains(lc1[i])) {
						yanList.add(lc1[i]);
					}
				}

				for (int j = 0; j < yanList.size(); j++) {
					if (yanList.get(j).first().getColor() != 0
							&& (yanList.get(j).first().getColor() != 1)) {
						Iterator<Stone> it = yanList.get(j).iterator();
						while (it.hasNext()) {
							Stone next = it.next();
							this.goboard.getChains().remove(next.getChain());
							next.setChain(list);
							list.add(next);
							stone = next;
						}
						control = true;
					}
				}

				if (control) {
					s.setColor(stone.getColor());
					s.setYan(true);
					s.setWei(null);
					s.setChain(stone.getChain());
					s.setBelongto(stone.getBelongto());
					s.setJiaYan(stone.isJiaYan());
					s.setZhenYan(stone.isZhenYan());
					list.add(s);
					b.getBoard()[location.x][location.y] = s;

					if (list.deeplyrecheckQis() > 0) {

						Iterator<Stone> it1 = list.iterator();
						while (it1.hasNext()) {
							Stone next = it1.next();
							Point location1 = next.getLocation();
							b.getBoard()[location1.x][location1.y] = null;

							this.goboard.getChains().remove(next.getChain());

						}

					} else if (list.deeplyrecheckQis() == 0) {
						this.goboard.getChains().add(list);
					}

				}
				// Step 5: Change the player color
				this.togglePlayer(this.color);

				// Step 6: Set the last Move icon
				Point lm = m.getlastMove();
				this.LastMovePostion = lm;

				// //Step 7: Make lastTiziPosition = Stones we just added back
				this.goboard.setLastTiziPosition(m.getlastTiziPosition());
				this.goboard.setLastTiziNum(m.getlastNumTizi());
				this.LastTiziPosition = m.getlastTiziPosition();
				this.LastTiziNum = m.getlastNumTizi();

			}// end if
			else {

				// I want to change the player color
				this.togglePlayer(this.color);
				this.justPassed = false;
				this.goboard.setLastTiziPosition(m.getlastTiziPosition());
				this.goboard.setLastTiziNum(m.getlastNumTizi());
				this.LastTiziPosition = m.getlastTiziPosition();
				this.LastTiziNum = m.getlastNumTizi();
			}// end else

		}// end else

		if (!this.goboard.getMoves().isEmpty()) {
			Move testm = this.goboard.getMoves().peek();
			boolean nextip = testm.getisPass();
			if (nextip) {
				this.justPassed = true;
			}
		}

		this.goboard.illegalMoveDetector();
	} // end undoMove()

	public void forfeit(int p) throws Exception {
		if (p == 0)
			System.out.println("Black Player Forfeits!");
		else
			System.out.println("White Player Forfeits!");
		this.gameOver();
	} // end forfeit()

	public void gameOver() throws Exception {

		String codes = this.goboard.getCodes();

		double scores[] = this.goboard.getScores();
		double value = scores[2]; // white - black
		String code = "11";
		if (value < 0) { // black wins
			code = "00";
		} else if (value == 0) { // tie game
			code = "01";
		} else if (value > 0) { // white wins
			code = "10";
		} // end if
		codes += " " + code;

		this.netTrain.addToTrainSet(codes);

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

	public Point getlastMovePosition() {
		return this.LastMovePostion;
	}

	public NetworkTrainer getNetTrain() {
		return netTrain;
	}

	public void setNetTrain(NetworkTrainer netTrain) {
		this.netTrain = netTrain;
	}

	public NeuralNetwork getNetwork() {
		return network;
	}

	public void setNetwork(NeuralNetwork network) {
		this.network = network;
	}

} // end class
