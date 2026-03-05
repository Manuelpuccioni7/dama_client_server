Il presente progetto consiste in un applicazione client-server di rete in Java che implementa il gioco "DAMA"
Il sistema permette a piu client di connettersi al server tramite la socket TCP. 
Il server gestisce i giocatori in attesa e crea automaticamente una partita quando sono connessi 2 client.

---Architettura---

Architettura client-server multithread in cui abbiamo:

-  Server -
-> Gestisce connessioni client
->  crea un thread per ogni client
-> associa i giocatori in attesa ad una GameRoom per iniziare la partita
->  gestisce la logica della partita

- Client -
-> Si connette al server
-> invia comandi di gioco
-> riceve aggiornamenti, in particolare rispetto alla scacchiera, alla vittoria/sconfitta, al turno, inoltre anche rispetto agli errori


---Protocollo---
Client e Server si avvalgono della comunicazione tramite messaggi testuali
il protocollo mette a disposizione i seguenti messaggi:
- PLAY -> richiesta di entrare in partita
- WAIT -> attesa avversario
- START -> inizio partita
- MOVE x1 y1 x2 y2 -> mossa
- OK -> mossa accettata
- ERROR -> mossa non valida
- WIN -> vittoria
- LOSE -> sconfitta
- QUIT -> uscita volontaria dalla partita

Per usare l'applicazione dovete 
1) avviare il server, tramite classe Server, che sarà in ascolto sulla porta 1234
2) avviare i client tramite classe Client
3) digitare PLAY per entrare in partita
4) enjoy 
