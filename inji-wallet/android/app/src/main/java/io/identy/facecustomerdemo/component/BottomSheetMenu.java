package io.identy.facecustomerdemo.component;

import io.mosip.residentapp.R;
import static io.identy.facecustomerdemo.util.Utils.ASSISTED_MODE;
import static io.identy.facecustomerdemo.util.Utils.AS_LEVEL;
import static io.identy.facecustomerdemo.util.Utils.BACKGROUND_COLOR;
import static io.identy.facecustomerdemo.util.Utils.DECRYPTION;
import static io.identy.facecustomerdemo.util.Utils.DETECT_EYE_OPENNESS;
import static io.identy.facecustomerdemo.util.Utils.DETECT_HAND;
import static io.identy.facecustomerdemo.util.Utils.DETECT_HAT;
import static io.identy.facecustomerdemo.util.Utils.DETECT_MASK;
import static io.identy.facecustomerdemo.util.Utils.DETECT_MOUTH_CLOSURE;
import static io.identy.facecustomerdemo.util.Utils.DETECT_STRICT_CAPTURE;
import static io.identy.facecustomerdemo.util.Utils.DETECT_SUNGLASSES;
import static io.identy.facecustomerdemo.util.Utils.DISPLAY_RESULT;
import static io.identy.facecustomerdemo.util.Utils.ENCRYPTION;
import static io.identy.facecustomerdemo.util.Utils.ENROLMENT_MODE;
import static io.identy.facecustomerdemo.util.Utils.ENROL_USER;
import static io.identy.facecustomerdemo.util.Utils.HD_CAPTURE;
import static io.identy.facecustomerdemo.util.Utils.LIVENESS_SERVER;
import static io.identy.facecustomerdemo.util.Utils.LIVENESS_WITHIN_SDK_OPTION;
import static io.identy.facecustomerdemo.util.Utils.TIMEOUT_CHECK;
import static io.identy.facecustomerdemo.util.Utils.WITH_TEMPLATES_METHODS;import static io.identy.facecustomerdemo.util.Utils.ICAO;
import static io.identy.facecustomerdemo.util.Utils.IMPORTED_ONE_N;
import static io.identy.facecustomerdemo.util.Utils.LOCALE;
import static io.identy.facecustomerdemo.util.Utils.OFFLINE_MODE;
import static io.identy.facecustomerdemo.util.Utils.REQUIRED_TEMPLATES;
import static io.identy.facecustomerdemo.util.Utils.RETAKE;
import static io.identy.facecustomerdemo.util.Utils.SAVE_ENROLMENT;
import static io.identy.facecustomerdemo.util.Utils.TRAINING;
import static io.identy.facecustomerdemo.util.Utils.UI_OPTION;
import static io.identy.facecustomerdemo.util.Utils.VERIFY_WITH_TEMPLATES_REPEATS;
import static io.identy.facecustomerdemo.util.Utils.VERIFY_WITH_TEMPLATES_TIME;
import static io.identy.facecustomerdemo.util.Utils.VERIFY_WITH_TEMPLATE_LIMITS;
import static io.identy.facecustomerdemo.util.Utils.getBoolean;import static io.identy.facecustomerdemo.util.Utils.getInt;
import static io.identy.facecustomerdemo.util.Utils.getString;
import static io.identy.facecustomerdemo.util.Utils.getStringSet;
import static io.identy.facecustomerdemo.util.Utils.saveBooleanState;
import static io.identy.facecustomerdemo.util.Utils.saveIntState;
import static io.identy.facecustomerdemo.util.Utils.saveStringSet;
import static io.identy.facecustomerdemo.util.Utils.saveStringState;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
//import com.identy.face.Action;
import com.identy.face.Attempt;
import com.identy.face.FileUtils;
import com.identy.face.IdentyError;
import com.identy.face.IdentyFaceSdk;
import com.identy.face.IdentyResponse;
import com.identy.face.IdentyResponseListener;
import com.identy.face.InitializationListener;
import com.identy.face.exception.LicenseValidationException;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.identy.facecustomerdemo.util.Utils;
public class BottomSheetMenu {

