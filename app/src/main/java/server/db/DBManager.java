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
import java.util.Properties;

import model.Coordinates;
import model.GroupBuilder;
import model.Person;
import model.StudyGroup;
import server.ServerLogger;
import utils.HashTools;

public class DBManager {
    private static final Path DB_CFG_PATH = Path.of("db.cfg");
    private static final Path SCHEMA_PATH = Path.of("app", "src", "main", "java", "server", "db", "schema.sql");

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
        String url = "jdbc:postgresql://" + requireConfig("host") + ":" + requireConfig("port") + "/" + requireConfig("name");
        Connection connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(true);
        return connection;
    }

    public void initSchema() throws SQLException, IOException {
        String schemaSql = Files.readString(SCHEMA_PATH, StandardCharsets.UTF_8);
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
        String sql = """
                INSERT INTO users(login, password_hash)
                VALUES (?, ?)
                RETURNING id
                """;
        byte[] passwordHash = HashTools.hashPassword(password);
        if (findUser(login) != null) {
            ServerLogger.log("Пользователь с логином " + login + " уже существует");
            return null;
        }
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setBytes(2, passwordHash);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Не удалось сохранить пользователя");
                }
                return new UserData(rs.getInt("id"), login, passwordHash);
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    public UserData authOrCreateUser(String login, String password) {
        if (findUser(login) != null) {
            return authUser(login, password);
        } else {
            return addUser(login, password);
        }
    }

    public UserData findUser(String login) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    public UserData authUser(String login, String password) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ? AND password_hash = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    public void addStudyGroup(StudyGroup group, int ownerId) throws SQLException, IOException {
        try (Connection conn = getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                int coordinatesId = insertCoordinates(conn, group.getCoordinates());
                Integer semesterId = insertSemester(conn, group);
                Integer adminId = insertAdmin(conn, group.getGroupAdmin());

                String sql = """
                        INSERT INTO study_groups(
                            name, coordinates_id, creation_date, students_count,
                            transferred_students, average_mark, semester_id, group_admin_id, owner_id
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, group.getName());
                    stmt.setInt(2, coordinatesId);
                    stmt.setTimestamp(3, Timestamp.valueOf(group.getCreationDate()));
                    if (group.getStudentsCount() == null) {
                        stmt.setNull(4, java.sql.Types.BIGINT);
                    } else {
                        stmt.setLong(4, group.getStudentsCount());
                    }
                    stmt.setInt(5, group.getTransferredStudents());
                    stmt.setInt(6, group.getAverageMark());
                    if (semesterId == null) {
                        stmt.setNull(7, java.sql.Types.INTEGER);
                    } else {
                        stmt.setInt(7, semesterId);
                    }
                    if (adminId == null) {
                        stmt.setNull(8, java.sql.Types.INTEGER);
                    } else {
                        stmt.setInt(8, adminId);
                    }
                    stmt.setInt(9, ownerId);
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
                String csv = toCsvLine(rs);
                StudyGroup group = new GroupBuilder().fromCSVString(csv, ";").buildLoaded();
                group.setAuthor(rs.getString("owner_login"));
                collection.addLoaded(group);
            }
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
        }
        return collection;
    }

    public void persistAddStudyGroup(StudyGroup group) {
        if (group.getAuthorName() == null || group.getAuthorName().isBlank()) {
            throw new IllegalStateException("Не указан автор для сохранения группы");
        }
        UserData userData = findUser(group.getAuthorName());
        if (userData == null) {
            throw new IllegalStateException("Пользователь не найден: " + group.getAuthorName());
        }
        try {
            addStudyGroup(group, userData.id());
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Не получилось сохранить группу в БД: " + e.getMessage(), e);
        }
    }

    public boolean persistRemoveStudyGroup(long groupId, String username) {
        UserData userData = findUser(username);
        if (userData == null) {
            throw new IllegalStateException("Пользователь не найден: " + username);
        }
        return removeStudyGroup(groupId, userData.id());
    }

    public boolean persistClearStudyGroups(String username) {
        UserData userData = findUser(username);
        if (userData == null) {
            throw new IllegalStateException("Пользователь не найден: " + username);
        }
        return clearUserStudyGroups(userData.id());
    }

    public boolean removeStudyGroup(long groupId, int ownerId) {
        String sql = "DELETE FROM study_groups WHERE id = ? AND owner_id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | IOException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return false;
        }
    }

    private Integer insertCoordinates(Connection conn, Coordinates coordinates) {
        String sql = "INSERT INTO coordinates(x, y) VALUES (?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, coordinates.getX());
            stmt.setLong(2, coordinates.getY());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    private Integer insertSemester(Connection conn, StudyGroup group) {
        if (group.getSemesterEnum() == null) {
            return null;
        }
        String sql = "INSERT INTO semesters(val) VALUES (?) ON CONFLICT (val) DO UPDATE SET val = EXCLUDED.val RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, group.getSemesterEnum().getName());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    private Integer insertColor(Connection conn, Person admin) {
        if (admin == null || admin.getHairColor() == null) {
            return null;
        }
        String sql = "INSERT INTO colors(val) VALUES (?) ON CONFLICT (val) DO UPDATE SET val = EXCLUDED.val RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, admin.getHairColor().getName());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            ServerLogger.log("Не получилось подключиться к БД: " + e.getMessage());
            return null;
        }
    }

    private Integer insertAdmin(Connection conn, Person admin) {
        if (admin == null) {
            return null;
        }
        Integer colorId = insertColor(conn, admin);
        String sql = "INSERT INTO admins(name, height, passportID, hair_color_id) VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        } catch (SQLException e) {
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

    public record UserData(int id, String login, byte[] passwordHash) {
    }
}