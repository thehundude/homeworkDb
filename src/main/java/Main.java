import runnersdb.LoginUtil;

import java.sql.SQLException;

/**
 * Created by tamas on 2017. 06. 27..
 */
public class Main {
    public static void main(String[] args) {
        // login
        try {
            LoginUtil.login();
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.println("Nem létezik ilyen felhasználó!");
        }
    }
}
