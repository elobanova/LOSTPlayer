package lab.android.evgalexandrakaterwth.lostplayer.context;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lab.android.evgalexandrakaterwth.lostplayer.json.AbstractJSONHandler;

/**
 * Created by evgenijavstein on 08/07/15.
 */
//EXTERNAL
public class SimpleLocation  extends AbstractJSONHandler {
    public static final String LOCATION = "Location";
    public static final String M_LATITUDE = "mLatitude";
    public static final String M_LONGITUDE = "mLongitude";
    private double mLatitude;
    private double mLongitude;




    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }


    @Override
    protected JSONObject prepareHeader() throws JSONException {
        JSONObject header=new JSONObject();


        JSONObject attributeMLatitude=new JSONObject();
        attributeMLatitude.put(NAME_PROPERTY, M_LATITUDE);
        attributeMLatitude.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeMLatitude.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeMLatitude.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeMLongtitude=new JSONObject();
        attributeMLongtitude.put(NAME_PROPERTY, M_LONGITUDE);
        attributeMLongtitude.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeMLongtitude.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeMLongtitude.put(WEIGHT_PROPERTY, WEIGHT_VALUE);



        JSONArray attributes=new JSONArray();
        attributes.put(attributeMLatitude);
        attributes.put(attributeMLongtitude);


        header.put(RELATION_PROPERTY, LOCATION);
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
        values.put(mLatitude==0.0d?"?":mLatitude);
        values.put(mLongitude==0.0d?"?":mLongitude);


        obj.put(VALUES_PROPERTY,values );
        data.put(obj);
        return data;
    }
}
