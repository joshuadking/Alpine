package com.arcturusmc.alpine.listeners.kills;

import com.arcturusmc.alpine.Alpine;
import com.arcturusmc.alpine.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Map;
import java.util.UUID;

public class KillListener implements Listener {

    private final Alpine alpine;

    public KillListener(Alpine alpine) {
        this.alpine = alpine;
    }


    // Adds the type of the entity killed to hashmap, adds category if applicable.
    @EventHandler
    public void playerKillEntityEvent(EntityDeathEvent e) {
        Entity dead = e.getEntity();

        if(e.getEntity().getKiller() != null) {
            UUID playerId = e.getEntity().getKiller().getUniqueId();
            User userKiller = alpine.getUserManager().getUser(playerId);

            userKiller.getCombatModule().addMobTypeKill(dead.getType().toString());

            if(dead instanceof Monster) {
                Monster killedMonster = (Monster) dead;
                userKiller.getCombatModule().addMonsterKill();

            } else if (dead instanceof Player) {
                userKiller.getCombatModule().addPlayerKill();
            }

        }
    }
}
