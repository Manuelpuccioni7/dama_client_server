package ServerSide;

import ServerSide.Board;
import ServerSide.ClientHandler;
import ServerSide.Color;
import ServerSide.Piece;

public class GameRoom {
	private Board board;
	private Color currentTurn;
	private boolean gameOver;
	private ClientHandler whitePlayer;
	private ClientHandler blackPlayer;
	private boolean moveProcessing = false;
	
	public GameRoom(ClientHandler whitePlayer, ClientHandler blackPlayer) {
		
		//assegnazione 
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		
		//logica partita
		board = new Board();
		board.initializeBoard();
		currentTurn = Color.WHITE;
		gameOver = false;
		
		//collegamento player->game room
		whitePlayer.setGameRoom(this);
		blackPlayer.setGameRoom(this);
		
		
		//messaggi inizilai
		whitePlayer.sendMessage("WHITE");
		blackPlayer.sendMessage("BLACK");
		
		//invio la board
		whitePlayer.sendMessage(board.getBoardString());
		blackPlayer.sendMessage(board.getBoardString());
		
		whitePlayer.sendMessage("TURN");
        blackPlayer.sendMessage("WAIT");
        
	}
	
	public Board getBoard() {
		return board;
	}
	
	public Color getTurn() {
		return currentTurn;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	
	public synchronized boolean makeMove(ClientHandler player, int fromRow, int fromCol, int toRow, int toCol) {
		//in questo modo viene gestito il caso di un client che manda x volte di fila la stessa richiesta viene bloccato il turno
	    if(moveProcessing) {
	        player.sendMessage("ERROR");
	        return false;
	    }
		
	    //controllo lo stato della partita
	    if(gameOver) {
	        player.sendMessage("ERROR");
	        return false;
	    }
	    
	    //controllo del turno corretto
	    if((currentTurn == Color.WHITE && player != whitePlayer) || (currentTurn == Color.BLACK && player != blackPlayer)) {
	        player.sendMessage("ERROR");
	        return false;
	    }
	    
	    Piece piece = board.getPiece(fromRow, fromCol);
	    
	    //Controllo che non voglio muovere una pedina inesistente
	    if(piece == null) {
	        player.sendMessage("ERROR");
	        return false;
	    }
	    
	    //Controllo del colore corretto
	    if(piece.getColore() != getTurn()) {
	        player.sendMessage("ERROR");
	        return false;
	    }
	    
	    //Controllo la validità della mossa
	    if(!board.validateMove(fromRow, fromCol, toRow, toCol)) {
	        player.sendMessage("ERROR");
	        return false;
	    }
	    
	    //Applico la mossa
	    board.applyMove(fromRow, fromCol, toRow, toCol);
	    player.sendMessage("OK");
	    
        //invio board aggiornata
        String boardState = board.getBoardString();
        whitePlayer.sendMessage(boardState);
        blackPlayer.sendMessage(boardState);
	    
	    //Controllo la vittoria e così la fine della partita
	    Color vincitore = board.checkVictory();
	    if(vincitore != null) {
	        
	        if(vincitore == Color.WHITE) {
	            whitePlayer.sendMessage("WIN");
	            blackPlayer.sendMessage("LOSE");
	        } else {
	            whitePlayer.sendMessage("LOSE");
	            blackPlayer.sendMessage("WIN");
	        }
	        
	        gameOver = true;
	        return true;
	    }
	    
	    //controllo se si è verificata una cattura così posso successivamente controllare se ci sono altre catture e farla senza cambiare turno
	    if(toRow - fromRow == 2 || toRow - fromRow == -2) {
	        if(board.canCapture(toRow, toCol)) {
	            player.sendMessage("TURN");
	            return true;
	        }
	    }
	    
	    //Cambio turno
	    switchTurn();
	    
	    //Notifico i giocatori
	    if(currentTurn == Color.WHITE) {
	        whitePlayer.sendMessage("TURN");
	        blackPlayer.sendMessage("WAIT");
	    } else {
	        blackPlayer.sendMessage("TURN");
	        whitePlayer.sendMessage("WAIT");
	    }
	    
	    return true;
	}
	
	public void switchTurn() {
		if(currentTurn == Color.WHITE) {
			currentTurn = Color.BLACK;
		} else {
			currentTurn = Color.WHITE;
		}
	}
	
	public synchronized void disconnection(ClientHandler player) {
		if(gameOver) {
			return;
		}
		
		gameOver = true;
		
		if(player == blackPlayer) {
			whitePlayer.sendMessage("WIN");
			whitePlayer.setGameRoom(null);
		} else {
			blackPlayer.sendMessage("WIN");
			blackPlayer.setGameRoom(null);
		}
		
	}
}
