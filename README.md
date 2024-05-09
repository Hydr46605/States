# States Plugin

Il plugin States per Minecraft consente ai giocatori di creare e gestire stati all'interno del mondo di gioco.

## Caratteristiche

- **Creazione di Stati**: I giocatori possono creare nuovi stati all'interno del gioco.
- **Assegnazione di Ruoli**: Assegna ruoli ai giocatori all'interno degli stati.
- **Visualizzazione delle Informazioni**: Ottieni informazioni dettagliate sugli stati e sui giocatori.
- **Persistenza degli Stati**: Gli stati vengono salvati automaticamente per essere recuperati in seguito.

## Installazione

1. Scarica l'ultima versione del plugin dal [link alle Releases](#).
2. Trascina il file JAR scaricato nella cartella `plugins` del tuo server Minecraft.
3. Riavvia il server Minecraft.

## Utilizzo

- `/states create <name> <size>`: Crea un nuovo stato specificando nome e dimensione.
- `/states trust <player> <role>`: Assegna un ruolo a un giocatore all'interno dello stato.
- `/states <stateName | playerName>`: Visualizza le informazioni su uno stato o un giocatore.

## Configurazione

Il file `config.yml` del plugin definisce i ruoli dei giocatori all'interno degli stati. Puoi personalizzare i ruoli e i loro colori nel file di configurazione.

```yaml
roles:
  leader: "&aLeader"
  member: "&eMember"
  visitor: "&7Visitor"
```

## Autori

- [Il Tuo Nome](#) - Sviluppatore principale

## Contributi

Siete invitati a contribuire al miglioramento di questo plugin attraverso segnalazioni di bug e richieste di pull su [GitHub](#).

## Licenza

Questo progetto è concesso in licenza con la licenza [MIT](#).
