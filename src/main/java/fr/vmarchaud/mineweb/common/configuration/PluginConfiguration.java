package fr.vmarchaud.mineweb.common.configuration;

import fr.vmarchaud.mineweb.common.*;
import java.io.*;

public class PluginConfiguration
{
    public transient File path;
    public String logLevel;
    public String secretkey;
    public String domain;
    public Integer port;
    
    public PluginConfiguration(final File path) {
        this.logLevel = "FINE";
        this.path = path;
    }
    
    public static PluginConfiguration load(final File path, final ICore api) {
        if (path.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(path);
                final PluginConfiguration conf = api.gson().fromJson(reader, PluginConfiguration.class);
                conf.path = path;
                return conf;
            }
            catch (Exception e) {
                api.logger().warning("Config file is invalid, replacing with a new one (" + e.getMessage() + ")");
                return new PluginConfiguration(path);
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
        api.logger().warning("Cant find a config file, creating it");
        return new PluginConfiguration(path);
    }
    
    public void save(final ICore api) {
        try {
            final String config = api.gson().toJson(this);
            final FileWriter writer = new FileWriter(this.path);
            writer.write(config);
            writer.close();
        }
        catch (IOException e) {
            api.logger().severe("Cant save the config file " + e.getMessage());
        }
    }
}
