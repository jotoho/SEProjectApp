package de.jotoho.fhswf.se.projectapp;

import static java.util.Objects.*;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unused")
public class Projekt {
    public enum Status {
        DRAFT,
        IN_REVIEW,
        APPROVED,
        DENIED
    }

    private static final int MAX_MEMBERS = 3;
    private final Set<Student> members = Collections.synchronizedSet(new HashSet<>(MAX_MEMBERS));
    private static final ConcurrentHashMap<Long, WeakReference<Projekt>> knownInstances =
            new ConcurrentHashMap<>();
    private final long projectID;
    private Ansprechpartner contact;
    private Status currentStatus = Status.DRAFT;
    private String title = "";
    private String textOutline = "";
    private String context = "";
    private String description = "";

    public synchronized boolean isSubmittable() {
        return !this.title.isBlank()
                && !this.textOutline.isBlank()
                && !this.context.isBlank()
                && !this.description.isBlank()
                && this.currentStatus == Status.DRAFT
                && !this.members.isEmpty()
                && this.members.size() <= MAX_MEMBERS
                && nonNull(this.contact);
    }

    public long getID() {
        return this.projectID;
    }

    public Set<Student> getMemberView() {
        return Collections.unmodifiableSet(this.members);
    }

    public synchronized void addMember(final Student student) {
        requireNonNull(student);
        if (this.members.size() < MAX_MEMBERS)
            this.members.add(student);
        else
            throw new IllegalStateException("Attempted to exceed max number of project members");
    }

    public synchronized void removeMembers(final Student student) {
        requireNonNull(student);
        this.members.remove(student);
    }

    public synchronized void setStatus(final Status status) {
        requireNonNull(status);
        this.currentStatus = status;
    }

    public synchronized Status getStatus() {
        return this.currentStatus;
    }

    public synchronized void setTitle(final String title) {
        requireNonNull(title);
        this.title = title.strip();
    }

    public synchronized String getTitle() {
        return this.title;
    }

    public synchronized void setTextOutline(final String textOutline) {
        requireNonNull(textOutline);
        this.textOutline = textOutline.strip();
    }

    public synchronized String getTextOutline() {
        return this.textOutline;
    }

    public synchronized void setContext(final String context) {
        requireNonNull(context);
        this.context = context.strip();
    }

    public synchronized String getContext() {
        return this.context;
    }

    public synchronized void setDescription(final String description) {
        requireNonNull(description);
        this.description = description.strip();
    }

    public synchronized String getDescription() {
        return this.description;
    }

    public synchronized void setContact(final Ansprechpartner contact) {
        this.contact = contact;
    }

    public synchronized Optional<Ansprechpartner> getContact() {
        return Optional.ofNullable(this.contact);
    }

    public static Optional<Projekt> getInstanceRef(final long projectID) {
        return Optional.ofNullable(knownInstances.getOrDefault(projectID,
                                                               new WeakReference<>(null)).get());
    }

    public static void flushDeadInstanceRecords() {
        knownInstances.forEachEntry(1, entry -> {
            final var key = entry.getKey();
            final var val = entry.getValue();
            if (isNull(val) || isNull(val.get()))
                knownInstances.remove(key, val);
        });
    }

    public Projekt(final long projectID) {
        this.projectID = projectID;

        final var instance = this;
        Thread.ofVirtual().start(Projekt::flushDeadInstanceRecords);
        knownInstances.compute(projectID, (uid, uWeakRef) -> {
            if (isNull(uWeakRef))
                return new WeakReference<>(instance);
            final var weakRefContent = uWeakRef.get();
            return isNull(weakRefContent) ? new WeakReference<>(instance) : uWeakRef;
        });

        if (knownInstances.get(projectID).get() != this)
            throw new IllegalStateException();
    }
}
