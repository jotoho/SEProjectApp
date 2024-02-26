/*
    SPDX-License-Identifier: AGPL-3.0-only
    SPDX-FileCopyrightText: 2024 Tim Beckmann <beckmann.tim@fh-swf.de>
    SPDX-FileCopyrightText: 2024 Jonas Tobias Hopusch <git@jotoho.de>
*/
package de.jotoho.fhswf.se.projectapp;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class Unternehmen {
    private static final ConcurrentHashMap<Long, WeakReference<Unternehmen>> knownInstances =
            new ConcurrentHashMap<>();
    private final long unternehmenID;
    private String name;

    private String address;

    public static Optional<Unternehmen> getInstanceRef(final long unternehmenID) {
        return Optional.ofNullable(
                knownInstances.getOrDefault(unternehmenID,
                                            new WeakReference<>(null))
                              .get());
    }

    public long getID() {
        return this.unternehmenID;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress(){
        return this.address;
    }

    public void setName(final String name) {
        requireNonNull(name);
        if (name.isBlank())
            throw new IllegalArgumentException();
        else
            this.name = name.strip();
    }

    public void setAddress(final String address){
        requireNonNull(address);
        if (address.isBlank())
            throw new IllegalArgumentException();
        else
            this.address = address.strip();
    }

    public static void flushDeadInstanceRecords() {
        knownInstances.forEachEntry(1, entry -> {
            final var key = entry.getKey();
            final var val = entry.getValue();
            if (isNull(val) || isNull(val.get()))
                knownInstances.remove(key, val);
        });
    }

    public Unternehmen(final long unternehmenID,
                       final String name,
                       final String address) {
        this.unternehmenID = unternehmenID;
        this.setName(name);
        this.setAddress(address);

        final var instance = this;
        Thread.ofVirtual().start(Projekt::flushDeadInstanceRecords);
        knownInstances.compute(unternehmenID, (uid, uWeakRef) -> {
            if (isNull(uWeakRef))
                return new WeakReference<>(instance);
            final var weakRefContent = uWeakRef.get();
            return isNull(weakRefContent) ? new WeakReference<>(instance) : uWeakRef;
        });

        if (knownInstances.get(unternehmenID).get() != this)
            throw new IllegalStateException();
    }
}
