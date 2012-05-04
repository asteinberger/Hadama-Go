
import java.util.*;

public class Neuron {

	private Connection biasConnection;
	private final double bias = -1;
	private double output;
	private ArrayList<Connection> dendrites = new ArrayList<Connection>();
	private HashMap<Integer, Connection> dTable = new HashMap<Integer, Connection>();
	private static int counter = 0;
	public final int id; // auto increment, starts at 0

	public Neuron() {
		this.id = Neuron.counter;
		Neuron.counter++;
	} // end constructor

	public void computeOutput() {
		double s = 0;
		for (int i = 0; i < this.dendrites.size(); i++) {
			Connection connect = this.dendrites.get(i);
			Neuron fromNeuron = connect.getFromNeuron();
			double weight = connect.getWeight();
			double a = fromNeuron.getOutput(); // output from previous layer
			s += weight * a;
		} // end for
		s += this.biasConnection.getWeight() * this.bias;
		this.output = this.g(s);
	} // end

	double g(double x) {
		return this.sigmoid(x);
	} // end g()

	double sigmoid(double x) {
		return 1.0 / (1.0 + (Math.exp(-x)));
	} // end sigmoid()

	public void addDendrites(ArrayList<Neuron> in) {
		for (int i = 0; i < in.size(); i++) {
			Neuron neuron = in.get(i);
			Connection connect = new Connection(neuron, this);
			this.dendrites.add(connect);
			this.dTable.put(neuron.id, connect);
		} // end for
	} // end addDendrites()

	public Connection getConnection(int nIndex) {
		return this.dTable.get(nIndex);
	} // end getConnection()

	public void addInConnection(Connection c) {
		this.dendrites.add(c);
	} // end addInConnection()

	public void addBiasConnection(Neuron n) {
		Connection connect = new Connection(n, this);
		this.biasConnection = connect;
		this.dendrites.add(connect);
	} // end addBiasConnection()

	public ArrayList<Connection> getDendrites() {
		return this.dendrites;
	} // end getDendrites()

	public double getBias() {
		return this.bias;
	} // end getBias()

	public double getOutput() {
		return this.output;
	} // end getOutput()

	public void setOutput(double o) {
		this.output = o;
	} // end setOutput()

	public Connection getBiasConnection() {
		return this.biasConnection;
	} // end getBiasConnection()

	public void setBiasConnection(Connection b) {
		this.biasConnection = b;
	} // end setBiasConnection()

	public HashMap<Integer, Connection> getdTable() {
		return this.dTable;
	} // end getdTable()

	public void setdTable(HashMap<Integer, Connection> d) {
		this.dTable = d;
	} // end setdTable()

	public static int getCounter() {
		return Neuron.counter;
	} // end getCounter()

	public static void setCounter(int c) {
		Neuron.counter = c;
	} // end setCounter()

	public int getId() {
		return this.id;
	} // end getId()

	public void setDendrites(ArrayList<Connection> dendrites) {
		this.dendrites = dendrites;
	} // end setDendrites()

} // end class
