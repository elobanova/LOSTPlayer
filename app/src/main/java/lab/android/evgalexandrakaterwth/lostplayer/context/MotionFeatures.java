package lab.android.evgalexandrakaterwth.lostplayer.context;


import lab.android.evgalexandrakaterwth.lostplayer.json.AbstractJSONHandler;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by evgenijavstein on 08/07/15.
 */
//INTERNAL
public class MotionFeatures extends AbstractJSONHandler {

    private String AVERAGE;
    private String MAX;
    private String MIN;
    private String SMA;
    private String STDEV;

    private String relation;
    private double average;
    private double max;
    private double min;
    private double standardDeviation;

    public MotionFeatures(String relation) {
        this.relation = relation;
        this.AVERAGE = relation + "Average";
        this.MAX = relation + "Max";
        this.MIN = relation + "Min";
        this.SMA = relation + "Sma";
        this.STDEV = relation + "Stdev";


    }

    public void setSma(double sma) {
        this.sma = sma;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    private double sma;


    public double getAverage() {
        return average;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public double getSma() {
        return sma;
    }


    protected JSONObject prepareHeader() throws JSONException {

        JSONObject header = new JSONObject();


        JSONObject attributeAverage = new JSONObject();
        attributeAverage.put(NAME_PROPERTY, AVERAGE);
        attributeAverage.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeAverage.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeAverage.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeMax = new JSONObject();
        attributeMax.put(NAME_PROPERTY, MAX);
        attributeMax.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeMax.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeMax.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeMin = new JSONObject();
        attributeMin.put(NAME_PROPERTY, MIN);
        attributeMin.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeMin.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeMin.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeSma = new JSONObject();
        attributeSma.put(NAME_PROPERTY, SMA);
        attributeSma.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeSma.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeSma.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONObject attributeStdev = new JSONObject();
        attributeStdev.put(NAME_PROPERTY, STDEV);
        attributeStdev.put(TYPE_PROPERTY, NUMERIC_VALUE_ATTRIBUTE);
        attributeStdev.put(CLASS_PROPERTY, IS_ATTRIBUTE_CLASS);
        attributeStdev.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONArray attributes = new JSONArray();
        attributes.put(attributeAverage);
        attributes.put(attributeMax);
        attributes.put(attributeMin);
        attributes.put(attributeSma);

        header.put(RELATION_PROPERTY, relation);
        header.put(ATTRIBUTES_PROPERTY, attributes);
        return header;
    }

    protected JSONArray prepareData() throws JSONException {
        JSONArray data = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put(SPARSE_ATTRIBUTE_NAME, false);
        obj.put(WEIGHT_PROPERTY, WEIGHT_VALUE);

        JSONArray values = new JSONArray();
        values.put(average);
        values.put(max);
        values.put(min);
        values.put(sma);

        obj.put(VALUES_PROPERTY, values);
        data.put(obj);
        return data;
    }


}
