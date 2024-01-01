package de.jotoho.fhswf.se.projectapp;

import de.jotoho.fhswf.se.projectapp.database.Database;

public class Main {
    public static void main(String[] args){
        Database.initDatabase();
        //Student student1 = new Student(12345,"Hallo","Welt",null);
        //Student student2 = new Student(123745,"Hallo","Welt","Hallo@Welt");
        //Database.insert(student1);
        //Database.insert(student2);
        //student2.setFamilyName("World");
        //Database.update(student2);
        for(Student studi : Database.loadStudentenFromDatabase())
            System.out.println(studi.getEmailAddr().orElse(null));
    }
}
