/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp;

import de.jotoho.fhswf.se.projectapp.backend.database.*;
import de.jotoho.fhswf.se.projectapp.ui.menu.StartMenu;

public class Main {
    public static void main(String[] args){
        Database.initDatabase();
        /*
        final Student student = new Student(123456,"Tim","Beckmann");
        final Unternehmen unternehmen = new Unternehmen(UnternehmenDatabase.getFreeID(),"FHSWF","Iserlohn");
        final Ansprechpartner ansprechpartner = new Ansprechpartner(AnsprechpartnerDatabase.getFreeID(),"Jonas","Hopush",unternehmen);
        final Projekt projekt = new Projekt(ProjektDatabase.getFreeID());

        projekt.setContact(ansprechpartner);
        projekt.setTitle("Test");
        projekt.setDescription("Beschreibung");
        projekt.setContext("Kontext");
        projekt.setTextOutline("Outline");
        projekt.setFeedback("Feedback");
        projekt.addMember(student);

        StudentDatabase.addStudent(student);
        UnternehmenDatabase.addUnternehmen(unternehmen);
        AnsprechpartnerDatabase.addAnsprechpartner(ansprechpartner);
        ProjektDatabase.addProjekt(projekt);
        Database.save();


        student.setFirstName("Timm");
        student.setEmail("test@fhswf.de");

        unternehmen.setName("FH-SWF");

        ansprechpartner.setFirstName("Jonas Tobias");

        projekt.setTitle("Update Test");

        final Student student2 = new Student(9874986,"Tom","Beckfrau","beckfrau@fh-swf.de");
        StudentDatabase.addStudent(student2);
        projekt.addMember(student2);

        Database.save();
        */
        StartMenu.startMenu();
    }
}
