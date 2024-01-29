package br.com.fiap.streaming.services;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import br.com.fiap.streaming.model.Video;
import br.com.fiap.streaming.repository.VideoRepository;
import br.com.fiap.streaming.dto.VideoRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class VideoServiceTest {

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById() {
        // Mock data
        String id = "123";
        String userId = "user1";
        Video video = new Video();
        video.setId(id);
        video.setViews(10);
        video.setLikedBy(new HashSet<>(Arrays.asList("user2")));

        // Mock behavior
        when(videoRepository.findById(id)).thenReturn(Mono.just(video));
        when(videoRepository.save(any(Video.class))).thenReturn(Mono.just(video));

        // Perform the method call
        Mono<Video> result = videoService.findById(id, userId);

        // Verify the result
        StepVerifier.create(result)
                .expectNextMatches(savedVideo -> {
                    Assert.isTrue(savedVideo.getId().equals(id), "Video ID should match");
                    Assert.isTrue(savedVideo.getViews() == 11, "Views should be incremented");
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void testUpdate() {
        // Mock data
        String id = "123";
        VideoRequestDTO videoRequest = new VideoRequestDTO();
        videoRequest.setTitle("Updated Title");
        videoRequest.setDescription("Updated Description");
        videoRequest.setDuration("00:20:00");
        videoRequest.setCategory("Updated Category");
        Set<String> tags = new HashSet<>(Arrays.asList("tag1", "tag2"));
        videoRequest.setTags(tags);

        Video videoToUpdate = new Video();
        videoToUpdate.setId(id);

        // Mock behavior
        when(videoRepository.findById(id)).thenReturn(Mono.just(videoToUpdate));
        when(videoRepository.save(any(Video.class))).thenReturn(Mono.just(videoToUpdate));

        // Perform the method call
        Mono<Video> result = videoService.update(id, videoRequest);

        // Verify the result
        StepVerifier.create(result)
                .expectNextMatches(savedVideo -> {
                    Assert.isTrue(savedVideo.getId().equals(id), "Video ID should match");
                    Assert.isTrue(savedVideo.getTitle().equals(videoRequest.getTitle()), "Title should be updated");
                    Assert.isTrue(savedVideo.getDescription().equals(videoRequest.getDescription()),
                            "Description should be updated");
                    Assert.isTrue(savedVideo.getDuration().equals(videoRequest.getDuration()), "Duration should be updated");
                    Assert.isTrue(savedVideo.getCategory().equals(videoRequest.getCategory()), "Category should be updated");
                    Assert.isTrue(savedVideo.getTags().equals(videoRequest.getTags()), "Tags should be updated");
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void testSave() {
        // Mock data
        VideoRequestDTO videoRequest = new VideoRequestDTO();
        videoRequest.setTitle("Sample Title");
        videoRequest.setDescription("Sample Description");
        videoRequest.setDuration("00:10:00");
        videoRequest.setCategory("Sample Category");
        Set<String> tags = new HashSet<>(Arrays.asList("tag1", "tag2"));
        videoRequest.setTags(tags);
        videoRequest.setPublicationDate("2022-01-01");
        videoRequest.setLikedBy(new HashSet<>(Arrays.asList("user1")));
        videoRequest.setViewedBy(new HashSet<>(Arrays.asList("user2")));

        // Mock behavior
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> {
            Video savedVideo = invocation.getArgument(0);
            savedVideo.setId("123");
            return Mono.just(savedVideo);
        });

        // Perform the method call
        Mono<Video> result = videoService.save(videoRequest);

        // Verify the result
        StepVerifier.create(result)
                .expectNextMatches(savedVideo -> {
                    Assert.isTrue(savedVideo.getId().equals("123"), "Video ID should be set");
                    Assert.isTrue(savedVideo.getTitle().equals(videoRequest.getTitle()), "Title should match");
                    Assert.isTrue(savedVideo.getDescription().equals(videoRequest.getDescription()), "Description should match");
                    Assert.isTrue(savedVideo.getDuration().equals(videoRequest.getDuration()), "Duration should match");
                    Assert.isTrue(savedVideo.getCategory().equals(videoRequest.getCategory()), "Category should match");
                    Assert.isTrue(savedVideo.getTags().equals(videoRequest.getTags()), "Tags should match");
                    Assert.isTrue(savedVideo.getPublicationDate().equals(videoRequest.getPublicationDate()), "Publication date should match");
                    Assert.isTrue(savedVideo.getLikes() == 0, "Likes should be initialized");
                    Assert.isTrue(savedVideo.getViews() == 0, "Views should be initialized");
                    Assert.isTrue(savedVideo.getLikedBy().equals(videoRequest.getLikedBy()), "LikedBy should match");
                    Assert.isTrue(savedVideo.getViewedBy().equals(videoRequest.getViewedBy()), "ViewedBy should match");
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void testDeleteById() {
        // Mock data
        String id = "123";

        // Mock behavior
        when(videoRepository.deleteById(id)).thenReturn(Mono.empty());

        // Perform the method call
        Mono<Void> result = videoService.deleteById(id);

        // Verify the result
        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(videoRepository, times(1)).deleteById(id);
    }
}