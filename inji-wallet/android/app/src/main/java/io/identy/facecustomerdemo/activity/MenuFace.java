package io.identy.facecustomerdemo.activity;


import static io.identy.facecustomerdemo.util.Utils.BACKGROUND_COLOR;
import static io.identy.facecustomerdemo.util.Utils.DETECT_EYE_OPENNESS;
import static io.identy.facecustomerdemo.util.Utils.DETECT_GLASSES;
import static io.identy.facecustomerdemo.util.Utils.DETECT_HAND;
import static io.identy.facecustomerdemo.util.Utils.DETECT_HAT;
import static io.identy.facecustomerdemo.util.Utils.DETECT_MASK;
import static io.identy.facecustomerdemo.util.Utils.DETECT_MOUTH_CLOSURE;
import static io.identy.facecustomerdemo.util.Utils.DETECT_STRICT_CAPTURE;
import static io.identy.facecustomerdemo.util.Utils.DETECT_SUNGLASSES;
import static io.identy.facecustomerdemo.util.Utils.HD_CAPTURE;
import static io.identy.facecustomerdemo.util.Utils.ICAO;
import static io.identy.facecustomerdemo.util.Utils.LIVENESS_SERVER;
import static io.identy.facecustomerdemo.util.Utils.LIVENESS_WITHIN_SDK_OPTION;
import static io.identy.facecustomerdemo.util.Utils.ONE2ONE_ONE2N;
import static io.identy.facecustomerdemo.util.Utils.UI_OPTION;
import static io.identy.facecustomerdemo.util.Utils.getBoolean;
import static io.identy.facecustomerdemo.util.Utils.getInt;
import static io.identy.facecustomerdemo.util.Utils.getTemplatesConfig;
import static io.identy.facecustomerdemo.util.Utils.replaceBase64Images;
import static io.identy.facecustomerdemo.util.Utils.saveStringState;
import static io.identy.facecustomerdemo.util.Utils.showSnackbar;

import io.mosip.residentapp.R;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.identy.face.AS;
import com.identy.face.Attempt;
import com.identy.face.FaceMatch;
import com.identy.face.IdentyEncrytion;
import com.identy.face.IdentyError;
import com.identy.face.IdentyFaceLocalMatch;
import com.identy.face.IdentyFaceSdk;
import com.identy.face.IdentyResponse;
import com.identy.face.IdentyResponseListener;
import com.identy.face.InitializationListener;
import com.identy.face.MatchSecLevel;
import com.identy.face.TemplateOutput;
import com.identy.face.VerifyIdentyResponse;
import com.identy.face.enums.EnrollmentFormat;
import com.identy.face.enums.FaceTemplate;
import com.identy.face.enums.PrecaptureChecks;
import com.identy.face.enums.UIOption;
import com.identy.face.exception.AttemptsExceededLimitException;
import com.identy.face.exception.InValidColorException;
import com.identy.face.exception.InValidMatcherException;
import com.identy.face.exception.LivenessValidException;
import com.identy.face.exception.TimeoutExceededLimitModeException;
import com.identy.face.exception.UnknownSecurityLevelException;
import com.identy.face.users.IdentyUser;
import com.identy.face.users.IdentyUserManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import io.identy.facecustomerdemo.component.BottomSheetMenu;
import io.identy.facecustomerdemo.util.Utils;

