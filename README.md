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

## Local learning and tracking update

Open **Move → Workout Video Library** or **8-Week Programme**. Open **Explore → Programmes & Short Courses**. Open **You → My Courses** or **Weight & Workout Tracker**. These sections have their own quick navigation for Plans, Videos, Courses, My Courses and Tracker.

- Six sample workout lessons grouped into Yoga, Stretch, Dance, Barre and Mindfulness. Topic filtering and search are functional.
- Eight-week programme with description, sample weekly guidance and lesson links.
- Two sample short courses with descriptions, prices, lesson lists and ownership state.
- **PayFast Dummy Checkout**: success saves one local purchase/unlocks the course; failure and cancellation save no purchase. It is not a PayFast integration and makes no network payment request. Repeating success cannot duplicate an existing purchase.
- My Courses persists across app restarts. This is one device's demo profile; logout does not clear or separate purchases by account.
- No video files are supplied. Lessons explicitly say **Video coming soon**. **Attach Local Video** uses Android's system picker, keeps URI permission and saves the link in SQLite. A supplied compatible local MP4 plays using Android's VideoView and standard controls. File access can be revoked or the original file deleted; use Replace Local Video if needed. Videos are referenced, not copied/hosted by this app.
- Weight tracker accepts positive kg values and records dates/history. Tap a history item to delete it. Future dates and invalid values are rejected.
- Workout tracker saves dated workouts, durations and history. Practice/meditation timer completion also creates a record. Video-library workouts are logged only after user confirmation. Repeated manual logs represent separate sessions.
- New data lives in `wellness.db` with unique purchase IDs, validation constraints and date indexes. Existing `LocalStore` profile and journal data remains intact. Keep the same application ID and install over the old app to retain data; uninstalling/clearing app data deletes local records.

`WellnessRepository` separates the new data access from screens; `DeviceWellnessRepository` is its SQLite implementation. AWS/API synchronisation and server-verified course entitlements must be added later. Content, course prices and programme schedules are placeholders for the client to approve. The free video library uses the same demo lessons as the courses; this build is a demonstration, not secure commercial paid-content delivery.

### Update your existing Windows project

Close Android Studio. Extract this ZIP to a temporary folder. Copy the contents of its `RhythmFlowAndroidProject` folder into your existing project folder, replacing matching source files. Keep your existing `.git`, `.idea`, `.gradle`, `local.properties` and machine-specific JDK settings. Do not delete the project folder. Reopen the original project, sync and run. The ZIP excludes Git history and local build/settings folders, so it can be overlaid without changing your existing branch connection.

### Check the new flows

1. Open Videos, choose a topic and search; open a lesson and verify Coming soon.
2. Attach a compatible video file; play, pause, seek; restart the app and reopen it.
3. Open the eight-week plan and inspect each week's lesson links.
4. Browse a course; verify locked lesson buttons. Simulate failure/cancel and confirm My Courses is unchanged. Simulate success; verify ownership and lesson access. Repeat checkout success via back navigation and verify only one purchase exists.
5. Record valid/invalid weights and a historical date; restart and check history; delete a record.
6. Record a workout; finish a practice timer; inspect both records in Tracker, then delete one.

Automated validation tests: `gradlew.bat testDebugUnitTest`. Build: `gradlew.bat assembleDebug`. PowerShell terminal builds need a compatible JAVA_HOME; the IDE uses the selected Gradle JDK (17 recommended).

### Verification of this update

`assembleDebug` and `testDebugUnitTest` both passed. Four validation tests completed with zero failures/errors. No phone/emulator runtime or playback test was performed; please follow the walkthrough above on your device.
