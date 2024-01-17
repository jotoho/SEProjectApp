package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.Projekt.Status;
import de.jotoho.fhswf.se.projectapp.Student;
import de.jotoho.fhswf.se.projectapp.Unternehmen;
import de.jotoho.fhswf.se.projectapp.database.Database;
import de.jotoho.fhswf.se.projectapp.ui.OptionSelectionMenu.Option;
import static de.jotoho.fhswf.se.projectapp.ui.TerminalInputUtil.getTextBlock;
import static de.jotoho.fhswf.se.projectapp.ui.TextFormatUtil.prepareText;
import static java.util.Collections.unmodifiableSequencedSet;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;

public class ProjektEditMenu {
    @Unmodifiable
    @NotNull
    private static final SequencedSet<Option<AttributeSelector>> ATTR_OPTIONS;

    static {
        final SequencedSet<Option<AttributeSelector>> data = new LinkedHashSet<>();
        data.addLast(new Option<>("Titel",
                                  Set.of("titel", "title"),
                                  true,
                                  AttributeSelector.TITLE,
                                  null));
        data.addLast(new Option<>("Textumriss",
                                  Set.of("umriss", "outline"),
                                  true,
                                  AttributeSelector.TEXT_OUTLINE,
                                  null));
        data.addLast(new Option<>("Projektkontext",
                                  Set.of("kontext", "context"),
                                  true,
                                  AttributeSelector.CONTEXT,
                                  null));
        data.addLast(new Option<>("Projektbeschreibung",
                                  Set.of("beschreibung"),
                                  true,
                                  AttributeSelector.DESCRIPTION,
                                  null));
        data.addLast(new Option<>("Neuen Studenten hinzufügen",
                                  Set.of("weitererstudent"),
                                  true,
                                  AttributeSelector.ADD_STUDENTS,
                                  null));
        data.addLast(new Option<>("Neuen Studenten entfernen",
                                  Set.of("ohnestudent"),
                                  true,
                                  AttributeSelector.REMOVE_STUDENTS,
                                  null));
        data.addLast(new Option<>("Für Review einsenden",
                                  Set.of("einsenden", "review"),
                                  true,
                                  AttributeSelector.STATUS,
                                  null));
        data.addLast(new Option<>("Beenden", Set.of("beenden"), true, null, null));

        ATTR_OPTIONS = unmodifiableSequencedSet(data);
    }

    private ProjektEditMenu() {
    }

    public static void main(final String[] args) {
        final var s = new Student(3, "Jonas Tobias", "Hopusch", "hopusch.jonastobias@fh-swf.de");
        final var s2 = new Student(2, "Tim", "Beckmann", "beckmann.tim@fh-swf.de");
        Database.addStudent(s);
        Database.addStudent(s2);
        final var o = new Unternehmen(4, "Fachhochschule Südwestfalen", "Iserlohn");
        final var a = new Ansprechpartner(5, "Uwe", "Klug", o);
        final var p = new Projekt(15);
        p.setTitle("Testprojekt");
        p.setTextOutline("Muahahahahaah" + "\nnundiuuifnjboaqqqqqqfwobfsafsssaffffffffffff" +
                         "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
                         false);
        p.addMember(s);
        p.setContact(a);
        editProjekt(p);
    }

    public static void editProjekt(final Projekt projekt) {
        requireNonNull(projekt);
        Optional<Option<AttributeSelector>> selection = Optional.empty();
        boolean firstTime = true;
        while (firstTime || selection.map(Option::get).isPresent()) {
            listProjekt(projekt);
            firstTime = false;
            final OptionSelectionMenu<AttributeSelector> menu = new OptionSelectionMenu<>(
                    "Welches Attribut möchten Sie bearbeiten" + "? ('beenden' zum beenden)");
            ATTR_OPTIONS.forEach(menu::addOption);
            menu.activate();

            selection = menu.getSelectedOption();
            selection.ifPresent(option -> {
                final var attr = option.get();
                if (nonNull(attr))
                    editProjektAttribute(projekt, attr);
            });
        }
    }

    public static void listProjekt(final Projekt projekt) {
        System.out.println("=== Informationen zu Projekt #" + projekt.getID() + " ===");
        System.out.print("Titel: ");
        System.out.println(projekt.getTitle());
        System.out.println("Status: " + switch (projekt.getStatus()) {
            case DRAFT -> "Entwurf";
            case IN_REVIEW -> "In Prüfung";
            case APPROVED -> "Genehmigt";
            case DENIED -> "Abgelehnt";
        });
        System.out.println("Studenten:");
        projekt.getMemberView()
               .stream()
               .sorted(Comparator.comparingLong(Student::getStudentID))
               .map(Student::toString)
               .map(s -> s.indent(2))
               .map(String::stripTrailing)
               .forEach(System.out::println);
        System.out.print("Ansprechpartner: ");
        System.out.println(projekt.getContact().map(Ansprechpartner::toString).orElse("unbekannt"));
        System.out.println("Textumriss:");
        System.out.println(prepareText(projekt.getTextOutline(), 70, 2));
        System.out.println("Projektkontext:");
        System.out.println(prepareText(projekt.getContext(), 70, 2));
        System.out.println("Beschreibung:");
        System.out.println(prepareText(projekt.getDescription(), 70, 2));
        System.out.println("Feedback:");
        System.out.println(prepareText(projekt.getFeedback(), 70, 2));
        if (projekt.isSubmittable())
            System.out.println("Dieses Projekt kann zum Review eingesendet werden.");
        else
            System.out.println("Dieses Projekt kann nicht eingesendet werden.");
    }

