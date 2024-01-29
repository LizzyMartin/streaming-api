package br.com.fiap.streaming.services;

import org.springframework.stereotype.Service;

import br.com.fiap.streaming.repository.UserRepository;
import br.com.fiap.streaming.repository.VideoRepository;
import reactor.core.publisher.Flux;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public RecommendationService(VideoRepository videoRepository, UserRepository userRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
    }

    public Flux<Object> recommendVideos(String userId) {
        return userRepository.findById(userId)
                .map(user -> user.getFavoriteVideos())
                .map(favoriteVideos -> favoriteVideos.stream()
                        .map(videoId -> videoRepository.findById(videoId).block())
                        .map(video -> video.getCategory())
                        .collect(Collectors.toSet()))
                .flatMapMany(favoriteCategories -> videoRepository.findByCategoryIn(favoriteCategories));
    }
}
