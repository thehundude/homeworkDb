package runnersdb;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by tamas on 2017. 07. 01..
 */
public class Trainer {
    private int trainerId;
    private int orgId;
    private String name;

    private int performanceScore;
    private int countFirst;
    private int countSecond;
    private int countThird;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(int performanceScore) {
        this.performanceScore = performanceScore;
    }

    public int getCountFirst() {
        return countFirst;
    }

    public void setCountFirst(int countFirst) {
        this.countFirst = countFirst;
    }

    public int getCountSecond() {
        return countSecond;
    }

    public void setCountSecond(int countSecond) {
        this.countSecond = countSecond;
    }

    public int getCountThird() {
        return countThird;
    }

    public void setCountThird(int countThird) {
        this.countThird = countThird;
    }

    public static void listTopTrainers() {
        Connection connection = DbUtil.getConnection();
        ArrayList<Trainer> trainers = new ArrayList<Trainer>();

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT trainer.trainer_name, " +
                    "trainer.org_id, COUNT(athlete_race.first_place) " +
                    "FROM ((trainer FULL OUTER JOIN athlete ON trainer.trainer_id = athlete.trainer_id) " +
                    "FULL OUTER JOIN athlete_race ON athlete.athlete_id = athlete_race.first_place) " +
                    "GROUP BY trainer.trainer_name, trainer.org_id");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Trainer trainer = new Trainer();

                trainer.setName(rs.getString(1));
                trainer.setOrgId(rs.getInt(2));
                trainer.setCountFirst(rs.getInt(3));

                trainers.add(trainer);
            }

            ps = connection.prepareStatement("SELECT COUNT(athlete_race.second_place)\n" +
                    "FROM ((trainer\n" +
                    "FULL OUTER JOIN athlete ON trainer.trainer_id = athlete.trainer_id)\n" +
                    "FULL OUTER JOIN athlete_race ON athlete.athlete_id = athlete_race.second_place)\n" +
                    "GROUP BY trainer.trainer_name, trainer.org_id\n" +
                    "ORDER BY trainer.trainer_name");

            rs = ps.executeQuery();

            for (Trainer item : trainers) {
                rs.next();
                item.countSecond = rs.getInt(1);
            }

            ps = connection.prepareStatement("SELECT COUNT(athlete_race.third_place)\n" +
                    "FROM ((trainer\n" +
                    "FULL OUTER JOIN athlete ON trainer.trainer_id = athlete.trainer_id)\n" +
                    "FULL OUTER JOIN athlete_race ON athlete.athlete_id = athlete_race.third_place)\n" +
                    "GROUP BY trainer.trainer_name, trainer.org_id\n" +
                    "ORDER BY trainer.trainer_name");

            rs = ps.executeQuery();

            for (Trainer item : trainers) {
                rs.next();
                item.countThird = rs.getInt(1);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Trainer item : trainers) {
            item.setPerformanceScore(item.countFirst * 3 + item.countSecond * 2 + item.countThird * 1);
        }

        Collections.sort(trainers, new Comparator<Trainer>() {
            public int compare(Trainer o1, Trainer o2) {
                return o2.getPerformanceScore() - o1.getPerformanceScore();
            }
        });

        System.out.print("A legjobb edzők\nNév\tEgyesület\tI.\tII.\tIII. helyezések\n");
        for (int i = 0; i < trainers.size(); i++) {
            System.out.print(trainers.get(i).getName() + "\t");

            try {
                connection = DbUtil.getConnection();

                PreparedStatement ps = connection.prepareStatement("SELECT org_name FROM organization WHERE org_id = '" +
                trainers.get(i).getOrgId() + "'");
                ResultSet rs = ps.executeQuery();
                rs.next();
                System.out.print(rs.getString(1) + "\t");

                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.print(trainers.get(i).getCountFirst() + "\t" + trainers.get(i).getCountSecond() + "\t" +
            trainers.get(i).getCountThird() + "\n");
        }

        // apache poi
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Edzők");

        int rownum = 1;
        Row row = sheet.createRow(rownum);
        String[] firstRow = {"Edző", "I. helyek", "II. helyek", "III. helyek"};
        for (int i = 0; i < firstRow.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(firstRow[i]);
        }

        for (Trainer item : trainers) {
            row = sheet.createRow(rownum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(item.getName());
            cell = row.createCell(1);
            cell.setCellValue(item.getCountFirst());
            cell = row.createCell(2);
            cell.setCellValue(item.getCountSecond());
            cell = row.createCell(3);
            cell.setCellValue(item.getCountThird());
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();

        try {
            FileOutputStream out = new FileOutputStream(new File("C:\\testdir\\trainers" + dateFormat.format(date) + ".xls"));
            workbook.write(out);
            out.close();
            System.out.println("Excel fájlba elmentve!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
