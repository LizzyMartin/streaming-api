package br.com.fiap.streaming.dto;

import java.util.Set;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoRequestDTO {
    
    @JsonIgnore
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String url;

    private String publicationDate = LocalDateTime.now().toString();
    private String thumbnail;

    @NotBlank
    private String duration;

    private Integer views = 0;
    private Integer likes = 0;

    @NotBlank
    private String category;
    private Set<String> tags = new HashSet<>();
    
    @JsonIgnore
    private Set<String> likedBy = new HashSet<>();

    @JsonIgnore
    private Set<String> viewedBy = new HashSet<>();

}
