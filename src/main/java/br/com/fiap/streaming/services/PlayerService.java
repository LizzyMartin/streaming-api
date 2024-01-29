package br.com.fiap.streaming.services;

import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import br.com.fiap.streaming.exceptions.VideoAlreadyLikedException;
import br.com.fiap.streaming.exceptions.VideoNotFoundException;
import br.com.fiap.streaming.model.Video;
import br.com.fiap.streaming.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private static final String VIDEO_FORMAT = "classpath:videos/%s/video.mp4";
    private static final String THUMBNAIL_FORMAT = "classpath:thumbnails/%s/thumbnail.jpg";

    private final VideoRepository videoRepository;
    private final ResourceLoader resourceLoader;

    public Mono<Void> like(String videoId, String userId) throws VideoNotFoundException {
        return videoRepository.findById(videoId)
            .switchIfEmpty(Mono.error(new VideoNotFoundException("Video with id " + videoId + " not found")))
            .flatMap(video -> {
                if (video.getLikedBy().contains(userId)) {
                    return Mono.error(new VideoAlreadyLikedException("Video already liked by user"));
                }
                video.setLikes(video.getLikes() + 1);
                video.getLikedBy().add(userId);
                return videoRepository.save(video).then();
            });
    }

    public Mono<Resource> play(String id) throws VideoNotFoundException {
        return videoRepository.findById(id)
            .switchIfEmpty(Mono.error(new VideoNotFoundException("Video with id " + id + " not found")))
            .flatMap(video -> Mono.fromSupplier(() -> resourceLoader.getResource(String.format(VIDEO_FORMAT, id))));
    }

    public Mono<Resource> getThumbnail(String id) throws VideoNotFoundException {
        return videoRepository.findById(id)
            .switchIfEmpty(Mono.error(new VideoNotFoundException("Video with id " + id + " not found")))
            .flatMap(video -> Mono.fromSupplier(() -> resourceLoader.getResource(String.format(THUMBNAIL_FORMAT, id))));
    }

}
