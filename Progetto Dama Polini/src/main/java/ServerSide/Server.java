package ServerSide;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	//porta TCP sulla quale sarà in ascolto il server
    private int port = 1234;
    
    //gestione partite e giocatori
    private GameManager gameManager;

    public Server() {
        gameManager = new GameManager();
    }

    public void start() throws Exception {
    	//creazione server socket che rimarrà in ascolto sulla porta indicata
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("avvio del server");
        
        //ciclo che accetta connessioni di nuovi client
        while(true) {
        	//accept() è bloccante quindi il server resta in attesa fino alla connessione di un nuovo client
            Socket clientSocket = serverSocket.accept();
            //per ogni client connesso verrà creato un ClientHandler, un thread che gestirà tutta la comunicazione con quel client
            ClientHandler handler = new ClientHandler(clientSocket, gameManager);
            //ogni client viene gestito in un thread separato in modo da garantire piu connessioni simultanee
            Thread thread = new Thread(handler, "ClientHandler");
            thread.start();
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.start();
    }
}