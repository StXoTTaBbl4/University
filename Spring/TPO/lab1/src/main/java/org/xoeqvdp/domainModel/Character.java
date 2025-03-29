package org.xoeqvdp.domainModel;

public class Character {
    String name;
    String role;
    public String lastInteraction;

    public Character(String name, String role) {
        this.name = name;
        this.role = role;
        this.lastInteraction = "";
    }

    public void interactWith(Character other) {
        this.lastInteraction = other.name;
        other.lastInteraction = this.name;
        System.out.println(this.name + " взаимодействует с " + other.name);
    }
}