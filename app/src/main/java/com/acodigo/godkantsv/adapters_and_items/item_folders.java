package com.acodigo.godkantsv.adapters_and_items;

public class item_folders {
    private final String title;
    private final String description;

    private final boolean open;
    private final boolean done;
    private final boolean success;
    private final boolean lock;

    public item_folders(String title, String description, Boolean open, Boolean done, Boolean success, Boolean lock) {
        this.title = title;
        this.description = description;
        this.open = open;
        this.done = done;
        this.success = success;
        this.lock = lock;
    }

    String getTitle() {
        return title;
    }
    String getDescription() {
        return description;
    }

    public boolean isOpen() {
        return open;
    }
    public boolean isDone() {
        return done;
    }
    public boolean isSuccess() {
        return success;
    }
    public boolean isLock() {
        return lock;
    }
}