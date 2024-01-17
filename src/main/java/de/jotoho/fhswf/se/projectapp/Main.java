package de.jotoho.fhswf.se.projectapp;

import de.jotoho.fhswf.se.projectapp.database.Database;
import de.jotoho.fhswf.se.projectapp.ui.StartMenu;

import static de.jotoho.fhswf.se.projectapp.ui.UnternehmenMenu.getFreeID;

public class Main {
    public static void main(String[] args){
        Database.initDatabase();
        final Unternehmen test1 = new Unternehmen(getFreeID(),"Test 1","Teststraße.1");
        Database.addUnternehmen(test1);
        final Unternehmen test2 = new Unternehmen(getFreeID(),"Test 2","Teststraße.2");
        Database.addUnternehmen(test2);
        final Unternehmen test3 = new Unternehmen(getFreeID(),"Test 3","Teststraße.3");
        Database.addUnternehmen(test3);
        StartMenu.startMenu();
    }
}
