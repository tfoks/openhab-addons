/*
 * Copyright (c) 2010-2026 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.ecoflow.internal.api.dto.response;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Danny Baumann - Initial contribution
 */
@NonNullByDefault
public class DataWrapper<T> {
    public final int code;
    public final String message;
    // May be absent even for successful requests, e.g. 'get all quota' for STREAM micro inverters
    public final @Nullable T data;

    DataWrapper(int code, String message, @Nullable T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
