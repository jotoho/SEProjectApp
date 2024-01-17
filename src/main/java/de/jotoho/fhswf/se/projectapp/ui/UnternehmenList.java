package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Unternehmen;
import static java.util.Objects.requireNonNull;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class UnternehmenList {
    private UnternehmenList() {

    }

    public static String getFormatted(final Collection<Unternehmen> unternehmen) {
        requireNonNull(unternehmen);
        final int maxLengthID = unternehmen.parallelStream()
                                           .mapToLong(Unternehmen::getID)
                                           .mapToObj(Long::toUnsignedString)
                                           .mapToInt(String::length)
                                           .max()
                                           .orElse(0);
        final int maxLengthName = unternehmen.parallelStream()
                                             .map(Unternehmen::getName)
                                             .mapToInt(String::length)
                                             .max()
                                             .orElse(0);

        final StringBuilder result = new StringBuilder();
        final Consumer<Unternehmen> printFn = organization -> {
            final String sID = Long.toUnsignedString(organization.getID(), 10);
            final String sName = organization.getName();

            result.repeat(" ", maxLengthID - sID.length())
                  .append(sID)
                  .repeat(" ", maxLengthName - sName.length() + 1)
                  .append(sName)
                  .append("\n");
        };
        unternehmen.stream()
                   .sorted(Comparator.comparingLong(Unternehmen::getID))
                   .forEachOrdered(printFn);
        return result.toString();
    }

    public static void main(final String[] args) {
        final Random rng = new Random();
        final var orgs = new HashSet<Unternehmen>();
        orgs.add(new Unternehmen(rng.nextInt(1_000_000), "Organization A","Teststraße.1"));
        orgs.add(new Unternehmen(rng.nextInt(1_000_000), "Organization B","Teststraße.2"));
        orgs.add(new Unternehmen(rng.nextInt(1_000_000), "Organization C","Teststraße.3"));
        orgs.add(new Unternehmen(rng.nextInt(1_000_000), "Organization D","Teststraße.4"));
        System.out.println(getFormatted(orgs));
    }
}
