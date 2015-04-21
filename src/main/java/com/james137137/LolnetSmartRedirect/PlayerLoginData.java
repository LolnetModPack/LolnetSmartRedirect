/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.james137137.LolnetSmartRedirect;

import java.util.List;

/**
 *
 * @author James
 */
class PlayerLoginData {

    
    public String playerName; 
    public String modPackName; 
    public List<String> modList;
    public long lastUpdate;
    PlayerLoginData(String playerName, String modPackName, List<String> modList) {
        lastUpdate = System.currentTimeMillis();
        this.playerName=playerName;
        this.modPackName = modPackName;
        this.modList = modList;
    }

    @Override
    public String toString() {
        return playerName + "~~~" + modPackName + "~~~" + modList.toString();
    }
    
    
    
}
