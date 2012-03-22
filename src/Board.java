import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Used to keep track of pieces to remove from board.
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <steinz08@gmail.com>
 */

public class Board {

	private Stone[][] board;
	public ArrayList<Chain> chains = new ArrayList<Chain>();
	public ArrayList<Wei> weis = new ArrayList<Wei>();
	public Stack<Move> moves = new Stack<Move>();
	public Point LastTiziPosition;
	public int LastTiziNum;
	public double Komi = 2.5;
	/**
	 * size is the height or width of the goboard. Traditionally, Go is played
	 * on a 19 by 19 sized board. Other standard dimensions include 13 by 13 and
	 * 9 by 9. Hard coded in HadamaGo.java you can find the size of the goboard
	 * displayed on the GUI. If the goboard is created from an artificial
	 * intelligence or machine learning algorithm, then the size of the goboard
	 * must be obtained from this class.
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
		this.board = new Stone[this.size][this.size];

	} // end constructor

	/**
	 * Add stone to game board. assume we have location, color, value for its
	 * newest position on board.
	 * 
	 * @param s
	 *            stone to add to board
	 */
	public ArrayList<Chain> addStone(Stone s) {

		LastTiziPosition = new Point(-1, -1);
		LastTiziNum = 0;

		int x = s.getLocation().x;
		int y = s.getLocation().y;

		// if this position is Yan, then remove the Stone from the Yan
		// If Yan is occupied, then remove it from the ChainList
		if (board[x][y] != null) {

			// *****for chain color = 3 **************
			Stone a = this.board[x][y].getChain().first(); // maybe become a bug
															// for the future
			Point p = a.getLocation();

			if ((this.board[p.x][p.y].getColor() == 3)
					|| (this.board[p.x][p.y].getColor() == 4)) {
				this.board[p.x][p.y].getChain().updateChains(s);
			} else {

				this.board[x][y].getChain().removeStone(s);

			}
			if (this.board[x][y] != null) {
				if (this.board[x][y].getChain().size() == 0) {
					chains.remove(realChainIndex(this.board[x][y].getChain()
							.getChainIndex()));
				}
			}
		}

		this.board[x][y] = s;

		// l is the liberties for Stone s
		int[] l = s.checkQi(this.board);
		// lc is the surrounding chains for Stone s

		Chain[] lc = s.checkChains(this.board);

		Chain c = new Chain(this);
		c.addToChain(s, this.board);
		this.chains.add(c);

		Wei[] lw = s.checkWeis(this.board);
		Wei w = new Wei(this);

		w.addToWei(s);
		if (s.getColor() != 3 && s.getColor() != 4) {
			this.weis.add(w);
		}

		// ************Add stone into chain************************
		// chain above
		if (l[0] == s.getColor()) {

			Iterator<Stone> it = lc[0].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				c.addToChain(next, this.board);
			} // end while

			if (lc[0].getChainIndex() != c.getChainIndex()
					&& realChainIndex(lc[0].getChainIndex()) != -1) {
				chains.remove(realChainIndex(lc[0].getChainIndex()));
			}
		}// end if

		// chain to right
		if (l[1] == s.getColor()) {

			Iterator<Stone> it = lc[1].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				c.addToChain(next, this.board);
			} // end while

