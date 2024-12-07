/* topTenMovies */
SELECT TOP 10 t.primaryTitle, tr.avgRating, tr.numVotes
FROM Title AS t
JOIN TitleRating AS tr ON t.titleID = tr.titleID
JOIN Movie AS m ON m.titleID = t.titleID
ORDER BY tr.avgRating DESC, tr.numVotes DESC;
/* topTenActors */ 
SELECT TOP 10 p.name, COUNT(a.titleID) AS credits
FROM Person AS p
JOIN ActsIn AS a ON a.personID = p.personID
GROUP BY p.personID, p.name
ORDER BY credits DESC;
/* directedTitles */
SELECT t.primaryTitle
FROM Title AS t
JOIN AssociatedWith AS aw ON aw.titleID = t.titleID
JOIN Person AS p ON aw.personID = p.personID
WHERE p.name like ? AND aw.flag = 'Directed'
ORDER BY t.primaryTitle;
/* movieAssociates */
SELECT DISTINCT p.personID, p.name
FROM Person AS p
JOIN AssociatedWith AS aw ON p.personID = aw.personID
JOIN Title AS t ON aw.titleID = t.titleID
JOIN Movie AS m ON t.titleID = m.titleID
WHERE m.titleID = ?;
/* titlesIn */
SELECT primaryTitle, originalTitle, Title.titleID
FROM Person
JOIN AssociatedWith ON Person.personID=AssociatedWith.personID
JOIN Title ON AssociatedWith.titleID=Title.titleID
WHERE flag != 'KnownFor' AND Person.personID = ?
UNION
SELECT primaryTitle, originalTitle, Title.titleID
FROM Person JOIN WorksOn ON Person.personID=WorksOn.personID
JOIN Title ON WorksOn.titleID=Title.titleID WHERE Person.personID = ?
UNION
SELECT primaryTitle, originalTitle, Title.titleID
FROM Person
JOIN ActsIn ON Person.personID=ActsIn.personID
JOIN Title ON ActsIn.titleID=Title.titleID WHERE Person.personID = ?;
/* moviesKnownFor */
SELECT t.primaryTitle, t.originalTitle
FROM Movie
JOIN Title AS t ON Movie.titleID = t.titleID
JOIN AssociatedWith AS aw ON t.titleID = aw.titleID
JOIN Person AS p ON aw.personID = p.personID
WHERE aw.flag = 'KnownFor' AND p.name LIKE ?;
/* seriesKnownFor */
SELECT t.primaryTitle, t.originalTitle
FROM TVSeries
JOIN Title AS t ON TVSeries.titleID = t.titleID
JOIN AssociatedWith AS aw ON t.titleID = aw.titleID
JOIN Person AS p ON aw.personID = p.personID
WHERE aw.flag = 'KnownFor' AND p.name LIKE ?;
/* findPerson */
SELECT name, personID, birthYear, deathYear, 
IIF(deathYear is NULL, YEAR(getdate())-birthYear, deathYear - birthYear) age
FROM Person WHERE name LIKE ?;
/* findTitle */
SELECT primaryTitle, originalTitle, titleID, runTime, startYear, isAdult 
FROM Title WHERE primaryTitle LIKE ? OR originalTitle LIKE ?;
/* getRatings */
SELECT avgRating, numVotes FROM Title
JOIN TitleRating ON Title.titleID=TitleRating.titleID
WHERE Title.titleID=?;
/* getProfessionals */
SELECT name, Person.personID, Profession.professionName FROM Profession
JOIN HasProfession ON Profession.professionName=HasProfession.professionName
JOIN Person ON HasProfession.personID=Person.personID
WHERE Profession.professionName like ?;
/* listSeriesEpisodes */
SELECT t.*, e.episodeID FROM TVSeries AS s
JOIN TVEpisode AS e ON s.seriesID = e.seriesID
JOIN Title AS t ON e.titleID = t.titleID
WHERE s.seriesID = ?;
/* seriesMainCast */
WITH allEpisodes AS (
SELECT s.*
FROM TVSeries AS s
JOIN TVEpisode AS e ON s.seriesID = e.seriesID
WHERE s.seriesID = ?)
SELECT *, IIF(deathYear is NULL, YEAR(getdate())-birthYear, deathYear - birthYear) age
from Person where Person.personID in (
SELECT p.personID FROM Person AS p
JOIN ActsIn ON p.personID = ActsIn.personID
JOIN TVEpisode ON ActsIn.titleID = TVEpisode.titleID
JOIN TVSeries ON TVEpisode.seriesID = TVSeries.seriesID
WHERE TVSeries.seriesID = ?
GROUP BY p.personID
HAVING count(*) = (SELECT count(*) FROM allEpisodes));
/* listCastAndRoles */
SELECT p.personID, p.name, ActsIn.characterPlayed FROM Title
JOIN ActsIn ON Title.titleID = ActsIn.titleID
JOIN Person AS p ON ActsIn.personID = p.personID
WHERE Title.titleID = ?;
/* listAllProfessions */
SELECT * FROM Profession;