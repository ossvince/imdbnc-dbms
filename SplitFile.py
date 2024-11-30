fileName = "Populate_Database.sql" # file to split

numSplits = 3

with open(fileName, "r") as file:
    data = file.readlines()
    size = len(data)

    for i in range(0, numSplits):
        newFileName = fileName.split(".")[0] + f"{i+1}." + fileName.split(".")[1] #add current split number to filename
        newFile = open(newFileName, "w")
        for line in range(i * size // numSplits, (i+1) * size // numSplits):
            newFile.write(data[line])
        newFile.close()