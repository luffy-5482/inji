package io.mosip.residentapp

import android.content.Intent
import com.facebook.react.bridge.*

interface FaceCallback {
    fun onResult(success: Boolean, error: String?)
}

class BiometricBridgeModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        @JvmField
        @Volatile
        var enrollCallback: FaceCallback? = null

        @JvmField
        @Volatile
        var verifyCallback: FaceCallback? = null

    }

    override fun getName() = "BiometricBridge"

    @ReactMethod
    fun enrollFace(promise: Promise) {
        val activity = currentActivity ?: run {
            promise.reject("NO_ACTIVITY", "No current activity")
            return
        }
        // Clear any stale callbacks from a previous interrupted session
        enrollCallback = null
        verifyCallback = null

        enrollCallback = object : FaceCallback {
            override fun onResult(success: Boolean, error: String?) {
                if (success) promise.resolve("enrolled")
                else promise.reject("ENROLL_FAILED", error ?: "Enrollment failed")
            }
        }
        val intent = Intent(activity, io.identy.facecustomerdemo.activity.MenuFace::class.java)
        intent.putExtra("mode", "enroll")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // ← missing
        activity.startActivity(intent)
    }

    @ReactMethod
    fun verifyFace(promise: Promise) {
        val activity = currentActivity ?: run {
            promise.reject("NO_ACTIVITY", "No current activity")
            return
        }
        // Clear any stale callbacks
        enrollCallback = null
        verifyCallback = null

        verifyCallback = object : FaceCallback {
            override fun onResult(success: Boolean, error: String?) {
                if (success) promise.resolve("verified")
                else promise.reject("VERIFY_FAILED", error ?: "Verification failed")
            }
        }
        val intent = Intent(activity, io.identy.facecustomerdemo.activity.MenuFace::class.java)
        intent.putExtra("mode", "verify")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // ← missing
        activity.startActivity(intent)
    }
}