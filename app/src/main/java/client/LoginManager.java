package client;

public class LoginManager {
    private String username;
    private String password;

    public void askCredentials(IOHandler ioHandler) {
        ioHandler.print("Введите имя пользователя: ");
        this.username = ioHandler.readLine().trim();
        ioHandler.print("Введите пароль: ");
        this.password = ioHandler.readLine();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
