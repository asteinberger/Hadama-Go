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
 *         <adam@akmaz.io>
 * 
 */
public class GamePlay {

	private Point location;
	private Point point;
	private Point intersection;
	private int[] horizontals;
	private int[] verticals;
	private GoBoard goBoard;
	private Player player;
	private boolean gameOver = false;
	private boolean justPassed = false;
	public GameMode gameMode;
	private int boardSize;
	private Point LastMovePosition;
	private Point LastTiziPosition;
	private int LastTiziNum;
	private int alphaBetaDepth = 2;
	private AlphaBetaSearch alphaBetaSearch = new AlphaBetaSearch();
	private QLearning qLearning = new QLearning(0.001, 0.01, 0.1);
	private NeuralNetworkTrainer neuralNetworkTrainer = new NeuralNetworkTrainer(
			162,
			40,
			2,
			0.0001,
			0.9f,
			0.7f,
			50000,
			0.0001,
			2500);
	private NeuralNetwork neuralNetwork;

	public GamePlay(GameMode gameMode, int boardSize) throws Exception {
		this.player = Player.NOT_A_PLAYER;
		this.gameMode = gameMode;
		this.boardSize = boardSize;
		this.location = new Point(0, this.boardSize - 1);
		this.point = new Point(0, 0);
		this.intersection = new Point(0, 0);
		this.horizontals = new int[this.boardSize];
		this.verticals = new int[this.boardSize];
		this.goBoard = new GoBoard(this.boardSize);
		this.neuralNetwork = new NeuralNetwork(162, 40, 2);
	} // end constructor

	/**
	 * Create new GamePlay object given full parameter set for object variables.
	 * 
	 * @param gameMode
	 *            game mode
	 * @param boardSize
	 *            size of GoBoard
	 * @param location
	 *            location
	 * @param point
	 *            point
	 * @param intersection
	 *            intersection
	 * @param verticals
	 *            verticals
	 * @param horizontals
	 *            horizontals
	 * @param goBoard
	 *            goBoard
	 * @param player
	 *            color
	 * @param goBoard
	 *            game
	 * 
	 * @throws Exception
	 */
	public GamePlay(GameMode gameMode, int boardSize, Point location, Point point, Point intersection, int[] horizontals,
					int[] verticals, GoBoard goBoard, Player player) throws Exception {
		this.gameMode = gameMode;
		this.boardSize = boardSize;
		this.location = location;
		this.point = point;
		this.intersection = intersection;
		this.horizontals = horizontals;
		this.verticals = verticals;
		this.goBoard = goBoard;
		this.player = player;
		this.neuralNetwork = new NeuralNetwork(162, 40, 2);
	} // end constructor

