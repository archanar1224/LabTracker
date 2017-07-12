package org.nimhans.EHealthCare;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.nimhans.EHealthCare.database.*;
import org.nimhans.EHealthCare.factory.LabFactory;
import org.nimhans.EHealthCare.factory.NimhansFactory;
import org.nimhans.EHealthCare.model.Station;

public class Loader implements ServletContextListener{

	private static LabFactory labFactory;
	private static Connection dbConnection;
	private static Logger logger;
	
	
	
	public static LabFactory getLabFactory() {
		return labFactory;
	}

	public static void setLabFactory(LabFactory labFactory) {
		Loader.labFactory = labFactory;
	}

	public static Connection getDbConnection() {
		return dbConnection;
	}

	public static void setDbConnection(Connection dbConnection) {
		Loader.dbConnection = dbConnection;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		Loader.logger = logger;
	}

	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener destroyed");
	}
        //Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent arg0) 
	{
		/* Instantiates the NimhansFactory */
		labFactory = new NimhansFactory();
		Properties prop = new Properties();
		InputStream input = null;
		String logPath = "";
		
		System.out.println("ServletContextListener started");
		try
		{
			/* Creates global database connection */
			Class.forName("com.mysql.jdbc.Driver");  
			input = this.getClass().getClassLoader().getResourceAsStream("config.properties");
			prop.load(input);
			String db = prop.getProperty("database");
			String dbuser = prop.getProperty("dbuser");
			String dbpwd = prop.getProperty("dbpassword");
			logPath = prop.getProperty("logpath");
			System.out.println(db + " " + dbuser + " " + dbpwd + "  "+ logPath);
			
			dbConnection = DriverManager.getConnection(db,dbuser,dbpwd);  
			
		
		}
		catch(Exception e)
		{
			System.out.println("exception in creation of database connection");
		}
		
		logger = Logger.getLogger("MyLog");  
		
	    FileHandler fh;  

	    try 
	    {  

	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler(logPath); 
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	        logger.addHandler(fh);
	        // the following statement is used to log any messages  
	        logger.info("My first log");  
	        //logger.setLevel(Level.OFF);

	    } 
	    catch (SecurityException e) 
	    {  
	        e.printStackTrace();  
	    }
	    catch (IOException e) 
	    {  
	        e.printStackTrace();  
	    }  
		
	}
}
