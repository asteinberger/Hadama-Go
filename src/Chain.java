import java.awt.*;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Head Node. Tail Node. Check Qis in order: top, right, bottom, left. If you
 * add stone of same color horiz/vert adjacent to chain, have tail of chain
 * point to new stone and set new stone as tail. If chain of same color
 * horiz/vert adjacent to another chain, tail of first chain links to head of
 * second chain, then we add all nodes of second chain to first chain.
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <adam@akmaz.io>
 * 
 */

public class Chain extends TreeSet<Stone> {
	/**
	 * Version ID required for Hadama Go.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Qis is an ArrayList of Points whose coordinates are those of the goboard
	 * position of the chain's liberties. Liberties of a chain are positions on
	 * the goboard that are horizontally or vertically adjacent to the chain and
	 * are not occupied by an active stone.
	 */
	private TreeSet<Qi> qis = new TreeSet<Qi>();
	private int boardSize;
	private GoBoard goBoard;
	private boolean isYan = false;
	private boolean isZhenYan = false;
	private boolean isJiaYan = false;
	/**
	 * chainNum gives each chain a unique number id.
	 */
	private int chainIndex;
	static int counterOfChains;

	/**
	 * Chain constructor. When two stones of the same color are adjacent
	 * horizontally or vertically, they make a chain. The Chain class is an
	 * ArrayList of Stones that form a chain. In Board.java, addStone() will
	 * create a chain for a single stone if that stone is placed on the goboard
	 * and has all four liberties.
	 */
	public Chain(GoBoard goBoard) {
		boardSize = goBoard.getBoardSize();
		this.goBoard = goBoard;
		chainIndex = counterOfChains;
		counterOfChains++;

	} // end constructor

	/**
	 * Add a stone to the chain. This only works if the stone being added is the
	 * same color as the rest of the stones in the chain.
	 * 
	 * @param stone
	 *            stone to add to chain
	 */
	public void addAStoneToChain(Stone stone) {

		// have to be the same color stone
		if (!this.contains(stone)) {
			Point point = stone.getLocation();
			stone.setChain(this);
			this.add(stone);

			Point top = new Point(point.x, point.y + 1);
			Point right = new Point(point.x + 1, point.y);
			Point bottom = new Point(point.x, point.y - 1);
			Point left = new Point(point.x - 1, point.y);

			Qi topQi = new Qi(top);
			Qi rightQi = new Qi(right);
			Qi bottomQi = new Qi(bottom);
			Qi leftQi = new Qi(left);
			Qi currentQi = new Qi(point);

			Stone topStone = new Stone(top);
			Stone rightStone = new Stone(right);
			Stone bottomStone = new Stone(bottom);
			Stone leftStone = new Stone(left);

			if (top.y < this.boardSize)
				topStone = this.goBoard.getStone(top);

			if (right.x < this.boardSize)
				rightStone = this.goBoard.getStone(right);

			if (bottom.y > -1)
				bottomStone = this.goBoard.getStone(bottom);

			if (left.x > -1)
				leftStone = this.goBoard.getStone(left);

			Stone[] neighboringStones = { topStone, rightStone, bottomStone, leftStone };
			Qi[] neighboringQis = { topQi, rightQi, bottomQi, leftQi };
			int[] leftCompare = { point.y, point.x, 0, 0 };
			int[] rightCompare = { this.boardSize - 1, this.boardSize - 1, point.y, point.x };

			this.qis.remove(currentQi);

			for (int index = 0; index < 4; index++) {

				if ((neighboringStones[index].getPlayer() == Player.NOT_A_PLAYER)
						&& (!this.qis.contains(neighboringQis[index]))
						&& (leftCompare[index] < rightCompare[index])) {
					this.qis.add(neighboringQis[index]);
				} else {

					if (neighboringStones[index].getChain() != null) {
						TreeSet<Qi> qis = neighboringStones[index].getChain().getQis();
						if (stone.getPlayer() != Player.BLACK_TERRITORY && stone.getPlayer() != Player.WHITE_TERRITORY) {
							qis.remove(currentQi);
						}
						neighboringStones[index].getChain().setQis(qis);
					}
				}
			}

			// if the adding stone's color is 1 or 2
			// then trade color 3 and 4 as a empty space
			if (stone.getPlayer() == Player.BLACK || stone.getPlayer() == Player.WHITE) {

				if ((topStone.getPlayer() == Player.BLACK_TERRITORY || topStone.getPlayer() == Player.WHITE_TERRITORY)
						&& (!this.qis.contains(topQi)) && (point.y < this.boardSize - 1)) {
					this.qis.add(topQi);
				}

				if ((rightStone.getPlayer() == Player.BLACK_TERRITORY || rightStone.getPlayer() == Player.WHITE_TERRITORY)
						&& (!this.qis.contains(rightQi))
						&& (point.x < this.boardSize - 1)) {
					this.qis.add(rightQi);
				}

				if ((bottomStone.getPlayer() == Player.BLACK_TERRITORY || bottomStone.getPlayer() == Player.WHITE_TERRITORY)
						&& (!this.qis.contains(bottomQi)) && (point.y > 0)) {
					this.qis.add(bottomQi);
				}

				if ((leftStone.getPlayer() == Player.BLACK_TERRITORY || leftStone.getPlayer() == Player.WHITE_TERRITORY)
						&& (!this.qis.contains(leftQi)) && (point.x > 0)) {
					this.qis.add(leftQi);
				}

			} // end if

		}
	} // end addStone()