public class MenuFace extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FACE_DEMO_APP";
    private static Activity mContext;
    private static AlertDialog alertDialog;
    private BottomSheetMenu menu;
    private AS asHighestSecurityLevelReached;
    private String launchMode = "enroll";
    private volatile boolean callbackFired = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mode = getIntent().getStringExtra("mode");

        mContext = MenuFace.this;
        Utils.initialize(mContext);
        Utils.clearState(Utils.USER_ALREADY_ENROLLED);
        requestPermissions();

        if ("preload".equals(mode)) {
            preloadSdk();
            return;
        }

        if ("enroll".equals(mode)) {
            Log.d("MenuFace","Try to enroll the face");
            launchDirectEnroll();
            return;
        }

        if ("verify".equals(mode)) {
            launchDirectVerify();
            return;
        }

        // fallback only
        setContentView(R.layout.activity_main);
    }
    private void preloadSdk() {
        try {
            IdentyFaceSdk.newInstance(
                    this,
                    Utils.LICENSE_FILE,
                    new InitializationListener<IdentyFaceSdk>() {
                        @Override
                        public void onInit(IdentyFaceSdk sdk) {
                            finish();
                        }

                        @Override
                        public void onInitFailed() {
                            finish();
                        }
                    },
                    null,
                    getBoolean(Utils.OFFLINE_MODE, false),
                    false
            );
        } catch (Exception e) {
            finish();
        }
    }
    private void launchDirectEnroll() {
        View fakeView = new View(this);
        fakeView.setId(R.id.face_regButton);
        onClick(fakeView);
    }
    private void launchDirectVerify() {
        View fakeView = new View(this);
        fakeView.setId(R.id.face_validateButton);
        onClick(fakeView);
    }

    private void requestPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_LOGS}, 1);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_LOGS}, 2);
        }
        if (checkSelfPermission(Manifest.permission.READ_LOGS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_LOGS}, 3);
        }
    }

    // Always fires callback on UI thread to ensure React Native promise resolves correctly
    private void fireCallback(final boolean success, final String error) {
        if (callbackFired) {
            Log.w(TAG, "fireCallback already fired — ignoring duplicate");
            return;
        }
        callbackFired = true;  // ← add this

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "fireCallback success=" + success + " enrollCb=" + (io.mosip.residentapp.BiometricBridgeModule.enrollCallback != null) + " verifyCb=" + (io.mosip.residentapp.BiometricBridgeModule.verifyCallback != null));
                if (io.mosip.residentapp.BiometricBridgeModule.enrollCallback != null) {
                    io.mosip.residentapp.BiometricBridgeModule.enrollCallback.onResult(success, error);
                    io.mosip.residentapp.BiometricBridgeModule.enrollCallback = null;
                } else if (io.mosip.residentapp.BiometricBridgeModule.verifyCallback != null) {
                    io.mosip.residentapp.BiometricBridgeModule.verifyCallback.onResult(success, error);
                    io.mosip.residentapp.BiometricBridgeModule.verifyCallback = null;
                } else {
                    Log.e(TAG, "fireCallback: NO CALLBACK SET - promise will not resolve!");
                    Log.d("VERIFY_DEBUG", "Callback fired. Success = " + success);
                }
                finish();
            }
        });
    }

    @Override
    public void onClick(final View view) {
        try {
            Log.d(TAG, "Creating new Instance, launchMode=" + launchMode);
            IdentyFaceSdk.newInstance(this, Utils.LICENSE_FILE, new InitializationListener<IdentyFaceSdk>() {
                @Override
                public void onInit(IdentyFaceSdk d) {

                    d.setLocale(Utils.getString(Utils.LOCALE, "en"));
                    d.handlePredictiveBackGesture();
                    d.displayResult(true);
                    if (getBoolean(DETECT_MASK, false)) {
                        d.setPrecaptureCheck(PrecaptureChecks.MASK, true);
                    }
                    if (getBoolean(DETECT_GLASSES, false)) {
                        d.setPrecaptureCheck(PrecaptureChecks.GLASSES, true);
                    }
                    if (getBoolean(DETECT_SUNGLASSES, false)) {
                        d.setPrecaptureCheck(PrecaptureChecks.SUNGLASSES, true);
                    }
                    if (getBoolean(DETECT_HAT, false)) {
                        d.setPrecaptureCheck(PrecaptureChecks.HAT, true);
                    }
                    if (getBoolean(DETECT_HAND, false)) {
                        d.setPrecaptureCheck(PrecaptureChecks.HAND, true);
                    }
                    if (getBoolean(DETECT_EYE_OPENNESS, false)) {
                        d.setPrecaptureCheck(PrecaptureChecks.EYE_OPENNESS, true);
                    }
                    if (getBoolean(DETECT_MOUTH_CLOSURE, false)) {
                        d.setPrecaptureCheck(PrecaptureChecks.MOUTH_CLOSURE, true);
                    }
                    if (getBoolean(DETECT_STRICT_CAPTURE, false)) {
                        d.enableStrictCaptureMode();
                    }

                    if (getBoolean(LIVENESS_SERVER, false)) {
                        try {
                            if (getBoolean(LIVENESS_WITHIN_SDK_OPTION, false)) {
                                d.setASServerConfig(Utils.AS_SERVER_CONFIG, Utils.AS_SERVER_LIVENESS, true);
                            } else {
                                d.setASServerConfig(Utils.AS_SERVER_CONFIG, Utils.AS_SERVER_LIVENESS, false);
                            }
                        } catch (LivenessValidException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (getBoolean(HD_CAPTURE, false)) {
                        Log.d("HD", "Enabled HD");
                        d.enablehw();
                    }

                    if (getBoolean(BACKGROUND_COLOR, false)) {
                        try {
                            d.enableBackgroundRemoval("#FFFFFF");
                        } catch (InValidColorException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    try {
                        d.setASSecLevel(AS.valueOf(Utils.getString(Utils.AS_LEVEL, "NONE")));
                    } catch (UnknownSecurityLevelException e) {
                        throw new RuntimeException(e);
                    }

                    ArrayList<FaceTemplate> faceTemplateArray = getTemplatesConfig();
                    d.setRequiredTemplates(faceTemplateArray);

                    if (getBoolean(Utils.ASSISTED_MODE, false)) {
                        d.enableAssistedMode();
                    }

                    d.displayResult(getBoolean(Utils.DISPLAY_RESULT, false));

                    if (getBoolean(Utils.RETAKE, false)) {
                        d.enableRetakeScreen();
                        d.setAllowedAttempts(1);
                        d.setAllowedTimeLimit(1);
                    } else {
                        d.disableRetakeScreen();
                        if (getBoolean(Utils.TIMEOUT_CHECK, false)) {
                            d.setAllowedAttempts(1);
                            d.setAllowedTimeLimit(60);
                        } else {
                            d.setAllowedAttempts(3);
                            d.setAllowedTimeLimit(30);
                        }
                    }

                    if (getBoolean(Utils.ENCRYPTION, false)) {
                        d.setEncryption(IdentyEncrytion.RSA_AES, "<YOUR-RSA-KEY-ENCRYPTION>");
                    }

                    d.setUioption(UIOption.valueOf(Utils.getString(UI_OPTION, "STANDARD")));

                    if (getBoolean(Utils.TRAINING, false)) {
                        d.enableTraining();
                    } else {
                        d.disableTraining();
                    }

                    final FaceMatch match = new IdentyFaceLocalMatch();

                    if (getBoolean(ICAO, false)) {
                        try {
                            d.enableICAOChecks();
                        } catch (InValidMatcherException e) {
                        }
                    }

                    switch (view.getId()) {
                        case R.id.face_previewButton:
                            try {
                                if (Utils.getString(ONE2ONE_ONE2N, "1:1").equals("1:N")) {
                                    if (!d.identify(true)) {
                                        showSnackbar(view, "Please enroll to identify", 4 * 1000);
                                    }
                                } else {
                                    d.capture();
                                }
                            } catch (Exception e) {
                                showSnackbar(view, e.getMessage(), 4 * 1000);
                                throw new RuntimeException(e);
                            }
                            break;
                        case R.id.face_regButton:
                            try {
                                if (Utils.getString(ONE2ONE_ONE2N, "1:1").equals("1:N")) {
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                IdentyUser identyUser;
                                                identyUser = IdentyUserManager.getInstance(mContext).getUserByuserName("test");
                                                if (identyUser == null) {
                                                    identyUser = IdentyUserManager.getInstance(mContext).createUser("test", "test");
                                                }
                                                d.enrollForOneN(identyUser);
                                            } catch (Exception e) {
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        showSnackbar(view, e.getMessage(), 4 * 1000);
                                                        throw new RuntimeException(e);
                                                    }
                                                });
                                            }
                                        }
                                    };
                                    new Thread(runnable).start();
                                } else {
                                    String enrolmentModeStr = Utils.getString(Utils.ENROLMENT_MODE, "NONE");
                                    Log.d("ENROLLA", enrolmentModeStr);
                                    if (!enrolmentModeStr.equals("NONE")) {
                                        d.enroll(match, EnrollmentFormat.valueOf(enrolmentModeStr), getBoolean(Utils.SAVE_ENROLMENT, false));
                                    } else {
                                        d.displayResult(true);
                                        d.enableTraining();
                                        d.enroll(match);
                                    }
                                }
                            } catch (Exception e) {
                                showSnackbar(view, e.getMessage(), 4 * 1000);
                                throw new RuntimeException(e);
                            }
                            break;
                        case R.id.face_validateButton:
                            boolean verifyAvailable;
                            try {
                                if (Utils.getString(ONE2ONE_ONE2N, "1:1").equals("1:N")) {
                                    boolean withLimits = getBoolean(Utils.VERIFY_WITH_TEMPLATE_LIMITS, false);
                                    if (withLimits) {
                                        int attempts = getInt(Utils.VERIFY_WITH_TEMPLATES_REPEATS, 2);
                                        int timeout = getInt(Utils.VERIFY_WITH_TEMPLATES_TIME, 40);
                                        verifyAvailable = d.verifyAgainstAll(match, attempts, timeout);
                                    } else {
                                        verifyAvailable = d.verifyAgainstAll(match);
                                    }
                                } else {
                                    d.displayResult(true);
                                    verifyAvailable = d.verify(match);
                                }
                            } catch (InValidMatcherException | AttemptsExceededLimitException |
                                     TimeoutExceededLimitModeException | LivenessValidException e) {
                                showSnackbar(view, e.getMessage(), 4 * 1000);
                                throw new RuntimeException(e);
                            }
                            if (!verifyAvailable) {
                                showSnackbar(view, "Please enroll to verify", 4 * 1000);
                            }
                            break;
                        case R.id.main_enroll_with_template:
                            String captureType = (Utils.getString(ONE2ONE_ONE2N, "1:1").equals("1:N")) ? "enrollWithTemplatesForOneN" : "enrollWithTemplates";
                            if (getBoolean(Utils.ENROL_USER, false)) {
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            IdentyUser identyUser;
                                            identyUser = IdentyUserManager.getInstance(mContext).getUserByuserName("test");
                                            if (identyUser == null) {
                                                identyUser = IdentyUserManager.getInstance(mContext).createUser("test", "test");
                                            }
                                            Utils.checkWithTemplatesMethods(MenuFace.this, d, captureType, identyUser, match);
                                        } catch (Exception e) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    showSnackbar(view, e.getMessage(), 4 * 1000);
                                                    throw new RuntimeException(e);
                                                }
                                            });
                                        }
                                    }
                                };
                                new Thread(runnable).start();
                            } else {
                                try {
                                    Utils.checkWithTemplatesMethods(MenuFace.this, d, captureType, null, match);
                                } catch (Exception e) {
                                    showSnackbar(view, e.getMessage(), 4 * 1000);
                                    throw new RuntimeException(e);
                                }
                            }
                            break;
                        case R.id.main_verify_with_picture_id:
                            try {
                                Utils.checkWithTemplatesMethods(MenuFace.this, d, "verifyWithPictureId", null, match);
                            } catch (Exception e) {
                                showSnackbar(view, e.getMessage(), 4 * 1000);
                                throw new RuntimeException(e);
                            }
                            break;
                        case R.id.main_match_with_template:
                            try {
                                Utils.checkWithTemplatesMethods(MenuFace.this, d, "matchWithTemplates", null, match);
                            } catch (Exception e) {
                                showSnackbar(view, e.getMessage(), 4 * 1000);
                                throw new RuntimeException(e);
                            }
                            break;
                        case R.id.main_verify_with_template:
                            try {
                                Utils.checkWithTemplatesMethods(MenuFace.this, d, "verifyWithTemplates", null, match);
                            } catch (Exception e) {
                                showSnackbar(view, e.getMessage(), 4 * 1000);
                                throw new RuntimeException(e);
                            }
                            break;
                    }
                }

                @Override
                public void onInitFailed() {
                    fireCallback(false, "SDK initialization failed");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showSnackbar(view, "Initialization failed ...", 4 * 1000);
                        }
                    });
                }
            }, new IdentyResponseListener() {
                @Override
                public void onAttempt(int i, Attempt attempt) {
                }

                @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                @Override
                public void onResponse(IdentyResponse identyResponse, HashSet hashSet) {
                    if (hashSet == null) {
                        Log.d(TAG, "onResponse: hashSet is null — ignoring, not a final result");
                        return;
                    }

                    Log.d(TAG, "onResponse triggered, hashSet=" + hashSet.toString());

                    boolean isSuccess = true;

                    if (identyResponse instanceof VerifyIdentyResponse) {
                        VerifyIdentyResponse resp = (VerifyIdentyResponse) identyResponse;
                        MatchSecLevel matchLevel = resp.getMatchHighestSecurityLevelReached();
                        Log.d(TAG, "VerifyIdentyResponse matchLevel=" + matchLevel);

                        if (matchLevel == null || matchLevel == MatchSecLevel.NONE) {
                            Log.d(TAG, "Verify FAILED — face did not match");
                            isSuccess = false;
                        } else {
                            Log.d(TAG, "Verify SUCCESS — matchLevel=" + matchLevel);
                        }
                    } else {
                        // Enroll path — onResponse always means success
                        Log.d(TAG, "Enroll onResponse — treating as success");
                        asHighestSecurityLevelReached = identyResponse.getPrints().getAsHighestSecurityLevelReached();

                        if (getBoolean(Utils.ENCRYPTION, false)) {
                            Map<FaceTemplate, TemplateOutput> encryptedTemplates =
                                    identyResponse.getPrints().getEncryptedTemplates();
                            Log.d(TAG, "Encrypted templates count=" + encryptedTemplates.size());
                        }

                        if (getBoolean(LIVENESS_SERVER, false)) {
                            String msg = identyResponse.getLivenessServerRequest();
                            if (msg != null) {
                                Log.d("FaceSDKServer", msg);
                            } else {
                                Log.d("FaceSDKServer", "LivenessServerRequest is null");
                            }
                        }
                    }

                    fireCallback(isSuccess, isSuccess ? null : "Face did not match");
                }

                @Override
                public void onErrorResponse(final IdentyError identyError, final HashSet hashSet) {
                    Log.d(TAG, "onErrorResponse — error=" + identyError.getError()
                            + " message=" + identyError.getMessage());

                    // Fire failure callback first — before any UI work
                    fireCallback(false, identyError.getMessage());

                    // UI updates after callback is fired
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (view != null && view.getId() == R.id.face_regButton) {
                                    if (Utils.getString(ONE2ONE_ONE2N, "1:1").equals("1:N")) {
                                        Utils.saveStringState(Utils.USER_ALREADY_ENROLLED, identyError.getMessage());
                                    }
                                }
                                showSnackbar(view, identyError.getMessage(), 4 * 1000);
                            } catch (Exception e) {
                                Log.e(TAG, "onErrorResponse UI update failed: " + e.getMessage());
                            }
                        }
                    });
                }

            }, getBoolean(Utils.OFFLINE_MODE, false), false);
        } catch (Exception e) {
            Utils.printStackInLogs(e, "w", "SDKException");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            Log.d("onCreateOptionsMenu", "start");
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.optionsmenu, menu);
        } catch (Exception e) {
            Log.d("MenuFace. Settings btn", e.getMessage());
            Utils.printStackInLogs(e, "d", "MenuFaceOnCreate");
        }
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // If activity is destroyed without fireCallback being called
        // (user pressed Close/Back), reject the promise explicitly
        if (!callbackFired) {
            Log.d(TAG, "onDestroy called without fireCallback — firing failure");
            fireCallback(false, "User cancelled");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            Log.d("onOptionsItemSelected", "start");
            if (item.isCheckable()) {
                item.setChecked(!item.isChecked());
            }
            if (item.getItemId() == R.id.menu_settings) {
                showSettingsDialog();
            }
        } catch (Exception e) {
            Log.d("Menu FACE. Settings btn", e.getMessage());
            Utils.printStackInLogs(e, "d", "MenuFACEItemSelect");
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog() {
        menu.show();
    }
}
