import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Think2 {

	public static void main(String[] args) throws IOException {

		double epsilon = 0.001;
		double learningRate = 0.9f;
		double momentum = 0.7f;

		FileInputStream fstream = new FileInputStream("trainData.txt");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		int lines = 0;
		String strLine;
		while ((strLine = br.readLine()) != null) {
			lines++;
		} // end while
		in.close();

		double[][] trainInput = new double[lines][162];
		double[][] trainOutput = new double[lines][162];
		fstream = new FileInputStream("trainData.txt");
		in = new DataInputStream(fstream);
		br = new BufferedReader(new InputStreamReader(in));
		int lineNum = 0;
		while ((strLine = br.readLine()) != null) {
			String[] data = strLine.split(" ");
			for (int i = 0; i < data[0].length(); i++) {
				String inputData = Character.toString(data[0].charAt(i));
				String outputData = Character.toString(data[1].charAt(i));
				trainInput[lineNum][i] = Integer.parseInt(inputData);
				trainOutput[lineNum][i] = Integer.parseInt(outputData);
			} // end for
			lineNum++;
		} // end while
		in.close();

		NeuralNetwork network = new NeuralNetwork(162, 20, 162);
		network.setupNetwork(epsilon, learningRate, momentum, trainInput,
				trainOutput);
		int maxRuns = 50000;
		double minError = 0.001;
		System.out.println("TRAINING");
		network.run(maxRuns, minError);
		System.out.println("TESTING");
		double[] in2 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		network.setInput(in2);
		network.feedForward();
		double[] out = network.getOutput();
		System.out.println(Arrays.toString(out));

	} // end main()

} // end class
