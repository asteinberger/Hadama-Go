import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.*;

public class GoTrainer extends JFrame implements ActionListener {

	/**
	 * Version ID required for Hadama Go.
	 */
	private static final long serialVersionUID = 4781863948394812333L;
	private String[] inputTypes = { "-", "black", "white" };
	private String[] outputTypes = { "black wins", "tie game", "white wins" };
	private HashMap<String, String> inputMap = new HashMap<String, String>();
	private HashMap<String, String> outputMap = new HashMap<String, String>();
	private JButton[][] inputStates = new JButton[9][9];
	private JButton outputState;
	private String outputCode = "11";
	private HashMap<JButton, String> inputTable = new HashMap<JButton, String>();
	private JButton teachTheNeuralNetwork = new JButton("add to training set");

	public GoTrainer() {
		this.inputMap.put(this.inputTypes[0], "00");
		this.inputMap.put(this.inputTypes[1], "01");
		this.inputMap.put(this.inputTypes[2], "10");
		this.outputMap.put(this.outputTypes[0], "00");
		this.outputMap.put(this.outputTypes[1], "01");
		this.outputMap.put(this.outputTypes[2], "10");
		initUI();
	} // end constructor

	public final void initUI() {

		JPanel panel = new JPanel();
		JPanel inputPanel = new JPanel();
		JPanel outputPanel = new JPanel();

		getContentPane().add(panel);
		panel.setLayout(new BorderLayout());
		inputPanel.setLayout(new GridLayout(9, 9));
		outputPanel.setLayout(new FlowLayout());

		panel.add(inputPanel, BorderLayout.CENTER);
		panel.add(outputPanel, BorderLayout.SOUTH);

		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				this.inputStates[row][column] = new JButton(this.inputTypes[0]);
				this.inputStates[row][column].addActionListener(this);
				inputPanel.add(this.inputStates[row][column]);
				this.inputTable.put(this.inputStates[row][column],
						this.inputTypes[0]);
			} // end for
		} // end for

		this.outputState = new JButton(this.outputTypes[0]);
		this.outputState.addActionListener(this);
		outputPanel.add(this.outputState);

		this.teachTheNeuralNetwork.addActionListener(this);
		outputPanel.add(this.teachTheNeuralNetwork);

		setTitle("Go Neural Network Trainer");
		setSize(600, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	} // end initUI()

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GoTrainer teacher = new GoTrainer();
				teacher.setVisible(true);
			} // end run()
		});
	} // end main()

	public String getCodes() {

		String codes = "";

		for (int row = 0; row < 9; row++) {

			for (int column = 0; column < 9; column++) {
				JButton inputState = this.inputStates[row][column];
				String input = this.inputTable.get(inputState);
				String code = this.inputMap.get(input);
				codes += code;
			} // end for

		} // end for

		codes += " ";

		JButton outputState = this.outputState;
		String output = outputState.getText();
		String code = this.outputMap.get(output);

		this.outputCode = code;

		codes += code;

		return codes;

	} // end getCodes()

	@Override
	public void actionPerformed(ActionEvent actionEvent) {

		JButton button = (JButton) actionEvent.getSource();
		String state = button.getText();

		if (state.equals(this.inputTypes[0])) {
			state = this.inputTypes[1];
		} else if (state.equals(this.inputTypes[1])) {
			state = this.inputTypes[2];
		} else if (state.equals(this.inputTypes[2])) {
			state = this.inputTypes[0];
		} else if (state.equals(this.outputTypes[0])) {
			state = this.outputTypes[1];
		} else if (state.equals(this.outputTypes[1])) {
			state = this.outputTypes[2];
		} else if (state.equals(this.outputTypes[2])) {
			state = this.outputTypes[0];
		} // end if

		if (state.equals("add to training set")) {

			try {
				FileWriter fileWriter = new FileWriter("trainData.txt", true);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(this.getCodes() + "\n");
				bufferedWriter.close();
			} catch (IOException ioException) {
				System.err.println("Error: " + ioException.getMessage());
			} // end try

		} else {

			if (this.inputTable.containsKey(button))
				this.inputTable.put(button, state);
			else
				this.outputCode = this.outputMap.get(state);

			button.setText(state);

		} // end if

	} // end actionPerformed()

} // end class
