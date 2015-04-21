/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.james137137.LolnetSmartRedirect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author James
 */
public class MyListener implements Listener {

    private List<String> firstTimeLogin;

    public MyListener(LolnetSmartRedirect aThis) {
        firstTimeLogin = new ArrayList<>();
    }

    @EventHandler
    public void onPlayerLogin(PreLoginEvent event) {
        System.out.println("debug1");
        String playerName = event.getConnection().getName();
        System.out.println(playerName);
        System.out.println(LolnetSmartRedirect.playerMap.containsKey(playerName));
        if (LolnetSmartRedirect.playerMap.containsKey(playerName)) {
            
            firstTimeLogin.add(playerName);
        }
    }

    @EventHandler
    public void onPlayerLogin(ServerConnectEvent event) {
        System.out.println("debug2");
        ProxiedPlayer player = event.getPlayer();
        System.out.println(firstTimeLogin.contains(player.getName()));
        if (firstTimeLogin.contains(player.getName())) {
            firstTimeLogin.remove(player.getName());
            ServerInfo target = event.getTarget();
            if (target.getName().equalsIgnoreCase("lobby") || target.getName().equalsIgnoreCase("lobby2")) {
                PlayerLoginData playerData = LolnetSmartRedirect.playerMap.get(player.getName());
                if (playerData.lastUpdate >= 5 * 1000)
                {
                    return;
                }
                ServerInfo serverInfo = LolnetSmartRedirect.plugin.getProxy().getServers().get(playerData.modPackName);
                if (serverInfo != null)
                {
                    event.setTarget(serverInfo);
                }
            }
        } else
        {
            if (event.getTarget().getName().equalsIgnoreCase("lobby"))
            {
                PlayerLoginData playerData = LolnetSmartRedirect.playerMap.get(player.getName());
                if (playerData == null || playerData.lastUpdate >= 5 * 1000)
                {
                    return;
                }
                if (playerData!= null && playerData.modPackName.contains("tolkien"))
                {
                    event.setTarget(LolnetSmartRedirect.plugin.getProxy().getServers().get("lobby3"));
                }
            }
        }
        LolnetSmartRedirect.playerMap.put(player.getName(), null);
    }
}
