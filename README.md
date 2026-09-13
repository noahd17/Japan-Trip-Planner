# Japan Trip Planner — Setup Guide

An Android app (Java) with a Firebase/Firestore backend for planning a
Japan trip: a packing list, a budget tracker with live USD→JPY conversion,
and a GPS-based "tag this spot" feature.

## What this covers, mapped to the assignment

| Assignment requirement | Where it's satisfied |
|---|---|
| #1 Install a dev environment | Android Studio |
| #2 Sample app that accepts user input | The whole app — text input (packing/budget), checkboxes, buttons |
| #3 GIT + task tracking | You'll push this to GitHub (steps below) and use Issues |
| #4 Partner checks out, builds, changes, checks back in | Have your partner clone the repo, add a small feature or fix, and push |
| #5 Web service (Firestore) | All three screens read/write Firestore |
| Exceptional: auth | `LoginActivity.java` — Firebase email/password |
| Exceptional: third-party data | `BudgetActivity.java` — live exchange rate from exchangerate-api.com |
| Exceptional: device sensor | `SpotsActivity.java` — GPS via FusedLocationProviderClient |

## 1. Open the project

1. Install **Android Studio** (Giraffe or newer) if you haven't already.
2. Choose **Open** and select this `JapanTripPlanner` folder.
3. Let Gradle sync. It will download the dependencies listed in
   `app/build.gradle` — this needs internet access the first time.

## 2. Create your Firebase project

1. Go to the [Firebase console](https://console.firebase.google.com) and
   create a new project (any name — e.g. "japan-trip-planner").
2. Click **Add app → Android**, and enter the package name
   `com.noah.japantripplanner` (must match exactly — this is set in
   `app/build.gradle` as the `namespace` / `applicationId`).
3. Download the generated **`google-services.json`** file and place it in
   `app/google-services.json` (same folder as `app/build.gradle`). This file
   is what actually connects your app to your Firebase project — the code
   in this repo has no credentials of its own.
4. In the Firebase console, enable:
   - **Authentication → Sign-in method → Email/Password**
   - **Firestore Database → Create database** (start in test mode for
     development; see the security rules note below before you submit)

## 3. Firestore security rules (recommended before submitting)

Test mode leaves your database open to anyone. Since each user's data is
scoped by their own UID (`users/{uid}/...`), lock it down with something
like:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

Paste this into **Firestore Database → Rules** in the console.

## 4. Run it

1. Plug in a physical Android device (not just the emulator, per the
   assignment) with USB debugging enabled.
2. Click Run in Android Studio and select your device.
3. Sign up with a test email/password on first launch — this creates a
   Firebase Auth user and everything else scopes to that user's UID.

## 5. Push to GitHub

```bash
cd JapanTripPlanner
git init
git add .
git commit -m "Initial commit: packing list, budget tracker, spot tagging"
```

**Before your first push**, add a `.gitignore` so you don't commit build
artifacts or your Firebase credentials by mistake:

```
*.iml
.gradle
/local.properties
/.idea
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
app/google-services.json
```

Then create a repo on GitHub and push:

```bash
git remote add origin <your-repo-url>
git push -u origin main
```

**Note:** `google-services.json` is excluded above because it's tied to
your specific Firebase project. If your partner needs to build and run
this app themselves, they'll either need their own Firebase project (with
their own `google-services.json`) or you'll need to share yours with them
directly (not via the public repo) — mention this in your writeup as part
of how you coordinated.

## 6. Working with your partner

Since you're keeping this loose (repo-level only):
1. Have them clone your repo.
2. Get them a working `google-services.json` (see note above) so it builds.
3. Ask them to make a small, real change — e.g. add a "sort by packed
   status" toggle to the packing list, or a delete-confirmation dialog —
   and push it back as a commit or PR.
4. You pull their change, build it, and confirm it works on your device.
5. Screenshot the commit history and both directions of the round trip
   for your writeup.

## 7. Ideas if you want to go further

- Add a category dropdown (Spinner) to expenses instead of free text.
- Sort tagged spots by distance from your current location.
- Add push notifications (Firebase Cloud Messaging) for a packing
  deadline reminder.
- Add a currency picker beyond just JPY, using the same exchange-rate API.
