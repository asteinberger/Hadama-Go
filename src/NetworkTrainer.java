import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class NetworkTrainer {

	private double epsilon;
	private double learningRate;
	private double momentum;
	private int maxRuns;
	private double minError;
	private double[][] trainInput;
	private double[][] trainOutput;
	private int numInput;
	private int numHidden;
	private int numOutput;
	private int numLines;
	private NeuralNetwork network;

	public NetworkTrainer(int i, int h, int o, double e, double l, double m,
			int mr, double me) throws Exception {

		this.numInput = i;
		this.numHidden = h;
		this.numOutput = o;
		this.epsilon = e;
		this.learningRate = l;
		this.momentum = m;
		this.maxRuns = mr;
		this.minError = me;
		this.numLines = this.getDataSize();
		this.trainInput = new double[this.numLines][i];
		this.trainOutput = new double[this.numLines][o];
		this.network = new NeuralNetwork(i, h, o);

	} // end constructor

	public static void main(String[] args) throws Exception {

		Random random = new Random(System.currentTimeMillis());

		System.out.println("Setup Network Trainer");

		NetworkTrainer netTrain = new NetworkTrainer(162, 40, 2, 0.001, 0.9f,
				0.7f, 50000, 0.001);

		System.out.println("Parse Data for Network Trainer");

		netTrain.parseData();

		System.out.println("Setup Network Data for Network Trainer");

		double[][] in = netTrain.trimData(netTrain.getTrainInput(), 1000);
		double[][] out = netTrain.trimData(netTrain.getTrainOutput(), 1000);

		netTrain.setTrainInput(in);
		netTrain.setTrainOutput(out);

		netTrain.getNetwork().setupNetwork(0.00001, 0.9f, 0.7f, in, out);

		System.out.println("Train Neural Network");
		System.out.println("Data Size: " + netTrain.getDataSize());

		netTrain.trainNetwork();

		double[] input = new double[162];

		for (int i = 0; i < 81; i++) {
			double in1 = Math.round(random.nextDouble());
			double in2 = Math.round(random.nextDouble());
			while ((in1 == 1) && (in2 == 1)) {
				in1 = Math.round(random.nextDouble());
				in2 = Math.round(random.nextDouble());
			} // end while
			input[2 * i] = in1;
			input[2 * i + 1] = in2;
		} // end for

		System.out.println(Arrays.toString(input));

		double[] output = netTrain.test(input);

		System.out.println(Arrays.toString(output));

	} // end main()

	public double[][] trimData(double[][] data, int size) {

		Random random = new Random(System.currentTimeMillis());

		int totalSize = data.length;
		int entrySize = data[0].length;

		double[][] trim = new double[size][entrySize];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < entrySize; j++) {
				int rand = random.nextInt(totalSize);
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
				this.trainInput[lineNum][i] = Integer.parseInt(inputData);
			} // end for

			for (int i = 0; i < data[1].length(); i++) {
				String outputData = Character.toString(data[1].charAt(i));
				this.trainOutput[lineNum][i] = Integer.parseInt(outputData);
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

} // end class
