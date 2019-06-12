package fr.vmarchaud.mineweb.common.configuration;

import com.google.gson.reflect.*;
import java.util.*;
import fr.vmarchaud.mineweb.common.*;
import java.io.*;

public class ScheduledStorage
{
    public File path;
    private Set<ScheduledCommand> commands;
    private static final TypeToken<Set<ScheduledCommand>> token;
    
    static {
        token = new TypeToken<Set<ScheduledCommand>>() {};
    }
    
    public ScheduledStorage(final File path) {
        this.commands = new HashSet<ScheduledCommand>();
        this.path = path;
    }
    
    public static ScheduledStorage load(final File path, final ICore api) {
        if (path.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(path);
                final ScheduledStorage conf = api.gson().fromJson(reader, ScheduledStorage.token.getType());
                conf.path = path;
                return conf;
            }
            catch (Exception e) {
                return new ScheduledStorage(path);
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException ex) {}
                }
            }
        }
        return new ScheduledStorage(path);
    }
    
    public void save(final ICore api) {
        try {
            final String config = api.gson().toJson(this.getCommands());
            final FileWriter writer = new FileWriter(this.path);
            writer.write(config);
            writer.close();
        }
        catch (IOException e) {
            api.logger().severe("Cant save the config file " + e.getMessage());
        }
    }
    
    public Set<ScheduledCommand> getCommands() {
        return this.commands;
    }
    
    public void setCommands(final Set<ScheduledCommand> commands) {
        this.commands = commands;
    }
}
