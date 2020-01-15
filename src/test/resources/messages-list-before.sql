delete from message;
delete from hibernate_sequence;

insert into message(id, text, tag, user_id) values
    (1, 'first message', 'my tag', 1),
    (2, 'second message', 'new tag', 1),
    (3, 'third message', 'my tag', 1),
    (4, 'last message', 'last tag', 1);

insert into hibernate_sequence values (10);