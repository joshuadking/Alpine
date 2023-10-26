package com.arcturusmc.alpine.user.UserModules;

import com.arcturusmc.alpine.Alpine;
import com.arcturusmc.alpine.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// This Module only Enables if CTFArcade is installed.
public class UserCTF {

    private Alpine alpine;
    private User user;

    public UserCTF(Alpine alpine, User user) {
        this.alpine = alpine;
        this.user = user;

        pullCTF();
    }

    // Statistics
    private int captures;

    private int returns;

    // Pull all class counts from the database to memory.
    public void pullCTF() {
        pullCaptures();
        pullReturns();
    }

    // Sync all local class counts to the database.
    public void syncCTF() {
        syncCaptures();
        syncReturns();
    }

    public void pullCaptures() {
        try {
            // Pull existing copy of jumps from the database and locally map it to the player.
            PreparedStatement sneakStatement = alpine.getSqlManager().getConnection().prepareStatement("SELECT amount FROM ctf_captures WHERE uuid = ?;");
            sneakStatement.setString(1, user.getId());

            ResultSet rs = sneakStatement.executeQuery();
            if(rs.next()) {
                captures = rs.getInt("amount");
            } else {

                // If the data doesn't already exist, create an entry and give the player a local copy.
                captures = 0;

                PreparedStatement insert = alpine.getSqlManager().getConnection().prepareStatement("INSERT INTO ctf_captures (uuid, amount) VALUES (?, default)");
                insert.setString(1, user.getId());
                insert.executeUpdate();
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }
    }

    // Add Jump to Local Count
    public void addCaptures() {
        this.captures = captures + 1;
    }

    // Set database jump count equal to count stored locally.
    public void syncCaptures() {
        try {
            PreparedStatement sneakStatement = alpine.getSqlManager().getConnection().prepareStatement("UPDATE ctf_captures SET amount = ? WHERE uuid = ?;");
            sneakStatement.setInt(1, captures);
            sneakStatement.setString(2, user.getId());

            sneakStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void pullReturns() {
        try {
            // Pull existing copy of jumps from the database and locally map it to the player.
            PreparedStatement returnStatement = alpine.getSqlManager().getConnection().prepareStatement("SELECT amount FROM ctf_returns WHERE uuid = ?;");
            returnStatement.setString(1, user.getId());

            ResultSet rs = returnStatement.executeQuery();
            if(rs.next()) {
                returns = rs.getInt("amount");
            } else {

                // If the data doesn't already exist, create an entry and give the player a local copy.
                returns = 0;

                PreparedStatement insert = alpine.getSqlManager().getConnection().prepareStatement("INSERT INTO ctf_returns (uuid, amount) VALUES (?, default)");
                insert.setString(1, user.getId());
                insert.executeUpdate();
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }
    }

    // Add Jump to Local Count
    public void addReturns() {
        this.returns = returns + 1;
    }

    // Set database jump count equal to count stored locally.
    public void syncReturns() {
        try {
            PreparedStatement returnStatement = alpine.getSqlManager().getConnection().prepareStatement("UPDATE ctf_returns SET amount = ? WHERE uuid = ?;");
            returnStatement.setInt(1, returns);
            returnStatement.setString(2, user.getId());

            returnStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCaptures() {
        return captures;
    }

    public int getReturns() {
        return returns;
    }
}
