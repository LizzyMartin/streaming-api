package br.com.fiap.streaming.services;

import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.mongodb.lang.Nullable;

import br.com.fiap.streaming.dto.VideoRequestDTO;
import br.com.fiap.streaming.model.Video;
import br.com.fiap.streaming.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final ReactiveMongoTemplate mongoTemplate;
    private final VideoRepository videoRepository;

    public Mono<Page<Video>> findAllVideos(Pageable pageable, @Nullable String title, @Nullable String category,
            @Nullable String publicationDate) {
        Query query = new Query().with(pageable);
        if (title != null) {
            query.addCriteria(
                    Criteria.where("title").regex(Pattern.compile(Pattern.quote(title), Pattern.CASE_INSENSITIVE)));
        }
        if (category != null) {
            query.addCriteria(Criteria.where("category").is(category));
        }
        if (publicationDate != null) {
            query.addCriteria(Criteria.where("publicationDate").is(publicationDate));
        }

        return mongoTemplate.find(query, Video.class)
                .collectList()
                .zipWith(mongoTemplate.count(query, Video.class))
                .map(p -> new PageImpl<Video>(p.getT1(), pageable, p.getT2()));
    }

    public Mono<Video> findById(String id, String userId) {
        return videoRepository.findById(id)
                .flatMap(video -> {
                    if (!video.getViewedBy().contains(userId)) {
                        video.setViews(video.getViews() + 1);
                        video.getViewedBy().add(userId);
                    }
                    return videoRepository.save(video);
                });
    }

    public Mono<Video> update(String id, @NonNull VideoRequestDTO video) {
        return videoRepository.findById(id)
                .flatMap(videoToUpdate -> {
                    videoToUpdate.setTitle(video.getTitle());
                    videoToUpdate.setDescription(video.getDescription());
                    videoToUpdate.setDuration(video.getDuration());
                    videoToUpdate.setCategory(video.getCategory());
                    videoToUpdate.setTags(video.getTags());
                    return videoRepository.save(videoToUpdate);
                });
    }

    public Mono<Video> save(VideoRequestDTO video) {
        Video videoToSave = new Video();
        videoToSave.setTitle(video.getTitle());
        videoToSave.setDescription(video.getDescription());
        videoToSave.setUrl("http://localhost:8080/api/videos/" + video.getId() + "/play");
        videoToSave.setThumbnail("http://localhost:8080/api/videos/" + video.getId() + "/thumbnail");
        videoToSave.setDuration(video.getDuration());
        videoToSave.setCategory(video.getCategory());
        videoToSave.setTags(video.getTags());
        videoToSave.setPublicationDate(video.getPublicationDate());
        videoToSave.setLikes(0);
        videoToSave.setViews(0);
        videoToSave.setLikedBy(video.getLikedBy());
        videoToSave.setViewedBy(video.getViewedBy());

        return videoRepository.save(videoToSave);
    }

    public Mono<Void> deleteById(@NonNull String id) {
        return videoRepository.deleteById(id);
    }

}
