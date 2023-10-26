package com.arcturusmc.alpine.user;

import com.arcturusmc.alpine.Alpine;
import com.arcturusmc.alpine.user.UserModules.UserCTF;
import com.arcturusmc.alpine.user.UserModules.UserCombat;
import com.arcturusmc.alpine.user.UserModules.UserFishing;
import com.arcturusmc.alpine.user.UserModules.UserMovement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class User {

    private Alpine alpine;
    private UUID uuid;

    private int connections;

    private UserCombat combatModule;
    private UserMovement movementModule;
    private UserFishing fishModule;

    private UserCTF ctfModule;

    public User(Alpine alpine, UUID uuid) {
        this.alpine = alpine;
        this.uuid = uuid;

        this.combatModule = new UserCombat(alpine, this);
        this.movementModule = new UserMovement(alpine, this);
        this.fishModule = new UserFishing(alpine, this);

        enableHookedModules();

        pullMain();
    }

    // Uploads all data from the local user to database, overwriting online data.
    public void sync() {
        syncMain();
        combatModule.syncKills();
        movementModule.syncMovement();
        fishModule.syncFishing();

        syncHooks();
    }

    // Pulls all data from the database, overwriting locally stored data.
    private void pull() {
        pullMain();
        combatModule.pullKills();
        movementModule.pullMovement();
        fishModule.pullFishing();

        pullHooks();
    }

    private void enableHookedModules() {
        if (alpine.getDependencies().getCtfArcade() != null) {
            this.ctfModule = new UserCTF(alpine, this);
        }
    }

    public void pullHooks() {
        if (alpine.getDependencies().getCtfArcade() != null) {
            ctfModule.pullCTF();
        }
    }

    public void syncHooks() {
        if (alpine.getDependencies().getCtfArcade() != null) {
            ctfModule.syncCTF();
        }
    }


    private void syncMain() {
        syncConnections();
    }

    private void pullMain() {
        pullConnections();
    }

    public void pullConnections() {

        try {
            PreparedStatement connectionStatement = alpine.getSqlManager().getConnection().prepareStatement("SELECT connections FROM connections WHERE uuid = ?;");
            connectionStatement.setString(1, uuid.toString());

            ResultSet rs = connectionStatement.executeQuery();
            if (rs.next()) {
                connections = rs.getInt("connections");
            } else {
                connections = 0;

                PreparedStatement insert = alpine.getSqlManager().getConnection().prepareStatement("INSERT INTO connections (uuid, connections) VALUES (" +
                        "'" + getId() + "'," +
                        "default);");
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Unable to fetch User.java#getConnections!");
            e.printStackTrace();
        }
    }

    public void addConnection() {
        this.connections = connections + 1;
    }

    public void syncConnections() {
        try {
            PreparedStatement connectionStatement = alpine.getSqlManager().getConnection().prepareStatement("UPDATE connections SET connections = ? WHERE uuid = ?;");
            connectionStatement.setInt(1, connections);
            connectionStatement.setString(2, uuid.toString());

            connectionStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return uuid.toString();
    }
    public int getConnections() {
        return connections;
    }

    // MODULES GETTERS
    public UserCombat getCombatModule() {
        return combatModule;
    }

    public UserMovement getMovementModule() {
        return movementModule;
    }

    public UserFishing getFishModule() {
        return fishModule;
    }

    public UserCTF getCtfModule() {
        // Checks if CTF is installed before attempting to serve data.
        try {
            return ctfModule;
        } catch (NullPointerException x) {
            alpine.getLogger().log(Level.SEVERE, "Tried to fetch CTF Module, but CTFArcade isn't installed!");
        }
        return null;
    }
}
