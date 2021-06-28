import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class QLearning {

	private final Random random = new Random(System.currentTimeMillis());
	private double epsilon = 0.1;
	private double previousQ = 0.0;
	private double learnRate = 0.001;
	private double discountFactor = 0.01;

	public QLearning(double learningRate, double discountFactor, double epsilon) {
		this.learnRate = learningRate;
		this.discountFactor = discountFactor;
		this.epsilon = epsilon;
	} // end constructor

	public boolean epsilonGreedy() {
		boolean[] result = { true, false };
		double[] weight = { this.epsilon, 1.0 - this.epsilon };
		double randomDouble = this.random.nextDouble();
		double temporaryCumulativeSum = 0; // temp cumulative sum
		int index = 0;
		while ((temporaryCumulativeSum += weight[index]) < randomDouble) {
			index++;
		}
		return result[index];
	} // end epsilonGreedy()

	/**
	 * We find all legalmoves for both players
	 * 
	 * @param player
	 * @param gamePlay
	 * @return ArrayList<Point>
	 */
	public ArrayList<Point> findLegalMoves(Player player, GamePlay gamePlay) {

		int boardSize = gamePlay.getGoBoard().getBoardSize();
		if (player == Player.BLACK) {
			ArrayList<Point> illegalMovesForBlack = gamePlay.getGoBoard()
					.getIllegalMovesForBlack();
			ArrayList<Point> legalMovesForBlack = new ArrayList<Point>();

			for (int row = 0; row < boardSize; row++) {
				for (int column = 0; column < boardSize; column++) {
					Point point = new Point(column, row);
					if (!illegalMovesForBlack.contains(point))
						legalMovesForBlack.add(point);
				}
			}
			return legalMovesForBlack;

		} else {

			ArrayList<Point> illegalMovesForWhite = gamePlay.getGoBoard()
					.getIllegalMovesForWhite();
			ArrayList<Point> legalMovesForWhite = new ArrayList<Point>();
			for (int row = 0; row < boardSize; row++) {
				for (int column = 0; column < boardSize; column++) {
					Point point = new Point(column, row);
					if (!illegalMovesForWhite.contains(point)) {
						legalMovesForWhite.add(point);
					}
				}
			}

			return legalMovesForWhite;
		}

	} // end findLegalMoves()

	public Point iterateValue(Player player, GamePlay gamePlay) throws Exception {

		String code = gamePlay.getGoBoard().getCodes();
		ArrayList<Point> moves = this.findLegalMoves(player, gamePlay);

		if (moves.size() == 0) {
			return new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		} // end Point

		NeuralNetwork neuralNetwork = new NeuralNetwork(
				162,
				40,
				2,
				"networkWeights.txt"
		);

		String playerCode = "00";
		if (player == Player.BLACK)
			playerCode = "01";
		else if (player == Player.WHITE)
			playerCode = "10";

		neuralNetwork.createInputLayerFromTrainingCodes(code);
		double[] outputs = neuralNetwork.getOutputs();
		double q = this.qValue(player, outputs);

		double[] outputsMaximum = new double[outputs.length];
		Arrays.fill(outputsMaximum, 1.0);
		Point bestMove = moves.get(0);
		double qMaximum = Double.NEGATIVE_INFINITY;

		ArrayList<ArrayList<Double>> outputMaximums = new ArrayList<ArrayList<Double>>();
		ArrayList<Point> bestMoves = new ArrayList<Point>();
		ArrayList<Double> qMaximums = new ArrayList<Double>();

		for (int index = 0; index < moves.size(); index++) {

			Point move = moves.get(index);
			Point moveReverse = new Point(move.y, move.x);
			String moveCode = code;
			int start = (18 * moveReverse.x) + (2 * moveReverse.y);

			String before = moveCode.substring(0, start);
			String after = moveCode.substring(start + 2);

			moveCode = before.concat(playerCode).concat(after);

			neuralNetwork.createInputLayerFromTrainingCodes(moveCode);
			double[] neuralNetworkOutputs = neuralNetwork.getOutputs();
			double qOfNeuralNetworkOutputs = this.qValue(player, neuralNetworkOutputs);

			if (qOfNeuralNetworkOutputs > qMaximum) {

				qMaximum = qOfNeuralNetworkOutputs;
				qMaximums.clear();
				qMaximums.add(qOfNeuralNetworkOutputs);

				outputMaximums.clear();
				ArrayList<Double> maximums = new ArrayList<Double>();
				for (int outputsIndex = 0; outputsIndex < outputsMaximum.length; outputsIndex++) {
					outputsMaximum[outputsIndex] = neuralNetworkOutputs[outputsIndex];
					maximums.add(neuralNetworkOutputs[outputsIndex]);
				} // end for
				outputMaximums.add(maximums);

				bestMove = new Point(moveReverse);
				bestMoves.clear();
				bestMoves.add(bestMove);

			} else if (qOfNeuralNetworkOutputs == qMaximum) {

				qMaximum = qOfNeuralNetworkOutputs;
				qMaximums.add(qOfNeuralNetworkOutputs);

				ArrayList<Double> maximums = new ArrayList<Double>();
				for (int outputsIndex = 0; outputsIndex < outputsMaximum.length; outputsIndex++) {
					outputsMaximum[outputsIndex] = neuralNetworkOutputs[outputsIndex];
					maximums.add(neuralNetworkOutputs[outputsIndex]);
				} // end for
				outputMaximums.add(maximums);

				bestMove = new Point(moveReverse);
				bestMoves.add(bestMove);

			} // end if

		} // end for

		if (qMaximums.size() > 1) {

			int randomInteger = this.random.nextInt(qMaximums.size());
			ArrayList<Double> randomOutputMaximum = outputMaximums.get(randomInteger);
			for (int index = 0; index < outputsMaximum.length; index++) {
				outputsMaximum[index] = randomOutputMaximum.get(index);
			}
			bestMove = bestMoves.get(randomInteger);
			qMaximum = qMaximums.get(randomInteger);

		} // end if

		boolean isRandom = this.epsilonGreedy();
		if (isRandom) {

			int randomInteger = this.random.nextInt(moves.size());
			bestMove = moves.get(randomInteger);
			String moveCode = code;
			int start = (18 * bestMove.x) + (2 * bestMove.y);

			String before = moveCode.substring(0, start);
			String after = moveCode.substring(start + 2);

			moveCode = before.concat(playerCode).concat(after);

			neuralNetwork.createInputLayerFromTrainingCodes(moveCode);
			double[] randomOutputs = neuralNetwork.getOutputs();
			qMaximum = this.qValue(player, randomOutputs);

		} // end if

		double updatedQ = q + this.learnRate
				* (this.previousQ + (this.discountFactor * qMaximum) - q);

		this.previousQ = updatedQ;

		Point bestMoveLocation = new Point(bestMove.y, bestMove.x);

		return bestMoveLocation;

	} // end QLearning()

	public double qValue(Player player, double[] out) {

		double q = 0;

		if (player == Player.BLACK) { // black player

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

		} else if (player == Player.WHITE) { // white player

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

} // end class
