package ru.vladimirt.domain.util;

import ru.vladimirt.domain.User;

public abstract class MessageHelper {
    public static String getAuthorName(User author){
        return author != null? author.getUsername() : "<none>";
    }
}

