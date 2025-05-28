package muri.memdumpbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "profile")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String fullName;

    private String description;

    @Column(columnDefinition = "BYTEA")
    private byte[] profilePicture;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "profile_preferred_tags",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "preferred_tags_id")
    )
    @Builder.Default
    private List<Tag> preferredTags = new ArrayList<>();
}
