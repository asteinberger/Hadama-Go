/**
 * HadamaGo.java - This is the main game thread for playing Hadama Go! 
 * It setups the window and starts a new game.
 * 
 * @author Adam Steinberger <steinz08@gmail.com>, Haoran Ma <mahaoran1020@gmail.com>
 */

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;

public class HadamaGo extends Thread {

	/**
	 * Hard coded game size calls for a 9x9 intersection go board.
	 */
	private static int length = 9;

	/**
	 * Hard coded game mode calls for a human versus human player game.
	 */
	private static String gameMode = "HvC";

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
	private static GameListener gListen;

	private HadamaGo game;

	/**
	 * The testFrame holds the graphic user interface.
	 */
	private Frame testFrame;

	/**
	 * The GoBoard contains the stones that are used while in game play.
	 */
	private Board goboard;

	/**
	 * The gamePlay variable controls what happens when players make moves on
	 * the go board.
	 */
	private static GamePlay gamePlay;

	/**
	 * Change the go board size using this variable.
	 */
	private int size;

	private static GoPanel goPanel;
	private static Panel panel;
	private static HashMap<String, Button> buttons = new HashMap<String, Button>();
	private static TextField scoreBlack = new TextField(12);
	private static TextField scoreWhite = new TextField(12);

	// d = dahi
	// t = testFrame
	// g = goboard
	// m = mode
	public HadamaGo(String m, int s) throws IOException {
		this.game = this;
		HadamaGo.gameMode = m;
		this.size = s;
		HadamaGo.length = s;
		this.goboard = new Board(this.size);
		HadamaGo.gamePlay = new GamePlay(HadamaGo.gListen, HadamaGo.gameMode,
				HadamaGo.length);
		HadamaGo.goPanel = new GoPanel(this.goboard, HadamaGo.gamePlay);
		HadamaGo.gListen = new GameListener(this, this.goboard, this.size,
				HadamaGo.gamePlay);
		this.testFrame = new Frame("Hadama Go v10.10");
	} // end constructor

	public static void main(String[] args) throws IOException {
		HadamaGo g = new HadamaGo(HadamaGo.gameMode, HadamaGo.length);
		g.start();
	} // end main()

	private void addButton(String n) {
		Button b = new Button(n);
		b.addActionListener(HadamaGo.gListen);
		HadamaGo.buttons.put(n, b);
	} // end addButton()

