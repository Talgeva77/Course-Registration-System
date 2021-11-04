package bgu.spl.net;

import java.util.LinkedList;
import java.util.List;

public class Course {
    private int courseNum;
    private String courseName;
    private List <Integer> kdamCourses;
    private int numOfMaxStudents;
    public int numOfRegisteredStudents;
    private int indexOfInsert;
    public Course(int courseNum, String courseName, int numOfMaxStudents, int indexOfInsert){
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.numOfMaxStudents = numOfMaxStudents;
        numOfRegisteredStudents = 0;
        this.kdamCourses = new LinkedList<>();
        this.indexOfInsert = indexOfInsert;
    }

    public int getNumOfMaxStudents() { return numOfMaxStudents; }

    public List <Integer> getKdamCourses(){
        return kdamCourses;
    }

    public void setKdamCourses(String stringKdamCourses){
        if (!kdamCourses.equals("[]")){
            for (int i=1;i<stringKdamCourses.length()-1; i=i+3){
                int intKdam = Integer.parseInt(stringKdamCourses.substring(i,i+2));
                kdamCourses.add(intKdam);
            }
        }
    }

    public int getCourseNum (){return courseNum;}


    public int getNumOfRegisteredStudents() {
        return numOfRegisteredStudents;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getIndexOfInsert() { return indexOfInsert;}
}
