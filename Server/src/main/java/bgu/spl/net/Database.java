package bgu.spl.net;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class implemented safely as a thread-safe singleton.
 */
public class Database {
    private List<Admin> admins = new LinkedList<Admin>();
    private ConcurrentHashMap<Course, List<Student>> hashMapCoursesToStudents = new ConcurrentHashMap<Course, List<Student>>();
    private ConcurrentHashMap<Student, List<Course>> hashMapStudentsToCourses= new ConcurrentHashMap<Student, List<Course>>();

    private static class DatabaseHolder {
        private static Database instance = new Database();
    }

    //to prevent user from creating new Database
    private Database() {

    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return DatabaseHolder.instance;
    }

    /**
     * loades the courses from the file path specified
     * into the Database, returns true if successful.
     */
    boolean initialize(String coursesFilePath) {
        try {
            File myObj = new File(coursesFilePath);
            Scanner myReader = new Scanner(myObj);
            int counter = 0;
            while (myReader.hasNextLine()) {
                counter++;
                String data = myReader.nextLine();
                String[] course = new String[4];
                course = data.split("\\|");
                int courseNum = Integer.parseInt(course[0]);
                int maxNumOfStudents = Integer.parseInt(course[3]);
                Course c = new Course(courseNum, course[1], maxNumOfStudents, counter);
                c.setKdamCourses(course[2]);
                hashMapCoursesToStudents.putIfAbsent(c, new LinkedList<Student>());
            }
            myReader.close();
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean studentRegisterToSystem(Student s) {
        for (Student student : hashMapStudentsToCourses.keySet()){
            if (s.getUsername().equals(student.getUsername()))
                return false;
        }
        hashMapStudentsToCourses.put(s, new LinkedList<>());
        return true;
    }

    public synchronized void registerToCourse(Student s, Course c) {
        if (hashMapCoursesToStudents.get(c) != null & hashMapStudentsToCourses.get(s) != null) {
            if (didKdam(s, c)) {
                if (c.numOfRegisteredStudents < c.getNumOfMaxStudents()) {
                    hashMapStudentsToCourses.get(s).add(c);
                    hashMapCoursesToStudents.get(c).add(s);
                    Collections.sort(hashMapCoursesToStudents.get(c), new Comparator<Student>() {
                        @Override
                        public int compare(Student o1, Student o2) {
                            return o1.getUsername().compareTo(o2.getUsername());
                        }
                    });

                    c.numOfRegisteredStudents++;
                }
            }
        }
    }

    public boolean unregisterToCourse(Student s, Course c) {
        if (hashMapCoursesToStudents.get(c) != null & hashMapStudentsToCourses.get(s) != null) {
            if (hashMapCoursesToStudents.get(c).contains(s)) {
                hashMapCoursesToStudents.get(c).remove(s);
                hashMapStudentsToCourses.get(s).remove(c);
                c.numOfRegisteredStudents--;
                return true;
            }
        }
        return false;
    }


    public boolean didKdam(Student s, Course c) {
        List<Integer> kdamcourses = c.getKdamCourses();
        if (kdamcourses.isEmpty())
            return true;
        else {
            for (Integer i : kdamcourses) {
                boolean did = false;
                for (int j = 0; j < hashMapStudentsToCourses.get(s).size(); j++) {
                    if (hashMapStudentsToCourses.get(s).get(j).getCourseNum() == i)
                        did = true;
                }
                if (!did)
                    return false;
            }
        }
        return true;
    }

    public boolean isRegister(Student s, Course c) { return hashMapCoursesToStudents.get(c).contains(s); }

    public String getKdamByNum(Integer courseNum) {
        String output = "";
        List kdam = fitCourseNumToCourse(courseNum).getKdamCourses();
        sortCoursesByIndexOfInsert(kdam);
        int counter = 0;
        if (kdam.size() != 1) {
            for (int i = 0; i < kdam.size(); i++) {
                counter++;
                if (counter < kdam.size())
                    output = output + kdam.get(i).toString() + ",";
                else
                    output = output + kdam.get(i).toString();
            }
            return "[" +output +"]";
        }
            return "[" + kdam.get(0) + "]";
    }
    public synchronized Admin fitUserNameToAdmin (String adminName){
        for(Admin a: admins){
            if( a.getUsername().equals(adminName))
                return a;
        }
        return null;
    }
    public synchronized void addAdmin(Admin ad){ admins.add(ad); }

    public String myCourses (String userName){
        String output = "";
        for (Student s: hashMapStudentsToCourses.keySet()){
            if(s.getUsername().equals(userName) && hashMapStudentsToCourses.get(s).size() > 0) {
                    for (int j = 0; j < hashMapStudentsToCourses.get(s).size() - 1; j++)
                        output = output + String.valueOf(hashMapStudentsToCourses.get(s).get(j).getCourseNum()) + "," ;
                    output = output + String.valueOf(hashMapStudentsToCourses.get(s).get(hashMapStudentsToCourses.get(s).size() - 1).getCourseNum());
            }
        }
        return "[" + output + "]";
    }
public synchronized String printCourseStatus(Course c){
    String output ="Course: " + "(" + c.getCourseNum() + ") " + c.getCourseName() + "\n";
    output = output + "Seats Available: " +(c.getNumOfMaxStudents() - c.getNumOfRegisteredStudents()) + "/" + c.getNumOfMaxStudents() + "\n";
    output = output + "[";
    int numOfStudents = 0;
    for (Student s : hashMapCoursesToStudents.get(c)) {
        numOfStudents = numOfStudents + 1;
        if (numOfStudents < c.getNumOfRegisteredStudents())
            output = output + s.getUsername() + ",";
        else
            output = output + s.getUsername();
    }
    output = output + "]";
    return output;
}

    public synchronized String printStudentStatus (Student s){
        String output ="Student: " + s.getUsername() + "\n";
        output = output + "Courses: " + "[";
        int numOfCourses = 0;
        List <Integer> myCourses = new LinkedList<Integer>();
        for (int i=0; i<hashMapStudentsToCourses.get(s).size(); i++)
            myCourses.add(hashMapStudentsToCourses.get(s).get(i).getCourseNum());
        sortCoursesByIndexOfInsert(myCourses);
        for (int j=0; j < myCourses.size(); j++){
            numOfCourses = numOfCourses + 1;
            if (numOfCourses == hashMapStudentsToCourses.get(s).size())
                output = output + myCourses.get(j);
            else
                output = output + myCourses.get(j) + ",";
        }
        return output = output + "]";
    }

    public boolean courseExists(Course c) {
        return hashMapCoursesToStudents.containsKey(c);
    }

    public synchronized boolean availableSeats(Course c) {
        return (c.getNumOfRegisteredStudents() < c.getNumOfMaxStudents());
    }

    public Student fitUserNameToStudent(String userName){
        for (Student s : hashMapStudentsToCourses.keySet()){
            if (s.getUsername().equals(userName))
                return s;
        }
        return null;
    }

    public Course fitCourseNumToCourse(int courseNum){
        for (Course c : hashMapCoursesToStudents.keySet()){
            if (courseNum == c.getCourseNum())
                return c;
        }
        return null;
    }

    public void sortCoursesByIndexOfInsert(List <Integer> courses){
        List<Course> coursesToSort = new LinkedList<Course>();
        for (Integer i : courses)
            coursesToSort.add(fitCourseNumToCourse(i));
        Collections.sort(coursesToSort, new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                return o1.getIndexOfInsert() - o2.getIndexOfInsert();
            }
        });
        for (int j=0; j<courses.size(); j++)
            courses.set(j, coursesToSort.get(j).getCourseNum());
    }
    public synchronized boolean adminLogIn(Admin a ,String password) {
        boolean output = false;
        if (a.getPassword().equals(password)) {
            if (!a.logIn) {
                a.logIn = true;
                output = true;
            }
        }
        return output;
    }
    public synchronized boolean studentLogIn(Student s ,String password) {
        boolean output = false;
        if (s.getPassword().equals(password)) {
            if (!s.logIn) {
                s.logIn = true;
                output = true;
            }
        }
        return output;
    }
    public synchronized void logout(User u){ u.logIn = false; }
}