	public void addQisBackToTheChain(Stone yan) {
		TreeSet<Qi> qis = this.getQis();
		Qi qi = new Qi(yan.getLocation());
		if ((!this.isYan) && (!qis.contains(qi)))
			qis.add(qi);
	} // end addBackQis()

	/**
	 * We go through each stone inside the chain and check its Qis 
	 */
	public void recheckQis() {

		TreeSet<Qi> qisToRemove = new TreeSet<Qi>();
		Iterator<Qi> iterator = this.qis.iterator();
		while (iterator.hasNext()) {
			Qi qi = iterator.next();

			// we have to make sure that x, y did not go out of board
			Point point = new Point(qi.getX(), qi.getY());
			Stone stone = this.goBoard.getStone(point);

			if ((stone.getPlayer() == Player.BLACK) || (stone.getPlayer() == Player.WHITE)) {
				qisToRemove.add(qi);
			}
		} // end while

		iterator = qisToRemove.iterator();
		while (iterator.hasNext()) {
			Qi qi = iterator.next();
			this.qis.remove(qi);
		} // end while

	} // end recheckQis()
	
	/**
	 * We go through each stone inside the chain and check its Qis 
	 */
	public int deeplyRecheckQis() {
		TreeSet<Qi> newQis = new TreeSet<Qi>();
		Iterator<Stone> iterator = this.iterator();
		while (iterator.hasNext()) {

			Stone next = iterator.next();
			Player[] nextQiPlayers = next.checkQi(goBoard.getBoard());
			Point nextLocation = next.getLocation();

			// chain above
			if (nextQiPlayers[0] != Player.BLACK && nextQiPlayers[0] != Player.WHITE) {
				if (nextLocation.y < this.boardSize - 1) {
					Point point = new Point(nextLocation.x, nextLocation.y + 1);
					Qi qi = new Qi(point);
					Stone newStone = new Stone(point);
					if (!newQis.contains(qi) && !this.contains(newStone)) {
						newQis.add(qi);
					}
				}
			}// end if

			// chain to right
			if (nextQiPlayers[1] != Player.BLACK && nextQiPlayers[1] != Player.WHITE) {
				if (nextLocation.x < this.boardSize - 1) {
					Point point = new Point(nextLocation.x + 1, nextLocation.y);
					Qi qi = new Qi(point);
					Stone newStone = new Stone(point);
					if (!newQis.contains(qi) && !this.contains(newStone)) {
						newQis.add(qi);
					}
				}
			} // end if

			// chain below
			if (nextQiPlayers[2] != Player.BLACK && nextQiPlayers[2] != Player.WHITE) {
				if (nextLocation.y > 0) {
					Point point = new Point(nextLocation.x, nextLocation.y - 1);
					Qi qi = new Qi(point);
					Stone newStone = new Stone(point);
					if (!newQis.contains(qi) && !this.contains(newStone)) {
						newQis.add(qi);
					}
				}
			} // end if

			// chain to left
			if (nextQiPlayers[3] != Player.BLACK && nextQiPlayers[3] != Player.WHITE) {
				if (nextLocation.x > 0) {
					Point point = new Point(nextLocation.x - 1, nextLocation.y);
					Qi qi = new Qi(point);
					Stone newStone = new Stone(point);
					if (!newQis.contains(qi) && !this.contains(newStone)) {
						newQis.add(qi);
					}
				}
			} // end if
		} // end while
		this.qis = newQis;
		return newQis.size();
	} // end deeplyrecheckQis()

