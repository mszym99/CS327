/*****************************
Query the University Database
*****************************/
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.util.*;

import com.mysql.cj.protocol.Resultset;

import java.lang.String;

public class MyQuery {

    private Connection conn = null;
	 private Statement statement = null;
	 private ResultSet resultSet = null;
    
    public MyQuery(Connection c)throws SQLException
    {
        conn = c;
        // Statements allow to issue SQL queries to the database
        statement = conn.createStatement();
    }
    
    public void findFall2009Students() throws SQLException
    {
        String query  = "SELECT DISTINCT name FROM student natural join takes where semester = \'Fall\' and year = 2009;";

        this.resultSet = this.statement.executeQuery(query);

        /** String query  = "select distinct name from student natural join takes where semester = \'Fall\' and year = 2009;";
        statement = conn.createStatement(); //create statement stmt
        // create rs query execution
        resultSet rs = statement.executeQuery("SELECT DISTINCT name FROM student NATURAL JOIN taeks WHERE semester = \'Fall\' AND year = 2009;"); */
    }
    
    public void printFall2009Students() throws IOException, SQLException
    {
	      System.out.println("******** Query 0 ********");
         System.out.println("name");
         while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number which starts at 1
			String name = resultSet.getString(1);
         System.out.println(name);
   		}        
    }

    public void findGPAInfo() throws SQLException
    {
        String query = "SELECT id, name, sum((CASE grade\n" +
            " WHEN 'A' THEN 4\n" +
            " WHEN 'A-' THEN 3.67\n" +
            " WHEN 'B+' THEN 3.33\n" +
            " WHEN 'B' THEN 3\n" +
            " WHEN 'B-' THEN 2.67\n" +
            " WHEN 'C+' THEN 2.33\n" +
            " WHEN 'C' THEN 2\n" +
            " WHEN 'C-' THEN 1.67\n" +
            " WHEN 'D+' THEN 1.33\n" +
            " WHEN 'D' THEN 1\n" +
            " WHEN 'D-' THEN 0.67\n" +
            " WHEN 'F' THEN 0\n" +
        " END) * credits) / sum(credits) GPA\n" +
        " FROM student join takes using(id) join course using(course_id)\n" +
        " WHERE grade IS NOT NULL\n" +
        " GROUP BY id;";
        this.resultSet = this.statement.executeQuery(query);
    }
    
    public void printGPAInfo() throws IOException, SQLException
    {
		   System.out.println("******** Query 1 ********");
           System.out.printf("ID, name,   GPA\n");
           System.out.println("*****************************************************************************************");	

           System.out.println("");
           String ID;
           String name;
           String GPA;
           while (this.resultSet.next()) {
                ID = this.resultSet.getString(1);
			    name = this.resultSet.getString(2);
                GPA = this.resultSet.getString(3);
         System.out.printf(ID + " | " + name + " | " + GPA + "\n");
           }
    }

    public void findMorningCourses() throws SQLException
    {
        String query = "SELECT course_id, sec_id, title, semester, year, name, count(DISTINCT takes.id) as enrollment\n" +
        "FROM course natural join section natural join time_slot natural join\n" +
        "instructor join takes using(course_id, sec_id, semester, year)\n" +
        "WHERE start_hr <= 12\n" +
        "GROUP BY course_id, sec_id, title, semester, year, name\n" +
        "HAVING count(DISTINCT takes.id) > 0;\n";
        this.resultSet = this.statement.executeQuery(query);

    }

    public void printMorningCourses() throws IOException, SQLException
    {
	   	System.out.println("******** Query 2 ********");
        System.out.printf( "course_id, sec_id,  title, semester, year, name, enrollment\n");
        System.out.println("*****************************************************************************************");	

        System.out.println("");
        String course_id;
        String sec_id;
        String title;
        String semester;
        String year;
        String name;
        String enrollment;

        while (this.resultSet.next()) {
            course_id = this.resultSet.getString(1);
			sec_id = this.resultSet.getString(2);
            title = this.resultSet.getString(3);
            semester = this.resultSet.getString(4);
            year = this.resultSet.getString(5);
            name = this.resultSet.getString(6);
            enrollment = this.resultSet.getString(7);

        System.out.printf(course_id + " | " + sec_id + " | " + title + " | " + semester + " | " + year + " | " + name + " | " + enrollment + "\n");
        }
           
    }

    public void findBusyInstructor() throws SQLException
    {
        String query = "SELECT name\n" +
                       "FROM instructor natural join teaches\n" +
                       "GROUP BY id\n" +
                       "HAVING count(id) >= ALL (SELECT count(id) FROM instructor natural join teaches\n" +
                       "GROUP BY id);";
        this.resultSet = this.statement.executeQuery(query);

    }

    public void printBusyInstructor() throws IOException, SQLException
    {
		   System.out.println("******** Query 3 ********");
           System.out.printf("name\n");
           System.out.println("*******");	

           System.out.println("");
           String name;
           while (this.resultSet.next()) {  
			    name = this.resultSet.getString(1);
                System.out.printf(name + "\n");
           }
    }

    public void findPrereq() throws SQLException
    {
        String query = "SELECT title as course,\n" +
                        "CASE WHEN prereq_id IS NULL THEN ''\n" +
                        "ELSE (SELECT pre.title FROM course pre WHERE tab.prereq_id = pre.course_id)\n" +
                        "END prereq\n" +
                        "FROM course LEFT JOIN prereq tab using(course_id);";
        this.resultSet = this.statement.executeQuery(query);
    }

    public void printPrereq() throws IOException, SQLException
    {
		   System.out.println("******** Query 4 ********");
           System.out.printf("course,         prereq\n");
           System.out.println("*****************************");	

           System.out.println("");
           String course;
           String prereq;
           while (this.resultSet.next()) {  
			    course = this.resultSet.getString(1);
                prereq = this.resultSet.getString(2);
                System.out.printf(course + " |||| " + prereq + "\n");
           }
    }

    public void updateTable() throws SQLException
    {
            String update = "UPDATE studentCopy t1\n" +
                            "SET tot_cred = (SELECT sum(credits) FROM course natural join takes t2\n" +
                            "WHERE t1.ID = t2.ID AND grade != 'F' AND grade IS NOT NULL);";
            this.statement.execute(update);

            String query = "SELECT * FROM studentCopy;";
            this.resultSet = this.statement.executeQuery(query);

    }

    public void printUpdatedTable() throws IOException, SQLException
    {
		   System.out.println("******** Query 5 ********");
           
           System.out.printf("ID,    name, dept_name, tot_cred\n");
           System.out.println("**************************************");	

           System.out.println("");
           String ID;
           String name;
           String dept_name;
           String tot_cred;
           while (this.resultSet.next()) {  
			    ID = this.resultSet.getString(1);
                name = this.resultSet.getString(2);
                dept_name = this.resultSet.getString(3);
                tot_cred = this.resultSet.getString(4);

                System.out.printf(ID + " | " + name + " | " + dept_name + " | " + tot_cred +  "\n");
           }
    }
	
	 public void findHeadCounts() throws SQLException
	 {
		  System.out.println("******** Query 6 ********");	
          System.out.println("*************************");	

          Scanner scanner = new Scanner(System.in);
          String deptname = scanner.nextLine();
          String query = "{ call getNumbers(\"" + deptname + "\", @studentCount, @instructorCount) }";
          String studentCount;
         // String instructorCount;
          this.statement = conn.prepareCall(query); 

          resultSet = this.statement.executeQuery(query);
          System.out.printf("Deptname,        StudentCount ->  Instructor Count\n");
          while (this.resultSet.next()) {  
              
            studentCount = this.resultSet.getString(1);
            //instructorCount = this.resultSet.getString(1); //not working like how i wanted to work but i got it to work using UNION into one column.

            System.out.printf(deptname + " Department has " + studentCount + "\n");
            //System.out.printf(deptname + "Department has" + instructorCount + "Instructors" + "\n");

       }
       scanner.close();
	 }
    
    
    public void findFirstLastSemester() throws SQLException
    {
        //This one was really hard to wrap my head around probably because i started way too late which is my fault
        //basically i need to implement a way to find First_Semester as a subquery and Last_Semester as a subquery I believe i got the first portion
        // of the subquery correct as my First_Semester lines up to the First_Semester reference given in the pdf.
        String query = "SELECT DISTINCT id, name, CONCAT(semester,' ', year) as First_Semester, CONCAT(semester, ' ', year) as Last_Semester,\n" +
        "CASE semester\n" +
            "WHEN 'Fall' THEN 1\n" +
            "WHEN 'Summer' THEN 0\n" +
            "WHEN 'Spring' THEN -1\n" +
        "END\n" +
        "FROM student natural join takes t1\n" +
        "WHERE year = (SELECT min(year) FROM takes t2 WHERE t1.id = t2.id) AND\n" +
        "semester = (SELECT max(semester) FROM takes t3 WHERE t1.id = t3.id);";
        
        this.resultSet = this.statement.executeQuery(query);
    }

    public void printFirstLastSemester() throws IOException, SQLException
    {
        System.out.println("******** Query 7 ********");
        System.out.printf("id,  name, First_Semester,   Last_Semester\n");
        System.out.println("************************************************");	

        System.out.println("");
        String id;
        String name;
        String First_Semester;
        String Last_Semester;
           while (this.resultSet.next()) {  
			    id = this.resultSet.getString(1);
                name = this.resultSet.getString(2);
                First_Semester = this.resultSet.getString(3);
                Last_Semester = this.resultSet.getString(4);

                System.out.printf(id + " | " + name + " | " + First_Semester + " | " + Last_Semester + " | " +"\n");
           }
    }

}
