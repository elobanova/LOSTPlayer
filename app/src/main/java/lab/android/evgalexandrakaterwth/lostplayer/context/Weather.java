package lab.android.evgalexandrakaterwth.lostplayer.context;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lab.android.evgalexandrakaterwth.lostplayer.json.AbstractJSONHandler;

/**
 * Created by evgenijavstein on 08/07/15.
 */
public class Weather  extends AbstractJSONHandler {


    public static final String WEATHER = "Weather";
   // public static final String MAIN = "main";
    public static final String TEMP = "temp";
    public static final String HUMIDITY = "humidity";
    public static final String CLOUDS = "clouds";
    public static final String RAIN_VOLUME = "rainVolume";
    public static final String SNOW_VOLUME = "snowVolume";
    public static final String VISIBILITY = "visibility";


    //current temp
    private double temp;
    private double humidity;
    //clouds in percentage
    private double clouds;
    //rain volume of last 3h
    private double rainVolume;


    //snow volume of last 3h
    private double snowVolume;

    //10000 is default, best visibility
    private double visibility;

    public double getSnowVolume() {
        return snowVolume;
    }

    public void setSnowVolume(double snowVolume) {
        this.snowVolume = snowVolume;
    }




    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getClouds() {
        return clouds;
    }

    public void setClouds(double clouds) {
        this.clouds = clouds;
    }

    public double getRainVolume() {
        return rainVolume;
    }

    public void setRainVolume(double rainVolume) {
        this.rainVolume = rainVolume;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }


    @Override
    protected JSONObject prepareHeader() throws JSONException {
        JSONObject header=new JSONObject();




        JSONObject attributeTemp=new JSONObject();
        attributeTemp.put(NAME_PROPERTY, TEMP);
        attributeTemp.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeTemp.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeTemp.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeHumidity=new JSONObject();
        attributeHumidity.put(NAME_PROPERTY, HUMIDITY);
        attributeHumidity.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeHumidity.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeHumidity.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeClouds=new JSONObject();
        attributeClouds.put(NAME_PROPERTY, CLOUDS);
        attributeClouds.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeClouds.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeClouds.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeRainvolume=new JSONObject();
        attributeRainvolume.put(NAME_PROPERTY, RAIN_VOLUME);
        attributeRainvolume.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeRainvolume.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeRainvolume.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeSnowvolume=new JSONObject();
        attributeSnowvolume.put(NAME_PROPERTY, SNOW_VOLUME);
        attributeSnowvolume.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeSnowvolume.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeSnowvolume.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeVisibility=new JSONObject();
        attributeVisibility.put(NAME_PROPERTY, VISIBILITY);
        attributeVisibility.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeVisibility.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeVisibility.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONArray attributes=new JSONArray();

        attributes.put(attributeTemp);
        attributes.put(attributeHumidity);
        attributes.put(attributeClouds);
        attributes.put(attributeRainvolume);
        attributes.put(attributeSnowvolume);
        attributes.put(attributeVisibility);


        header.put(RELATION_PROPERTY, WEATHER);
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


        values.put(temp==0.0d?"?":"/"+doubleFormat(temp)+"/");
        values.put(humidity==0.0d?"?":"/"+doubleFormat(humidity)+"/");
        values.put(clouds==0.0d?"?":doubleFormat(clouds));
        values.put(rainVolume==0.0d?"?":"/"+doubleFormat(rainVolume)+"/");
        values.put(snowVolume==0.0d?"?":"/"+doubleFormat(snowVolume)+"/");
        values.put(visibility==0.0d?"?":"/"+doubleFormat(visibility)+"/");


        obj.put(VALUES_PROPERTY,values );
        data.put(obj);
        return data;
    }
}
