package com.example.myrecyclerview;

public class SettingItem {
    String name;
    String id;
    int info;

    public SettingItem(String name, String id, int info) {
        this.name = name;
        this.id = id;
        this.info = info;
    }

    public SettingItem(String name, int info) {
        this(name, null, info);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getInfo() {
        return info;
    }
}
