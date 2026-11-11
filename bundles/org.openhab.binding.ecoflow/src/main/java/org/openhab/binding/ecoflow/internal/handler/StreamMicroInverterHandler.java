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

package org.openhab.binding.ecoflow.internal.handler;

import static org.openhab.binding.ecoflow.internal.EcoflowBindingConstants.StreamMicroInverterChannels.*;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.types.Command;

import com.google.gson.JsonObject;

/**
 * @author Danny Baumann - Initial contribution
 */
@NonNullByDefault
public class StreamMicroInverterHandler extends AbstractEcoflowHandler {
    private static final ValueConverter DECIMAL_CONVERTER = value -> new DecimalType(value.getAsNumber());
    private static final ValueConverter STRING_CONVERTER = value -> new StringType(value.getAsString());

    private static final List<ChannelMapping> MAPPINGS = List.of(
            new ChannelMapping("", "gridConnectionPower", CHANNEL_ID_GRID_POWER, 1, Units.WATT),
            new ChannelMapping("", "gridConnectionVol", CHANNEL_ID_GRID_VOLTAGE, 1, Units.VOLT),
            new ChannelMapping("", "gridConnectionAmp", CHANNEL_ID_GRID_CURRENT, 1, Units.AMPERE),
            new ChannelMapping("", "gridConnectionFreq", CHANNEL_ID_GRID_FREQUENCY, 1, Units.HERTZ),

            new ChannelMapping("", "plugInInfoPvVol", CHANNEL_ID_PV_IN_VOLTAGE, 1, Units.VOLT),
            new ChannelMapping("", "plugInInfoPvAmp", CHANNEL_ID_PV_IN_CURRENT, 1, Units.AMPERE),
            new ChannelMapping("", "powGetPv", CHANNEL_ID_PV_IN_POWER, 1, Units.WATT),

            new ChannelMapping("", "plugInInfoPv2Vol", CHANNEL_ID_PV_IN2_VOLTAGE, 1, Units.VOLT),
            new ChannelMapping("", "plugInInfoPv2Amp", CHANNEL_ID_PV_IN2_CURRENT, 1, Units.AMPERE),
            new ChannelMapping("", "powGetPv2", CHANNEL_ID_PV_IN2_POWER, 1, Units.WATT),

            new ChannelMapping("", "invNtcTemp3", CHANNEL_ID_TEMPERATURE, 1, SIUnits.CELSIUS),
            new ChannelMapping("", "moduleWifiRssi", CHANNEL_ID_WIFI_RSSI, DECIMAL_CONVERTER),
            new ChannelMapping("", "gridConnectionSta", CHANNEL_ID_GRID_CONNECTION_STATE, STRING_CONVERTER),
            new ChannelMapping("", "feedGridModePowLimit", CHANNEL_ID_GRID_FEED_LIMIT, 1, Units.WATT),
            new ChannelMapping("", "feedGridModePowMax", CHANNEL_ID_GRID_FEED_MAX, 1, Units.WATT));
    // TODO: "invTargetPwr":0.0 - meaning unclear, always 0 so far

    public StreamMicroInverterHandler(Thing thing) {
        super(thing, MAPPINGS);
    }

    @Override
    protected Optional<String> extractGroupKeyFromMqttMessage(JsonObject payload) {
        return Optional.of("");
    }

    @Override
    protected @Nullable JsonObject extractParamsFromQuotaMessage(JsonObject payload) {
        // 'Get all quota' returns no data for this device, so a quota message also proves it is online
        if (getThing().getStatus() != ThingStatus.ONLINE) {
            updateStatus(ThingStatus.ONLINE);
        }
        return payload;
    }

    @Override
    protected int getMqttMessageTimeoutSeconds() {
        // The device only does a full upload every 5 minutes (runtimePropertyFullUploadPeriod = 300000 ms) and
        // sends incremental updates only on value changes, so at low PV power (dawn/dusk) 5 minutes without any
        // message are normal (gaps of more than 14 minutes were observed at dawn). Allow four full upload periods
        // before considering the subscription stalled.
        return 4 * 5 * 60;
    }

    @Override
    protected Optional<JsonObject> convertCommand(String channelId, Command command) {
        return Optional.empty();
    }
}
