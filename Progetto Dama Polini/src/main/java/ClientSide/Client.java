package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import ServerSide.WinException;

public class Client {

    public static void main(String[] args) {
        try {

        	Socket socket = new Socket("localhost", 1234);
        	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        	
        	Scanner scanner = new Scanner(System.in);
        	String input;

            

            while(true) {
            	
            	System.out.println("WELCOME - Per giocare digita 'PLAY' per uscire digita 'QUIT'");
            	System.out.print("> ");
               
                input = scanner.nextLine();
                //invio del comando al server
                
                if(input.equalsIgnoreCase("QUIT")) {
                	scanner.close();
                	socket.close();
                    return;
                }
                
                if (input.equalsIgnoreCase("PLAY")) {
                	
                	//Invio play al server
                	out.println(input);
                	
                	//Aspetto il messaggio di waiting
                	waitForMessage("WAIT",in);
                	System.out.println("WAITING FOR PLAYERS ...");
                	
                	waitForMessage("START",in);
                	//A questo punto la partita è iniziata
                	
                	//Funzione di gestione della partita
                	handleGame(in,out);
                }


            }
            
            

        } catch(IOException e) {
            System.out.println("Server disconnesso, riprova più tardi");
        } 
        catch(WinException e) {
        	System.out.println("Errore imprevisto, riavvia l'applicazione");
        }
        catch(Exception e) {
        	
        }
    }

	private static void handleGame(BufferedReader in,PrintWriter out) throws Exception {
		Scanner scanner = new Scanner(System.in);
		String myTurn;
		String color;
		
		try {
			color = read(in);//Leggo il colore delle mie pedine

			System.out.println("PARTITA INIZIATA:\n");
			
			//Ciclo lettura/scrittura mossa
			while(true) {
				
				//Stamp la board attuale
				System.out.println("\n------------------------------------");
				System.out.println("You are the : " + color);
				System.out.println(readBoard(in));
				
				//Leggo il turno
				myTurn = in.readLine();

				 
				 //Controllo se è il mio turno
				 if(myTurn.equalsIgnoreCase("TURN")) {
					 
					 System.out.print("È il tuo turno, ");
					 
					 while(true) {
						 System.out.println("Per inserire la mossa digitare: ");
						 System.out.println("MOVE riga colonna rigaDestinazione colonnaDestinazione");
						 System.out.println("Per uscire scrivere QUIT");
						 System.out.print("> ");
						 
						 String input = scanner.nextLine();
						 
						 //Invio la mossa
						 out.println(input);
						 
						 //Aspetto la risposta
						 String result = read(in);
						 
						 if(result.equalsIgnoreCase("OK")) {
							 break;
						 }
						 
						 if(input.equalsIgnoreCase("QUIT")) {
							 System.exit(0);
			              }
						 
						 System.out.println("Errore mossa non valida\n");
					 }
					 
				 }
				 
				 if(myTurn.equalsIgnoreCase("WAIT")) {
					 //Leggo la board
					 System.out.println("è il turno dell' avversario");
				 }
				 
				 
				 
				 if(myTurn.equalsIgnoreCase("LOSE")) {
					 System.out.println("PARTITA FINITA : HAI PERSO!");
					 break;
				 }
				 
			}
			
			
		}catch(WinException e) {
			System.out.println("PARTITA FINITA : HAI VINTO!, AVVERSARIO DISCONNESSO\n\n");
		}
	}
	
	private static String read(BufferedReader in)throws WinException, IOException, Exception {
		String string = in.readLine();
		
		if(string == null) {
			throw new Exception();
		}
		
		if(string.equalsIgnoreCase("WIN")) {
			throw new WinException();
		}
		
		return string;
	}

	private static void waitForMessage(String string, BufferedReader in) throws WinException, Exception {
		
		while(true) {
			try {
				if(read(in).equalsIgnoreCase(string)) 
					return;
			} catch (IOException e) {
				System.out.println("Server disconnesso, riprova più tardi");
				System.exit(0);
			}
			System.out.println("Messaggio non gestito");
		}
	}
	
	private static String readBoard(BufferedReader in) throws IOException, WinException, Exception {

	    String board = "";

	    for (int i = 0; i < 9; i++) {   // 1 header + 8 righe
	        board += read(in);
	        board += "\n";
	    }
	    
	    return board;
	}
	
}