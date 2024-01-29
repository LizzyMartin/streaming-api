package br.com.fiap.streaming.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import br.com.fiap.streaming.model.User;
import reactor.core.publisher.Flux;

public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Flux<User> findByUsername(String username);
}
