import { useMachine, useSelector } from '@xstate/react';
import { useContext, useEffect, useState } from 'react';
import * as LocalAuthentication from 'expo-local-authentication';
import { NativeModules } from 'react-native';

import {
AuthEvents,
selectSettingUp,
selectAuthorized,
} from '../machines/auth';
import { RootRouteProps } from '../routes';
import { GlobalContext } from '../shared/GlobalContext';
import {
biometricsMachine,
selectError,
selectIsEnabled,
selectIsSuccess,
selectIsUnvailable,
selectUnenrolledNotice,
selectErrorResponse,
} from '../machines/biometrics';
import { SettingsEvents } from '../machines/settings';
import { useTranslation } from 'react-i18next';
import {
sendStartEvent,
sendImpressionEvent,
sendInteractEvent,
getStartEventData,
getInteractEventData,
getImpressionEventData,
getEndEventData,
sendEndEvent,
} from '../shared/telemetry/TelemetryUtils';
import { TelemetryConstants } from '../shared/telemetry/TelemetryConstants';

export function useAuthScreen(props: RootRouteProps) {
  const { appService } = useContext(GlobalContext);
  const authService = appService.children.get('auth');
  const settingsService = appService.children.get('settings');

  const isSettingUp = useSelector(authService, selectSettingUp);
  const isAuthorized = useSelector(authService, selectAuthorized);

  // 🔥 Get faceEnrolled directly from context
  const faceEnrolled = authService.getSnapshot().context.faceEnrolled;

  const [alertMsg, setHasAlertMsg] = useState('');
  const [isBiometricsAvailable, setIsBiometricsAvailable] = useState(false);

  const [biometricState, biometricSend, bioService] =
    useMachine(biometricsMachine);

  const isEnabledBio = useSelector(bioService, selectIsEnabled);
  const isUnavailableBio = useSelector(bioService, selectIsUnvailable);
  const isSuccessBio = useSelector(bioService, selectIsSuccess);
  const errorMsgBio = useSelector(bioService, selectError);
  const unEnrolledNoticeBio = useSelector(bioService, selectUnenrolledNotice);
  const errorResponse = useSelector(bioService, selectErrorResponse);

  const { t } = useTranslation('AuthScreen');

  // 🔥 ENROLL FACE
  const useFaceEnroll = async () => {
    try {
      await NativeModules.BiometricBridge.enrollFace();

      authService.send(AuthEvents.SETUP_FACE());
      authService.send(AuthEvents.LOGIN());

    } catch (e) {
      setHasAlertMsg('Face enrollment failed. Please try again.');
    }
  };

  // 🔥 VERIFY FACE
  // useAuthScreen.ts

  const useFaceVerify = async () => {
    try {
      const res = await NativeModules.BiometricBridge.verifyFace();
      console.log("VERIFY RESULT:", res);
      authService.send(AuthEvents.LOGIN());

    } catch (e: any) {
      console.log("VERIFY ERROR:", e);

      // Do NOT send any auth event on failure
      // Just show the error — stay on this screen
      const errorMsg = e?.code === 'VERIFY_FAILED'
         ? 'Face did not match. Please try again.'
         : 'Face verification failed. Please try again.';

      setHasAlertMsg(errorMsg);

      // Explicitly reset any partial auth state
      authService.send(AuthEvents.LOGOUT()); // or whatever your reset event is
    }
  };

  const usePasscode = () => {
    props.navigation.navigate('Passcode', { setup: isSettingUp });
  };

  useEffect(() => {
    const fetchIsAvailable = async () => {
      const result = await LocalAuthentication.hasHardwareAsync();
      setIsBiometricsAvailable(result);
    };
    fetchIsAvailable();
  }, []);

  // 🔥 Navigate when authorized
  useEffect(() => {
    if (isAuthorized) {
      sendEndEvent(
        getEndEventData(
          TelemetryConstants.FlowType.appOnboarding,
          TelemetryConstants.EndEventStatus.success,
        ),
      );

      props.navigation.reset({
        index: 0,
        routes: [{ name: 'Main' }],
      });

      sendImpressionEvent(
        getImpressionEventData(
          TelemetryConstants.FlowType.appOnboarding,
          TelemetryConstants.Screens.home,
        ),
      );

      return;
    }

    if (isSuccessBio) {
      authService.send(AuthEvents.SETUP_BIOMETRICS('true'));
      settingsService.send(
        SettingsEvents.TOGGLE_BIOMETRIC_UNLOCK(true, false),
      );
      usePasscode();
    }

    else if (errorMsgBio) {
      sendEndEvent(
        getEndEventData(
          TelemetryConstants.FlowType.appOnboarding,
          TelemetryConstants.EndEventStatus.failure,
          {
            errorId: errorResponse.res.error,
            errorMessage: errorResponse.res.warning,
            stackTrace: errorResponse.stacktrace,
          },
        ),
      );

      if (errorResponse.res.error !== 'user_cancel') {
        setHasAlertMsg(t(errorMsgBio));
      }
    }

    else if (unEnrolledNoticeBio) {
      setHasAlertMsg(t(unEnrolledNoticeBio));
    }

    else if (isUnavailableBio) {
      sendStartEvent(
        getStartEventData(TelemetryConstants.FlowType.appOnboarding),
      );
      usePasscode();
    }

  }, [
    isAuthorized,
    isSuccessBio,
    isUnavailableBio,
    errorMsgBio,
    unEnrolledNoticeBio,
  ]);

  const hideAlert = () => {
    setHasAlertMsg('');
  };

  return {
    isBiometricsAvailable,
    isSettingUp,
    faceEnrolled,      // 🔥 important
    alertMsg,
    isEnabledBio,
    hideAlert,
    usePasscode,
    useFaceEnroll,
    useFaceVerify,
  };
}