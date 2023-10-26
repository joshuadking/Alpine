package com.arcturusmc.alpine;

import com.arcturusmc.alpine.listeners.ctf.CTFListener;
import com.arcturusmc.ctfarcade.CTFArcade;
import org.bukkit.Bukkit;

public class Dependencies {

    private Alpine alpine;

    private CTFArcade ctfArcade = null;

    public Dependencies(Alpine alpine) {
        this.alpine = alpine;
    }

    public CTFArcade getCtfArcade() {
        if(Bukkit.getPluginManager().isPluginEnabled("CTFArcade")) {
            ctfArcade = (CTFArcade) Bukkit.getPluginManager().getPlugin("CTFArcade");

            // Registers the CTFArcade Listeners if the plugin is installed.
            Bukkit.getPluginManager().registerEvents(new CTFListener(alpine), alpine);
        }

        return ctfArcade;
    }
}
