create database tikklemoa default character set utf8mb4 default collate utf8mb4_general_ci;
use tikklemoa;

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(20) NOT NULL,
    userid VARCHAR(20) NOT NULL,
    userpw TEXT NOT NULL,
    img TEXT NOT NULL,
    badge VARCHAR(20) NOT NULL
);

CREATE TABLE board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(30) NOT NULL,
    date DATE NOT NULL,
    content TEXT NOT NULL,
    img TEXT,
    userid BIGINT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (userid) REFERENCES user(id)
);

CREATE TABLE calendar (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    category ENUM('PLUS', 'MINUS') NOT NULL,
    subcategory VARCHAR(20) NOT NULL,
    price INT NOT NULL,
    details VARCHAR(100),
    userid BIGINT NOT NULL,
    CONSTRAINT fk_calendar_user FOREIGN KEY (userid) REFERENCES user(id)
);

CREATE TABLE comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    content VARCHAR(100) NOT NULL,
    userid BIGINT NOT NULL,
    boardid BIGINT NOT NULL,
    CONSTRAINT fk_comment_user FOREIGN KEY (userid) REFERENCES user(id),
    CONSTRAINT fk_comment_board FOREIGN KEY (boardid) REFERENCES board(id)
);

CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userid BIGINT NOT NULL,
    boardid BIGINT NOT NULL,
    CONSTRAINT fk_likes_user FOREIGN KEY (userid) REFERENCES user(id),
    CONSTRAINT fk_likes_board FOREIGN KEY (boardid) REFERENCES board(id)
);

CREATE TABLE post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(30) NOT NULL,
    date DATE NOT NULL,
    content TEXT NOT NULL,
    img TEXT,
    isRead BOOLEAN NOT NULL DEFAULT FALSE,
    senderDel BOOLEAN NOT NULL DEFAULT FALSE,
    recipientDel BOOLEAN NOT NULL DEFAULT FALSE,
    senderid BIGINT NOT NULL,
    recipientid BIGINT NOT NULL,
    CONSTRAINT fk_post_sender FOREIGN KEY (senderid) REFERENCES user(id),
    CONSTRAINT fk_post_recipient FOREIGN KEY (recipientid) REFERENCES user(id)
);

CREATE TABLE setting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    theme VARCHAR(20) NOT NULL,
    Lone INT NOT NULL,
    Ltwo INT NOT NULL,
    Lthree INT NOT NULL,
    priceView BOOLEAN NOT NULL DEFAULT FALSE,
    userid BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_setting_user FOREIGN KEY (userid) REFERENCES user(id)
);