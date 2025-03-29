package org.xoeqvdp.tests;

import org.junit.Test;
import org.xoeqvdp.domainModel.*;
import org.xoeqvdp.domainModel.Character;

import static org.junit.Assert.*;

public class DomainModelTests {
    @Test
    public void testDomainModel() {
        Characters characters = new Characters();
        Character ford = new Character("Форд", "путешественник");
        Character zafod = new Character("Зафод", "путешественник");
        Screen screen = new Screen();
        Obj rockets = new Obj("ракеты");

        characters.addCharacter("Форд", ford);
        characters.addCharacter("Зафод", zafod);

        screen.show(rockets);
        assertTrue(characters.isPresent("Форд"));
        assertFalse(characters.isPresent("Не Форд"));
        assertTrue(screen.isDisplayed(rockets));

        //Расширить персонажа(взаимодействие(кто последний)), объект(видели-не-видели)
    }

    @Test
    public void testAddCharacter() {
        Characters characters = new Characters();
        Character ford = new Character("Форд", "Путешественник");
        assertTrue(characters.addCharacter("Форд", ford));
        assertFalse(characters.addCharacter("Форд", ford)); //  дубль
    }

    @Test
    public void testIsPresent() {
        Characters characters = new Characters();
        Character ford = new Character("Форд", "Путешественник");
        characters.addCharacter("Форд", ford);
        assertTrue(characters.isPresent("Форд"));
        assertFalse(characters.isPresent("Зафод"));
    }

    @Test
    public void testInteract() {
        Characters characters = new Characters();
        Character ford = new Character("Форд", "Путешественник");
        Character zaphod = new Character("Зафод", "Президент");

        characters.addCharacter("Форд", ford);
        characters.addCharacter("Зафод", zaphod);

        assertTrue(characters.interact("Форд", "Зафод"));
        assertEquals("Зафод", ford.lastInteraction);
        assertEquals("Форд", zaphod.lastInteraction);
    }

    @Test
    public void testInteractWithNonExistentCharacter() {
        Characters characters = new Characters();
        Character ford = new Character("Форд", "Путешественник");
        characters.addCharacter("Форд", ford);

        assertFalse(characters.interact("Форд", "Зафод")); // Зафода нет
    }

    @Test
    public void testShowAndIsDisplayed() {
        Screen screen = new Screen();
        Obj rocket = new Obj("Rocket");

        assertFalse(screen.isDisplayed(rocket));
        screen.show(rocket);
        assertTrue(screen.isDisplayed(rocket));
    }

    @Test
    public void testZoomIn() {
        Screen screen = new Screen();
        Obj rocket = new Obj("Rocket");
        screen.show(rocket);
        screen.zoomIn(rocket);

        assertEquals(rocket.name, screen.zoomedAt);
    }

    @Test
    public void testAddCharacterToScene() {
        Scene scene = new Scene();
        Character ford = new Character("Ford", "Traveler");
        scene.addCharacter(ford);
        assertTrue(scene.characters.isPresent("Ford"));
    }

    @Test
    public void testDisplayObjectInScene() {
        Scene scene = new Scene();
        Obj rocket = new Obj("Rocket");
        scene.displayObject(rocket);
        assertTrue(scene.screen.isDisplayed(rocket));
        assertFalse(rocket.isNew);
    }
}
