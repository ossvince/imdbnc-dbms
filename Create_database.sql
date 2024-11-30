CREATE TABLE Person(
    personID INT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    age INT,
    birthYear INT,
    deathYear INT
);

CREATE TABLE Title(
    titleID INT NOT NULL PRIMARY KEY,
    primaryTitle TEXT NOT NULL,
    originalTitle TEXT NOT NULL,
    startYear INT,
    runTime INT CHECK(runTime>0),
    isAdult BIT NOT NULL
);

CREATE TABLE Profession(
    professionName VARCHAR(150) PRIMARY KEY
);

CREATE TABLE HasProfession(
    personID INT REFERENCES Person ON DELETE CASCADE,
    professionName VARCHAR(150) FOREIGN KEY REFERENCES Profession ON DELETE CASCADE,
    PRIMARY KEY (personID, professionName)
);

CREATE TABLE AssociatedWith(
    personID INT FOREIGN KEY REFERENCES Person ON DELETE CASCADE,
    titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,
    flag VARCHAR(20),
    CHECK (flag in ('KnownFor', 'Directed', 'Wrote')),
    PRIMARY KEY (personID, titleID, flag)
);

CREATE TABLE WorksOn(
    personID INT FOREIGN KEY REFERENCES Person ON DELETE CASCADE,
    titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,
    jobCategory VARCHAR(200) NOT NULL,
    jobName VARCHAR(200),
    PRIMARY KEY (personID, titleID, jobCategory, jobName) 
);

CREATE TABLE Directs(
    personID INT FOREIGN KEY REFERENCES Person ON DELETE CASCADE,
    titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,
    PRIMARY KEY (personID, titleID) 
);

CREATE TABLE Writes(
    personID INT FOREIGN KEY REFERENCES Person ON DELETE CASCADE,
    titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,
    PRIMARY KEY (personID, titleID) 
);

CREATE TABLE ActsIn(
    personID INT FOREIGN KEY REFERENCES Person ON DELETE CASCADE,
    titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,
    characterPlayed VARCHAR(200),
    PRIMARY KEY (personID, titleID, characterPlayed)
);

CREATE TABLE KnownFor(
    personID INT  FOREIGN KEY REFERENCES Person ON DELETE CASCADE,
    titleID INT  FOREIGN KEY REFERENCES Title ON DELETE CASCADE,
    PRIMARY KEY (personID, titleID) 
);

CREATE TABLE TitleRating(
    titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,
    avgRating NUMERIC(3,1) NOT NULL,
    numVotes INT NOT NULL,
    PRIMARY KEY (titleID, avgRating,numVotes)
);

CREATE TABLE TVSeries(
    seriesID INT PRIMARY KEY,
    titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE
);

CREATE TABLE TVEpisode(
    episodeID INT IDENTITY(1, 1),
    titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,
    seriesID INT FOREIGN KEY REFERENCES TVSeries,
    seasonNum INT,
    episodeNum INT,
    PRIMARY KEY (episodeID, titleID, seriesID)
);

CREATE TABLE Movie(
    movieID INT IDENTITY(1, 1),
    titleID INT FOREIGN KEY REFERENCES Title ON DELETE CASCADE,
    PRIMARY KEY (titleID, movieID),
);