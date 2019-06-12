package fr.vmarchaud.mineweb.bukkit;

import org.bukkit.craftbukkit.v1_7_R4.command.*;

public class MinewebCommandSender extends ServerCommandSender
{
    public void sendMessage(final String message) {
    }
    
    public void sendMessage(final String[] messages) {
        for (final String message : messages) {
            this.sendMessage(message);
        }
    }
    
    public String getName() {
        return "Rcon";
    }
    
    public boolean isOp() {
        return true;
    }
    
    public void setOp(final boolean value) {
    }
}
