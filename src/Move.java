import java.util.ArrayList;

public class Move {

	private Stone added;
	private ArrayList<Chain> removedChain;
	private int counter;
	private static int counterOfMove;

	public Move(Stone s, ArrayList<Chain> c) {
		this.added = s;
		this.removedChain = c;

		counter = counterOfMove;
		counterOfMove++;
	} // end constructor

	public Stone getAddedStone() {
		return this.added;
	}

	public void setAddedStone(Stone added) {
		this.added = added;
	}

	public ArrayList<Chain> getRemovedChain() {
		return removedChain;
	}

	public void setRemovedChain(ArrayList<Chain> removedChain) {
		this.removedChain = removedChain;
	}

	public int getCounter() {
		return counter;
	} // end getCounter()

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	@Override
	public String toString() {
		return "Move " + counter + "\n" + 
		"[Stone added: " + added + "]\n" +
		
		"[Chain removed:\n" + removedChain.toString().replaceAll("],", "]\n")
		
		+ "]" ;
	}

} // end class
