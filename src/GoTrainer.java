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
	private JButton teachNetwork = new JButton("add to training set");

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
			for (int col = 0; col < 9; col++) {
				this.inputStates[row][col] = new JButton(this.inputTypes[0]);
				this.inputStates[row][col].addActionListener(this);
				inputPanel.add(this.inputStates[row][col]);
				this.inputTable.put(this.inputStates[row][col],
						this.inputTypes[0]);
			} // end for
		} // end for

		this.outputState = new JButton(this.outputTypes[0]);
		this.outputState.addActionListener(this);
		outputPanel.add(this.outputState);

		this.teachNetwork.addActionListener(this);
		outputPanel.add(this.teachNetwork);

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

			for (int col = 0; col < 9; col++) {
				JButton input = this.inputStates[row][col];
				String in = this.inputTable.get(input);
				String code = this.inputMap.get(in);
				codes += code;
			} // end for

		} // end for

		codes += " ";

		JButton output = this.outputState;
		String out = output.getText();
		String code = this.outputMap.get(out);

		this.outputCode = code;

		codes += code;

		return codes;

	} // end getCodes()

	@Override
	public void actionPerformed(ActionEvent ae) {

		JButton button = (JButton) ae.getSource();
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
				FileWriter fstream = new FileWriter("trainData.txt", true);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(this.getCodes() + "\n");
				out.close();
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			} // end try

		} else {

			if (this.inputTable.containsKey(button))
				this.inputTable.put(button, state);
			else
				this.outputCode = this.outputMap.get(state);

			button.setText(state);

		} // end if

	} // end actionPerformed()

	public JButton[][] getInputStates() {
		return this.inputStates;
	} // end getInputStates()

	public void setInputStates(JButton[][] i) {
		this.inputStates = i;
	} // end setInputStates()

	public HashMap<JButton, String> getInputTable() {
		return this.inputTable;
	} // end getInputTable()

	public void setInputTable(HashMap<JButton, String> i) {
		this.inputTable = i;
	} // end setInputTable()

	public String getOutputCode() {
		return this.outputCode;
	} // end getOutputCode()

	public void setOutputCode(String c) {
		this.outputCode = c;
	} // end setOutputCode()

	public static long getSerialversionuid() {
		return serialVersionUID;
	} // end getSerialversionuid()

	public JButton getTeachNetwork() {
		return this.teachNetwork;
	} // end getTeachNetwork()

	public void setTeachNetwork(JButton t) {
		this.teachNetwork = t;
	} // end setTeachNetwork()

	public String[] getOutputTypes() {
		return this.outputTypes;
	} // end getOutputTypes()

	public void setOutputTypes(String[] o) {
		this.outputTypes = o;
	} // end setOutputTypes()

	public String[] getInputTypes() {
		return this.inputTypes;
	} // end getInputTypes()

	public void setInputTypes(String[] i) {
		this.inputTypes = i;
	} // end setInputTypes()

	public HashMap<String, String> getInputMap() {
		return inputMap;
	}

	public void setInputMap(HashMap<String, String> inputMap) {
		this.inputMap = inputMap;
	}

	public HashMap<String, String> getOutputMap() {
		return outputMap;
	}

	public void setOutputMap(HashMap<String, String> outputMap) {
		this.outputMap = outputMap;
	}

	public JButton getOutputState() {
		return outputState;
	}

	public void setOutputState(JButton outputState) {
		this.outputState = outputState;
	}

} // end class
