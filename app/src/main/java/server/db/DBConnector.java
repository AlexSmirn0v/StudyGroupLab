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
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import model.Coordinates;
import model.GroupBuilder;
import model.Person;
import model.StudyGroup;
import server.ServerLogger;

public class DBConnector implements AutoCloseable {
    private static final String DB_HOST = "localhost"; // for debug
    // private static final String DB_HOST = "localhost"; // for prod
    private static final String DB_PORT = "5432";
    private static final String DB_NAME = "studs";
    private static final Path DB_CFG_PATH = Path.of("db.cfg");
    private static final Path SCHEMA_PATH = Path.of("app", "src", "main", "java", "server", "db", "schema.sql");

    private final DBCollection collection;
    private final Properties config;
    private Connection connection;

    public DBConnector() {
        this.collection = new DBCollection();
        this.config = loadConfig();
        try {
            initSchema();
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            ServerLogger.log("Не получилось закрыть соединение с БД: " + e.getMessage());
        }
    }

    public DBCollection getCollection() {
        return collection;
    }

    public synchronized Connection getConnection() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            String user = requireConfig("user");
            String password = requireConfig("password");
            String url = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(true);
        }
        return connection;
    }

    public synchronized void initSchema() throws SQLException, IOException {
        String schemaSql = Files.readString(SCHEMA_PATH, StandardCharsets.UTF_8);
        try (Statement statement = getConnection().createStatement()) {
            for (String line : schemaSql.split(";")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty())
                    statement.execute(trimmed);
            }
        }
    }

    public Integer upsertUser(String login, String passwordHash) {
        String sql = """
                INSERT INTO users(login, password_hash)
                VALUES (?, ?)
                ON CONFLICT (login)
                DO UPDATE SET password_hash = EXCLUDED.password_hash
                RETURNING id
                """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, passwordHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Не удалось сохранить пользователя");
                }
                return rs.getInt("id");
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    public Optional<UserData> findUser(String login) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new UserData(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password_hash")));
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<UserData> authenticateUser(String login, String passwordHash) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ? AND password_hash = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, passwordHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new UserData(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("password_hash")));
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return Optional.empty();
        }
    }

    public void addStudyGroup(StudyGroup group, int ownerId) throws SQLException, IOException {
        Connection conn = getConnection();
        boolean oldAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            int coordinatesId = insertCoordinates(group.getCoordinates());
            Integer semesterId = insertSemester(group);
            Integer adminId = insertAdmin(group.getGroupAdmin());

            String sql = """
                    INSERT INTO study_groups(
                        id, name, coordinates_id, creation_date, students_count,
                        transferred_students, average_mark, semester_id, group_admin_id, owner_id
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, group.getId());
                stmt.setString(2, group.getName());
                stmt.setInt(3, coordinatesId);
                stmt.setTimestamp(4, Timestamp.valueOf(group.getCreationDate()));
                if (group.getStudentsCount() == null) {
                    stmt.setNull(5, java.sql.Types.BIGINT);
                } else {
                    stmt.setLong(5, group.getStudentsCount());
                }
                stmt.setInt(6, group.getTransferredStudents());
                stmt.setInt(7, group.getAverageMark());
                if (semesterId == null) {
                    stmt.setNull(8, java.sql.Types.INTEGER);
                } else {
                    stmt.setInt(8, semesterId);
                }
                if (adminId == null) {
                    stmt.setNull(9, java.sql.Types.INTEGER);
                } else {
                    stmt.setInt(9, adminId);
                }
                stmt.setInt(10, ownerId);
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(oldAutoCommit);
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

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String csv = toCsvLine(rs);
                StudyGroup group = new GroupBuilder().fromCSVString(csv, ";").buildLoaded();
                OwnedStudyGroup ownGroup = new OwnedStudyGroup(group, rs.getInt("owner_id"),
                        rs.getString("owner_login"));
                collection.add(group);
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
        }
        return collection;
    }

    public boolean removeStudyGroup(long groupId, int ownerId) {
        String sql = "DELETE FROM study_groups WHERE id = ? AND owner_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setLong(1, groupId);
            stmt.setInt(2, ownerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return false;
        }
    }

    public boolean clearUserStudyGroups(int ownerId) {
        String sql = "DELETE FROM study_groups WHERE owner_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, ownerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return false;
        }
    }

    private Integer insertCoordinates(Coordinates coordinates) {
        String sql = "INSERT INTO coordinates(x, y) VALUES (?, ?) RETURNING id";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setLong(1, coordinates.getX());
            stmt.setLong(2, coordinates.getY());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    private Integer insertSemester(StudyGroup group) {
        if (group.getSemesterEnum() == null) {
            return null;
        }
        String sql = "INSERT INTO semesters(val) VALUES (?) ON CONFLICT (val) DO UPDATE SET val = EXCLUDED.val RETURNING id";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, group.getSemesterEnum().getName());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    private Integer insertColor(Person admin) {
        if (admin == null || admin.getHairColor() == null) {
            return null;
        }
        String sql = "INSERT INTO colors(val) VALUES (?) ON CONFLICT (val) DO UPDATE SET val = EXCLUDED.val RETURNING id";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, admin.getHairColor().getName());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    private Integer insertAdmin(Person admin) {
        if (admin == null) {
            return null;
        }
        Integer colorId = insertColor(admin);
        String sql = "INSERT INTO admins(name, height, passportID, hair_color_id) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, admin.getName());
            stmt.setInt(2, admin.getHeight());
            stmt.setString(3, admin.getPassportID());
            if (colorId == null) {
                stmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(4, colorId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    private String toCsvLine(ResultSet rs) throws SQLException {
        return String.join(";",
                String.valueOf(rs.getLong("id")),
                rs.getTimestamp("creation_date").toLocalDateTime().toString(),
                rs.getString("name"),
                String.valueOf(rs.getLong("x")),
                String.valueOf(rs.getLong("y")),
                rs.getObject("students_count") == null ? "" : String.valueOf(rs.getLong("students_count")),
                String.valueOf(rs.getInt("transferred_students")),
                String.valueOf(rs.getInt("average_mark")),
                nullToEmpty(rs.getString("semester_val")),
                nullToEmpty(rs.getString("admin_name")),
                rs.getObject("admin_height") == null ? "" : String.valueOf(rs.getInt("admin_height")),
                nullToEmpty(rs.getString("admin_passport_id")),
                nullToEmpty(rs.getString("admin_hair_color")));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
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

    public record UserData(int id, String login, String passwordHash) {
    }

    public record OwnedStudyGroup(StudyGroup group, int ownerId, String ownerLogin) {
    }
}