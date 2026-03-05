package ServerSide;

import ServerSide.Color;
import ServerSide.Piece;

public class Board {
	private Piece board [][];
	private int rows;
	private int cols;
	
	public Board() {
		this.rows = 8;
		this.cols = 8;
		this.board = new Piece[rows][cols];
	}
	
	public Piece getPiece(int row, int col) {
	    if(row < 0 || row > 7 || col < 0 || col > 7) {
	        return null;
	    }
	    return board[row][col];
	}
	
	public void initializeBoard() {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				
				//individuazione delle caselle nere in cui prenderanno posto tutte le pedine all'inizio game
				//abbiamo semplicemente notato che sono individuabili quando la somma dell indice della riga e della colonna
				//da come risultato un numero dispari
				if((i + j) % 2 != 0) {
					if(i < 3) {
						//inserimento pedine nere
						board[i][j] = new Piece(Color.BLACK, false);
					} else if (i > 4) {
						//inserimento pedine bianche
						board[i][j] = new Piece(Color.WHITE, false);
					}
				}
			}
		}
	}
	
	public String getBoardString() {

	    String boardString = "";
	    
	    boardString += "/ 0 1 2 3 4 5 6 7\n";

	    for(int i = 0; i < rows; i++) {

	        boardString += i + " ";

	        for(int j = 0; j < cols; j++) {

	            if(board[i][j] == null) 
	                boardString += ". ";
	                
	            else if(board[i][j].getColore() == Color.BLACK)
	                boardString += "N ";
	            
	            else
	                boardString += "B ";
	        }
	        	
	        //Inserisco il new line per tutte le righe tranne l'ultima
	        if(i+1 < rows)
	        	boardString += "\n";
	    }
	    
	    
	    return boardString;
	}
	
	
	public boolean validateMove(int fromRow, int fromCol, int toRow, int toCol) {
		
		// Controllo se uno degli indicatori di posizione eccede il numero massimo
		if(fromRow > 7 || fromRow < 0 || fromCol > 7 || fromCol < 0 || toRow > 7 || toRow < 0 || toCol > 7 || toCol < 0) {
			return false;
		}
		
		// Controllo se sto cercando di operare su una casella vuota
		if(board[fromRow][fromCol] == null) {
			return false;
		}
		
		// Controllo se sto cercando di muovere una pedina in una casella già occupata
		if(board[toRow][toCol] != null) {
			return false;
		}
		
		// spostamento di una pedina di una casella senza cattura 
		if((toRow - fromRow == 1 || toRow - fromRow == -1) && (toCol - fromCol == 1 || toCol - fromCol == -1)) {
			
			// Una pedina non può muoversi indietro se non è dama
			if(board[fromRow][fromCol].isPromoted() == false && board[fromRow][fromCol].getColore() == Color.WHITE && toRow >= fromRow) {
				return false;
			}
			
			// Una pedina non può muoversi indietro se non è dama
			if(board[fromRow][fromCol].isPromoted() == false && board[fromRow][fromCol].getColore() == Color.BLACK && toRow <= fromRow) {
				return false;
			}
			
			// se esiste una cattura da poter fare allora si dovrà obbligatoriamente fare quella e non si può fare la mossa semplice
			if(forcedCapture(board[fromRow][fromCol].getColore())) {
				return false;
			}
			
			return true;
		}
		
		// Caso in cui lo spostamento di una pedina comprenda la cattura di un altra quindi la pedina verrà spostata di 2 caselle
		if((toRow - fromRow == 2 || toRow - fromRow == -2) && (toCol - fromCol == 2 || toCol - fromCol == -2)) {
			
			// Una pedina non può muoversi indietro se non è dama
			if(board[fromRow][fromCol].isPromoted() == false && board[fromRow][fromCol].getColore() == Color.WHITE && toRow >= fromRow) {
				return false;
			}
			
			// Una pedina non può muoversi indietro se non è dama
			if(board[fromRow][fromCol].isPromoted() == false && board[fromRow][fromCol].getColore() == Color.BLACK && toRow <= fromRow) {
				return false;
			}
			
			// la pedina da catturare si trova esattamente in mezzo tra la casella di partenza e la casella di destinazione
			int pieceToCapture_row = (fromRow + toRow) / 2;
			int pieceToCapture_col = (fromCol + toCol) / 2; 
			Piece pieceToCapture = board[pieceToCapture_row][pieceToCapture_col];
			
			if(pieceToCapture != null && pieceToCapture.getColore() != board[fromRow][fromCol].getColore()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean canCapture(int row, int col) {
		
		// Controllo limiti
		if(row < 0 || row > 7 || col < 0 || col > 7) {
			return false;
		}
		
		// Se non c'è una pedina non può catturare
		if(board[row][col] == null) {
			return false;
		}
		
		Piece piece = board[row][col];
		
		// Controllo tutte e quattro le direzioni diagonali
		int[] rowDir = {-1, -1, 1, 1};
		int[] colDir = {-1, 1, -1, 1};
		
		for(int i = 0; i < 4; i++) {
			
			int middleRow = row + rowDir[i];
			int middleCol = col + colDir[i];
			int targetRow = row + 2 * rowDir[i];
			int targetCol = col + 2 * colDir[i];
			
			// Controllo che la destinazione sia dentro la scacchiera
			if(targetRow < 0 || targetRow > 7 || targetCol < 0 || targetCol > 7) {
				continue;
			}
			
			// Se non è dama limito le direzioni
			if(piece.isPromoted() == false) {
				if(piece.getColore() == Color.WHITE && rowDir[i] > 0) {
					continue;
				}
				if(piece.getColore() == Color.BLACK && rowDir[i] < 0) {
					continue;
				}
			}
			
			Piece middlePiece = board[middleRow][middleCol];
			
			if(middlePiece != null && middlePiece.getColore() != piece.getColore() && board[targetRow][targetCol] == null) {
				return true;
			}
		}
		return false;
	}
	
	public void applyMove(int fromRow, int fromCol, int toRow, int toCol) {
		Piece piece = board[fromRow][fromCol];
		
		// se la condizione è veriricata allora si tratta di una cattura e procedo 
		if(toRow - fromRow == 2 || toRow - fromRow == -2) {
			int pieceToCapture_row = (fromRow + toRow) / 2;
			int pieceToCapture_col = (fromCol + toCol) / 2;
			board[pieceToCapture_row][pieceToCapture_col] = null;
		}
		
		// Spostamento di una pedina dalla posizione iniziale verso quella finale
		board[toRow][toCol] = piece;
		board[fromRow][fromCol] = null;
		
		// Controllo della promozione a dama
		if(toRow == 7 && piece.getColore() == Color.BLACK) {
			piece.promote();
		} else if(toRow == 0 && piece.getColore() == Color.WHITE) {
			piece.promote();
		}
		
	}
	
	public boolean forcedCapture(Color color) {
		//questo metodo si occupa di scorrere tutta la board e verifica se è possibile fare una cattura,
		//essa diventerà obbligatoria come implementato nel metodo validateMove
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j< cols; j++) {
				
				Piece piece = board[i][j];
				
				if(piece != null && piece.getColore() == color) {
					if(canCapture(i, j)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public Color checkVictory() {
		int whitePieces = 0;
		int blackPieces = 0;
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				if(board[i][j] != null) {
					if(board[i][j].getColore() == Color.WHITE) {
						whitePieces++;
					} else {
						blackPieces++;
					}
				}
			}
		}
		
		if(whitePieces == 0) {
			return Color.BLACK;
		}
		
		if(blackPieces == 0) {
			return Color.WHITE;
		}
		return null;
	}
	
	public Piece[][] getBoard() {
		return board;
	}
		
}


