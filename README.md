# ing-sw-2026-GIANGREGORIO-FORMICA-GALANTE-LAROSA
Sviluppo del gioco da tavolo MESOS (progetto ingegneria del software POLIMI 2026)

RICCARDO FORMICA - 10901857 ,
GIUSEPPE MICHELE LA ROSA - 10919952 ,
ALESSANDRO GALANTE - 10928390 ,
DANIELE GIANGREGORIO - 10937103 ,

Regole Complete + TUI + GUI + RMI + Socket + 3 FA 

FA 1 : Database
FA 2 : Resilienza alle disconnessioni
FA 3 : Persistenza


ISTRUZIONI SU COME ESEGUIRE I FILE JAR :


### Avvio Server

```bash
java -jar deliverables/final/jar/Mesos-Server.jar  
```

**Parametri:**
- `<porta_socket>` — porta su cui il server ascolta le connessioni Socket
- `<porta_rmi>` — porta su cui il server ascolta le connessioni RMI

Il server avvia **entrambi i protocolli contemporaneamente** su thread separati, così i client possono scegliere liberamente quale usare.

**Esempio:**
```bash
java -jar deliverables/final/jar/Mesos-Server.jar 11111 10099
```

---

### Avvio Client

```bash
java -jar deliverables/final/jar/Mesos-Client.jar   
```

**Parametri:**
- `<ip_server>` — indirizzo IP del server (es. `localhost` se sul tuo stesso PC, oppure l'IP della macchina del server se in rete)
- `<porta_socket>` — porta Socket del server (deve coincidere con quella usata per avviare il server)
- `<porta_rmi>` — porta RMI del server (deve coincidere con quella usata per avviare il server)

**Esempio:**
```bash
java -jar deliverables/final/jar/Mesos-Client.jar localhost 11111 10099
```
Dopo l'avvio, il client chiederà a runtime:
1. **Protocollo di rete**: `[1] Socket` oppure `[2] RMI`
2. **Interfaccia**: `[1] TUI` (testuale) oppure `[2] GUI` (grafica)

---
