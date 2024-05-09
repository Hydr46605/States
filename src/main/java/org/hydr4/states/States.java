package org.hydr4.states;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class States extends JavaPlugin {

    private Map<String, State> states; // Mappa degli stati
    private File savesFile; // File per il salvataggio degli stati
    private FileConfiguration savesConfig; // Configurazione per il file di salvataggio
    private FileConfiguration config; // Configurazione del plugin

    @Override
    public void onEnable() {
        states = new HashMap<>(); // Inizializza la mappa degli stati
        savesFile = new File(getDataFolder(), "saves.yml"); // Crea il file per il salvataggio degli stati

        // Carica i file di configurazione
        savesConfig = loadConfigFile(savesFile);
        config = getConfig();

        // Carica gli stati dal file saves.yml
        loadStatesFromConfig();

        saveDefaultConfig();

        getLogger().info("States plugin enabled.");
    }

    @Override
    public void onDisable() {
        // Salva gli stati nel file saves.yml prima di disattivare il plugin
        saveStates();

        getLogger().info("States plugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("states")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;

            // Gestisce i comandi del plugin
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /states create <name> <size>");
                player.sendMessage(ChatColor.RED + "Usage: /states trust <player> <role>");
                player.sendMessage(ChatColor.RED + "Usage: /states info <stateName | playerName>");
                return true;
            }

            String subCommand = args[0];

            switch (subCommand.toLowerCase()) {
                case "create":
                    createNewState(player, args);
                    break;
                case "trust":
                    trustPlayer(player, args);
                    break;
                case "info":
                    getStateInfo(player, args);
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "Invalid command. Use /states create, trust, or info.");
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        // Tab completion per i comandi del plugin
        if (cmd.getName().equalsIgnoreCase("states") && args.length == 1) {
            return Arrays.asList("create", "trust", "info");
        }
        return Collections.emptyList();
    }

    private void createNewState(Player player, String[] args) {
        // Crea un nuovo stato con il comando /states create
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /states create <name> <size>");
            return;
        }

        String stateName = args[1];
        int size;

        try {
            size = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid size. Please specify a valid number for the size.");
            return;
        }

        // Verifica se già esiste uno stato con lo stesso nome
        if (states.containsKey(stateName)) {
            player.sendMessage(ChatColor.RED + "A state with that name already exists.");
            return;
        }

        // Verifica se uno stato esistente è sovrapposto alla posizione in cui si vuole creare il nuovo stato
        for (State state : states.values()) {
            if (state.getLocation().getWorld().equals(player.getLocation().getWorld())
                    && state.getLocation().distance(player.getLocation()) <= size) {
                player.sendMessage(ChatColor.RED + "A state already exists in this area.");
                return;
            }
        }

        Location location = player.getLocation();
        State newState = new State(player.getName(), location, size);
        states.put(stateName, newState);
        player.sendMessage(ChatColor.GREEN + "State \"" + stateName + "\" created successfully.");

        // Salva lo stato appena creato nel file saves.yml
        newState.saveState();
    }

    private void trustPlayer(Player player, String[] args) {
        // Concede fiducia a un giocatore con il comando /states trust
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /states trust <player> <role>");
            return;
        }

        String playerName = args[1];
        String roleName = args[2];

        // Verifica se il ruolo specificato è valido
        if (!config.contains("roles." + roleName)) {
            player.sendMessage(ChatColor.RED + "Invalid role specified.");
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found or is offline.");
            return;
        }

        // Assegna il ruolo specificato al giocatore target
        String stateName = player.getWorld().getName(); // Assumendo che ogni mondo abbia il proprio stato
        State state = states.get(stateName);
        if (state != null) {
            state.addMember(playerName, roleName, targetPlayer.getLocation());
            player.sendMessage(ChatColor.GREEN + "Player " + playerName + " trusted with role " + roleName + ".");
        } else {
            player.sendMessage(ChatColor.RED + "You are not in any state.");
        }
    }

    private void getStateInfo(Player player, String[] args) {
        // Ottiene le informazioni su uno stato o un giocatore con il comando /states info
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /states info <stateName | playerName>");
            return;
        }

        String query = args[1];
        State state = states.get(query);

        if (state != null) {
            // Mostra le informazioni dello stato
            player.sendMessage(ChatColor.GREEN + "State Name: " + state.getName());
            player.sendMessage(ChatColor.GREEN + "Owner: " + state.getOwner());
            player.sendMessage(ChatColor.GREEN + "Members: ");
            for (Map.Entry<String, String> entry : state.getMembers().entrySet()) {
                String playerName = entry.getKey();
                String roleName = entry.getValue().split(";")[0]; // Ottieni il ruolo dal valore memorizzato
                player.sendMessage(ChatColor.YELLOW + "- " + playerName + " (" + roleName + ")");
            }
            player.sendMessage(ChatColor.GREEN + "Size: " + state.getSize() + " blocks");
        } else {
            Player targetPlayer = Bukkit.getPlayer(query);
            if (targetPlayer != null) {
                player.sendMessage(ChatColor.GREEN + "Player Name: " + targetPlayer.getName());
                // Altre informazioni sul giocatore possono essere aggiunte qui
            } else {
                player.sendMessage(ChatColor.RED + "State or player not found.");
            }
        }
    }

    private void saveStates() {
        // Salva gli stati nel file saves.yml
        for (State state : states.values()) {
            state.saveState();
        }
    }

    private FileConfiguration loadConfigFile(File file) {
        // Carica il file di configurazione
        if (!file.exists()) {
            saveResource("saves.yml", false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    private void loadStatesFromConfig() {
        // Carica gli stati dal file saves.yml
        if (savesConfig.contains("states")) {
            ConfigurationSection statesSection = savesConfig.getConfigurationSection("states");
            for (String key : statesSection.getKeys(false)) {
                State state = new State(key, statesSection.getConfigurationSection(key));
                states.put(key, state);
            }
        }
    }

    private class State {
        private String name;
        private String owner;
        private Location location;
        private int size;
        private Map<String, String> members; // Mappa dei nomi dei membri degli stati

        public State(String name, ConfigurationSection section) {
            // Costruttore per creare uno stato da una sezione di configurazione
            this.name = name;
            this.owner = section.getString("owner");
            this.location = (Location) section.get("location");
            this.size = section.getInt("size");
            this.members = new HashMap<>();
            ConfigurationSection membersSection = section.getConfigurationSection("members");
            if (membersSection != null) {
                for (String key : membersSection.getKeys(false)) {
                    this.members.put(key, membersSection.getString(key));
                }
            }
        }

        public State(String owner, Location location, int size) {
            // Costruttore per creare un nuovo stato
            this.owner = owner;
            this.location = location;
            this.size = size;
            this.members = new HashMap<>();
        }

        public void addMember(String playerName, String roleName, Location position) {
            // Aggiunge un nuovo membro allo stato
            members.put(playerName, roleName + ";" + position.getX() + "," + position.getY() + "," + position.getZ());
            saveState();
        }

        public void saveState() {
            // Salva lo stato nel file di configurazione
            savesConfig.set("states." + name + ".owner", owner);
            savesConfig.set("states." + name + ".location", location);
            savesConfig.set("states." + name + ".size", size);
            ConfigurationSection membersSection = savesConfig.createSection("states." + name + ".members");
            for (Map.Entry<String, String> entry : members.entrySet()) {
                membersSection.set(entry.getKey(), entry.getValue());
            }
            try {
                savesConfig.save(savesFile);
            } catch (IOException e) {
                getLogger().warning("Could not save saves.yml: " + e.getMessage());
            }
        }

        // Metodi getter e setter
        public String getName() {
            return name;
        }

        public String getOwner() {
            return owner;
        }

        public Location getLocation() {
            return location;
        }

        public int getSize() {
            return size;
        }

        public Map<String, String> getMembers() {
            return members;
        }
    }

    /**
     * Changelog v1.1.0:
     * - Aggiunta funzionalità di ruolo per i membri degli stati
     * - Aggiornati i comandi /states create e /states trust per supportare i ruoli
     * - Modificato il formato di memorizzazione dei membri degli stati per includere i ruoli
     * - Aggiornato il metodo getStateInfo per visualizzare correttamente i ruoli dei membri degli stati
     * - Incrementato il numero di versione del plugin a 1.1.0
     */
}
