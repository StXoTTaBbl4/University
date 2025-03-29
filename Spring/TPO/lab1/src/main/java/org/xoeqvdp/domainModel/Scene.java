package org.xoeqvdp.domainModel;

public class Scene {
    public Screen screen;
    public Characters characters;

    public Scene() {
        this.screen = new Screen();
        this.characters = new Characters();
    }

    public void addCharacter(Character character) {
        characters.addCharacter(character.name, character);
    }

    public void displayObject(Obj obj) {
        screen.show(obj);
        obj.isNew = false;
        System.out.println(obj.name + " теперь отображается на экране.");
    }

}
