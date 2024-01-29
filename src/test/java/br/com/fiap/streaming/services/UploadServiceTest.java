package br.com.fiap.streaming.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.codec.multipart.FilePart;

import br.com.fiap.streaming.exceptions.VideoNotFoundException;
import br.com.fiap.streaming.repository.VideoRepository;
import reactor.core.publisher.Mono;

public class UploadServiceTest {

    @Mock
    private UploadService uploadService;
    private VideoRepository videoRepository;

    @BeforeEach
    public void setup() {
        videoRepository = mock(VideoRepository.class);
        uploadService = new UploadService(videoRepository);
    }

    @Test
    public void testUploadVideoNotFound() throws IllegalStateException, IOException, VideoNotFoundException {
        FilePart filePart = mock(FilePart.class);
        Mono<FilePart> file = Mono.just(filePart);
        String videoId = "123";

        when(videoRepository.findById(videoId)).thenReturn(null);

        assertThrows(VideoNotFoundException.class, () -> uploadService.uploadThumbnail(file, videoId));
    }


    @Test
    public void testUploadThumbnailNotFound() throws IllegalStateException, IOException, VideoNotFoundException {
        FilePart filePart = mock(FilePart.class);
        Mono<FilePart> file = Mono.just(filePart);
        String videoId = "123";

        when(videoRepository.findById(videoId)).thenReturn(null);

        assertThrows(VideoNotFoundException.class, () -> uploadService.uploadThumbnail(file, videoId));
    }
}
