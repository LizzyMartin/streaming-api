package br.com.fiap.streaming.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VideoRequestDTOTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testVideoRequestDTO_ValidData() {
        // Arrange
        VideoRequestDTO videoRequestDTO = new VideoRequestDTO();
        videoRequestDTO.setTitle("Sample Title");
        videoRequestDTO.setDescription("Sample Description");
        videoRequestDTO.setUrl("https://example.com/video");
        videoRequestDTO.setPublicationDate(LocalDateTime.now().toString());
        videoRequestDTO.setThumbnail("https://example.com/thumbnail");
        videoRequestDTO.setDuration("00:05:30");
        videoRequestDTO.setCategory("Sample Category");
        videoRequestDTO.setTags(new HashSet<>());

        // Act
        Set<ConstraintViolation<VideoRequestDTO>> violations = validator.validate(videoRequestDTO);

        // Assert
        assertEquals(0, violations.size());
    }

    @Test
    public void testVideoRequestDTO_InvalidData() {
        // Arrange
        VideoRequestDTO videoRequestDTO = new VideoRequestDTO();
        videoRequestDTO.setTitle("");
        videoRequestDTO.setDescription("");
        videoRequestDTO.setUrl("invalid-url");
        videoRequestDTO.setPublicationDate("");
        videoRequestDTO.setThumbnail("");
        videoRequestDTO.setDuration("");
        videoRequestDTO.setCategory("");
        videoRequestDTO.setTags(null);

        // Act
        Set<ConstraintViolation<VideoRequestDTO>> violations = validator.validate(videoRequestDTO);

        // Assert
        assertEquals(4, violations.size());
    }

    @Test
    public void testVideoRequestDTO_DefaultValues() {
        // Arrange
        VideoRequestDTO videoRequestDTO = new VideoRequestDTO();

        // Act
        String publicationDate = videoRequestDTO.getPublicationDate();
        Integer views = videoRequestDTO.getViews();
        Integer likes = videoRequestDTO.getLikes();
        Set<String> tags = videoRequestDTO.getTags();
        Set<String> likedBy = videoRequestDTO.getLikedBy();
        Set<String> viewedBy = videoRequestDTO.getViewedBy();

        // Assert
        assertNotNull(publicationDate);
        assertEquals(0, views);
        assertEquals(0, likes);
        assertNotNull(tags);
        assertNotNull(likedBy);
        assertNotNull(viewedBy);
    }
}