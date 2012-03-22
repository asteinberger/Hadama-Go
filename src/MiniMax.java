/**
 * @author Adam Steinberger
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class MiniMax {

	private int size;

	public MiniMax(int s) {
		this.size = s;
	} // end constructor

	/**
	 * @param game
	 * @return maximum value of all moves
	 */
	public double maxValue(GamePlay game) {
		double result = Double.NEGATIVE_INFINITY;
		if (game.isGameOver()) {
			double[] scores = game.getGoboard().getScores();
			result = scores[0];
		} else {
			for (int row = 0; row < this.size; row++) {
				for (int col = 0; col < this.size; col++) {
					if (game.validMove(row, col, 0)) {
						game.placePiece(game.getGoboard(), 0);
						double value = minValue(game);
						game.undoMove(row, col);
						if (value > result) {
							result = value;
						} // end if
					} // end if
				} // end for
			} // end for
		} // end if
		return result;
	} // end maxValue()

	/**
	 * @param game
	 * @return minimum value of all moves
	 */
	public double minValue(GamePlay game) {
		double result = Double.POSITIVE_INFINITY;
		if (game.isGameOver()) {
			double[] scores = game.getGoboard().getScores();
			result = scores[1];
		} else {
			for (int row = 0; row < this.size; row++) {
				for (int col = 0; col < this.size; col++) {
					if (game.validMove(row, col)) {
						game.makeMove(row, col);
						double value = maxValue(game);
						game.undoMove(row, col);
						if (value < result) {
							result = value;
						} // end if
					} // end if
				} // end for
			} // end for
		} // end if
		return result;
	} // end minValue()

	/**
	 * make the best move for min player
	 */
	public void makeBestMoveForMin(GamePlay game) {
		double result = Double.POSITIVE_INFINITY;
		int bestRow = 0;
		int bestCol = 0;
		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				if (game.validMove(row, col)) {
					game.makeMove(row, col);
					double value = maxValue(game);
					game.undoMove(row, col);
					if (value < result) {
						result = value;
						bestRow = row;
						bestCol = col;
					} // end if
				} // end if
			} // end for
		} // end for
		game.makeMove(bestRow, bestCol);
	} // end makeBestMoveForMin()

	/**
	 * make the best move for max player
	 */
	public void makeBestMoveForMax(GamePlay game) {
		double result = Double.NEGATIVE_INFINITY;
		int bestRow = 0;
		int bestCol = 0;
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (game.validMove(row, col)) {
					game.makeMove(row, col);
					double value = minValue(game);
					game.undoMove(row, col);
					if (value > result) {
						result = value;
						bestRow = row;
						bestCol = col;
					} // end if
				} // end if
			} // end for
		} // end for
		game.makeMove(bestRow, bestCol);
	} // end makeBestMoveForMin()

} // end MiniMax class
