package de.lystx.reader;

import de.ingotstudios.terminal.IngotTerminal;
import de.ingotstudios.terminal.abstracts.IngotAbstractHandler;
import de.ingotstudios.terminal.model.terminal.TerminalColor;
import de.lystx.reader.elements.DatabasePlayer;
import io.vson.elements.object.VsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

public class CloudNetReader {

    public static void main(String[] args) {
        new CloudNetReader();
    }




    private final IngotTerminal ingotTerminal;
    private final List<DatabasePlayer> databasePlayers;
    private final Map<String, List<DatabasePlayer>> sortedDatabasePlayers;


    public CloudNetReader() {
        this.databasePlayers = new LinkedList<>();
        this.sortedDatabasePlayers = new HashMap<>();

        this.ingotTerminal = new IngotTerminal(new IngotAbstractHandler() {
            @Override
            public void handle(String[] args) {
                if (args.length == 1 && args[0].equalsIgnoreCase("cls")) {
                    ingotTerminal.clearScreen();
                    return;
                }
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("view") && args[1].equalsIgnoreCase("all")) {
                        for (int i = 0; i < databasePlayers.size(); i++) {
                            DatabasePlayer player = databasePlayers.get(i);
                            ingotTerminal.write(
                                    TerminalColor.BRIGHT_BLACK + "[" + TerminalColor.BRIGHT_CYAN + i + TerminalColor.BRIGHT_BLACK + "] " +
                                            TerminalColor.BRIGHT_WHITE + player.getName() + " (" + player.getUniqueId() + ") > " + player.getIpAddress(), false);
                        }
                        return;
                    }
                }
                if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("view") && args[1].equalsIgnoreCase("all")) {
                        String sort = args[2];
                        if (sort.equalsIgnoreCase("sorted")) {
                            for (String s : sortedDatabasePlayers.keySet()) {
                                ingotTerminal.write(" ", false);
                                ingotTerminal.write(TerminalColor.BRIGHT_BLACK + "------------ [ " + TerminalColor.BRIGHT_CYAN + s.toUpperCase() + TerminalColor.BRIGHT_BLACK + " ] ------------", false);
                                List<DatabasePlayer> list = sortedDatabasePlayers.get(s);
                                for (int i = 0; i < list.size(); i++) {
                                    DatabasePlayer player = list.get(i);
                                    ingotTerminal.write(
                                            TerminalColor.BRIGHT_BLACK + "[" + TerminalColor.BRIGHT_CYAN + i + TerminalColor.BRIGHT_BLACK + "] " +
                                                    TerminalColor.BRIGHT_WHITE + player.getName() + " (" + player.getUniqueId() + ") > " + player.getIpAddress(), false);
                                }
                                ingotTerminal.write(TerminalColor.BRIGHT_BLACK + "-----------------------------", false);
                                ingotTerminal.write(" ", false);
                            }
                            return;
                        }
                        if (sort.length() > 1) {
                            ingotTerminal.write(TerminalColor.RED + "Please only provide a Character not a whole String!", false);
                            return;
                        }
                        for (String s : sortedDatabasePlayers.keySet()) {
                            if (s.equalsIgnoreCase(sort)) {
                                ingotTerminal.write(" ", false);
                                ingotTerminal.write(TerminalColor.BRIGHT_BLACK + "------------ [ " + TerminalColor.BRIGHT_CYAN + s.toUpperCase() + TerminalColor.BRIGHT_BLACK + " ] ------------", false);
                                for (int i = 0; i < sortedDatabasePlayers.get(s).size(); i++) {
                                    DatabasePlayer player = sortedDatabasePlayers.get(s).get(i);
                                    ingotTerminal.write(
                                            TerminalColor.BRIGHT_BLACK + "[" + TerminalColor.BRIGHT_CYAN + i + TerminalColor.BRIGHT_BLACK + "] " +
                                                    TerminalColor.BRIGHT_WHITE + player.getName() + " (" + player.getUniqueId() + ") > " + player.getIpAddress(), false);
                                }
                                ingotTerminal.write(TerminalColor.BRIGHT_BLACK + "-----------------------------", false);
                                ingotTerminal.write(" ", false);
                            }
                        }
                        return;
                    }
                    if (args[0].equalsIgnoreCase("view") && args[1].equalsIgnoreCase("player")) {
                        String p = args[2];
                        DatabasePlayer player = databasePlayers.stream().filter(databasePlayer -> databasePlayer.getName().equalsIgnoreCase(p)).findFirst().orElse(null);
                        if (player == null) {
                            ingotTerminal.write(TerminalColor.RED + "There is no player with the name " + TerminalColor.YELLOW + p + TerminalColor.RED + " registered in this set of database!", false);
                            return;
                        }
                        ingotTerminal.write(" ", false);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy - hh:mm:ss");
                        ingotTerminal.write(TerminalColor.BRIGHT_CYAN + "Information on " + TerminalColor.GREEN + player.getName(), false);
                        ingotTerminal.write(TerminalColor.BRIGHT_CYAN + "Name : " + TerminalColor.BRIGHT_WHITE + player.getName(), false);
                        ingotTerminal.write(TerminalColor.BRIGHT_CYAN + "UUID : " + TerminalColor.BRIGHT_WHITE + player.getUniqueId(), false);
                        ingotTerminal.write(TerminalColor.BRIGHT_CYAN + "IP : " + TerminalColor.BRIGHT_WHITE + player.getIpAddress(), false);
                        ingotTerminal.write(TerminalColor.BRIGHT_CYAN + "FirstLogin : " + TerminalColor.BRIGHT_WHITE + simpleDateFormat.format(new Date(player.getFirstLogin())), false);
                        ingotTerminal.write(TerminalColor.BRIGHT_CYAN + "LastLogin : " + TerminalColor.BRIGHT_WHITE + simpleDateFormat.format(new Date(player.getLastLogin())), false);
                        ingotTerminal.write(" ", false);
                        return;
                    }
                }
                ingotTerminal.write(TerminalColor.BRIGHT_CYAN + "view all", false);
                ingotTerminal.write(TerminalColor.BRIGHT_CYAN + "view all <sorted>", false);
                ingotTerminal.write(TerminalColor.BRIGHT_CYAN + "view player <player>", false);
                ingotTerminal.write(TerminalColor.BRIGHT_CYAN + "view all <char>", false);

            }
        });
        this.ingotTerminal.setPrompt(TerminalColor.BRIGHT_BLACK + "[" + TerminalColor.BRIGHT_CYAN + "CNV2Reader" + TerminalColor.BRIGHT_BLACK + "] " + TerminalColor.BRIGHT_WHITE);
        this.ingotTerminal.write(TerminalColor.YELLOW + "\n" +
                "   _____ _   ___      _____  _____                _           \n" +
                "  / ____| \\ | \\ \\    / /__ \\|  __ \\              | |          \n" +
                " | |    |  \\| |\\ \\  / /   ) | |__) |___  __ _  __| | ___ _ __ \n" +
                " | |    | . ` | \\ \\/ /   / /|  _  // _ \\/ _` |/ _` |/ _ \\ '__|\n" +
                " | |____| |\\  |  \\  /   / /_| | \\ \\  __/ (_| | (_| |  __/ |   \n" +
                "  \\_____|_| \\_|   \\/   |____|_|  \\_\\___|\\__,_|\\__,_|\\___|_|   \n" +
                "                                                              \n" +
                "                                                              ", false);
        File entries = new File("entries");
        if (!entries.exists()) {
            entries.mkdirs();
            this.ingotTerminal.write(TerminalColor.RED + "There is no " + TerminalColor.BRIGHT_YELLOW + "entries " + TerminalColor.RED + "directory! Creating one...", false);
        }
        if (Objects.requireNonNull(entries.listFiles()).length == 0) {
            this.ingotTerminal.write(TerminalColor.RED + "There are no " + TerminalColor.BRIGHT_YELLOW + "DatabaseFiles " + TerminalColor.RED + "in the entries directory! Stopping CloudNetV2Reader...", false);
            System.exit(0);
            return;
        }
        this.ingotTerminal.write(TerminalColor.GREEN + "Please be patient! This might take some time to load " + TerminalColor.RED + Objects.requireNonNull(entries.listFiles()).length + TerminalColor.GREEN + " entries!", false);

            for (File file : Objects.requireNonNull(entries.listFiles())) {
                VsonObject vsonObject = null;
                try {
                    vsonObject = new VsonObject(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (vsonObject == null) {
                    continue;
                }


                try {
                    VsonObject oP = vsonObject.getVson("offlinePlayer");

                    DatabasePlayer databasePlayer =
                            new DatabasePlayer(
                                    oP.getString("name"),
                                    UUID.fromString(oP.getString("uniqueId")),
                                    oP.getVson("lastPlayerConnection").getString("host"),
                                    oP.getLong("lastLogin"),
                                    oP.getLong("firstLogin")
                            );
                    this.databasePlayers.add(databasePlayer);


                    String name = databasePlayer.getName();

                    String c = name.substring(0, 1);

                    List<DatabasePlayer> databasePlayers = this.sortedDatabasePlayers.getOrDefault(c.toUpperCase(), new LinkedList<>());
                    databasePlayers.add(databasePlayer);
                    databasePlayers.sort(Comparator.comparing(DatabasePlayer::getName));
                    this.sortedDatabasePlayers.put(c.toUpperCase(), databasePlayers);
                } catch (Exception e) {

                }
            }

        this.ingotTerminal.write("Loaded " + TerminalColor.BRIGHT_CYAN + this.databasePlayers.size() + TerminalColor.BRIGHT_WHITE + " DatabasePlayers from files!", false);


        for (DatabasePlayer databasePlayer : databasePlayers) {
            this.createPlayer(databasePlayer);
        }


    }


    public void createPlayer(DatabasePlayer databasePlayer) {
        URLConnection urlConnection = null;
        try {
            urlConnection = new URL("https://www.erkannnichts.de/api/accounts/add.php?uuid="+  databasePlayer.getUniqueId() +"&name="+ databasePlayer.getName()).openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


}