	public void recheckChains(Stone currentStone) {

		// remove the stone not belong to the chain anymore
		Chain chain = this;
		Player player = chain.first().getBelongsTo();

		Iterator<Stone> iterator = this.iterator();
		while (iterator.hasNext()) {
			Stone stone = iterator.next();
			Point point = stone.getLocation();
			this.goBoard.getBoard()[point.x][point.y] = null;
		}

		this.goBoard.getChains().remove(this);
		// Add back all stones back to board

		chain.remove(currentStone);
		iterator = chain.iterator();
		while (iterator.hasNext()) {
			Stone stone = iterator.next();
			if (player == Player.BLACK) {
				stone.setPlayer(Player.WHITE_TERRITORY);
			} else if (player == Player.WHITE) {
				stone.setPlayer(Player.BLACK_TERRITORY);
			}
			this.goBoard.addStone(stone);
		}

	} // end checkQis()

	public void realYanDetector() {
		Iterator<Stone> iterator = this.iterator();
		int totalNumberOfYanjiao = 0;
		int totalNumberOfEdges = 0;
		while (iterator.hasNext()) {

			Stone stone = iterator.next();
			Player[] players = stone.checkQiForYan(this.goBoard.getBoard());
			int numberOfYanjiao = 0;
			int numberOfEdges = 0;

			for (int index = 0; index < 8; index++) {

				if (players[index] == Player.NOT_A_PLAYER || players[index] == Player.BLACK_TERRITORY || players[index] == Player.WHITE_TERRITORY) {
					numberOfYanjiao++;
				}
				if (players[index] == Player.OUTSIDE_THE_BOARD) {
					numberOfEdges++;
				}
			}

			totalNumberOfYanjiao += numberOfYanjiao;
			totalNumberOfEdges += numberOfEdges;
		}

		if (totalNumberOfYanjiao <= 1) {

			if (totalNumberOfEdges > 0 && totalNumberOfYanjiao == 0) {
				iterator = this.iterator();
				while (iterator.hasNext()) {
					Stone stone = iterator.next();
					stone.setZhenYan(true);
					stone.setJiaYan(false);
				}
				this.setZhenYan(true);
				this.setJiaYan(false);

			}
			if (totalNumberOfEdges == 0 && totalNumberOfYanjiao <= 1) {
				iterator = this.iterator();
				while (iterator.hasNext()) {
					Stone stone = iterator.next();
					stone.setZhenYan(true);
					stone.setJiaYan(false);
				}
				this.setZhenYan(true);
				this.setJiaYan(false);
			}
			if (totalNumberOfEdges > 0 && totalNumberOfYanjiao == 1) {

				iterator = this.iterator();
				while (iterator.hasNext()) {
					Stone stone = iterator.next();
					stone.setJiaYan(true);
					stone.setZhenYan(false);
				}
				this.setJiaYan(true);
				this.setZhenYan(false);
			}

		} else {

			iterator = this.iterator();
			while (iterator.hasNext()) {
				Stone stone = iterator.next();
				stone.setJiaYan(true);
				stone.setZhenYan(false);
			}
			this.setJiaYan(true);
			this.setZhenYan(false);
		}
	}

	public int getChainIndex() {
		return chainIndex;
	}// end getChainIndex()

	public TreeSet<Qi> getQis() {
		return this.qis;
	} // end getQis()

	public void setQis(TreeSet<Qi> Qis) {
		this.qis = Qis;
	} // end setQis()

	public boolean isYan() {
		return this.isYan;
	} // end isQin()

	public void setYan(boolean isYan) {
		this.isYan = isYan;
	} // end setQin()

	public boolean isZhenYan() {
		return this.isZhenYan;
	} // end isZhenYan()

	public void setZhenYan(boolean isZhenyan) {
		this.isZhenYan = isZhenyan;
	} // end setZhenYan()

	public boolean isJiaYan() {
		return this.isJiaYan;
	} // end isJiaYan()

	public void setJiaYan(boolean isJiayan) {
		this.isJiaYan = isJiayan;
	} // end setJiaYan()

} // end class
