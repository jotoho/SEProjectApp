/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp.ui;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.function.Predicate.not;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public final class TerminalInputUtil {
    private TerminalInputUtil() {

    }

    @SuppressWarnings("unused")
    public static String getTextBlock(@NotNull final String escapeString) {
        requireNonNull(escapeString);
        final StringBuilder strBuilder = new StringBuilder();
        final AtomicBoolean escapeCondition = new AtomicBoolean(false);
        while (!escapeCondition.get()) {
            final var lineOpt = getNextLine();
            lineOpt.filter(not(escapeString::equals))
                   .map(s -> s + "\n")
                   .ifPresentOrElse(strBuilder::append, () -> escapeCondition.set(true));
        }

        return strBuilder.toString().strip();
    }

    @SuppressWarnings("unused")
    public static Optional<String> getNextLine() {
        final var scanner = new Scanner(System.in);
        while (!scanner.hasNextLine()) {
            // Do nothing but wait
            try {
                Thread.sleep(Duration.ofSeconds(1));
            }
            catch (final InterruptedException ie) {
                return Optional.empty();
            }
        }
        try {
            final var line = scanner.nextLine();
            if (nonNull(line)) {
                return Optional.of(line.strip());
            }
            else {
                return Optional.empty();
            }
        }
        catch (final NoSuchElementException | IllegalStateException unused) {
            return Optional.empty();
        }
    }
}
