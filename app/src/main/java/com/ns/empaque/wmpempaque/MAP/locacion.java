package com.ns.empaque.wmpempaque.MAP;

/**
 * Created by jcalderon on 25/05/2016.
 */
public class locacion {
    private int idLocation, Width, Height, Lenght, Farm, type, framework, container, active;
    private String nameLocation, description, code;

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getContainer() {
        return container;
    }

    public void setContainer(int container) {
        this.container = container;
    }

    public int getFarm() {
        return Farm;
    }

    public void setFarm(int farm) {
        Farm = farm;
    }

    public int getFramework() {
        return framework;
    }

    public void setFramework(int framework) {
        this.framework = framework;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public int getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(int idLocation) {
        this.idLocation = idLocation;
    }

    public int getLenght() {
        return Lenght;
    }

    public void setLenght(int lenght) {
        Lenght = lenght;
    }

    public String getNameLocation() {
        return nameLocation;
    }

    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWidth() {
        return Width;
    }

    public void setWidth(int width) {
        Width = width;
    }

}


