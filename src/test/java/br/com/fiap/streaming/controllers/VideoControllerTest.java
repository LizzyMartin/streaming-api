package br.com.fiap.streaming.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerErrorException;

import br.com.fiap.streaming.model.Stats;
import br.com.fiap.streaming.model.Video;
import br.com.fiap.streaming.dto.VideoRequestDTO;
import br.com.fiap.streaming.exceptions.VideoAlreadyLikedException;
import br.com.fiap.streaming.exceptions.VideoNotFoundException;
import br.com.fiap.streaming.services.PlayerService;
import br.com.fiap.streaming.services.RecommendationService;
import br.com.fiap.streaming.services.StatsService;
import br.com.fiap.streaming.services.VideoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class VideoControllerTest {

    private VideoController videoController;
    private VideoService videoService;
    private StatsService statsService;
    private PlayerService playerService;
    private RecommendationService recommendationService;

    @BeforeEach
    public void setup() {
        videoService = mock(VideoService.class);
        statsService = mock(StatsService.class);
        playerService = mock(PlayerService.class);
        recommendationService = mock(RecommendationService.class);
        videoController = new VideoController(videoService, statsService, playerService, recommendationService);
    }

    @Test
    public void testSave() {
        VideoRequestDTO videoRequestDTO = mock(VideoRequestDTO.class);
        Video video = mock(Video.class);

        when(videoService.save(any(VideoRequestDTO.class))).thenReturn(Mono.just(video));

        Mono<Video> result = videoController.save(videoRequestDTO);

        StepVerifier.create(result)
                .expectNext(video)
                .verifyComplete();
    }

    @Test
    public void testSaveError() {
        VideoRequestDTO videoRequestDTO = mock(VideoRequestDTO.class);

        when(videoService.save(any(VideoRequestDTO.class)))
                .thenReturn(Mono.error(new ServerErrorException("Internal Server Error", new RuntimeException())));

        Mono<Video> result = videoController.save(videoRequestDTO);

        StepVerifier.create(result)
                .expectError(ServerErrorException.class)
                .verify();
    }

    @Test
    public void testGetVideoStats() {
        Stats stats = mock(Stats.class);
        when(statsService.getVideoStats()).thenReturn(Mono.just(stats));

        Mono<Stats> result = videoController.getVideoStats();

        StepVerifier.create(result)
                .expectNext(stats)
                .verifyComplete();
    }

    @Test
    public void testGetRecommendations() {
        String userId = "user123";
        Flux<Object> recommendations = Flux.just(new Object(), new Object());

        when(recommendationService.recommendVideos(userId)).thenReturn(recommendations);

        Flux<Object> result = videoController.getRecommendations(userId);

        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void testLike() throws VideoNotFoundException {
        String id = "video123";
        String userId = "me";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(playerService.like(id, userId)).thenReturn(Mono.empty());

        Mono<ResponseEntity<Object>> result = videoController.like(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(playerService).like(id, userId);
    }

    @Test
    public void testLikeVideoAlreadyLikedException() throws VideoNotFoundException {
        String id = "video123";
        String userId = "me";
        ResponseEntity<Object> expectedResponse = ResponseEntity.badRequest().build();

        when(playerService.like(id, userId)).thenReturn(Mono.error(() -> new VideoAlreadyLikedException()));

        Mono<ResponseEntity<Object>> result = videoController.like(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(playerService).like(id, userId);
    }

    @Test
    public void testLikeVideoNotFoundException() throws VideoNotFoundException {
        String id = "video123";
        ResponseEntity<Object> expectedResponse = ResponseEntity.notFound().build();

        when(playerService.like(id, "me")).thenThrow(new VideoNotFoundException());

        Mono<ResponseEntity<Object>> result = videoController.like(id);

        StepVerifier.create(result)
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(playerService).like(id, "me");
    }

    @Test
    public void testThumbnail() throws VideoNotFoundException {
        String id = "video123";
        Resource thumbnailResource = mock(Resource.class);

        when(playerService.getThumbnail(id)).thenReturn(Mono.just(thumbnailResource));

        Mono<Resource> result = videoController.thumbnail(id);

        StepVerifier.create(result)
                .expectNext(thumbnailResource)
                .verifyComplete();

        verify(playerService).getThumbnail(id);
    }

    @Test
    public void testThumbnailVideoNotFoundException() throws VideoNotFoundException {
        String id = "video123";

        when(playerService.getThumbnail(id)).thenThrow(new VideoNotFoundException());

        Mono<Resource> result = videoController.thumbnail(id);

        StepVerifier.create(result)
                .expectError(VideoNotFoundException.class)
                .verify();

        verify(playerService).getThumbnail(id);
    }

    @Test
    public void testFindAllVideos() {
        Pageable pageable = mock(Pageable.class);
        String title = "video";
        String category = "comedy";
        String publicationDate = "2022-01-01";
        Page<Video> videos = mock(Page.class);

        when(videoService.findAllVideos(pageable, title, category, publicationDate)).thenReturn(Mono.just(videos));

        Mono<Page<Video>> result = videoController.findAllVideos(0, 10, title, category, publicationDate);

        StepVerifier.create(result)
                .expectNext(videos)
                .verifyComplete();

        verify(videoService).findAllVideos(pageable, title, category, publicationDate);
    }

    @Test
    void play_WhenVideoExists_ShouldReturnVideo() throws VideoNotFoundException {
        String videoId = "123";
        Resource videoResource = new ByteArrayResource(new byte[0]);
        when(playerService.play(anyString())).thenReturn(Mono.just(videoResource));

        Mono<Resource> result = videoController.play(videoId);
        StepVerifier.create(result)
            .expectNext(videoResource)
            .verifyComplete();
    }

    @Test
    void play_WhenVideoDoesNotExist_ShouldThrowVideoNotFoundException() throws VideoNotFoundException {
        String videoId = "123";
        when(playerService.play(anyString())).thenReturn(Mono.error(new VideoNotFoundException("Video not found")));

        Mono<Resource> result = videoController.play(videoId);
        StepVerifier.create(result)
            .expectError(VideoNotFoundException.class)
            .verify();
    }

    @Test
    void findById_WhenVideoExists_ShouldReturnVideo() {
        String videoId = "123";
        Video video = new Video();
        when(videoService.findById(anyString(), anyString())).thenReturn(Mono.just(video));

        Mono<Video> result = videoController.findById(videoId);
        StepVerifier.create(result)
            .expectNext(video)
            .verifyComplete();
    }

    @Test
    void update_WhenVideoExists_ShouldReturnUpdatedVideo() {
        String videoId = "123";
        VideoRequestDTO videoRequestDTO = new VideoRequestDTO();
        Video updatedVideo = new Video();
        when(videoService.update(anyString(), any(VideoRequestDTO.class))).thenReturn(Mono.just(updatedVideo));

        Mono<Video> result = videoController.update(videoId, videoRequestDTO);
        StepVerifier.create(result)
            .expectNext(updatedVideo)
            .verifyComplete();
    }
}