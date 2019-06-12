package fr.vmarchaud.mineweb.common;

public class ScheduledCommand
{
    private String command;
    private String player;
    private Long timestamp;
    
    public ScheduledCommand(final String command2, final String player2, final Long time) {
        this.command = command2;
        this.setPlayer(player2);
        this.timestamp = time;
    }
    
    public Long getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    public String getPlayer() {
        return this.player;
    }
    
    public void setPlayer(final String player) {
        this.player = player;
    }
}