	public void run() {

		this.testFrame.setLayout(new BorderLayout());

		this.addButton("forfeit");
		this.addButton("pass turn");
		this.addButton("undo move");
		this.addButton("new game");

		double[] scores = this.goboard.getScores();
		HadamaGo.scoreBlack.addActionListener(HadamaGo.gListen);
		HadamaGo.scoreWhite.addActionListener(HadamaGo.gListen);
		HadamaGo.scoreBlack.setText("Black = " + Double.toString(scores[0]));
		HadamaGo.scoreWhite.setText("White = " + Double.toString(scores[1]));

		HadamaGo.panel = new Panel();
		HadamaGo.panel.setLayout(new FlowLayout());
		HadamaGo.panel.setSize(722, 20);

		HadamaGo.panel.add(HadamaGo.buttons.get("pass turn"));
		HadamaGo.panel.add(HadamaGo.buttons.get("forfeit"));
		HadamaGo.panel.add(HadamaGo.buttons.get("undo move"));
		HadamaGo.panel.add(HadamaGo.buttons.get("new game"));
		HadamaGo.panel.add(HadamaGo.scoreBlack);
		HadamaGo.panel.add(HadamaGo.scoreWhite);

		this.testFrame.add(HadamaGo.goPanel, BorderLayout.CENTER);
		this.testFrame.add(HadamaGo.panel, BorderLayout.SOUTH);
		this.testFrame.setLocation(10, 10);
		this.testFrame.setSize(HadamaGo.frameLength, HadamaGo.frameHeight);

		// create an instance of the Class that listens to all events
		// (GLEvents, Keyboard, Mouse and Menu events)
		// add this object as all these listeners to the canvas and menu
		this.testFrame.addKeyListener(HadamaGo.gListen);
		this.testFrame.addMouseListener(HadamaGo.gListen);
		this.testFrame.addMouseMotionListener(HadamaGo.gListen);
		HadamaGo.goPanel.addKeyListener(HadamaGo.gListen);
		HadamaGo.goPanel.addMouseListener(HadamaGo.gListen);
		HadamaGo.goPanel.addMouseMotionListener(HadamaGo.gListen);
		HadamaGo.panel.addKeyListener(HadamaGo.gListen);
		HadamaGo.panel.addMouseListener(HadamaGo.gListen);
		HadamaGo.panel.addMouseMotionListener(HadamaGo.gListen);

		this.testFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // end window
		});

		// start the Animator, which periodically calls display() on the
		// GLCanvas
		this.testFrame.setVisible(true);

	} // end main

	/*
	 * getters and setters
	 */
	public HadamaGo getGame() {
		return this.game;
	} // end getGame()

	public void setGame(HadamaGo g) {
		this.game = g;
	} // end setGame()

	public Frame getTestFrame() {
		return this.testFrame;
	} // end getTestFrame()

	public void setTestFrame(Frame f) {
		this.testFrame = f;
	} // end setTestFrame()

	public Board getGoboard() {
		return this.goboard;
	} // end getGoboard()

	public void setGoboard(Board b) {
		this.goboard = b;
	} // end setGoboard()

	public int getSize() {
		return this.size;
	} // end getSize()

	public void setSize(int size) {
		this.size = size;
	} // end setSize()

	public static GameListener getgListen() {
		return HadamaGo.gListen;
	} // end getgListen()

	public static void setgListen(GameListener gListen) {
		HadamaGo.gListen = gListen;
	} // end setgListen()

	public static GamePlay getGamePlay() {
		return HadamaGo.gamePlay;
	} // end getGamePlay()

	public static void setGamePlay(GamePlay gamePlay) {
		HadamaGo.gamePlay = gamePlay;
	} // end setGamePlay()

	public static GoPanel getGoPanel() {
		return HadamaGo.goPanel;
	} // end getGoPanel()

	public static void setGoPanel(GoPanel goPanel) {
		HadamaGo.goPanel = goPanel;
	} // end setGoPanel()

	public static Panel getPanel() {
		return HadamaGo.panel;
	} // end getPanel()

	public static void setPanel(Panel panel) {
		HadamaGo.panel = panel;
	} // end setPanel()

	public static HashMap<String, Button> getButtons() {
		return HadamaGo.buttons;
	} // end getButtons()

	public static void setButtons(HashMap<String, Button> buttons) {
		HadamaGo.buttons = buttons;
	} // end setButtons()

	public static int getLength() {
		return length;
	}

	public static void setLength(int length) {
		HadamaGo.length = length;
	}

	public static String getGameMode() {
		return gameMode;
	}

	public static void setGameMode(String gameMode) {
		HadamaGo.gameMode = gameMode;
	}

	public static int getFrameHeight() {
		return frameHeight;
	}

	public static void setFrameHeight(int frameHeight) {
		HadamaGo.frameHeight = frameHeight;
	}

	public static int getFrameLength() {
		return frameLength;
	}

	public static void setFrameLength(int frameLength) {
		HadamaGo.frameLength = frameLength;
	}

	public static TextField getScoreBlack() {
		return HadamaGo.scoreBlack;
	}

	public void setScoreBlack(TextField scoreBlack) {
		HadamaGo.scoreBlack = scoreBlack;
	}

	public static TextField getScoreWhite() {
		return HadamaGo.scoreWhite;
	}

	public void setScoreWhite(TextField scoreWhite) {
		HadamaGo.scoreWhite = scoreWhite;
	}

} // end class
