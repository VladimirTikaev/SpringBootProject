package ru.vladimirt.domain.dto;

import ru.vladimirt.domain.Message;
import ru.vladimirt.domain.User;
import ru.vladimirt.domain.util.MessageHelper;

import java.util.HashSet;
import java.util.Set;

public class MessageDto {
    private Long id;
    private String text;
    private String tag;
    private User author;
    private String fileName;
    private Long likes;
    private Boolean meLiked;

    public MessageDto(Message message, Long likes, Boolean meLiked) {
        this.id = message.getId();
        this.text = message.getText();
        this.tag = message.getTag();
        this.author = message.getAuthor();
        this.fileName = message.getFileName();
        this.likes = likes;
        this.meLiked = meLiked;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getTag() {
        return tag;
    }

    public User getAuthor() {
        return author;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getLikes() {
        return likes;
    }

    public Boolean getMeLiked() {
        return meLiked;
    }

    public String getAuthorName(){
        return MessageHelper.getAuthorName(author);
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + id +
                ", author=" + author +
                ", likes=" + likes +
                ", meLiked=" + meLiked +
                '}';
    }
}
