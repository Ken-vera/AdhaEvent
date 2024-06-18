package lunatic.adhaevent.Hook;

import me.kenvera.chronocore.ChronoCore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseHook {
    private final ChronoCore chronoCore;
    public DatabaseHook() {
        chronoCore = ChronoCore.getInstance();
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS adha_event (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "username VARCHAR(16), " +
                "points INT DEFAULT 0)";
        try (Connection connection = chronoCore.getSqlManager().getConnection();
             PreparedStatement ps = connection.prepareStatement(createTableSQL)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    public void registerPlayer(UUID uuid, String username) {
        if (!isPlayerRegistered(uuid)) {
            String insertPlayerSQL = "INSERT INTO adha_event (uuid, username) VALUES (?, ?)";
            try (Connection connection = chronoCore.getSqlManager().getConnection();
                    PreparedStatement ps = connection.prepareStatement(insertPlayerSQL)) {
                ps.setString(1, uuid.toString());
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public boolean isPlayerRegistered(UUID uuid) {
        String selectSQL = "SELECT COUNT(*) FROM adha_event WHERE uuid = ?";
        try (Connection connection = chronoCore.getSqlManager().getConnection();
                PreparedStatement ps = connection.prepareStatement(selectSQL)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    public int getpoints(UUID uuid) {
        String selectSQL = "SELECT points FROM adha_event WHERE uuid = ?";
        try (Connection connection = chronoCore.getSqlManager().getConnection();
                PreparedStatement ps = connection.prepareStatement(selectSQL)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("points");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return 0;
    }

    public void addAdhaPoints(UUID uuid, String playerName, int amount) {
        String updateSql = "UPDATE adha_event SET points = points + ? WHERE uuid = ?";
        String insertSql = "INSERT INTO adha_event (uuid, username, points) VALUES (?, ?, ?)";

        try (Connection connection = chronoCore.getSqlManager().getConnection()) {
            // Attempt to update first
            try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                updateStmt.setInt(1, amount);
                updateStmt.setString(2, uuid.toString());

                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    return; // Player record updated successfully, exit
                }
            }

            // If no rows were updated, the player is not yet in the table, so insert
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setString(1, uuid.toString());
                insertStmt.setString(2, playerName);
                insertStmt.setInt(3, amount);

                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    public void subtractpoints(UUID uuid, int amount) {
        String updateSQL = "UPDATE adha_event SET points = points - ? WHERE uuid = ?";
        try (Connection connection = chronoCore.getSqlManager().getConnection();
                PreparedStatement ps = connection.prepareStatement(updateSQL)) {
            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }
}
