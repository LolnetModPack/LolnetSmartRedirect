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

    static Logger log;
    static LolnetSmartRedirect plugin;
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
        getProxy().getPluginManager().registerListener(this, new MyListener(this));
        getProxy().getPluginManager().registerCommand(this, new Commands.LolnetSmartRedirectReload(this));
        plugin = this;
        log = this.getLogger();
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
    }

    public void reload() {
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            LolnetSmartRedirect.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException ex) {
            Logger.getLogger(LolnetFourmPermissionBCBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
