import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * GameListener.java - Handle user input for game graphic user interface.
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
public class GameListener implements KeyListener, ActionListener,
		MouseListener, MouseMotionListener {

	private static Point cellDim = new Point(35, 35);
	private static Point borderTopLeft = new Point(200, 184);
	private static Point borderBottomRight = new Point(493, 463);
	private HadamaGo ait;
	private Board goBoard;
	private int size = 9;
	private Point location;
	private Point point;
	private Point intersection;
	private int[] verticals;
	private int[] horizontals;
	private GamePlay gamePlay;
	private int color = 0;
	private String mode = "HvH";
	private boolean gameOver = false;

	public GameListener(HadamaGo ait, Board b, int s, GamePlay gp) {
		this.ait = ait;
		this.size = s;
		this.goBoard = b;
		this.location = new Point(0, s - 1);
		this.point = new Point(GameListener.borderTopLeft.x,
				GameListener.borderTopLeft.y);
		this.intersection = new Point(0, 0);
		this.verticals = new int[s];
		this.horizontals = new int[s];
		for (int i = 0; i < s; i++) {
			this.horizontals[i] = GameListener.borderTopLeft.y
					+ ((this.size - i - 1) * GameListener.cellDim.y);
			this.verticals[i] = GameListener.borderTopLeft.x
					+ (i * GameListener.cellDim.x);
		} // end for
		this.gamePlay = gp;
	} // end constructor

	@Override
	// Menu Option Events
	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		if (s.equals("pass turn"))
			this.pass();
		else if (s.equals("forfeit"))
			this.forfeit();
		else if (s.equals("undo move"))
			try {
				this.undoMove();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else if (s.equals("new game"))
			this.newGame();
	} // end actionPerformed()

	@Override
	public void keyPressed(KeyEvent arg0) {
	} // end keyPressed()

	@Override
	public void keyReleased(KeyEvent arg0) {
	} // end keyReleased()

	@Override
	public void keyTyped(KeyEvent arg0) {
		char ch = arg0.getKeyChar();
		try {
			this.movePiece(ch);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} // end keyTyped()

	private void movePiece(char ch) throws CloneNotSupportedException,
			InterruptedException {
		if (!this.gameOver) {
			switch (ch) {
			case 'A':
			case 'a':
				HadamaGo.getGoPanel().moveLeft();
				break;
			case 'D':
			case 'd':
				HadamaGo.getGoPanel().moveRight();
				break;
			case 'S':
			case 's':
				HadamaGo.getGoPanel().moveDown();
				break;
			case 'W':
			case 'w':
				HadamaGo.getGoPanel().moveUp();
				break;
			case ' ':
				this.placePiece();
				break;
			case 'P':
			case 'p':
				this.pass();
				break;
			case '.':
				System.out.println(this.gamePlay.getJustPassed());
				break;
			case 'U':
			case 'u':
				this.undoMove();
				break;
			case 'I':
			case 'i':
				this.goBoard.printIllegalMoves();
				break;
			case 'K':
			case 'k':
				this.forfeit();
				break;
			} // end switch
		} // end if
		switch (ch) {
		case 'B':
		case 'b':
			this.goBoard.printBoard();
			break;
		case 'C':
		case 'c':
			this.goBoard.printChains();
			break;
		case 'T':
		case 't':
			this.goBoard.printScores();
			break;
		case 'L':
		case 'l':
			this.goBoard.printWeis();
			break;
		case 'M':
		case 'm':
			this.goBoard.printMoves();
			break;
		case 'N':
		case 'n':
			this.newGame();
			break;
		case 'Q':
		case 'q':
			System.exit(0);
			break;
		} // end switch
	} // end movePiece()

	@Override
	public void mouseClicked(MouseEvent arg0) {
	} // end mouseClicked()

	@Override
	public void mouseEntered(MouseEvent arg0) {
	} // end mouseEntered()

	@Override
	public void mouseExited(MouseEvent arg0) {
	} // end mouseExited()

	@Override
	public void mousePressed(MouseEvent arg0) {
	} // end mousePressed()

	@Override
	public void mouseReleased(MouseEvent arg0) {
	} // end mouseReleased()

	@Override
	public void mouseDragged(MouseEvent arg0) {
	} // end mouseDragged()

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// System.out.println(Integer.toString(arg0.getX()) + ", "
		// + Integer.toString(arg0.getY()));
	} // end mouseMoved()

	private void forfeit() {
		this.gamePlay.forfeit(this.color);
		this.gameOver = true;
		this.gamePlay.setGameOver(this.gameOver);
		this.updateScores();
	} // end forfeit()

	private void newGame() {
		this.gameOver = false;
		this.gamePlay.setGameOver(this.gameOver);
		this.gamePlay.newGame();
		this.gamePlay.setPlayer(0);
		this.color = 0;
		HadamaGo.getGoPanel().paint(HadamaGo.getGoPanel().getGraphics());
		this.updateScores();
	} // end newGame()

	public void undoMove() throws CloneNotSupportedException,
			InterruptedException {
		this.gamePlay.undoMove(this.goBoard);
		this.togglePlayer();
		HadamaGo.getGoPanel().setColor(this.color);
		HadamaGo.getGoPanel().paint(HadamaGo.getGoPanel().getGraphics());
		this.updateScores();
	} // end undoMove()

	public void placePiece() throws CloneNotSupportedException,
			InterruptedException {
		this.gamePlay.placePiece(this.goBoard, this.color);
		this.gameOver = this.gamePlay.isGameOver();
		this.color = this.gamePlay.getPlayer();
		this.ait.setGoboard(this.goBoard);
		GoPanel gp = HadamaGo.getGoPanel();
		gp.setColor(this.color);
		HadamaGo.setGoPanel(gp);
		HadamaGo.getGoPanel().paint(HadamaGo.getGoPanel().getGraphics());
		this.updateScores();
	} // end placePiece()

	private void updateScores() {
		double[] scores = this.goBoard.getScores();
		HadamaGo.getScoreBlack().setText(
				"Black = " + Double.toString(scores[0]));
		HadamaGo.getScoreWhite().setText(
				"White = " + Double.toString(scores[1]));
	} // end updateScores()

	private void pass() {
		if (this.gamePlay.getJustPassed()) {
			this.gameOver = true;
			this.gamePlay.setGameOver(this.gameOver);
			this.gamePlay.gameOver();
		} else {

			Stone ns = new Stone();
			ns.setColor(this.color);
			ArrayList<Chain> removedChains = new ArrayList<Chain>();
			Move newMove = new Move(ns, removedChains, null, true);
			this.gamePlay.getGoboard().getMoves().push(newMove);
			this.togglePlayer();
			this.gamePlay.setJustPassed(true);

		} // end if

		this.gamePlay.setPlayer(this.color);
		HadamaGo.getGoPanel().setColor(this.color);
		HadamaGo.getGoPanel().paint(HadamaGo.getGoPanel().getGraphics());
		this.updateScores();
	} // end pass()

	private void togglePlayer() {
		if (this.color == 0)
			this.color = 1;
		else if (this.color == 1)
			this.color = 0;
		else
			this.color = -1;
	} // end togglePlayer()

	/*
	 * getters and setters
	 */
	public HadamaGo getAit() {
		return this.ait;
	} // end getAit()

	public void setAit(HadamaGo ait) {
		this.ait = ait;
	} // end setAit()

	public static Point getCell() {
		return GameListener.cellDim;
	} // end getCell()

	public static void setCell(Point cell) {
		GameListener.cellDim = cell;
	} // end setCell()

	public static Point getBorderTopLeft() {
		return GameListener.borderTopLeft;
	} // end getBorderTopLeft()

	public static void setBorderTopLeft(Point borderTopLeft) {
		GameListener.borderTopLeft = borderTopLeft;
	} // end setBorderTopLeft()

	public static Point getBorderBottomRight() {
		return GameListener.borderBottomRight;
	} // end getBorderBottomRight()

	public static void setBorderBottomRight(Point borderBottomRight) {
		GameListener.borderBottomRight = borderBottomRight;
	} // end setBorderBottomRight()

	public String getMode() {
		return this.mode;
	} // end getMode()

	public void setMode(String mode) {
		this.mode = mode;
	} // end setMode()

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Point getIntersection() {
		return intersection;
	}

	public void setIntersection(Point intersection) {
		this.intersection = intersection;
	}

} // end class