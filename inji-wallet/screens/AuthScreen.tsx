import React from 'react';
import { useTranslation } from 'react-i18next';
import { MessageOverlay } from '../components/MessageOverlay';
import { Button, Column } from '../components/ui';
import { Theme } from '../components/ui/styleUtils';
import { RootRouteProps } from '../routes';
import { useAuthScreen } from './AuthScreenController';

export const AuthScreen: React.FC<RootRouteProps> = props => {
  const { t } = useTranslation('AuthScreen');
  const controller = useAuthScreen(props);

  return (
    <Column
      fill
      padding={[32, 25, 32, 32]}
      backgroundColor={Theme.Colors.whiteBackgroundColor}
      align="space-between">

      <MessageOverlay
        isVisible={controller.alertMsg !== ''}
        onBackdropPress={controller.hideAlert}
        title={controller.alertMsg}
      />

      <Column>
        <Button
          testID="faceAction"
          title={controller.faceEnrolled ? "Verify Face" : "Register Face"}
          type="gradient"
          margin="0 0 8 0"
          onPress={
            controller.faceEnrolled
              ? controller.useFaceVerify
              : controller.useFaceEnroll
          }
        />
      </Column>
    </Column>
  );
};