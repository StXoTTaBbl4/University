package org.xoeqvdp.domainModel;

import java.util.HashMap;

public class Characters {
    private HashMap<String, Character> characters = new HashMap<>();

    public boolean addCharacter(String name, Character c) {
        if (characters.containsKey(name)) return false;
        characters.put(name, c);
        return true;
    }

    public boolean isPresent(String name) {
        return characters.containsKey(name);
    }

    public boolean interact(String name1, String name2) {
        if (!characters.containsKey(name1) || !characters.containsKey(name2)) return false;

        Character c1 = characters.get(name1);
        Character c2 = characters.get(name2);
        c1.interactWith(c2);
        return true;
    }
}