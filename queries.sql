/* topTenMovies */
SELECT m.primaryTitle, tr.avgRating, tr.numVotes
FROM Title AS t
JOIN TitleRating AS tr ON t.titleID = tr.titleID
JOIN Movie AS m ON m.titleID = tr.titleID
WHERE tr.numVotes >= 5000
ORDER BY tr.avgRating DESC
LIMIT 10;
/* topTenActors */
SELECT p.name, COUNT(a.titleID) AS credits
FROM Person AS p
JOIN ActsIn AS a ON a.personID = p.personID
ORDER BY credits DESC
LIMIT 10;
/* directedTitles */
SELECT t.primaryTitle
FROM Title AS t
JOIN AssociatedWith AS aw ON aw.titleID = t.titleID
JOIN Person AS p ON aw.personID = p.personID
WHERE p.personID = ? AND aw.flag = ‘Directed’
ORDER BY t.primaryTitle ASC;
/* movieAssociates */
SELECT DISTINCT p.personID, p.name
FROM Person AS p
JOIN AssociatedWith AS aw ON p.personID = aw.personID
JOIN Title AS t ON aw.titleID = t.titleID
JOIN Movie AS m ON t.titleID = m.titleID
WHERE m.titleID = ?;
/* titlesIn */
SELECT primaryTitle, originalTitle, titleID
FROM Person
JOIN AssociatedWith ON Person.personID=AssociatedWith.personID
JOIN Title ON AssociatedWith.titleID=Title.titleID
WHERE flag != ‘KnownFor’ AND Person.personID = ?
UNION
SELECT primaryTitle, originalTitle, titleID
FROM Person JOIN WorksOn ON Person.personID=WorksOn.personID
JOIN Title ON WorksOn.titleID=Title.titleID WHERE Person.personID = ?
UNION
SELECT primaryTitle, originalTitle, titleID
FROM Person
JOIN ActsIn ON Person.personID=ActsIn.personID
JOIN Title ON ActsIn.titleID=Title.titleID WHERE Person.personID = ?;
/* moviesKnownFor */
SELECT t.primaryTitle, t.originalTitle
FROM Movie
JOIN Title AS t ON Movie.titleID = t.titleID
JOIN AssociatedWith AS aw ON t.titleID = aw.titleID
JOIN Person AS p ON aw.personID = p.personID
WHERE aw.flag = ‘KnownFor’ AND p.personID= ?;
/* seriesKnownFor */
SELECT t.primaryTitle, t.originalTitle
FROM TVSeries
JOIN Title AS t ON TVSeries.titleID = t.titleID
JOIN AssociatedWith AS aw ON t.titleID = aw.titleID
JOIN Person AS p ON aw.personID = p.personID
WHERE aw.flag = ‘KnownFor’ AND p.personID= ?;
/* findPerson */
SELECT name, personID, age, birthYear, deathYear FROM Person WHERE name LIKE = ?;
/* findTitle */
SELECT primaryTitle, originalTitle, titleID, runTime, startYear, isAdult 
FROM Title WHERE primaryTitle LIKE ? OR originalTitle LIKE ?;
/* getRatings */
SELECT avgRating, numVotes FROM Title
JOIN TitleRating ON Title.titleID=TitleRating.titleID
WHERE Title.titleID=?;
/* getProfessionals */
SELECT name, personID FROM Profession
JOIN HasProfession ON Profession.professionName=HasProfession.professionName
JOIN Person ON HasProfession.personID=Person.personID
WHERE Profession.professionName = ?;
/* listSeriesEpisodes */
SELECT t.*, e.episodeID FROM TVSeries AS s
JOIN EpisodeOf AS epof ON s.seriesID = epof.seriesID
JOIN TVEpisode AS e ON epof.episodeID = e.episodeID
JOIN Title AS t ON e.titleID = t.titleID
WHERE s.seriesID = ?;
/* seriesMainCast */
WITH allEpisodes as (SELECT * AS episodes
FROM TVSeries AS s
JOIN EpisodeOF AS epof ON s.seriesID = epof.seriesID
JOIN TVEpisode as e ON epof.episodeID = e.episodeID
WHERE s.seriesID = ?)
SELECT p.* FROM People AS p
JOIN ActsIn ON p.peopleID = ActsIn.peopleID
JOIN TVEpisode ON ActsIn.titleID = TVEpisode.titleID
JOIN EpisodeOf ON TVEpisode.episodeID = EpisodeOf.episodeID
WHERE EpisodeOF.seriesID = ?
GROUP BY p.peopleID
HAVING count(*) = (SELECT count(*) FROM allEpisodes);
/* listCastAndRoles */
SELECT p.name, p.personID, ActsIn.character FROM Title
JOIN ActsIn ON Title.titleID = ActsIn.titleID
JOIN People AS p ON ActsIn.personID = p.personID
WHERE Title.titleID = ?;