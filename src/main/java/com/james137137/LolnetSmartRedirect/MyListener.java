/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.james137137.LolnetSmartRedirect;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author James
 */
public class MyListener implements Listener {

    public MyListener(LolnetSmartRedirect aThis) {

    }

    @EventHandler
    public void onPlayerLogin(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        //event.setTarget(serverInfo);

        //check if member
        if (event.getTarget().getName().contains("tolkien") && !LolnetSmartRedirect.isfourmRegistered(player.getName())) {
            event.getPlayer().disconnect(ChatColor.GREEN + "Please Register on fourm: lolnet.co.nz before entering TolkienCraft");
            //event.getPlayer().sendMessage(ChatColor.GREEN + "Please Register on fourm");
            //event.setTarget(LolnetSmartRedirect.plugin.getProxy().getServers().get("lobby3"));
        }
        if (LolnetSmartRedirect.config.getStringList("MemberOnlyServers").contains(event.getTarget().getName()) && !LolnetSmartRedirect.isfourmRegistered(player.getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GREEN + "Please Register on fourm: lolnet.co.nz before entering " + event.getPlayer().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerKickEvent(ServerKickEvent ev) {
        if (ev.getKickedFrom().getName().contains("tolkien")) {
            ev.setCancelled(true);
            ev.setCancelServer(LolnetSmartRedirect.plugin.getProxy().getServers().get("lobby3"));
        }
    }
}
