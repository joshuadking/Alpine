package com.arcturusmc.alpine.user.UserModules;

import com.arcturusmc.alpine.Alpine;
import com.arcturusmc.alpine.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMovement {

    private Alpine alpine;
    private User user;

    public UserMovement(Alpine alpine, User user) {
        this.alpine = alpine;
        this.user = user;

        pullMovement();
    }

    // Statistics
    private int sneaks;

    // Pull all class counts from the database to memory.
    public void pullMovement() {
        pullSneaks();
    }

    // Sync all local class counts to the database.
    public void syncMovement() {
        syncSneaks();
    }

    public void pullSneaks() {
        try {
            // Pull existing copy of jumps from the database and locally map it to the player.
            PreparedStatement sneakStatement = alpine.getSqlManager().getConnection().prepareStatement("SELECT amount FROM sneaks WHERE uuid = ?;");
            sneakStatement.setString(1, user.getId());

            ResultSet rs = sneakStatement.executeQuery();
            if(rs.next()) {
                sneaks = rs.getInt("amount");
            } else {

                // If the data doesn't already exist, create an entry and give the player a local copy.
                sneaks = 0;

                PreparedStatement insert = alpine.getSqlManager().getConnection().prepareStatement("INSERT INTO sneaks (uuid, amount) VALUES (?, default)");
                insert.setString(1, user.getId());
                insert.executeUpdate();
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }
    }

    // Add Jump to Local Count
    public void addSneak() {
        this.sneaks = sneaks + 1;
    }

    // Set database jump count equal to count stored locally.
    public void syncSneaks() {
        try {
            PreparedStatement sneakStatement = alpine.getSqlManager().getConnection().prepareStatement("UPDATE sneaks SET amount = ? WHERE uuid = ?;");
            sneakStatement.setInt(1, sneaks);
            sneakStatement.setString(2, user.getId());

            sneakStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
