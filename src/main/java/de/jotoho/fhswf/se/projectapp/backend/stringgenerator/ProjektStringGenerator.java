package de.jotoho.fhswf.se.projectapp.backend.stringgenerator;

import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.Student;

import java.util.Objects;
import java.util.Set;

@SuppressWarnings("unused")
public final class ProjektStringGenerator {

    public static final String SELECT_ALL = "select * from Projekt";
    public static final String GET_ALL_IDS = "select P_ID from Projekt ";
    public static final String NUMBER_OF_P_IDS = "select count(*) from P_ID";
    public static final String FREE_IDS = "select * from P_ID";
    public static final String NEW_IDS = "insert into P_ID default values";


    private ProjektStringGenerator(){}

    public static String selectProjektString(final long id) {
        return "select * from Projekt where P_ID = " + id;
    }

    private static String insertMembers(final Set<Student> members){
        String memberString = "";
        if(members.isEmpty())
            return memberString;

        for(int member = 1; member <= members.size();member++)
            memberString = memberString.concat("Student_"+member+',');
        return memberString;
    }

    private static String insertMemberIDs(final Set<Student> members){
        String memberString = "";
        if(members.isEmpty())
            return memberString;

        for(final Student member : members)
            memberString = memberString.concat("," + member.getStudentID());
        return memberString;
    }

    private static String updateMember(final Set<Student> members){
        String memberString = "";
        if(members.isEmpty())
            return memberString;

        int memberCount = 1;
        for(final Student member : members) {
            memberString = memberString.concat("Student_" + memberCount + '=' + member.getStudentID() + ',');
            memberCount++;
        }

        for(int member = memberCount; member <= Projekt.MAX_MEMBERS;member++) {
            memberString = memberString.concat("Student_" + member + '=' +null +',');
        }
        return memberString;
    }

    public static String insertProjektString(final Projekt projekt) {
        Objects.requireNonNull(projekt);
        String contact = "";
        if(projekt.getContact().isPresent())
            contact = contact.concat("," + projekt.getContact().get().getID());
        return "insert into Projekt(P_ID,Title,Beschreibung," +
                "Kontext,Outline,Status,Feedback," +
                insertMembers(projekt.getMemberView()) +
                "Ansprechpartner)"+
                "values(" + projekt.getID() +
                ",\"" + projekt.getTitle() + '\"' +
                ",\"" + projekt.getDescription() + '\"' +
                ",\"" + projekt.getContext() + '\"' +
                ",\"" + projekt.getTextOutline() + '\"' +
                ",\"" + projekt.getStatus() + '\"' +
                ",\"" + projekt.getFeedback() + '\"' +
                insertMemberIDs(projekt.getMemberView())+
                contact + ");";
    }

    public static String updateProjektString(final Projekt projekt) {
        Objects.requireNonNull(projekt);
        if(projekt.getContact().isEmpty())
            return null;
        return "update Projekt set " +
                "Title = \"" + projekt.getTitle() + "\"," +
                "Beschreibung = \"" + projekt.getDescription() + "\"," +
                "Kontext = \"" + projekt.getContext() + "\"," +
                "Outline = \"" + projekt.getTextOutline() + "\"," +
                "Status = \"" + projekt.getStatus() + "\"," +
                "Feedback = \"" + projekt.getFeedback() + "\"," +
                updateMember(projekt.getMemberView())+
                "Ansprechpartner = " + projekt.getContact().get().getID() +
                " where P_ID =" + projekt.getID();
    }

    public static String deleteProjektString(final long id) {
        return "delete from Projekt where P_ID =" + id;
    }
}


