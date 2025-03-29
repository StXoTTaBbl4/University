package org.xoeqvdp.domainModel;

import java.util.ArrayList;
import java.util.List;

public class Screen {
    private List<Obj> displayedObjects = new ArrayList<>();
    public String zoomedAt;

    public void show(Obj obj) {
        displayedObjects.add(obj);
    }

    public boolean isDisplayed(Obj obj) {
        return displayedObjects.contains(obj);
    }

    public void zoomIn(Obj obj) {
        if (isDisplayed(obj)) {
            System.out.println("Увеличение объекта: " + obj.name);
            zoomedAt = obj.name;
        } else {
            System.out.println("Объект не отображается на экране.");
        }
    }
}
