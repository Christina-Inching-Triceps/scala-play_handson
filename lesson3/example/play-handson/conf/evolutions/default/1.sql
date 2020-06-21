-- Tweet schema

-- !Ups
CREATE TABLE tweet (
    id         BIGINT(20)    NOT NULL AUTO_INCREMENT,
    content    VARCHAR(120)  NOT NULL,
    posted_at  DATETIME      NOT NULL,
    created_at TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
);

-- sample data
INSERT INTO tweet(id, content, posted_at) VALUES
(1, 'tweet1', '2020-03-15 13:15:00'),
(2, 'tweet2', '2020-03-15 14:15:00'),
(3, 'tweet3', '2020-03-15 15:15:00'),
(4, 'tweet4', '2020-03-15 16:15:00'),
(5, 'tweet5', '2020-03-15 17:15:00');

-- !Downs
DROP TABLE tweet;

