package br.com.fiap.streaming.controllers;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.streaming.services.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping(value = "/api/upload/video", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<URI> uploadVideo(@RequestPart("file") Mono<FilePart> file, @RequestPart("videoId") String videoId) {
            try{
                log.info("uploading: videoId={}", videoId);
                return uploadService.uploadVideo(file, videoId);
            } catch (Exception e) {
                return Mono.error(e);
            }
    }

    @PostMapping(value = "/api/upload/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<URI> uploadImage(@RequestPart("file") Mono<FilePart> file, @RequestPart("videoId") String videoId) {
        try{
            log.info("uploading: videoId={}", videoId);
            return uploadService.uploadThumbnail(file, videoId);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
    
}
