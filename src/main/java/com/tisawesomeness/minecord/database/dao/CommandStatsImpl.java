package com.tisawesomeness.minecord.database.dao;

import com.tisawesomeness.minecord.database.Database;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiSetUtils;
import org.apache.commons.collections4.multiset.HashMultiSet;

import java.sql.*;

/**
 * Implements command stats tracking in a SQL backend
 */
@RequiredArgsConstructor
public class CommandStatsImpl implements CommandStats {

    private static final String UPDATE_SQL = "UPDATE command SET uses = uses + ? WHERE id = ?;";
    private static final String INSERT_SQL =
            "INSERT INTO command (id, uses)\n" +
                    "SELECT ?, ?\n" +
                    "WHERE NOT EXISTS (\n" +
                    "    SELECT 1 FROM command WHERE id = ?\n" +
                    ");";

    private final Database db;

    public MultiSet<String> getCommandUses() throws SQLException {
        @Cleanup Connection connect = db.getConnect();
        @Cleanup Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "SELECT * FROM command;"
        );
        MultiSet<String> commandUses = new HashMultiSet<>();
        while (rs.next()) {
            commandUses.add(rs.getString("id"), rs.getInt("uses"));
        }
        return MultiSetUtils.unmodifiableMultiSet(commandUses);
    }

    public void pushCommandUses(MultiSet<String> commandUses) throws SQLException {
        @Cleanup Connection connect = db.getConnect();
        connect.setAutoCommit(false);
        @Cleanup PreparedStatement updateSt = connect.prepareStatement(UPDATE_SQL);
        @Cleanup PreparedStatement insertSt = connect.prepareStatement(INSERT_SQL);
        try {
            for (MultiSet.Entry<String> entry : commandUses.entrySet()) {
                String id = entry.getElement();
                int uses = entry.getCount();
                updateSt.setInt(1, uses);
                updateSt.setString(2, id);
                updateSt.executeUpdate();
                insertSt.setString(1, id);
                insertSt.setInt(2, uses);
                insertSt.setString(3, id);
                insertSt.executeUpdate();
            }
            connect.commit();
        } catch (Exception ex) {
            connect.rollback();
            throw ex;
        }
    }

}
