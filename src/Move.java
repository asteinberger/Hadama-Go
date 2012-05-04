import java.awt.Point;
import java.util.ArrayList;

public class Move {

	private Stone added;
	private ArrayList<Chain> removedChain;
	private int counter;
	private static int counterOfMove;
	private Point lastMove;
	private boolean ispass;
	private Point lastTiziPosition;
	private int lastNumTizi;

	public Move(Stone s, ArrayList<Chain> c, Point lm, boolean p, Point lp,
			int lntizi) {
		this.added = s;
		this.removedChain = c;
		this.lastMove = lm;
		this.ispass = p;
		this.lastTiziPosition = lp;
		this.lastNumTizi = lntizi;

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

	public Point getlastTiziPosition() {
		return this.lastTiziPosition;
	}

	public void setlastTiziPosition(Point lp) {
		this.lastTiziPosition = lp;
	}

	public int getlastNumTizi() {
		return this.lastNumTizi;
	}

	public int setlastNumTizi(int lntizi) {
		return this.lastNumTizi = lntizi;
	}

	public ArrayList<Chain> getRemovedChain() {
		return removedChain;
	}

	public void setRemovedChain(ArrayList<Chain> removedChain) {
		this.removedChain = removedChain;
	}

	public Point getlastMove() {
		return this.lastMove;
	}

	public void setlastMove(Point lm) {
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
		return "Move " + counter + ": " + "[isPass: " + ispass + ", "
				+ "lastTiziPosition: " + this.lastTiziPosition + ", "
				+ "lastNumTizi: " + this.lastNumTizi + ", " + "Last Move: "
				+ lastMove + "]\n" + "[Stone added: " + added + "]\n"
				+ "[Chain removed:\n"
				+ removedChain.toString().replaceAll("],", "]\n") + "]";
	}

} // end class
