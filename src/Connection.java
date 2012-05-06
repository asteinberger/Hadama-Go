public class Connection {

	private double weight = 0;
	private double prevDeltaWeight = 0;
	private double deltaWeight = 0;
	private final Neuron fromNeuron;
	private final Neuron toNeuron;
	private static int counter = 0;
	public final int id; // auto increment, starts at 0

	public Connection(Neuron from, Neuron to) {
		this.fromNeuron = from;
		this.toNeuron = to;
		id = counter;
		counter++;
	} // end constructor

	@Override
	public String toString() {
		return "[Connection weight=" + this.weight + ", prevDeltaWeight="
				+ this.prevDeltaWeight + ", deltaWeight=" + this.deltaWeight
				+ ", fromNeuronID=" + this.fromNeuron.id + ", toNeuronID="
				+ this.toNeuron.id + ", id=" + this.id + "]";
	} // end toString()

	public double getWeight() {
		return this.weight;
	} // end getWeight()

	public void setWeight(double w) {
		this.weight = w;
	} // end setWeight()

	public void setDeltaWeight(double w) {
		this.prevDeltaWeight = this.deltaWeight;
		this.deltaWeight = w;
	} // end setDeltaWeight()

	public double getPrevDeltaWeight() {
		return this.prevDeltaWeight;
	} // end getPrevDeltaWeight()

	public Neuron getFromNeuron() {
		return this.fromNeuron;
	} // end getFromNeuron()

	public Neuron getToNeuron() {
		return this.toNeuron;
	} // end getToNeuron()

	public static int getCounter() {
		return Connection.counter;
	} // end getCounter()

	public static void setCounter(int c) {
		Connection.counter = c;
	} // end setCounter()

	public double getDeltaWeight() {
		return this.deltaWeight;
	} // end getDeltaWeight()

	public Neuron getLeftNeuron() {
		return this.fromNeuron;
	} // end getLeftNeuron()

	public Neuron getRightNeuron() {
		return this.toNeuron;
	} // end getRightNeuron()

	public int getId() {
		return this.id;
	} // end getId()

	public void setPrevDeltaWeight(double p) {
		this.prevDeltaWeight = p;
	} // end setPrevDeltaWeight()

} // end class
