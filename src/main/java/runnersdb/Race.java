package runnersdb;

import java.util.Date;

/**
 * Created by tamas on 2017. 07. 01..
 */
public class Race {
    private int raceId;
    private String raceName;
    private Date raceDate;

    public int getRaceId() {
        return raceId;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public Date getRaceDate() {
        return raceDate;
    }

    public void setRaceDate(Date raceDate) {
        this.raceDate = raceDate;
    }
}
