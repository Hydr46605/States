# States Plugin

Il plugin States consente ai giocatori di creare stati o meglio chiamate zone private.

## Caratteristiche

- **Creazione di Stati**: I giocatori possono creare nuovi stati all'interno del gioco.
- **Assegnazione di Ruoli**: Assegna ruoli ai giocatori all'interno degli stati.
- **Visualizzazione delle Informazioni**: Ottieni informazioni dettagliate sugli stati e sui giocatori.
- **Persistenza degli Stati**: Gli stati vengono salvati automaticamente per essere recuperati in seguito.

## Installazione

1. Scarica l'ultima versione del plugin dal [Releases](#).
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

- [Hydr4](https://hydr4.rf.gd) - Sviluppatore principale

## Contributi

Siete invitati a contribuire al miglioramento di questo plugin attraverso segnalazioni di bug e richieste di pull su [GitHub](#).

## Licenza

Questo progetto è concesso in licenza con la licenza [MIT](#).
