package ServerSide;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
	//coda dei giocatori che hanno richiesto una partita
    private PlayerQueue playerQueue = new PlayerQueue();
    private ArrayList<GameRoom> activeGames = new ArrayList<GameRoom>();

    //synchronized perchè può essere chiamato contemporaneamente da piu ClientHandler
    //quindi se due client inviano PLAY nello stesso momento evitiamo la race condition
    public synchronized void addPlayer(ClientHandler player) {
    	
    	//in questo modo un client non entrerà due volte nella coda
        if(player.getGameRoom() != null) {
            return;
        }
    	
    	
    	//inserimento del giocatore nella coda di attesta
        playerQueue.add(player);

        player.sendMessage("WAIT");
        
        //la partita puo essere ovviamente avviata in presenza di almeno 2 giocatori
        if(playerQueue.size() >= 2) {
        	
        	//prelevo i due giocatori in attesa e li assegno una nuova GameRoom - inizia la partita
            ClientHandler p1 = playerQueue.poll();
            p1.sendMessage("START");
            ClientHandler p2 = playerQueue.poll();
            p2.sendMessage("START");
            GameRoom game = new GameRoom(p1, p2);

            activeGames.add(game);
        }
    }

    public synchronized void removeClient(ClientHandler client) {
    	playerQueue.remove(client);
    }
    
    public synchronized void removeGame(GameRoom game) {
        activeGames.remove(game);
    }
}