package com.arcturusmc.alpine.user.UserModules;

import com.arcturusmc.alpine.Alpine;
import com.arcturusmc.alpine.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserFishing {

    private Alpine alpine;

    private User user;

    private HashMap<String, Integer> fishCaught;

    public UserFishing(Alpine alpine, User user) {
        this.alpine = alpine;
        this.user = user;

        fishCaught = new HashMap<>();

        pullFishing();
    }

    // Master Synchronization & Polling
    public void pullFishing() {
        pullFishTypeCaught();
    }

    public void syncFishing() {
        syncFishTypeCaught();
    }

    public void pullFishTypeCaught() {
        try {
            // Pull the stored data from Database and locally map it to the player.
            PreparedStatement catchStatement = alpine.getSqlManager().getConnection().prepareStatement("SELECT * FROM fishtype WHERE uuid = ?;");
            catchStatement.setString(1, user.getId());

            ResultSet rs = catchStatement.executeQuery();

            boolean records = false;
            while (rs.next()) {
                records = true;

                String type = rs.getString("type");
                int amount = rs.getInt("amount");

                System.out.println(rs.getString("type"));

                fishCaught.put(type, amount);
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }
    }

    // Updates local HashMap with mobType data, adds it if it doesn't exist.
    public void addFishTypeCatch(String type) {
        if(fishCaught.containsKey(type)) {
            int newValue = fishCaught.get(type) + 1;
            System.out.println(newValue);
            fishCaught.replace(type, newValue);
        } else {
            fishCaught.put(type, 1);
        }
    }

    // Syncs local HashMap to the Database, replacing existing database values.
    public void syncFishTypeCaught() {
        for(Map.Entry<String, Integer> set :
                fishCaught.entrySet()) {
            try {
                PreparedStatement killStatement = alpine.getSqlManager().getConnection().prepareStatement("SELECT * FROM fishtype WHERE uuid = ? and type = ?;");
                //killStatement.setInt(1, set.getValue());
                killStatement.setString(1, user.getId());
                killStatement.setString(2, set.getKey());

                ResultSet rs = killStatement.executeQuery();

                if(rs.next()) {
                    PreparedStatement updateStatement = alpine.getSqlManager().getConnection().prepareStatement("UPDATE fishtype SET amount = ? WHERE uuid = ? and type = ?;");
                    updateStatement.setInt(1, set.getValue());
                    updateStatement.setString(2, user.getId());
                    updateStatement.setString(3, set.getKey());

                    updateStatement.executeUpdate();

                } else {

                    PreparedStatement generateStatement = alpine.getSqlManager().getConnection().prepareStatement("INSERT INTO fishtype (uuid, type, amount) VALUES (?, ?, ?);");
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

    public HashMap<String, Integer> getFishCaught() {
        return fishCaught;
    }
}
