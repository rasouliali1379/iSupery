package com.rayanandisheh.isuperynew.models.drawer_model;


public class Drawer_Items {
    int id;
    Integer icon;
    String title;


    public Drawer_Items(int id, Integer icon, String title) {
        this.id = id;
        this.icon = icon;
        this.title = title;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