			if (lc[1].getChainIndex() != c.getChainIndex()
					&& realChainIndex(lc[1].getChainIndex()) != -1) {
				chains.remove(realChainIndex(lc[1].getChainIndex()));
			}
		} // end if

		// chain below
		if (l[2] == s.getColor()) {

			Iterator<Stone> it = lc[2].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				c.addToChain(next, this.board);
			} // end while

			if (lc[2].getChainIndex() != c.getChainIndex()
					&& realChainIndex(lc[2].getChainIndex()) != -1) {
				chains.remove(realChainIndex(lc[2].getChainIndex()));
			}

		} // end if

		// chain to left
		if (l[3] == s.getColor()) {

			Iterator<Stone> it = lc[3].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				c.addToChain(next, this.board);
			} // end while

			if (lc[3].getChainIndex() != c.getChainIndex()
					&& realChainIndex(lc[3].getChainIndex()) != -1) {
				chains.remove(realChainIndex(lc[3].getChainIndex()));
			}
		} // end if

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

		// chain top-left
		if (lc[4] != null) {
			if (l[4] == -1 && lc[4].isYan()) {
				lc[4].realYandetector();
			}// end if
		}

		// chain top-right
		if (lc[5] != null) {
			if (l[5] == -1 && lc[5].isYan()) {
				lc[5].realYandetector();
			}// end if
		}

		// chain bottom-right
		if (lc[6] != null) {
			if (l[6] == -1 && lc[6].isYan()) {
				lc[6].realYandetector();
			}// end if
		}

		// chain bottom-left
		if (lc[7] != null) {
			if (l[7] == -1 && lc[7].isYan()) {
				lc[7].realYandetector();
			}// end if
		}

		// ************Add stone into Wei************************
		// wei above
		if (l[0] == s.getColor()) {

			Iterator<Stone> it = lw[0].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next);

			} // end while

			if (lw[0].getWeiIndex() != w.getWeiIndex()
					&& realWeiIndex(lw[0].getWeiIndex()) != -1) {
				weis.remove(realWeiIndex(lw[0].getWeiIndex()));
			}
		}// end if

		// wei to right
		if (l[1] == s.getColor()) {

			Iterator<Stone> it = lw[1].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next);

			} // end while

			if (lw[1].getWeiIndex() != w.getWeiIndex()
					&& realWeiIndex(lw[1].getWeiIndex()) != -1) {
				weis.remove(realWeiIndex(lw[1].getWeiIndex()));
			}
		} // end if

		// wei below
		if (l[2] == s.getColor()) {

			Iterator<Stone> it = lw[2].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next);
			} // end while

			if (lw[2].getWeiIndex() != w.getWeiIndex()
					&& realWeiIndex(lw[2].getWeiIndex()) != -1) {
				weis.remove(realWeiIndex(lw[2].getWeiIndex()));
			}

		} // end if

		// chain to left
		if (l[3] == s.getColor()) {

			Iterator<Stone> it = lw[3].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next);
			} // end while

			if (lw[3].getWeiIndex() != w.getWeiIndex()
					&& realWeiIndex(lw[3].getWeiIndex()) != -1) {
				weis.remove(realWeiIndex(lw[3].getWeiIndex()));
			}
		} // end if

		// wei top-left
		if (l[4] == s.getColor()) {

			Iterator<Stone> it = lw[4].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next);
			} // end while

			if (lw[4].getWeiIndex() != w.getWeiIndex()
					&& realWeiIndex(lw[4].getWeiIndex()) != -1) {
				weis.remove(realWeiIndex(lw[4].getWeiIndex()));
			}
		}// end if

		// wei top-right
		if (l[5] == s.getColor()) {

			Iterator<Stone> it = lw[5].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next);
			} // end while

			if (lw[5].getWeiIndex() != w.getWeiIndex()
					&& realWeiIndex(lw[5].getWeiIndex()) != -1) {
				weis.remove(realWeiIndex(lw[5].getWeiIndex()));
			}

		}// end if

		// wei bottom-right
		if (l[6] == s.getColor()) {

			Iterator<Stone> it = lw[6].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next);
			} // end while

			if (lw[6].getWeiIndex() != w.getWeiIndex()
					&& realWeiIndex(lw[6].getWeiIndex()) != -1) {
				weis.remove(realWeiIndex(lw[6].getWeiIndex()));
			}
		}// end if

		// wei bottom-left
		if (l[7] == s.getColor()) {

			Iterator<Stone> it = lw[7].iterator();
			while (it.hasNext()) {
				Stone next = it.next();
				w.addToWei(next);
			} // end while

			if (lw[7].getWeiIndex() != w.getWeiIndex()
					&& realWeiIndex(lw[7].getWeiIndex()) != -1) {
				weis.remove(realWeiIndex(lw[7].getWeiIndex()));
			}
		}// end if

		// detect if wei contains Yan. If so, turn them into Yan and add into
		// Chain list.

		// if stone color = 3 or 4, trade it as empty, so that we do not call
		// yanDetector() and tizi()
		ArrayList<Chain> removedChains = new ArrayList<Chain>();
		if (s.getColor() != 3 && s.getColor() != 4) {
			w.yanDetector();
			removedChains = this.tizi();
		}

		return removedChains;
	} // end addStone()

	public ArrayList<Chain> tizi() {
		ArrayList<Chain> removedChains = new ArrayList<Chain>();
		// check each chain, if chain's Qis equals to zero, change them into Yan
		for (int i = 0; i < this.chains.size(); i++) {
			Chain c = this.chains.get(i);
			c.recheckQis();
			Stone s = c.first();
			Point p = s.getLocation();
			Wei w = s.getWei();

			// if chain c's Qis equals to zero , turn all the stone into Qis
			if (c.getQis().size() == 0 && !c.isYan()) {

				Chain copyC = new Chain(this);
				Iterator<Stone> it3 = c.iterator();
				Stone firstStone = c.first();
				if (firstStone.getColor() != 4 && firstStone.getColor() != 3) {
					// make a copy of chain
					while (it3.hasNext()) {
						Stone next = it3.next();
						copyC.add((Stone) next.clone());
					}
					removedChains.add(copyC);
				}

				int r = 0;
				Iterator<Stone> it2 = c.iterator();
				while (it2.hasNext()) {

					Stone next = it2.next();

					if (next.getWei() != null
							&& realWeiIndex(next.getWei().getWeiIndex()) != -1) {
						if (w.size() == 0) {
							this.weis.remove(realWeiIndex(next.getWei()
									.getWeiIndex()));
						} else if (w.size() == 1) {
							w.remove(next);
							this.weis.remove(realWeiIndex(next.getWei()
									.getWeiIndex()));
						} else {
							w.remove(next);
						}
					}

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
						LastTiziPosition = next.getLocation();
						r++;
					}

					next.setColor(-1);
					next.setYan(true);

					s = next;
					p = s.getLocation();
					this.board[p.x][p.y] = s;

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
						stTop = this.board[top.x][top.y];
						stTop.getChain().addBackQis(stTop, s);
					}

					if (right.x < size && (!stRight.isYan())) {
						stRight = this.board[right.x][right.y];
						stRight.getChain().addBackQis(stRight, s);
					}

					if (bottom.y > -1) {
						stBottom = this.board[bottom.x][bottom.y];
						stBottom.getChain().addBackQis(stBottom, s);
					}

					if (left.x > -1) {
						stLeft = this.board[left.x][left.y];
						stLeft.getChain().addBackQis(stLeft, s);
					}
				} // end while

				LastTiziNum = r;
				c.setYan(true);

				// clean the Qis for the Yan
				TreeSet<Qi> empty = new TreeSet<Qi>();
				c.setQis(empty);
				c.realYandetector();

			} // end if
		} // end for
		return removedChains;
	}// end tizi()

	public int realChainIndex(int indexFound) {
		int result = 0;
		for (int i = 0; i < chains.size(); i++) {
			if (indexFound == chains.get(i).getChainIndex()) {
				return result;
			} else {
				result++;
			}
		}
		// did not find the result
		return -1;
	}// end realChainIndex()

	public int realWeiIndex(int indexFound) {
		int result = 0;
		for (int i = 0; i < weis.size(); i++) {
			if (indexFound == weis.get(i).getWeiIndex()) {
				return result;
			} else {
				result++;
			}
		}
		// did not find the result
		return -1;
	}// end rrealWeiIndex()

	public double[] getScores() {
		double[] scores = { 0.0f, 0.0f };
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {
				if (this.board[i][j] != null) {
					if (this.board[i][j].getBelongto() == 0)
						scores[0]++;
					else if (this.board[i][j].getBelongto() == 1)
						scores[1]++;
				} // end if
			} // end for
		} // end for
		scores[1] += this.Komi;
		return scores;
	} // end scores

	public void printScores() {

		double scoreBlack = 0;
		double scoreWhite = 0;
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {

				if (this.board[i][j] != null) {
					if (this.board[i][j].getBelongto() == 0) {
						scoreBlack++;
					} else if (this.board[i][j].getBelongto() == 1) {
						scoreWhite++;
					}
				}
			} // end for
		}

		scoreWhite = scoreWhite + Komi;
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("Score:");
		System.out.println("Komi =" + Komi);
		System.out.println("Black Player =" + scoreBlack);
		System.out.println("White Player =" + scoreWhite);

		if (scoreBlack > scoreWhite) {
			System.out.println("Bloack Player won this game!");
		} else {
			System.out.println("White Player won this game!");
		}

	}

	public void printMoves() {
		Iterator<Move> it = moves.iterator();
		while (it.hasNext()) {
			Move next = it.next();
			System.out.println(next.toString());
			System.out.println("========================");

		}
	}

	public void printChains() {
		System.out.println("" + this.chains.size() + " chains");
		for (int i = 0; i < this.chains.size(); i++) {
			System.out.println("*************");

			Chain c = this.chains.get(i);
			System.out.println("Chain " + c.getChainIndex() + ": ["
					+ "isYan = " + c.isYan() + " isZhenYan = " + c.isZhenYan()
					+ " isJiaYan = " + c.isJiaYan() + "]");
			System.out.println(c.toString().replaceAll("],", "]\n"));
			System.out.println("Total Stone #: " + Integer.toString(c.size()));
			System.out.println(c.getQis().toString().replaceAll("],", "]\n"));
			System.out.println("Total Qi #: "
					+ Integer.toString(c.getQis().size()));
		} // end for
		System.out.println("=================");
	} // end printChains()

	public void printWeis() {
		System.out.println("" + this.weis.size() + " wei");
		for (int i = 0; i < this.weis.size(); i++) {
			System.out.println("*************");

			Wei w = this.weis.get(i);
			System.out.println("Wei " + w.getWeiIndex());
			System.out.println(w.toString().replaceAll("],", "]\n"));
			System.out.println("Total Stone #: " + Integer.toString(w.size()));
		} // end for
		System.out.println("=================");
	} // end printChains()

	public void printBoard() {
		for (int j = this.size - 1; j > -1; j--) {
			for (int i = 0; i < this.size; i++) {
				if (this.board[i][j] != null) {
					if (this.board[i][j].getColor() == 0
							|| this.board[i][j].getColor() == 1
							|| this.board[i][j].getColor() == 3
							|| this.board[i][j].getColor() == 4) {
						System.out.print("[+" + this.board[i][j].getColor()
								+ "]");
					} else {
						System.out.print("[" + this.board[i][j].getColor()
								+ "]");
					}
				} else
					System.out.print("[**]");
				System.out.print(" ");
			} // end for
			System.out.println(" ");
		} // end for

		System.out.println();

	} // end printBoard()

	public void newGame() {
		this.board = new Stone[this.size][this.size];
		this.chains = new ArrayList<Chain>();
	} // end newGame()

	public Stone getStone(Point p) {
		Stone result = this.board[p.x][p.y];
		if (result == null)
			result = new Stone(p);
		return result;
	} // end getStone()

	public Stone[][] getBoard() {
		return this.board;
	} // end getBoard()

	public int getSize() {
		return this.size;
	} // end getSize()

	public void setSize(int size) {
		this.size = size;
	} // end setSize()

	public void setMoves(Stack<Move> moves) {
		this.moves = moves;
	}

	public Stack<Move> getMoves() {
		return moves;
	}
} // end class
