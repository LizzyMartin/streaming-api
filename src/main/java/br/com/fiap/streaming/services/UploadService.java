package br.com.fiap.streaming.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import br.com.fiap.streaming.exceptions.VideoNotFoundException;
import br.com.fiap.streaming.repository.VideoRepository;
import reactor.core.publisher.Mono;

@Service
public class UploadService {

    private final Path videoBasePath = Paths.get("./src/main/resources/videos/");
    private final Path thumbnailsBasePath = Paths.get("./src/main/resources/thumbnails/");

    private final VideoRepository videoRepository;

    public UploadService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }
    
    public Mono<URI> uploadVideo(Mono<FilePart> file, String videoId) throws IllegalStateException, IOException, VideoNotFoundException {
        var video = videoRepository.findById(videoId);
        if (video != null) {
            return file.flatMap(part -> {
                new File(videoBasePath.toString() + "\\" + videoId).mkdirs();
                var path = Paths.get(videoBasePath.toString() + "\\" + videoId + "\\video.mp4");
                var fileMono = part.transferTo(path);
                return fileMono.then(Mono.just(URI.create("http://localhost:8080/api/videos/" + videoId + "/play")));
            });
        }
        throw new VideoNotFoundException(videoId);
    }

    public Mono<URI> uploadThumbnail(Mono<FilePart> file, String videoId) throws IllegalStateException, IOException, VideoNotFoundException {
        if (videoId != null) {
            var video = videoRepository.findById(videoId);
            if (video != null) {
                return file.flatMap(part -> {
                    new File(videoBasePath.toString() + "\\" + videoId).mkdirs();
                    var path = Paths.get(thumbnailsBasePath.toString() + "\\" + videoId + "\\image.jpg");
                    var fileMono = part.transferTo(path);
                    return fileMono.then(Mono.just(URI.create("http://localhost:8080/api/videos/" + videoId + "/thumbnail")));
                });
            }
            throw new VideoNotFoundException(videoId);
        }
        throw new IllegalArgumentException("videoId cannot be null");
    }
    
}