	// c = player stone color
	public void placePiece(GoBoard goBoard, Player player) throws Exception {

		this.player = player;
		Point point = new Point(this.location.x, boardSize - this.location.y - 1);
		this.goBoard = goBoard;

		ArrayList<Point> legalMoves = this.qLearning.findLegalMoves(this.player, this);
		int numberOfAvailableMoves = legalMoves.size();
		double[] scores = this.goBoard.getScores();

		if (((player == Player.BLACK) && (scores[2] < 0) && (numberOfAvailableMoves <= 15))
				|| ((player == Player.WHITE) && (scores[2] > 0) && (numberOfAvailableMoves <= 15))) {
			this.forfeit(this.player);
			if (this.gameMode == GameMode.TRAIN_THE_NETWORK) {
				this.newGame();
				this.placePiece(this.goBoard, this.player);
			} // end if
		} // end if

		if (this.gameMode == GameMode.HUMAN_VS_HUMAN) {
			if (player == Player.BLACK) {
				if (!this.goBoard.getIllegalMovesForBlack().contains(point)) {

					Point newPoint = new Point(this.location.x, boardSize - this.location.y
							- 1);
					Stone newStone = new Stone(this.player, newPoint); // 0 is black 1 is
															// white
					ArrayList<Chain> removedChains = this.goBoard.addStone(newStone);
					this.justPassed = false;
					// change player color
					this.togglePlayer(this.player);
					// store for undo move
					Stone newStoneClone = (Stone) newStone.clone();
					Move newMove = new Move(newStoneClone, removedChains,
							this.LastMovePosition, false, this.LastTiziPosition,
							this.LastTiziNum);
					this.LastTiziNum = this.goBoard.getLastTiziNumber();
					this.LastTiziPosition = this.goBoard.getLastTiziPosition();
					LastMovePosition = point;
					this.goBoard.getMoves().push(newMove);
				}
			}

			if (player == Player.WHITE) {

				if (!this.goBoard.getIllegalMovesForWhite().contains(point)) {

					Point newPoint = new Point(this.location.x, boardSize - this.location.y
							- 1);
					Stone newStone = new Stone(this.player, newPoint); // 0 is black 1 is
															// white
					ArrayList<Chain> removedChains = this.goBoard.addStone(newStone);
					this.justPassed = false;
					// change player color
					this.togglePlayer(this.player);
					// store for undo move
					Stone newStoneClone = (Stone) newStone.clone();
					Move newMove = new Move(newStoneClone, removedChains,
							this.LastMovePosition, false, this.LastTiziPosition,
							this.LastTiziNum);
					this.LastTiziNum = this.goBoard.getLastTiziNumber();
					this.LastTiziPosition = this.goBoard.getLastTiziPosition();
					LastMovePosition = point;

					this.goBoard.getMoves().push(newMove);
				}
			}
		} // end mode HvH

		else if (this.gameMode == GameMode.HUMAN_VS_COMPUTER) {

			// Human Move first, that is Black
			if (!this.goBoard.getIllegalMovesForBlack().contains(point)) {

				Point newPoint = new Point(this.location.x, boardSize - this.location.y - 1);
				Stone newStone = new Stone(this.player, newPoint);
				ArrayList<Chain> removedChains = this.goBoard.addStone(newStone);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.player);
				// store for undo move
				Stone newStoneClone = (Stone) newStone.clone();
				Move newMove = new Move(newStoneClone, removedChains,
						this.LastMovePosition, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goBoard.getLastTiziNumber();
				this.LastTiziPosition = this.goBoard.getLastTiziPosition();
				LastMovePosition = point;
				this.goBoard.getMoves().push(newMove);

				// alpha-beta player turn, that is white
				Point alphaBetaSearchResult = this.alphaBetaSearch.Search(this.player, this,
						this.alphaBetaDepth);
				Stone alphaBetaSearchResultStone = new Stone(this.player, alphaBetaSearchResult);
				ArrayList<Chain> alphaBetaSearchRemovedChains = this.goBoard.addStone(alphaBetaSearchResultStone);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.player);
				// store for undo move
				Stone newAlphaBetaSearchResultStone = (Stone) alphaBetaSearchResultStone.clone();
				Move newAlphaBetaSearchResultMove = new Move(newAlphaBetaSearchResultStone, alphaBetaSearchRemovedChains,
						this.LastMovePosition, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goBoard.getLastTiziNumber();
				this.LastTiziPosition = this.goBoard.getLastTiziPosition();
				LastMovePosition = alphaBetaSearchResult;
				this.goBoard.getMoves().push(newAlphaBetaSearchResultMove);
			}
		} else if (this.gameMode == GameMode.TRAIN_THE_NETWORK) {

			// Human Move first, that is Black
			if (!this.goBoard.getIllegalMovesForBlack().contains(point)) {

				Point newPoint = new Point(this.location.x, boardSize - this.location.y - 1);
				Stone newStone = new Stone(this.player, newPoint);
				ArrayList<Chain> removedChains = this.goBoard.addStone(newStone);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.player);
				// store for undo move
				Stone newStoneClone = (Stone) newStone.clone();
				Move newMove = new Move(newStoneClone, removedChains,
						this.LastMovePosition, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goBoard.getLastTiziNumber();
				this.LastTiziPosition = this.goBoard.getLastTiziPosition();
				LastMovePosition = point;
				this.goBoard.getMoves().push(newMove);

				this.addTrainingData();

				for (int index = 0; index < 100; index++) {

					Random random = new Random(System.currentTimeMillis());
					ArrayList<Point> currentLegalMoves = this
							.findLegalMoves(this.player);

					if (currentLegalMoves.size() <= 0) {
						this.togglePlayer(this.player);
						continue;
					} // end if

					int randomInteger = random.nextInt(currentLegalMoves.size());
					Point aiPoint = currentLegalMoves.get(randomInteger);
					Stone aiStone = new Stone(this.player, aiPoint);
					ArrayList<Chain> aiRemovedChains = this.goBoard
							.addStone(aiStone);
					this.justPassed = false;
					// change player color
					this.togglePlayer(this.player);
					// store for undo move
					Stone newAIStoneClone = (Stone) aiStone.clone();
					Move newMoveAI = new Move(newAIStoneClone, aiRemovedChains,
							this.LastMovePosition, false, this.LastTiziPosition,
							this.LastTiziNum);
					this.LastTiziNum = this.goBoard.getLastTiziNumber();
					this.LastTiziPosition = this.goBoard.getLastTiziPosition();
					LastMovePosition = aiPoint;
					this.goBoard.getMoves().push(newMoveAI);

					this.addTrainingData();

				} // end for

				this.gameOver();
				this.newGame();

				this.placePiece(this.goBoard, this.player);

			} // end if

		} // end if

		else if (this.gameMode == GameMode.Q_LEARNING_VS_HUMAN ||
				this.gameMode == GameMode.HUMAN_VS_Q_LEARNING) {

			// Human Move first, that is Black
			if (!this.goBoard.getIllegalMovesForBlack().contains(point)) {

				Point newPoint = new Point(this.location.x, boardSize - this.location.y - 1);
				Stone newStone = new Stone(this.player, newPoint);
				ArrayList<Chain> removedChains = this.goBoard.addStone(newStone);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.player);
				// store for undo move
				Stone newStoneClone = (Stone) newStone.clone();
				Move newMove = new Move(newStoneClone, removedChains,
						this.LastMovePosition, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goBoard.getLastTiziNumber();
				this.LastTiziPosition = this.goBoard.getLastTiziPosition();
				LastMovePosition = point;
				this.goBoard.getMoves().push(newMove);

				this.addTrainingData();

				// alpha-beta player turn, that is white
				Point qLearningResultPoint = this.qLearning.iterateValue(this.player, this);
				if ((qLearningResultPoint.x != Integer.MAX_VALUE)
						&& (qLearningResultPoint.y != Integer.MAX_VALUE)) {
					Stone qLearningResultStone = new Stone(this.player, qLearningResultPoint);
					ArrayList<Chain> qLearningRemovedChains = this.goBoard
							.addStone(qLearningResultStone);
					this.justPassed = false;
					// change player color
					this.togglePlayer(this.player);
					// store for undo move
					Stone qLearningResultStoneClone = (Stone) qLearningResultStone.clone();
					Move newMoveAI = new Move(qLearningResultStoneClone, qLearningRemovedChains,
							this.LastMovePosition, false, this.LastTiziPosition,
							this.LastTiziNum);
					this.LastTiziNum = this.goBoard.getLastTiziNumber();
					this.LastTiziPosition = this.goBoard.getLastTiziPosition();
					LastMovePosition = qLearningResultPoint;
					this.goBoard.getMoves().push(newMoveAI);
					this.addTrainingData();
				} // end if

			}
		}

	}// end placePiece()

