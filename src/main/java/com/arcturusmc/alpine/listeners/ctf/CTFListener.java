package com.arcturusmc.alpine.listeners.ctf;

import com.arcturusmc.alpine.Alpine;
import com.arcturusmc.alpine.user.User;
import com.arcturusmc.ctfarcade.minigame.events.FlagCaptureEvent;
import com.arcturusmc.ctfarcade.minigame.events.FlagReturnEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class CTFListener implements Listener {

    private Alpine alpine;

    public CTFListener(Alpine alpine) {
        this.alpine = alpine;
    }

    @EventHandler
    public void onFlagCapture(FlagCaptureEvent e) {
        Player player = e.getPlayer();

        UUID playerId = e.getPlayer().getUniqueId();
        double health = e.getPlayerHealth();
        User user = alpine.getUserManager().getUser(playerId);

        // Adds a capture in the ctf_captures table
        user.getCtfModule().addCaptures();
    }

    @EventHandler
    public void onFlagReturn(FlagReturnEvent e) {
        Player player = e.getPlayer();

        UUID playerId = e.getPlayer().getUniqueId();
        User user = alpine.getUserManager().getUser(playerId);

        // Adds a capture in the ctf_captures table
        user.getCtfModule().addReturns();
    }
}
