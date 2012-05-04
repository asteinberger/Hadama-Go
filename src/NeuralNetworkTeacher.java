import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.*;

public class NeuralNetworkTeacher extends JFrame implements ActionListener {

	/**
	 * Version ID required for Hadama Go.
	 */
	private static final long serialVersionUID = 4781863948394812333L;

	private JComboBox[][] inputStates = new JComboBox[9][9];
	private JComboBox[][] outputStates = new JComboBox[9][9];
	private HashMap<JComboBox, String> inputTable = new HashMap<JComboBox, String>();
	private HashMap<JComboBox, String> outputTable = new HashMap<JComboBox, String>();
	private TeachListener tListen = new TeachListener(this);

	public NeuralNetworkTeacher() {
		initUI();
	} // end constructor

	public final void initUI() {

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		JMenuItem menuItem = new JMenuItem("Teach to Network", KeyEvent.VK_T);
		menuItem.addActionListener(tListen);
		menu.add(menuItem);

		JPanel panel = new JPanel();
		JPanel inputPanel = new JPanel();
		JPanel outputPanel = new JPanel();

		this.setJMenuBar(menuBar);
		getContentPane().add(panel);
		panel.add(inputPanel);
		panel.add(outputPanel);

		String[] states = { "-", "B", "W" };

		panel.setLayout(new GridLayout(1, 2));
		inputPanel.setLayout(new GridLayout(9, 9));
		outputPanel.setLayout(new GridLayout(9, 9));

		inputPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		outputPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		for (int row = 0; row < 9; row++) {

			for (int col = 0; col < 9; col++) {

				this.inputStates[row][col] = new JComboBox(states);
				this.outputStates[row][col] = new JComboBox(states);

				this.inputStates[row][col].addActionListener(this);
				this.outputStates[row][col].addActionListener(this);

				inputPanel.add(this.inputStates[row][col]);
				outputPanel.add(this.outputStates[row][col]);

				this.inputTable.put(this.inputStates[row][col], "-");
				this.outputTable.put(this.outputStates[row][col], "-");

			} // end for

		} // end for

		setTitle("Neural Network Teacher");
		setSize(1200, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	} // end initUI()

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NeuralNetworkTeacher teacher = new NeuralNetworkTeacher();
				teacher.setVisible(true);
			} // end run()
		});
	} // end main()

	public void printStates() {

		System.out.println("===========================");

		for (int row = 0; row < 9; row++) {

			for (int col = 0; col < 9; col++) {
				JComboBox input = this.inputStates[row][col];
				String in = this.inputTable.get(input);
				System.out.print(in + " ");
			} // end for

			System.out.print("-> ");

			for (int col = 0; col < 9; col++) {
				JComboBox output = this.outputStates[row][col];
				String out = this.outputTable.get(output);
				System.out.print(out + " ");
			} // end for

			System.out.println();

		} // end for

		System.out.println("===========================");

	} // end printStates()

	public String getCodes() {

		String codes = "";

		HashMap<String, String> sCode = new HashMap<String, String>();
		sCode.put("-", "00");
		sCode.put("B", "01");
		sCode.put("W", "10");

		for (int row = 0; row < 9; row++) {

			for (int col = 0; col < 9; col++) {
				JComboBox input = this.inputStates[row][col];
				String in = this.inputTable.get(input);
				String code = sCode.get(in);
				codes += code;
			} // end for

		} // end for

		codes += " ";

		for (int row = 0; row < 9; row++) {

			for (int col = 0; col < 9; col++) {
				JComboBox output = this.outputStates[row][col];
				String out = this.outputTable.get(output);
				String code = sCode.get(out);
				codes += code;
			} // end for

		} // end for

		return codes;

	} // end getCodes()

	public class TeachListener implements ActionListener {

		public NeuralNetworkTeacher teacher;

		public TeachListener(NeuralNetworkTeacher t) {
			this.teacher = t;
		} // end constructor

		@Override
		public void actionPerformed(ActionEvent ae) {
			JMenuItem source = (JMenuItem) (ae.getSource());
			String command = source.getText();
			System.out.println(command);
			System.out.println(this.teacher.getCodes());
			try {
				FileWriter fstream = new FileWriter("trainData.txt", true);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(this.teacher.getCodes() + "\n");
				out.close();
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			} // end try
		} // end actionPerformed()

		public NeuralNetworkTeacher getTeacher() {
			return this.teacher;
		} // end getTeacher()

		public void setTeacher(NeuralNetworkTeacher t) {
			this.teacher = t;
		} // end setTeacher()

	} // end sub-class

	@Override
	public void actionPerformed(ActionEvent ae) {
		JComboBox cb = (JComboBox) ae.getSource();
		String state = (String) cb.getSelectedItem();
		if (this.inputTable.containsKey(cb))
			this.inputTable.put(cb, state);
		else
			this.outputTable.put(cb, state);
		this.printStates();
		System.out.println(this.getCodes());
	} // end actionPerformed()

	public JComboBox[][] getInputStates() {
		return inputStates;
	}

	public void setInputStates(JComboBox[][] inputStates) {
		this.inputStates = inputStates;
	}

	public JComboBox[][] getOutputStates() {
		return outputStates;
	}

	public void setOutputStates(JComboBox[][] outputStates) {
		this.outputStates = outputStates;
	}

	public HashMap<JComboBox, String> getInputTable() {
		return inputTable;
	}

	public void setInputTable(HashMap<JComboBox, String> inputTable) {
		this.inputTable = inputTable;
	}

	public HashMap<JComboBox, String> getOutputTable() {
		return outputTable;
	}

	public void setOutputTable(HashMap<JComboBox, String> outputTable) {
		this.outputTable = outputTable;
	}

	public TeachListener gettListen() {
		return tListen;
	}

	public void settListen(TeachListener tListen) {
		this.tListen = tListen;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

} // end class
