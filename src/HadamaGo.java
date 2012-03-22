/**
 * Go.java - This class has the main method and sets up the window and
 * does some initialization.  The bulk of the work is done in
 * DrawAndHandleInput.java
 * 
 * =========================================================
 * 
 * This program is a modified version of a Java program used
 * in Michael Eckmann's Computer Graphics course at Skidmore
 * College, which is a modified version of a C program 
 * used in G. Drew Kessler's Computer Graphics course at 
 * Lehigh University.  Michael Eckmann based some of his
 * code on the code found in JOGL: A Beginner's Guide and
 * Tutorial By Kevin Conroy
 * http://www.cs.umd.edu/~meesh/kmconroy/JOGLTutorial/
 * 
 * =========================================================
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
	private static String gameMode = "HvH";

	/**
	 * The window frame dimensions are hard coded to 525 by 400 pixels.
	 */
	private static int frameHeight = 525;

	/**
	 * The window frame dimensions are hard coded to 450 by 400 pixels.
	 */
	private static int frameLength = 400;

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
	private static TextField scoreBlack = new TextField(20);
	private static TextField scoreWhite = new TextField(20);

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
		HadamaGo.gamePlay = new GamePlay(HadamaGo.gameMode, HadamaGo.length);
		HadamaGo.goPanel = new GoPanel(this.goboard, HadamaGo.gamePlay);
		HadamaGo.gListen = new GameListener(this, this.goboard, this.size,
				HadamaGo.gamePlay);
		this.testFrame = new Frame("Hadama Go v8.04");
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

		this.addButton("up");
		this.addButton("right");
		this.addButton("down");
		this.addButton("left");
		this.addButton("place stone");
		this.addButton("pass turn");
		this.addButton("forfeit");
		this.addButton("print board");
		this.addButton("print chains");
		this.addButton("print weis");
		this.addButton("print moves");
		this.addButton("print scores");
		this.addButton("undo move");
		this.addButton("new game");

		double[] scores = this.goboard.getScores();
		HadamaGo.scoreBlack.addActionListener(HadamaGo.gListen);
		HadamaGo.scoreWhite.addActionListener(HadamaGo.gListen);
		HadamaGo.scoreBlack.setText("Black = " + Double.toString(scores[0]));
		HadamaGo.scoreWhite.setText("White = " + Double.toString(scores[1]));

		HadamaGo.panel = new Panel();
		HadamaGo.panel.setLayout(new GridLayout(4, 4, 2, 2));
		HadamaGo.panel.setSize(200, 150);
		HadamaGo.panel.add(HadamaGo.buttons.get("up"));
		HadamaGo.panel.add(HadamaGo.buttons.get("right"));
		HadamaGo.panel.add(HadamaGo.buttons.get("down"));
		HadamaGo.panel.add(HadamaGo.buttons.get("left"));
		HadamaGo.panel.add(HadamaGo.buttons.get("place stone"));
		HadamaGo.panel.add(HadamaGo.buttons.get("pass turn"));
		HadamaGo.panel.add(HadamaGo.buttons.get("forfeit"));
		HadamaGo.panel.add(HadamaGo.buttons.get("print board"));
		HadamaGo.panel.add(HadamaGo.buttons.get("print chains"));
		HadamaGo.panel.add(HadamaGo.buttons.get("print weis"));
		HadamaGo.panel.add(HadamaGo.buttons.get("print moves"));
		HadamaGo.panel.add(HadamaGo.buttons.get("print scores"));
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

		// code to set up a menu and add it to our frame
		// and register an event listener to it
		String[] s1 = { "New Game", "Quit" };
		Menu m1 = this.addMenu("File", s1);

		String[] s2 = { "Undo", "Forfeit" };
		Menu m2 = this.addMenu("Edit", s2);

		MenuBar menuBar = new MenuBar();
		menuBar.add(m1);
		menuBar.add(m2);
		this.testFrame.setMenuBar(menuBar);

		this.testFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // end window
		});

		// start the Animator, which periodically calls display() on the
		// GLCanvas
		this.testFrame.setVisible(true);

	} // end main

	private Menu addMenu(String n, String[] mi) {
		Menu m = new Menu(n);
		for (int i = 0; i < mi.length; i++)
			m.add(this.addMenuItem(mi[i]));
		return m;
	} // end addMenu()

	private MenuItem addMenuItem(String s) {
		MenuItem mi = new MenuItem(s);
		mi.addActionListener(HadamaGo.gListen);
		return mi;
	} // end addMenuItem()

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
