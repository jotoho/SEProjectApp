package de.jotoho.fhswf.se.projectapp.backend.stringgenerator;

import de.jotoho.fhswf.se.projectapp.Unternehmen;

import java.util.Objects;

@SuppressWarnings("unused")
public final class UnternehmenStringGenerator {

    public static final String SELECT_ALL = "select * from Unternehmen";
    public static final String GET_ALL_IDS = "select U_ID from Unternehmen ";
    public static final String NUMBER_OF_U_IDS = "select count(*) from U_ID";
    public static final String FREE_IDS = "select * from U_ID";
    public static final String NEW_IDS = "insert into U_ID default values";


    private UnternehmenStringGenerator(){}

    public static String selectUnternehmenString(final long id) {
        return "select * from Unternehmen where U_ID = " + id;
    }

    public static String insertUnternehmenString(final Unternehmen unternehmen) {
        Objects.requireNonNull(unternehmen);
        return "insert into Unternehmen(U_ID,Name,Adresse) " +
                "values(" + unternehmen.getID() +
                ",\"" + unternehmen.getName() + '\"' +
                ",\"" + unternehmen.getAddress() + "\");";
    }

    public static String updateUnternehmenString(final Unternehmen unternehmen) {
        Objects.requireNonNull(unternehmen);
        return "update Unternehmen set " +
                "Name = \"" + unternehmen.getName() + "\"," +
                "Adresse = \"" + unternehmen.getAddress() + '\"' +
                "where U_ID =" + unternehmen.getID();
    }

    public static String deleteUnternehmenString(final long id) {
        return "delete from Unternehmen where U_ID =" + id;
    }
}
