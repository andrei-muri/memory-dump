package muri.memdumpbackend.model.filter;


import muri.memdumpbackend.model.Post;
import muri.memdumpbackend.model.Tag;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class PostFilter {
    public static Predicate<Post> filterById(String postId) {
        return post -> Optional.ofNullable(postId)
                .filter(id -> !id.isBlank())
                .map(id -> post.getAuthor().getId().equals(UUID.fromString(id)))
                .orElse(true);
    }

    public static Predicate<Post> filterByTags(List<String> tags) {
        return post -> Optional.ofNullable(tags)
                .filter(t -> !t.isEmpty())
                .map(t -> post.getTags().stream()
                        .map(Tag::getName)
                        .anyMatch(t::contains))
                .orElse(true);
    }

    public static Comparator<Post> sortByCreated(String sort) {
        return "created".equalsIgnoreCase(sort) ? Comparator.comparing(Post::getCreated).reversed() : Comparator.comparing(Post::getCreated);
    }

    public static Integer setLimit(Integer limit, Integer size) {
        return limit == null ? size : limit;
    }
}
