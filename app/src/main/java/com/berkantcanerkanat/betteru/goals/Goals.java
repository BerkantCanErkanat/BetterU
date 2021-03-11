package com.berkantcanerkanat.betteru.goals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Goals {
    public String title;
    public String madeDate;
    public String checkDate;
    public String isCameTrue;
    public String uid;
    public final long daysBetweenDates;
    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public Goals(String title, String madeDate, String isCameTrue, String uid,String checkDate) {
        this.title = title;
        this.madeDate = madeDate;
        this.isCameTrue = isCameTrue;
        this.uid = uid;
        this.checkDate = checkDate;
        daysBetweenDates = getDaysBetweenDates(madeDate,checkDate);
    }
    public long getDaysBetweenDates(String madeDate, String checkDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(madeDate);
            endDate = dateFormat.parse(checkDate);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays;
    }
    private long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }
}
