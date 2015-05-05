/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.james137137.LolnetSmartRedirect;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author James
 */
public class Commands {
    
    

    static class LolnetSmartRedirectReload extends Command {
        private final LolnetSmartRedirect plugin;
        
        public LolnetSmartRedirectReload(LolnetSmartRedirect aThis) {
            super("LolnetSmartRedirectReload", "LolnetSmartRedirectReload.command.LolnetSmartRedirectReload");
            plugin = aThis;
        }

        @Override
        public void execute(CommandSender cs, String[] strings) {
            plugin.reload();
            cs.sendMessage(ChatColor.GREEN + "Reloaded.");
        }
    }

}
