package br.com.fiap.streaming.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatsTest {

    private Stats stats;

    @BeforeEach
    public void setup() {
        stats = new Stats();
    }

    @Test
    public void testGetSetTotalVideos() {
        long totalVideos = 10;
        stats.setTotalVideos(totalVideos);
        assertEquals(totalVideos, stats.getTotalVideos());
    }

    @Test
    public void testGetSetTotalFavoritedVideos() {
        long totalFavoritedVideos = 5;
        stats.setTotalFavoritedVideos(totalFavoritedVideos);
        assertEquals(totalFavoritedVideos, stats.getTotalFavoritedVideos());
    }

    @Test
    public void testGetSetAverageViews() {
        double averageViews = 100.5;
        stats.setAverageViews(averageViews);
        assertEquals(averageViews, stats.getAverageViews(), 0.001);
    }
}