DROP DATABASE IF EXISTS MRDb;
CREATE DATABASE MRDb;
USE MRDb;

DROP TABLE IF EXISTS TITLEINFO;
CREATE TABLE TITLEINFO
(
  tconst VARCHAR(30),
  title VARCHAR(30) NOT NULL,
  year INT NOT NULL,
  runtimeMinutes INT,
  genre1 VARCHAR(30),
  genre2 VARCHAR(30),
  genre3 VARCHAR(30),
  PRIMARY KEY (tconst)
);

DROP TABLE IF EXISTS TITLERATING;
CREATE TABLE TITLERATING
(
  tconst VARCHAR(30),
  averageRating DOUBLE DEFAULT 0,
  numVotes INT DEFAULT 0,
  PRIMARY KEY(tconst),
  FOREIGN KEY(tconst) REFERENCES TITLEINFO(tconst) on delete cascade
);

DROP TABLE IF EXISTS USERS;
CREATE TABLE USERS
(
  uID INT AUTO_INCREMENT,
  name VARCHAR(30),
  username VARCHAR(30) NOT NULL,
  pass VARCHAR(30) NOT NULL,
  admin BOOLEAN DEFAULT FALSE,
  lastLoggedIn timestamp DEFAULT NULL,
  updatedOn timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP on update current_timestamp,
  PRIMARY KEY(uID),
  UNIQUE KEY(username)
);
ALTER TABLE USERS AUTO_INCREMENT = 1001;

DROP TABLE IF EXISTS ARCHIVEDUSERS;
CREATE TABLE ARCHIVEDUSERS
(
  uID INT,
  name VARCHAR(30),
  username VARCHAR(30),
  pass VARCHAR(30),
  admin BOOLEAN,
  lastLoggedIn timestamp,
  updatedOn timestamp,
  PRIMARY KEY(uID),
  UNIQUE KEY(username)
);

DROP TABLE IF EXISTS TITLEREVIEWS;
CREATE TABLE TITLEREVIEWS
(
  rID INT AUTO_INCREMENT,
  uID INT,
  tconst VARCHAR(30),
  review VARCHAR(1000),
  stars INT CHECK(stars>0 AND stars<=10),
  PRIMARY KEY(rID),
  UNIQUE KEY (uID, tconst),
  FOREIGN KEY(tconst) REFERENCES TITLEINFO(tconst) on delete cascade,
  FOREIGN KEY(uID) REFERENCES USERS(uID) on delete cascade
);
ALTER TABLE TITLEREVIEWS AUTO_INCREMENT = 2001;

DROP TABLE IF EXISTS COMMENTREVIEWS;
CREATE TABLE COMMENTREVIEWS
(
  cID INT AUTO_INCREMENT,
  uID INT,
  rID INT,
  comment VARCHAR(1000),
  helpful INT CHECK(helpful=1 OR helpful=-1),
  PRIMARY KEY(cID),
  UNIQUE KEY(uID, rID),
  FOREIGN KEY(uID) REFERENCES USERS(uID) on delete cascade,
  FOREIGN KEY(rID) REFERENCES TITLEREVIEWS(rID) on delete cascade
);
ALTER TABLE COMMENTREVIEWS AUTO_INCREMENT = 3001;

DROP TRIGGER IF EXISTS insertReview;
DELIMITER //
CREATE TRIGGER insertReview
AFTER INSERT ON TitleReviews
FOR EACH ROW
BEGIN
  IF (NEW.stars < 1 OR NEW.stars > 10) THEN 
    UPDATE `Error: invalid stars value` SET x=1;
  END IF;
  UPDATE TitleRating
  SET averageRating = ROUND((numVotes * averageRating + NEW.stars) / (numVotes + 1), 1), numVotes = numVotes + 1
  WHERE tconst = NEW.tconst;
END //
DELIMITER ;

DROP TRIGGER IF EXISTS deleteReview;
DELIMITER //
CREATE TRIGGER deleteReview
AFTER DELETE ON TitleReviews
FOR EACH ROW
BEGIN
  UPDATE TitleRating
  SET averageRating = ROUND((numVotes * averageRating - OLD.stars) / (numVotes - 1), 1), numVotes = numVotes - 1
  WHERE tconst = OLD.tconst;
END //
DELIMITER ;

DROP TRIGGER IF EXISTS updateReview;
DELIMITER //
CREATE TRIGGER updateReview
AFTER UPDATE ON TitleReviews
FOR EACH ROW
BEGIN
  IF(NEW.stars < 1 OR NEW.stars > 10) THEN
    UPDATE `Error: invalid stars value` SET x=1;
  END IF;
  UPDATE TitleRating
  SET averageRating = (numVotes * averageRating - OLD.stars) / (numVotes - 1), numVotes = numVotes - 1
  WHERE tconst = OLD.tconst;
  UPDATE TitleRating
  SET averageRating = ROUND((numVotes * averageRating + NEW.stars) / (numVotes + 1), 1), numVotes = numVotes + 1 
  WHERE tconst = NEW.tconst;
END //
DELIMITER ;

DROP TRIGGER IF EXISTS insertComment;
DELIMITER //
CREATE TRIGGER insertComment
AFTER INSERT ON CommentReviews
FOR EACH ROW
BEGIN
  IF(NEW.helpful <> 1 AND NEW.helpful <> -1) THEN
    UPDATE `Error: invalid helpful value` SET x=1;
  END IF;
END //
DELIMITER ;

DROP TRIGGER IF EXISTS updateComment;
DELIMITER //
CREATE TRIGGER updateComment
AFTER UPDATE ON CommentReviews
FOR EACH ROW
BEGIN
  IF(NEW.helpful <> 1 AND NEW.helpful <> -1) THEN
        UPDATE `Error: invalid helpful value` SET x=1;
  END IF;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS archiveUsers;
DELIMITER //
CREATE PROCEDURE archiveUsers(IN cutoffYear INT)
BEGIN
  INSERT INTO archivedUsers
  SELECT * 
  FROM Users
  WHERE YEAR(updatedOn) <= cutoffYear;
  DELETE FROM Users
  WHERE YEAR(updatedOn) <= cutOffYear;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS userLogsIn;
DELIMITER //
CREATE PROCEDURE userLogsIn(IN userID INT)
BEGIN
  UPDATE Users
  SET lastLoggedIn = NOW()
  WHERE uID = userID;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS deleteReview;
DELIMITER //
CREATE PROCEDURE deleteReview(IN reviewID INT)
BEGIN
  DELETE FROM TitleReviews
  WHERE rID = reviewID;
END //
DELIMITER ;
