package io.identy.facecustomerdemo.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.identy.facecustomerdemo.util.Utils
import io.mosip.residentapp.R

class JsonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.json_screen_result)

        val jsonTvJsonResponse = findViewById<TextView>(R.id.json_tv_json_response)
        val jsonTvAsHighestSecurityLevel = findViewById<TextView>(R.id.json_tv_as_highest_security_level)
        val jsonTvAsHighestSecurityLevelResult = findViewById<TextView>(R.id.json_tv_as_highest_security_level_result)
        val jsonTvMatchHighestSecurityLevel = findViewById<TextView>(R.id.json_tv_match_highest_security_level)
        val jsonTvMatchHighestSecurityLevelResult = findViewById<TextView>(R.id.json_tv_match_highest_security_level_result)
        val jsonTvEncryptedTemplates = findViewById<TextView>(R.id.json_tv_encrypted_templates)
        val jsonTvEncryptedTemplatesResult = findViewById<TextView>(R.id.json_tv_encrypted_templates_result)
        val jsonTvHasImage = findViewById<TextView>(R.id.json_tv_has_image)
        val jsonTvHasImageResult = findViewById<TextView>(R.id.json_tv_has_image_result)

        val identyResponseJSONString = intent.getStringExtra("identyResponseJSON")
        if (identyResponseJSONString != null) {
            try {
                jsonTvJsonResponse.text = identyResponseJSONString
            } catch (e: Exception) {
                Utils.printStackInLogs(e, "w", "JSONActivityException")
            }
        }

        val asHighestSecurityLevelReachedString = intent.getStringExtra("asHighestSecurityLevelReached")
        if (asHighestSecurityLevelReachedString != null) {
            try {
                jsonTvAsHighestSecurityLevel.visibility = View.VISIBLE
                jsonTvAsHighestSecurityLevelResult.visibility = View.VISIBLE
                jsonTvAsHighestSecurityLevelResult.text = asHighestSecurityLevelReachedString
            } catch (e: Exception) {
                Utils.printStackInLogs(e, "w", "JSONActivityException")
            }
        }

        val matchHighestSecurityLevelReached = intent.getStringExtra("matchHighestSecurityLevelReached")
        if (matchHighestSecurityLevelReached != null) {
            try {
                jsonTvMatchHighestSecurityLevel.visibility = View.VISIBLE
                jsonTvMatchHighestSecurityLevelResult.visibility = View.VISIBLE
                jsonTvMatchHighestSecurityLevelResult.text = matchHighestSecurityLevelReached
            } catch (e: Exception) {
                Utils.printStackInLogs(e, "w", "JSONActivityException")
            }
        }

        val encryptedTemplates = intent.getBooleanExtra("encryptedTemplates", false)
        if (encryptedTemplates) {
            try {
                jsonTvEncryptedTemplates.visibility = View.VISIBLE
                jsonTvEncryptedTemplatesResult.visibility = View.VISIBLE
                jsonTvEncryptedTemplatesResult.text = encryptedTemplates.toString()
            } catch (e: Exception) {
                Utils.printStackInLogs(e, "w", "JSONActivityException")
            }
        }

        val hasImage = intent.getBooleanExtra("hasImage", false)
        if (hasImage) {
            try {
                jsonTvHasImage.visibility = View.VISIBLE
                jsonTvHasImageResult.visibility = View.VISIBLE
                jsonTvHasImageResult.text = hasImage.toString()
            } catch (e: Exception) {
                Utils.printStackInLogs(e, "w", "JSONActivityException")
            }
        }
    }
}