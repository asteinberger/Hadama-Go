import java.util.ArrayList;

public class Move {

	private Stone added;
	private ArrayList<Chain> removedChains;
	private ArrayList<Wei> removedWeis;

	public Move(Stone a) {
		this.added = a;
	} // end constructor

	public Move() {
		// TODO Auto-generated constructor stub
	}

	public Stone getAdded() {
		return this.added;
	} // end getAdded()

	public void setAdded(Stone added) {
		this.added = added;
	} // end setAdded()

	public ArrayList<Chain> getRemovedChains() {
		return this.removedChains;
	} // end getRemovedChains()

	public void setRemovedChains(ArrayList<Chain> removedChains) {
		this.removedChains = removedChains;
	} // end setRemovedChains()

	public ArrayList<Wei> getRemovedWeis() {
		return this.removedWeis;
	} // end getRemovedWeis()

	public void setRemovedWeis(ArrayList<Wei> removedWeis) {
		this.removedWeis = removedWeis;
	} // end setRemovedWeis()

} // end class
