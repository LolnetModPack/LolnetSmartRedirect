package com.james137137.LolnetSmartRedirect;

import com.google.common.io.ByteStreams;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import nz.co.lolnet.lolnetfourmpermissionbcbridge.LolnetFourmPermissionBCBridge;

/**
 *
 * @author James
 */
public class LolnetSmartRedirect extends Plugin {

    public static HashMap<String, PlayerLoginData> playerMap;
    static Logger log;
    private static final int PORT = 10009;
    InfomationListener Listener;
    static LolnetSmartRedirect plugin;
    public static boolean run = false;
    static ServerSocket serverSocket = null;
    public static Configuration config;

    public static boolean isfourmRegistered(String userName) {
        boolean result = false;
        try {
            result = LolnetFourmPermissionBCBridge.getRankList(userName).contains("REGISTERED");
        } catch (Exception ex) {
        }
        return result;
    }

    @Override
    public void onEnable() {
        setupConfigFile();
        playerMap = new HashMap<>();
        getProxy().getPluginManager().registerListener(this, new MyListener(this));
        getProxy().getPluginManager().registerCommand(this, new Commands.LolnetSmartRedirectReload(this));
        plugin = this;
        log = this.getLogger();
        Listener = new InfomationListener();
    }
    
    private void setupConfigFile() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                if (getResourceAsStream("config.yml") == null) {
                    System.out.println("Failed to obtain config.yml from getResourceAsStream(\"config.yml\")");
                }
                try (InputStream is = getResourceAsStream("config.yml");
                        OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
        try {
            LolnetFourmPermissionBCBridge.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException ex) {
            Logger.getLogger(LolnetFourmPermissionBCBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
        saveConfig();
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(LolnetFourmPermissionBCBridge.config, new File(getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            Logger.getLogger(LolnetFourmPermissionBCBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onDisable() {
        Listener = null;
    }
    
     public void reload() {
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            LolnetSmartRedirect.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException ex) {
            Logger.getLogger(LolnetFourmPermissionBCBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class InfomationListener {

        public InfomationListener() {

            try {
                serverSocket = new ServerSocket(LolnetSmartRedirect.PORT);
                log.info("listening on port: " + LolnetSmartRedirect.PORT + " for server messages.");
                run = true;
            } catch (IOException e) {
                log.warning("Could not listen on port: " + LolnetSmartRedirect.PORT);
                return;
            }

            new ThreadListenForClients();
        }
    }

    private static class ThreadListenForClients {

        public ThreadListenForClients() {
            start();
        }

        private void start() {
            LolnetSmartRedirect.plugin.getProxy().getScheduler().runAsync(LolnetSmartRedirect.plugin, new Runnable() {

                @Override
                public void run() {
                    while (run) {
                        Socket clientSocket = null;

                        try {
                            clientSocket = serverSocket.accept();
                            new ThreadListenClient(clientSocket);
                        } catch (IOException e) {
                        }
                    }
                }

            });
        }

        private static class ThreadListenClient {

            Socket clientSocket;
            BufferedReader in;
            PrintWriter out;

            public ThreadListenClient(final Socket clientSocket) {

                LolnetSmartRedirect.plugin.getProxy().getScheduler().runAsync(LolnetSmartRedirect.plugin, new Runnable() {

                    @Override
                    public void run() {
                        try {
                            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            out = new PrintWriter(clientSocket.getOutputStream(), true);
                        } catch (IOException ex) {
                            Logger.getLogger(InfomationListener.class.getName()).log(Level.SEVERE, null, ex);
                            return;
                        }
                        try {
                            String inputLine;
                            String playerName = null;
                            while ((inputLine = in.readLine()) != null) {
                                if (inputLine.contains("~~~")) {
                                    String[] split = inputLine.split("~~~");
                                    if (split.length == 4) {
                                        UUID playerUUID = UUID.fromString(split[0]);
                                        playerName = split[1];
                                        String modPackName = split[2];
                                        String data = split[3];
                                        data = data.replace("[", "").replace("]", "");
                                        List<String> modList = Arrays.asList(data.split(", ", -1));
                                        PlayerLoginData playerLoginData = new PlayerLoginData(playerName, modPackName, modList);
                                        LolnetSmartRedirect.playerMap.put(playerName, playerLoginData);
                                    }
                                }
                                if (playerName == null) {
                                    out.println("MessageFormatError");
                                } else {
                                    if (LolnetSmartRedirect.isfourmRegistered(playerName)) {
                                        out.println("Registered");
                                    }
                                    else
                                    {
                                        out.println("NotRegistered");
                                    }
                                }

                                
                            }
                        } catch (Exception ex) {
                        }
                        try {
                            in.close();
                            out.close();
                            clientSocket.close();
                        } catch (IOException ex) {
                            Logger.getLogger(InfomationListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
        }
    }

}
