package de.jotoho.fhswf.se.projectapp;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Projekt {
    private static final int MAX_MEMBERS = 3;
    @NotNull
    private static final Pattern regexTitle = Pattern.compile("^[ \\x21-\\x7Eäöüß]{3,50}$",
                                                              Pattern.CASE_INSENSITIVE);
    @NotNull
    private static final Pattern regexContext = Pattern.compile("^(.|\\s){1,1000}$",
                                                                Pattern.CASE_INSENSITIVE);
    @NotNull
    private static final Pattern regexTextOutline = Pattern.compile("^(.|\\s){1,1000}$",
                                                                    Pattern.CASE_INSENSITIVE);
    @NotNull
    private static final Pattern regexDescription = Pattern.compile("^(.|\\s){1,5000}$",
                                                                    Pattern.CASE_INSENSITIVE);
    @NotNull
    private static final Pattern regexFeedback = Pattern.compile("^(.|\\s){1,1000}$",
                                                                 Pattern.CASE_INSENSITIVE);

    @NotNull
    private static final ConcurrentHashMap<Long, WeakReference<Projekt>> knownInstances =
            new ConcurrentHashMap<>();
    @NotNull
    private final Set<Student> members = Collections.synchronizedSet(new HashSet<>(MAX_MEMBERS));
    @Range(from = 0,
           to = Long.MAX_VALUE)
    private final long projectID;
    @NotNull
    private Optional<Ansprechpartner> contact = Optional.empty();
    @NotNull
    private Status currentStatus = Status.DRAFT;
    @NotNull
    private String title = "";
    @NotNull
    private String textOutline = "";
    @NotNull
    private String context = "";
    @NotNull
    private String description = "";
    @NotNull
    private String feedback = "";

    public Projekt(@Range(from = 0,
                          to = Long.MAX_VALUE) final long projectID)
    {
        if (projectID < 0)
            throw new IllegalArgumentException("Project id is out of bounds");

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

    public static void flushDeadInstanceRecords() {
        knownInstances.forEachEntry(1, entry -> {
            final var key = entry.getKey();
            final var val = entry.getValue();
            if (isNull(val) || isNull(val.get()))
                knownInstances.remove(key, val);
        });
    }

    @NotNull
    public static Optional<Projekt> getInstanceRef(final long projectID) {
        return Optional.ofNullable(knownInstances.getOrDefault(projectID, new WeakReference<>(null))
                                                 .get());
    }

    public synchronized boolean isSubmittable() {
        return this.validateTitle() && this.validateTextOutline() && this.validateContext() &&
               this.validateDescription() && this.currentStatus == Status.DRAFT &&
               !this.members.isEmpty() && this.members.size() <= MAX_MEMBERS &&
               this.contact.isPresent();
    }

    public synchronized boolean validateTitle() {
        return validateTitle(this.title);
    }

    public synchronized boolean validateTextOutline() {
        return validateTextOutline(this.textOutline);
    }

    public synchronized boolean validateContext() {
        return validateContext(this.context);
    }

    public synchronized boolean validateDescription() {
        return validateDescription(this.description);
    }

    public synchronized boolean validateFeedback() {
        return validateFeedback(this.feedback);
    }

    public static boolean validateTitle(@Nullable final String title) {
        if (isNull(title) || title.isBlank())
            return false;

        return regexTitle.matcher(title)
                         .matches();
    }

    public static boolean validateTextOutline(@Nullable final String textOutline) {
        if (isNull(textOutline) || textOutline.isBlank())
            return false;

        return regexTextOutline.matcher(textOutline)
                               .matches();
    }

    public static boolean validateContext(@Nullable final String context) {
        if (isNull(context) || context.isBlank())
            return false;

        return regexContext.matcher(context)
                           .matches();

    }

    public static boolean validateDescription(@Nullable final String description) {
        if (isNull(description) || description.isBlank())
            return false;

        return regexDescription.matcher(description)
                               .matches();
    }

    public static boolean validateFeedback(@Nullable final String feedback) {
        if (isNull(feedback) || feedback.isBlank())
            return false;

        return regexFeedback.matcher(feedback)
                            .matches();
    }

    @Range(from = 0,
           to = Long.MAX_VALUE)
    public long getID() {
        return this.projectID;
    }

    @UnmodifiableView
    @NotNull
    public Set<Student> getMemberView() {
        return Collections.unmodifiableSet(this.members);
    }

    public synchronized void addMember(@NotNull final Student student) {
        requireNonNull(student);
        if (this.members.size() < MAX_MEMBERS)
            this.members.add(student);
        else
            throw new IllegalStateException("Attempted to exceed max number of project members");
    }

    public synchronized void removeMember(@NotNull final Student student) {
        requireNonNull(student);
        this.members.remove(student);
    }

    @NotNull
    public synchronized Status getStatus() {
        return this.currentStatus;
    }

    public synchronized void setStatus(@NotNull final Status status) {
        requireNonNull(status);
        this.currentStatus = status;
    }

    @NotNull
    public synchronized String getTitle() {
        return this.title;
    }

    public synchronized void setTitle(@NotNull final String title) {
        this.setTitle(title, true);
    }

    public synchronized void setTitle(@NotNull final String title, final boolean doValidate) {
        requireNonNull(title);
        final String strippedVal = title.strip();
        if (!doValidate || validateTitle(strippedVal)) {
            this.title = strippedVal;
        }
        else
            throw new IllegalArgumentException();
    }

    @NotNull
    public synchronized String getTextOutline() {
        return this.textOutline;
    }

    public synchronized void setTextOutline(@NotNull final String textOutline) {
        this.setTextOutline(textOutline, true);
    }

    public synchronized void setTextOutline(@NotNull final String textOutline,
                                            final boolean doValidate)
    {
        requireNonNull(textOutline);
        final String strippedVal = textOutline.strip();
        if (!doValidate || validateTextOutline(strippedVal)) {
            this.textOutline = strippedVal;
        }
        else
            throw new IllegalArgumentException();
    }

    @NotNull
    public synchronized String getContext() {
        return this.context;
    }

    public synchronized void setContext(@NotNull final String context) {
        this.setContext(context, true);
    }

    public synchronized void setContext(@NotNull final String context, final boolean doValidate) {
        requireNonNull(context);
        final String strippedVal = context.strip();
        if (!doValidate || validateContext(strippedVal)) {
            this.context = strippedVal;
        }
        else
            throw new IllegalArgumentException();
    }

    public synchronized void setDescription(@NotNull final String description,
                                            final boolean doValidate)
    {
        requireNonNull(description);
        final String strippedVal = description.strip();
        if (!doValidate || validateDescription(strippedVal)) {
            this.description = strippedVal;
        }
        else
            throw new IllegalArgumentException();
    }

    @NotNull
    public synchronized String getDescription() {
        return this.description;
    }

    public synchronized void setDescription(@NotNull final String description) {
        requireNonNull(description);
        this.setDescription(description, true);
    }

    @NotNull
    public synchronized Optional<Ansprechpartner> getContact() {
        return this.contact;
    }

    public synchronized void setContact(@Nullable final Ansprechpartner contact) {
        this.contact = Optional.ofNullable(contact);
    }

    public synchronized void setFeedback(@NotNull final String feedback, final boolean doValidate) {
        requireNonNull(feedback);
        final String strippedVal = feedback.strip();
        if (!doValidate || validateFeedback(strippedVal)) {
            this.feedback = strippedVal;
        }
        else
            throw new IllegalArgumentException();
    }

    public synchronized void setFeedback(@NotNull final String feedback) {
        this.setFeedback(feedback, true);
    }

    @NotNull
    public synchronized String getFeedback() {
        return this.feedback;
    }

    public enum Status {
        DRAFT,
        IN_REVIEW,
        APPROVED,
        DENIED
    }
}
