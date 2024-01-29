package br.com.fiap.streaming.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VideoTest {

    private Video video;

    @BeforeEach
    public void setup() {
        video = new Video();
    }

    @Test
    public void testVideoInitialization() {
        assertNotNull(video);
    }

    @Test
    public void testVideoProperties() {
        String id = "123";
        String title = "Sample Video";
        String description = "This is a sample video";
        String url = "https://example.com/video";
        String publicationDate = "2022-01-01";
        String thumbnail = "https://example.com/thumbnail";
        String duration = "00:10:00";
        Integer views = 100;
        Integer likes = 50;
        Set<String> likedBy = new HashSet<>();
        likedBy.add("user1");
        likedBy.add("user2");
        String category = "Sample Category";
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        tags.add("tag2");
        Set<String> viewedBy = new HashSet<>();
        viewedBy.add("user1");
        viewedBy.add("user2");

        video.setId(id);
        video.setTitle(title);
        video.setDescription(description);
        video.setUrl(url);
        video.setPublicationDate(publicationDate);
        video.setThumbnail(thumbnail);
        video.setDuration(duration);
        video.setViews(views);
        video.setLikes(likes);
        video.setLikedBy(likedBy);
        video.setCategory(category);
        video.setTags(tags);
        video.setViewedBy(viewedBy);

        assertEquals(id, video.getId());
        assertEquals(title, video.getTitle());
        assertEquals(description, video.getDescription());
        assertEquals(url, video.getUrl());
        assertEquals(publicationDate, video.getPublicationDate());
        assertEquals(thumbnail, video.getThumbnail());
        assertEquals(duration, video.getDuration());
        assertEquals(views, video.getViews());
        assertEquals(likes, video.getLikes());
        assertEquals(likedBy, video.getLikedBy());
        assertEquals(category, video.getCategory());
        assertEquals(tags, video.getTags());
        assertEquals(viewedBy, video.getViewedBy());
    }
}