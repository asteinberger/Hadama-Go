import java.awt.Point;
import java.util.ArrayList;

public class Move {

	private Stone added;
	private ArrayList<Chain> removedChain;
	private int counter;
	private static int counterOfMove;
	private Point lastMove;
	private boolean isPass;
	private Point lastTiziPosition;
	private int lastNumberTizi;

	public Move(Stone stone,
				ArrayList<Chain> chains,
				Point lastMove,
				boolean isPass,
				Point lastTiziPosition,
				int lastNumberTizi) {
		this.added = stone;
		this.removedChain = chains;
		this.lastMove = lastMove;
		this.isPass = isPass;
		this.lastTiziPosition = lastTiziPosition;
		this.lastNumberTizi = lastNumberTizi;
		this.counter = counterOfMove;
		this.counterOfMove++;
	} // end constructor

	public boolean getisPass() {
		return this.isPass;
	}

	public Stone getAddedStone() {
		return this.added;
	}

	public Point getlastTiziPosition() {
		return this.lastTiziPosition;
	}

	public int getlastNumTizi() {
		return this.lastNumberTizi;
	}

	public ArrayList<Chain> getRemovedChain() {
		return removedChain;
	}

	public Point getlastMove() {
		return this.lastMove;
	}

	@Override
	public String toString() {
		return "Move " + counter + ": " + "[isPass: " + isPass + ", "
				+ "lastTiziPosition: " + this.lastTiziPosition + ", "
				+ "lastNumberTizi: " + this.lastNumberTizi + ", " + "Last Move: "
				+ lastMove + "]\n" + "[Stone added: " + added + "]\n"
				+ "[Chain removed:\n"
				+ removedChain.toString().replaceAll("],", "]\n") + "]";
	}

} // end class
