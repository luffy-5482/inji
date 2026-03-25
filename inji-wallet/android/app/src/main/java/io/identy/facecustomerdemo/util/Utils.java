package io.identy.facecustomerdemo.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import com.identy.face.FaceMatch;
import com.identy.face.IdentyFaceSdk;
import com.identy.face.TemplateOutput;
import com.identy.face.enums.EnrollmentFormat;
import com.identy.face.enums.FaceTemplate;
import com.identy.face.users.IdentyUser;


import org.json.JSONObject;
import java.io.ByteArrayOutputStream;import java.io.PrintWriter;
import java.io.StringWriter;import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashSet;import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Utils {
    public static final String LICENSE_FILE = "5178-io.mosip.residentapp-29-03-2026.lic";
    public static final String PREFS_NAME = "FACE_QA_APP";

    public static final String TEST_LAUNCHED = "TEST_LAUNCHED";

    public static final String REQUIRED_TEMPLATES = "REQUIRED_TEMPLATES";

    public static final String ENCRYPTION = "ENCRYPTION";
    public static final String DECRYPTION = "DECRYPTION ";
    public static final String ASSISTED_MODE = "ASSISTED_MODE ";
    // OBJECT DETECTION
    public static final String DETECT_MASK = "DETECT_MASK ";
    public static final String DETECT_GLASSES = "DETECT_GLASSES ";
    public static final String DETECT_HAND = "DETECT_HAND ";
    public static final String DETECT_HAT = "DETECT_HAT ";
    public static final String DETECT_SUNGLASSES = "DETECT_SUNGLASSES ";
    public static final String DETECT_EYE_OPENNESS = "DETECT_EYE_OPENNESS ";
    public static final String DETECT_MOUTH_CLOSURE = "DETECT_MOUTH_CLOSURE ";
    public static final String DETECT_STRICT_CAPTURE = "DETECT_STRICT_CAPTURE ";

    // OD ENDS    public static final String ASSISTED_MODE = "ASSISTED_MODE";
    public static final String TRAINING = "TRAINING";
    public static final String ICAO = "ICAO";

    public static final String LIVENESS_SERVER = "LIVENESS_SERVER";

    public static final String LIVENESS_WITHIN_SDK_OPTION = "LIVENESS_WITHIN_SDK_OPTION";
    public static final String HD_CAPTURE = "HD_CAPTURE";
    public static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";

    public static final String ENROL_USER = "ENROL_USER";
    public static final String UI_OPTION = "UI_OPTION";
    public static final String OFFLINE_MODE = "OFFLINE_MODE";
    public static final String ENROLMENT_MODE = "ENROLMENT_MODE";
    public static final String SAVE_ENROLMENT = "SAVE_ENROLMENT";
    public static final String RETAKE = "RETAKE";
    public static final String TIMEOUT_CHECK = "TIMEOUT_60";
    public static final String DISPLAY_RESULT = "DISPLAY_RESULT";
    public static final String AS_LEVEL = "AS_LEVEL";
    public static final String VERIFY_WITH_TEMPLATE_LIMITS = "VERIFY_WITH_TEMPLATE_LIMITS";
    public static final String WITH_TEMPLATES_METHODS = "WITH_TEMPLATES_METHODS";
    public static final String WITH_TEMPLATES_METHODS_SAVE_TEMPLATES = "WITH_TEMPLATES_METHODS_SAVE_TEMPLATES";
    public static final String TEST_NAME = "TEST_NAME";

    public static final String VERIFY_WITH_TEMPLATES_TIME = "VERIFY_WITH_TEMPLATES_TIME";
    public static final String VERIFY_WITH_TEMPLATES_REPEATS = "VERIFY_WITH_TEMPLATES_REPEATS";
    public static final String ONE2ONE_ONE2N = "ONE2ONE_ONE2N";
    public static final String IMPORTED_ONE_N = "IMPORTED_ONE_N";
    public static final String USER_ALREADY_ENROLLED = "USER_ALREADY_ENROLLED";
    public static final String LOCALE = "LOCALE";

    public static final String AS_SERVER_CONFIG = "";
    public static final String AS_SERVER_LIVENESS = "";
    public static final String SPOOF_CHECK = "SPOOF_CHECK";


    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;


    public static void initialize(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }
    }


    public static SharedPreferences getPreferences() {
        if (preferences == null) {
            throw new IllegalStateException("Utils not initialized. Call Utils.initialize(context) before using this method.");
        }
        return preferences;
    }

    public static SharedPreferences.Editor getEditor() {
        if (editor == null) {
            throw new IllegalStateException("Utils not initialized. Call Utils.initialize(context) before using this method.");
        }
        return editor;
    }

    public static void saveBooleanState(String key, boolean obj) {
        getEditor().putBoolean(key, obj);
        getEditor().apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static void saveIntState(String key, int obj) {
        getEditor().putInt(key, obj);
        getEditor().apply();
    }

    public static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static void saveStringState(String key, String obj) {
        getEditor().putString(key, obj);
        getEditor().apply();
    }

    public static String getString(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static void saveStringSet(String key, Set<String> obj) {
        if (obj.isEmpty()) {
            editor.remove(key);
            editor.commit();
            return;
        }
        getEditor().putStringSet(key, obj);
        getEditor().apply();
    }

    public static Set<String> getStringSet(String key) {
        return getPreferences().getStringSet(key, new HashSet<>());
    }

    public static void clearState(String key) {
        getEditor().remove(key);
        getEditor().apply();
    }

    public static void printStackInLogs(Exception e, String level, String tag){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        switch(level){
            case "d":
                Log.d(tag, "Error msg: " + errors);
                break;
            case "w":
                Log.w(tag, "Error msg: " + errors);
        }
    }

    public static Bitmap base64ToBitmap(String base64String){
        String bitmapStr = base64String.replace("\n", "").replace("\\", "");
        byte[] bytes = Base64.decode(bitmapStr, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(format, quality, outputStream);
        return outputStream.toByteArray();
    }

    /*
     * IMPORTANT:
     * WE USE THIS METHOD TO SIMPLIFY THE JSON RESPONSE AND SEND IT TO ANOTHER ACTIVITY
     * BUT SINGLETON CLASS COULD BE USED INSTEAD OF THIS METHOD TO SAVE ALL THE RESPONSE
     * */
    public static String replaceBase64Images(Activity activity, JSONObject jo) {
        Set<String> currentTemplates = Utils.getStringSet(Utils.REQUIRED_TEMPLATES);
        ArrayList<String> templates = new ArrayList<>(currentTemplates);

        try {
            boolean hasICAOData = jo.has("icao_data");            if (!getBoolean(Utils.ENCRYPTION, false)) {
                if (!jo.getJSONObject("data").has("templates")) {
                    throw new Exception("Template object is not in the response.");
                }
                if (!currentTemplates.isEmpty() && templates.isEmpty()){
                    throw new Exception("Template object is empty when it should not.");
                }
            }
            for (int j = 0; j < templates.size(); j++) {
                String template = templates.get(j);
                String templateImage = null;
                if (!getBoolean(Utils.ENCRYPTION, false)){
                    if (jo.getJSONObject("data").getJSONObject("templates").has(template)){
                        templateImage = jo.getJSONObject("data").getJSONObject("templates").getString(template);
                    } else{
                        throw new Exception("Template " + template + " should be in template object.");
                    }
                    if (templateImage.isEmpty()){
                        throw new Exception("Template " + template + " base64 string is empty when it should not.");
                    }
                }                jo.getJSONObject("data").getJSONObject("templates").put(template, "");
                if (hasICAOData){
                    jo.getJSONObject("icao_data").getJSONObject("templates").put(template, "");
                }                if (jo.getJSONObject("data").getJSONObject("encryptedtemplates").has(template)){
                    jo.getJSONObject("data").getJSONObject("encryptedtemplates").getJSONObject(template).put("data", "");
                    jo.getJSONObject("data").getJSONObject("encryptedtemplates").getJSONObject(template).put("key1", "");
                    jo.getJSONObject("data").getJSONObject("encryptedtemplates").getJSONObject(template).put("key2", "");
                }
            }
            if(jo.getJSONObject("data").has("identy_template")){
                jo.getJSONObject("data").put("identy_template", "");
            }
            if(jo.has("high_res")){
                jo.put("high_res", "");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return jo.toString();
    }


    public static ArrayList<FaceTemplate> getTemplatesConfig() {
        ArrayList<FaceTemplate> faceTemplateArray = new ArrayList<>();

        Set<String> templates = Utils.getStringSet(Utils.REQUIRED_TEMPLATES);
        for (String template : templates) {
            faceTemplateArray.add(FaceTemplate.valueOf(template));
        }

        return faceTemplateArray;
    }

    public static void checkWithTemplatesMethods(Context context, IdentyFaceSdk d, String captureType, IdentyUser identyUser, FaceMatch match) throws Exception {
        String currentWithTemplateMethods = Utils.getString(Utils.WITH_TEMPLATES_METHODS, "PNG");
        FaceTemplate templateType = FaceTemplate.valueOf(currentWithTemplateMethods);
        byte[] template = Utils.convertBitmapToByteArray(Utils.base64ToBitmap("<YOUR-TEMPLATE-STRING>"), Bitmap.CompressFormat.PNG, 100);


        switch (captureType) {
            case "enrollWithTemplates":
                if (identyUser != null){
                    d.enrollwithTemplate(identyUser, match, templateType, template);
                } else{
                    d.enrollwithTemplate(match, templateType, template);
                }
                break;
            case "verifyWithTemplates":
                d.verifywithTemplate(match, templateType, template);
                break;
            case "verifyWithPictureId":
                d.verifywithPictureId(match, templateType, template);
                break;
            case "matchWithTemplates":
                d.matchwithTemplate(match, templateType, template, template);
                break;
            case "enrollWithTemplatesForOneN":
                String enrolmentModeStr = Utils.getString(Utils.ENROLMENT_MODE,"NONE");
                if (!enrolmentModeStr.equals("NONE") && identyUser != null){
                    d.enrollwithTemplateForOneN(identyUser, match, templateType, template, EnrollmentFormat.valueOf(enrolmentModeStr), true);
                }
                break;
        }
    }

    public static String repairBase64String(String base64) {
        // Remove whitespace
        String repaired = base64.replaceAll("\\s", "");

        // Add padding if necessary
        int paddingLength = 4 - (repaired.length() % 4);
        if (paddingLength < 4) {
            for (int i = 0; i < paddingLength; i++) {
                repaired += "=";
            }
        }

        return repaired;
    }


    private static boolean isValidBitmap(byte[] bytes) {
        try {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bmp != null;
        } catch (Throwable t) {
            return false;
        }
    }


    //To Decrypt Encrypted Template
    public static byte[] decryptTemplateOAEP(TemplateOutput output, PrivateKey pkey) throws Exception {
        return decryptTemplateOAEP(output, pkey, false); // no assertion by default
    }

    public static byte[] decryptTemplateOAEP(TemplateOutput output, PrivateKey pkey, boolean assertBitmap)
            throws Exception {
        try {
            byte[] encryptedData = Base64.decode(output.getData().getBytes(), Base64.DEFAULT);
            byte[] keyB = Base64.decode(output.getKey1(), Base64.DEFAULT);
            byte[] ivB  = Base64.decode(output.getKey2(), Base64.DEFAULT);

            Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            rsa.init(Cipher.DECRYPT_MODE, pkey);
            byte[] key = rsa.doFinal(keyB);
            byte[] iv  = rsa.doFinal(ivB);

            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, originalKey, spec);

            byte[] data = cipher.doFinal(encryptedData);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }




    public static void showSnackbar(View view, String message, int timeLength){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(timeLength);
        snackbar.setTextMaxLines(5);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

}
