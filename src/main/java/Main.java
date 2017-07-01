import runnersdb.Athlete;
import runnersdb.LoginUtil;
import runnersdb.MenuUtil;
import runnersdb.User;

import java.sql.SQLException;

/**
 * Created by tamas on 2017. 06. 27..
 */
public class Main {
    public static void main(String[] args) {
        // ebbe kerülnek a belépett felhasználó adatai
        User loggedInUser = new User();

        // login
        try {
            loggedInUser = LoginUtil.login();
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Nem létezik ilyen felhasználó vagy hibás a jelszó!");
            return;
        }

        // menü meghívása
        MenuUtil.mainMenu(loggedInUser.isUserCanEdit());
    }
}
