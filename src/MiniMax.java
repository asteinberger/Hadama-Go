import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Adam Steinberger
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class MiniMax {

	private static final int waitTime = 100;
	private ArrayList<Point> legalMovesBlack = new ArrayList<Point>();
	private ArrayList<Point> legalMovesWhite = new ArrayList<Point>();
	private int size;

	public MiniMax(int s) {
		this.size = s;
	} // end constructor

	private void findLegalMoves(GamePlay game) {

		ArrayList<Point> illegalMovesBlack = game.getGoboard()
				.getIllegalMovesforBlack();
		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				Point p = new Point(row, col);
				if (!illegalMovesBlack.contains(p)
						&& !this.legalMovesBlack.contains(p)) {
					this.legalMovesBlack.add(p);
				} else if (illegalMovesBlack.contains(p)
						&& this.legalMovesBlack.contains(p)) {
					this.legalMovesBlack.remove(p);
				}
			}
		}

		ArrayList<Point> illegalMovesWhite = game.getGoboard()
				.getIllegalMovesforWhite();
		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				Point p = new Point(row, col);
				if (!illegalMovesWhite.contains(p)
						&& !this.legalMovesWhite.contains(p)) {
					this.legalMovesWhite.add(p);
				} else if (illegalMovesWhite.contains(p)
						&& this.legalMovesWhite.contains(p)) {
					this.legalMovesWhite.remove(p);
				}
			}
		}

	} // end findLegalMoves()

	/**
	 * @param game
	 * @return maximum value of all moves
	 * @throws CloneNotSupportedException
	 * @throws InterruptedException
	 */
	public double maxValue(GamePlay game, double alpha, double beta)
			throws CloneNotSupportedException, InterruptedException {

		double result = Double.NEGATIVE_INFINITY;
		if (game.isGameOver()) {
			double[] scores = game.getGoboard().getScores();
			result = scores[0];
		} else {

			game.setLearning(true);

			this.findLegalMoves(game);

			for (int i = 0; i < this.legalMovesBlack.size(); i++) {

				Point p = this.legalMovesBlack.get(i);

				System.out.println("==============");
				System.out.println("MaxValue @ " + Integer.toString(p.x) + ", "
						+ Integer.toString(p.y));

				Stone s = game.getGoboard().getStone(p);
				game.getGoboard().addStone(new Stone(0, p));
				game.getGoboard().printBoard();

				HadamaGo.getGoPanel()
						.paint(HadamaGo.getGoPanel().getGraphics());

				Thread.sleep(waitTime);

				result = Math.max(result, this.minValue(game, alpha, beta));

				// double value = minValue(game);
				// System.out
				// .println("Value = " + Double.toString(result));

				System.out.println("Max Value So Far = "
						+ Double.toString(result));

				if (s != null)
					game.getGoboard().addStone(s);
				else
					game.getGoboard().addStone(new Stone(-1, p));

				if (result >= beta)
					return result;
				alpha = Math.max(alpha, result);

			} // end for

			game.setLearning(false);

		} // end if
		return result;

	} // end maxValue()

	/**
	 * @param game
	 * @return minimum value of all moves
	 * @throws CloneNotSupportedException
	 * @throws InterruptedException
	 */
	public double minValue(GamePlay game, double alpha, double beta)
			throws CloneNotSupportedException, InterruptedException {

		double result = Double.POSITIVE_INFINITY;
		if (game.isGameOver()) {
			double[] scores = game.getGoboard().getScores();
			result = scores[1];
		} else {

			game.setLearning(true);

			this.findLegalMoves(game);

			for (int i = 0; i < this.legalMovesWhite.size(); i++) {

				Point p = this.legalMovesWhite.get(i);

				System.out.println("==============");
				System.out.println("MinValue @ " + Integer.toString(p.x) + ", "
						+ Integer.toString(p.y));

				// game.setCurrLocation(p);
				// game.placePiece(game.getGoboard(), 1);

				Stone s = game.getGoboard().getStone(p);
				game.getGoboard().addStone(new Stone(1, p));

				HadamaGo.getGoPanel()
						.paint(HadamaGo.getGoPanel().getGraphics());

				game.getGoboard().printBoard();

				Thread.sleep(waitTime);

				result = Math.min(result, this.maxValue(game, alpha, beta));
				// double value = minValue(game);
				// System.out
				// .println("Value = " + Double.toString(result));

				System.out.println("Min Value So Far = "
						+ Double.toString(result));

				if (s != null)
					game.getGoboard().addStone(s);
				else
					game.getGoboard().addStone(new Stone(-1, p));

				if (result <= alpha)
					return result;
				beta = Math.min(beta, result);

			} // end for

			game.setLearning(false);

		} // end if
		return result;

	} // end minValue()

	/**
	 * make the best move for min player
	 * 
	 * @throws CloneNotSupportedException
	 * @throws InterruptedException
	 */
	public void makeBestMoveForMin(GamePlay game)
			throws CloneNotSupportedException, InterruptedException {

		double result = Double.POSITIVE_INFINITY;
		game.setLearning(true);
		this.findLegalMoves(game);
		Point bestMove = new Point(0, 0);

		for (int i = 0; i < this.legalMovesWhite.size(); i++) {
			Point p = this.legalMovesWhite.get(i);
			Stone s = game.getGoboard().getStone(p);
			game.getGoboard().addStone(new Stone(0, p));
			HadamaGo.getGoPanel().paint(HadamaGo.getGoPanel().getGraphics());
			Thread.sleep(waitTime);
			double value = this.maxValue(game, Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY);
			if (s != null) {
				game.setCurrLocation(p);
				game.setPlayer(1);
				game.placePiece(game.getGoboard(), 1);
			}
			// game.getGoboard().addStone(s);
			else
				game.getGoboard().addStone(new Stone(-1, p));
			if (value < result) {
				result = value;
				bestMove = p;
			} // end if
		} // end for

		game.setLearning(false);
		game.setCurrLocation(bestMove);
		game.placePiece(game.getGoboard(), 0);

	} // end makeBestMoveForMin()

	/**
	 * make the best move for max player
	 * 
	 * @throws CloneNotSupportedException
	 * @throws InterruptedException
	 */
	public void makeBestMoveForMax(GamePlay game)
			throws CloneNotSupportedException, InterruptedException {

		double result = Double.NEGATIVE_INFINITY;
		game.setLearning(true);
		this.findLegalMoves(game);
		Point bestMove = new Point(0, 0);

		for (int i = 0; i < this.legalMovesWhite.size(); i++) {
			Point p = this.legalMovesWhite.get(i);
			Stone s = game.getGoboard().getStone(p);
			game.getGoboard().addStone(new Stone(1, p));
			HadamaGo.getGoPanel().paint(HadamaGo.getGoPanel().getGraphics());
			Thread.sleep(waitTime);
			double value = this.minValue(game, Double.POSITIVE_INFINITY,
					Double.NEGATIVE_INFINITY);
			if (s != null)
				game.getGoboard().addStone(s);
			else
				game.getGoboard().addStone(new Stone(-1, p));
			if (value > result) {
				result = value;
				bestMove = p;
			} // end if
		} // end for

		System.out.println(bestMove);

		game.setLearning(false);
		game.setCurrLocation(bestMove);
		game.placePiece(game.getGoboard(), 1);

	} // end makeBestMoveForMin()

} // end MiniMax class
