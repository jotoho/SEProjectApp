/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp.ui.menu;

import static java.util.Collections.unmodifiableSequencedSet;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.text.Collator;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public final class OptionSelectionMenu<T> {
    @NotNull
    private final SequencedSet<Option<T>> options = new LinkedHashSet<>();
    @NotNull
    private final String prompt;
    @NotNull
    private Optional<Option<T>> selectedOption = Optional.empty();
    private boolean listingEnabled = true;
    @SafeVarargs
    public OptionSelectionMenu(@NotNull final String prompt, @NotNull final Option<T>... options) {
        this(prompt, Arrays.asList(requireNonNull(options)));
    }

    public OptionSelectionMenu(@NotNull final String prompt,
                               @NotNull final Collection<Option<T>> options)
    {
        this(prompt);
        requireNonNull(options);
        this.options.addAll(options.stream().filter(Objects::nonNull).toList());
    }

    public OptionSelectionMenu(@NotNull final String prompt) {
        super();
        requireNonNull(prompt);
        if (prompt.isBlank())
            throw new IllegalArgumentException("Blank prompts are not allowed");
        this.prompt = prompt.strip();
    }

    public synchronized void addOption(@NotNull final Option<T> option) {
        requireNonNull(option);
        this.options.addLast(option);
    }

    public synchronized void removeOption(@Nullable final Option<T> option) {
        if (nonNull(option)) {
            this.options.remove(option);
            if (this.selectedOption.isPresent() &&
                this.selectedOption.filter(option::equals).isPresent())
            {
                this.selectedOption = Optional.empty();
            }
        }
    }

    public synchronized void enableListing() {
        this.listingEnabled = true;
    }

    public synchronized void disableListing() {
        this.listingEnabled = false;
    }

    /**
     * @return an unmodifiable view of the internal set-of-options
     */
    @NotNull
    @UnmodifiableView
    public SequencedSet<Option<T>> getOptions() {
        return unmodifiableSequencedSet(this.options);
    }

    public synchronized void activate() {
        boolean freshActivation = true;
        this.selectedOption = Optional.empty();
        if (this.options.stream().noneMatch(opt -> opt.enumerate || !opt.aliases().isEmpty())) {
            return;
        }

        final var scanner = new Scanner(System.in);

        while (freshActivation || this.selectedOption.filter(this.options::contains).isEmpty()) {
            if (freshActivation)
                freshActivation = false;
            try {
                System.out.println();
                System.out.println(this.prompt);

                if (this.listingEnabled)
                    System.out.print("Wählen Sie eine Option (l um alle anzuzeigen): ");
                else
                    System.out.print("Wählen Sie eine Option: ");

                final String userInput = scanner.nextLine().strip();

                if (this.listingEnabled && userInput.equals("l")) {
                    printOptionsAsList();
                    continue;
                }

                final var collator = Collator.getInstance(Locale.GERMANY);
                collator.setStrength(Collator.PRIMARY);
                final Predicate<String> userInputComparison = a -> collator.equals(a, userInput);
                final var aliasHits = this.options.stream()
                                                  .filter(opt -> opt.aliases()
                                                                    .stream()
                                                                    .anyMatch(userInputComparison))
                                                  .toList();

                if (!aliasHits.isEmpty()) {
                    this.selectedOption = Optional.of(aliasHits.getFirst());
                    continue;
                }

                final int index = Integer.parseInt(userInput, 10);
                final var enumerators = this.getEnumeratedOptions();
                if (enumerators.size() >= index + 1) {
                    this.selectedOption = Optional.of(enumerators.get(index));
                }
            }
            catch (final NumberFormatException ignore) {
            }
        }
    }

    private void printOptionsAsList() {
        final List<Option<T>> enumeratedOptions = this.getEnumeratedOptions();
        final int colsNumber =
                Math.max(1, (int) Math.ceil(Math.log10(enumeratedOptions.size() - 1)));
        if (!enumeratedOptions.isEmpty()) {
            System.out.println("Nummerierte Möglichkeiten:");
        }
        for (int index = 0; index < enumeratedOptions.size(); index++) {
            final var opt = enumeratedOptions.get(index);
            final String indexStr =
                    "0".repeat(colsNumber - 1).concat(Integer.valueOf(index).toString());
            System.out.println(indexStr + ". " + opt.label + " " + opt.aliases());
        }

        final List<Option<T>> aliasOptions =
                this.options.stream().filter(not(enumeratedOptions::contains)).toList();
        if (!aliasOptions.isEmpty()) {
            System.out.println("Benannte Möglichkeiten:");
            for (final var opt : aliasOptions) {
                System.out.println(opt.label + " " + opt.aliases());
            }
        }
    }

    @NotNull
    @Unmodifiable
    private List<Option<T>> getEnumeratedOptions() {
        return this.options.stream().filter(Option::enumerate).toList();
    }

    @NotNull
    public synchronized Optional<Option<T>> getSelectedOption() {
        return this.selectedOption;
    }

    public record Option<T>(@NotNull String label,
                            @NotNull Set<String> aliases,
                            boolean enumerate,
                            @Nullable T value,
                            @Nullable Callable<T> code)
    {

        public T get() {
            if (nonNull(value))
                return value;
            if (nonNull(code)) {
                try {
                    return code.call();
                }
                catch (Throwable ignore) {
                }
            }
            return null;
        }
    }
}
