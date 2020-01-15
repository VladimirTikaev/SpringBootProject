delete from user_role;
delete from usr;

insert into usr(id, active, password, username) values
    (1, true, '{aSEoCOSzkbM0QGCAFm6AZOIiD2iDlUozyEdzVfmKnyc=}105ffdd36397197d5e4bbb039b9dae33', 'TestUser'),
    (2, true, '{+0WJdgdheaRSRBjEUPAaPNiVwb7nYa2pVgakli+eBG0=}7f2149c7f27bfe28dcffb73b44d2516a', 'SomeUser');

insert into user_role(user_id, roles) values
    (1, 'USER'), (1, 'ADMIN'),
    (2, 'USER');
