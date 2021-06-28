import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * http://www.roseindia.net/java/example/java/awt/AwtImage.shtml
 * 
 * dx = 40 dy = 39 Top (39,38) Bottom (360,348)
 * 
 * 
 * @author asteinb1
 * 
 */
public class GoPanel extends Panel {

	/**
	 * Version ID required for Hadama Go.
	 */
	private static final long serialVersionUID = 4781863948394812333L;

	private GoBoard goBoard;
	private Point location;
	private Point point;
	private Point intersection;
	private static Image background;
	private static Image blackStone;
	private static Image whiteStone;
	private static Image lastMove;
	private static Image cursor;
	private static Image highlight;
	private Player player = Player.BLACK;
	private int[] verticals;
	private int[] horizontals;
	private GamePlay gamePlay;
	private GameMode mode = GameMode.HUMAN_VS_HUMAN;
	private int boardSize;

	private static Point cellDimensions = new Point(35, 35);
	private static Point borderTopLeft = new Point(200, 184);
	private static Point borderBottomRight = new Point(493, 463);

	public GoPanel(GoBoard goBoard, GamePlay gamePlay) throws IOException {

		GoPanel.background = ImageIO.read(new File("images/board.gif"));

		GoPanel.blackStone = ImageIO.read(new File("images/goStoneBlack.gif"));

		GoPanel.whiteStone = ImageIO.read(new File("images/goStoneWhite.gif"));
		GoPanel.lastMove = ImageIO.read(new File("images/lastMove.gif"));

		GoPanel.cursor = ImageIO.read(new File("images/goCursor.gif"));
		GoPanel.highlight = ImageIO.read(new File("images/goHighlight.gif"));

		this.setSize(400, 386);

		this.boardSize = goBoard.getBoardSize();
		this.location = new Point(0, goBoard.getBoardSize() - 1);
		this.point = new Point(GoPanel.borderTopLeft.x,
				GoPanel.borderBottomRight.y);

		this.intersection = new Point(0, 0);
		this.verticals = new int[goBoard.getBoardSize()];
		this.horizontals = new int[goBoard.getBoardSize()];

		for (int index = 0; index < goBoard.getBoardSize(); index++) {
			this.horizontals[index] = GoPanel.borderTopLeft.y
					+ ((goBoard.getBoardSize() - index - 1) * GoPanel.cellDimensions.y);
			this.verticals[index] = GoPanel.borderTopLeft.x
					+ (index * GoPanel.cellDimensions.x);
		} // end for

		this.gamePlay = gamePlay;
		this.goBoard = goBoard;

	} // end constructor

	public void paint(Graphics graphics) {

		graphics.drawImage(GoPanel.background, 0, 0, null);

		// display pieces on board
		for (int x = 0; x < this.boardSize; x++) {

			for (int y = 0; y < this.boardSize; y++) {

				Stone stone = this.goBoard.getStone(new Point(x, y));
				Image image = GoPanel.blackStone;

				if ((stone != null) && (stone.getChain() != null)
						&& (stone.getPlayer() == Player.BLACK) && (!stone.getChain().isYan())) {
					image = GoPanel.blackStone;
				}

				else if ((stone != null) && (stone.getChain() != null)
						&& (stone.getPlayer() == Player.WHITE) && (!stone.getChain().isYan())) {
					image = GoPanel.whiteStone;
				}

				if ((stone.getPlayer() != Player.YAN) && (stone.getPlayer() != Player.NOT_A_PLAYER)
						&& (stone.getPlayer() != Player.BLACK_TERRITORY) && (stone.getPlayer() != Player.WHITE_TERRITORY)
						&& (stone.getPlayer() != Player.OUTSIDE_THE_BOARD) && (stone.getChain() != null)
						&& (!stone.getChain().isYan())) {
					graphics.drawImage(image, this.verticals[x], this.horizontals[y],
							null);
				}

				Point point = new Point(x, y);

				if (point.equals(this.gamePlay.getlastMovePosition())) {

					graphics.drawImage(GoPanel.lastMove, this.verticals[x],
							this.horizontals[y], null);
				}

			} // end for

		} // end for
		
		Font font = new Font("Arial Bold", Font.PLAIN, 32);
		graphics.setFont(font);

		if (this.player == Player.BLACK)
			graphics.drawImage(GoPanel.highlight, 543, 193, null);
		else
			graphics.drawImage(GoPanel.highlight, 543, 293, null);

		graphics.drawImage(GoPanel.blackStone, 560, 200, null);
		graphics.drawString(Double.toString(this.goBoard.getScores()[0]), 617, 230);

		graphics.drawImage(GoPanel.whiteStone, 560, 300, null);
		graphics.drawString(Double.toString(this.goBoard.getScores()[1]), 617, 330);

		graphics.drawImage(GoPanel.cursor, this.point.x, this.point.y, null);

	} // end paint()

	public void moveUp() {
		if (this.intersection.y < this.boardSize - 1) {
			this.intersection.y++;
			this.point.y = this.horizontals[this.intersection.y];
			this.location.y--;
			this.gamePlay.setIntersection(this.intersection);
			this.gamePlay.setPoint(this.point);
			this.gamePlay.setCurrLocation(this.location);
			this.update(getGraphics());
		} // end if
	} // end moveUp()

	public void moveRight() {
		if (this.intersection.x < this.boardSize - 1) {
			this.intersection.x++;
			this.point.x = this.verticals[this.intersection.x];
			this.location.x++;
			this.gamePlay.setIntersection(this.intersection);
			this.gamePlay.setPoint(this.point);
			this.gamePlay.setCurrLocation(this.location);
			this.update(getGraphics());
		} // end if
	} // end moveRight()

	public void moveDown() {
		if (this.intersection.y > 0) {
			this.intersection.y--;
			this.point.y = this.horizontals[this.intersection.y];
			this.location.y++;
			this.gamePlay.setIntersection(this.intersection);
			this.gamePlay.setPoint(this.point);
			this.gamePlay.setCurrLocation(this.location);
			this.update(getGraphics());
		} // end if
	} // end moveDown()

	public void moveLeft() {
		if (this.intersection.x > 0) {
			this.intersection.x--;
			this.point.x = this.verticals[this.intersection.x];
			this.location.x--;
			this.gamePlay.setIntersection(this.intersection);
			this.gamePlay.setPoint(this.point);
			this.gamePlay.setCurrLocation(this.location);
			this.update(getGraphics());
		} // end if
	} // end moveLeft()

	/*
	 * getters and setters
	 */
	public Point getLocation() {
		return this.location;
	} // end getLocation()

	public void setLocation(Point location) {
		this.location = location;
	} // end setLocation()

	public void setPlayer(Player player) {
		this.player = player;
	} // end setColor()

	public Point getPoint() {
		return this.point;
	} // end getPoint()

	public void setPoint(Point point) {
		this.point = point;
	} // end setPoint()
	
} // end class