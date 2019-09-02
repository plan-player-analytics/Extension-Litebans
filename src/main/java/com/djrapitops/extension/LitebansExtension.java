/*
    Copyright(c) 2019 Risto Lahtela (Rsl1122)

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
package com.djrapitops.extension;

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.Caller;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.annotation.Tab;
import com.djrapitops.plan.extension.annotation.TabInfo;
import com.djrapitops.plan.extension.annotation.TableProvider;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import com.djrapitops.plan.query.QueryService;
import litebans.api.Entry;
import litebans.api.Events;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

/**
 * Litebans DataExtension.
 *
 * @author Rsl1122
 */
@PluginInfo(name = "Litebans", iconName = "gavel", iconFamily = Family.SOLID, color = Color.RED)
@TabInfo(tab = "Bans", iconName = "gavel", elementOrder = {})
@TabInfo(tab = "Mutes", iconName = "bell-slash", iconFamily = Family.REGULAR, elementOrder = {})
@TabInfo(tab = "Warnings", iconName = "exclamation-triangle", elementOrder = {})
@TabInfo(tab = "Kicks", iconName = "user-times", elementOrder = {})
public class LitebansExtension implements DataExtension {

    private LitebansDatabaseQueries queries;
    private SimpleDateFormat formatter;

    public LitebansExtension() {
        queries = new LitebansDatabaseQueries();
        formatter = new SimpleDateFormat("MMM d YYYY, HH:mm");
    }

    public static void registerEvents(Caller caller) {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry entry) {
                update(entry);
            }

            private void update(Entry entry) {
                String uuid = entry.getUuid();
                if (uuid == null) return;
                try {
                    caller.updatePlayerData(UUID.fromString(uuid), null); // Player name is not needed by LitebansExtension
                } catch (IllegalArgumentException ignore) {
                }
            }

            @Override
            public void entryRemoved(Entry entry) {
                update(entry);
            }
        });
    }

    @Override
    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.PLAYER_LEAVE,
                CallEvents.SERVER_PERIODICAL
        };
    }

    @TableProvider(tableColor = Color.RED)
    @Tab("Bans")
    public Table bans(UUID playerUUID) {
        Table.Factory table = playerTable();
        addRows(table, queries.getBans(playerUUID));
        return table.build();
    }

    @TableProvider(tableColor = Color.DEEP_ORANGE)
    @Tab("Mutes")
    public Table mutes(UUID playerUUID) {
        Table.Factory table = playerTable();
        addRows(table, queries.getMutes(playerUUID));
        return table.build();
    }

    @TableProvider(tableColor = Color.AMBER)
    @Tab("Warnings")
    public Table warns(UUID playerUUID) {
        Table.Factory table = playerTable();
        addRows(table, queries.getWarnings(playerUUID));
        return table.build();
    }

    @TableProvider(tableColor = Color.BROWN)
    @Tab("Kicks")
    public Table kicks(UUID playerUUID) {
        Table.Factory table = playerTable();
        addRows(table, queries.getKicks(playerUUID));
        return table.build();
    }

    @TableProvider(tableColor = Color.RED)
    @Tab("Bans")
    public Table bans() {
        Table.Factory table = serverTable();
        addRows(table, QueryService.getInstance(), queries.getBans());
        return table.build();
    }

    @TableProvider(tableColor = Color.DEEP_ORANGE)
    @Tab("Mutes")
    public Table mutes() {
        Table.Factory table = serverTable();
        addRows(table, QueryService.getInstance(), queries.getMutes());
        return table.build();
    }

    @TableProvider(tableColor = Color.AMBER)
    @Tab("Warnings")
    public Table warns() {
        Table.Factory table = serverTable();
        addRows(table, QueryService.getInstance(), queries.getWarnings());
        return table.build();
    }

    @TableProvider(tableColor = Color.BROWN)
    @Tab("Kicks")
    public Table kicks() {
        Table.Factory table = serverTable();
        addRows(table, QueryService.getInstance(), queries.getKicks());
        return table.build();
    }

    private Table.Factory playerTable() {
        return Table.builder()
                .columnOne("Reason", Icon.called("balance-scale").build())
                .columnTwo("By", Icon.called("user-shield").build())
                .columnThree("Given", Icon.called("clock").build())
                .columnFour("Expires", Icon.called("clock").of(Family.REGULAR).build());
    }

    private Table.Factory serverTable() {
        return Table.builder()
                .columnOne("Affects", Icon.called("user").build())
                .columnTwo("By", Icon.called("user-shield").build())
                .columnThree("Reason", Icon.called("balance-scale").build())
                .columnFour("Given", Icon.called("clock").build())
                .columnFive("Expires", Icon.called("clock").of(Family.REGULAR).build());
    }

    private String formatDate(long date) {
        return date <= 0 ? "Never" : formatter.format(date);
    }

    private void addRows(Table.Factory table, List<LitebansDBEntry> entries) {
        for (LitebansDBEntry entry : entries) {
            table.addRow(entry.getReason(), entry.getBannedBy(), formatDate(entry.getTime()), formatDate(entry.getExpiry()));
        }
    }

    private void addRows(Table.Factory table, QueryService queryService, List<LitebansDBEntry> entries) {
        for (LitebansDBEntry entry : entries) {
            UUID uuid = entry.getUuid();
            String affects = queryService.getCommonQueries().fetchNameOf(uuid).orElse(uuid.toString());
            table.addRow(affects, entry.getBannedBy(), entry.getReason(), formatDate(entry.getTime()), formatDate(entry.getExpiry()));
        }
    }
}