import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class QLearning {

	private final Random random = new Random(System.currentTimeMillis());
	private double epsilon = 0.1;
	private double qPrev = 0.0;
	private double learnRate = 0.001;
	private double discountFactor = 0.01;

	public QLearning(double lr, double df, double ep) {
		this.learnRate = lr;
		this.discountFactor = df;
		this.epsilon = ep;
	} // end constructor

	public boolean epsilonGreedy() {
		boolean[] select = { true, false };
		double[] weight = { this.epsilon, 1.0 - this.epsilon };
		double rand = this.random.nextDouble();
		double s = 0; // temp cumulative sum
		int i = 0;
		while ((s += weight[i]) < rand)
			i++;
		boolean isRandom = select[i];
		return isRandom;
	} // end epsilonGreedy()

	/**
	 * We find all legalmoves for both players
	 * 
	 * @param player
	 * @param game
	 * @return ArrayList<Point>
	 */
	public ArrayList<Point> findLegalMoves(int player, GamePlay game) {

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

	public Point iterateValue(int player, GamePlay game) throws Exception {

		String code = game.getGoboard().getCodes();
		ArrayList<Point> moves = this.findLegalMoves(player, game);

		NeuralNetwork network = new NeuralNetwork(162, 40, 2,
				"networkWeights.txt");

		String playerCode = "00";
		if (player == 0)
			playerCode = "01";
		else if (player == 1)
			playerCode = "10";

		network.setInput(code);
		double[] out = network.getOutput();
		double q = this.qValue(player, out);

		double[] outMax = new double[out.length];
		Arrays.fill(outMax, 1.0);
		Point bestMove = moves.get(0);
		double qMax = Double.NEGATIVE_INFINITY;

		ArrayList<ArrayList<Double>> outMaxes = new ArrayList<ArrayList<Double>>();
		ArrayList<Point> bestMoves = new ArrayList<Point>();
		ArrayList<Double> qMaxes = new ArrayList<Double>();

		for (int i = 0; i < moves.size(); i++) {

			Point m = moves.get(i);
			Point move = new Point(m.y, m.x);
			String moveCode = new String(code);
			int start = (18 * move.x) + (2 * move.y);

			String before = moveCode.substring(0, start);
			String after = moveCode.substring(start + 2);

			moveCode = before.concat(playerCode).concat(after);

			network.setInput(moveCode);
			double[] outNext = network.getOutput();
			double qNext = this.qValue(player, outNext);

			if (qNext > qMax) {

				qMax = qNext;
				qMaxes.clear();
				qMaxes.add(qNext);

				outMaxes.clear();
				ArrayList<Double> maxes = new ArrayList<Double>();
				for (int j = 0; j < outMax.length; j++) {
					outMax[j] = outNext[j];
					maxes.add(outNext[j]);
				} // end for
				outMaxes.add(maxes);

				bestMove = new Point(move);
				bestMoves.clear();
				bestMoves.add(bestMove);

			} else if (qNext == qMax) {

				qMax = qNext;
				qMaxes.add(qNext);

				ArrayList<Double> maxes = new ArrayList<Double>();
				for (int j = 0; j < outMax.length; j++) {
					outMax[j] = outNext[j];
					maxes.add(outNext[j]);
				} // end for
				outMaxes.add(maxes);

				bestMove = new Point(move);
				bestMoves.add(bestMove);

			} // end if

		} // end for

		if (qMaxes.size() > 1) {

			int rand = this.random.nextInt(qMaxes.size());
			ArrayList<Double> mo = outMaxes.get(rand);
			for (int i = 0; i < outMax.length; i++)
				outMax[i] = mo.get(i);
			bestMove = bestMoves.get(rand);
			qMax = qMaxes.get(rand);

		} // end if

		boolean isRandom = this.epsilonGreedy();
		if (isRandom) {

			int rand = this.random.nextInt(moves.size());
			bestMove = moves.get(rand);
			String moveCode = new String(code);
			int start = (18 * bestMove.x) + (2 * bestMove.y);

			String before = moveCode.substring(0, start);
			String after = moveCode.substring(start + 2);

			moveCode = before.concat(playerCode).concat(after);

			network.setInput(moveCode);
			double[] randOut = network.getOutput();
			qMax = this.qValue(player, randOut);

		} // end if

		double qUpdate = q + this.learnRate
				* (this.qPrev + (this.discountFactor * qMax) - q);

		this.qPrev = qUpdate;

		Point best = new Point(bestMove.y, bestMove.x);

		return best;

	} // end QLearning()

	public double qValue(int player, double[] out) {

		double q = 0;

		if (player == 0) { // black player

			// black wins
			if ((Math.round(out[0]) == 0.0) && (Math.round(out[1]) == 0.0))
				q = 1;
			// tie game
			else if ((Math.round(out[0]) == 0.0) && (Math.round(out[1]) == 1.0))
				q = 0;
			// white wins
			else if ((Math.round(out[0]) == 1.0) && (Math.round(out[1]) == 0.0))
				q = -10;
			// avoid garbage
			else if ((Math.round(out[0]) == 1.0) && (Math.round(out[1]) == 1.0))
				q = -20;

		} else if (player == 1) { // white player

			// black wins
			if ((Math.round(out[0]) == 0.0) && (Math.round(out[1]) == 0.0))
				q = -10;
			// tie game
			else if ((Math.round(out[0]) == 0.0) && (Math.round(out[1]) == 1.0))
				q = 0;
			// white wins
			else if ((Math.round(out[0]) == 1.0) && (Math.round(out[1]) == 0.0))
				q = 1;
			// avoid garbage
			else if ((Math.round(out[0]) == 1.0) && (Math.round(out[1]) == 1.0))
				q = -20;

		} // end if

		return q;

	} // end qValue()

	public double getqPrev() {
		return qPrev;
	}

	public void setqPrev(double qPrev) {
		this.qPrev = qPrev;
	}

	public double getLearnRate() {
		return learnRate;
	}

	public void setLearnRate(double learnRate) {
		this.learnRate = learnRate;
	}

	public double getDiscountFactor() {
		return discountFactor;
	}

	public void setDiscountFactor(double discountFactor) {
		this.discountFactor = discountFactor;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public Random getRandom() {
		return random;
	}

} // end class
