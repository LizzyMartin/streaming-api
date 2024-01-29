package br.com.fiap.streaming.repository;

import java.util.Set;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.streaming.model.Video;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VideoRepository extends ReactiveCrudRepository<Video, String> {
    Flux<Video> findByCategoryIn(Set<String> favoriteCategories);
    Mono<Long> countByLikesNotNull();
    Flux<Video> findAllBy(Query query);
}
