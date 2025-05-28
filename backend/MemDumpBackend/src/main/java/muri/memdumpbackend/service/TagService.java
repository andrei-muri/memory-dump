package muri.memdumpbackend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import muri.memdumpbackend.dto.tag.TagCreateDTO;
import muri.memdumpbackend.exception.CreationException;
import muri.memdumpbackend.model.Tag;
import muri.memdumpbackend.repo.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public void createTag(TagCreateDTO tagCreateDTO) {
        tagRepository.findTagByName(tagCreateDTO.getTagName())
                .ifPresent(tag -> {throw new CreationException("Tag already exists");});
        Tag tag = Tag.builder()
                .name(tagCreateDTO.getTagName())
                .build();
        tagRepository.save(tag);
    }

    public List<Tag> convertToListOfTags(List<String> stringTags) {
        return stringTags.stream()
                .map(str -> tagRepository.findTagByName(str).orElseThrow(() -> new CreationException("Tag name is invalid")))
                .collect(Collectors.toList());
    }

    public List<String> convertToListOfStringTags(List<Tag> stringTags) {
        return stringTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    public List<Tag> getTags() {
        return tagRepository.findAll();
    }
}
