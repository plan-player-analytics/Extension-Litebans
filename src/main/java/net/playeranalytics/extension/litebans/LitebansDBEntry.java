/*
    Copyright(c) 2019 AuroraLS3

    The MIT License(MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files(the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions :
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
package net.playeranalytics.extension.litebans;

import java.util.UUID;

/**
 * Represents something in the Litebans database.
 *
 * @author AuroraLS3
 */
public class LitebansDBEntry {
    private final UUID uuid;
    private final String reason;
    private final String bannedBy;
    private final long expiry;
    private final boolean active;
    private final long time;

    public LitebansDBEntry(UUID uuid, String reason, String bannedBy, long expiry, boolean active, long time) {
        this.uuid = uuid;
        this.reason = reason;
        this.bannedBy = bannedBy;
        this.expiry = expiry;
        this.active = active;
        this.time = time;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getReason() {
        return reason;
    }

    public String getBannedBy() {
        return bannedBy;
    }

    public long getExpiry() {
        return expiry;
    }

    public boolean isActive() {
        return active;
    }

    public long getTime() {
        return time;
    }
}