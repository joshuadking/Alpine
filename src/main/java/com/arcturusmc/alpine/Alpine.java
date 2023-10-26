package com.arcturusmc.alpine;

import com.arcturusmc.alpine.commands.AddFishCatchesCommand;
import com.arcturusmc.alpine.configuration.ConfigManager;
import com.arcturusmc.alpine.db.SQLManager;
import com.arcturusmc.alpine.listeners.MovementListener;
import com.arcturusmc.alpine.listeners.connections.ConnectionListener;
import com.arcturusmc.alpine.listeners.kills.KillListener;
import com.arcturusmc.alpine.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Alpine extends JavaPlugin {

    private Dependencies dependencies;
    private SQLManager sqlManager;
    private UserManager userManager;

    @Override
    public void onEnable() {

        dependencies = new Dependencies(this);

        // Creates config.yml plugin file.
        ConfigManager.setupConfig(this);

        sqlManager = new SQLManager();
        userManager = new UserManager(this);

        databaseConnect();

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new KillListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MovementListener(this), this);

        getCommand("addcatch").setExecutor(new AddFishCatchesCommand(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        databaseDisconnect();
    }

    public Dependencies getDependencies() {
        return dependencies;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    private void databaseConnect() {
        try {
            sqlManager.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void databaseDisconnect() {
        sqlManager.disconnect();
    }
}
