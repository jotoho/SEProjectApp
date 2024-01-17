package de.jotoho.fhswf.se.projectapp;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class Ansprechpartner {
    private static final ConcurrentHashMap<Long, WeakReference<Ansprechpartner>> knownInstances =
            new ConcurrentHashMap<>();
    private final long ansprechpartnerID;
    private String firstName;
    private String familyName;
    private Unternehmen organization;

    public static Optional<Ansprechpartner> getInstanceRef(final long ansprechpartnerID) {
        return Optional.ofNullable(knownInstances.getOrDefault(ansprechpartnerID,
                                                               new WeakReference<>(null))
                                                 .get());
    }

    public long getID() {
        return this.ansprechpartnerID;
    }

    public synchronized String getFirstName() {
        return this.firstName;
    }

    public synchronized void setFirstName(final String name) {
        requireNonNull(name);
        if (name.isBlank())
            throw new IllegalArgumentException();
        else
            this.firstName = name.strip();
    }

    public synchronized String getFamilyName() {
        return this.familyName;
    }

    public synchronized void setFamilyName(final String name) {
        requireNonNull(name);
        if (name.isBlank())
            throw new IllegalArgumentException();
        else
            this.familyName = name.strip();
    }

    public synchronized void setOrganization(final Unternehmen organization) {
        this.organization = requireNonNull(organization);
    }

    public synchronized Unternehmen getOrganization() {
        return this.organization;
    }

    public static void flushDeadInstanceRecords() {
        knownInstances.forEachEntry(1, entry -> {
            final var key = entry.getKey();
            final var val = entry.getValue();
            if (isNull(val) || isNull(val.get()))
                knownInstances.remove(key, val);
        });
    }

    public Ansprechpartner(final long ansprechpartnerID,
                           final String firstName,
                           final String familyName,
                           final Unternehmen organization) {
        this.ansprechpartnerID = ansprechpartnerID;
        this.setFirstName(firstName);
        this.setFamilyName(familyName);
        this.setOrganization(organization);

        final var instance = this;
        Thread.ofVirtual().start(Projekt::flushDeadInstanceRecords);
        knownInstances.compute(ansprechpartnerID, (uid, uWeakRef) -> {
            if (isNull(uWeakRef))
                return new WeakReference<>(instance);
            final var weakRefContent = uWeakRef.get();
            return isNull(weakRefContent) ? new WeakReference<>(instance) : uWeakRef;
        });

        if (knownInstances.get(ansprechpartnerID).get() != this)
            throw new IllegalStateException();
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.familyName + " (" + this.organization.getName() + ")";
    }
}
