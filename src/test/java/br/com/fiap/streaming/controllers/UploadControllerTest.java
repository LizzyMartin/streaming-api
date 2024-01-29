package br.com.fiap.streaming.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.server.ServerErrorException;

import br.com.fiap.streaming.exceptions.VideoNotFoundException;
import br.com.fiap.streaming.services.UploadService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class UploadControllerTest {

    private UploadController uploadController;
    private UploadService uploadService;

    @BeforeEach
    public void setup() {
        uploadService = mock(UploadService.class);
        uploadController = new UploadController(uploadService);
    }

    @Test
    public void testUploadVideo() throws IllegalStateException, IOException, VideoNotFoundException {
        FilePart filePart = mock(FilePart.class);
        Mono<FilePart> file = Mono.just(filePart);
        String videoId = "123";

        when(uploadService.uploadVideo(any(Mono.class), any(String.class)))
                .thenReturn(Mono.just(URI.create("http://example.com/video")));

        Mono<URI> result = uploadController.uploadVideo(file, videoId);

        StepVerifier.create(result)
                .expectNextMatches(uri -> uri.toString().equals("http://example.com/video"))
                .verifyComplete();
    }

    @Test
    public void testUploadVideoError() throws IllegalStateException, IOException, VideoNotFoundException {
        FilePart filePart = mock(FilePart.class);
        Mono<FilePart> file = Mono.just(filePart);
        String videoId = "123";

        when(uploadService.uploadVideo(any(Mono.class), any(String.class)))
                .thenReturn(Mono.error(new ServerErrorException("Internal Server Error", new RuntimeException())));

        Mono<URI> result = uploadController.uploadVideo(file, videoId);

        StepVerifier.create(result)
                .expectError(ServerErrorException.class)
                .verify();
    }

    @Test
    public void testUploadImage() throws IllegalStateException, IOException, VideoNotFoundException {
        FilePart filePart = mock(FilePart.class);
        Mono<FilePart> file = Mono.just(filePart);
        String videoId = "123";

        when(uploadService.uploadThumbnail(any(Mono.class), any(String.class)))
                .thenReturn(Mono.just(URI.create("http://example.com/image")));

        Mono<URI> result = uploadController.uploadImage(file, videoId);

        StepVerifier.create(result)
                .expectNextMatches(uri -> uri.toString().equals("http://example.com/image"))
                .verifyComplete();
    }

    @Test
    public void testUploadImageError() throws IllegalStateException, IOException, VideoNotFoundException {
        FilePart filePart = mock(FilePart.class);
        Mono<FilePart> file = Mono.just(filePart);
        String videoId = "123";

        when(uploadService.uploadThumbnail(any(Mono.class), any(String.class)))
                .thenReturn(Mono.error(new ServerErrorException("Internal Server Error", new RuntimeException())));

        Mono<URI> result = uploadController.uploadImage(file, videoId);

        StepVerifier.create(result)
                .expectError(ServerErrorException.class)
                .verify();
    }
}
