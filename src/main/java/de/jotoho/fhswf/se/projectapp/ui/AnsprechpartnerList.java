package de.jotoho.fhswf.se.projectapp.ui;

import de.jotoho.fhswf.se.projectapp.Ansprechpartner;
import de.jotoho.fhswf.se.projectapp.Unternehmen;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;

@SuppressWarnings("unused")
public final class AnsprechpartnerList {
    private AnsprechpartnerList() {

    }

    public static String getFormatted(final Collection<Ansprechpartner> ansprechpartner) {
        requireNonNull(ansprechpartner);
        final int maxLengthID = ansprechpartner.parallelStream()
                                               .mapToLong(Ansprechpartner::getID)
                                               .mapToObj(Long::toUnsignedString)
                                               .mapToInt(String::length)
                                               .max()
                                               .orElse(0);
        final int maxLengthOrg = ansprechpartner.parallelStream()
                                                .map(Ansprechpartner::getOrganization)
                                                .map(Unternehmen::getName)
                                                .mapToInt(String::length)
                                                .max()
                                                .orElse(0);
        final int maxLengthFamilyName = ansprechpartner.parallelStream()
                                                       .map(Ansprechpartner::getFamilyName)
                                                       .mapToInt(String::length)
                                                       .max()
                                                       .orElse(0);
        final int maxLengthFirstName = ansprechpartner.parallelStream()
                                                      .map(Ansprechpartner::getFirstName)
                                                      .mapToInt(String::length)
                                                      .max()
                                                      .orElse(0);

        final StringBuilder result = new StringBuilder();
        ansprechpartner.stream()
                       .sorted(Comparator.comparingLong(Ansprechpartner::getID))
                       .forEachOrdered(a -> {
            final String sID = Long.toUnsignedString(a.getID(), 10);
            final String sFamilyName = a.getFamilyName();
            final String sFirstName = a.getFirstName();
            final String sOrganization = a.getOrganization()
                                          .getName();

            result.repeat(" ", maxLengthID - sID.length())
                  .append(sID)
                  .repeat(" ", maxLengthOrg - sOrganization.length() + 1)
                  .append(sOrganization)
                  .repeat(" ", maxLengthFirstName - sFirstName.length() + 1)
                  .append(sFirstName)
                  .repeat(" ", maxLengthFamilyName - sFamilyName.length() + 1)
                  .append(sFamilyName)
                  .append("\n");
        });

        return result.toString();
    }

    public static void main(final String[] args) {
        final Random rng = new Random();
        final ArrayList<Ansprechpartner> list = new ArrayList<>();
        final var orgA = new Unternehmen(rng.nextInt(1_000_000), "OrgA");
        final var orgB = new Unternehmen(rng.nextInt(1_000_000), "OrgB");
        final var personA =
                new Ansprechpartner(rng.nextInt(1_000_000),
                                    "Max",
                                    "Mustermann",
                                    orgA);
        final var personB =
                new Ansprechpartner(rng.nextInt(1_000_000),
                                    "Emilia",
                                    "Musterfrau",
                                    orgB);
        list.add(personA);
        list.add(personB);


        System.out.println(getFormatted(list));
    }
}
