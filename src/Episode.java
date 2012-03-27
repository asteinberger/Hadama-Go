import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * Episode.java - Keeps track of discrete sequences of stone interactions on the
 * go game board.
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
public class Episode {

	private ArrayList<Point> bestMoves = new ArrayList<Point>();

	public Episode() {
	} // end constructor

	public Episode(Point p) {
		this.bestMoves.add(p);
	} // end constructor

	public void add(Point p) {
		this.bestMoves.add(p);
	} // end add()

	public void remove(Point p) {
		this.bestMoves.remove(p);
	} // end remove()

	public void checkBestMoves(Board b) {
		ArrayList<Point> toRemove = new ArrayList<Point>();
		for (int i = 0; i < this.bestMoves.size(); i++) {
			int color = b.getStone(this.bestMoves.get(i)).getColor();
			if ((color == 0) || (color == 1))
				toRemove.add(this.bestMoves.get(i));
		} // end for
		for (int i = 0; i < toRemove.size(); i++)
			this.bestMoves.remove(toRemove.get(i));
	} // end checkBestMoves()

	public Point randomMove() {
		Random rand = new Random(System.currentTimeMillis());
		int index = rand.nextInt(this.bestMoves.size());
		Point p = this.bestMoves.get(index);
		this.bestMoves.remove(index);
		return p;
	} // end randomMove()

	public ArrayList<Point> getBestMoves() {
		return this.bestMoves;
	} // end getBestMoves()

	public void setBestMoves(ArrayList<Point> bestMoves) {
		this.bestMoves = bestMoves;
	} // end setBestMoves()

	@Override
	public String toString() {
		return "Episode [bestMoves=" + bestMoves + "]";
	}

} // end class
