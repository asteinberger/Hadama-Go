
import java.util.Arrays;

public class Think {

	public static void main(String[] args) {
		double epsilon = 0.00000000001;
		double learningRate = 0.9f;
		double momentum = 0.7f;
		double[][] trainInput = { { 1, 1 }, { 1, 0 }, { 0, 1 }, { 0, 0 } };
		double[][] trainOutput = { { 0 }, { 1 }, { 1 }, { 0 } };
		NeuralNetwork network = new NeuralNetwork(2, 4, 1);
		network.setupNetwork(epsilon, learningRate, momentum, trainInput,
				trainOutput);
		int maxRuns = 50000;
		double minError = 0.001;
		network.run(maxRuns, minError);
		System.out.println("Test 0 XOR 1");
		double[] in = { 0, 1 };
		network.setInput(in);
		network.feedForward();
		double[] out = network.getOutput();
		System.out.println(Arrays.toString(out));
		System.out.println(" ");
		System.out.println("Test 0 XOR 0");
		in[1] = 0;
		network.setInput(in);
		network.feedForward();
		out = network.getOutput();
		System.out.println(Arrays.toString(out));
	}
	
	
	
}
