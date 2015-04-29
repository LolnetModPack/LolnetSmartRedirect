package com.james137137.LolnetSmartRedirect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import nz.co.lolnet.lolnetfourmpermissionbcbridge.LolnetFourmPermissionBCBridge;

/**
 *
 * @author James
 */
public class LolnetSmartRedirect extends Plugin {

    public static HashMap<String,PlayerLoginData> playerMap;
    static Logger log;
    private static final int PORT = 10009;
    InfomationListener Listener;
    static LolnetSmartRedirect plugin;
    public static boolean run = false;
    static ServerSocket serverSocket = null;
    
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
        playerMap = new HashMap<>();
        getProxy().getPluginManager().registerListener(this, new MyListener(this));
        plugin = this;
        log = this.getLogger();
        Listener = new InfomationListener();
    }

    @Override
    public void onDisable() {
        Listener = null;
    }

    private static class InfomationListener {

        public InfomationListener() {

            try {
                serverSocket = new ServerSocket(LolnetSmartRedirect.PORT);
                System.out.println("listening on port: " + LolnetSmartRedirect.PORT + " for server messages.");
                run = true;
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + LolnetSmartRedirect.PORT);
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
                            while ((inputLine = in.readLine()) != null) {
                                if (inputLine.contains("~~~")) {
                                    String[] split = inputLine.split("~~~");
                                    if (split.length == 4) {
                                        UUID playerUUID = UUID.fromString(split[0]);
                                        String playerName = split[1];
                                        String modPackName = split[2];
                                        String data = split[3];
                                        data = data.replace("[", "").replace("]", "");
                                        List<String> modList = Arrays.asList(data.split(", ", -1));
                                        PlayerLoginData playerLoginData = new PlayerLoginData(playerName,modPackName,modList);
                                        LolnetSmartRedirect.playerMap.put(playerName, playerLoginData);
                                        System.out.println(playerLoginData.toString());
                                    }
                                }
                                out.println("done");
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
