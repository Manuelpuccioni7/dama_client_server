package ServerSide;

import ServerSide.Color;

public class Piece {
	
	/*
	 *  Le pedine dovranno contenere informazioni riguardo:
	 *  	- Colore
	 *  	- Promozione
	 *  dovrà inoltre essere possibile ottenere informazioni su di esse
	 *
	 */
	
	private Color colore;
	private boolean isPromoted;
	
	public Piece(Color colore, boolean isPromoted) {
		this.colore = colore;
		this.isPromoted = isPromoted;
	}
	
	public Color getColore() {
		return colore;
	}
	
	public boolean isPromoted() {
		return isPromoted;
	}
	
	public void promote() {
		this.isPromoted = true;
	}
	
}
