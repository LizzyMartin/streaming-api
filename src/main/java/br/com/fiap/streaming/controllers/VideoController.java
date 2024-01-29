package br.com.fiap.streaming.controllers;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.streaming.dto.VideoRequestDTO;
import br.com.fiap.streaming.exceptions.VideoAlreadyLikedException;
import br.com.fiap.streaming.exceptions.VideoNotFoundException;
import br.com.fiap.streaming.model.Stats;
import br.com.fiap.streaming.model.Video;
import br.com.fiap.streaming.services.PlayerService;
import br.com.fiap.streaming.services.RecommendationService;
import br.com.fiap.streaming.services.StatsService;
import br.com.fiap.streaming.services.VideoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final StatsService statsService;
    private final PlayerService playerService;
    private final RecommendationService recommendationService;

    @GetMapping
    public Mono<Page<Video>> findAllVideos(Pageable pageable,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "publicationDate", required = false) String publicationDate) {
        return videoService.findAllVideos(pageable, title, category, publicationDate);
    }

    @GetMapping(value = "/{id}/thumbnail", produces = "image/jpeg")
    public Mono<Resource> thumbnail(@PathVariable("id") @NotBlank String id) {
        try {
            return playerService.getThumbnail(id);
        } catch (VideoNotFoundException e) {
            return Mono.error(e);
        }
    }

    @GetMapping(value = "/{id}/play", produces = "video/mp4")
    public Mono<Resource> play(@PathVariable("id") @NotBlank String id) {
        try {
            return playerService.play(id);
        } catch (VideoNotFoundException e) {
            return Mono.error(e);
        }
    }

    @GetMapping(value = "/{id}/like")
    public Mono<ResponseEntity<Object>> like(@PathVariable("id") @NotBlank String id) {
        try {
            return this.playerService.like(id, "me")
                    .then(Mono.just(ResponseEntity.ok().build()))
                    .onErrorResume(VideoAlreadyLikedException.class,
                            e -> Mono.just(ResponseEntity.badRequest().build()));
        } catch (VideoNotFoundException e) {
            return Mono.just(ResponseEntity.notFound().build());
        }
    }

    @GetMapping("/{id}")
    public Mono<Video> findById(@PathVariable("id") @NotBlank String id) {
        String userId = "me";
        return videoService.findById(id, userId);
    }

    @PutMapping("/{id}")
    public Mono<Video> update(@NotBlank String videoId, @Valid VideoRequestDTO video) {
        return videoService.update(videoId, video);
    }

    @PostMapping
    public Mono<Video> save(@Valid VideoRequestDTO video) {
        return videoService.save(video);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable @NotBlank @NonNull String id) {
        return videoService.deleteById(id);
    }

    @GetMapping("/stats")
    public Mono<Stats> getVideoStats() {
        return statsService.getVideoStats();
    }

    @GetMapping("/{userId}/recommendations")
    public Flux<Object> getRecommendations(@PathVariable("userId") @NotBlank String userId) {
        return recommendationService.recommendVideos(userId);
    }

}
