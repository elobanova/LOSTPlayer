package lab.android.evgalexandrakaterwth.lostplayer.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by evgenijavstein on 12/07/15.
 */
public abstract class AbstractJSONHandler {

    public static final String HEADER_PROPERTY = "header";
    public static final String DATA_PROPERTY = "data";
    public static final String VALUES_PROPERTY = "values";
    public static final String RELATION_PROPERTY = "relation";
    public static final String ATTRIBUTES_PROPERTY = "attributes";
    public static final String NAME_PROPERTY = "name";
    public static final String TYPE_PROPERTY = "type";
    public static final String CLASS_PROPERTY = "class";
    public static final String WEIGHT_PROPERTY = "weight";
    public static final String AUDIO_RELATION_NAME = "audio";
    public static final String SPARSE_ATTRIBUTE_NAME = "sparse";
    public static final String NUMERIC_VALUE_ATTRIBUTE = "numeric";
    public static final String NOMINAL_VALUE_ATTRIBUTE = "nominal";
    public static final String STRING_VALUE_ATTRIBUTE = "string";
    public static final String LABEL_ATTRIBUTE = "labels";
    public static final boolean IS_ATTRIBUTE_CLASS = false;
    public static final double WEIGHT_VALUE = 1.0;




    /**
     * Returns JSON representation as needed by parser of lerning lib
     *
     * @return
     */
    public JSONObject getAsJSON() {
        JSONObject motionFeatures = new JSONObject();

        try {
            motionFeatures.put(HEADER_PROPERTY, prepareHeader());
            motionFeatures.put(DATA_PROPERTY, prepareData());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return motionFeatures;
    }

    /**
     * Forbids scientific double representaiton
     * @return
     */
    protected String doubleFormat(double d){
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);
        String s=df.format(d);
        if(!s.contains("."))//workaround accept maximum fraction digits, but produce 0.0 for 0 etc.
            s=s+".0";
        return s;
    }

    protected abstract JSONObject prepareHeader() throws JSONException;

    protected abstract JSONArray prepareData() throws JSONException;
}
