import java.awt.Point;
import java.util.ArrayList;

public class Move {

	private Stone added;
	private ArrayList<Chain> removedChain;
	private int counter;
	private static int counterOfMove;
	private Point lastMove;
	private boolean ispass;

	public Move(Stone s, ArrayList<Chain> c, Point lm, boolean p) {
		this.added = s;
		this.removedChain = c;
		this.lastMove = lm;
		this.ispass = p;

		counter = counterOfMove;
		counterOfMove++;
	} // end constructor

	public boolean getisPass() {
		return this.ispass;
	}

	public void setisPass(boolean ip) {
		this.ispass = ip;
	}
	
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

	public Point getlastMove(){
		return this.lastMove;
	}
	
	public void setlastMove(Point lm){
		this.lastMove = lm;
	}
	
	public int getCounter() {
		return counter;
	} // end getCounter()

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	@Override
	public String toString() {
		return 
		"Move " + counter + "\n" + 
		"[isPass: " + ispass + "]\n" +
		"[Last Move: " + lastMove + "]\n" +
		"[Stone added: " + added + "]\n" +
		
		"[Chain removed:\n" + removedChain.toString().replaceAll("],", "]\n")
		
		+ "]" ;
	}

} // end class