	/**
	 * We find all legalmoves for both players
	 * 
	 * @param player
	 * @return ArrayList<Point>
	 */
	private ArrayList<Point> findLegalMoves(Player player) {

		int size = this.goBoard.getBoardSize();
		if (player == Player.BLACK) {
			ArrayList<Point> illegalMovesForBlack = this.goBoard
					.getIllegalMovesForBlack();
			ArrayList<Point> legalMovesForBlack = new ArrayList<Point>();

			for (int row = 0; row < size; row++) {
				for (int column = 0; column < size; column++) {
					Point point = new Point(column, row);
					if (!illegalMovesForBlack.contains(point))
						legalMovesForBlack.add(point);
				}
			}
			return legalMovesForBlack;

		} else {

			ArrayList<Point> illegalMovesForWhite = this.goBoard
					.getIllegalMovesForWhite();
			ArrayList<Point> legalMovesForWhite = new ArrayList<Point>();
			for (int row = 0; row < size; row++) {
				for (int column = 0; column < size; column++) {
					Point point = new Point(column, row);
					if (!illegalMovesForWhite.contains(point)) {
						legalMovesForWhite.add(point);
					}
				}
			}

			return legalMovesForWhite;
		}

	} // end findLegalMoves()

	public void placePieceOnTheBoardUsingAlphaBetaSearch(GoBoard goBoard, Player player) throws Exception {
		this.player = player;
		Point point = new Point(this.location.x, boardSize - this.location.y - 1);
		this.goBoard = goBoard;

		ArrayList<Point> legalMoves = this.qLearning.findLegalMoves(this.player, this);
		int numberOfAvailableMoves = legalMoves.size();
		double[] scores = this.goBoard.getScores();

		if (((player == Player.BLACK) && (scores[2] < 0) && (numberOfAvailableMoves <= 15))
				|| ((player == Player.WHITE) && (scores[2] > 0) && (numberOfAvailableMoves <= 15))) {
			this.forfeit(this.player);
			if (this.gameMode.equals("Train")) {
				this.newGame();
				this.placePiece(this.goBoard, this.player);
			} // end if
		} // end if

		if (player == Player.BLACK) {
			if (!this.goBoard.getIllegalMovesForBlack().contains(point)) {

				Point newPoint = new Point(this.location.x, boardSize - this.location.y - 1);
				Stone newStone = new Stone(this.player, newPoint); // 0 is black 1 is
														// white
				ArrayList<Chain> removedChains = this.goBoard.addStone(newStone);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.player);
				// store for undo move
				Stone newStoneClone = (Stone) newStone.clone();
				Move newMove = new Move(newStoneClone, removedChains,
						this.LastMovePosition, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goBoard.getLastTiziNumber();
				this.LastTiziPosition = this.goBoard.getLastTiziPosition();
				LastMovePosition = point;
				this.goBoard.getMoves().push(newMove);
			}
		}

		if (player == Player.WHITE) {

			if (!this.goBoard.getIllegalMovesForWhite().contains(point)) {

				Point newPoint = new Point(this.location.x, boardSize - this.location.y - 1);
				Stone newStone = new Stone(this.player, newPoint); // 0 is black 1 is
														// white
				ArrayList<Chain> removedChains = this.goBoard.addStone(newStone);
				this.justPassed = false;
				// change player color
				this.togglePlayer(this.player);
				// store for undo move
				Stone newStoneClone = (Stone) newStone.clone();
				Move newMove = new Move(newStoneClone, removedChains,
						this.LastMovePosition, false, this.LastTiziPosition,
						this.LastTiziNum);
				this.LastTiziNum = this.goBoard.getLastTiziNumber();
				this.LastTiziPosition = this.goBoard.getLastTiziPosition();
				LastMovePosition = point;

				this.goBoard.getMoves().push(newMove);
			}
		}
	}// end placePieceAlphaBeta

