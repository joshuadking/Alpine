package com.arcturusmc.alpine.user;

import com.arcturusmc.alpine.Alpine;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    Alpine alpine;
    public UserManager(Alpine alpine) {
        this.alpine = alpine;
    }

    private HashMap<UUID, User> users = new HashMap<UUID, User>();

    public User getUser(UUID uuid) {
        return users.get(uuid);
    }

    public void addUser(UUID uuid, User user) {
        users.put(uuid, user);
    }

    public void removeUser(UUID uuid) {

        // Syncs locally stored data.
        getUser(uuid).sync();

        users.remove(uuid);
    }
}
