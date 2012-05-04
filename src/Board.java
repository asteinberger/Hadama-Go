/**
 * Board.java - Keeps track of stone interactions on the go game board.
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <steinz08@gmail.com>
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;

public class Board {

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
	private int lastNumTizi;

	/**
	 * Since the player with black stones has first move advantage, the white
	 * player gets compensation or komi of 2.5 extra points in the game.
	 */
	private double komi = 2.5;

	/**
	 * Keep track of intersections where it's not legal to place black stones on
	 * the game board.
	 */
	private ArrayList<Point> illegalMovesforBlack = new ArrayList<Point>();

	/**
	 * Keep track of intersections where it's not legal to place white stones on
	 * the game board.
	 */
	private ArrayList<Point> illegalMovesforWhite = new ArrayList<Point>();

	/**
	 * size is the height or width of the game board. Traditionally, Go is
	 * played on a 19 by 19 sized board. Other standard dimensions include 13 by
	 * 13 and 9 by 9. Hard coded in HadamaGo.java you can find the size of the
	 * board displayed on the GUI. If the board is created from an artificial
	 * intelligence or machine learning algorithm, then its size must be
	 * obtained from this class.
	 */
	private int size;

	/**
	 * Create new Board instance to keep track of states of go game.
	 * 
	 * @param s
	 *            size of board (e.g., 9x9 vs 13x13 vs 19x19)
	 */
	public Board(int s) {
		this.size = s;
		this.stones = new Stone[s][s];
	} // end constructor

	/**
	 * Add stone to game board. assume we have location, color, value for its
	 * newest position on board.
	 * 
	 * @param s
	 *            stone to add to board
	 */
	public ArrayList<Chain> addStone(Stone s) {

		this.lastTiziPosition = new Point(-1, -1);
		this.lastNumTizi = 0;

		// get location of stone on game board. The origin of the board is at
		// the bottom left corner. X increases to the right, and y increases
		// above.
		int x = s.getLocation().x;
		int y = s.getLocation().y;

		// if this position is Yan, then remove the Stone from the Yan
		// If Yan is occupied, then remove it from the ChainList
		if (this.stones[x][y] != null) {

			// *****for chain color = 3 or color = 4 **************
			Stone a = this.stones[x][y].getChain().first();
			Point p = a.getLocation();

			if (!this.stones[x][y].isYan()) {
				this.stones[p.x][p.y].getChain().recheckChains(s);
			} else {
				this.stones[x][y].getChain().remove(s);
			}

			if (this.stones[x][y] != null) {
				if (this.stones[x][y].getChain().isEmpty()) {
					chains.remove(this.stones[x][y].getChain());
				}
			}
		} // end if

		// ************place stone on the board**************************
		this.stones[x][y] = s;

		// l contains the qi (or yan or liberties) for Stone s at (x,y).
		int[] l = s.checkQi(this.stones);

		// ************Add stone into chain*******************************
		// lc is the surrounding chains for Stone s
		Chain[] lc = s.checkChains(this.stones);
		// add stone s to new chain, then store new chain in chains list.
		Chain c = new Chain(this);
		c.addToChain(s, this.stones);
		this.chains.add(c);

		for (int i = 0; i < 4; i++) {
			if (l[i] == s.getColor()) {
				if (lc[i] != null) {
					Iterator<Stone> it = lc[i].iterator();
					while (it.hasNext()) {
						Stone next = it.next();
						c.addToChain(next, this.stones);
					} // end while

					if (lc[i].getChainIndex() != c.getChainIndex()) {
						chains.remove(lc[i]);
					}
				}
			}// end if
		}

		int c0 = -1;
		int c1 = -1;
		int c2 = -1;
		int c3 = -1;

		if (lc[0] != null) {
			c0 = lc[0].getChainIndex();
			if (lc[0].isYan()) {
				lc[0].recheckChains(s);
			}
		}

		if (lc[1] != null) {
			c1 = lc[1].getChainIndex();
			if (lc[1].isYan() && c0 != c1) {
				lc[1].recheckChains(s);
			}
		}
		if (lc[2] != null) {
			c2 = lc[2].getChainIndex();
			if (lc[2].isYan() && c2 != c0 && c2 != c1) {
				lc[2].recheckChains(s);
			}
		}
		if (lc[3] != null) {
			c3 = lc[3].getChainIndex();
			if (lc[3].isYan() && c3 != c0 && c3 != c1 && c3 != c2) {
				lc[3].recheckChains(s);
			}
		}

		// update eye corners
		for (int i = 4; i < 8; i++) {
			if (lc[i] != null) {
				if (l[i] == -1 && lc[i].isYan()) {
					lc[i].realYanDetector();
				}// end if
			}
		}

		// ************Add stone into Wei************************************
		// lw contains the weis (or stones diagonally adjacent to each other)
		// for stone s at (x,y).
		Wei[] lw = s.checkWeis(this.stones);

		// add stone to new wei, then store new wei in weis list.
		Wei w = new Wei(this);
		w.addToWei(s);
		if ((s.getColor() != 3) && (s.getColor() != 4))
			this.weis.add(w);

		for (int i = 0; i < 8; i++) {
			// if wei adjacent is smaller than wei containing stone
			// s, then move the stones from the adjacent wei to the
			// original wei. otherwise, move all the stones in the
			// original stone's wei to the wei adjacent.
			if (l[i] == s.getColor()) {

				if (lw[i] != null) {
					Iterator<Stone> it = lw[i].iterator();
					while (it.hasNext()) {
						Stone next = it.next();
						w.addToWei(next);

					} // end while

					if (lw[i].getWeiIndex() != w.getWeiIndex()) {
						weis.remove(lw[i]);
					}
				}
			}// end if
		}

		ArrayList<Chain> removedChains = new ArrayList<Chain>();
		// if stone color = 3 or 4, trade it as empty, so that we do not call
		// yanDetector() and tizi()
		if (s.getColor() != 3 && s.getColor() != 4) {
			// Detect if wei contains Yan. If so, turn them into Yan and add
			// into
			// Chain list.
			w.yanDetector();
			removedChains = this.tizi();
		}

		// run illegal Move Detector to find out illegal moves for both players
		this.illegalMovesforBlack = new ArrayList<Point>();
		this.illegalMovesforWhite = new ArrayList<Point>();
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
		for (int i = 0; i < this.chains.size(); i++) {

			// get chain in chain list
			Chain c = this.chains.get(i);

			// recheck qis in current chain
			c.recheckQis();

			// get first stone in current chain, and its location and wei
			Stone s = c.first();
			Point p = s.getLocation();
			Wei w = s.getWei();

			// if chain c's Qis equals to zero , turn all the stone into Yans
			if (c.getQis().size() == 0 && !c.isYan()) {

				Chain copyC = new Chain(this);
				Iterator<Stone> it3 = c.iterator();
				Stone firstStone = c.first();
				if (firstStone.getColor() != 4 && firstStone.getColor() != 3) {
					// make a copy of chain
					while (it3.hasNext()) {
						Stone next = it3.next();
						copyC.add((Stone) next.clone());
					}// end while
					removedChains.add(copyC);
				}// end if

				int r = 0;
				Iterator<Stone> it2 = c.iterator();
				while (it2.hasNext()) {

					Stone next = it2.next();

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

					if (next.getColor() == 1) {
						next.setBelongto(0);
					} else if (next.getColor() == 0) {
						next.setBelongto(1);
					} else if (next.getColor() == 3) {
						next.setBelongto(1);
					} else if (next.getColor() == 4) {
						next.setBelongto(0);
					}

					if (next.getColor() != 3 && next.getColor() != 4) {
						lastTiziPosition = next.getLocation();
						r++;
					}

					// set each stone into Yan
					next.setColor(-1);
					next.setYan(true);
					next.setWei(null);

					s = next;
					p = s.getLocation();
					this.stones[p.x][p.y] = s;

					// Add Qis back to surrounding stones
					Point top = new Point(p.x, p.y + 1);
					Point right = new Point(p.x + 1, p.y);
					Point bottom = new Point(p.x, p.y - 1);
					Point left = new Point(p.x - 1, p.y);

					Stone stTop = new Stone();
					Stone stRight = new Stone();
					Stone stBottom = new Stone();
					Stone stLeft = new Stone();

					if (top.y < size) {
						stTop = this.stones[top.x][top.y];
						if (stTop != null)
							stTop.getChain().addBackQis(s);
					}

					if (right.x < size) {
						stRight = this.stones[right.x][right.y];
						if (stRight != null)
							stRight.getChain().addBackQis(s);
					}

					if (bottom.y > -1) {
						stBottom = this.stones[bottom.x][bottom.y];
						if (stBottom != null)
							stBottom.getChain().addBackQis(s);
					}

					if (left.x > -1) {
						stLeft = this.stones[left.x][left.y];
						if (stLeft != null)
							stLeft.getChain().addBackQis(s);
					}
				} // end while

				lastNumTizi = r;
				c.setYan(true);

				// clean the Qis for the Yan
				TreeSet<Qi> empty = new TreeSet<Qi>();
				c.setQis(empty);
				c.realYanDetector();

			} // end if
		} // end for
		return removedChains;
	}// end tizi()

	public void illegalMoveDetector() {
		this.illegalMovesforBlack = new ArrayList<Point>();
		this.illegalMovesforWhite = new ArrayList<Point>();
		for (int i = 0; i < chains.size(); i++) {
			Chain c = chains.get(i);
			if (c.isYan()) {
				if (c.size() == 1) {

					Stone first = c.first();
					Point p = first.getLocation();
					Chain[] lc = first.checkChains(this.stones);
					int numTiziWhite = 0;
					int numHuoziBlack = 0;
					int numTiziBlack = 0;
					int numHuoziWhite = 0;
					int numStoneTiziBlack = 0;
					int numStoneTiziWhite = 0;

					for (int j = 0; j < 4; j++) {
						if (lc[j] != null) {

							// case 1
							if (lc[j].first().getColor() == 0
									&& lc[j].getQis().size() == 1) {
								numTiziWhite++;
								numStoneTiziWhite = lc[j].size();

							}

							// case 2
							if (lc[j].first().getColor() == 1
									&& lc[j].getQis().size() == 1) {
								numTiziBlack++;
								numStoneTiziBlack = lc[j].size();

							}

							// case 3
							if (lc[j].first().getColor() == 0
									&& lc[j].getQis().size() > 1) {
								numHuoziBlack++;
							}

							// case 4
							if (lc[j].first().getColor() == 1
									&& lc[j].getQis().size() > 1) {
								numHuoziWhite++;
							}
						}
					}// end for

					if (numTiziWhite == 0 && numHuoziWhite == 0) {
						this.illegalMovesforWhite.add(p);
					}// end if

					if (numTiziBlack == 0 && numHuoziBlack == 0) {
						this.illegalMovesforBlack.add(p);
					}// end if

					if (numTiziWhite == 1 && numHuoziBlack > 1) {

						if (getLastTiziPosition().equals(p)) {
							if (numStoneTiziWhite != 2) {
								this.illegalMovesforWhite.add(p);
							}
						}
					}

					if (numTiziBlack == 1 && numHuoziWhite > 1) {

						if (getLastTiziPosition().equals(p)) {
							if (numStoneTiziBlack != 2) {
								this.illegalMovesforBlack.add(p);
							}
						}
					}

				}// end if
			}// end if

			// it is not Yan
			else {

				Iterator<Stone> it = c.iterator();
				while (it.hasNext()) {
					Stone next = it.next();
					int color = next.getColor();
					Point p = next.getLocation();
					if (color == 0 || color == 1) {
						this.illegalMovesforWhite.add(p);
						this.illegalMovesforBlack.add(p);
					}
				}
			}
		}// end for
	}// end illegalMoveDetector()

	public double[] getScores() {
		double[] scores = { 0.0f, 0.0f, 0, 0f };
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {
				if (this.stones[i][j] != null) {
					if (this.stones[i][j].getBelongto() == 0)
						scores[0]++;
					else if (this.stones[i][j].getBelongto() == 1)
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
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {

				if (this.stones[i][j] != null) {
					if (this.stones[i][j].getBelongto() == 0) {
						scoreBlack++;
					} else if (this.stones[i][j].getBelongto() == 1) {
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
			System.out.println("Bloack Player won this game!");
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
				.println("(Total = " + this.illegalMovesforBlack.size() + ")");
		for (int i = 0; i < this.illegalMovesforBlack.size(); i++) {
			System.out.print("(" + this.illegalMovesforBlack.get(i).x + ","
					+ this.illegalMovesforBlack.get(i).y + "), ");

		}
		System.out.println();
		System.out.print("White Players Illegal Moves:");
		System.out
				.println("(Total = " + this.illegalMovesforWhite.size() + ")");
		for (int i = 0; i < this.illegalMovesforWhite.size(); i++) {
			System.out.print("(" + this.illegalMovesforWhite.get(i).x + ","
					+ this.illegalMovesforWhite.get(i).y + "), ");

		}
		System.out.println();
		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");

	}

	public void printChains() {
		System.out.println("" + this.chains.size() + " chains");
		for (int i = 0; i < this.chains.size(); i++) {
			System.out
					.println("***********************************************************");
			Chain c = this.chains.get(i);
			System.out.println("Chain " + c.getChainIndex() + ": [" + "isYan: "
					+ c.isYan() + ", isZhenYan: " + c.isZhenYan()
					+ ", isJiaYan: " + c.isJiaYan() + ", Total: "
					+ Integer.toString(c.size()) + "]");
			System.out.println(c.toString().replaceAll("],", "]\n"));
			System.out.println("Total Qi #: "
					+ Integer.toString(c.getQis().size()));
			System.out.println(c.getQis().toString().replaceAll("],", "]"));
		} // end for
		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");
	} // end printChains()

	public void printWeis() {
		System.out.println("" + this.weis.size() + " wei");
		for (int i = 0; i < this.weis.size(); i++) {
			System.out
					.println("***********************************************************");

			Wei w = this.weis.get(i);
			System.out.print("Wei " + w.getWeiIndex() + ": [");
			if (w.first().getColor() == 0) {
				System.out.print("Color: Black,");
			} else {
				System.out.print("Color: White,");
			}
			System.out.println(" Total: " + Integer.toString(w.size()) + "]");
			Iterator<Stone> it = w.iterator();
			while (it.hasNext()) {
				Stone s = it.next();
				Point p = s.getLocation();
				System.out.print("(" + p.x + "," + p.y + ") ");
			}
			System.out.println();
		} // end for
		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");
	} // end printChains()

	public void printBoard() {
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {
				if (this.stones[i][j] != null) {
					if (this.stones[i][j].getColor() != -1) {

						if (this.stones[i][j].getColor() == 0) {
							System.out.print("[Black,"
									+ this.stones[i][j].getChain()
											.getChainIndex() + ","
									+ this.stones[i][j].getWei().getWeiIndex()
									+ "]");
						} else {
							System.out.print("[White,"
									+ this.stones[i][j].getChain()
											.getChainIndex() + ","
									+ this.stones[i][j].getWei().getWeiIndex()
									+ "]");
						}

					} else {
						System.out.print("[Yan  ,"
								+ this.stones[i][j].getChain().getChainIndex()
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
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {

				System.out.println("Position = (" + i + "," + j + ")");
				if (this.stones[i][j] == null) {
					System.out.println("Empty");
				} else {
					System.out.println(this.stones[i][j]);
					System.out.println("Chain index: "
							+ this.stones[i][j].getChain().getChainIndex());
					System.out.println(this.stones[i][j].getChain().toString()
							.replaceAll("],", "]\n"));

					if (this.stones[i][j].getWei() == null) {
						System.out.println("Wei is Empty");
					} else {
						System.out.println("Wei index: "
								+ this.stones[i][j].getWei().getWeiIndex());
						System.out.println(this.stones[i][j].getWei()
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

	public void printMoves() {
		System.out.println("" + this.moves.size() + " moves");
		Iterator<Move> it = moves.iterator();
		while (it.hasNext()) {
			Move next = it.next();
			System.out
					.println("***********************************************************");
			System.out.println(next.toString());
		}
		System.out
				.println("=================================================================================================================");
		System.out
				.println("=================================================================================================================");
	}// end printMoves()

	public Stone getStone(Point p) {
		Stone result = this.stones[p.x][p.y];
		if (result == null)
			result = new Stone(p);
		return result;
	} // end getStone()

	public ArrayList<Chain> getChains() {
		return this.chains;
	} // end getChains()

	public void setChains(ArrayList<Chain> c) {
		this.chains = c;
	} // end setChains()

	public ArrayList<Point> getIllegalMovesforBlack() {
		return this.illegalMovesforBlack;
	} // end getChains()

	public void setIllegalMovesforBlack(ArrayList<Point> ib) {
		this.illegalMovesforBlack = ib;
	} // end setChains()

	public ArrayList<Point> getIllegalMovesforWhite() {
		return this.illegalMovesforWhite;
	} // end getChains()

	public void setIllegalMovesforWhite(ArrayList<Point> iw) {
		this.illegalMovesforWhite = iw;
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

	public int getSize() {
		return this.size;
	} // end getSize()

	public void setSize(int size) {
		this.size = size;
	} // end setSize()

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

	public int getLastTiziNum() {
		return this.lastNumTizi;
	} // end getLastTiziNum

	public void setLastTiziNum(int lastTiziNum) {
		this.lastNumTizi = lastTiziNum;
	} // end setLastTiziNum()
} // end class

