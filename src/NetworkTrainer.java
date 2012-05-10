import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class NetworkTrainer {

	private Random random = new Random(System.currentTimeMillis());
	private double epsilon;
	private double learningRate;
	private double momentum;
	private int maxRuns;
	private double minError;
	private double[][] trainInput;
	private double[][] trainOutput;
	private double[][] totalInput;
	private double[][] totalOutput;
	private int numInput;
	private int numHidden;
	private int numOutput;
	private int numLines;
	private NeuralNetwork network;
	private int sampleSize = 5000;
	private double probCV = 0.001;

	public NetworkTrainer(int i, int h, int o, double e, double l, double m,
			int mr, double me, int ss) throws Exception {

		this.numInput = i;
		this.numHidden = h;
		this.numOutput = o;
		this.epsilon = e;
		this.learningRate = l;
		this.momentum = m;
		this.maxRuns = mr;
		this.minError = me;
		this.numLines = this.getDataSize();
		this.sampleSize = ss;
		this.trainInput = new double[ss][i];
		this.trainOutput = new double[ss][o];
		this.totalInput = new double[this.numLines][i];
		this.totalOutput = new double[this.numLines][o];
		this.network = new NeuralNetwork(i, h, o);

	} // end constructor

	public static void main(String[] args) throws Exception {

		System.out.println("Setup Network Trainer");

		NetworkTrainer netTrain = new NetworkTrainer(162, 40, 2, 0.001, 0.9f,
				0.7f, 50000, 0.001, 2500);

		NeuralNetwork network = new NeuralNetwork(162, 40, 2,
				"networkWeights.txt");
		netTrain.setNetwork(network);

		System.out.println(netTrain.getNetwork());

		System.out.println("Parse Data for Network Trainer");

		netTrain.parseData();

		System.out.println("Setup Network Data for Network Trainer");

		double[][] in = netTrain.trimData(netTrain.getTotalInput(),
				netTrain.getSampleSize());
		double[][] out = netTrain.trimData(netTrain.getTotalOutput(),
				netTrain.getSampleSize());

		netTrain.setTrainInput(in);
		netTrain.setTrainOutput(out);

		netTrain.getNetwork().setupNetwork(0.00001, 0.9f, 0.7f, in, out);

		System.out.println("Train Neural Network");
		System.out.println("Data Size: " + netTrain.getDataSize());

		netTrain.trainNetwork();

		double[] input = new double[162];

		for (int i = 0; i < 81; i++) {
			double in1 = Math.round(netTrain.random.nextDouble());
			double in2 = Math.round(netTrain.random.nextDouble());
			while ((in1 == 1) && (in2 == 1)) {
				in1 = Math.round(netTrain.random.nextDouble());
				in2 = Math.round(netTrain.random.nextDouble());
			} // end while
			input[2 * i] = in1;
			input[2 * i + 1] = in2;
		} // end for

		System.out.println(Arrays.toString(input));

		double[] output = netTrain.test(input);

		System.out.println(Arrays.toString(output));

	} // end main()

	public void crossValidate() {
		this.trainInput = this.trimData(this.totalInput, this.sampleSize);
		this.trainOutput = this.trimData(this.totalOutput, this.sampleSize);
		this.network.setupNetwork(this.epsilon, this.learningRate,
				this.momentum, this.trainInput, this.trainOutput);
	} // end crossValidate()

	public double[][] trimData(double[][] data, int size) {

		int totalSize = data.length;
		int entrySize = data[0].length;

		double[][] trim = new double[size][entrySize];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < entrySize; j++) {
				int rand = this.random.nextInt(totalSize);
				trim[i][j] = data[rand][j];
			} // end for
		} // end for

		return trim;

	} // end trimData()

	public void addToTrainSet(String codes) throws Exception {
		FileWriter fstream = new FileWriter("trainData.txt", true);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(codes + "\n");
		out.close();
	} // end addToTrainSet()

	public void trainNetwork() throws Exception {
		this.network.run(this.maxRuns, this.minError, "networkWeights.txt");
	} // end trainNetwork()

	public double[] test(double[] testInput) {
		this.network.setInput(testInput);
		this.network.feedForward();
		double[] out = this.network.getOutput();
		return out;
	} // end test()

	public boolean checkCrossValid() {
		boolean[] select = { true, false };
		double[] weight = { this.probCV, 1.0 - this.probCV };
		double rand = this.random.nextDouble();
		double s = 0; // temp cumulative sum
		int i = 0;
		while ((s += weight[i]) < rand)
			i++;
		boolean isRandom = select[i];
		return isRandom;
	} // end checkCrossValid()

	public void parseData() throws Exception {

		FileInputStream fstream = new FileInputStream("trainData.txt");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		int lineNum = 0;
		String strLine;

		while ((strLine = br.readLine()) != null) {

			String[] data = strLine.split(" ");

			for (int i = 0; i < data[0].length(); i++) {
				String inputData = Character.toString(data[0].charAt(i));
				this.totalInput[lineNum][i] = Integer.parseInt(inputData);
			} // end for

			for (int i = 0; i < data[1].length(); i++) {
				String outputData = Character.toString(data[1].charAt(i));
				this.totalOutput[lineNum][i] = Integer.parseInt(outputData);
			} // end for

			lineNum++;

		} // end while

		in.close();

	} // end parseData()

	@Override
	public String toString() {
		return "[NetworkTrainer epsilon=" + epsilon + ", learningRate="
				+ learningRate + ", momentum=" + momentum + ", maxRuns="
				+ maxRuns + ", minError=" + minError + ", trainInput="
				+ Arrays.toString(trainInput) + ", trainOutput="
				+ Arrays.toString(trainOutput) + ", numInput=" + numInput
				+ ", numHidden=" + numHidden + ", numOutput=" + numOutput
				+ ", numLines=" + numLines + ", network=" + network + "]";
	} // end toString()

	/**
	 * get number of lines in data file
	 * 
	 * @return number of training data sets in data file
	 * @throws Exception
	 */
	public int getDataSize() throws Exception {

		FileInputStream fstream = new FileInputStream("trainData.txt");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		int lines = 0;
		String strLine;

		while ((strLine = br.readLine()) != null)
			lines++;

		in.close();

		System.err.println(strLine);

		return lines;

	} // end getDataSize()

	public double getEpsilon() {
		return this.epsilon;
	} // end getEpsilon()

	public void setEpsilon(double e) {
		this.epsilon = e;
	} // end setEpsilon()

	public double getLearningRate() {
		return this.learningRate;
	} // end getLearningRate()

	public void setLearningRate(double l) {
		this.learningRate = l;
	} // end setLearningRate()

	public double getMomentum() {
		return this.momentum;
	} // end getMomentum()

	public void setMomentum(double m) {
		this.momentum = m;
	} // end setMomentum()

	public int getMaxRuns() {
		return maxRuns;
	} // end getMaxRuns()

	public void setMaxRuns(int m) {
		this.maxRuns = m;
	} // end setMaxRuns()

	public double getMinError() {
		return this.minError;
	} // end getMinError()

	public void setMinError(double m) {
		this.minError = m;
	} // end setMinError()

	public double[][] getTrainInput() {
		return this.trainInput;
	} // end getTrainInput()

	public void setTrainInput(double[][] t) {
		this.trainInput = t;
	} // end setTrainInput()

	public double[][] getTrainOutput() {
		return this.trainOutput;
	} // end getTrainOutput()

	public void setTrainOutput(double[][] t) {
		this.trainOutput = t;
	} // end setTrainOutput()

	public int getNumInput() {
		return this.numInput;
	} // end getNumInput()

	public void setNumInput(int n) {
		this.numInput = n;
	} // end setNumInput()

	public int getNumHidden() {
		return this.numHidden;
	} // end getNumHidden()

	public void setNumHidden(int n) {
		this.numHidden = n;
	} // end setNumHidden()

	public int getNumOutput() {
		return this.numOutput;
	} // end getNumOutput()

	public void setNumOutput(int n) {
		this.numOutput = n;
	} // end setNumOutput()

	public int getNumLines() {
		return this.numLines;
	} // end getNumLines()

	public void setNumLines(int n) {
		this.numLines = n;
	} // end setNumLines()

	public NeuralNetwork getNetwork() {
		return this.network;
	} // end getNetwork()

	public void setNetwork(NeuralNetwork n) {
		this.network = n;
	} // end setNetwork()

	public double[][] getTotalInput() {
		return totalInput;
	}

	public void setTotalInput(double[][] totalInput) {
		this.totalInput = totalInput;
	}

	public double[][] getTotalOutput() {
		return totalOutput;
	}

	public void setTotalOutput(double[][] totalOutput) {
		this.totalOutput = totalOutput;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public double getProbCV() {
		return probCV;
	}

	public void setProbCV(double probCV) {
		this.probCV = probCV;
	}

} // end class
