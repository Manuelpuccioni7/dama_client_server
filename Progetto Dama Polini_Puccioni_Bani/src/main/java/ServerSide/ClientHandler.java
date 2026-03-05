package ServerSide;



import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
	//scoket di connessione con il client
    private Socket clientSocket;
    private GameRoom gameRoom;
    private GameManager gameManager;
    
    //output stream verso il client
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socketClient, GameManager gameManager) {
        this.clientSocket = socketClient;
        this.gameManager = gameManager;
    }

    @Override
    public void run() {

        try {
        	//stream di comunicazione
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);


            String messaggio;
      
            //ciclo per l'ascolto dei messaggi inviati dal client
            while((messaggio = in.readLine()) != null) {
            	
            	//richiesta di entrare in partita
                if(messaggio.equalsIgnoreCase("PLAY")) {
                    gameManager.addPlayer(this);
                }
                
                //gestione richiesta mossa
                if(messaggio.startsWith("MOVE")) {

                    String[] parts = messaggio.split(" ");
                    
                    //aggiunta verifica per la correttezza del formato della richiesta
                    if(parts.length != 5) {
                        sendMessage("ERROR");
                        continue;
                    }
                    
                    try {
                    	
                    //conversione della coordinata di partenza della riga
                    //il messaggio MOVE arriva come stringa, quindi bisogna convertire
                    //il valore numerico da testo a intero per poterlo usare nella logica del gioco
                    int fromRow = Integer.parseInt(parts[1]);
                    int fromCol = Integer.parseInt(parts[2]);
                    int toRow = Integer.parseInt(parts[3]);
                    int toCol = Integer.parseInt(parts[4]);
                    
                    // controllo coordinate valide
                    if(fromRow < 0 || fromRow > 7 ||
                       fromCol < 0 || fromCol > 7 ||
                       toRow < 0 || toRow > 7 ||
                       toCol < 0 || toCol > 7) {

                        sendMessage("ERROR");
                        continue;
                    }
                    
                    //inoltro della mossa alla gameroom
                    if(gameRoom != null) {
                        gameRoom.makeMove(this, fromRow, fromCol, toRow, toCol);
                    } else {
                    	sendMessage("ERROR");
                    }
                } catch(NumberFormatException e) {
                	sendMessage("ERROR");
                }
                }
                
                //uscita volontaria della partita
                if(messaggio.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
            
            if (gameRoom != null) {
            	gameRoom.disconnection(this);
            	
            }
            
            clientSocket.close();

        } catch(Exception e) {
        	//Elimino il client dalla lista dei client:
        	gameManager.removeClient(this);
        	
        	//Se è in una partita comunico la vittoria all'altro client
        	if(gameRoom != null) {
        		gameRoom.disconnection(this);
        	}
        	
        	//Se sono in game invio all' avversario che ha vinto
        }
    }
    
    public GameRoom getGameRoom() {
    	return gameRoom;
    }
    
    //il server utilizza questo metodo per inviare messaggi al client
    public void sendMessage(String msg) {
        out.println(msg);
    }
    
    //associazione client - gameroom
    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }
    
    public void closeConnection() {
        try {
            clientSocket.close();
        } catch(Exception e) {
            
        }
    }
    
}

