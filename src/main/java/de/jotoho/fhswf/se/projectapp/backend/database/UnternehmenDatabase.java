package de.jotoho.fhswf.se.projectapp.backend.database;


import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Unternehmen;
import de.jotoho.fhswf.se.projectapp.backend.stringgenerator.UnternehmenStringGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static de.jotoho.fhswf.se.projectapp.backend.database.Database.*;

@SuppressWarnings("unused")
public final class UnternehmenDatabase {
    private static final Set<Unternehmen> loadedUnternehmen = new HashSet<>();
    private static final SequencedSet<Long> freeIDsFromDatabase = new LinkedHashSet<>();

    private UnternehmenDatabase() {
    }

    public static void initUnternehmen() {
        loadedUnternehmen.addAll(loadUnternehmenFromDatabase());
        freeIDsFromDatabase.addAll(loadUIDsFromDatabase());
    }

    public static Set<Unternehmen> getUnternehmen() {
        return Collections.unmodifiableSet(loadedUnternehmen);
    }

    public static Optional<Unternehmen> getUnternehmen(final long id) {
        for (final Unternehmen unternehmen : loadedUnternehmen) {
            if (unternehmen.getID() == id)
                return Optional.of(unternehmen);
        }
        return Optional.empty();
    }

    public static void addUnternehmen(final Unternehmen unternehmen) {
        Objects.requireNonNull(unternehmen);
        loadedUnternehmen.add(unternehmen);
    }

    public static void removeUnternehmen(final Unternehmen unternehmen) {
        Objects.requireNonNull(unternehmen);
        if (AnsprechpartnerDatabase.getAnsprechpartner().stream().map(Ansprechpartner::getOrganization).anyMatch(unternehmen::equals))
            System.err.println("Organization still in use!");
        else
            loadedUnternehmen.remove(unternehmen);
    }

    private static Set<Long> getUnternehmenIDsFromDatabase() {
        final Set<Long> ids = new HashSet<>();
        try {
            final ResultSet results = executeQuery(UnternehmenStringGenerator.GET_ALL_IDS, getConnection());
            while (results.next()) {
                ids.add(results.getLong("U_ID"));
            }
        } catch (final SQLException exception) {
            System.err.println(exception.getMessage());
        }
        return ids;
    }

    private static SequencedSet<Long> getFreeIDs() {
        freeIDsFromDatabase.addAll(loadUIDsFromDatabase());
        final SequencedSet<Long> usedIDs = new LinkedHashSet<>();
        loadedUnternehmen.stream().map(Unternehmen::getID).forEach(usedIDs::add);
        freeIDsFromDatabase.removeAll(usedIDs);
        return freeIDsFromDatabase;
    }

    public static Long getFreeID() {
        final SequencedSet<Long> ids = getFreeIDs();
        if (ids.isEmpty()) {
            executeStatement(UnternehmenStringGenerator.NEW_IDS);
            ids.addAll(getFreeIDs());
        }
        return ids.getFirst();
    }

    public static boolean checkIfUnternehmenExists(final long id) {
        return getUnternehmenIDsFromDatabase().contains(id);
    }

    private static Unternehmen fillUnternehmenWithResult(final ResultSet results) throws SQLException {
        Objects.requireNonNull(results);
        if (Unternehmen.getInstanceRef(results.getLong("U_ID")).isPresent())
            return null;
        return new Unternehmen(
                results.getLong("U_ID"),
                results.getString("Name"),
                results.getString("Adresse")
        );
    }

    private static Set<Unternehmen> loadUnternehmenFromDatabase() {
        final Set<Unternehmen> loadedUnternehmen = new HashSet<>();
        try (final ResultSet results = Database.executeQuery(UnternehmenStringGenerator.SELECT_ALL, Database.getConnection())) {
            while (results.next()) {
                final Unternehmen unternehmen = fillUnternehmenWithResult(results);
                if (unternehmen != null)
                    loadedUnternehmen.add(unternehmen);
            }
        } catch (final SQLException exception) {
            System.err.println("Unable to load Unternehmen!\n" + exception.getMessage());
        }
        return loadedUnternehmen;
    }

    private static Set<Long> loadUIDsFromDatabase() {
        final Set<Long> ids = new HashSet<>();
        try (final ResultSet results = Database.executeQuery(UnternehmenStringGenerator.FREE_IDS, Database.getConnection())) {
            while (results.next()) {
                ids.add(results.getLong("ID"));
            }
        } catch (final SQLException exception) {
            System.err.println("Unable to load U_IDs!\n" + exception.getMessage());
        }
        return ids;
    }

    public static Optional<Unternehmen> selectUnternehmenFromDatabase(final long id) {
        Optional<Unternehmen> selectedUnternehmen = Optional.empty();
        try {
            final ResultSet results = executeQuery(UnternehmenStringGenerator.selectUnternehmenString(id), getConnection());
            selectedUnternehmen = Optional.ofNullable(fillUnternehmenWithResult(results));
        } catch (final Exception exception) {
            System.err.println("Unable to select Unternehmen!\n" + exception.getMessage());
        }
        if (selectedUnternehmen.isEmpty())
            return Unternehmen.getInstanceRef(id);
        return selectedUnternehmen;
    }

    public static void removeDeletedUnternehmen() {
        for (final long id : getUnternehmenIDsFromDatabase())
            if (getUnternehmen(id).isEmpty())
                executeStatement(UnternehmenStringGenerator.deleteUnternehmenString(id));
    }

    public static void updateUnternehmen() {
        for (final Unternehmen unternehmen : getUnternehmen())
            update(unternehmen);
    }

    public static void insertNewUnternehmen() {
        for (final Unternehmen unternehmen : loadedUnternehmen)
            if (!checkIfUnternehmenExists(unternehmen.getID()))
                insert(unternehmen);
    }

    public static void saveUnternehmen() {
        insertNewUnternehmen();
        updateUnternehmen();
        removeDeletedUnternehmen();
    }
}
