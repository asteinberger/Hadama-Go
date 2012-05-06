import java.util.*;

public class Think {

	public static void main(String[] args) throws Exception {

		// NeuralNetwork network = new NeuralNetwork(2, 4, 1);

		NeuralNetwork network = new NeuralNetwork(2, 4, 1, "think.txt");

		// double[][] inputs = { { 1, 1 }, { 1, 0 }, { 0, 1 }, { 0, 0 } };
		// network.setInputs(inputs);
		//
		// double[][] outputs = { { 0 }, { 1 }, { 1 }, { 0 } };
		// network.setExpectedOutputs(outputs);

		// System.out.println("Network Before Thinking:");
		// System.out.println(network);
		// System.out.println(" ");

		// int maxRuns = 50000;
		// double minError = 0.001;
		// network.run(maxRuns, minError, "think.txt");

		System.out.println("Network After Thinking:");
		System.out.println(network);
		System.out.println(" ");

		System.out.println("Test 0 XOR 0");
		double[] in = { 0, 0 };
		network.setInput(in);
		network.feedForward();
		double[] out = network.getOutput();
		System.out.println(Arrays.toString(out));
		System.out.println(" ");

		System.out.println("Test 0 XOR 1");
		in[1] = 1;
		network.setInput(in);
		network.feedForward();
		out = network.getOutput();
		System.out.println(Arrays.toString(out));
		System.out.println(" ");

		System.out.println("Test 1 XOR 0");
		in[0] = 1;
		in[1] = 0;
		network.setInput(in);
		network.feedForward();
		out = network.getOutput();
		System.out.println(Arrays.toString(out));
		System.out.println(" ");

		System.out.println("Test 1 XOR 1");
		in[1] = 1;
		network.setInput(in);
		network.feedForward();
		out = network.getOutput();
		System.out.println(Arrays.toString(out));
		System.out.println(" ");

	} // end main()

} // end class
