import json
import tempfile
PersonTSVFileName  = "IMDB Dataset/name.basics.tsv"
TitleTSVFileName = "IMDB Dataset/title.basics.tsv"
RatigsTSVFileName = "IMDB Dataset/title.ratings.tsv"
EpisodesTSVFileName = "IMDB Dataset/title.episode.tsv"
WorksOnTSVFileName = "IMDB Dataset/title.principals.tsv" # has jobs and actors
DirectWritesTSVFileName = "IMDB Dataset/title.crew.tsv" # has diretors and writors


SQLFileName = "Populate_Database.sql" # appends insert statements to end of file

MIN_NUMBER_OF_REVIEWS = 100000

validRatings = []
validRatingsIDS = set()

tvSeriesIDS = {}
validTvEpisodeIDS = set()
validTvEpisodes = []

validTitleIDS = set()

validPeopleIDS = set()

directs = set()
writes = set()

professions = set()

seriesID = 1
def main():
    ratingsTempFile = tempfile.NamedTemporaryFile(mode = "w+")
    moviesTempFile = tempfile.NamedTemporaryFile(mode = "w+") 
    tvEpisodeTempFile = tempfile.NamedTemporaryFile(mode = "w+") 
    tvSeriesTempFile = tempfile.NamedTemporaryFile(mode = "w+") 
    actsInTempFile = tempfile.NamedTemporaryFile(mode = "w+") 
    directsTempFile = tempfile.NamedTemporaryFile(mode = "w+") 
    writesTempFile = tempfile.NamedTemporaryFile(mode = "w+") 
    worksOnTempFile = tempfile.NamedTemporaryFile(mode = "w+")
    hasProfessionTempFile = tempfile.NamedTemporaryFile(mode = "w+")
    knownForTempFile = tempfile.NamedTemporaryFile(mode = "w+")

    print("Processing")
    with open(SQLFileName, 'a') as SQLFile:
        with open(RatigsTSVFileName, 'r') as ratingsFile:
            ratingsFile.readline() # skip top line of file that describes file
            line = ratingsFile.readline().strip()
            count = 0
            while line:
                tokens = line.split("\t")
                numVotes = int(tokens[2])
                if numVotes > MIN_NUMBER_OF_REVIEWS:
                    count += 1
                    validRatingsIDS.add(toID(tokens[0]))
                    insertStatement = f"INSERT INTO TitleRating (titleID, avgRating, numVotes) VALUES ({toID(tokens[0])}, {tokens[1]}, {numVotes});\n"
                    insertStatement = insertStatement.replace("\\N", "NULL")
                    ratingsTempFile.write(insertStatement)
                    
                line = ratingsFile.readline().strip()
            print(f"There are {count} number of valid titles due to ratings")


        with open(EpisodesTSVFileName, 'r') as episodesFile:
            episodesFile.readline() # skip top line of file that describes file
            line = episodesFile.readline().strip()
            episodeCount = 0
            while line:
                tokens = line.split("\t")
                seriesID = toID(tokens[1])
                if seriesID in validRatingsIDS:
                    episodeCount += 1
                    validTvEpisodes.append((toID(tokens[0]), seriesID, tokens[2], tokens[3]))
                    validTvEpisodeIDS.add(toID(tokens[0]))

                line = episodesFile.readline().strip()
            print(f"There are {episodeCount} number of valid episodes due to ratings")
            

        with open(TitleTSVFileName, 'r') as titleFile:
            titleFile.readline() # skip top line of file that describes file
            line = titleFile.readline().strip()
            movieCount = 0
            tvSeriesCount = 0
            tvEpisodeCount = 0
            while line:
                tokens = line.split("\t")
                # verify it has enough ratings and is a movie, TvSeries or TVEpisode
                if tokens[1] in ['movie', 'tvEpisode', 'tvSeries']:
                    titleID = toID(tokens[0])
                    
                    valid = False
                    match tokens[1]:
                        case 'movie':
                            if titleID in validRatingsIDS:
                                valid = True
                                movieCount += 1
                                validTitleIDS.add(titleID)

                                insertStatement = f"INSERT INTO Movie (titleID) VALUES ({titleID});\n"
                                moviesTempFile.write(insertStatement)
                        case 'tvEpisode':
                            if titleID in validTvEpisodeIDS:
                                valid = True
                                tvEpisodeCount += 1
                                validTitleIDS.add(titleID)

                        case 'tvSeries':
                            if titleID in validRatingsIDS:
                                valid = True
                                tvSeriesCount += 1
                                validTitleIDS.add(titleID)

                                tvSeriesIDS[f"{titleID}"] = seriesID
                                insertStatement = f"INSERT INTO TVSeries (titleID, seriesID) VALUES ({titleID}, {seriesID});\n"
                                tvSeriesTempFile.write(insertStatement)
                                seriesID += 1
                    if valid:
                        primaryTitle = tokens[2].replace("'", "''")
                        originalTitle = tokens[3].replace("'", "''")
                        insertStatement = f"INSERT INTO Title (titleID, primaryTitle, originalTitle, startYear, runTime, isAdult) VALUES ({titleID}, '{primaryTitle}', '{originalTitle}', {tokens[5]}, {tokens[7]}, {tokens[4]});\n"
                        insertStatement = insertStatement.replace("'\\N'", "NULL")
                        insertStatement = insertStatement.replace("\\N", "NULL")
                        SQLFile.write(insertStatement)
                    
                line = titleFile.readline().strip()
            print(f"There are {movieCount} number of movies")
            print(f"There are {tvSeriesCount} number of tvSeries")
            print(f"There are {tvEpisodeCount} number of tvEpisodes")


        with open(WorksOnTSVFileName, 'r') as WorksOnFile:
            WorksOnFile.readline() # skip top line of file that describes file
            line = WorksOnFile.readline().strip()
            directCount = 0
            writeCount = 0
            actorCount = 0
            while line:
                tokens = line.split("\t")
                titleID = toID(tokens[0])
                personID = toID(tokens[2])
                if titleID in validTitleIDS:
                    validPeopleIDS.add(personID)
                    match tokens[3]:
                        case 'director':
                            directs.add((titleID, personID))
                            insertStatement = f"INSERT INTO Directs (titleID, personID) VALUES ({titleID}, {personID});\n"
                            directsTempFile.write(insertStatement)
                            directCount += 1
                        case 'writer':
                            writes.add((titleID, personID))
                            insertStatement = f"INSERT INTO Writes (titleID, personID) VALUES ({titleID}, {personID});\n"
                            writesTempFile.write(insertStatement)
                            writeCount += 1
                        case 'actor' | 'actress':
                            if tokens[5] != "\\N":
                                roles = json.loads(tokens[5])
                                for r in roles:
                                    character = r.replace("'","''")

                                    insertStatement = f"INSERT INTO ActsIn (titleID, personID, characterPlayed) VALUES ({titleID}, {personID}, '{character}');\n"
                                    insertStatement = insertStatement.replace("'\\N'", "NULL")
                                    actsInTempFile.write(insertStatement)
                                    actorCount += 1
                            else:
                                insertStatement = f"INSERT INTO ActsIn (titleID, personID) VALUES ({titleID}, {personID});\n"
                                actsInTempFile.write(insertStatement)
                        case _:
                            # default/other
                            jobCatagory = tokens[3].replace("'","''")
                            jobName = tokens[4].replace("'","''")
                            insertStatement = f"INSERT INTO WorksOn (titleID, personID, jobCategory, jobName) VALUES ({titleID}, {personID}, '{jobCatagory}', '{jobName}');\n"
                            insertStatement = insertStatement.replace("'\\N'", "NULL")
                            worksOnTempFile.write(insertStatement)
                line = WorksOnFile.readline().strip()
            print(f"There are {directCount} number of directors")
            print(f"There are {writeCount} number of writers")
            print(f"There are {actorCount} number of actors")

            
        with open(DirectWritesTSVFileName, 'r') as DirectWritesFile:
            DirectWritesFile.readline() # skip top line of file that describes file
            line = DirectWritesFile.readline().strip()
            directCount = 0
            writeCount = 0
            while line:
                tokens = line.split("\t")
                titleID = toID(tokens[0])
                if titleID in validTitleIDS:
                    if tokens[1] != "\\N":
                        directors = tokens[1].split(",")
                        for d in directors:
                            personID = toID(d)
                            validPeopleIDS.add(personID)
                            if (titleID, personID) not in directs:
                                insertStatement = f"INSERT INTO Directs (titleID, personID) VALUES ({titleID}, {personID});\n"
                                directsTempFile.write(insertStatement)
                                
                        directCount += len(directors)
                    if tokens[2] != "\\N":
                        writers = tokens[2].split(",")
                        for w in writers:
                            personID = toID(w)
                            validPeopleIDS.add(personID)
                            if (titleID, personID) not in writes:
                                insertStatement = f"INSERT INTO Writes (titleID, personID) VALUES ({titleID}, {personID});\n"
                                writesTempFile.write(insertStatement)
                        writeCount += len(writers)
                line = DirectWritesFile.readline().strip()
            print(f"There are {directCount} number of directors")
            print(f"There are {writeCount} number of writors")


        with open(PersonTSVFileName, 'r') as personFile:
            # Inserts into person table
            personFile.readline() # skip top line of file that describes file
            count = 0
            line = personFile.readline().strip()
            SQLFile.write("\n")
            while line:
                tokens = line.split("\t")
                personID = toID(tokens[0])
                if personID in validPeopleIDS:
                    count += 1
                    name = tokens[1].replace("'", "''")
                    insertStatement = f"INSERT INTO Person (personID, name, birthYear, deathYear) VALUES ({personID}, '{name}', {tokens[2]}, {tokens[3]});\n"
                    insertStatement = insertStatement.replace("\\N", "NULL")
                    SQLFile.write(insertStatement)
                    if tokens[4] != "\\N":
                        for p in tokens[4].split(","):
                            p = p.replace("'", "''")
                            professions.add(p)
                            insertStatement = f"INSERT INTO HasProfession (personID, professionName) VALUES ({personID}, '{p}');\n"
                            hasProfessionTempFile.write(insertStatement)
                    if tokens[5] != "\\N":
                        for t in tokens[5].split(","):
                            titleID = toID(t)
                            if titleID in validTitleIDS:
                                insertStatement = f"INSERT INTO KnownFor (personID, titleID) VALUES ({personID}, {titleID});\n"
                                knownForTempFile.write(insertStatement)

                line = personFile.readline().strip()
        
        # Combine temp files together

        for p in professions:
            insertStatement = f"INSERT INTO Profession (professionName) VALUES ('{p}');\n"
            SQLFile.write(insertStatement)

        hasProfessionTempFile.seek(0)
        SQLFile.write("\n")
        SQLFile.write(hasProfessionTempFile.read())

        for e in validTvEpisodes:
            try:
                seriesID = tvSeriesIDS[f"{e[1]}"]
                insertStatement = f"INSERT INTO TVEpisode (titleID, seriesID, seasonNum, episodeNum) VALUES ({e[0]}, {seriesID}, {e[2]}, {e[3]});\n"
                insertStatement = insertStatement.replace("\\N", "NULL")
                tvEpisodeTempFile.write(insertStatement)
            except KeyError as e:
                print(e)
        
        moviesTempFile.seek(0)
        SQLFile.write("\n")
        SQLFile.write(moviesTempFile.read())

        tvSeriesTempFile.seek(0)
        SQLFile.write("\n")
        SQLFile.write(tvSeriesTempFile.read())

        tvEpisodeTempFile.seek(0)
        SQLFile.write("\n")
        SQLFile.write(tvEpisodeTempFile.read())

        actsInTempFile.seek(0)
        SQLFile.write("\n")
        SQLFile.write(actsInTempFile.read())

        directsTempFile.seek(0)
        SQLFile.write("\n")
        SQLFile.write(directsTempFile.read())

        writesTempFile.seek(0)
        SQLFile.write("\n")
        SQLFile.write(writesTempFile.read())

        worksOnTempFile.seek(0)
        SQLFile.write("\n")
        SQLFile.write(worksOnTempFile.read())

        knownForTempFile.seek(0)
        SQLFile.write("\n")
        SQLFile.write(knownForTempFile.read())

        ratingsTempFile.seek(0)
        SQLFile.write("\n")
        SQLFile.write(ratingsTempFile.read())

    ratingsTempFile.close()
    moviesTempFile.close()
    tvEpisodeTempFile.close()
    tvSeriesTempFile.close()
    actsInTempFile.close()
    directsTempFile.close()
    writesTempFile.close()
    worksOnTempFile.close()
    print("Done processing")



def toID(s: str)-> int:
    # takes tconst or nconst to an id, removes letters and turns to str
    return int(s[2:])

main()