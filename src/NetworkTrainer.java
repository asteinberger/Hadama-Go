import java.io.*;
import java.util.Arrays;

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
		this.network.setupNetwork(e, l, m, this.trainInput, this.trainOutput);
	} // end constructor

	public static void main(String[] args) throws Exception {

		NetworkTrainer netTrain = new NetworkTrainer(162, 40, 2, 0.0001, 0.9f,
				0.7f, 50000, 0.0001);

		netTrain.trainNetwork();

		double[] input = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		double[] output = netTrain.test(input);

		System.out.println(Arrays.toString(output));

	} // end main()

	public void addToTrainSet(String codes) {

		try {
			FileWriter fstream = new FileWriter("trainData.txt", true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(codes + "\n");
			out.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		} // end try

	} // end addToTrainSet()

	public void trainNetwork() throws Exception {
		this.parseData();
		this.network.run(this.maxRuns, this.minError);
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

	} // end

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
