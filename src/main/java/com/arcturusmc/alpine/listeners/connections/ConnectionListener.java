package com.arcturusmc.alpine.listeners.connections;

import com.arcturusmc.alpine.Alpine;
import com.arcturusmc.alpine.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    private Alpine alpine;

    public ConnectionListener(Alpine alpine) {
        this.alpine = alpine;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        User userData = new User(alpine, player.getUniqueId());

        alpine.getUserManager().addUser(player.getUniqueId(), userData);

        userData.addConnection();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        // Uploads all new data to the database upon departure.
        alpine.getUserManager().getUser(player.getUniqueId()).sync();

        alpine.getUserManager().removeUser(player.getUniqueId());
    }
}
