package runnersdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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
}
