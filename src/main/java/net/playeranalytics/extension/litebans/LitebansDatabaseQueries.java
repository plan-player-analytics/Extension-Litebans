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

import com.djrapitops.plan.extension.NotReadyException;
import litebans.api.Database;
import litebans.api.exception.MissingImplementationException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Queries towards Litebans database
 *
 * @author AuroraLS3
 */
public class LitebansDatabaseQueries {
    private final String banTable;
    private final String mutesTable;
    private final String warningsTable;
    private final String kicksTable;

    private final String selectSQL;

    public LitebansDatabaseQueries() {
        banTable = "{bans}";
        mutesTable = "{mutes}";
        warningsTable = "{warnings}";
        kicksTable = "{kicks}";
        selectSQL = "SELECT uuid, reason, banned_by_name, until, active, time FROM ";
    }

    private List<LitebansDBEntry> getObjs(String table) {
        String sql = selectSQL + table + " ORDER BY time DESC LIMIT 5000";

        try (PreparedStatement statement = Database.get().prepareStatement(sql);
             ResultSet set = statement.executeQuery()) {
            return processIntoObjects(set);
        } catch (IllegalStateException | MissingImplementationException e) {
            throw new NotReadyException();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<LitebansDBEntry> getBans() {
        return getObjs(banTable);
    }

    public List<LitebansDBEntry> getMutes() {
        return getObjs(mutesTable);
    }

    public List<LitebansDBEntry> getWarnings() {
        return getObjs(warningsTable);
    }

    public List<LitebansDBEntry> getKicks() {
        return getObjs(kicksTable);
    }

    private List<LitebansDBEntry> processIntoObjects(ResultSet set) throws SQLException {
        List<LitebansDBEntry> objs = new ArrayList<>();
        while (set.next()) {
            String uuidS = set.getString("uuid");
            if (uuidS == null) {
                continue;
            }
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidS);
            } catch (IllegalArgumentException e) {
                continue;
            }
            String reason = set.getString("reason");
            String bannedBy = set.getString("banned_by_name");
            long until = set.getLong("until");
            long time = set.getLong("time");
            boolean active = set.getBoolean("active");
            objs.add(new LitebansDBEntry(uuid, reason, bannedBy, until, active, time));
        }
        return objs;
    }

    public List<LitebansDBEntry> getBans(UUID playerUUID) {
        return getObjs(playerUUID, banTable);
    }

    public List<LitebansDBEntry> getMutes(UUID playerUUID) {
        return getObjs(playerUUID, mutesTable);
    }

    public List<LitebansDBEntry> getWarnings(UUID playerUUID) {
        return getObjs(playerUUID, warningsTable);
    }

    public List<LitebansDBEntry> getKicks(UUID playerUUID) {
        return getObjs(playerUUID, kicksTable);
    }

    private List<LitebansDBEntry> getObjs(UUID playerUUID, String table) {
        String sql = selectSQL + table + " WHERE uuid=? ORDER BY time DESC";

        try (PreparedStatement statement = Database.get().prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            try (ResultSet set = statement.executeQuery()) {
                return processIntoObjects(set);
            }
        } catch (IllegalStateException | MissingImplementationException e) {
            throw new NotReadyException();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<String> getAlternativeConnects(UUID playerUUID) {
        String sql = "SELECT name FROM {history} WHERE uuid=? UNION SELECT name FROM {history} WHERE ip IN (SELECT ip FROM {history} WHERE uuid=?)";
        try (PreparedStatement statement = Database.get().prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, playerUUID.toString());
            try (ResultSet set = statement.executeQuery()) {
                List<String> names = new ArrayList<>();
                while (set.next()) names.add(set.getString("name"));
                return names;
            }
        } catch (IllegalStateException | MissingImplementationException e) {
            throw new NotReadyException();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
