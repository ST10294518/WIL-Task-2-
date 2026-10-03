# Rhythm & Flow — Android frontend

Updated from your supplied Kotlin Android project using your 16-screen PDF wireframe. Native Android views with no third-party UI dependencies. Package: `co.za.rhythmandflow`.

## Open and run

1. Extract the ZIP completely.
2. Android Studio → Open → select `RhythmFlowAndroidProject` (the folder containing `settings.gradle.kts`).
3. Set Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK to JDK 17.
4. Allow Gradle sync and install Android SDK Platform 33 if prompted. Internet is needed for first sync.
5. Select an emulator or phone with Android 8.0/API 26 or newer and press Run.
6. Choose **Preview App** on the sign-in page to explore without a cloud account.

Android Studio creates its own `local.properties`. The ZIP intentionally excludes machine-specific paths and generated build files.

## All 16 reference screens

Welcome; Sign In; Create Your Account; Home / Feeling Check-In; Your Rhythm Today; Move; Full Body Flow / Practice Detail; Practice Complete; Explore; Article Detail; Journal; Meditation Detail; You; Edit Profile; Shop; Settings.

Your Rhythm Today opens after a check-in or via the Home button. Settings opens from Home's menu or You. Shop opens from Explore. Practice Complete opens by finishing the practice timer.

Additional supporting pages: timers, cart, notification preferences and journeys. Cream backgrounds, teal controls, serif headings, rounded cards, scalable line icons and photography cropped from your supplied design. The reference is adapted into real native components rather than static screen images. Layouts scroll and constrain width on larger devices; bottom navigation stays outside the scroll content.

## Working local interactions

- Navigation, back history, validation and password visibility.
- Search/filter practices, explore categories and products.
- Mood check-ins and current-month count.
- Journal/reflection/gratitude saving, history and deletion.
- Profile editing and photo selection through Android's document picker.
- Wellness intentions, favourite practice categories and saved content.
- Local cart quantities/totals and notification preferences.
- Start/pause/resume timers and practice completion.

`LocalStore` keeps preview data on this device. No passwords are stored. Login and registration open a clearly identified **local preview session**, not a server-authenticated account. Google login/password reset explain pending cloud configuration. Checkout does not charge or create an order. Guided audio/video is not provided; timers and text prompts demonstrate the frontend. Article and programme content is sample text until the client supplies final content.

## Backend stage

Connect authentication/Google OAuth/password reset, hosted APIs and database, cloud synchronisation, media, push delivery, order creation and payment processing. This ZIP is the frontend, not the completed backend/hosting/CI/CD portion of Task 2.

## Build setup

Preserves AGP 8.7.3, Kotlin 1.9.24, Gradle 8.9, compile/target SDK 33 and min SDK 26 from your project. Added the official Gradle 8.9 wrapper JAR and launcher scripts missing from the original upload.

## Verification

Full Gradle `assembleDebug` succeeded with AGP 8.7.3, Kotlin 1.9.24, JDK 17 and Android API 33 (33 tasks completed). Independent Kotlin type checking and AAPT2 resource compilation/linking also passed. XML, image references and ZIP integrity were checked. There are only deprecation warnings for legacy native Activity back/status-bar APIs. No emulator/device visual test was completed, so screen appearance and interactions should still be reviewed on your phone/emulator before submission.

## Walkthrough

Preview App → mood check-in → Your Rhythm Today → Move → search/filter → practice → Begin Practice → Finish Practice → select feeling/reflection → Save to Journal → inspect/delete entry → Explore → read/save article → You → Saved → Edit Profile → save → Shop → add/remove cart items → Settings → reminders → Log Out. Also check a small phone, landscape/tablet and larger system font size.