    public static void editProjektAttribute(@NotNull final Projekt projekt,
                                            @NotNull final AttributeSelector attribute)
    {
        requireNonNull(projekt);
        requireNonNull(attribute);
        switch (attribute) {
            case TITLE -> {
                System.out.println("Geben Sie bitte den neuen Projekttitel an.");
                while (true) {
                    System.out.print("Neuer Titel: ");
                    final var input = TerminalInputUtil.getNextLine().orElse("");
                    try {
                        projekt.setTitle(input);
                        break;
                    }
                    catch (final IllegalArgumentException ignored) {

                    }
                }
            }
            case TEXT_OUTLINE -> {
                System.out.println("Geben Sie bitte die neue Textskizze ein. (ENDE zum stoppen)");
                final var input = getTextBlock("ENDE");
                if (Projekt.validateTextOutline(input))
                    projekt.setTextOutline(input);
                else
                    System.err.println("Eingabe für Textskizze ist ungültig.");
            }
            case CONTEXT -> {
                System.out.println(
                        "Geben Sie bitte die neue Projektkontext ein. (ENDE zum " + "stoppen)");
                final var input = getTextBlock("ENDE");
                if (Projekt.validateContext(input))
                    projekt.setContext(input);
                else
                    System.err.println("Eingabe für Projektkontext ist ungültig.");
            }
            case DESCRIPTION -> {
                System.out.println("Geben Sie bitte die neue Projektbeschreibung ein. (ENDE zum " +
                                   "stoppen)");
                final var input = getTextBlock("ENDE");
                if (Projekt.validateDescription(input))
                    projekt.setDescription(input);
                else
                    System.err.println("Eingabe für Projektbeschreibung ist ungültig.");
            }
            case ADD_STUDENTS -> {
                final OptionSelectionMenu<Student> studentSelect =
                        new OptionSelectionMenu<>("Wählen Sie einen Studenten aus.");
                final Consumer<Student> optionAdder = student -> {
                    studentSelect.addOption(new Option<>(student.toString(),
                                                         Set.of(Long.toUnsignedString(student.getStudentID()),
                                                                student.getFamilyName()
                                                                       .toLowerCase()),
                                                         false,
                                                         student,
                                                         null));
                };
                Database.getStudents()
                        .stream()
                        .filter(not(projekt.getMemberView()::contains))
                        .forEach(optionAdder);
                studentSelect.addOption(new Option<>("Neuer Student",
                                                     Set.of("neu"),
                                                     false,
                                                     null,
                                                     ProjektEditMenu::createStudentWizard));
                studentSelect.addOption(new Option<>("Abbrechen",
                                                     Set.of("abbrechen", "abbruch"),
                                                     false,
                                                     null,
                                                     null));
                studentSelect.activate();
                @Nullable
                final Student student =
                        studentSelect.getSelectedOption().map(Option::get).orElse(null);
                if (nonNull(student))
                    projekt.addMember(student);
            }
            case REMOVE_STUDENTS -> {
                final OptionSelectionMenu<Student> studentSelect =
                        new OptionSelectionMenu<>("Wählen Sie einen Studenten aus.");
                final Consumer<Student> optionAdder = student -> {
                    studentSelect.addOption(new Option<>(student.toString(),
                                                         Set.of(Long.toUnsignedString(student.getStudentID()),
                                                                student.getFamilyName()
                                                                       .toLowerCase()),
                                                         false,
                                                         student,
                                                         null));
                };

                projekt.getMemberView().forEach(optionAdder);
                studentSelect.addOption(new Option<>("Abbrechen",
                                                     Set.of("abbrechen", "abbruch"),
                                                     false,
                                                     null,
                                                     null));
                studentSelect.activate();
                @Nullable
                final Student student =
                        studentSelect.getSelectedOption().map(Option::get).orElse(null);
                if (nonNull(student))
                    projekt.removeMember(student);
            }
            case CONTACTPERSON -> {

            }
            case STATUS -> {
                if (projekt.isSubmittable())
                    projekt.setStatus(Status.IN_REVIEW);
                else
                    System.err.println("Projekt is not suitable for review.");
            }
        }
    }

    // TODO Implement on-the-fly student creation
    private static Student createStudentWizard() {
        return null;
    }

    public enum AttributeSelector {
        TITLE,
        TEXT_OUTLINE,
        CONTEXT,
        DESCRIPTION,
        ADD_STUDENTS,
        REMOVE_STUDENTS,
        CONTACTPERSON,
        STATUS
    }
}