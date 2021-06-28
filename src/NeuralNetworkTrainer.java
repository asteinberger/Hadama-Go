import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class NeuralNetworkTrainer {

	private Random random = new Random(System.currentTimeMillis());
	private double epsilon;
	private double learningRate;
	private double momentum;
	private int maximumRuns;
	private double minimumError;
	private double[][] trainingInput;
	private double[][] trainingOutput;
	private int numberOfInputNeurons;
	private int numberOfHiddenNeurons;
	private int numberOfOutputNeurons;
	private int numberOfTrainingDataLines;
	private NeuralNetwork neuralNetwork;
	private int sampleSize = 5000;

	public NeuralNetworkTrainer(int numberOfInputNeurons,
								int numberOfHiddenNeurons,
								int numberOfOutputNeurons,
								double epsilon,
								double learningRate,
								double momentum,
								int maximumRuns,
								double minimumError,
								int sampleSize) throws Exception {

		this.numberOfInputNeurons = numberOfInputNeurons;
		this.numberOfHiddenNeurons = numberOfHiddenNeurons;
		this.numberOfOutputNeurons = numberOfOutputNeurons;
		this.epsilon = epsilon;
		this.learningRate = learningRate;
		this.momentum = momentum;
		this.maximumRuns = maximumRuns;
		this.minimumError = minimumError;
		this.numberOfTrainingDataLines = this.getDataSize();
		this.sampleSize = sampleSize;
		this.trainingInput = new double[this.numberOfTrainingDataLines][numberOfInputNeurons];
		this.trainingOutput = new double[this.numberOfTrainingDataLines][numberOfOutputNeurons];
		this.neuralNetwork = new NeuralNetwork(numberOfInputNeurons, numberOfHiddenNeurons, numberOfOutputNeurons);

	} // end constructor

	public static void main(String[] args) throws Exception {

		System.out.println("Setup Network Trainer");

		NeuralNetworkTrainer neuralNetworkTrainer =
				new NeuralNetworkTrainer(
						162,
						40,
						2,
						0.001,
						0.9f,
						0.7f,
						50000,
						0.001,
						2500
				);

		NeuralNetwork neuralNetwork = new NeuralNetwork(
				162,
				40,
				2,
				"networkWeights.txt"
		);

		neuralNetworkTrainer.setNeuralNetwork(neuralNetwork);

		System.out.println(neuralNetworkTrainer.getNeuralNetwork());

		System.out.println("Parse Data for Network Trainer");

		neuralNetworkTrainer.parseData();

		System.out.println("Setup Network Data for Network Trainer");

		double[][] trainingInputs = neuralNetworkTrainer.getARandomSampleOfData(neuralNetworkTrainer.getTrainingInput(),
				neuralNetworkTrainer.getSampleSize());
		double[][] expectedOutputs = neuralNetworkTrainer.getARandomSampleOfData(neuralNetworkTrainer.getTrainingOutput(),
				neuralNetworkTrainer.getSampleSize());

		neuralNetworkTrainer.getNeuralNetwork().setupTheNetwork(0.00001, 0.9f, 0.7f, trainingInputs, expectedOutputs);

		System.out.println("Train Neural Network");
		System.out.println("Data Size: " + neuralNetworkTrainer.getDataSize());

		neuralNetworkTrainer.trainTheNetwork();

		double[] input = new double[162];

		for (int index = 0; index < 81; index++) {
			double input1 = Math.round(neuralNetworkTrainer.random.nextDouble());
			double input2 = Math.round(neuralNetworkTrainer.random.nextDouble());
			while ((input1 == 1) && (input2 == 1)) {
				input1 = Math.round(neuralNetworkTrainer.random.nextDouble());
				input2 = Math.round(neuralNetworkTrainer.random.nextDouble());
			} // end while
			input[2 * index] = input1;
			input[2 * index + 1] = input2;
		} // end for

		System.out.println(Arrays.toString(input));

		double[] output = neuralNetworkTrainer.testTheNetwork(input);

		System.out.println(Arrays.toString(output));

	} // end main()

	public double[][] getARandomSampleOfData(double[][] data, int size) {

		int totalSize = data.length;
		int entrySize = data[0].length;

		double[][] randomSample = new double[size][entrySize];

		for (int row = 0; row < size; row++) {
			for (int column = 0; column < entrySize; column++) {
				int randomRow = this.random.nextInt(totalSize);
				randomSample[row][column] = data[randomRow][column];
			} // end for
		} // end for

		return randomSample;

	} // end getARandomSampleOfData()

	public void addTrainingDataToTrainingSet(String trainingData) throws Exception {
		FileWriter fileWriter = new FileWriter("trainData.txt", true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(trainingData + "\n");
		bufferedWriter.close();
	} // end addTrainingDataToTrainingSet()

	public void trainTheNetwork() throws Exception {
		this.neuralNetwork.trainTheNetwork(this.maximumRuns, this.minimumError, "networkWeights.txt");
	} // end trainTheNetwork()

	public double[] testTheNetwork(double[] testInputs) {
		this.neuralNetwork.setTrainingInputs(testInputs);
		this.neuralNetwork.feedForward();
		double[] outputs = this.neuralNetwork.getOutputs();
		return outputs;
	} // end testTheNetwork()

	public void parseData() throws Exception {

		FileInputStream fileInputStream = new FileInputStream("trainData.txt");
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));

		int lineNumber = 0;
		String line;

		while ((line = bufferedReader.readLine()) != null) {

			String[] data = line.split(" ");

			for (int index = 0; index < data[0].length(); index++) {
				String inputData = Character.toString(data[0].charAt(index));
				this.trainingInput[lineNumber][index] = Integer.parseInt(inputData);
			} // end for

			for (int index = 0; index < data[1].length(); index++) {
				String outputData = Character.toString(data[1].charAt(index));
				this.trainingOutput[lineNumber][index] = Integer.parseInt(outputData);
			} // end for

			lineNumber++;

		} // end while

		dataInputStream.close();

	} // end parseData()

	@Override
	public String toString() {
		return "[NeuralNetworkTrainer epsilon=" + epsilon
				+ ", learningRate=" + learningRate + ", momentum=" + momentum
				+ ", maximumRuns=" + maximumRuns + ", minimumError=" + minimumError
				+ ", numberOfInputNeurons=" + numberOfInputNeurons
				+ ", numberOfHiddenNeurons=" + numberOfHiddenNeurons
				+ ", numberOfOutputNeurons=" + numberOfOutputNeurons
				+ ", numberOfTrainingDataLines=" + numberOfTrainingDataLines
				+ ", neuralNetwork=" + neuralNetwork + "]";
	} // end toString()

	/**
	 * get number of lines in data file
	 * 
	 * @return number of training data sets in data file
	 * @throws Exception
	 */
	public int getDataSize() throws Exception {

		FileInputStream fileInputStream = new FileInputStream("trainData.txt");
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));

		int numberOfLines = 0;

		while ((bufferedReader.readLine()) != null) {
			numberOfLines++;
		}

		dataInputStream.close();

		return numberOfLines;

	} // end getDataSize()

	public NeuralNetwork getNeuralNetwork() {
		return this.neuralNetwork;
	} // end getNeuralNetwork()

	public void setNeuralNetwork(NeuralNetwork n) {
		this.neuralNetwork = n;
	} // end setNeuralNetwork()

	public double[][] getTrainingInput() {
		return trainingInput;
	}

	public double[][] getTrainingOutput() {
		return trainingOutput;
	}

	public int getSampleSize() {
		return sampleSize;
	}

} // end class
