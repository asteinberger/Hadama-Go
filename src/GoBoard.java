/**
 * Board.java - Keeps track of stone interactions on the go game board.
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <adam@akmaz.io>
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;

public class GoBoard {

	/**
	 * In go the game board is a square grid on which stones are placed on the
	 * intersections of grid lines. Stones can have various "colors" like black
	 * (0) and white (1), but sometimes they're colors are non-visible values
	 * like -2 for yan (empty stones inside of a chain), -1 for empty
	 * intersections, 3 for black territory on the board and 4 for white
	 * territory.
	 */

	private Stone[][] stones;

	/**
	 * Stones that are horizontally or vertically adjacent on the game board
	 * form chains. In go, creating chains is advantageous at times because a
	 * chain is harder to capture than a single stone. Chains have liberties or
	 * qis, which are the open intersections horizontally or vertically adjacent
	 * to the chain's stones. A chain is captured by an enemy when its liberties
	 * are all taken away. Chains also have yan, which are open intersections
	 * inside a chain. With two true yan, a chain is living and cannot be
	 * capture by enemy stones.
	 */
	private ArrayList<Chain> chains = new ArrayList<Chain>();
	private ArrayList<Wei> weis = new ArrayList<Wei>();
	private Stack<Move> moves = new Stack<Move>();
	private Point lastTiziPosition;
	private int lastTiziNumber;

	/**
	 * Since the player with black stones has first move advantage, the white
	 * player gets compensation or komi of 2.5 extra points in the game.
	 */
	private final double komi = 2.5;

	/**
	 * Keep track of intersections where it's not legal to place black stones on
	 * the game board.
	 */
	private ArrayList<Point> illegalMovesForBlack = new ArrayList<Point>();

	/**
	 * Keep track of intersections where it's not legal to place white stones on
	 * the game board.
	 */
	private ArrayList<Point> illegalMovesForWhite = new ArrayList<Point>();

	/**
	 * size is the height or width of the game board. Traditionally, Go is
	 * played on a 19 by 19 sized board. Other standard dimensions include 13 by
	 * 13 and 9 by 9. Hard coded in HadamaGo.java you can find the size of the
	 * board displayed on the GUI. If the board is created from an artificial
	 * intelligence or machine learning algorithm, then its size must be
	 * obtained from this class.
	 */
	private int boardSize;

	/**
	 * Create new Board instance to keep track of states of go game.
	 * 
	 * @param boardSize
	 *            size of board (e.g., 9x9 vs 13x13 vs 19x19)
	 */
	public GoBoard(int boardSize) {
		this.boardSize = boardSize;
		this.stones = new Stone[boardSize][boardSize];
	} // end constructor

	/**
	 * Add stone to game board. assume we have location, color, value for its
	 * newest position on board.
	 * 
	 * @param stone
	 *            stone to add to board
	 */
	public ArrayList<Chain> addStone(Stone stone) {

		this.lastTiziPosition = new Point(-1, -1);
		this.lastTiziNumber = 0;

		// get location of stone on game board. The origin of the board is at
		// the bottom left corner. X increases to the right, and y increases
		// above.
		int x = stone.getLocation().x;
		int y = stone.getLocation().y;

		// if this position is Yan, then remove the Stone from the Yan
		// If Yan is occupied, then remove it from the ChainList
		if (this.stones[x][y] != null) {

			// *****for chain color = 3 or color = 4 **************
			Stone firstStoneInChain = this.stones[x][y].getChain().first();
			Point stonePoint = firstStoneInChain.getLocation();

			if (!this.stones[x][y].isYan()) {
				this.stones[stonePoint.x][stonePoint.y].getChain().recheckChains(stone);
			} else {
				this.stones[x][y].getChain().remove(stone);
			}

			if (this.stones[x][y] != null) {
				if (this.stones[x][y].getChain().isEmpty()) {
					chains.remove(this.stones[x][y].getChain());
				}
			}
		} // end if

		// ************place stone on the board**************************
		this.stones[x][y] = stone;

		// l contains the qi (or yan or liberties) for Stone s at (x,y).
		Player[] qis = stone.checkQi(this.stones);

		// ************Add stone into chain*******************************
		// lc is the surrounding chains for Stone s
		Chain[] surroundingChains = stone.checkChains(this.stones);
		// add stone s to new chain, then store new chain in chains list.
		Chain chain = new Chain(this);
		chain.addAStoneToChain(stone);
		this.chains.add(chain);

		for (int index = 0; index < 4; index++) {
			if (qis[index] == stone.getPlayer()) {
				if (surroundingChains[index] != null) {
					Iterator<Stone> iterator = surroundingChains[index].iterator();
					while (iterator.hasNext()) {
						Stone nextStone = iterator.next();
						chain.addAStoneToChain(nextStone);
					} // end while

					if (surroundingChains[index].getChainIndex() != chain.getChainIndex()) {
						chains.remove(surroundingChains[index]);
					}
				}
			}// end if
		}

		int firstChainIndex = -1;
		int secondChainIndex = -1;
		int thirdChainIndex = -1;
		int fourthChainIndex = -1;

		if (surroundingChains[0] != null) {
			firstChainIndex = surroundingChains[0].getChainIndex();
			if (surroundingChains[0].isYan()) {
				surroundingChains[0].recheckChains(stone);
			}
		}

		if (surroundingChains[1] != null) {
			secondChainIndex = surroundingChains[1].getChainIndex();
			if (surroundingChains[1].isYan() && firstChainIndex != secondChainIndex) {
				surroundingChains[1].recheckChains(stone);
			}
		}
		if (surroundingChains[2] != null) {
			thirdChainIndex = surroundingChains[2].getChainIndex();
			if (surroundingChains[2].isYan() && thirdChainIndex != firstChainIndex && thirdChainIndex != secondChainIndex) {
				surroundingChains[2].recheckChains(stone);
			}
		}
		if (surroundingChains[3] != null) {
			fourthChainIndex = surroundingChains[3].getChainIndex();
			if (surroundingChains[3].isYan() && fourthChainIndex != firstChainIndex && fourthChainIndex != secondChainIndex &&
					fourthChainIndex != thirdChainIndex) {
				surroundingChains[3].recheckChains(stone);
			}
		}

		// update eye corners
		for (int index = 4; index < 8; index++) {
			if (surroundingChains[index] != null) {
				if (qis[index] == Player.NOT_A_PLAYER && surroundingChains[index].isYan()) {
					surroundingChains[index].realYanDetector();
				}// end if
			}
		}

		// ************Add stone into Wei************************************
		// lw contains the weis (or stones diagonally adjacent to each other)
		// for stone s at (x,y).
		Wei[] weis = stone.checkWeis(this.stones);

		// add stone to new wei, then store new wei in weis list.
		Wei wei = new Wei(this);
		wei.addToWei(stone);
		if ((stone.getPlayer() != Player.BLACK_TERRITORY) && (stone.getPlayer() != Player.WHITE_TERRITORY))
			this.weis.add(wei);

		for (int index = 0; index < 8; index++) {
			// if wei adjacent is smaller than wei containing stone
			// s, then move the stones from the adjacent wei to the
			// original wei. otherwise, move all the stones in the
			// original stone's wei to the wei adjacent.
			if (qis[index] == stone.getPlayer()) {

				if (weis[index] != null) {
					Iterator<Stone> iterator = weis[index].iterator();
					while (iterator.hasNext()) {
						Stone next = iterator.next();
						wei.addToWei(next);

					} // end while

					if (weis[index].getWeiIndex() != wei.getWeiIndex()) {
						this.weis.remove(weis[index]);
					}
				}
			}// end if
		}

		ArrayList<Chain> removedChains = new ArrayList<Chain>();
		// if stone color = 3 or 4, trade it as empty, so that we do not call
		// yanDetector() and tizi()
		if (stone.getPlayer() != Player.BLACK_TERRITORY && stone.getPlayer() != Player.WHITE_TERRITORY) {
			// Detect if wei contains Yan. If so, turn them into Yan and add
			// into
			// Chain list.
			wei.yanDetector();
			removedChains = this.tizi();
		}

		// run illegal Move Detector to find out illegal moves for both players
		this.illegalMovesForBlack = new ArrayList<Point>();
		this.illegalMovesForWhite = new ArrayList<Point>();
		illegalMoveDetector();

		// return list of removed chains
		return removedChains;
	} // end addStone()

	/**
	 * 
	 * 
	 * @return list of chains removed during tizi
	 */
	public ArrayList<Chain> tizi() {
		ArrayList<Chain> removedChains = new ArrayList<Chain>();

		// check each chain, if chain's Qis equals to zero, change them into Yan
		for (int index = 0; index < this.chains.size(); index++) {

			// get chain in chain list
			Chain chain = this.chains.get(index);

			// recheck qis in current chain
			chain.recheckQis();

			// get first stone in current chain, and its location and wei
			Stone stone = chain.first();
			Point stoneLocation;
			Wei w = stone.getWei();

			// if chain c's Qis equals to zero , turn all the stone into Yans
			if (chain.getQis().size() == 0 && !chain.isYan()) {

				Chain chainCopy = new Chain(this);
				Iterator<Stone> iterator = chain.iterator();
				Stone firstStone = chain.first();
				if (firstStone.getPlayer() != Player.BLACK_TERRITORY && firstStone.getPlayer() != Player.WHITE_TERRITORY) {
					// make a copy of chain
					while (iterator.hasNext()) {
						Stone next = iterator.next();
						chainCopy.add((Stone) next.clone());
					}// end while
					removedChains.add(chainCopy);
				}// end if

				int removedStoneIndex = 0;
				iterator = chain.iterator();
				while (iterator.hasNext()) {

					Stone next = iterator.next();

					if (next.getWei() != null) {
						if (w.size() == 0) {
							this.weis.remove(next.getWei());
						} else if (w.size() == 1) {
							w.remove(next);
							this.weis.remove(next.getWei());
						} else {
							w.remove(next);
						} // end if
					} // end if

					if (next.getPlayer() == Player.WHITE) {
						next.setBelongsTo(Player.BLACK);
					} else if (next.getPlayer() == Player.BLACK) {
						next.setBelongsTo(Player.WHITE);
					} else if (next.getPlayer() == Player.BLACK_TERRITORY) {
						next.setBelongsTo(Player.WHITE);
					} else if (next.getPlayer() == Player.WHITE_TERRITORY) {
						next.setBelongsTo(Player.BLACK);
					}

					if (next.getPlayer() != Player.BLACK_TERRITORY && next.getPlayer() != Player.WHITE_TERRITORY) {
						lastTiziPosition = next.getLocation();
						removedStoneIndex++;
					}

					// set each stone into Yan
					next.setPlayer(Player.NOT_A_PLAYER);
					next.setYan(true);
					next.setWei(null);

					stone = next;
					stoneLocation = stone.getLocation();
					this.stones[stoneLocation.x][stoneLocation.y] = stone;

					// Add Qis back to surrounding stones
					Point top = new Point(stoneLocation.x, stoneLocation.y + 1);
					Point right = new Point(stoneLocation.x + 1, stoneLocation.y);
					Point bottom = new Point(stoneLocation.x, stoneLocation.y - 1);
					Point left = new Point(stoneLocation.x - 1, stoneLocation.y);

					Stone topStone;
					Stone rightStone;
					Stone bottomStone;
					Stone leftStone;

					if (top.y < boardSize) {
						topStone = this.stones[top.x][top.y];
						if (topStone != null)
							topStone.getChain().addQisBackToTheChain(stone);
					}

					if (right.x < boardSize) {
						rightStone = this.stones[right.x][right.y];
						if (rightStone != null)
							rightStone.getChain().addQisBackToTheChain(stone);
					}

					if (bottom.y > -1) {
						bottomStone = this.stones[bottom.x][bottom.y];
						if (bottomStone != null)
							bottomStone.getChain().addQisBackToTheChain(stone);
					}

					if (left.x > -1) {
						leftStone = this.stones[left.x][left.y];
						if (leftStone != null)
							leftStone.getChain().addQisBackToTheChain(stone);
					}
				} // end while

				lastTiziNumber = removedStoneIndex;
				chain.setYan(true);

				// clean the Qis for the Yan
				TreeSet<Qi> emptyQi = new TreeSet<Qi>();
				chain.setQis(emptyQi);
				chain.realYanDetector();

			} // end if
		} // end for
		return removedChains;
	}// end tizi()

	public void illegalMoveDetector() {
		this.illegalMovesForBlack = new ArrayList<Point>();
		this.illegalMovesForWhite = new ArrayList<Point>();
		for (int index = 0; index < chains.size(); index++) {
			Chain chain = chains.get(index);
			if (chain.isYan()) {
				if (chain.size() == 1) {

					Stone firstStone = chain.first();
					Point point = firstStone.getLocation();
					Chain[] legalChain = firstStone.checkChains(this.stones);
					int numTiziWhite = 0;
					int numHuoziBlack = 0;
					int numTiziBlack = 0;
					int numHuoziWhite = 0;
					int numStoneTiziBlack = 0;
					int numStoneTiziWhite = 0;

					for (int caseIndex = 0; caseIndex < 4; caseIndex++) {
						if (legalChain[caseIndex] != null) {

							// case 1
							if (legalChain[caseIndex].first().getPlayer() == Player.BLACK
									&& legalChain[caseIndex].getQis().size() == 1) {
								numTiziWhite++;
								numStoneTiziWhite = legalChain[caseIndex].size();

							}

							// case 2
							if (legalChain[caseIndex].first().getPlayer() == Player.WHITE
									&& legalChain[caseIndex].getQis().size() == 1) {
								numTiziBlack++;
								numStoneTiziBlack = legalChain[caseIndex].size();

							}

							// case 3
							if (legalChain[caseIndex].first().getPlayer() == Player.BLACK
									&& legalChain[caseIndex].getQis().size() > 1) {
								numHuoziBlack++;
							}

							// case 4
							if (legalChain[caseIndex].first().getPlayer() == Player.WHITE
									&& legalChain[caseIndex].getQis().size() > 1) {
								numHuoziWhite++;
							}
						}
					}// end for

					if (numTiziWhite == 0 && numHuoziWhite == 0) {
						this.illegalMovesForWhite.add(point);
					}// end if

					if (numTiziBlack == 0 && numHuoziBlack == 0) {
						this.illegalMovesForBlack.add(point);
					}// end if

					if (numTiziWhite == 1 && numHuoziBlack > 1) {

						if (getLastTiziPosition().equals(point)) {
							if (numStoneTiziWhite != 2) {
								this.illegalMovesForWhite.add(point);
							}
						}
					}

					if (numTiziBlack == 1 && numHuoziWhite > 1) {

						if (getLastTiziPosition().equals(point)) {
							if (numStoneTiziBlack != 2) {
								this.illegalMovesForBlack.add(point);
							}
						}
					}

				}// end if
			}// end if

			// it is not Yan
			else {

				Iterator<Stone> it = chain.iterator();
				while (it.hasNext()) {
					Stone next = it.next();
					Player player = next.getPlayer();
					Point point = next.getLocation();
					if (player == Player.BLACK || player == Player.WHITE) {
						this.illegalMovesForWhite.add(point);
						this.illegalMovesForBlack.add(point);
					}
				}
			}
		}// end for
	}// end illegalMoveDetector()

	public double[] getScores() {
		double[] scores = { 0.0f, 0.0f, 0, 0f };
		for (int y = this.boardSize - 1; y > -1; y--) {
			for (int x = 0; x < this.boardSize; x++) {
				if (this.stones[x][y] != null) {
					if (this.stones[x][y].getBelongsTo() == Player.BLACK)
						scores[0]++;
					else if (this.stones[x][y].getBelongsTo() == Player.WHITE)
						scores[1]++;
				} // end if
			} // end for
		} // end for
		scores[1] += this.komi;
		scores[2] = scores[1] - scores[0];
		return scores;
	} // end scores

	public void printScores() {
		double scoreBlack = 0;
		double scoreWhite = 0;
		for (int y = this.boardSize - 1; y > -1; y--) {
			for (int x = 0; x < this.boardSize; x++) {

				if (this.stones[x][y] != null) {
					if (this.stones[x][y].getBelongsTo() == Player.BLACK) {
						scoreBlack++;
					} else if (this.stones[x][y].getBelongsTo() == Player.WHITE) {
						scoreWhite++;
					}
				} // end if
			} // end for
		} // end for

		scoreWhite = scoreWhite + komi;
		System.out.println("Score:");
		System.out.println("Komi =" + komi);
		System.out.println("Black Player =" + scoreBlack);
		System.out.println("White Player =" + scoreWhite);

		if (scoreBlack > scoreWhite) {
			System.out.println("Black Player won this game!");
		} else {
			System.out.println("White Player won this game!");
		}
		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");

	} // end printScores()

	public void printIllegalMoves() {

		System.out.print("Black Players Illegal Moves:");
		System.out
				.println("(Total = " + this.illegalMovesForBlack.size() + ")");
		for (int index = 0; index < this.illegalMovesForBlack.size(); index++) {
			System.out.print("(" + this.illegalMovesForBlack.get(index).x + ","
					+ this.illegalMovesForBlack.get(index).y + "), ");

		}
		System.out.println();
		System.out.print("White Players Illegal Moves:");
		System.out
				.println("(Total = " + this.illegalMovesForWhite.size() + ")");
		for (int index = 0; index < this.illegalMovesForWhite.size(); index++) {
			System.out.print("(" + this.illegalMovesForWhite.get(index).x + ","
					+ this.illegalMovesForWhite.get(index).y + "), ");

		}
		System.out.println();
		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");

	}

	public void printChains() {
		System.out.println("" + this.chains.size() + " chains");
		for (int index = 0; index < this.chains.size(); index++) {
			System.out
					.println("***********************************************************");
			Chain chain = this.chains.get(index);
			System.out.println("Chain " + chain.getChainIndex() + ": [" + "isYan: "
					+ chain.isYan() + ", isZhenYan: " + chain.isZhenYan()
					+ ", isJiaYan: " + chain.isJiaYan() + ", Total: "
					+ Integer.toString(chain.size()) + "]");
			System.out.println(chain.toString().replaceAll("],", "]\n"));
			System.out.println("Total Qi #: "
					+ Integer.toString(chain.getQis().size()));
			System.out.println(chain.getQis().toString().replaceAll("],", "]"));
		} // end for
		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");
	} // end printChains()

	public void printWeis() {
		System.out.println("" + this.weis.size() + " wei");
		for (int index = 0; index < this.weis.size(); index++) {
			System.out
					.println("***********************************************************");

			Wei wei = this.weis.get(index);
			System.out.print("Wei " + wei.getWeiIndex() + ": [");
			if (wei.first().getPlayer() == Player.BLACK) {
				System.out.print("Color: Black,");
			} else {
				System.out.print("Color: White,");
			}
			System.out.println(" Total: " + Integer.toString(wei.size()) + "]");
			Iterator<Stone> iterator = wei.iterator();
			while (iterator.hasNext()) {
				Stone stone = iterator.next();
				Point point = stone.getLocation();
				System.out.print("(" + point.x + "," + point.y + ") ");
			}
			System.out.println();
		} // end for
		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");
	} // end printChains()

	public void printBoard() {
		for (int y = this.boardSize - 1; y > -1; y--) {
			for (int x = 0; x < this.boardSize; x++) {
				if (this.stones[x][y] != null) {
					if (this.stones[x][y].getPlayer() != Player.NOT_A_PLAYER) {

						if (this.stones[x][y].getPlayer() == Player.BLACK) {
							System.out.print("[Black,"
									+ this.stones[x][y].getChain()
											.getChainIndex() + ","
									+ this.stones[x][y].getWei().getWeiIndex()
									+ "]");
						} else {
							System.out.print("[White,"
									+ this.stones[x][y].getChain()
											.getChainIndex() + ","
									+ this.stones[x][y].getWei().getWeiIndex()
									+ "]");
						}

					} else {
						System.out.print("[Yan  ,"
								+ this.stones[x][y].getChain().getChainIndex()
								+ ",****]");
					}
				} else
					System.out.print("[***********]");
				System.out.print(" ");
			} // end for
			System.out.println(" ");
		} // end for

		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");

	} // end printBoard()

	public void printStones() {
		for (int y = this.boardSize - 1; y > -1; y--) {
			for (int x = 0; x < this.boardSize; x++) {

				System.out.println("Position = (" + x + "," + y + ")");
				if (this.stones[x][y] == null) {
					System.out.println("Empty");
				} else {
					System.out.println(this.stones[x][y]);
					System.out.println("Chain index: "
							+ this.stones[x][y].getChain().getChainIndex());
					System.out.println(this.stones[x][y].getChain().toString()
							.replaceAll("],", "]\n"));

					if (this.stones[x][y].getWei() == null) {
						System.out.println("Wei is Empty");
					} else {
						System.out.println("Wei index: "
								+ this.stones[x][y].getWei().getWeiIndex());
						System.out.println(this.stones[x][y].getWei()
								.toString().replaceAll("],", "]\n"));
					}
				}
				System.out
						.println("***********************************************************");

			} // end for
		} // end for

		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");

	}

	public String getCodes() {

		String codes = "";

		for (int row = 0; row < this.boardSize; row++) {

			for (int col = 0; col < this.boardSize; col++) {

				Stone stone = this.stones[row][col];

				Player player = Player.NOT_A_PLAYER;
				if (stone != null)
					player = stone.getPlayer();

				if (player == Player.BLACK) {
					codes += "01";
				} else if (player == Player.WHITE) {
					codes += "10";
				} else {
					codes += "00";
				}

			} // end for

		} // end for

		return codes;

	} // end getCodes()

	public void printMoves() {
		System.out.println("" + this.moves.size() + " moves");
		Iterator<Move> iterator = moves.iterator();
		while (iterator.hasNext()) {
			Move nextMove = iterator.next();
			System.out
					.println("***********************************************************");
			System.out.println(nextMove.toString());
		}
		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");
	}// end printMoves()

	public Stone getStone(Point point) {
		Stone result = this.stones[point.x][point.y];
		if (result == null)
			result = new Stone(point);
		return result;
	} // end getStone()

	public ArrayList<Chain> getChains() {
		return this.chains;
	} // end getChains()

	public void setChains(ArrayList<Chain> c) {
		this.chains = c;
	} // end setChains()

	public ArrayList<Point> getIllegalMovesForBlack() {
		return this.illegalMovesForBlack;
	} // end getChains()

	public void setIllegalMovesForBlack(ArrayList<Point> ib) {
		this.illegalMovesForBlack = ib;
	} // end setChains()

	public ArrayList<Point> getIllegalMovesForWhite() {
		return this.illegalMovesForWhite;
	} // end getChains()

	public void setIllegalMovesForWhite(ArrayList<Point> iw) {
		this.illegalMovesForWhite = iw;
	} // end setChains()

	public ArrayList<Wei> getWeis() {
		return this.weis;
	} // end getWeis()

	public void setWeis(ArrayList<Wei> w) {
		this.weis = w;
	} // end setWeis()

	public Stone[][] getBoard() {
		return this.stones;
	} // end getBoard()

	public void setBoard(Stone[][] b) {
		this.stones = b;
	} // end getBoard()

	public int getBoardSize() {
		return this.boardSize;
	} // end getSize()

	public Stack<Move> getMoves() {
		return this.moves;
	} // end getMoves()

	public void setMoves(Stack<Move> moves) {
		this.moves = moves;
	} // end setMoves()

	public Point getLastTiziPosition() {
		return this.lastTiziPosition;
	} // end getLastTiziPosition()

	public void setLastTiziPosition(Point lastTiziPosition) {
		this.lastTiziPosition = lastTiziPosition;
	} // end setLastTiziPosition()

	public int getLastTiziNumber() {
		return this.lastTiziNumber;
	} // end getLastTiziNum

	public void setLastTiziNumber(int lastTiziNum) {
		this.lastTiziNumber = lastTiziNum;
	} // end setLastTiziNum()
} // end class

