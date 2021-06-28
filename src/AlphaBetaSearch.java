import java.awt.Point;
import java.util.ArrayList;

public class AlphaBetaSearch {

	/**
	 * We find all legalmoves for both players
	 * 
	 * @param player
	 * @param gamePlay
	 * @return ArrayList<Point>
	 */
	private ArrayList<Point> findLegalMoves(Player player, GamePlay gamePlay) {
		int size = gamePlay.getGoBoard().getBoardSize();
		if (player == Player.BLACK) {
			ArrayList<Point> blackPlayersIllegalMoves = gamePlay.getGoBoard()
					.getIllegalMovesForBlack();
			return getPlayersLegalMoves(size, blackPlayersIllegalMoves);
		} else {
			ArrayList<Point> whitePlayersIllegalMoves = gamePlay.getGoBoard()
					.getIllegalMovesForWhite();
			return getPlayersLegalMoves(size, whitePlayersIllegalMoves);
		}
	} // end findLegalMoves()

	 private static ArrayList<Point> getPlayersLegalMoves(int boardSize, ArrayList<Point> playersIllegalMoves) {
		ArrayList<Point> playersLegalMoves = new ArrayList<Point>();
		for (int row = 0; row < boardSize; row++) {
			for (int column = 0; column < boardSize; column++) {
				Point point = new Point(column, row);
				if (!playersIllegalMoves.contains(point)) {
					playersLegalMoves.add(point);
				}
			}
		}
		return playersLegalMoves;
	}

	/**
	 * It is alpha-beta player's turn
	 * 
	 * @param gamePlay
	 * @param alpha
	 * @param beta
	 * @param depthLimit
	 * @param depth
	 * @return
	 * @throws Exception
	 */
	public Pair<Double, Point> maximumValue(GamePlay gamePlay, double alpha,
											double beta, int depthLimit, int depth) throws Exception {
		Point bestPoint = new Point();

		if (depthLimit <= depth) {
			double[] scores = gamePlay.getGoBoard().getScores();
			Pair<Double, Point> finalPoint = new Pair<Double, Point>(scores[2],
					bestPoint);
			return finalPoint;

		} else {

			ArrayList<Point> actions = findLegalMoves(gamePlay.getPlayer(), gamePlay);

			for (int actionIndex = 0; actionIndex < actions.size(); actionIndex++) {

				Point point = new Point(actions.get(actionIndex).x, gamePlay.getGoBoard()
						.getBoardSize() - actions.get(actionIndex).y - 1);

				gamePlay.setCurrLocation(point);

				// place stone on the board
				gamePlay.placePieceOnTheBoardUsingAlphaBetaSearch(gamePlay.getGoBoard(), gamePlay.getPlayer());
				Pair<Double, Point> pointForMinimumValue = minimumValue(gamePlay, alpha, beta,
						depthLimit, depth + 1);

				gamePlay.undoMove(gamePlay.getGoBoard());

				if (pointForMinimumValue.getLeft() > alpha) {
					alpha = pointForMinimumValue.getLeft();
					bestPoint = actions.get(actionIndex);

					if (alpha >= beta) {
						Pair<Double, Point> finalPoint = new Pair<Double, Point>(
								alpha, bestPoint);
						return finalPoint;
					}
				}

			} // end for

			Pair<Double, Point> finalPoint = new Pair<Double, Point>(alpha, bestPoint);
			return finalPoint;

		} // end if

	} // end maxValue()

	public Pair<Double, Point> minimumValue(GamePlay gamePlay, double alpha,
											double beta, int depthLimit, int depth) throws Exception {

		// System.out.println("we called minValue");

		Point bestPoint = new Point();
		if (depthLimit <= depth) {

			double[] scores = gamePlay.getGoBoard().getScores();
			Pair<Double, Point> finalPoint = new Pair<Double, Point>(scores[2],
					bestPoint);
			return finalPoint;
		} else {
			ArrayList<Point> actions = findLegalMoves(gamePlay.getPlayer(), gamePlay);

			for (int actionIndex = 0; actionIndex < actions.size(); actionIndex++) {

				Point point = new Point(actions.get(actionIndex).x, gamePlay.getGoBoard()
						.getBoardSize() - actions.get(actionIndex).y - 1);
				gamePlay.setCurrLocation(point);
				gamePlay.placePieceOnTheBoardUsingAlphaBetaSearch(gamePlay.getGoBoard(), gamePlay.getPlayer());

				Pair<Double, Point> pointForMaximumValue = this.maximumValue(gamePlay, alpha,
						beta, depthLimit, depth + 1);

				gamePlay.undoMove(gamePlay.getGoBoard());

				if (pointForMaximumValue.getLeft() < beta) {

					beta = pointForMaximumValue.getLeft();
					bestPoint = pointForMaximumValue.getRight();
					if (alpha >= beta) {
						Pair<Double, Point> finalPoint = new Pair<Double, Point>(
								beta, bestPoint);
						return finalPoint;
					}
				}

			} // end for
			Pair<Double, Point> finalPoint = new Pair<Double, Point>(beta, bestPoint);
			return finalPoint;

		} // end if

	} // end maxValue()

	public Point Search(Player player, GamePlay gamePlay, int depthLimit)
			throws Exception {

		// min's turn black
		Pair<Double, Point> tuple;
		if (player == Player.BLACK) {
			tuple = minimumValue(gamePlay,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
					depthLimit, 0);
		}

		// max's turn White
		else {
			tuple = maximumValue(gamePlay,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
					depthLimit, 0);
		}
		return tuple.getRight();

	} // end AlphaBetaSearch()

} // end AlphaBeta class

