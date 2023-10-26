package com.arcturusmc.alpine.commands;

import com.arcturusmc.alpine.Alpine;
import com.arcturusmc.alpine.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddFishCatchesCommand implements CommandExecutor {

    private Alpine alpine;

    public AddFishCatchesCommand(Alpine alpine) {
        this.alpine = alpine;
    }

    // TODO: Close off DB addition openness.
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player &&  sender.isOp() ||
                sender instanceof ConsoleCommandSender) {
            if(args.length == 2) {
                if(Bukkit.getPlayerExact(args[0]) != null) {
                    Player receiver = Bukkit.getPlayerExact(args[0]);
                    User user = alpine.getUserManager().getUser(receiver.getUniqueId());

                    String fishType = args[1];

                    user.getFishModule().addFishTypeCatch(fishType);
                }
            } else {
                System.out.println("Bad syntax! Use /addcatch <player> <fish>");
            }
        }



        return true;
    }
}
