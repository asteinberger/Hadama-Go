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

	private Board goBoard;
	private Point location;
	private Point point;
	private Point intersection;
	private static Image background;
	private static Image blackStone;
	private static Image whiteStone;
	private static Image lastMove;
	private static Image cursor;
	private int color = 0;
	private int[] verticals;
	private int[] horizontals;
	private GamePlay gamePlay;
	private String mode = "HvH";
	private int size;

	private static Point cellDim = new Point(35, 35);
	private static Point borderTopLeft = new Point(200, 184);
	private static Point borderBottomRight = new Point(493, 463);

	public GoPanel(Board b, GamePlay gp) throws IOException {

		GoPanel.background = ImageIO.read(new File("images/board.gif"));

		GoPanel.blackStone = ImageIO.read(new File("images/goStoneBlack.gif"));

		GoPanel.whiteStone = ImageIO.read(new File("images/goStoneWhite.gif"));
		GoPanel.lastMove = ImageIO.read(new File("images/lastMove.gif"));

		GoPanel.cursor = ImageIO.read(new File("images/goCursor.gif"));

		this.setSize(400, 386);
		// this.setMinimumSize(new Dimension(400, 386));

		this.size = b.getSize();
		this.location = new Point(0, b.getSize() - 1);
		this.point = new Point(GoPanel.borderTopLeft.x,
				GoPanel.borderBottomRight.y);

		this.intersection = new Point(0, 0);
		this.verticals = new int[b.getSize()];
		this.horizontals = new int[b.getSize()];

		for (int i = 0; i < b.getSize(); i++) {
			this.horizontals[i] = GoPanel.borderTopLeft.y
					+ ((b.getSize() - i - 1) * GoPanel.cellDim.y);
			this.verticals[i] = GoPanel.borderTopLeft.x
					+ (i * GoPanel.cellDim.x);
		} // end for

		this.gamePlay = gp;
		this.goBoard = b;

	} // end constructor

	public void paint(Graphics g) {

		g.drawImage(GoPanel.background, 0, 0, null);

		// display pieces on board
		for (int x = 0; x < this.size; x++) {

			for (int y = 0; y < this.size; y++) {

				Stone st = this.goBoard.getStone(new Point(x, y));
				Image img = GoPanel.blackStone;

				if ((st != null) && (st.getChain() != null)
						&& (st.getColor() == 0) && (!st.getChain().isYan())) {
					img = GoPanel.blackStone;
				}

				else if ((st != null) && (st.getChain() != null)
						&& (st.getColor() == 1) && (!st.getChain().isYan())) {
					img = GoPanel.whiteStone;
				}

				if ((st.getColor() != -2) && (st.getColor() != -1)
						&& (st.getColor() != 3) && (st.getColor() != 4)
						&& (st.getColor() != 5) && (st.getChain() != null)
						&& (!st.getChain().isYan())) {
					g.drawImage(img, this.verticals[x], this.horizontals[y],
							null);
				}

				Point p = new Point(x, y);

				if (p.equals(this.gamePlay.getlastMovePosition())) {

					g.drawImage(GoPanel.lastMove, this.verticals[x],
							this.horizontals[y], null);
				}
				
			} // end for

		} // end for

		g.drawImage(GoPanel.cursor, this.point.x, this.point.y, null);

	} // end paint()

	public void moveUp() {
		if (this.intersection.y < this.size - 1) {
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
		if (this.intersection.x < this.size - 1) {
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
	public Image getBlackStone() {
		return GoPanel.blackStone;
	} // end getBlackStone()

	public void setBlackStone(Image blackStone) {
		GoPanel.blackStone = blackStone;
	} // end setBlackStone()

	public Image getWhiteStone() {
		return GoPanel.whiteStone;
	} // end getWhiteStone()

	public void setWhiteStone(Image whiteStone) {
		GoPanel.whiteStone = whiteStone;
	} // end setWhiteStone()

	public Point getLocation() {
		return this.location;
	} // end getLocation()

	public void setLocation(Point location) {
		this.location = location;
	} // end setLocation()

	public static Point getCell() {
		return GoPanel.cellDim;
	} // end getCell()

	public static void setCell(Point cell) {
		GoPanel.cellDim = cell;
	} // end setCell()

	public static Point getBorderTopLeft() {
		return GoPanel.borderTopLeft;
	} // end getBorderTopLeft()

	public static void setBorderTopLeft(Point borderTopLeft) {
		GoPanel.borderTopLeft = borderTopLeft;
	} // end setBorderTopLeft()

	public static Point getBorderBottomRight() {
		return GoPanel.borderBottomRight;
	} // end getBorderBottomRight()

	public static void setBorderBottomRight(Point borderBottomRight) {
		GoPanel.borderBottomRight = borderBottomRight;
	} // end setBorderBottomRight()

	public int getColor() {
		return this.color;
	} // end getColor()

	public void setColor(int color) {
		this.color = color;
	} // end setColor()

	public Point getPoint() {
		return this.point;
	} // end getPoint()

	public void setPoint(Point point) {
		this.point = point;
	} // end setPoint()

	public Point getIntersection() {
		return this.intersection;
	} // end getIntersection()

	public void setIntersection(Point intersection) {
		this.intersection = intersection;
	} // end setIntersection()

	public GamePlay getGamePlay() {
		return this.gamePlay;
	} // end getGamePlay()

	public void setGamePlay(GamePlay gamePlay) {
		this.gamePlay = gamePlay;
	} // end setGamePlay()

	public static Image getBlackCursor() {
		return GoPanel.cursor;
	} // end getBlackCursor()

	public static void setBlackCursor(Image blackCursor) {
		GoPanel.cursor = blackCursor;
	} // end setBlackCursor()

	public static Image getlastMove() {
		return GoPanel.lastMove;
	}

	public static void setlastMove(Image lastMove) {
		GoPanel.lastMove = lastMove;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

} // end class