	protected void togglePlayer(Player player) {
		if (player == Player.BLACK) {
			this.player = Player.WHITE;
		} else {
			this.player = Player.BLACK;
		} // end if
	} // end togglePlayer()

	// c = opponent stone color

	public void newGame() {
		this.goBoard.setBoard(new Stone[this.boardSize][this.boardSize]);
		this.goBoard.setChains(new ArrayList<Chain>());
		this.goBoard.setMoves(new Stack<Move>());
		this.goBoard.setWeis(new ArrayList<Wei>());
		this.goBoard.setIllegalMovesForBlack(new ArrayList<Point>());
		this.goBoard.setIllegalMovesForWhite(new ArrayList<Point>());
		this.LastMovePosition = null;
		this.LastTiziNum = 0;
		this.LastTiziPosition = null;
	} // end newGame()

	public void undoMove(GoBoard goBoard) {
		if (goBoard.getMoves().size() == 0) {
			System.out.println("No move has been made!");
		} else {
			Move move = goBoard.getMoves().pop();
			boolean thisMoveIsAPass = move.getThisMoveIsAPass();
			Stone stone = move.getAddedStone();
			if (!thisMoveIsAPass) {
				Point location = stone.getLocation();
				Stone newStone = goBoard.getStone(location);
				Chain newChain = newStone.getChain();
				Chain[] checkChains = newStone.checkChains(goBoard.getBoard());
				Wei[] checkWeis = newStone.checkWeis(goBoard.getBoard());

				// update this stone's chain, if the size is 1 which we want to
				// move
				// from the chains
				if (newChain.size() == 1) {
					goBoard.getChains().remove(newStone.getChain());
				}

				// Step 1: Remove the stone from the board
				goBoard.getBoard()[location.x][location.y] = null;

				// Step 2: Update the surrounding Stones
				// If they have different colors
				ArrayList<Chain> chainList = new ArrayList<Chain>();
				for (int index = 0; index < 4; index++) {
					if (checkChains[index] != null && !checkChains[index].isEmpty()) {
						if ((checkChains[index].first().getPlayer() != Player.NOT_A_PLAYER)
								&& (checkChains[index].first().getPlayer() != stone.getPlayer())
								&& !chainList.contains(checkChains[index])) {
							chainList.add(checkChains[index]);
						}
					}
				}

				for (int index = 0; index < chainList.size(); index++) {
					chainList.get(index).deeplyRecheckQis();
				}

				// If they have the same colors
				ArrayList<Wei> weiList = new ArrayList<Wei>();
				for (int index = 0; index < 8; index++) {
					if (checkWeis[index] != null && !checkWeis[index].isEmpty()) {
						if ((checkWeis[index].first().getPlayer() != Player.NOT_A_PLAYER)
								&& (checkWeis[index].first().getPlayer() == stone.getPlayer())
								&& !weiList.contains(checkWeis[index])) {
							weiList.add(checkWeis[index]);
						}
					}
				}

				if (weiList.size() == 0) {
					goBoard.getWeis().remove(checkWeis[8]);
				} else {
					for (int index = 0; index < weiList.size(); index++) {
						weiList.get(index).updateWeis(stone);
					}

				}

				// Step 3: Add the removed stones back
				ArrayList<Chain> removedChain = move.getRemovedChains();
				for (int index = 0; index < removedChain.size(); index++) {
					Chain chain = removedChain.get(index);
					Iterator<Stone> iterator = chain.iterator();
					while (iterator.hasNext()) {
						Stone next = iterator.next();
						goBoard.addStone(next);
					}
				}// end for

				// Step 4: Update Yans
				Stone anotherNewStone = goBoard.getStone(location);
				Chain[] checkMoreChains = anotherNewStone.checkChains(goBoard.getBoard());

				Chain list = stone.getChain();
				list.clear();
				list.setYan(true);
				TreeSet<Qi> qis = new TreeSet<Qi>();
				list.setQis(qis);
				boolean control = false;

				ArrayList<Chain> yanList = new ArrayList<Chain>();
				for (int index = 0; index < 4; index++) {
					if (checkMoreChains[index] != null && !yanList.contains(checkMoreChains[index])) {
						yanList.add(checkMoreChains[index]);
					}
				}

				for (int index = 0; index < yanList.size(); index++) {
					if (yanList.get(index).first().getPlayer() != Player.BLACK
							&& (yanList.get(index).first().getPlayer() != Player.WHITE)) {
						Iterator<Stone> iterator = yanList.get(index).iterator();
						while (iterator.hasNext()) {
							Stone next = iterator.next();
							this.goBoard.getChains().remove(next.getChain());
							next.setChain(list);
							list.add(next);
							stone = next;
						}
						control = true;
					}
				}

				if (control) {
					stone.setPlayer(stone.getPlayer());
					stone.setYan(true);
					stone.setWei(null);
					stone.setChain(stone.getChain());
					stone.setBelongsTo(stone.getBelongsTo());
					stone.setJiaYan(stone.isJiaYan());
					stone.setZhenYan(stone.isZhenYan());
					list.add(stone);
					goBoard.getBoard()[location.x][location.y] = stone;

					if (list.deeplyRecheckQis() > 0) {

						Iterator<Stone> iterator = list.iterator();
						while (iterator.hasNext()) {
							Stone next = iterator.next();
							Point point = next.getLocation();
							goBoard.getBoard()[point.x][point.y] = null;

							this.goBoard.getChains().remove(next.getChain());

						}

					} else if (list.deeplyRecheckQis() == 0) {
						this.goBoard.getChains().add(list);
					}

				}
				// Step 5: Change the player color
				this.togglePlayer(this.player);

				// Step 6: Set the last Move icon
				Point lastMove = move.getLastMove();
				this.LastMovePosition = lastMove;

				// //Step 7: Make lastTiziPosition = Stones we just added back
				this.goBoard.setLastTiziPosition(move.getlastTiziPosition());
				this.goBoard.setLastTiziNumber(move.getlastTiziNumber());
				this.LastTiziPosition = move.getlastTiziPosition();
				this.LastTiziNum = move.getlastTiziNumber();

			}// end if
			else {

				// I want to change the player color
				this.togglePlayer(this.player);
				this.justPassed = false;
				this.goBoard.setLastTiziPosition(move.getlastTiziPosition());
				this.goBoard.setLastTiziNumber(move.getlastTiziNumber());
				this.LastTiziPosition = move.getlastTiziPosition();
				this.LastTiziNum = move.getlastTiziNumber();
			}// end else

		}// end else

		if (!this.goBoard.getMoves().isEmpty()) {
			Move nextMove = this.goBoard.getMoves().peek();
			boolean isTheNextMoveAPass = nextMove.getThisMoveIsAPass();
			if (isTheNextMoveAPass) {
				this.justPassed = true;
			}
		}

		this.goBoard.illegalMoveDetector();
	} // end undoMove()

	public void forfeit(Player player) throws Exception {
		if (player == Player.BLACK)
			System.out.println("Black Player Forfeits!");
		else
			System.out.println("White Player Forfeits!");
		this.gameOver();
	} // end forfeit()

	public void addTrainingData() throws Exception {

		String codes = this.goBoard.getCodes();

		double scores[] = this.goBoard.getScores();
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

		this.neuralNetworkTrainer.addTrainingDataToTrainingSet(codes);

	} // end

	public void gameOver() throws Exception {

		System.out.println("Game Over!");
		this.addTrainingData();

	} // end forfeit()

	// getters and setters
	public void setCurrLocation(Point currLocation) {
		this.location = currLocation;
	} // end setCurrLocation()

	public Point getPoint() {
		return this.point;
	} // end getPoint()

	public void setPoint(Point point) {
		this.point = point;
	} // end setPoint()

	public void setIntersection(Point intersect) {
		this.intersection = intersect;
	} // end setIntersection()

	public GoBoard getGoBoard() {
		return this.goBoard;
	} // end getGoboard()

	public Player getPlayer() {
		return this.player;
	} // end getPlayer()

	public void setPlayer(Player player) {
		this.player = player;
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
		return this.LastMovePosition;
	}
} // end class
