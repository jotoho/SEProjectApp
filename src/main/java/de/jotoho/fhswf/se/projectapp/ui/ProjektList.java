package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Projekt;
import de.jotoho.fhswf.se.projectapp.Student;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class ProjektList {
    private ProjektList() {

    }

    public static String getFormatted(final Collection<Projekt> projects) {
        requireNonNull(projects);
        final int maxLengthID = projects.parallelStream()
                                        .mapToLong(Projekt::getID)
                                        .mapToObj(Long::toUnsignedString)
                                        .mapToInt(String::length)
                                        .max()
                                        .orElse(0);
        final int maxLengthTitle = projects.parallelStream()
                                           .map(Projekt::getTitle)
                                           .filter(Objects::nonNull)
                                           .filter(not(String::isBlank))
                                           .mapToInt(String::length)
                                           .max()
                                           .orElse(0);
        final Function<Set<Student>, Set<String>> nameExtractor =
                s -> s.parallelStream()
                      .map(Student::getFamilyName)
                      .collect(Collectors.toUnmodifiableSet());
        final int maxLengthNames = projects.parallelStream()
                                           .map(Projekt::getMemberView)
                                           .map(nameExtractor)
                                           .map(Set::toString)
                                           .mapToInt(String::length)
                                           .max()
                                           .orElse(0);

        final StringBuilder result = new StringBuilder();
        final Consumer<Projekt> printFn = p -> {
            final String sID = Long.toUnsignedString(p.getID());
            final String sTitle = p.getTitle()
                                   .strip();
            final String sMembers = nameExtractor.apply(p.getMemberView())
                                                 .toString();

            result.repeat(" ", maxLengthID - sID.length())
                  .append(sID)
                  .repeat(" ", maxLengthTitle - sTitle.length() + 1)
                  .append(sTitle)
                  .repeat(" ", maxLengthNames - sMembers.length() + 1)
                  .append(sMembers)
                  .append("\n");
        };

        projects.parallelStream()
                .sorted(Comparator.comparingLong(Projekt::getID))
                .forEachOrdered(printFn);

        return result.toString();
    }

    public static void main(final String[] args) {
        final var s0 = new Student(0, "Jonas Tobias", "Hopusch");
        final var s1 = new Student(1, "Tim", "Beckmann");

        final var p0 = new Projekt(0);
        p0.setTitle("Test project #1");
        p0.addMember(s0);
        p0.addMember(s1);
        final var p1 = new Projekt(1);
        p1.setTitle("Test project #2");
        final var p2 = new Projekt(2);
        p2.setTitle("Test project #3");
        final Set<Projekt> projects = Set.of(p0, p1, p2);
        System.out.println(getFormatted(projects));
    }
}
