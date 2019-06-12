package fr.vmarchaud.mineweb.common;

import fr.vmarchaud.mineweb.common.configuration.*;
import java.util.concurrent.*;
import java.util.*;

public class CommandScheduler implements Runnable
{
    private ICore api;
    private ScheduledStorage storage;
    private Set<ScheduledCommand> commands;
    private ConcurrentLinkedQueue<ScheduledCommand> queue;
    
    public CommandScheduler(final ICore api, final ScheduledStorage storage) {
        this.queue = new ConcurrentLinkedQueue<ScheduledCommand>();
        this.api = api;
        this.storage = storage;
        this.commands = storage.getCommands();
    }
    
    @Override
    public void run() {
        final Iterator<ScheduledCommand> it = this.commands.iterator();
        final Date now = new Date();
        while (it.hasNext()) {
            final ScheduledCommand command = it.next();
            if (new Date(command.getTimestamp()).after(now)) {
                continue;
            }
            this.api.runCommand(command.getCommand());
            it.remove();
        }
        ScheduledCommand next = null;
        while ((next = this.getQueue().poll()) != null) {
            this.commands.add(next);
        }
        this.save();
    }
    
    public void save() {
        this.storage.setCommands(this.commands);
        this.storage.save(this.api);
    }
    
    public ConcurrentLinkedQueue<ScheduledCommand> getQueue() {
        return this.queue;
    }
    
    public void setQueue(final ConcurrentLinkedQueue<ScheduledCommand> queue) {
        this.queue = queue;
    }
}
