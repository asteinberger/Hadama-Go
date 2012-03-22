import java.awt.Point;
import java.util.Random;

/**
 * GamePlay - place stones on board for both human and machine players. Legal
 * moves in go include passing, placing a stone in an area that's not completely
 * surrounded by enemies, and sukuru ("double jeopardy") which is placing a
 * stone where you take an enemy's stone and then your opponent takes that
 * stone. Illegal moves are where the stone is completely surrounded by enemies,
 * or a stone color 0 or 1 is already on the board at its location.
 * 
 * @author Haoran Ma <mahaoran1020@gmail.com>, Adam Steinberger
 *         <steinz08@gmail.com>
 * 
 */
public class GamePlay {

	private int color;
	private boolean gameOver = false;
	private boolean justPassed = false;
	private String mode;

	/**
	 * Create new GamePlay object for a new game with a new board.
	 * 
	 * @param m
	 *            mode
	 * @param s
	 *            size of goBoard
	 */
	public GamePlay(String m) {
		this.color = 0;
		m = m;
	} // end constructor

	/**
	 * Create new GamePlay object given full parameter set for object variables.
	 * 
	 * @param m
	 *            game mode
	 * @param s
	 *            size of board
	 * @param c
	 *            color
	 */
	public GamePlay(String m, int c) {
		m = m;
		this.color = c;
	} // end constructor

	private int getStoneColor(Board b, int x, int y) {
		Stone s = new Stone(b);
		Point p = new Point(x, y);
		if (b.getStone(p) != null)
			s = b.getStone(p);
		int color = 1;
		if (s != null)
			color = s.getColor();
		return color;
	} // end getStoneColor()

	private void toggleColor(int c) {
		this.color = 0;
		if (c == 0)
			this.color = 1;
	} // end toggleColor()

	/**
	 * Place a Stone onto the board.
	 * 
	 * @param b
	 *            board
	 * @param c
	 *            color of stone. player 1 has color 0 and player 2 has color 1;
	 *            while empty stone has color 0 and yan has color -1.
	 */
	public Board placePiece(Board b, String m, Point l, int c) {

		Point p = new Point(l.x, b.getSize() - l.y - 1);
		Stone s = b.getStone(p);

		if (s.getChain() != null) {

			// if stone's color is 3, you can put there any time
			if ((s.getColor() == 3) && (s.getColor() != 0)
					&& (s.getColor() != 1)) {

				Point a = new Point(l.x, b.getSize() - l.y - 1);
				Stone st = new Stone(b, c, a);
				b.addStone(st);
				this.justPassed = false;

				if (m.equals("HvC") || m.equals("CvH")) {
					if (c == 0)
						this.moveEnemy(b, 1);
					else
						this.moveEnemy(b, 0);
				} else
					this.toggleColor(c);

			} // end if

			// if it is Yan and only one empty place left
			if (s.getChain().size() == 1) {

				Chain[] lc = s.checkChains();
				int numTizi = 0;
				int numHuozi = 0;
				int numTiziStone = 0;

				for (int i = 0; i < 4; i++) {

					if (lc[i] != null) {

						if ((lc[i].first().getColor() != c)
								&& (lc[i].getQis().size() == 1)) {
							numTizi++;
							numTiziStone = lc[i].size();
						} // end if

						if ((lc[i].first().getColor() == c)
								&& (lc[i].getQis().size() > 1))
							numHuozi++;

					} // end if

				} // end for

				if ((numTizi > 0) && (s.getColor() != 0) && (s.getColor() != 1)) {

					if (!b.getLastTiziPosition().equals(p)) {

						Point a = new Point(l.x, b.getSize() - l.y - 1);
						Stone st = new Stone(b, c, a);
						b.addStone(st);
						this.justPassed = false;

						if (m.equals("HvC") || m.equals("CvH")) {
							if (c == 0)
								this.moveEnemy(b, 1);
							else
								this.moveEnemy(b, 0);
						} else
							this.toggleColor(c);

					} else if (numTiziStone == 2) {

						Point a = new Point(l.x, b.getSize() - l.y - 1);
						Stone st = new Stone(b, c, a);
						b.addStone(st);
						this.justPassed = false;

						if (m.equals("HvC") || m.equals("CvH")) {
							if (c == 0)
								this.moveEnemy(b, 1);
							else
								this.moveEnemy(b, 0);
						} else
							this.toggleColor(c);

					} // end if

				} // end if

				if ((numHuozi > 0) && (s.getColor() != 0)
						&& (s.getColor() != 1)) {

					Point a = new Point(l.x, b.getSize() - l.y - 1);
					Stone st = new Stone(b, c, a);
					b.addStone(st);
					this.justPassed = false;

					if (m.equals("HvC") || m.equals("CvH")) {
						if (c == 0)
							this.moveEnemy(b, 1);
						else
							this.moveEnemy(b, 0);

					} else
						this.toggleColor(c);

				} // end if

			} else if ((s.getColor() != 0) && (s.getColor() != 1)) {

				Point a = new Point(l.x, b.getSize() - l.y - 1);
				Stone st = new Stone(b, c, a);
				b.addStone(st);
				this.justPassed = false;

				if (m.equals("HvC") || m.equals("CvH")) {
					if (c == 0)
						this.moveEnemy(b, 1);
					else
						this.moveEnemy(b, 0);
				} else
					this.toggleColor(c);

			} // end if

		} else if ((s.getColor() != 0) && (s.getColor() != 1)) {

			Point a = new Point(l.x, b.getSize() - l.y - 1);
			Stone st = new Stone(b, c, a);
			b.addStone(st);
			this.justPassed = false;

			if (m.equals("HvC") || m.equals("CvH")) {
				if (c == 0)
					this.moveEnemy(b, 1);
				else
					this.moveEnemy(b, 0);
			} else
				this.toggleColor(c);

		} // end if

		return b;

	} // end placePiece()

