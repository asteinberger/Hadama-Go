import java.awt.Point;
import java.util.ArrayList;

public class AlphaBeta {

	/**
	 * We find all legalmoves for both players
	 * 
	 * @param player
	 * @param game
	 * @return ArrayList<Point>
	 */
	private ArrayList<Point> findLegalMoves(int player, GamePlay game) {

		int size = game.getGoboard().getSize();
		if (player == 0) {
			ArrayList<Point> illegalMovesBlack = game.getGoboard()
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

			ArrayList<Point> illegalMovesWhite = game.getGoboard()
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

	/**
	 * It is alpha-beta player's turn
	 * 
	 * @param game
	 * @param alpha
	 * @param beta
	 * @param depthLimit
	 * @param depth
	 * @return
	 */
	public Pair<Double, Point> maxValue(GamePlay game, double alpha,
			double beta, int depthLimit, int depth) {
		// System.out.println("we called maxValue");
		Point pBest = new Point();

		if (depthLimit <= depth) {
			double[] scores = game.getGoboard().getScores();
			Pair<Double, Point> pFinal = new Pair<Double, Point>(scores[2],
					pBest);
			return pFinal;

		} else {

			ArrayList<Point> actions = findLegalMoves(game.getPlayer(), game);

			for (int i = 0; i < actions.size(); i++) {

				Point p = new Point(actions.get(i).x, game.getGoboard()
						.getSize() - actions.get(i).y - 1);

				game.setCurrLocation(p);
				// place stone on the board
				game.placePieceAlphaBeta(game.getGoboard(), game.getPlayer());
				Pair<Double, Point> pForminValue = minValue(game, alpha, beta,
						depthLimit, depth + 1);

				game.undoMove(game.getGoboard());

				if (pForminValue.getLeft() > alpha) {
					alpha = pForminValue.getLeft();
					pBest = actions.get(i);

					if (alpha >= beta) {
						Pair<Double, Point> pFinal = new Pair<Double, Point>(
								alpha, pBest);
						return pFinal;
					}
				}

			} // end for

			Pair<Double, Point> pFinal = new Pair<Double, Point>(alpha, pBest);
			return pFinal;

		} // end if

	} // end maxValue()

	public Pair<Double, Point> minValue(GamePlay game, double alpha,
			double beta, int depthLimit, int depth) {

		// System.out.println("we called minValue");

		Point pBest = new Point();
		if (depthLimit <= depth) {

			double[] scores = game.getGoboard().getScores();
			Pair<Double, Point> pFinal = new Pair<Double, Point>(scores[2],
					pBest);
			return pFinal;
		} else {
			ArrayList<Point> actions = findLegalMoves(game.getPlayer(), game);

			for (int i = 0; i < actions.size(); i++) {

				Point p = new Point(actions.get(i).x, game.getGoboard()
						.getSize() - actions.get(i).y - 1);
				game.setCurrLocation(p);
				game.placePieceAlphaBeta(game.getGoboard(), game.getPlayer());

				Pair<Double, Point> pForminValue = this.maxValue(game, alpha,
						beta, depthLimit, depth + 1);

				game.undoMove(game.getGoboard());

				if (pForminValue.getLeft() < beta) {

					beta = pForminValue.getLeft();
					pBest = pForminValue.getRight();
					if (alpha >= beta) {

						Pair<Double, Point> pFinal = new Pair<Double, Point>(
								beta, pBest);
						return pFinal;
					}
				}

			} // end for
			Pair<Double, Point> pFinal = new Pair<Double, Point>(beta, pBest);
			return pFinal;

		} // end if

	} // end maxValue()

	public Point AlphaBetaSearch(int player, GamePlay game, int depthLimit) {

		// min's turn black
		if (player == 0) {

			Pair<Double, Point> tuple = minValue(game,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
					depthLimit, 0);
			return tuple.getRight();
		}

		// max's turn White
		else {
			Pair<Double, Point> tuple = maxValue(game,
					Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
					depthLimit, 0);
			return tuple.getRight();
		}

	} // end AlphaBetaSearch()

} // end AlphaBeta class

