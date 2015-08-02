package lab.android.evgalexandrakaterwth.lostplayer.context;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lab.android.evgalexandrakaterwth.lostplayer.json.AbstractJSONHandler;

/**
 * Created by evgenijavstein on 08/07/15.
 */
//GLOBAL
public class UserTime extends AbstractJSONHandler {


    public static final String SEASON = "season";
    public static final String DAY_OF_WEEK = "dayOfWeek";
    public static final String USER_TIME = "Time";
    public static final String TIME_OF_DAY = "timeOfDay";
    private int season;
    private int dayOfWeek;
    private int timeOfDay;


    public int getSeason() {
        return season;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getTimeOfDay() {
        return timeOfDay;
    }

    public static String[] getSeasons() {
        return seasons;
    }

    public static String[] getDaysOfWeek() {
        return daysOfWeek;
    }


    private static final String seasons[] = {
            "Winter", "Winter", "Spring", "Spring", "Spring", "Summer",
            "Summer", "Summer", "Fall", "Fall", "Fall", "Winter"
    };

    private static final Integer seasonsIntegers[] = {
            1, 1, 2, 2, 2, 3,
            3, 3, 4, 4, 4, 1
    };


    //to get string value later on if needed
    private static final String daysOfWeek[]={
            "","SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY","THURSDAY", "FRIDAY", "SATURDAY"
    };
    //we take sunday as first day as accepted in christian world
    private static Integer daysOfWeekIntegers[]={
            0,1, 2, 3,4, 5, 6,7
    };
    public void setTime( Date date ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        this.season= seasonsIntegers[ cal.get(Calendar.MONTH) ];

        this.dayOfWeek=daysOfWeekIntegers[cal.get(Calendar.DAY_OF_WEEK)];
        this.timeOfDay=Integer.parseInt(new SimpleDateFormat("HH").format(date));
    }


    @Override
    protected JSONObject prepareHeader() throws JSONException {
        JSONObject header=new JSONObject();


        JSONObject attributeSeason=new JSONObject();
        attributeSeason.put(NAME_PROPERTY, SEASON);
        attributeSeason.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeSeason.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeSeason.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeDayOfWeek=new JSONObject();
        attributeDayOfWeek.put(NAME_PROPERTY, DAY_OF_WEEK);
        attributeDayOfWeek.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeDayOfWeek.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeDayOfWeek.put(WEIGHT_PROPERTY, WEIGHT_VALUE);



        JSONObject attributeTimeOfDay=new JSONObject();
        attributeTimeOfDay.put(NAME_PROPERTY, TIME_OF_DAY);
        attributeTimeOfDay.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeTimeOfDay.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeTimeOfDay.put(WEIGHT_PROPERTY, WEIGHT_VALUE);


        JSONArray attributes=new JSONArray();
        attributes.put(attributeSeason);
        attributes.put(attributeDayOfWeek);
        attributes.put(attributeTimeOfDay);

        header.put(RELATION_PROPERTY, USER_TIME);
        header.put(ATTRIBUTES_PROPERTY,attributes);
        return header;
    }

    @Override
    protected JSONArray prepareData() throws JSONException {
        JSONArray data=new JSONArray();
        JSONObject obj=new JSONObject();
        obj.put(SPARSE_ATTRIBUTE_NAME,false);
        obj.put(WEIGHT_PROPERTY,WEIGHT_VALUE);

        JSONArray values=new JSONArray();
        values.put(season);
        values.put(dayOfWeek);
        values.put(timeOfDay);


        obj.put(VALUES_PROPERTY,values );
        data.put(obj);
        return data;
    }
}