    private final BottomSheetDialog dialog;

    public BottomSheetMenu(Activity activity) {
        dialog = new BottomSheetDialog(activity, R.style.BottomSheetDialogTheme);
        dialog.setContentView(R.layout.bottom_sheet_menu);
        dialog.setDismissWithAnimation(true);

        //------------------------------------------------------------------------------------------
        RadioGroup asLevel;
        try {
            asLevel = dialog.findViewById(R.id.settings_rg_as_level);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (asLevel != null) {
            Map<String, Integer> asLevels = new HashMap<>();
            asLevels.put("NONE", R.id.settings_rb_none);
            asLevels.put("LOW", R.id.settings_rb_low);
            asLevels.put("MEDIUM", R.id.settings_rb_medium);
            asLevels.put("BALANCED_HIGH", R.id.settings_rb_balanced_high);
            asLevels.put("HIGH", R.id.settings_rb_high);
            asLevels.put("BALANCED_VERY_HIGH", R.id.settings_rb_balanced_very_high);
            asLevels.put("VERY_HIGH", R.id.settings_rb_very_high);
            asLevels.put("HIGHEST", R.id.settings_rb_highest);

            String currentASLevel = getString(AS_LEVEL, "NONE");

            if (asLevels.containsKey(currentASLevel)) {
                Integer buttonId = asLevels.get(currentASLevel);
                if (buttonId != null) {
                    asLevel.check(buttonId);
                }
            }

            asLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton mode = radioGroup.findViewById(i);
                    if (mode != null) {
                        saveStringState(AS_LEVEL, mode.getText().toString());
                    }
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat assistedMode;
        try {
            assistedMode = dialog.findViewById(R.id.settings_switch_assisted_mode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (assistedMode != null) {
            assistedMode.setChecked(getBoolean(ASSISTED_MODE, false));
            assistedMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(ASSISTED_MODE, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat encryption;
        try {
            encryption = dialog.findViewById(R.id.settings_switch_encryption);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (encryption != null) {
            encryption.setChecked(getBoolean(ENCRYPTION, false));
            encryption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(ENCRYPTION, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat decryption;
        try {
            decryption = dialog.findViewById(R.id.settings_switch_decryption);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (decryption != null) {
            decryption.setChecked(getBoolean(DECRYPTION, false));
            decryption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(DECRYPTION, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
//        OBJECT DETECTION AND STRICT CAPTURE MODE
        SwitchCompat detect_mask;
        try {
            detect_mask = dialog.findViewById(R.id.settings_switch_detect_mask);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (detect_mask != null) {
            detect_mask.setChecked(getBoolean(DETECT_MASK, false));
            detect_mask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(DETECT_MASK, b);
                }
            });
        }

        SwitchCompat detect_GLASSES;
        try {
            detect_GLASSES = dialog.findViewById(R.id.settings_switch_detect_glasses);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (detect_GLASSES != null) {
            detect_GLASSES.setChecked(getBoolean(DETECT_MASK, false));
            detect_GLASSES.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(DETECT_MASK, b);
                }
            });
        }

        SwitchCompat detect_HAND;
        try {
            detect_HAND = dialog.findViewById(R.id.settings_switch_detect_HAND);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (detect_HAND != null) {
            detect_HAND.setChecked(getBoolean(DETECT_HAND, false));
            detect_HAND.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(DETECT_HAND, b);
                }
            });
        }

        SwitchCompat detect_HAT;
        try {
            detect_HAT = dialog.findViewById(R.id.settings_switch_detect_hat);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (detect_HAT != null) {
            detect_HAT.setChecked(getBoolean(DETECT_HAT, false));
            detect_HAT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(DETECT_HAT, b);
                }
            });
        }

        SwitchCompat detect_SUNGLASSES;
        try {
            detect_SUNGLASSES = dialog.findViewById(R.id.settings_switch_detect_sunglasses);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (detect_SUNGLASSES != null) {
            detect_SUNGLASSES.setChecked(getBoolean(DETECT_SUNGLASSES, false));
            detect_SUNGLASSES.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(DETECT_SUNGLASSES, b);
                }
            });
        }

        SwitchCompat detect_EYE_OPENNESS;
        try {
            detect_EYE_OPENNESS = dialog.findViewById(R.id.settings_switch_detect_EYE_OPENNESS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (detect_EYE_OPENNESS != null) {
            detect_EYE_OPENNESS.setChecked(getBoolean(DETECT_EYE_OPENNESS, false));
            detect_EYE_OPENNESS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(DETECT_EYE_OPENNESS, b);
                }
            });
        }

        SwitchCompat detect_MOUTH_CLOSURE;
        try {
            detect_MOUTH_CLOSURE = dialog.findViewById(R.id.settings_switch_detect_MOUTH_CLOSURE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (detect_MOUTH_CLOSURE != null) {
            detect_MOUTH_CLOSURE.setChecked(getBoolean(DETECT_MOUTH_CLOSURE, false));
            detect_MOUTH_CLOSURE.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(DETECT_MOUTH_CLOSURE, b);
                }
            });
        }


        SwitchCompat detect_strict_capture;
        try {
            detect_strict_capture = dialog.findViewById(R.id.settings_switch_strict_capture_mode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (detect_strict_capture != null) {
            detect_strict_capture.setChecked(getBoolean(DETECT_STRICT_CAPTURE, false));
            detect_strict_capture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(DETECT_STRICT_CAPTURE, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        ChipGroup requiredTemplates;
        try {
            requiredTemplates = dialog.findViewById(R.id.settings_cg_required_templates);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (requiredTemplates != null) {
            Map<String, Integer> templates = new HashMap<>();
            templates.put("PNG", R.id.settings_cb_png);
            templates.put("JPEG", R.id.settings_cb_jpeg);
            templates.put("JP2K", R.id.settings_cb_jp2k);
            templates.put("ISO_19794_5", R.id.settings_cb_iso_19794_5);
            templates.put("IDENTY_TYPE_1", R.id.settings_cb_identy_type_1);
            templates.put("Bitmap", R.id.settings_cb_bitmap);
            templates.put("ISO_39794_5", R.id.settings_cb_iso_39794_5);
            templates.put("NIST_ITL_1_2015", R.id.settings_cb_nist_itl_1_2015);

            Set<String> requiredTemplatesSet = getStringSet(REQUIRED_TEMPLATES);
            if (!requiredTemplatesSet.isEmpty()) {
                for (String template : requiredTemplatesSet) {
                    Integer chipId = templates.get(template);
                    if (chipId != null) {
                        requiredTemplates.check(chipId);
                    }
                }
            }

            requiredTemplates.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                    HashSet<String> templates = new HashSet<>();
                    for (Integer i : checkedIds) {
                        Chip field = group.findViewById(i);
                        templates.add(field.getText().toString());
                    }
                    saveStringSet(REQUIRED_TEMPLATES, templates);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat training;
        try {
            training = dialog.findViewById(R.id.settings_switch_training);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (training != null) {
            training.setChecked(getBoolean(TRAINING, true));
            training.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(TRAINING, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        RadioGroup faceTemplateMode;

        try {
            faceTemplateMode = dialog.findViewById(R.id.settings_rg_face_template_mode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (faceTemplateMode != null) {
            Map<String, Integer> faceTemplateModes = new HashMap<>();
            faceTemplateModes.put("PNG", R.id.settings_rb_face_template_png);
            faceTemplateModes.put("JPEG", R.id.settings_rb_face_template_jpeg);
            faceTemplateModes.put("JP2K", R.id.settings_rb_face_template_jp2k);
            faceTemplateModes.put("ISO_19794_5", R.id.settings_rb_face_template_iso_19794_5);
            faceTemplateModes.put("ISO_39794_5", R.id.settings_rb_face_template_iso_39794_5);
            faceTemplateModes.put("NIST_ITL_1_2015", R.id.settings_rb_face_template_nist_itl_1_2015);
            faceTemplateModes.put("IDENTY_TYPE_1", R.id.settings_rb_face_template_identy_type_1);
            String currentASLevel = getString(WITH_TEMPLATES_METHODS, "PNG");

            if (faceTemplateModes.containsKey(currentASLevel)) {
                Integer buttonId = faceTemplateModes.get(currentASLevel);
                if (buttonId != null) {
                    faceTemplateMode.check(buttonId);
                }
            }

            faceTemplateMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton mode = radioGroup.findViewById(i);
                    if (mode != null) {
                        saveStringState(WITH_TEMPLATES_METHODS, mode.getText().toString());
                    }
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat icao;
        try {
            icao = dialog.findViewById(R.id.settings_switch_icao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (icao != null) {
            icao.setChecked(getBoolean(ICAO, false));
            icao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(ICAO, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat livenessServer;
        try {
            livenessServer = dialog.findViewById(R.id.settings_switch_liveness_server);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (livenessServer != null) {
            livenessServer.setChecked(getBoolean(LIVENESS_SERVER, false));
            livenessServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(LIVENESS_SERVER, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat livenessWithinSDKOption;
        try {
            livenessWithinSDKOption = dialog.findViewById(R.id.settings_switch_liveness_withinSDK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (livenessWithinSDKOption != null) {
            livenessWithinSDKOption.setChecked(getBoolean(LIVENESS_WITHIN_SDK_OPTION, false));
            livenessWithinSDKOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(LIVENESS_WITHIN_SDK_OPTION, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat hdcapture;
        try {
            hdcapture = dialog.findViewById(R.id.settings_switch_hd_capture);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (hdcapture != null) {
            hdcapture.setChecked(getBoolean(HD_CAPTURE, false));
            hdcapture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(HD_CAPTURE, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat backgroundColor;

        try {
            backgroundColor = dialog.findViewById(R.id.settings_add_background_color);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (backgroundColor != null) {
            backgroundColor.setChecked(getBoolean(BACKGROUND_COLOR, false));
            backgroundColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(BACKGROUND_COLOR, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat offlineMode;
        try {
            offlineMode = dialog.findViewById(R.id.settings_switch_offline_mode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (offlineMode != null) {
            offlineMode.setChecked(getBoolean(OFFLINE_MODE, false));
            offlineMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(OFFLINE_MODE, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat retake;
        try {
            retake = dialog.findViewById(R.id.settings_switch_retake);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (retake != null) {
            retake.setChecked(getBoolean(RETAKE, false));
            retake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(RETAKE, b);
                }
            });

        }

        //------------------------------------------------------------------------------------------
        SwitchCompat timeoutmax;
        try {
            timeoutmax = dialog.findViewById(R.id.settings_switch_timeout_max);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (timeoutmax != null) {
            timeoutmax.setChecked(getBoolean(TIMEOUT_CHECK, false));
            timeoutmax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(TIMEOUT_CHECK, b);
                }
            });

        }

        //------------------------------------------------------------------------------------------
        SwitchCompat enrolWithTestUser;
        try {
            enrolWithTestUser = dialog.findViewById(R.id.settings_switch_enrol_test_user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (enrolWithTestUser != null) {
            enrolWithTestUser.setChecked(getBoolean(ENROL_USER, false));
            enrolWithTestUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(ENROL_USER, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat verifyWithTemplateLimits;
        EditText timeLimit;
        EditText allowedAttempts;
        try {
            verifyWithTemplateLimits = dialog.findViewById(R.id.settings_switch_with_template_limits);
            timeLimit = dialog.findViewById(R.id.settings_et_verify_with_templates_duration);
            allowedAttempts = dialog.findViewById(R.id.settings_et_verify_with_templates_repeats);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (verifyWithTemplateLimits != null && timeLimit != null && allowedAttempts != null) {
            timeLimit.setText(String.valueOf(getInt(VERIFY_WITH_TEMPLATES_TIME, 40)));
            timeLimit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String num = timeLimit.getText().toString();
                    if (!num.isEmpty() && !num.equals("-")) {
                        saveIntState(VERIFY_WITH_TEMPLATES_TIME, Integer.parseInt(timeLimit.getText().toString()));
                    }
                }
            });

            allowedAttempts.setText(String.valueOf(getInt(VERIFY_WITH_TEMPLATES_REPEATS, 2)));
            allowedAttempts.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String num = allowedAttempts.getText().toString();
                    if (!num.isEmpty() && !num.equals("-")) {
                        saveIntState(VERIFY_WITH_TEMPLATES_REPEATS, Integer.parseInt(allowedAttempts.getText().toString()));
                    }
                }
            });

            verifyWithTemplateLimits.setChecked(getBoolean(VERIFY_WITH_TEMPLATE_LIMITS, false));
            if (verifyWithTemplateLimits.isChecked()) {
                timeLimit.setVisibility(View.VISIBLE);
                allowedAttempts.setVisibility(View.VISIBLE);
            }
            verifyWithTemplateLimits.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(VERIFY_WITH_TEMPLATE_LIMITS, b);
                    if (b) {
                        timeLimit.setVisibility(View.VISIBLE);
                        allowedAttempts.setVisibility(View.VISIBLE);
                    } else {
                        timeLimit.setVisibility(View.INVISIBLE);
                        allowedAttempts.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        //------------------------------------------------------------------------------------------
        RadioGroup enrolmentMode;
        try {
            enrolmentMode = dialog.findViewById(R.id.settings_rg_enrolment_mode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (enrolmentMode != null) {

            Map<String, Integer> enrolmentModes = new HashMap<>();
            enrolmentModes.put("IMAGE", R.id.settings_rb_image);
            enrolmentModes.put("IDENTY_TEMPLATE", R.id.settings_rb_identy_template);
            enrolmentModes.put("NONE", R.id.settings_rb_identy_none);

            String currentEnrolmentMode = getString(ENROLMENT_MODE, "NONE");

            if (enrolmentModes.containsKey(currentEnrolmentMode)) {
                Integer buttonId = enrolmentModes.get(currentEnrolmentMode);
                if (buttonId != null) {
                    enrolmentMode.check(buttonId);
                }
            }

            enrolmentMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton mode = radioGroup.findViewById(i);
                    if (mode != null)
                        saveStringState(ENROLMENT_MODE, mode.getText().toString());
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat saveEnrolment;
        try {
            saveEnrolment = dialog.findViewById(R.id.settings_switch_save_enrolment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (saveEnrolment != null) {
            saveEnrolment.setChecked(getBoolean(SAVE_ENROLMENT, true));
            saveEnrolment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(SAVE_ENROLMENT, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        SwitchCompat displayResult;
        try {
            displayResult = dialog.findViewById(R.id.settings_switch_display_result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (displayResult != null) {
            displayResult.setChecked(getBoolean(DISPLAY_RESULT, true));
            displayResult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    saveBooleanState(DISPLAY_RESULT, b);
                }
            });
        }

        //------------------------------------------------------------------------------------------
        RadioGroup locale;
        try {
            locale = dialog.findViewById(R.id.settings_rg_locale);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (locale != null) {
            Map<String, Integer> localeOptions = new HashMap<>();
            localeOptions.put("en", R.id.settings_rb_en);
            localeOptions.put("es", R.id.settings_rb_es);

            String currentUIOption = getString(LOCALE, "en");

            if (localeOptions.containsKey(currentUIOption)) {
                Integer buttonId = localeOptions.get(currentUIOption);
                if (buttonId != null) {
                    locale.check(buttonId);
                }
            }

            locale.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton mode = radioGroup.findViewById(i);
                    if (mode != null)
                        saveStringState(LOCALE, mode.getText().toString());
                }
            });

        }

        //------------------------------------------------------------------------------------------
        RadioGroup uiOption;
        try {
            uiOption = dialog.findViewById(R.id.settings_rg_uioption);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (uiOption != null) {
            Map<String, Integer> uiOptions = new HashMap<>();
            uiOptions.put("CUSTOM", R.id.settings_rb_uioption_custom);
            uiOptions.put("STANDARD", R.id.settings_rb_uioption_standard);
            uiOptions.put("TICKING", R.id.settings_rb_uioption_ticking);
            uiOptions.put("TICKING_V2", R.id.settings_rb_uioption_ticking_v2);

            String currentUIOption = getString(UI_OPTION, "STANDARD");

            if (uiOptions.containsKey(currentUIOption)) {
                Integer buttonId = uiOptions.get(currentUIOption);
                if (buttonId != null) {
                    uiOption.check(buttonId);
                }
            }

            uiOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton mode = radioGroup.findViewById(i);
                    if (mode != null)
                        saveStringState(UI_OPTION, mode.getText().toString());
                }
            });

        }
        //------------------------------------------------------------------------------------------
        Button importOneToN;
        try {
            importOneToN = dialog.findViewById(R.id.settings_btn_import_oneton);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (importOneToN != null) {
            if (getBoolean(IMPORTED_ONE_N, false)) {
                importOneToN.setText("Reimport");
            }
            importOneToN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        IdentyFaceSdk.newInstance(activity, Utils.LICENSE_FILE, new InitializationListener<IdentyFaceSdk>() {
                            @Override
                            public void onInit(IdentyFaceSdk d) {
                                try {
                                    String oneToN = "<YOUR-ENROLS-FILE>";
                                    d.importForOneN(oneToN, false, true);
                                    Utils.saveBooleanState(IMPORTED_ONE_N, true);
                                    importOneToN.setText("Reimport");
                                    return;
                                } catch (Exception e) {
                                    e.printStackTrace();
//                                    Toast.makeText(activity, "Exception:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, "Failed to load ", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onInitFailed() {

                            }
                        }, new IdentyResponseListener() {
                            @Override
                            public void onAttempt(int i, Attempt attempt) {

                            }

                            @Override
                            public void onResponse(IdentyResponse identyResponse, HashSet<String> hashSet) {
                                Log.d("onen", String.valueOf(identyResponse.toJson(activity)));

                            }

                            @Override
                            public void onErrorResponse(IdentyError identyError, HashSet<String> hashSet) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, identyError.getError().toString(), Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        }, getBoolean(Utils.OFFLINE_MODE, false), false);
                    } catch (LicenseValidationException e) {
                        Toast.makeText(activity, "Exception:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

        //------------------------------------------------------------------------------------------
        TextView exportOneToN;
        try {
            exportOneToN = dialog.findViewById(R.id.settings_btn_tv_export_oneton);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (exportOneToN != null) {
            exportOneToN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        IdentyFaceSdk.newInstance(activity, Utils.LICENSE_FILE, new InitializationListener<IdentyFaceSdk>() {
                            @Override
                            public void onInit(IdentyFaceSdk d) {
                                d.exportForOneN(true);
                            }

                            @Override
                            public void onInitFailed() {

                            }
                        }, new IdentyResponseListener() {
                            @Override
                            public void onAttempt(int i, Attempt attempt) {

                            }

                            @Override
                            public void onResponse(IdentyResponse identyResponse, HashSet<String> hashSet) {
                                if (identyResponse.getAction().equals("CAPTURE")) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity, "Exported successfully", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    try {
                                        File dir = FileUtils.createExternalDirectory("<YOUR_DIRECTORY>");

                                        String data = identyResponse.getExportUserData();
                                        FileUtils.writeStringToFile(dir.getAbsolutePath(), "data_" + Calendar.getInstance().getTimeInMillis() + ".json", data);
                                    } catch (final Exception e) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(activity, "Exported successfully" + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onErrorResponse(IdentyError identyError, HashSet<String> hashSet) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, identyError.getError().toString(), Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        }, getBoolean(Utils.OFFLINE_MODE, false), false);
                    } catch (LicenseValidationException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        //------------------------------------------------------------------------------------------
    }

    public void show() {
        View bottomSheetInternal = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        assert bottomSheetInternal != null;
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);

        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

}
