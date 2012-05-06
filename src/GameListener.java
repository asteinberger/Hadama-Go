import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Top (39,38); Bottom (360,348)
 * 
 * 
 * @author asteinb1
 * 
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
		if (s.equals("pass turn")) {
			try {
				this.pass();
			} catch (Exception e1) {
				e1.printStackTrace();
			} // end try
		} else if (s.equals("forfeit")) {
			try {
				this.forfeit();
			} catch (Exception e) {
				e.printStackTrace();
			} // end try
		} else if (s.equals("undo move"))
			this.undoMove();
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
		} catch (Exception e) {
			e.printStackTrace();
		} // end try
	} // end keyTyped()

	private void movePiece(char ch) throws Exception {
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
		case ']':
			this.goBoard.printStones();
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

	private void forfeit() throws Exception {
		this.gamePlay.forfeit(this.color);
		this.gameOver = true;
		this.gamePlay.setGameOver(this.gameOver);
	} // end forfeit()

	private void newGame() {
		this.gameOver = false;
		this.gamePlay.setGameOver(this.gameOver);
		this.gamePlay.newGame();
		this.gamePlay.setPlayer(0);
		this.color = 0;
		HadamaGo.getGoPanel().paint(HadamaGo.getGoPanel().getGraphics());
	} // end newGame()

	private void undoMove() {
		this.gamePlay.undoMove(this.goBoard);
		this.togglePlayer();
		HadamaGo.getGoPanel().setColor(this.color);
		HadamaGo.getGoPanel().paint(HadamaGo.getGoPanel().getGraphics());
	} // end undoMove()

	private void placePiece() throws Exception {
		this.gamePlay.placePiece(this.goBoard, this.color);
		this.gameOver = this.gamePlay.isGameOver();
		this.color = this.gamePlay.getPlayer();
		this.ait.setGoboard(this.goBoard);
		GoPanel gp = HadamaGo.getGoPanel();
		gp.setColor(this.color);
		HadamaGo.setGoPanel(gp);
		HadamaGo.getGoPanel().paint(HadamaGo.getGoPanel().getGraphics());
	} // end placePiece()

	private void pass() throws Exception {

		if (this.gamePlay.getJustPassed()) {

			this.gameOver = true;
			this.gamePlay.setGameOver(this.gameOver);
			this.gamePlay.gameOver();

		} else {

			Stone ns = new Stone();
			ns.setColor(this.color);
			ArrayList<Chain> removedChains = new ArrayList<Chain>();

			Move newMove = new Move(ns, removedChains, null, true,
					this.goBoard.getLastTiziPosition(),
					this.goBoard.getLastTiziNum());

			this.gamePlay.getGoboard().getMoves().push(newMove);
			this.togglePlayer();
			this.gamePlay.setJustPassed(true);

		} // end if

		this.gamePlay.setPlayer(this.color);
		HadamaGo.getGoPanel().setColor(this.color);
		HadamaGo.getGoPanel().paint(HadamaGo.getGoPanel().getGraphics());

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