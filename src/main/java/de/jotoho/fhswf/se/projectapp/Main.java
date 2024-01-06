package de.jotoho.fhswf.se.projectapp;

import de.jotoho.fhswf.se.projectapp.database.Database;
import de.jotoho.fhswf.se.projectapp.ui.StartMenu;

import javax.xml.crypto.Data;

public class Main {
    public static void main(String[] args){
        Database.initDatabase();
        //StartMenu.userOptions();
    }
}
