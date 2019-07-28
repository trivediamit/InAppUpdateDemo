# InAppUpdateDemo

## About In App Update feature
In-app updates is a Play Core library feature that introduces a new request flow to prompt active users to update your app. 

## Requirements
In-app updates works only with devices running Android 5.0 (API level 21) or higher, and requires you to use Play Core library 1.5.0 or higher. After meeting these requirements, your app can support the following UX for in-app updates:
### 1. Flexible
A user experience that provides background download and installation with graceful state monitoring. This UX is appropriate when it’s acceptable for the user to use the app while downloading the update.

### 2. Immediate
A full screen user experience that requires the user to update and restart the app in order to continue using the app. This UX is best for cases where an update is critical for continued use of the app. After a user accepts an immediate update, Google Play handles the update installation and app restart.

This sample code contains both of the above experiences for the understanding.

#### For more information about the feature, common pitfalls or testing, refer to the following links:
https://developer.android.com/guide/app-bundle/in-app-updates

https://proandroiddev.com/android-in-app-updates-common-pitfalls-and-some-good-patterns-9024988bbbe8
