package my.messages;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        LoginView loginView = new LoginView(true);

        loginView.show(null);
    }

}
