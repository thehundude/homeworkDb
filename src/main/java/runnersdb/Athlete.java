package runnersdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by tamas on 2017. 07. 01..
 */
public class Athlete {
    private int athleteId;
    private int trainerId;
    private int orgId;
    private String athleteName;
    private Date dob;
    private String nationality;

    private int firstPlace;
    private int secondPlace;
    private int thirdPlace;

    public int getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(int athleteId) {
        this.athleteId = athleteId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getAthleteName() {
        return athleteName;
    }

    public void setAthleteName(String athleteName) {
        this.athleteName = athleteName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public int getFirstPlace() {
        return firstPlace;
    }

    public void setFirstPlace(int firstPlace) {
        this.firstPlace = firstPlace;
    }

    public int getSecondPlace() {
        return secondPlace;
    }

    public void setSecondPlace(int secondPlace) {
        this.secondPlace = secondPlace;
    }

    public int getThirdPlace() {
        return thirdPlace;
    }

    public void setThirdPlace(int thirdPlace) {
        this.thirdPlace = thirdPlace;
    }

    // lekérdezésekhez használt függvények
    // klub futóinak kiírása
    public static void clubAthletes() {
        System.out.println("A Baranya SE futói:\nFutó neve\tSzül.idő\tNemzetiség\tI.\tII.\tIII.");

        Connection connection = DbUtil.getConnection();

        ArrayList<Athlete> athletes = new ArrayList();

        try {
            // lekérjük a futók adatait plusz az első helyek számát
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT athlete.athlete_name, athlete.dob, athlete.nationality, COUNT(athlete_race.first_place) " +
                            "FROM athlete " +
                            "FULL OUTER JOIN athlete_race ON athlete_id = athlete_race.first_place " +
                            "WHERE org_id = 1 " +
                            "GROUP BY athlete.athlete_name, athlete.dob, athlete.nationality " +
                            "ORDER BY athlete_name ASC");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Athlete athlete = new Athlete();

                athlete.setAthleteName(rs.getString(1));
                athlete.setDob(rs.getDate(2));
                athlete.setNationality(rs.getString(3));
                athlete.setFirstPlace(rs.getInt(4));

                athletes.add(athlete);
            }

            // itt csak a második helyek számát kérjük le
            ps = connection.prepareStatement("SELECT COUNT(athlete_race.second_place)\n" +
                    "FROM athlete\n" +
                    "FULL OUTER JOIN athlete_race ON athlete.athlete_id = athlete_race.second_place\n" +
                    "WHERE org_id = 1\n" +
                    "GROUP BY athlete.athlete_name, athlete.dob, athlete.nationality\n" +
                    "ORDER BY athlete_name ASC");

            rs = ps.executeQuery();

            while (rs.next()) {
                for (Athlete item : athletes) {
                    item.secondPlace = rs.getInt(1);
                }
            }

            // itt csak a harmadik helyek számát kérjük le
            ps = connection.prepareStatement("SELECT COUNT(athlete_race.third_place)\n" +
                    "FROM athlete\n" +
                    "FULL OUTER JOIN athlete_race ON athlete.athlete_id = athlete_race.third_place\n" +
                    "WHERE org_id = 1\n" +
                    "GROUP BY athlete.athlete_name, athlete.dob, athlete.nationality\n" +
                    "ORDER BY athlete_name ASC");

            rs = ps.executeQuery();

            while (rs.next()) {
                for (Athlete item : athletes) {
                    item.thirdPlace = rs.getInt(1);
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Athlete item : athletes) {
            System.out.print(item.athleteName + "\t" + item.dob + "\t" + item.nationality + "\t" + item.firstPlace + "\t" +
            item.secondPlace + "\t" + item.thirdPlace + "\n");
        }
    }

    // futó szerkesztése
    public static void modifyAthlete() {
        Connection connection = DbUtil.getConnection();
        Scanner reader = new Scanner(System.in);

        System.out.print("Írd be a futó nevét, akit módosítani szeretnél! ");
        String modifyAthleteName = reader.nextLine();

        PreparedStatement ps = null;
        ResultSet rs = null;
        Athlete modifyAthlete = new Athlete();

        String modifyAthleteTrainerName = null;
        String modifyAthleteOrgName = null;

        try {
            ps = connection.prepareStatement("SELECT * FROM athlete WHERE athlete_name = '" + modifyAthleteName + "'");
            rs = ps.executeQuery();
            rs.next();
            modifyAthlete.setAthleteId(rs.getInt(1));
            modifyAthlete.setTrainerId(rs.getInt(2));
            modifyAthlete.setOrgId(rs.getInt(3));
            modifyAthlete.setAthleteName(rs.getString(4));
            modifyAthlete.setDob(rs.getDate(5));
            modifyAthlete.setNationality(rs.getString(6));
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.print("Nincs ilyen futó!");
            return;
        }

        try {
            ps = connection.prepareStatement("SELECT org_name FROM organization WHERE org_id = '" + modifyAthlete.orgId + "'");
            rs = ps.executeQuery();
            rs.next();
            modifyAthleteOrgName = rs.getString(1);
            ps = connection.prepareStatement("SELECT trainer_name FROM trainer WHERE trainer_id = '" + modifyAthlete.trainerId + "'");
            rs = ps.executeQuery();
            rs.next();
            modifyAthleteTrainerName = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.print("A futó jelenlegi adatai:\nNeve: " + modifyAthlete.athleteName + "\nSzületési ideje: " +
            modifyAthlete.dob + "\nNemzetisége: " + modifyAthlete.nationality + "\nKlubja: " + modifyAthleteOrgName +
            "\nEdzője: " + modifyAthleteTrainerName + "\n\nAdd meg az új adatokat!\n");

        System.out.print("Írd be a futó nevét:");
        modifyAthlete.athleteName = reader.nextLine();

        System.out.print("Mikor született? (ÉÉÉÉ-HH-NN formátumban)");
        String dobString = reader.nextLine();
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        try {
            modifyAthlete.dob = df.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.print("Milyen nemzetiségű?");
        modifyAthlete.nationality = reader.nextLine();

        System.out.print("Melyik sportegyesület tagja?");
        String modifyAthleteOrg = reader.nextLine();
        try {
            ps = connection.prepareStatement("SELECT org_id FROM organization WHERE org_name = '" + modifyAthleteOrg + "'");
            rs = ps.executeQuery();
            rs.next();
            modifyAthlete.orgId = rs.getInt(1);
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.print("Nincs ilyen sportegyesület!");
        }

        System.out.print("Ki az edzője?");
        String modifyAthleteTrainer = reader.nextLine();
        try {
            ps = connection.prepareStatement("SELECT trainer_id FROM trainer WHERE trainer_name = '" + modifyAthleteTrainer + "'");
            rs = ps.executeQuery();
            rs.next();
            modifyAthlete.trainerId = rs.getInt(1);
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.print("Nincs ilyen edző!");
        }

        try {
            ps = connection.prepareStatement("UPDATE athlete SET trainer_id = '" + modifyAthlete.trainerId +
                "', org_id = '" + modifyAthlete.orgId + "', athlete_name = '" + modifyAthlete.athleteName +
                "', dob = '" + modifyAthlete.dob + "', nationality = '" + modifyAthlete.nationality + "' WHERE " +
                "athlete_id = " + modifyAthlete.athleteId);
            ps.executeUpdate();
            System.out.print("Sikerült az adatok frissítése!");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // futó hozzáadása
    public static void addAthlete(){
        Connection connection = DbUtil.getConnection();
        Scanner reader = new Scanner(System.in);

        // adatok bekérése
        System.out.print("Írd be a futó nevét:");
        String athleteName = reader.nextLine();
        System.out.print("Mikor született? (ÉÉÉÉ-HH-NN formátumban)");
        String athleteDob = reader.nextLine();
        System.out.print("Milyen nemzetiségű?");
        String athleteNationality = reader.nextLine();

        System.out.print("Melyik sportegyesület tagja?");
        String athleteOrg = reader.nextLine();
        int athleteOrgId = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement("SELECT org_id FROM organization WHERE org_name = '" + athleteOrg + "'");
            rs = ps.executeQuery();
            rs.next();
            athleteOrgId = rs.getInt(1);
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.print("Nincs ilyen sportegyesület!");
        }

        System.out.print("Ki az edzője?");
        String athleteTrainer = reader.nextLine();
        int athleteTrainerId = 0;
        try {
            ps = connection.prepareStatement("SELECT trainer_id FROM trainer WHERE trainer_name = '" + athleteTrainer + "'");
            rs = ps.executeQuery();
            rs.next();
            athleteTrainerId = rs.getInt(1);
        } catch (SQLException e) {
            // e.printStackTrace();
            System.out.print("Nincs ilyen edző!");
        }

        try {
            ps = connection.prepareStatement("INSERT INTO athlete (athlete_name, dob, nationality, org_id, trainer_id)\n" +
                    "VALUES ('" + athleteName + "', '" + athleteDob + "', '" + athleteNationality + "', '" + Integer.toString(athleteOrgId) +
                    "', '" + Integer.toString(athleteTrainerId) + "')");
            ps.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
