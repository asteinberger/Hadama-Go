import java.awt.Point;
import java.util.ArrayList;

public class Move {

	private Stone addedStone;
	private ArrayList<Chain> removedChains;
	private int counter;
	private static int counterOfMoves;
	private Point lastMove;
	private boolean thisMoveIsAPass;
	private Point lastTiziPosition;
	private int lastTiziNumber;

	public Move(Stone addedStone,
				ArrayList<Chain> removedChains,
				Point lastMove,
				boolean thisMoveIsAPass,
				Point lastTiziPosition,
				int lastTiziNumber) {
		this.addedStone = addedStone;
		this.removedChains = removedChains;
		this.lastMove = lastMove;
		this.thisMoveIsAPass = thisMoveIsAPass;
		this.lastTiziPosition = lastTiziPosition;
		this.lastTiziNumber = lastTiziNumber;
		this.counter = counterOfMoves;
		this.counterOfMoves++;
	} // end constructor

	public boolean getThisMoveIsAPass() {
		return this.thisMoveIsAPass;
	}

	public Stone getAddedStone() {
		return this.addedStone;
	}

	public Point getlastTiziPosition() {
		return this.lastTiziPosition;
	}

	public int getlastTiziNumber() {
		return this.lastTiziNumber;
	}

	public ArrayList<Chain> getRemovedChains() {
		return removedChains;
	}

	public Point getLastMove() {
		return this.lastMove;
	}

	@Override
	public String toString() {
		return "Move " + counter + ": " + "[isPass: " + thisMoveIsAPass + ", "
				+ "lastTiziPosition: " + this.lastTiziPosition + ", "
				+ "lastNumberTizi: " + this.lastTiziNumber + ", " + "Last Move: "
				+ lastMove + "]\n" + "[Stone added: " + addedStone + "]\n"
				+ "[Chain removed:\n"
				+ removedChains.toString().replaceAll("],", "]\n") + "]";
	}

} // end class