	/**
	 * Have enemy place a stone onto the board
	 * 
	 * @param b
	 *            board
	 * @param c
	 *            color of enemy stones
	 */
	public Board moveEnemy(Board b, int c) {

		if (!this.gameOver) {

			int oppX = 0;
			int oppY = 0;
			int clr = 0;
			boolean freeSpace = false;

			for (int i = 0; i < b.getSize(); i++) {
				for (int j = 0; j < b.getSize(); j++) {
					clr = this.getStoneColor(b, i, j);
					Point p = new Point(i, j);
					boolean isYan = b.getStone(p).isYan();
					if (!isYan)
						freeSpace = true;
				} // end for
			} // end for

			if (freeSpace) {

				boolean isYan = false;
				int size = 0;

				do {

					Random rand = new Random(System.currentTimeMillis());
					oppX = rand.nextInt(b.getSize());
					oppY = rand.nextInt(b.getSize());

					int randPass = rand.nextInt(999);
					if (randPass == 900) {
						if (this.justPassed) {
							this.gameOver = true;
							this.gameOver();
						} else {
							this.justPassed = true;
						} // end if
					} else
						this.justPassed = false;

					Point p = new Point(oppX, oppY);
					clr = this.getStoneColor(b, oppX, oppY);
					isYan = b.getStone(p).isYan();
					size = b.getStone(p).getChain().size();

				} while (!(isYan && (this.color != clr))
						|| !(isYan && (size == 0)) || (clr == 0) || (clr == 1));
				Point a = new Point(oppX, oppY);
				Stone s = new Stone(b, c, a);
				b.addStone(s);

			} // end if

		} else
			this.gameOver();

		return b;

	} // end moveOpponent()

	public void forfeit(int p) {
		System.out.println("Player " + Integer.toString(p) + " forfeits!");
		this.gameOver();
	} // end forfeit()

	public void gameOver() {
		System.out.println("Player " + Integer.toString(this.color)
				+ " ended game!");
	} // end forfeit()

	/*
	 * getters and setters
	 */
	public int getColor() {
		return this.color;
	} // end getColor()

	public void setColor(int c) {
		this.color = c;
	} // end setColor()

	public boolean isGameOver() {
		return this.gameOver;
	} // end isGameOver()

	public void setGameOver(boolean go) {
		this.gameOver = go;
	} // end setGameOver()

	public boolean isJustPassed() {
		return this.justPassed;
	} // end isJustPassed()

	public void setJustPassed(boolean jp) {
		this.justPassed = jp;
	} // end setJustPassed()

} // end class
