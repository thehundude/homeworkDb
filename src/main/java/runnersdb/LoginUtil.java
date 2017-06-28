package runnersdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Created by tamas on 2017. 06. 27..
 */
public class LoginUtil {
    private static String usernameInput;
    private static String passwordInput;

    public static User login () throws SQLException {
        Connection connection = DbUtil.getConnection();

        Scanner reader = new Scanner(System.in);

        System.out.print("Írd be a felhasználóneved: ");
        usernameInput = reader.nextLine();

        System.out.print("Írd be a jelszavad: ");
        passwordInput = reader.nextLine();

        PreparedStatement ps = connection.prepareStatement("SELECT * FROM dbuser WHERE user_name='" + usernameInput +
                "' AND user_password='" + passwordInput + "'");

        ResultSet rs = ps.executeQuery();

        rs.next();

        User loggedInUser = new User();

        loggedInUser.setUserId(rs.getInt(1));
        loggedInUser.setUserName((rs.getString(2)));
        loggedInUser.setUserPassword(rs.getString(3));
        loggedInUser.setUserCanEdit(rs.getBoolean(4));

        connection.close();

        return loggedInUser;
    }
}
