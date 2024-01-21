package de.jotoho.fhswf.se.projectapp.backend.database;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.Unternehmen;
import de.jotoho.fhswf.se.projectapp.backend.stringgenerator.AnsprechpartnerStringGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static de.jotoho.fhswf.se.projectapp.backend.database.Database.*;

@SuppressWarnings("unused")
public final class AnsprechpartnerDatabase {
    private static final Set<Ansprechpartner> loadedAnsprechpartner = new HashSet<>();
    private static final SequencedSet<Long> freeIDsFromDatabase = new LinkedHashSet<>();

    private AnsprechpartnerDatabase() {
    }

    public static void initAnsprechpartner() {
        loadedAnsprechpartner.addAll(loadAnsprechpartnerFromDatabase());
        freeIDsFromDatabase.addAll(loadAIDsFromDatabase());
    }

    public static Set<Ansprechpartner> getAnsprechpartner() {
        return Collections.unmodifiableSet(loadedAnsprechpartner);
    }

    public static Optional<Ansprechpartner> getAnsprechpartner(final long id) {
        for (final Ansprechpartner ansprechpartner : loadedAnsprechpartner) {
            if (ansprechpartner.getID() == id)
                return Optional.of(ansprechpartner);
        }
        return Optional.empty();
    }

    private static Set<Long> getAnsprechpartnerIDsFromDatabase() {
        final Set<Long> ids = new HashSet<>();
        try {
            final ResultSet results = executeQuery(AnsprechpartnerStringGenerator.GET_ALL_IDS, getConnection());
            while (results.next()) {
                ids.add(results.getLong("A_ID"));
            }
        } catch (final SQLException exception) {
            System.err.println(exception.getMessage());
        }
        return ids;
    }

    private static SequencedSet<Long> getFreeIDs() {
        freeIDsFromDatabase.addAll(loadAIDsFromDatabase());
        final SequencedSet<Long> usedIDs = new LinkedHashSet<>();
        loadedAnsprechpartner.stream().map(Ansprechpartner::getID).forEach(usedIDs::add);
        freeIDsFromDatabase.removeAll(usedIDs);
        return freeIDsFromDatabase;
    }

    public static Long getFreeID() {
        final SequencedSet<Long> ids = getFreeIDs();
        if (ids.isEmpty()) {
            executeStatement(AnsprechpartnerStringGenerator.NEW_IDS);
            ids.addAll(getFreeIDs());
        }
        return ids.getFirst();
    }

    public static void addAnsprechpartner(final Ansprechpartner ansprechpartner) {
        Objects.requireNonNull(ansprechpartner);
        loadedAnsprechpartner.add(ansprechpartner);
    }

    public static void removeAnsprechpartner(final Ansprechpartner ansprechpartner) {
        Objects.requireNonNull(ansprechpartner);
        if (ProjektDatabase .getProjekte().stream()
                            .map(Projekt::getContact)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .anyMatch(ansprechpartner::equals))
            System.err.println("Contact still in use!");
        else
            loadedAnsprechpartner.remove(ansprechpartner);
    }

    public static boolean checkIfAnsprechpartnerExists(final long id) {
        return getAnsprechpartnerIDsFromDatabase().contains(id);
    }

    private static Ansprechpartner fillAnsprechpartnerWithResult(final ResultSet results) throws SQLException {
        Objects.requireNonNull(results);
        if (Ansprechpartner.getInstanceRef(results.getLong("A_ID")).isPresent())
            return null;

        final Optional<Unternehmen> arbeitgeber = UnternehmenDatabase.getUnternehmen(results.getLong("Arbeitgeber"));
        if (arbeitgeber.isEmpty())
            return null;
        return new Ansprechpartner(
                results.getLong("A_ID"),
                results.getString("Vorname"),
                results.getString("Nachname"),
                arbeitgeber.get()
        );
    }

    private static Set<Ansprechpartner> loadAnsprechpartnerFromDatabase() {
        final Set<Ansprechpartner> loadedAnsprechpartner = new HashSet<>();
        try (final ResultSet results = Database.executeQuery(AnsprechpartnerStringGenerator.SELECT_ALL, Database.getConnection())) {
            while (results.next()) {
                final Ansprechpartner ansprechpartner = fillAnsprechpartnerWithResult(results);
                if (ansprechpartner != null)
                    loadedAnsprechpartner.add(ansprechpartner);
            }
        } catch (final SQLException exception) {
            System.err.println("Unable to load Ansprechpartner!\n" + exception.getMessage());
        }
        return loadedAnsprechpartner;
    }

    private static Set<Long> loadAIDsFromDatabase() {
        final Set<Long> ids = new HashSet<>();
        try (final ResultSet results = Database.executeQuery(AnsprechpartnerStringGenerator.FREE_IDS, Database.getConnection())) {
            while (results.next()) {
                ids.add(results.getLong("ID"));
            }
        } catch (final SQLException exception) {
            System.err.println("Unable to load A_IDs!\n" + exception.getMessage());
        }
        return ids;
    }

    public static void removeDeletedAnsprechpartner() {
        for (final long id : getAnsprechpartnerIDsFromDatabase())
            if (getAnsprechpartner(id).isEmpty())
                executeStatement(AnsprechpartnerStringGenerator.deleteAnsprechpartnerString(id));
    }

    public static void updateAnsprechpartner() {
        for (final Ansprechpartner ansprechpartner : getAnsprechpartner())
            update(ansprechpartner);
    }

    public static void insertNewAnsprechpartner() {
        for (final Ansprechpartner ansprechpartner : loadedAnsprechpartner)
            if (!checkIfAnsprechpartnerExists(ansprechpartner.getID()))
                insert(ansprechpartner);
    }

    public static void saveAnsprechpartner() {
        insertNewAnsprechpartner();
        updateAnsprechpartner();
        removeDeletedAnsprechpartner();
    }
}
