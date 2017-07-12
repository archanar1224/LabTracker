Steps to be followed to deploy the project:

CREATING DATABASE :
1. Go to mysql
2. Create a database in mysql. Example : create database <database_name>
3. Using the database dump, load the above created database by running the following command:
	mysql -u username -p database_name < database_dump.sql

IMPORTING THE PROJECT :

1. Extract code from the zip file.
2. In Eclipse, import the code folder as 'Existing Maven Projects', and proceed and finish importing.
3. Go to 'src/main/resources' path in the Project. Edit config.properties file.
	- Change the database name
	- Change the database user and password
	- Change the path to the log file
4. Change the project URL in angular JS file.
5. Change the swagger path in web.xml file under src/main/webapp/WEB-INF and also in swagger.html under src/main/webapp/swagger
6. Run the project on server.




