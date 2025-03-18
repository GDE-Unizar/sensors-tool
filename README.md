![Banner](./PlayStore/banner.png)

# Sensors tools

This is the Android Studio project for the application 'GDE Sensors tool', created for physics practices within the GDE group.

The application was uploaded to the Play Store (https://play.google.com/store/apps/details?id=es.unizar.gde.sensors).

Developed in Kotlin, using few libraries (one for the graphics and a little more) and has been programmed with the idea of future modifications.

## Details

Almost all classes and functions are commented, so it shouldn't be very complicated to understand, but a general overview is:
- The main screen is made up of 'fragments', one for each sensor/actuator, which operate independently (accelerometer, microphone, speaker, and vibrator).
- There are also some experiments to automate certain processes (the idea is to add any that are needed for the practice).
- The button at the top indicates how to access help, which is done by long-pressing any element of the application.
- It also has an extra activity that automatically loads all the sensors of the device and their properties.

## Development

- Signed with the key `./PlayStore/key.jks` (the password is indicated in the file password.txt).
- The images used for the Play Store listing are also included.
