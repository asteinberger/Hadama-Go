import java.awt.*;
import java.util.Iterator;
import java.util.TreeSet;

public class Wei extends TreeSet<Stone> {
	/**
	 * Version ID required for ArrayLists.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Qis is an ArrayList of Points whose coordinates are those of the goboard
	 * position of the chain's liberties. Liberties of a chain are positions on
	 * the goboard that are horizontally or vertically adjacent to the chain and
	 * are not occupied by an active stone.
	 */

	private GoBoard goBoard;
	private int weiNumber;
	static int counterOfWei;

	public Wei(GoBoard goBoard) {
		this.goBoard = goBoard;
		weiNumber = counterOfWei;
		counterOfWei++;

	} // end constructor

	public void addToWei(Stone stone) {
		// have to be the same color stone
		if (!this.contains(stone)) {
			stone.setWei(this);
			this.add(stone);
		} // end if
	} // end addToWei()

	public void updateWeis(Stone currentStone) {
		// remove the stone not belong to the chain anymore
		Wei wei = this;

		Iterator<Stone> iterator = this.iterator();
		while (iterator.hasNext()) {
			Stone stone = iterator.next();
			Point point = stone.getLocation();
			this.goBoard.getBoard()[point.x][point.y] = null;
			this.goBoard.getChains().remove(stone.getChain());
		}

		this.goBoard.getWeis().remove(this);

		// Add back all stones back to board
		wei.remove(currentStone);
		iterator = wei.iterator();
		while (iterator.hasNext()) {
			Stone stone = iterator.next();
			this.goBoard.addStone(stone);
		}

	} // end checkQis()

	public void yanDetector() {
		int xMinimum;
		int xMaximum;
		int yMinimum = Integer.MAX_VALUE;
		int yMaximum = Integer.MIN_VALUE;
		Player player = this.first().getPlayer();
		xMinimum = this.first().getLocation().x;
		xMaximum = this.last().getLocation().x;

		Iterator<Stone> yIterator = this.iterator();
		while (yIterator.hasNext()) {
			Stone stone = yIterator.next();
			int y = stone.getLocation().y;
			if (y >= yMaximum) {
				yMaximum = y;
			}
			if (y <= yMinimum) {
				yMinimum = y;
			}
		}

		if (xMaximum == 8)
			xMaximum++;
		if (yMaximum == 8)
			yMaximum++;
		if (xMinimum == 0)
			xMinimum--;
		if (yMinimum == 0)
			yMinimum--;

		for (int yIndex = yMinimum + 1; yIndex < yMaximum; yIndex++) {

			for (int xIndex = xMinimum + 1; xIndex < xMaximum; xIndex++) {

				Point point = new Point(xIndex, yIndex);
				Stone stone = this.goBoard.getStone(point);

				if ((stone.getPlayer() != Player.BLACK) && (stone.getPlayer() != Player.WHITE)
						&& (stone.getPlayer() != Player.BLACK_TERRITORY) && (stone.getPlayer() != Player.WHITE_TERRITORY)) {
					// if chain is empty,add it no matter what
					// if chain is not empty, check if it already became chain
					if (stone.getChain() != null) {
						if (!stone.getChain().isYan()) {
							if (player == Player.WHITE) {
								stone.setPlayer(Player.WHITE_TERRITORY);
								this.goBoard.addStone(stone);
							} else {
								stone.setPlayer(Player.BLACK_TERRITORY);
								this.goBoard.addStone(stone);
							}
						}
					} else {
						if (player == Player.WHITE) {
							stone.setPlayer(Player.WHITE_TERRITORY);
							this.goBoard.addStone(stone);
						} else {
							stone.setPlayer(Player.BLACK_TERRITORY);
							this.goBoard.addStone(stone);
						}
					}
				}
			}
		}
	}

	public int getWeiIndex() {
		return weiNumber;
	}
} // end class
