-- 
-- SQL script that will be used to initialize the database
-- Note that the syntax is very picky. A reference is available at
--   http://www.hsqldb.org/doc/guide/sqlgeneral-chapt.html
-- 
-- When writing INSERT statements, the order must be exactly as found in
-- the server logs (search for "create table user"), or as 
-- specified within (creation-compatible) parenthesis:
--     INSERT INTO user(id,enabled,login,password,roles) values (...)
-- vs
--     INSERT INTO user VALUES (...)
-- You can find the expected order by inspecting the output of Hibernate
-- in your server logs (assuming you use Spring + JPA)
--

-- On passwords:
--
-- valid bcrypt-iterated, salted-and-hashed passwords can be generated via
-- https://www.dailycred.com/article/bcrypt-calculator
-- (or any other implementation) and prepending the text '{bcrypt}'
--
-- Note that every time that you generate a bcrypt password with a given 
-- password you will get a different result, because the first characters
-- after the third $ correspond to a random salt that will be different each time.
--
-- a few simple examples:
-- {bcrypt}$2a$04$2ao4NQnJbq3Z6UeGGv24a.wRRX0FGq2l5gcy2Pjd/83ps7YaBXk9C == 'a'
-- {bcrypt}$2a$04$5v02dQ.kxt7B5tJIA4gh3u/JFQlxmoCadSnk76PnvoN35Oz.ge3GK == 'p'
-- {bcrypt}$2a$04$9rrSETFYL/gqiBxBCy3DMOIZ6qmLigzjqnOGbsNji/bt65q.YBfjK == 'q'

-- USERS

INSERT INTO USER
("ACTIVE", "DESCRIPTION", "EMAIL", "LAST_NAME", "NAME", "NICKNAME", "PASSWORD", "BIRTHDATE", "ROLES", "AVATAR")
VALUES ( 1, '', 'flopezcarr@ucm.es', 'Lopez', 'Fernando', 'BadmintonNoob', '{bcrypt}$2a$04$2ao4NQnJbq3Z6UeGGv24a.wRRX0FGq2l5gcy2Pjd/83ps7YaBXk9C', TO_DATE('1996-09-06', 'YYYY-MM-DD'), 'user', '/img/avatar-1.png');

INSERT INTO USER
("ACTIVE", "DESCRIPTION", "EMAIL", "LAST_NAME", "NAME", "NICKNAME", "PASSWORD", "BIRTHDATE", "ROLES", "AVATAR")
VALUES ( 1, '', 'guicor@ucm.es', 'Cortina', 'Guillermo', 'ChessNoob', '{bcrypt}$2a$04$2ao4NQnJbq3Z6UeGGv24a.wRRX0FGq2l5gcy2Pjd/83ps7YaBXk9C', TO_DATE('1996-09-06', 'YYYY-MM-DD'), 'user', '/img/avatar-3.png');

INSERT INTO USER
("ACTIVE", "DESCRIPTION", "EMAIL", "LAST_NAME", "NAME", "NICKNAME", "PASSWORD", "BIRTHDATE", "ROLES", "AVATAR")
VALUES ( 1, '', 'semart12@ucm.es', 'Martin', 'Sergio', 'ChessMaister', '{bcrypt}$2a$04$2ao4NQnJbq3Z6UeGGv24a.wRRX0FGq2l5gcy2Pjd/83ps7YaBXk9C', TO_DATE('1996-09-06', 'YYYY-MM-DD'), 'user', '/img/avatar-1.png');

INSERT INTO USER
("ACTIVE", "DESCRIPTION", "EMAIL", "LAST_NAME", "NAME", "NICKNAME", "PASSWORD", "BIRTHDATE", "ROLES", "AVATAR")
VALUES ( 1, '', 'imart02@ucm.es', 'Martin', 'Irene', 'Darko', '{bcrypt}$2a$04$2ao4NQnJbq3Z6UeGGv24a.wRRX0FGq2l5gcy2Pjd/83ps7YaBXk9C', TO_DATE('1996-09-06', 'YYYY-MM-DD'), 'user', '/img/avatar-2.png');


INSERT INTO USER
("ACTIVE", "DESCRIPTION", "EMAIL", "LAST_NAME", "NAME", "NICKNAME", "PASSWORD", "ROLES", "AVATAR")
VALUES ( 1, '', 'admin@admin.es', 'Admin', 'Admin', 'admin', '{bcrypt}$2a$04$2ao4NQnJbq3Z6UeGGv24a.wRRX0FGq2l5gcy2Pjd/83ps7YaBXk9C', 'admin', '/img/admin.png');


-- GROUPS

INSERT INTO CGroup
("NAME")
VALUES ('Prueba');


-- TAGS

INSERT INTO TAG
("COLOR", "NAME", "PARENT_ID", "USER_ID", "PLAYLIST")
VALUES ('#3AF132', 'Video', null, 1, false);

INSERT INTO TAG
("COLOR", "NAME", "PARENT_ID", "USER_ID", "PLAYLIST")
VALUES ('#9FBFFF', 'Music', null, 1, false);

INSERT INTO TAG
("COLOR", "NAME", "PARENT_ID", "USER_ID", "PLAYLIST")
VALUES ('#8CDE45', 'Pictures', null, 1, false);

INSERT INTO TAG
("COLOR", "NAME", "PARENT_ID", "USER_ID", "PLAYLIST")
VALUES ('#F60045', 'Home Videos', 1, 1, false);

INSERT INTO TAG
("COLOR", "NAME", "PARENT_ID", "USER_ID", "PLAYLIST")
VALUES ('#3AF132', 'Video', null, 2, false);

INSERT INTO TAG
("COLOR", "NAME", "PARENT_ID", "USER_ID", "PLAYLIST")
VALUES ('#F60045', 'Music', null, 2, false);

INSERT INTO TAG
("COLOR", "NAME", "PARENT_ID", "USER_ID", "PLAYLIST")
VALUES ('#8CDE45', 'Pictures', null, 2, false);

INSERT INTO TAG
("COLOR", "NAME", "PARENT_ID", "USER_ID", "PLAYLIST")
VALUES ('#9FBFFF', 'Movies', null, 2, false);


-- FRIEND

INSERT INTO FRIEND
("ACCEPTED", "MESSAGE", "FIRST_USER_ID", "SECOND_USER_ID" )
VALUES (1, '', 1, 2);

INSERT INTO FRIEND
("ACCEPTED", "MESSAGE", "FIRST_USER_ID", "SECOND_USER_ID" )
VALUES (0, '', 3, 1);
