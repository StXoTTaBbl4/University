package org.xoeqvdp.tests;


import org.junit.Test;
import org.xoeqvdp.ArcCos;

import static org.junit.Assert.assertEquals;

public class ArcCosTests {
    public static ArcCos arcCos = new ArcCos();

    @Test
    public void testArccosBoundaries() {
        assertEquals(Math.acos(-1), arcCos.arccos(-1.0), 1e-6);
        assertEquals(Math.acos(0), arcCos.arccos( 0.0), 1e-6);
        assertEquals(Math.acos(1), arcCos.arccos(1.0), 1e-6);
    }

    @Test
    public void testArccosConvergence() {
        assertEquals(Math.acos(0.5), arcCos.arccos(0.5), 1e-3);
        assertEquals(Math.acos(-0.5), arcCos.arccos(-0.5), 1e-3);
        assertEquals(Math.acos(0.7), arcCos.arccos(0.7), 1e-3);
    }
}
