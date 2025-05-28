package muri.memdumpbackend.service;

import lombok.AllArgsConstructor;
import muri.memdumpbackend.dto.flask.StatisticsResponse;
import muri.memdumpbackend.dto.post.*;
import muri.memdumpbackend.exception.*;
import muri.memdumpbackend.flaskservice.FlaskEndpoint;
import muri.memdumpbackend.model.*;
import muri.memdumpbackend.model.filter.PostFilter;
import muri.memdumpbackend.repo.CommentRepository;
import muri.memdumpbackend.repo.PostRepository;
import muri.memdumpbackend.repo.UserRepository;
import muri.memdumpbackend.repo.VoteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final RestClient restClient;
    private final TagService tagService;


    public void createPost(PostCreateDTO postCreateDTO) {
        String loggedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!loggedUsername.equalsIgnoreCase(postCreateDTO.getAuthor())) {
            throw new CreationException("You can create a post only for you");
        }
        User user = userRepository.findByUsername(postCreateDTO.getAuthor())
                .orElseThrow(() -> new CreationException("User " + postCreateDTO + " not found"));

        List<Tag> tags = tagService.convertToListOfTags(postCreateDTO.getTags());
        List<ProgrammingLanguagePercentage> programmingLanguagePercentages =
                getProgrammingLanguagePercentages(postCreateDTO.getContent());

        Post post = Post.builder()
                .author(user)
                .title(postCreateDTO.getTitle())
                .content(postCreateDTO.getContent())
                .created(LocalDateTime.now())
                .tags(tags)
                .programmingLanguages(programmingLanguagePercentages)
                .score(0)
                .build();
        postRepository.save(post);

    }

    public GetPostByIdResponse getPostById(String id) {
        Post post = postRepository.findById(UUID.fromString(id))
                .orElseThrow(()-> new NotFoundException("Post with id " + id + " not found"));
        String html;
        try {
            html = restClient.post()
                    .uri(FlaskEndpoint.CONVERT_TO_HTML_ENDPOINT)
                    .body(post.getContent())
                    .retrieve()
                    .body(String.class);

        } catch (RestClientException ex) {
            throw new ParsingException("Could not parse markdown");
        }
        if (html == null) throw new ParsingException("Could not parse markdown");
        List<String> tagNames = tagService.convertToListOfStringTags(post.getTags());

        return GetPostByIdResponse.builder()
                .id(post.getId().toString())
                .author(post.getAuthor().getUsername())
                .title(post.getTitle())
                .content(html)
                .created(post.getCreated())
                .tags(tagNames)
                .score(post.getScore())
                .programmingLanguagePercentages(post.getProgrammingLanguages())
                .build();
    }

    public List<GetPostMetadataResponse> getAllPosts(String id, String sort, List<String> tags, Integer limit) {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .filter(PostFilter.filterById(id))
                .filter(PostFilter.filterByTags(tags))
                .sorted(PostFilter.sortByCreated(sort))
                .limit(PostFilter.setLimit(limit,  posts.size()))
                .map(post -> GetPostMetadataResponse.builder()
                        .id(post.getId().toString())
                        .author(post.getAuthor().getUsername())
                        .title(post.getTitle())
                        .created(post.getCreated())
                        .score(post.getScore())
                        .tags(tagService.convertToListOfStringTags(post.getTags()))
                        .build()
                )
                .collect(Collectors.toList());
    }

    public int vote(VoteRequest voteRequest) {
        String loggedUsername = getLoggedUsername();
        if (!loggedUsername.equalsIgnoreCase(voteRequest.getUsername())) {
            throw new VoteException("You can vote only in your name");
        }
        User user = userRepository.findByUsername(voteRequest.getUsername())
                .orElseThrow(() -> new VoteException("User " + voteRequest.getUsername() + " not found"));
        Post post = postRepository.findById(UUID.fromString(voteRequest.getPostId()))
                .orElseThrow(() -> new VoteException("Post " + voteRequest.getPostId() + " not found"));
        Vote existingVote = voteRepository.findByAuthorAndPost(user, post).orElse(null);
        if (existingVote != null) {
            VoteType voteType = existingVote.getVoteType();
            if (voteType == VoteType.UPVOTE) {
                post.decrementScore();
            } else if (voteType == VoteType.DOWNVOTE) {
                post.incrementScore();
            }
            if (voteRequest.getVoteType() == VoteType.UPVOTE) {
                post.incrementScore();
                existingVote.setVoteType(VoteType.UPVOTE);
            } else if (voteRequest.getVoteType() == VoteType.DOWNVOTE) {
                post.decrementScore();
                existingVote.setVoteType(VoteType.DOWNVOTE);
            }
            voteRepository.save(existingVote);
            postRepository.save(post);
            return post.getScore();
        }

        if (voteRequest.getVoteType() == VoteType.UPVOTE) {
            post.incrementScore();
        } else if (voteRequest.getVoteType() == VoteType.DOWNVOTE) {
            post.decrementScore();
        }
        Vote vote = Vote.builder()
                .voteType(voteRequest.getVoteType())
                .author(user)
                .post(post)
                .build();
        postRepository.save(post);
        voteRepository.save(vote);
        return post.getScore();
    }

    public void comment(CommentRequest commentRequest) {
        String loggedUsername = getLoggedUsername();
        if (!loggedUsername.equalsIgnoreCase(commentRequest.getUsername())) {
            throw new CommentException("You can comment only in your name");
        }
        User user = userRepository.findByUsername(commentRequest.getUsername())
                .orElseThrow(() -> new CommentException("User " + commentRequest.getUsername() + " not found"));
        Post post = postRepository.findById(UUID.fromString(commentRequest.getPostId()))
                .orElseThrow(() -> new CommentException("Post " + commentRequest.getPostId() + " not found"));
//        commentRepository.findByAuthorAndPost(user, post)
//                .ifPresent((c) -> {throw new CommentException("Comment " + commentRequest.getPostId() + " already exists");});
        Comment comment = Comment.builder()
                .author(user)
                .post(post)
                .content(commentRequest.getContent())
                .build();
        commentRepository.save(comment);
    }

    private String getLoggedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    private List<ProgrammingLanguagePercentage> getProgrammingLanguagePercentages(String content) {
        StatisticsResponse response;
        try {
            response = restClient.post()
                    .uri(FlaskEndpoint.GET_STATISTICS_ENDPOINT)
                    .body(content)
                    .retrieve()
                    .body(StatisticsResponse.class);
        } catch (RestClientException ex) {
            throw new CreationException(ex.getMessage());
        }
        if (response == null || response.getStatistics() == null) {
            throw new CreationException("No statistics found");
        }
        return response.getStatistics().stream()
                .map(list -> new ProgrammingLanguagePercentage(list.getFirst(), list.get(1)))
                .collect(Collectors.toList());
    }
}
