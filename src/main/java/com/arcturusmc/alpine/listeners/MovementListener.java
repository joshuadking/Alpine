package com.arcturusmc.alpine.listeners;

import com.arcturusmc.alpine.Alpine;
import com.arcturusmc.alpine.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.UUID;

public class MovementListener implements Listener {

    private final Alpine alpine;

    public MovementListener(Alpine alpine) {
        this.alpine = alpine;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if(e.isSneaking()) {

            UUID playerId = e.getPlayer().getUniqueId();
            User user = alpine.getUserManager().getUser(playerId);

            user.getMovementModule().addSneak();
        }
    }
}
