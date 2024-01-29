package br.com.fiap.streaming.services;

import org.springframework.stereotype.Service;

import br.com.fiap.streaming.model.Stats;
import br.com.fiap.streaming.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final VideoRepository videoRepository;

    public Mono<Stats> getVideoStats() {
        Stats stats = new Stats();

        return videoRepository.count()
                .doOnNext(count -> stats.setTotalVideos(count))
                .then(videoRepository.countByLikesNotNull())
                .doOnNext(count -> stats.setTotalFavoritedVideos(count))
                .then(videoRepository.findAll().map(video -> video.getViews()).reduce(0.0,
                        Double::sum))
                .doOnNext(sum -> stats.setAverageViews(sum / stats.getTotalVideos()))
                .thenReturn(stats);
    }

}
