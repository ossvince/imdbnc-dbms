## 3380 Final Project

You should not need to enter a userid and password to use the interface as the file *auth.cfg* 
automatically does it for you. However, if the userid and password for the fully populated db are needed they are as follows

    userid = 
    password = 
TODO add userid and password for populated db

### How to run

To run our interface use the command 

    make run

in the terminal.

### Create Database

To create the database first run the interface with the previously mentioned command. 
Then one the interface is running and connected use the command 

    dbc

in the interface which will create the tables for the database.

To then populate the database use the command

    dbr

in the interface to repopulate it with all the data. (It Will likely take 15-20 ish minutes to complete)
The interface will say 

    Database has been successfully populated

when the repopulation is complete.
