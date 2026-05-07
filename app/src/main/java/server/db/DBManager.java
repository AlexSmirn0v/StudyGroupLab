package server.db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import model.GroupParams;
import model.GroupBuilder;
import model.StudyGroup;
import server.ServerLogger;

public class DBManager {
    private static final Path DB_CFG_PATH = Path.of("db.cfg");

    private final DBCollection collection;
    private final Properties config;

    public DBManager() {
        this.collection = new DBCollection(this);
        this.config = loadConfig();
        try {
            initSchema();
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
        }
    }

    public DBCollection getCollection() {
        return collection;
    }

    public Connection getConnection() throws SQLException, IOException {
        String user = requireConfig("user");
        String password = requireConfig("password");
        String url = "jdbc:postgresql://" + requireConfig("host") + ":" + requireConfig("port") + "/"
                + requireConfig("name");
        Connection connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(true);
        return connection;
    }

    public void initSchema() throws SQLException, IOException {
        Path schemaPath = Path.of(requireConfig("initsql"));
        String schemaSql = Files.readString(schemaPath, StandardCharsets.UTF_8);
        try (Connection conn = getConnection();
                Statement statement = conn.createStatement()) {
            for (String line : schemaSql.split(";")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty())
                    statement.execute(trimmed);
            }
        }
    }

    public UserData addUser(String login, String password) {
        try (Connection conn = getConnection()) {
            return UserHandler.addUser(conn, login, password);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    public UserData authOrCreateUser(String login, String password) {
        try (Connection conn = getConnection()) {
            return UserHandler.authOrCreateUser(conn, login, password);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    public UserData findUser(String login) {
        try (Connection conn = getConnection()) {
            return UserHandler.findUser(conn, login);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    public UserData authUser(String login, String password) {
        try (Connection conn = getConnection()) {
            return UserHandler.authUser(conn, login, password);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    public DBCollection loadStudyGroups() throws SQLException {
        String sql = """
                SELECT
                    sg.id,
                    sg.creation_date,
                    sg.name,
                    c.x,
                    c.y,
                    sg.students_count,
                    sg.transferred_students,
                    sg.average_mark,
                    sem.val AS semester_val,
                    a.name AS admin_name,
                    a.height AS admin_height,
                    a.passportID AS admin_passport_id,
                    clr.val AS admin_hair_color,
                    u.id AS owner_id,
                    u.login AS owner_login
                FROM study_groups sg
                INNER JOIN coordinates c ON sg.coordinates_id = c.id
                LEFT JOIN semesters sem ON sg.semester_id = sem.id
                LEFT JOIN admins a ON sg.group_admin_id = a.id
                LEFT JOIN colors clr ON a.hair_color_id = clr.id
                INNER JOIN users u ON sg.owner_id = u.id
                ORDER BY sg.id
                """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            collection.clear();
            while (rs.next()) {
                String csv = GroupHandler.toCsvLine(rs);
                StudyGroup group = new GroupBuilder().fromCSVString(csv, ";").buildLoaded();
                group.setAuthor(rs.getString("owner_login"));
                collection.addLoaded(group);
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
        }
        return collection;
    }

    public void addStudyGroup(StudyGroup group, int ownerId) throws SQLException, IOException {
        try (Connection conn = getConnection()) {
            GroupHandler.addStudyGroup(conn, group, ownerId);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
        }
    }

    public void persistAddStudyGroup(StudyGroup group) {
        try (Connection conn = getConnection()) {
            GroupHandler.persistAddStudyGroup(conn, group);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            throw new IllegalStateException("Не получилось подключиться к БД: " + e.getMessage(), e);
        }
    }

    public StudyGroup getStudyGroupByName(String name) {
        try (Connection conn = getConnection()) {
            return GroupHandler.getStudyGroupByName(conn, name);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    public StudyGroup getStudyGroupById(long id) {
        try (Connection conn = getConnection()) {
            return new GroupHandler().getStudyGroupById(conn, id);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    public boolean persistRemoveStudyGroup(long groupId, String username) {
        try (Connection conn = getConnection()) {
            return GroupHandler.persistRemoveStudyGroup(conn, groupId, username);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return false;
        }
    }

    public boolean persistClearStudyGroups(String username) {
        try (Connection conn = getConnection()) {
            return GroupHandler.persistClearStudyGroups(conn, username);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return false;
        }
    }

    public boolean persistUpdateStudyGroup(long groupId, GroupParams param, String value, String username) {
        try (Connection conn = getConnection()) {
            return GroupHandler.persistUpdateStudyGroup(conn, groupId, param, value, username);
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return false;
        }
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try {
            List<String> lines = Files.readAllLines(DB_CFG_PATH, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] pair = trimmed.split("=", 2);
                if (pair.length == 2) {
                    props.setProperty(pair[0].trim(), pair[1].trim());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать db.cfg", e);
        }
        return props;
    }

    private String requireConfig(String key) {
        String value = config.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("В db.cfg отсутствует ключ: " + key);
        }
        return value;
    }

    public record UserData(int id, String login, byte[] passwordHash) {
    }
}