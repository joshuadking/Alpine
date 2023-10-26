package com.arcturusmc.alpine.configuration;

import com.arcturusmc.alpine.Alpine;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private static FileConfiguration config;

    public static void setupConfig(Alpine alpine) {
        ConfigManager.config = alpine.getConfig();
        alpine.saveDefaultConfig();
    }

    public static String getHost() {
        return config.getString("sql-credentials.hostname");
    }

    public static String getDatabase() {
        return config.getString("sql-credentials.database");
    }

    public static int getPort() {
        return config.getInt("sql-credentials.port");
    }

    public static String getUsername() {
        return config.getString("sql-credentials.username");
    }

    public static String getPassword() {
        return config.getString("sql-credentials.password");
    }
}
