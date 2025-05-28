package muri.memdumpbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "post")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime created;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tags_id")
    )
    private List<Tag> tags;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "post_programming_percentage",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Builder.Default
    private List<ProgrammingLanguagePercentage> programmingLanguages = new ArrayList<>();

    private Integer score;

    public void incrementScore() {
        this.score++;
    }

    public void decrementScore() {
        this.score--;
    }
}
