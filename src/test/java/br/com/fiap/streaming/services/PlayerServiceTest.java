package br.com.fiap.streaming.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.fiap.streaming.exceptions.VideoAlreadyLikedException;
import br.com.fiap.streaming.exceptions.VideoNotFoundException;
import br.com.fiap.streaming.model.Video;
import br.com.fiap.streaming.repository.VideoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PlayerServiceTest {

    private PlayerService playerService;
    private VideoRepository videoRepository;
    private ResourceLoader resourceLoader;

    @BeforeEach
    void setup() {
        videoRepository = mock(VideoRepository.class);
        resourceLoader = mock(ResourceLoader.class);
        playerService = new PlayerService(videoRepository, resourceLoader);
    }

    @Test
    void like_WhenVideoExistsAndUserHasNotLiked_ShouldIncrementLikesAndAddUserToLikedBy()
            throws VideoNotFoundException {
        // Arrange
        String videoId = "123";
        String userId = "user1";
        Video video = new Video();
        video.setId(videoId);
        video.setLikes(10);
        video.getLikedBy().add("user2");

        when(videoRepository.findById(videoId)).thenReturn(Mono.just(video));
        when(videoRepository.save(video)).thenReturn(Mono.just(video));

        // Act
        Mono<Void> result = playerService.like(videoId, userId);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
        assertEquals(11, video.getLikes());
        assertEquals(2, video.getLikedBy().size());
        assertEquals(true, video.getLikedBy().contains(userId));
    }

    @Test
    void like_WhenVideoExistsAndUserHasAlreadyLiked_ShouldThrowVideoAlreadyLikedException()
            throws VideoNotFoundException {
        // Arrange
        String videoId = "123";
        String userId = "user1";
        Video video = new Video();
        video.setId(videoId);
        video.getLikedBy().add(userId);

        when(videoRepository.findById(videoId)).thenReturn(Mono.just(video));
        var result = playerService.like(videoId, userId);

        StepVerifier.create(result)
                .expectError(VideoAlreadyLikedException.class)
                .verify();
    }

    @Test
    void like_WhenVideoDoesNotExist_ShouldThrowVideoNotFoundException() throws VideoNotFoundException {
        // Arrange
        String videoId = "123";
        String userId = "user1";

        when(videoRepository.findById(videoId)).thenReturn(Mono.empty());
        var result = playerService.like(videoId, userId);

        StepVerifier.create(result)
                .expectError(VideoNotFoundException.class)
                .verify();
    }

    @Test
    void play_WhenVideoExists_ShouldReturnVideoResource() throws VideoNotFoundException {
        // Arrange
        String videoId = "123";
        Resource expectedResource = mock(Resource.class);

        when(resourceLoader.getResource(String.format("classpath:videos/%s/video.mp4", videoId)))
                .thenReturn(expectedResource);
        when(videoRepository.findById(videoId)).thenReturn(Mono.just(new Video()));

        // Act
        Mono<Resource> result = playerService.play(videoId);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedResource)
                .verifyComplete();
    }

    @Test
    void play_WhenVideoDoesNotExist_ShouldThrowVideoNotFoundException() throws VideoNotFoundException {
        String videoId = "123";

        when(videoRepository.findById(videoId)).thenReturn(Mono.empty());
        var result = playerService.play(videoId);

        StepVerifier.create(result)
                .expectError(VideoNotFoundException.class)
                .verify();
    }

    @Test
    void getThumbnail_WhenVideoExists_ShouldReturnThumbnailResource() throws VideoNotFoundException {
        // Arrange
        String videoId = "123";
        Resource expectedResource = mock(Resource.class);

        when(resourceLoader.getResource(String.format("classpath:thumbnails/%s/thumbnail.jpg", videoId)))
                .thenReturn(expectedResource);
        when(videoRepository.findById(videoId)).thenReturn(Mono.just(new Video()));

        // Act
        Mono<Resource> result = playerService.getThumbnail(videoId);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedResource)
                .verifyComplete();
    }

    @Test
    void getThumbnail_WhenVideoDoesNotExist_ShouldThrowVideoNotFoundException() throws VideoNotFoundException {
        String videoId = "123";

        when(videoRepository.findById(videoId)).thenReturn(Mono.empty());
        var result = playerService.getThumbnail(videoId);

        StepVerifier.create(result)
                .expectError(VideoNotFoundException.class)
                .verify();
    }
}