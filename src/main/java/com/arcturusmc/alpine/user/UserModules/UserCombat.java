package com.arcturusmc.alpine.user.UserModules;

import com.arcturusmc.alpine.Alpine;
import com.arcturusmc.alpine.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserCombat {

    private Alpine alpine;

    private User user;

    private int playerKills;
    private int monsterKills;

    private HashMap<String, Integer> mobTypeKills;

    public UserCombat(Alpine alpine, User user) {
        this.alpine = alpine;
        this.user = user;

        mobTypeKills = new HashMap<>();

        pullKills();
    }

    public void pullKills() {
        pullMonsterKills();
        pullPlayerKills();
        pullMobTypeKills();
    }

    // Sync all data in the UserCombat class to the database.
    public void syncKills() {
        syncMonsterKills();
        syncPlayerKills();
        syncMobTypeKills();
    }

    // Pulls Monster Kills from Database
    public void pullMonsterKills() {

        try {
            // Pull existing copy kills/uuid/monsters from the database and locally map it to the player.
            PreparedStatement killStatement = alpine.getSqlManager().getConnection().prepareStatement("SELECT amount FROM kills WHERE uuid = ? AND category = 'monsters';");
            killStatement.setString(1, user.getId());

            ResultSet rs = killStatement.executeQuery();
            if(rs.next()) {
                monsterKills = rs.getInt("amount");
            } else {

                // If the data doesn't already exist, create an entry and give the player a local copy.
                monsterKills = 0;

                PreparedStatement insert = alpine.getSqlManager().getConnection().prepareStatement("INSERT INTO kills (uuid, category, amount) VALUES (?, 'monsters', default)");
                insert.setString(1, user.getId());
                insert.executeUpdate();
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }
    }

    // Locally adds an additional monster kill.
    public void addMonsterKill() {
        this.monsterKills = monsterKills + 1;
    }

    // Syncs Monster kills to the database.
    public void syncMonsterKills() {
        try {
            PreparedStatement connectionStatement = alpine.getSqlManager().getConnection().prepareStatement("UPDATE kills SET amount = ? WHERE uuid = ? AND category = 'monsters';");
            connectionStatement.setInt(1, monsterKills);
            connectionStatement.setString(2, user.getId());

            connectionStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void pullPlayerKills() {

        try {
            // Pull the stored data from Database and locally map it to the player.
            PreparedStatement killStatement = alpine.getSqlManager().getConnection().prepareStatement("SELECT amount FROM kills WHERE uuid = ? and category = 'players';");
            killStatement.setString(1, user.getId());

            ResultSet rs = killStatement.executeQuery();
            if(rs.next()) {
                playerKills = rs.getInt("amount");
            } else {

                // If the data doesn't already exist, generate a new copy and locally map it to the player.
                playerKills = 0;

                PreparedStatement insert = alpine.getSqlManager().getConnection().prepareStatement("INSERT INTO kills (uuid, category, amount) VALUES (?, 'players', default)");
                insert.setString(1, user.getId());
                insert.executeUpdate();
            }
        } catch (SQLException x) {

        }
    }

    // Locally adds an additional player kill.
    public void addPlayerKill() {
        this.playerKills = playerKills + 1;
    }

    // Syncs player kills with the database.
    public void syncPlayerKills() {
        try {
            PreparedStatement connectionStatement = alpine.getSqlManager().getConnection().prepareStatement("UPDATE kills SET amount = ? WHERE uuid = ? and category = 'player';");
            connectionStatement.setInt(1, playerKills);
            connectionStatement.setString(2, user.getId());

            connectionStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void pullMobTypeKills() {
        try {
            // Pull the stored data from Database and locally map it to the player.
            PreparedStatement killStatement = alpine.getSqlManager().getConnection().prepareStatement("SELECT * FROM killtype WHERE uuid = ?;");
            killStatement.setString(1, user.getId());

            ResultSet rs = killStatement.executeQuery();

            boolean records = false;
            while (rs.next()) {
                records = true;

                String type = rs.getString("type");
                int amount = rs.getInt("amount");

                System.out.println(rs.getString("type"));

                mobTypeKills.put(type, amount);
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }
    }

    // Updates local HashMap with mobType data, adds it if it doesn't exist.
    public void addMobTypeKill(String type) {
        if(mobTypeKills.containsKey(type)) {
            int newValue = mobTypeKills.get(type) + 1;
            System.out.println(newValue);
            mobTypeKills.replace(type, newValue);
        } else {
            mobTypeKills.put(type, 1);
        }
    }

    // Syncs local HashMap to the Database, replacing existing database values.
    public void syncMobTypeKills() {
        for(Map.Entry<String, Integer> set :
                mobTypeKills.entrySet()) {
            try {
                PreparedStatement killStatement = alpine.getSqlManager().getConnection().prepareStatement("SELECT * FROM killtype WHERE uuid = ? and type = ?;");
                //killStatement.setInt(1, set.getValue());
                killStatement.setString(1, user.getId());
                killStatement.setString(2, set.getKey());

                ResultSet rs = killStatement.executeQuery();

                if(rs.next()) {
                    PreparedStatement updateStatement = alpine.getSqlManager().getConnection().prepareStatement("UPDATE killtype SET amount = ? WHERE uuid = ? and type = ?;");
                    updateStatement.setInt(1, set.getValue());
                    updateStatement.setString(2, user.getId());
                    updateStatement.setString(3, set.getKey());

                    updateStatement.executeUpdate();

                } else {

                    PreparedStatement generateStatement = alpine.getSqlManager().getConnection().prepareStatement("INSERT INTO killtype (uuid, type, amount) VALUES (?, ?, ?);");
                    generateStatement.setString(1, user.getId());
                    generateStatement.setString(2, set.getKey());
                    generateStatement.setInt(3, set.getValue());

                    generateStatement.executeUpdate();

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<String, Integer> getMobTypeKills() {
        return mobTypeKills;
    }
}
