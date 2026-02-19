package pro.polini.tepsit;

public class Board {
	private Piece board [][];
	private int rows;
	private int cols;
	private int WhitePieces;
	private int BlackPieces;
	
	public Board() {
		this.rows = 8;
		this.cols = 8;
		this.board = new Piece[rows][cols];
		this.WhitePieces = 12;
		this.BlackPieces = 12;
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
	
	public void printBoard() {
		
		// Inanzitutto è stato curato lo stile della scacchiera in modo da renderla nè troppo pesante alla vista, ma neanche troppo essenziale
		System.out.print("      ");
		for(int j = 0; j < cols; j++) {
			System.out.print(j + "   ");
		}
		System.out.println();

		System.out.print("    ");
		for(int j = 0; j < cols; j++) {
			System.out.print("----");
		}
		System.out.println();
		////////////////////////////
		
		// Successivamente abbiamo piazzato le pedine sulla scacchiera 
		for(int i = 0; i < rows; i++) {

			System.out.print(i + "  |  ");
			for(int j = 0; j < cols; j++) {

				if(board[i][j] == null) {
					System.out.print(".   ");
				} else if(board[i][j].getColore() == Color.BLACK) {
					System.out.print("N   ");
				} else {
					System.out.print("B   ");
				}
			}
			System.out.println();
		}
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
		board[fromRow][toRow] = null;
		
		// Controllo della promozione a dama
		if(toRow == 7 && piece.getColore() == Color.BLACK) {
			piece.promote();
		} else if(toRow == 0 && piece.getColore() == Color.WHITE) {
			piece.promote();
		}
		
		
	}
	
	
	
	
	public boolean checkVictory() {
		
		
		
		return false;
	}
}
