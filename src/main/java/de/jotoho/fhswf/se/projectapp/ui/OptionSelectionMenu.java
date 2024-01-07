package de.jotoho.fhswf.se.projectapp.ui;

import java.util.*;
import java.util.concurrent.Callable;

import static java.util.Collections.unmodifiableSequencedSet;
import static java.util.Objects.*;
import static java.util.function.Predicate.not;

@SuppressWarnings("unused")
public final class OptionSelectionMenu<T> {
    private final SequencedSet<Option<T>> options = new LinkedHashSet<>();
    private final String prompt;
    private Optional<Option<T>> selectedOption = Optional.empty();
    private boolean listingEnabled = true;
    public OptionSelectionMenu(final String prompt) {
        super();
        requireNonNull(prompt);
        if (prompt.isBlank())
            throw new IllegalArgumentException("Blank prompts are not allowed");
        this.prompt = prompt.strip();
    }

    public OptionSelectionMenu(final String prompt,
                               final Collection<Option<T>> options) {
        this(prompt);
        this.options.addAll(
                options.stream()
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

    @SafeVarargs
    public OptionSelectionMenu(final String prompt,
                               final Option<T>... options) {
        this(prompt,
                Arrays.asList(options));
    }

    public synchronized void addOption(final Option<T> option) {
        requireNonNull(option);
        this.options.addLast(option);
    }

    public synchronized void removeOption(final Option<T> option) {
        if (nonNull(option)) {
            this.options.remove(option);
            if (this.selectedOption.isPresent()
                    && this.selectedOption.filter(option::equals).isPresent()) {
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
    public SequencedSet<Option<T>> getOptions() {
        return unmodifiableSequencedSet(this.options);
    }

    private void printOptionsAsList() {
        final List<Option<T>> enumeratedOptions = this.getEnumeratedOptions();
        final int colsNumber = Math.max(1,
                (int) Math.ceil(Math.log10(enumeratedOptions.size() - 1)));
        if (!enumeratedOptions.isEmpty()) {
            System.out.println("Nummerierte Möglichkeiten:");
        }
        for (int index = 0; index < enumeratedOptions.size(); index++) {
            final var opt = enumeratedOptions.get(index);
            final String indexStr =
                    "0".repeat(colsNumber - 1)
                            .concat(Integer.valueOf(index)
                                    .toString());
            System.out.println(indexStr + ". " + opt.label + " " + opt.aliases().toString());
        }

        final List<Option<T>> aliasOptions = this.options.stream()
                .filter(not(enumeratedOptions::contains))
                .toList();
        if (!aliasOptions.isEmpty()) {
            System.out.println("Benannte Möglichkeiten:");
            for (final var opt : aliasOptions) {
                System.out.println(opt.label + " " + opt.aliases().toString());
            }
        }
    }

    private List<Option<T>> getEnumeratedOptions() {
        return this.options.stream().filter(Option::enumerate).toList();
    }

    public synchronized void activate() {
        boolean freshActivation = true;
        this.selectedOption = Optional.empty();
        if (this.options.stream()
                .noneMatch(opt -> opt.enumerate || !opt.aliases().isEmpty())) {
            return;
        }

        final var scanner = new Scanner(System.in);

        while (isNull(this.selectedOption) || freshActivation ||
                this.selectedOption.filter(this.options::contains).isEmpty()) {
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

                final var aliasHits = this.options.stream()
                        .filter(opt -> opt.aliases()
                                .contains(userInput))
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
            } catch (final NumberFormatException ignore) {
            }
        }

    }

    public synchronized Optional<Option<T>> getSelectedOption() {
        return this.selectedOption;
    }

    public record Option<T>(String label,
                            Set<String> aliases,
                            boolean enumerate,
                            T value,
                            Callable<T> code) {

        public T get() {
            if (nonNull(value))
                return value;
            if (nonNull(code)) {
                try {
                    return code.call();
                } catch (Throwable ignore) {
                }
            }
            return null;
        }
    }
}
