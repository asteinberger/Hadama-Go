import java.io.*;
import java.util.*;

public class NeuralNetwork {

	private final Random random = new Random(System.currentTimeMillis());
	private ArrayList<Neuron> inputLayer = new ArrayList<Neuron>();
	private ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
	private ArrayList<Neuron> outputLayer = new ArrayList<Neuron>();
	private Neuron bias = new Neuron();
	private int[] layers;
	private double epsilon = 0.00000000001;
	private double learningRate = 0.9f;
	private double momentum = 0.7f;
	private double inputs[][] = { { 1, 1 }, { 1, 0 }, { 0, 1 }, { 0, 0 } };
	private double expectedOutputs[][] = { { 0 }, { 1 }, { 1 }, { 0 } };
	private double resultOutputs[][] = { { -1 }, { -1 }, { -1 }, { -1 } };
	private double output[];
	private boolean isTrained = false;

	public NeuralNetwork(int input, int hidden, int output) throws Exception {

		this.layers = new int[] { input, hidden, output };

		for (int i = 0; i < this.layers.length; i++) {

			if (i == 0) { // input layer

				for (int j = 0; j < this.layers[i]; j++) {
					Neuron neuron = new Neuron();
					this.inputLayer.add(neuron);
				} // end if

			} else if (i == 1) { // hidden layer

				for (int j = 0; j < this.layers[i]; j++) {
					Neuron neuron = new Neuron();
					neuron.addDendrites(this.inputLayer);
					neuron.addBiasConnection(this.bias);
					this.hiddenLayer.add(neuron);
				} // end for

			} else if (i == 2) { // output layer

				for (int j = 0; j < this.layers[i]; j++) {
					Neuron neuron = new Neuron();
					neuron.addDendrites(this.hiddenLayer);
					neuron.addBiasConnection(this.bias);
					this.outputLayer.add(neuron);
				} // end for

			} else {
				System.err
						.println("Error: Neural Network could not be initialized!");
			} // end if

		} // end for

		// initialize random weights
		for (int n = 0; n < this.hiddenLayer.size(); n++) {
			Neuron neuron = this.hiddenLayer.get(n);
			ArrayList<Connection> connections = neuron.getDendrites();
			for (int c = 0; c < connections.size(); c++) {
				Connection connect = connections.get(c);
				double weight = this.getRandom();
				connect.setWeight(weight);
			} // end for
		} // end for

		for (int n = 0; n < this.outputLayer.size(); n++) {
			Neuron neuron = this.outputLayer.get(n);
			ArrayList<Connection> connections = neuron.getDendrites();
			for (int c = 0; c < connections.size(); c++) {
				Connection connect = connections.get(c);
				double weight = this.getRandom();
				connect.setWeight(weight);
			} // end for
		} // end for

		// reset id counters
		Neuron.setCounter(0);
		Connection.setCounter(0);

	} // end constructor

	public void setupNetwork(double e, double lr, double m, double[][] in,
			double[][] out) {
		this.epsilon = e;
		this.learningRate = lr;
		this.momentum = m;
		this.inputs = in;
		this.expectedOutputs = out;
		this.resultOutputs = new double[out.length][out[0].length];
		for (int i = 0; i < out.length; i++)
			Arrays.fill(this.resultOutputs[i], 0.0);
	} // end setupNetwork()

	double getRandom() {
		return this.random.nextDouble(); // [0,1]
	} // end getRandom()

	/**
	 * Input percept data for thinking
	 * 
	 * @param inputs
	 */
	public void setInput(double inputs[]) {
		for (int i = 0; i < this.inputLayer.size(); i++)
			this.inputLayer.get(i).setOutput(inputs[i]);
	} // end setInput()

	/**
	 * Get output learned from neural network thought
	 * 
	 * @return
	 */
	public double[] getOutput() {
		double[] outputs = new double[this.outputLayer.size()];
		for (int i = 0; i < this.outputLayer.size(); i++)
			outputs[i] = this.outputLayer.get(i).getOutput();
		return outputs;
	} // end getOutput()

	/**
	 * Feed Forward
	 */
	public void feedForward() {

		for (int n = 0; n < this.hiddenLayer.size(); n++) {
			Neuron neuron = this.hiddenLayer.get(n);
			neuron.computeOutput();
		} // end for

		for (int n = 0; n < this.outputLayer.size(); n++) {
			Neuron neuron = this.outputLayer.get(n);
			neuron.computeOutput();
		} // end for

	} // end activate()

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
		for (int i = 0; i < expectedOutput.length; i++) {
			double d = expectedOutput[i];
			if ((d < 0) || (d > 1)) {
				if (d < 0)
					expectedOutput[i] = this.epsilon;
				else
					expectedOutput[i] = 1.0 - this.epsilon;
			} // end if
		} // end for

