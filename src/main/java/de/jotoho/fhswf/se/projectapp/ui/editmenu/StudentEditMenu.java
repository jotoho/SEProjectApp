package de.jotoho.fhswf.se.projectapp.ui.editmenu;

import de.jotoho.fhswf.se.projectapp.Student;
import de.jotoho.fhswf.se.projectapp.ui.menu.OptionSelectionMenu;
import de.jotoho.fhswf.se.projectapp.ui.menu.OptionSelectionMenu.Option;
import static de.jotoho.fhswf.se.projectapp.ui.TerminalInputUtil.getNextLine;
import static java.util.Objects.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class StudentEditMenu {
    private static final Pattern emailPattern = Pattern.compile(
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+" + "(?:\\.[a-z0-9!#$%&'*+/=?^_" + "`{|}~-]+)*|\"" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e" + "-\\x1f\\x21\\x23-\\x5b\\x5d" +
            "-\\x7f]|\\\\[\\x01-\\x09\\x0b" + "\\x0c\\x0e-\\x7f])*\")@(?:" +
            "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9" + "])?\\.)+[a-z0-9]" +
            "(?:[a-z0-9-]*[a-z0-9])?|\\[(?:" + "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9" +
            "][0-9]?)\\.){3}" + "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9" + "][0-9]?|[a-z0-9-]*[a-z0-9]:" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e" + "-\\x1f\\x21-\\x5a\\x53-\\x7f" +
            "]|\\\\[\\x01-\\x09\\x0b\\x0c" + "\\x0e-\\x7f])+)\\])");

    private static final Pattern familyNamePattern =
            Pattern.compile("[a-z \\-]{2,30}", Pattern.CASE_INSENSITIVE);
    private static final Pattern firstNamePattern =
            Pattern.compile("[a-z \\-]{2,30}", Pattern.CASE_INSENSITIVE);

    private StudentEditMenu() {
    }

    public static void main(final String[] args) {
        final var student =
                new Student(30271232, "Jonas Tobias", "Hopusch", "hopusch.jonastobias@fh-swf.de");
        StudentEditMenu.editStudent(student);
        StudentEditMenu.listStudent(student, System.out);
    }

    public static void editStudent(final Student student) {
        requireNonNull(student);
        final ArrayList<Option<AttributeSelector>> attributes = new ArrayList<>();
        Optional<Option<AttributeSelector>> selection = Optional.empty();
        boolean firstTime = true;
        while (firstTime || selection.map(Option::get).isPresent()) {
            listStudent(student);
            firstTime = false;
            final OptionSelectionMenu<AttributeSelector> menu = new OptionSelectionMenu<>(
                    "Welches Attribut möchten Sie bearbeiten" + "? ('beenden' zum beenden)");
            menu.addOption(new Option<>("Nachname",
                                        Set.of("nachname"),
                                        true,
                                        AttributeSelector.FAMILY_NAME,
                                        null));
            menu.addOption(new Option<>("Vorname",
                                        Set.of("vorname"),
                                        true,
                                        AttributeSelector.FIRST_NAME,
                                        null));
            menu.addOption(new Option<>("Emailadresse",
                                        Set.of("email"),
                                        true,
                                        AttributeSelector.EMAIL,
                                        null));
            menu.addOption(new Option<>("Beenden", Set.of("beenden"), true, null, null));
            menu.activate();

            selection = menu.getSelectedOption();
            if (selection.isPresent() && selection.map(Option::get).isPresent()) {
                editStudentAttribute(student, selection.get().get());
            }
        }
    }

    /**
     * For API backwards-compatibility only.
     *
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public static void listStudent(final Student student, final PrintStream unused) {
        listStudent(student);
    }

    public static void listStudent(final Student student) {
        System.out.println("== Studenteninformation ==");
        System.out.print("Matrikelnummer: ");
        System.out.println(student.getStudentID());
        System.out.print("Nachname: ");
        System.out.println(student.getFamilyName());
        System.out.print("Vorname: ");
        System.out.println(student.getFirstName());
        System.out.print("Emailadresse: ");
        System.out.println(student.getEmailAddr().orElse("nicht vorhanden"));
    }

    public static void editStudentAttribute(final Student student,
                                            final AttributeSelector attribute)
    {
        requireNonNull(student);
        requireNonNull(attribute);
        final var scanner = new Scanner(System.in);
        switch (attribute) {
            case FAMILY_NAME -> {
                System.out.println(
                        "Geben Sie einen neuen Nachnamen für Studenten " + student.getStudentID() +
                        " ein.");
                String newName = null;
                while (isNull(newName) || newName.isBlank() ||
                       !familyNamePattern.matcher(newName).matches()) {
                    System.out.print("Neuer Nachname: ");
                    newName = getNextLine().orElse(null);
                }
                student.setFamilyName(newName);
            }
            case FIRST_NAME -> {
                System.out.println(
                        "Geben Sie einen neuen Vornamen für Studenten " + student.getStudentID() +
                        " ein.");
                String newName = null;
                while (isNull(newName) || newName.isBlank() ||
                       !firstNamePattern.matcher(newName).matches()) {
                    System.out.print("Neuer Vorname: ");
                    newName = getNextLine().orElse(null);
                }
                student.setFirstName(newName);
            }
            case EMAIL -> {
                System.out.println(
                        "Geben Sie eine neue Emailadresse für Studenten " + student.getStudentID() +
                        " ein.");
                String newName = null;
                while (isNull(newName) || newName.isBlank() || newName.length() > 90 ||
                       !emailPattern.matcher(newName).matches()) {
                    System.out.print("Neue Email: ");
                    newName = getNextLine().orElse(null);
                    if (nonNull(newName) && newName.isBlank()) {
                        student.setEmail(null);
                        return;
                    }
                }
                student.setEmail(newName);
            }
        }
    }

    public enum AttributeSelector {
        FAMILY_NAME,
        FIRST_NAME,
        EMAIL
    }
}
