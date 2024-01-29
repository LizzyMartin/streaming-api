package br.com.fiap.streaming.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "videos")
@NoArgsConstructor
public class Video {
    
    @Id
    private String id;
    private String title;
    private String description;
    private String url;
    private String publicationDate;
    private String thumbnail;
    private String duration;
    private Integer views;
    private Integer likes;
    private Set<String> likedBy = new HashSet<>();
    private String category;
    private Set<String> tags = new HashSet<>();
    private Set<String> viewedBy = new HashSet<>();

}
