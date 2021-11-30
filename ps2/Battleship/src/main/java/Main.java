import controllers.Controller;
import controllers.LoginMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner inputStream = new Scanner(System.in);
        Controller loginMenu = new LoginMenu(inputStream);

        loginMenu.run();
    }
}
