package server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import server.ServerLogger;
import server.db.DBManager.UserData;
import utils.HashTools;

class UserHandler {
    static UserData addUser(Connection conn, String login, String password) {
        String sql = """
                INSERT INTO users(login, password_hash)
                VALUES (?, ?)
                RETURNING id
                """;
        byte[] passwordHash = HashTools.hashPassword(password);
        if (findUser(conn, login) != null) {
            ServerLogger.log("Пользователь с логином " + login + " уже существует");
            return null;
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setBytes(2, passwordHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Не удалось сохранить пользователя");
                }
                return new UserData(rs.getInt("id"), login, passwordHash);
            }
        } catch (SQLException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    static UserData authOrCreateUser(Connection conn, String login, String password) {
        if (findUser(conn, login) != null) {
            return authUser(conn, login, password);
        } else {
            return addUser(conn, login, password);
        }
    }

    static UserData findUser(Connection conn, String login) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new UserData(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getBytes("password_hash"));
            }
        } catch (SQLException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    static UserData authUser(Connection conn, String login, String password) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ? AND password_hash = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setBytes(2, HashTools.hashPassword(password));
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new UserData(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getBytes("password_hash"));
            }
        } catch (SQLException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }
}