		int i = 0;

		// update weights for the output layer
		for (int n = 0; n < this.outputLayer.size(); n++) {

			Neuron neuron = this.outputLayer.get(n);
			ArrayList<Connection> connections = neuron.getDendrites();

			for (int c = 0; c < connections.size(); c++) {
				Connection connect = connections.get(c);
				double ak = neuron.getOutput();
				double ai = connect.getLeftNeuron().getOutput();
				double desiredOutput = expectedOutput[i];
				double partialDeriv = -ak * (1.0 - ak) * ai
						* (desiredOutput - ak);
				double deltaWeight = -this.learningRate * partialDeriv;
				double weight = connect.getWeight() + deltaWeight;
				connect.setDeltaWeight(deltaWeight);
				connect.setWeight(weight + this.momentum
						* connect.getPrevDeltaWeight());
			} // end for

			i++;

		} // end for

		// update weights for the hidden layer
		for (int n = 0; n < this.hiddenLayer.size(); n++) {

			Neuron neuron = this.hiddenLayer.get(n);
			ArrayList<Connection> connections = neuron.getDendrites();

			for (int c = 0; c < connections.size(); c++) {

				Connection connect = connections.get(c);
				double aj = neuron.getOutput();
				double ai = connect.getLeftNeuron().getOutput();
				double sumKoutputs = 0;
				int j = 0;

				for (int n2 = 0; n2 < this.outputLayer.size(); n2++) {
					Neuron neuron2 = this.outputLayer.get(n2);
					double wjk = neuron2.getConnection(neuron.id).getWeight();
					double desiredOutput = (double) expectedOutput[j];
					double ak = neuron2.getOutput();
					j++;
					sumKoutputs += -(desiredOutput - ak) * ak * (1 - ak) * wjk;
				} // end for

				double partialDeriv = aj * (1 - aj) * ai * sumKoutputs;
				double deltaWeight = -this.learningRate * partialDeriv;
				double weight = connect.getWeight() + deltaWeight;
				connect.setDeltaWeight(deltaWeight);
				connect.setWeight(weight + this.momentum
						* connect.getPrevDeltaWeight());

			} // end for

		} // end for

	} // end backPropagate()

	public void run(int maxSteps, double minError, String fileName)
			throws Exception {

		int i;
		double error = 1;

		for (i = 0; (i < maxSteps) && (error > minError); i++) {

			error = 0;

			for (int p = 0; p < this.inputs.length; p++) {

				this.setInput(this.inputs[p]);
				this.feedForward();
				this.output = this.getOutput();
				this.resultOutputs[p] = this.output;

				for (int j = 0; j < this.expectedOutputs[p].length; j++) {
					double err = Math.pow(this.output[j]
							- this.expectedOutputs[p][j], 2.0);
					error += err;
				} // end for

				this.backPropagate(this.expectedOutputs[p]);

				this.saveWeights(fileName);

			} // end for

		} // end for

		this.isTrained = true;

		// this.printResult();

		if (i == maxSteps)
			System.err
					.println("Error: Neural Network training procedure has failed!");

	} // end if

	void printResult() {

		for (int p = 0; p < this.inputs.length; p++) {

			System.out.print("INPUTS: ");
			for (int x = 0; x < this.layers[0]; x++)
				System.out.print(this.inputs[p][x] + " ");

			System.out.print("EXPECTED: ");
			for (int x = 0; x < this.layers[2]; x++)
				System.out.print(this.expectedOutputs[p][x] + " ");

			System.out.print("ACTUAL: ");
			for (int x = 0; x < this.layers[2]; x++)
				System.out.print(this.resultOutputs[p][x] + " ");

			System.out.println();

		} // end for

		System.out.println();

	} // end printResult()

	@Override
	public String toString() {
		return "[NeuralNetwork \ninputLayer=" + this.inputLayer
				+ ", \nhiddenLayer=" + this.hiddenLayer + ", \noutputLayer="
				+ this.outputLayer + ", \nbias=" + this.bias + ", layers="
				+ Arrays.toString(this.layers) + ", epsilon=" + this.epsilon
				+ ", learningRate=" + this.learningRate + ", momentum="
				+ this.momentum + ", \ninputs="
				+ Arrays.deepToString(this.inputs) + ", \nexpectedOutputs="
				+ Arrays.deepToString(this.expectedOutputs)
				+ ", \nresultOutputs="
				+ Arrays.deepToString(this.resultOutputs) + ", \noutput="
				+ Arrays.toString(this.output) + ", \nisTrained="
				+ this.isTrained + "]";
	} // end toString()

	public void saveWeights(String fileName) throws Exception {

		ArrayList<Double> input = new ArrayList<Double>();
		Iterator<Neuron> it = this.inputLayer.iterator();
		while (it.hasNext()) {
			Neuron next = it.next();
			ArrayList<Connection> dendrites = next.getDendrites();
			Iterator<Connection> it2 = dendrites.iterator();
			while (it2.hasNext()) {
				Connection next2 = it2.next();
				double weight = next2.getWeight();
				input.add(weight);
			} // end while
		} // end while

		ArrayList<Double> hidden = new ArrayList<Double>();
		it = this.hiddenLayer.iterator();
		while (it.hasNext()) {
			Neuron next = it.next();
			ArrayList<Connection> dendrites = next.getDendrites();
			Iterator<Connection> it2 = dendrites.iterator();
			while (it2.hasNext()) {
				Connection next2 = it2.next();
				double weight = next2.getWeight();
				hidden.add(weight);
			} // end while
		} // end while

		ArrayList<Double> output = new ArrayList<Double>();
		it = this.outputLayer.iterator();
		while (it.hasNext()) {
			Neuron next = it.next();
			ArrayList<Connection> dendrites = next.getDendrites();
			Iterator<Connection> it2 = dendrites.iterator();
			while (it2.hasNext()) {
				Connection next2 = it2.next();
				double weight = next2.getWeight();
				output.add(weight);
			} // end while
		} // end while

		int numInput = this.layers[0];
		int numHidden = this.layers[1];
		int numOutput = this.layers[2];

		Iterator<Double> it1 = input.iterator();
		Iterator<Double> it2 = hidden.iterator();
		Iterator<Double> it3 = output.iterator();

		System.out.println("number of input layer weights: " + input.size());
		System.out.println("number of hidden layer weights: " + hidden.size());
		System.out.println("number of output layer weights: " + output.size());

		String data = "";

		while (it1.hasNext()) {
			double next = it1.next();
			data += next + " ";
		} // end while

		data += "\n";

		while (it2.hasNext()) {
			double next = it2.next();
			data += next + " ";
		} // end while

		data += "\n";

		while (it3.hasNext()) {
			double next = it3.next();
			data += next + " ";
		} // end while

		FileWriter fstream = new FileWriter(fileName);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(data + "\n");
		out.close();

	} // end saveWeights()

	public ArrayList<Double> layerWeights(ArrayList<Neuron> layer) {
		ArrayList<Double> weights = new ArrayList<Double>();
		Iterator<Neuron> it = this.inputLayer.iterator();
		while (it.hasNext()) {
			Neuron next = it.next();
			ArrayList<Connection> dendrites = next.getDendrites();
			Iterator<Connection> it2 = dendrites.iterator();
			while (it2.hasNext()) {
				Connection next2 = it2.next();
				double weight = next2.getWeight();
				weights.add(weight);
			} // end while
		} // end while
		return weights;
	} // end printLayers()

	public ArrayList<Neuron> getInputLayer() {
		return this.inputLayer;
	} // end getInputLayer()

	public void setInputLayer(ArrayList<Neuron> i) {
		this.inputLayer = i;
	} // end setInputLayer()

	public ArrayList<Neuron> getHiddenLayer() {
		return this.hiddenLayer;
	} // end getHiddenLayer()

	public void setHiddenLayer(ArrayList<Neuron> h) {
		this.hiddenLayer = h;
	} // end setHiddenLayer()

	public ArrayList<Neuron> getOutputLayer() {
		return this.outputLayer;
	} // end getOutputLayer()

	public void setOutputLayer(ArrayList<Neuron> o) {
		this.outputLayer = o;
	} // end setOutputLayer()

	public Neuron getBias() {
		return this.bias;
	} // end getBias()

	public void setBias(Neuron b) {
		this.bias = b;
	} // end setBias()

	public int[] getLayers() {
		return this.layers;
	} // end getLayers()

	public void setLayers(int[] l) {
		this.layers = l;
	} // end setLayers()

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

	public double[][] getInputs() {
		return this.inputs;
	} // end getInputs()

	public void setInputs(double[][] i) {
		this.inputs = i;
	} // end setInputs()

	public double[][] getExpectedOutputs() {
		return this.expectedOutputs;
	} // end getExpectedOutputs()

	public void setExpectedOutputs(double[][] e) {
		this.expectedOutputs = e;
	} // end setExpectedOutputs()

	public double[][] getResultOutputs() {
		return this.resultOutputs;
	} // end getResultOutputs()

	public void setResultOutputs(double[][] r) {
		this.resultOutputs = r;
	} // end setResultOutputs()

	public void setOutput(double[] o) {
		this.output = o;
	} // end setOutput()

	public boolean isTrained() {
		return this.isTrained;
	} // end isTrained()

	public void setTrained(boolean i) {
		this.isTrained = i;
	} // end setTrained()

} // end class