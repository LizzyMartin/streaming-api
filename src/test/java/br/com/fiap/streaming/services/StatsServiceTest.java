package br.com.fiap.streaming.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import br.com.fiap.streaming.model.Stats;
import br.com.fiap.streaming.model.Video;
import br.com.fiap.streaming.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class StatsServiceTest {

    private StatsService statsService;
    private VideoRepository videoRepository;

    @BeforeEach
    void setUp() {
        videoRepository = mock(VideoRepository.class);
        statsService = new StatsService(videoRepository);
    }

    @Test
    void testGetVideoStats() {
        // Mock the videoRepository methods
        when(videoRepository.count()).thenReturn(Mono.just(10L));
        when(videoRepository.countByLikesNotNull()).thenReturn(Mono.just(5L));
        when(videoRepository.findAll()).thenReturn(Flux.just(
                createVideoWithViews(100),
                createVideoWithViews(200),
                createVideoWithViews(300)
        ));

        // Call the getVideoStats method
        Mono<Stats> result = statsService.getVideoStats();

        // Verify the result
        StepVerifier.create(result)
                .expectNextMatches(stats -> {
                    assertEquals(10L, stats.getTotalVideos());
                    assertEquals(5L, stats.getTotalFavoritedVideos());
                    assertEquals(60.0, stats.getAverageViews());
                    return true;
                })
                .verifyComplete();

        // Verify the interactions with videoRepository
        verify(videoRepository, times(1)).count();
        verify(videoRepository, times(1)).countByLikesNotNull();
        verify(videoRepository, times(1)).findAll();
    }

    private Video createVideoWithViews(int views) {
        Video video = new Video();
        video.setViews(views);
        return video;
    }
}