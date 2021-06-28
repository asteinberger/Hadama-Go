/**
 * HadamaGo.java - This is the main game thread for playing Hadama Go! 
 * It setups the window and starts a new game.
 * 
 * @author Adam Steinberger <adam@akmaz.io>, Haoran Ma <mahaoran1020@gmail.com>
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HadamaGo extends Thread {

	/**
	 * Hard coded game size calls for a 9x9 intersection go board.
	 */
	private static int boardLength = 9;

	/**
	 * Hard coded game mode calls for a human versus human player game.
	 */
	private static GameMode gameMode = GameMode.HUMAN_VS_Q_LEARNING;

	/**
	 * The window frame dimensions are hard coded to 694 by 722 pixels.
	 */
	private static int frameHeight = 694;

	/**
	 * The window frame dimensions are hard coded to 694 by 722 pixels.
	 */
	private static int frameLength = 722;

	/**
	 * The game listener variable controls the action, key and mouse events
	 */
	private static GameListener gameListener;

	private HadamaGo game;

	/**
	 * The testFrame holds the graphic user interface.
	 */
	private Frame frame;

	/**
	 * The GoBoard contains the stones that are used while in game play.
	 */
	private GoBoard gameBoard;

	/**
	 * The gamePlay variable controls what happens when players make moves on
	 * the go board.
	 */
	private static GamePlay gamePlay;

	/**
	 * Change the go board size using this variable.
	 */
	private int boardSize;

	private static GoPanel gamePanel;
	private static Panel bottomPanel;
	private static HashMap<String, Button> buttons = new HashMap<>();

	// d = dahi
	// t = testFrame
	// g = goboard
	// gameMode = mode
	public HadamaGo(GameMode gameMode, int boardSize) throws Exception {
		this.game = this;
		this.gameMode = gameMode;
		this.boardSize = boardSize;
		this.boardLength = boardSize;
		this.gameBoard = new GoBoard(this.boardSize);
		this.gamePlay = new GamePlay(this.gameMode, this.boardLength);
		this.gamePanel = new GoPanel(this.gameBoard, this.gamePlay);
		this.gameListener = new GameListener(this, this.gameBoard, this.boardSize, this.gamePlay);
		this.frame = new Frame("Hadama Go 3.0 Níngjìng");
	} // end constructor

	public static void main(String[] args) throws Exception {
		HadamaGo game = new HadamaGo(HadamaGo.gameMode, HadamaGo.boardLength);
		game.start();
	} // end main()

	private void addButton(String name) {
		Button button = new Button(name);
		button.addActionListener(HadamaGo.gameListener);
		HadamaGo.buttons.put(name, button);
	} // end addButton()

	public void run() {

		this.frame.setLayout(new BorderLayout());

		this.addButton("forfeit");
		this.addButton("pass turn");
		this.addButton("undo move");
		this.addButton("new game");

		HadamaGo.bottomPanel = new Panel();
		HadamaGo.bottomPanel.setLayout(new FlowLayout());
		HadamaGo.bottomPanel.setSize(722, 20);

		HadamaGo.bottomPanel.add(HadamaGo.buttons.get("pass turn"));
		HadamaGo.bottomPanel.add(HadamaGo.buttons.get("forfeit"));
		HadamaGo.bottomPanel.add(HadamaGo.buttons.get("undo move"));
		HadamaGo.bottomPanel.add(HadamaGo.buttons.get("new game"));

		this.frame.add(HadamaGo.gamePanel, BorderLayout.CENTER);
		this.frame.add(HadamaGo.bottomPanel, BorderLayout.SOUTH);
		this.frame.setLocation(10, 10);
		this.frame.setSize(HadamaGo.frameLength, HadamaGo.frameHeight);

		// create an instance of the Class that listens to all events
		// (GLEvents, Keyboard, Mouse and Menu events)
		// add this object as all these listeners to the canvas and menu
		this.frame.addKeyListener(HadamaGo.gameListener);
		this.frame.addMouseListener(HadamaGo.gameListener);
		this.frame.addMouseMotionListener(HadamaGo.gameListener);
		HadamaGo.gamePanel.addKeyListener(HadamaGo.gameListener);
		HadamaGo.gamePanel.addMouseListener(HadamaGo.gameListener);
		HadamaGo.gamePanel.addMouseMotionListener(HadamaGo.gameListener);
		HadamaGo.bottomPanel.addKeyListener(HadamaGo.gameListener);
		HadamaGo.bottomPanel.addMouseListener(HadamaGo.gameListener);
		HadamaGo.bottomPanel.addMouseMotionListener(HadamaGo.gameListener);

		this.frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // end window
		});

		// start the Animator, which periodically calls display() on the
		// GLCanvas
		this.frame.setVisible(true);

	} // end main

	public void setGameBoard(GoBoard b) {
		this.gameBoard = b;
	} // end setGameBoard()

	public static GoPanel getGamePanel() {
		return HadamaGo.gamePanel;
	} // end getGamePanel()

	public static void setGamePanel(GoPanel gamePanel) {
		HadamaGo.gamePanel = gamePanel;
	} // end setGamePanel()

} // end class
