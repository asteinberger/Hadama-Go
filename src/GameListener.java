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

	private static Point cellDimensions = new Point(35, 35);
	private static Point borderTopLeft = new Point(200, 184);
	private HadamaGo hadamaGo;
	private GoBoard goBoard;
	private int boardSize = 9;
	private Point point;
	private int[] verticals;
	private int[] horizontals;
	private GamePlay gamePlay;
	private Player player = Player.BLACK;
	private GameMode mode = GameMode.HUMAN_VS_HUMAN;
	private boolean gameOver = false;

	public GameListener(HadamaGo hadamaGo, GoBoard goBoard, int boardSize, GamePlay gamePlay) {
		this.hadamaGo = hadamaGo;
		this.boardSize = boardSize;
		this.goBoard = goBoard;
		this.point = new Point(GameListener.borderTopLeft.x,
				GameListener.borderTopLeft.y);
		this.verticals = new int[boardSize];
		this.horizontals = new int[boardSize];
		for (int index = 0; index < boardSize; index++) {
			this.horizontals[index] = GameListener.borderTopLeft.y
					+ ((this.boardSize - index - 1) * GameListener.cellDimensions.y);
			this.verticals[index] = GameListener.borderTopLeft.x
					+ (index * GameListener.cellDimensions.x);
		} // end for
		this.gamePlay = gamePlay;
	} // end constructor

	@Override
	// Menu Option Events
	public void actionPerformed(ActionEvent actionEvent) {
		String actionCommand = actionEvent.getActionCommand();
		if (actionCommand.equals("pass turn")) {
			try {
				this.pass();
			} catch (Exception exception) {
				exception.printStackTrace();
			} // end try
		} else if (actionCommand.equals("forfeit")) {
			try {
				this.forfeit();
			} catch (Exception exception) {
				exception.printStackTrace();
			} // end try
		} else if (actionCommand.equals("undo move"))
			this.undoMove();
		else if (actionCommand.equals("new game"))
			this.newGame();
	} // end actionPerformed()

	@Override
	public void keyPressed(KeyEvent keyEvent) {
	} // end keyPressed()

	@Override
	public void keyReleased(KeyEvent keyEvent) {
	} // end keyReleased()

	@Override
	public void keyTyped(KeyEvent keyEvent) {
		char keyChar = keyEvent.getKeyChar();
		try {
			this.movePiece(keyChar);
		} catch (Exception exception) {
			exception.printStackTrace();
		} // end try
	} // end keyTyped()

	private void movePiece(char ch) throws Exception {
		if (!this.gameOver) {
			switch (ch) {
			case 'A':
			case 'a':
				HadamaGo.getGamePanel().moveLeft();
				break;
			case 'D':
			case 'd':
				HadamaGo.getGamePanel().moveRight();
				break;
			case 'S':
			case 's':
				HadamaGo.getGamePanel().moveDown();
				break;
			case 'W':
			case 'w':
				HadamaGo.getGamePanel().moveUp();
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
	public void mouseClicked(MouseEvent mouseEvent) {
	} // end mouseClicked()

	@Override
	public void mouseEntered(MouseEvent mouseEvent) {
	} // end mouseEntered()

	@Override
	public void mouseExited(MouseEvent mouseEvent) {
	} // end mouseExited()

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
	} // end mousePressed()

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
	} // end mouseReleased()

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
	} // end mouseDragged()

	@Override
	public void mouseMoved(MouseEvent mouseEvent) {
	} // end mouseMoved()

	private void forfeit() throws Exception {
		this.gamePlay.forfeit(this.player);
		this.gameOver = true;
		this.gamePlay.setGameOver(true);
	} // end forfeit()

	private void newGame() {
		this.gameOver = false;
		this.gamePlay.setGameOver(false);
		this.gamePlay.newGame();
		this.gamePlay.setPlayer(Player.BLACK);
		this.player = Player.BLACK;
		HadamaGo.getGamePanel().paint(HadamaGo.getGamePanel().getGraphics());
	} // end newGame()

	private void undoMove() {
		this.gamePlay.undoMove(this.goBoard);
		this.togglePlayer();
		HadamaGo.getGamePanel().setPlayer(this.player);
		HadamaGo.getGamePanel().paint(HadamaGo.getGamePanel().getGraphics());
	} // end undoMove()

	private void placePiece() throws Exception {
		this.gamePlay.placePiece(this.goBoard, this.player);
		this.gameOver = this.gamePlay.isGameOver();
		this.player = this.gamePlay.getPlayer();
		this.hadamaGo.setGameBoard(this.goBoard);
		GoPanel goPanel = HadamaGo.getGamePanel();
		goPanel.setPlayer(this.player);
		HadamaGo.setGamePanel(goPanel);
		HadamaGo.getGamePanel().paint(HadamaGo.getGamePanel().getGraphics());
	} // end placePiece()

	private void pass() throws Exception {

		if (this.gamePlay.getJustPassed()) {

			this.gameOver = true;
			this.gamePlay.setGameOver(true);
			this.gamePlay.gameOver();

		} else {

			Stone stone = new Stone();
			stone.setPlayer(this.player);
			ArrayList<Chain> removedChains = new ArrayList<Chain>();

			Move newMove = new Move(stone, removedChains, null, true,
					this.goBoard.getLastTiziPosition(),
					this.goBoard.getLastTiziNumber());

			this.gamePlay.getGoBoard().getMoves().push(newMove);
			this.togglePlayer();
			this.gamePlay.setJustPassed(true);

		} // end if

		this.gamePlay.setPlayer(this.player);
		HadamaGo.getGamePanel().setPlayer(this.player);
		HadamaGo.getGamePanel().paint(HadamaGo.getGamePanel().getGraphics());

	} // end pass()

	private void togglePlayer() {
		if (this.player == Player.BLACK)
			this.player = Player.WHITE;
		else if (this.player == Player.WHITE)
			this.player = Player.BLACK;
		else
			this.player = Player.NOT_A_PLAYER;
	} // end togglePlayer()

	/*
	 * getters and setters
	 */
	public GameMode getMode() {
		return this.mode;
	} // end getMode()

	public void setMode(GameMode mode) {
		this.mode = mode;
	} // end setMode()

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

} // end class