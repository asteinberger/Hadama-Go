import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Move {

	private Board goBoard;
	private int color = -1;
	private TreeSet<Stone> added = new TreeSet<Stone>();
	private TreeSet<Stone> removed = new TreeSet<Stone>();
	private ArrayList<Chain> addedChains = new ArrayList<Chain>();
	private ArrayList<Chain> removedChains = new ArrayList<Chain>();
	private ArrayList<Wei> addedWeis = new ArrayList<Wei>();
	private ArrayList<Wei> removedWeis = new ArrayList<Wei>();
	private HashMap<Stone, Chain> addedToChain = new HashMap<Stone, Chain>();
	private HashMap<Stone, Chain> removedFromChain = new HashMap<Stone, Chain>();
	private HashMap<Qi, Chain> addedToChainQi = new HashMap<Qi, Chain>();
	private HashMap<Qi, Chain> removedFromChainQi = new HashMap<Qi, Chain>();
	private HashMap<Stone, Wei> addedToWei = new HashMap<Stone, Wei>();
	private HashMap<Stone, Wei> removedFromWei = new HashMap<Stone, Wei>();

	public Move(Board gb) {
		this.goBoard = gb;
	} // end constructor

	public Move(Board gb, int c) {
		this.goBoard = gb;
		this.color = c;
	} // end constructor

	public void addChain(Chain c) {
		this.addedChains.add(c);
	} // end addChain()

	public void removeChain(Chain c) {
		this.removedChains.add(c);
	} // end removeChain()

	public void addWei(Wei w) {
		this.addedWeis.add(w);
	} // end addWei()

	public void removeWei(Wei w) {
		this.removedWeis.add(w);
	} // end removeWeis()

	public void addStones(TreeSet<Stone> r) {
		Iterator<Stone> it = r.iterator();
		while (it.hasNext()) {
			Stone next = it.next();
			this.added.add(next);
		} // end while
	} // end addStones()

	public void addStone(Stone s) {
		this.added.add(s);
	} // end addStones()

	public void removeStones(TreeSet<Stone> r) {
		Iterator<Stone> it = r.iterator();
		while (it.hasNext()) {
			Stone next = it.next();
			this.removed.add(next);
		} // end while
	} // end removeStones()

	public void removeStone(Stone s) {
		this.removed.add(s);
	} // end removeStones()

	public void addChainStones(HashMap<Stone, Chain> r) {
		Set<Stone> stones = r.keySet();
		Iterator<Stone> it = stones.iterator();
		while (it.hasNext()) {
			Stone next = it.next();
			Chain c = r.get(next);
			this.addedToChain.put(next, c);
		} // end while
	} // end addChainStones()

	public void addChainStone(Chain c, Stone s) {
		this.addedToChain.put(s, c);
	} // end addChainStone()

	public void removeChainStones(HashMap<Stone, Chain> r) {
		Set<Stone> stones = r.keySet();
		Iterator<Stone> it = stones.iterator();
		while (it.hasNext()) {
			Stone next = it.next();
			Chain c = r.get(next);
			this.removedFromChain.put(next, c);
		} // end while
	} // end removeChainStones()

	public void removeChainStone(Chain c, Stone s) {
		this.removedFromChain.put(s, c);
	} // end removeStones()

	public void addChainQis(HashMap<Qi, Chain> r) {
		Set<Qi> qis = r.keySet();
		Iterator<Qi> it = qis.iterator();
		while (it.hasNext()) {
			Qi next = it.next();
			Chain c = r.get(next);
			this.addedToChainQi.put(next, c);
		} // end while
	} // end addChainQis()

	public void addChainQi(Chain c, Qi q) {
		this.addedToChainQi.put(q, c);
	} // end addChainQi()

	public void removeChainQis(HashMap<Qi, Chain> r) {
		Set<Qi> qis = r.keySet();
		Iterator<Qi> it = qis.iterator();
		while (it.hasNext()) {
			Qi next = it.next();
			Chain c = r.get(next);
			this.removedFromChainQi.put(next, c);
		} // end while
	} // end removeChainQis()

	public void removeChainQi(Chain c, Qi q) {
		this.removedFromChainQi.put(q, c);
	} // end removeChainQi()

	public void addWeiStones(HashMap<Stone, Wei> r) {
		Set<Stone> stones = r.keySet();
		Iterator<Stone> it = stones.iterator();
		while (it.hasNext()) {
			Stone next = it.next();
			Wei w = r.get(next);
			this.addedToWei.put(next, w);
		} // end while
	} // end addWeiStones()

	public void addWeiStone(Wei w, Stone s) {
		this.addedToWei.put(s, w);
	} // end addWeiStone()

	public void removeWeiStones(HashMap<Stone, Wei> r) {
		Set<Stone> stones = r.keySet();
		Iterator<Stone> it = stones.iterator();
		while (it.hasNext()) {
			Stone next = it.next();
			Wei w = r.get(next);
			this.removedFromWei.put(next, w);
		} // end while
	} // end removeWeiStones()

	public void removeWeiStone(Wei w, Stone s) {
		this.removedFromWei.put(s, w);
	} // end removeWeiStone()

	@Override
	public String toString() {
		return "Move [goBoard=" + goBoard + ", color=" + color + ", added="
				+ added + ", removed=" + removed + ", addedChains="
				+ addedChains + ", removedChains=" + removedChains
				+ ", addedWeis=" + addedWeis + ", removedWeis=" + removedWeis
				+ ", addedToChain=" + addedToChain + ", removedFromChain="
				+ removedFromChain + ", addedToChainQi=" + addedToChainQi
				+ ", removedFromChainQi=" + removedFromChainQi
				+ ", addedToWei=" + addedToWei + ", removedFromWei="
				+ removedFromWei + "]";
	}

	/*
	 * getters and setters
	 */
	public Board getGoBoard() {
		return this.goBoard;
	} // end getGoBoard()

	public void setGoBoard(Board goBoard) {
		this.goBoard = goBoard;
	} // end setGoBoard()

	public TreeSet<Stone> getAdded() {
		return this.added;
	} // end getAdded()

	public void setAdded(TreeSet<Stone> added) {
		this.added = added;
	} // end setAdded()

	public TreeSet<Stone> getRemoved() {
		return this.removed;
	} // end getRemoved()

	public void setRemoved(TreeSet<Stone> removed) {
		this.removed = removed;
	} // end setRemoved()

	public int getColor() {
		return this.color;
	} // end getColor()

	public void setColor(int color) {
		this.color = color;
	} // end setColor()

	public HashMap<Stone, Chain> getAddedToChain() {
		return this.addedToChain;
	}

	public void setAddedToChain(HashMap<Stone, Chain> addedToChain) {
		this.addedToChain = addedToChain;
	}

	public HashMap<Stone, Chain> getRemovedFromChain() {
		return this.removedFromChain;
	}

	public void setRemovedFromChain(HashMap<Stone, Chain> removedFromChain) {
		this.removedFromChain = removedFromChain;
	}

	public HashMap<Qi, Chain> getAddedToChainQi() {
		return this.addedToChainQi;
	}

	public void setAddedToChainQi(HashMap<Qi, Chain> addedToChainQi) {
		this.addedToChainQi = addedToChainQi;
	}

	public HashMap<Qi, Chain> getRemovedFromChainQi() {
		return this.removedFromChainQi;
	}

	public void setRemovedFromChainQi(HashMap<Qi, Chain> removedFromChainQi) {
		this.removedFromChainQi = removedFromChainQi;
	}

	public HashMap<Stone, Wei> getAddedToWei() {
		return this.addedToWei;
	}

	public void setAddedToWei(HashMap<Stone, Wei> addedToWei) {
		this.addedToWei = addedToWei;
	}

	public HashMap<Stone, Wei> getRemovedFromWei() {
		return removedFromWei;
	}

	public void setRemovedFromWei(HashMap<Stone, Wei> removedFromWei) {
		this.removedFromWei = removedFromWei;
	}

	public ArrayList<Chain> getAddedChains() {
		return addedChains;
	}

	public void setAddedChains(ArrayList<Chain> addedChains) {
		this.addedChains = addedChains;
	}

	public ArrayList<Chain> getRemovedChains() {
		return removedChains;
	}

	public void setRemovedChains(ArrayList<Chain> removedChains) {
		this.removedChains = removedChains;
	}

	public ArrayList<Wei> getAddedWeis() {
		return addedWeis;
	}

	public void setAddedWeis(ArrayList<Wei> addedWeis) {
		this.addedWeis = addedWeis;
	}

	public ArrayList<Wei> getRemovedWeis() {
		return removedWeis;
	}

	public void setRemovedWeis(ArrayList<Wei> removedWeis) {
		this.removedWeis = removedWeis;
	}

} // end class
