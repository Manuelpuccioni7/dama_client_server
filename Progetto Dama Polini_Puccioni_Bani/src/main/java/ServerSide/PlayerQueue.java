package ServerSide;

import java.util.LinkedList;
import java.util.Queue;

public class PlayerQueue {
	
	//lista che mantiene i giocatori in attesa
	//struttura FIFO
    private Queue<ClientHandler> queue = new LinkedList<>();

    //inserico giocatore nella coda
    public synchronized void add(ClientHandler player) {
        queue.add(player);
    }
    
    //rimuovo il giocatore dalla coda
    public synchronized ClientHandler poll() {
        return queue.poll();
    }
    
    public synchronized void remove(ClientHandler client) {
    	queue.remove(client);
    }
    
    //giocatori in attesa nella coda
    public synchronized int size() {
        return queue.size();
    }
}
