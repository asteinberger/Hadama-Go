import java.awt.Point;
import java.util.ArrayList;

/**
 * Move.java - Keep track of everything that changes during a move in the game.
 * 
 * This file is part of Hadama Go.
 * 
 * Hadama Go is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Hadama Go is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Hadama Go. If not, see http://www.gnu.org/licenses/.
 * 
 * @author Haoran Ma (mahaoran1020@gmail.com), Adam Steinberger
 *         (steinz08@gmail.com)
 */
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
		return "Move " + counter + "\n" + "[isPass: " + ispass + "]\n"
				+ "[Last Move: " + lastMove + "]\n" + "[Stone added: " + added
				+ "]\n" +

				"[Chain removed:\n"
				+ removedChain.toString().replaceAll("],", "]\n")

				+ "]";
	}

} // end class
