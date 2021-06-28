import java.io.*;
import java.util.*;

public class NeuralNetwork {

	private final Random random = new Random(System.currentTimeMillis());
	private ArrayList<Neuron> inputLayer = new ArrayList<Neuron>();
	private ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
	private ArrayList<Neuron> outputLayer = new ArrayList<Neuron>();
	private Neuron biasNeuron = new Neuron();
	private int[] layers;
	private double epsilon = 0.00000000001;
	private double learningRate = 0.9f;
	private double momentum = 0.7f;
	private double trainingInputs[][];
	private double expectedOutputs[][];
	private double resultingOutputs[][];
	private boolean isTheNetworkTrained = false;

	public NeuralNetwork(int numberOfInputNeurons,
						 int numberOfHiddenNeurons,
						 int numberOfOutputNeurons) {

		// build the neural network
		buildNeuralNetwork(
				numberOfInputNeurons,
				numberOfHiddenNeurons,
				numberOfOutputNeurons
		);

		// set neural connection weights
		setWeights(true, null, null);

		// reset id counters
		resetIdCounters();

	} // end constructor

	public NeuralNetwork(int numberOfInputNeurons,
						 int numberOfHiddenNeurons,
						 int numberOfOutputNeurons,
						 String providedWeightsFileName)
			throws Exception {

		FileInputStream inputFileStream = new FileInputStream(providedWeightsFileName);
		DataInputStream inputDataStream = new DataInputStream(inputFileStream);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputDataStream));

		ArrayList<String> inputData = new ArrayList<String>();
		String inputLine;

		while ((inputLine = bufferedReader.readLine()) != null) {
			inputData.add(inputLine);
		}

		inputDataStream.close();

		String[] inputHiddenLayer = inputData.get(0).split(" ");
		String[] inputOutputLayer = inputData.get(1).split(" ");

		// Build the Neural Network
		buildNeuralNetwork(
				numberOfInputNeurons,
				numberOfHiddenNeurons,
				numberOfOutputNeurons
		);

		// set neural connection weights
		setWeights(false, inputHiddenLayer, inputOutputLayer);

		// reset id counters
		resetIdCounters();

	} // end constructor

	private void buildNeuralNetwork(int numberOfInputNeurons,
									int numberOfHiddenNeurons,
									int numberOfOutputNeurons) {

		this.layers = new int[] {
				numberOfInputNeurons,
				numberOfHiddenNeurons,
				numberOfOutputNeurons
		};

		for (int layerIndex = 0; layerIndex < this.layers.length; layerIndex++) {

			if (layerIndex == 0) { // input layer

				for (int neuronIndex = 0; neuronIndex < this.layers[layerIndex]; neuronIndex++) {
					Neuron neuron = new Neuron();
					this.inputLayer.add(neuron);
				} // end if

			} else if (layerIndex == 1) { // hidden layer

				for (int neuronIndex = 0; neuronIndex < this.layers[layerIndex]; neuronIndex++) {
					Neuron neuron = new Neuron();
					neuron.addNeurons(this.inputLayer);
					neuron.addBias(this.biasNeuron);
					this.hiddenLayer.add(neuron);
				} // end for

			} else if (layerIndex == 2) { // output layer

				for (int neuronIndex = 0; neuronIndex < this.layers[layerIndex]; neuronIndex++) {
					Neuron neuron = new Neuron();
					neuron.addNeurons(this.hiddenLayer);
					neuron.addBias(this.biasNeuron);
					this.outputLayer.add(neuron);
				} // end for

			} else {
				System.err
						.println("Error: Neural Network could not be initialized!");
			} // end if

		} // end for

	} // end buildNeuralNetwork()

	private void setWeights(boolean weightsAreRandom,
							String[] hiddenLayerWeights,
							String[] outputLayerWeights) {

		int weightsIndex = 0;
		for (int hiddenLayerIndex = 0; hiddenLayerIndex < this.hiddenLayer.size(); hiddenLayerIndex++) {
			Neuron neuron = this.hiddenLayer.get(hiddenLayerIndex);
			ArrayList<Dendrite> dendrites = neuron.getDendrites();
			setDendriteWeight(
					weightsAreRandom,
					weightsIndex,
					dendrites,
					hiddenLayerWeights
			);
		} // end for

		weightsIndex = 0;
		for (int outputLayerIndex = 0; outputLayerIndex < this.outputLayer.size(); outputLayerIndex++) {
			Neuron neuron = this.outputLayer.get(outputLayerIndex);
			ArrayList<Dendrite> dendrites = neuron.getDendrites();
			setDendriteWeight(
					weightsAreRandom,
					weightsIndex,
					dendrites,
					outputLayerWeights
			);
		} // end for

	} // end setWeights()

	private void setDendriteWeight(boolean weightsAreRandom,
								  int weightsIndex,
								  ArrayList<Dendrite> dendrites,
								  String[] layerWeights) {

		for (int connectionIndex = 0; connectionIndex < dendrites.size(); connectionIndex++) {
			Dendrite dendrite = dendrites.get(connectionIndex);
			double connectionWeight;
			if (weightsAreRandom) {
				connectionWeight = this.getRandom();
			} else {
				connectionWeight = Double.parseDouble(layerWeights[weightsIndex]);
			}
			dendrite.setWeight(connectionWeight);
			weightsIndex++;
		} // end for

	} // end setDendriteWeight()

	private void resetIdCounters() {
		Neuron.setCounter(0);
		Dendrite.setCounter(0);
	} // end resetIdCounters()

	public void setupTheNetwork(double epsilon,
								double learningRate,
								double momentum,
								double[][] trainingInputs,
								double[][] expectedOutputs) {
		this.epsilon = epsilon;
		this.learningRate = learningRate;
		this.momentum = momentum;
		this.trainingInputs = trainingInputs;
		this.expectedOutputs = expectedOutputs;
		this.resultingOutputs = new double[expectedOutputs.length][expectedOutputs[0].length];
		for (int index = 0; index < expectedOutputs.length; index++) {
			Arrays.fill(this.resultingOutputs[index], 0.0);
		}
	} // end setupNetwork()

	private double getRandom() {
		return this.random.nextDouble(); // [0,1]
	} // end getRandom()

	/**
	 * Input percept data for thinking
	 * 
	 * @param trainingInputs
	 */
	public void setInputLayerNeurons(double trainingInputs[]) {
		for (int index = 0; index < this.inputLayer.size(); index++) {
			this.inputLayer.get(index).setOutput(trainingInputs[index]);
		}
	} // end setInput()

	public void createInputLayerFromTrainingCodes(String code) {
		for (int index = 0; index < code.length(); index++) {
			char character = code.charAt(index);
			String string = String.valueOf(character);
			int bit = Integer.parseInt(string);
			this.inputLayer.get(index).setOutput(bit);
		} // end for
	} // end setInputs()

	/**
	 * Get output learned from neural network thought
	 * 
	 * @return
	 */
	public double[] getOutputs() {
		double[] outputs = new double[this.outputLayer.size()];
		for (int index = 0; index < this.outputLayer.size(); index++) {
			outputs[index] = this.outputLayer.get(index).getOutput();
		}
		return outputs;
	} // end getOutputs()

	/**
	 * Run the Neural Network
	 *
	 * @return the output from the neural network
	 */
	public double[] runTheNetwork(double[] inputs) {
		this.setInputLayerNeurons(inputs);
		this.feedForward();
		double[] outputs = new double[outputLayer.size()];
		int outputIndex = 0;
		Iterator<Neuron> iterator = outputLayer.iterator();
		while (iterator.hasNext()) {
			Neuron neuron = iterator.next();
			outputs[outputIndex] = neuron.getOutput();
			outputIndex++;
		}
		return outputs;
	}

	/**
	 * Feed Forward
	 */
	public void feedForward() {

		for (int index = 0; index < this.hiddenLayer.size(); index++) {
			Neuron neuron = this.hiddenLayer.get(index);
			neuron.computeOutput();
		} // end for

		for (int index = 0; index < this.outputLayer.size(); index++) {
			Neuron neuron = this.outputLayer.get(index);
			neuron.computeOutput();
		} // end for

	} // end feedForward()

	/**
	 * Back Propagation
	 * 
	 * @param expectedOutput
	 *            first calculate the partial derivative of the error with
	 *            respect to each of the weight leading into the output neurons
	 *            bias is also updated here
	 */
	public void backPropagate(double expectedOutput[]) {

		// error check, normalize value [0,1]
		for (int expectedOutputIndex = 0; expectedOutputIndex < expectedOutput.length; expectedOutputIndex++) {
			double expected = expectedOutput[expectedOutputIndex];
			if ((expected < 0) || (expected > 1)) {
				if (expected < 0) {
					expectedOutput[expectedOutputIndex] = this.epsilon;
				} else {
					expectedOutput[expectedOutputIndex] = 1.0 - this.epsilon;
				}
			} // end if
		} // end for

		int index = 0;

		// update weights for the output layer
		for (int outputIndex = 0; outputIndex < this.outputLayer.size(); outputIndex++) {

			Neuron outputNeuron = this.outputLayer.get(outputIndex);
			ArrayList<Dendrite> dendrites = outputNeuron.getDendrites();

			for (int dendriteIndex = 0; dendriteIndex < dendrites.size(); dendriteIndex++) {
				Dendrite dendrite = dendrites.get(dendriteIndex);
				double output = outputNeuron.getOutput();
				double hiddenNeuronActivation = dendrite.getFromNeuron().getOutput();
				double desiredOutput = expectedOutput[index];
				double partialDerivative = -output * (1.0 - output) * hiddenNeuronActivation
						* (desiredOutput - output);
				double deltaWeight = -this.learningRate * partialDerivative;
				double adjustedWeight = dendrite.getWeight() + deltaWeight;
				dendrite.setDeltaWeight(deltaWeight);
				dendrite.setWeight(adjustedWeight + this.momentum
						* dendrite.getPreviousDeltaWeight());
			} // end for

			index++;

		} // end for

		// update weights for the hidden layer
		for (int hiddenIndex = 0; hiddenIndex < this.hiddenLayer.size(); hiddenIndex++) {

			Neuron hiddenNeuron = this.hiddenLayer.get(hiddenIndex);
			ArrayList<Dendrite> dendrites = hiddenNeuron.getDendrites();

			for (int dendriteIndex = 0; dendriteIndex < dendrites.size(); dendriteIndex++) {

				Dendrite dendrite = dendrites.get(dendriteIndex);
				double hiddenNeuronOutput = hiddenNeuron.getOutput();
				double inputNeuronOutput = dendrite.getFromNeuron().getOutput();
				double computedSumOfOutputs = 0;

				for (int outputIndex = 0; outputIndex < this.outputLayer.size(); outputIndex++) {
					Neuron outputNeuron = this.outputLayer.get(outputIndex);
					double hiddenNeuronWeight = outputNeuron.getDendrite(hiddenNeuron.id).getWeight();
					double desiredOutput = expectedOutput[outputIndex];
					double actualOutput = outputNeuron.getOutput();
					computedSumOfOutputs += -(desiredOutput - actualOutput) * actualOutput * (1 - actualOutput) * hiddenNeuronWeight;
				} // end for

				double partialDerivative = hiddenNeuronOutput * (1 - hiddenNeuronOutput) * inputNeuronOutput * computedSumOfOutputs;
				double deltaWeight = -this.learningRate * partialDerivative;
				double adjustedWeight = dendrite.getWeight() + deltaWeight;
				dendrite.setDeltaWeight(deltaWeight);
				dendrite.setWeight(adjustedWeight + this.momentum
						* dendrite.getPreviousDeltaWeight());

			} // end for

		} // end for

	} // end backPropagate()

	public void trainTheNetwork(int maximumSteps, double minimumError, String trainedWeightsOutputFileName)
			throws Exception {

		int steps;
		double error = 1;

		for (steps = 0; (steps < maximumSteps) && (error > minimumError); steps++) {

			error = 0;

			for (int row = 0; row < this.trainingInputs.length; row++) {

				this.setInputLayerNeurons(this.trainingInputs[row]);
				this.feedForward();
				this.resultingOutputs[row] = this.getOutputs();

				for (int column = 0; column < this.expectedOutputs[row].length; column++) {
					double calculatedError = Math.pow(this.resultingOutputs[row][column]
							- this.expectedOutputs[row][column], 2.0);
					error += calculatedError;
				} // end for

				this.backPropagate(this.expectedOutputs[row]);

				this.saveWeights(trainedWeightsOutputFileName);

			} // end for

		} // end for

		this.isTheNetworkTrained = true;

		if (steps == maximumSteps) {
			System.err
					.println("Error: Neural Network training procedure has failed!");
		}

	} // end trainTheNetwork()

	@Override
	public String toString() {
		return "[NeuralNetwork \ninputLayer=" + this.inputLayer
				+ ", \nhiddenLayer=" + this.hiddenLayer + ", \noutputLayer="
				+ this.outputLayer + ", \nbiasNeuron=" + this.biasNeuron + ", layers="
				+ Arrays.toString(this.layers) + ", epsilon=" + this.epsilon
				+ ", learningRate=" + this.learningRate + ", momentum="
				+ this.momentum + ", \ninputs="
				+ Arrays.deepToString(this.trainingInputs) + ", \nexpectedOutputs="
				+ Arrays.deepToString(this.expectedOutputs)
				+ ", \nresultingOutputs="
				+ Arrays.deepToString(this.resultingOutputs) + ", \nisTheNetworkTrained="
				+ this.isTheNetworkTrained + "]";
	} // end toString()

	public void saveWeights(String outputFileName) throws Exception {

		ArrayList<Double> hiddenWeights = new ArrayList<Double>();
		for (int neuronIndex = 0; neuronIndex < this.hiddenLayer.size(); neuronIndex++) {
			Neuron neuron = this.hiddenLayer.get(neuronIndex);
			ArrayList<Dendrite> dendrites = neuron.getDendrites();
			for (int dendriteIndex = 0; dendriteIndex < dendrites.size(); dendriteIndex++) {
				Dendrite dendrite = dendrites.get(dendriteIndex);
				double weight = dendrite.getWeight();
				hiddenWeights.add(weight);
			} // end for
		} // end for

		ArrayList<Double> outputWeights = new ArrayList<Double>();
		for (int neuronIndex = 0; neuronIndex < this.outputLayer.size(); neuronIndex++) {
			Neuron neuron = this.outputLayer.get(neuronIndex);
			ArrayList<Dendrite> dendrites = neuron.getDendrites();
			for (int dendriteIndex = 0; dendriteIndex < dendrites.size(); dendriteIndex++) {
				Dendrite dendrite = dendrites.get(dendriteIndex);
				double weight = dendrite.getWeight();
				outputWeights.add(weight);
			} // end for
		} // end for

		String data = "";

		for (int index = 0; index < hiddenWeights.size(); index++) {
			double weight = hiddenWeights.get(index);
			data += weight + " ";
		} // end for

		data += "\n";

		for (int index = 0; index < outputWeights.size(); index++) {
			double weight = outputWeights.get(index);
			data += weight + " ";
		} // end for

		FileWriter fileWriter = new FileWriter(outputFileName);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(data + "\n");
		bufferedWriter.close();

	} // end saveWeights()

} // end class