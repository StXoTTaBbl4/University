package org.xoeqvdp.lab1.beans;

import org.xoeqvdp.lab1.model.Coordinates;

public interface CoordinatesService {
    boolean createCoordinates(Coordinates coordinates, Long userId);

    boolean getCoordinatesById(Long id);

    boolean updateCoordinates(Long id, Coordinates updatedCoordinates, Long userId);

    boolean deleteCoordinates(Long id);
}
