package lab.android.evgalexandrakaterwth.lostplayer.context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lab.android.evgalexandrakaterwth.lostplayer.json.AbstractJSONHandler;

/**
 * Created by evgenijavstein on 04/07/15.
 */
public class ContextFeatures extends AbstractJSONHandler {
    protected MotionFeatures[] accelerationFeatures;
    protected MotionFeatures[] gyroscopeFeatures;
    protected Weather weather;
    protected SimpleLocation location;
    protected UserTime userTime;
    private String relation = "contextInstance";

    public ContextFeatures() {
    }

    public MotionFeatures[] getAccelerationFeatures() {
        return accelerationFeatures;
    }

    public MotionFeatures[] getGyroscopeFeatures() {
        return gyroscopeFeatures;
    }

    public Weather getWeather() {
        return weather;
    }

    public SimpleLocation getLocation() {
        return location;
    }

    public UserTime getUserTime() {
        return userTime;
    }

    public void setAccelerationFeatures(MotionFeatures[] accelerationFeatures) {
        this.accelerationFeatures = accelerationFeatures;
    }

    public void setGyroscopeFeatures(MotionFeatures[] gyroscopeFeatures) {
        this.gyroscopeFeatures = gyroscopeFeatures;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public void setLocation(SimpleLocation simpleLocation) {
        this.location = simpleLocation;
    }

    public void setUserTime(UserTime userTime) {
        this.userTime = userTime;
    }

    /**
     * Gathers all headers from sub context instances
     *
     * @return
     * @throws JSONException
     */
    @Override
    protected JSONObject prepareHeader() throws JSONException,NullPointerException {
        if (this.weather == null) {
            this.weather = new Weather();//we create an empty object
            //in that case it produces a data filled with question marks
            //this is how the ML lib expects the data
        }
        if (this.location == null) {
            this.location = new SimpleLocation();//see above for weather
        }

        //we assume that acceleration, gyroscope, user time is always availbale here
        //corresponding checks need to be implemented on higher level

        JSONArray attributes = concatArray(
                getJSONDataList(accelerationFeatures[0], AbstractJSONHandler.HEADER_PROPERTY, AbstractJSONHandler.ATTRIBUTES_PROPERTY),
                getJSONDataList(accelerationFeatures[1], AbstractJSONHandler.HEADER_PROPERTY, AbstractJSONHandler.ATTRIBUTES_PROPERTY),
                getJSONDataList(accelerationFeatures[2], AbstractJSONHandler.HEADER_PROPERTY, AbstractJSONHandler.ATTRIBUTES_PROPERTY),
                getJSONDataList(gyroscopeFeatures[0], AbstractJSONHandler.HEADER_PROPERTY, AbstractJSONHandler.ATTRIBUTES_PROPERTY),
                getJSONDataList(gyroscopeFeatures[1], AbstractJSONHandler.HEADER_PROPERTY, AbstractJSONHandler.ATTRIBUTES_PROPERTY),
                getJSONDataList(gyroscopeFeatures[2], AbstractJSONHandler.HEADER_PROPERTY, AbstractJSONHandler.ATTRIBUTES_PROPERTY),
                getJSONDataList(this.userTime, AbstractJSONHandler.HEADER_PROPERTY, AbstractJSONHandler.ATTRIBUTES_PROPERTY),
                getJSONDataList(this.location, AbstractJSONHandler.HEADER_PROPERTY, AbstractJSONHandler.ATTRIBUTES_PROPERTY),
                getJSONDataList(this.weather, AbstractJSONHandler.HEADER_PROPERTY, AbstractJSONHandler.ATTRIBUTES_PROPERTY)
        );

        JSONObject header = new JSONObject();
        header.put(RELATION_PROPERTY, relation);
        header.put(ATTRIBUTES_PROPERTY, attributes);

        return header;
    }

    /**
     * Gathers all data from sub context instances
     *
     * @return
     * @throws JSONException
     */
    @Override
    protected JSONArray prepareData() throws JSONException,NullPointerException {
        JSONArray values = concatArray(
                getJSONDataList(accelerationFeatures[0], AbstractJSONHandler.DATA_PROPERTY, AbstractJSONHandler.VALUES_PROPERTY),
                getJSONDataList(accelerationFeatures[1], AbstractJSONHandler.DATA_PROPERTY, AbstractJSONHandler.VALUES_PROPERTY),
                getJSONDataList(accelerationFeatures[2], AbstractJSONHandler.DATA_PROPERTY, AbstractJSONHandler.VALUES_PROPERTY),
                getJSONDataList(gyroscopeFeatures[0], AbstractJSONHandler.DATA_PROPERTY, AbstractJSONHandler.VALUES_PROPERTY),
                getJSONDataList(gyroscopeFeatures[1], AbstractJSONHandler.DATA_PROPERTY, AbstractJSONHandler.VALUES_PROPERTY),
                getJSONDataList(gyroscopeFeatures[2], AbstractJSONHandler.DATA_PROPERTY, AbstractJSONHandler.VALUES_PROPERTY),
                getJSONDataList(this.userTime, AbstractJSONHandler.DATA_PROPERTY, AbstractJSONHandler.VALUES_PROPERTY),
                getJSONDataList(this.location, AbstractJSONHandler.DATA_PROPERTY, AbstractJSONHandler.VALUES_PROPERTY),
                getJSONDataList(this.weather, AbstractJSONHandler.DATA_PROPERTY, AbstractJSONHandler.VALUES_PROPERTY)
        );

        JSONArray data = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put(SPARSE_ATTRIBUTE_NAME, false);
        obj.put(WEIGHT_PROPERTY, WEIGHT_VALUE);
        obj.put(VALUES_PROPERTY, values);
        data.put(obj);
        return data;
    }

    /**
     * Retrieves data or attribute description list for a single sub context
     *
     * @param featuresData
     * @param field
     * @param array
     * @return
     * @throws JSONException
     */
    private JSONArray getJSONDataList(AbstractJSONHandler featuresData, String field, String array) throws JSONException {
        JSONArray jsonFeatureDataList = null;

        JSONObject jsonFeaturesData = featuresData.getAsJSON();
        if (field.equals(AbstractJSONHandler.HEADER_PROPERTY)) {
            jsonFeatureDataList = jsonFeaturesData.
                    getJSONObject(field).getJSONArray(array);
        } else if (field.equals(AbstractJSONHandler.DATA_PROPERTY)) {
            jsonFeatureDataList = jsonFeaturesData.getJSONArray(field).getJSONObject(0).getJSONArray(array);
        }

        return jsonFeatureDataList;
    }

    private JSONArray concatArray(JSONArray... arrs)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                result.put(arr.get(i));
            }
        }
        return result;
    }
}