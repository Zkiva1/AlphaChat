# Mechinet

Mechinet is an Android application designed to help users explore and connect with Israeli pre-military academies (Mechinot) and communicate with students, graduates, and staff members through an integrated chat system.

## Project Documentation

For detailed documentation, requirements analysis, design diagrams, and development process, see:

[📄 Mechinet Project Documentation](https://docs.google.com/document/d/1aEylbgZKup3nAOCc7Sk199Cwh-6wgA3ws84vzlqNoSo/edit?usp=sharing)

## Features

### Mechina Directory
- Browse different Mechinot
- View information about each Mechina
- Filter by:
  - Region
  - Gender
  - Type
- Open official Mechina websites directly from the app

### Real-Time Chat
- One-on-one messaging
- Recent conversations list
- User search functionality
- Firebase-powered real-time communication

### User Profiles
- Registration and login
- Email/password authentication
- Profile management
- Profile image support

### Notifications
- Firebase Cloud Messaging (FCM)
- Push notifications for new messages

## Technologies Used

### Android
- Java
- Android SDK
- View Binding
- Material Design Components

### Firebase
- Firebase Authentication
- Cloud Firestore
- Firebase Storage
- Firebase Cloud Messaging (FCM)

### Libraries
- Glide (image loading)
- Firebase UI Firestore
- Gson
- Android Image Cropper
- Android WorkManager

## Project Structure

```
app/
├── activities/
│   ├── MainActivity
│   ├── ChatActivity
│   ├── SearchUserActivity
│   ├── MechinaActivity
│   └── Authentication Activities
│
├── fragments/
│   ├── ChatFragment
│   ├── MechinotFragment
│   ├── ProfileFragment
│   ├── StudentsFragment
│   └── TeachersFragment
│
├── adapters/
│   ├── ChatRecyclerAdapter
│   ├── RecentChatRecyclerAdapter
│   ├── SearchUserRecyclerAdapter
│   └── MechinaAdapter
│
├── model/
│   ├── UserModel
│   ├── ChatMessageModel
│   ├── ChatroomModel
│   └── Mechina
│
└── utils/
    ├── FirebaseUtil
    └── AndroidUtil
```

## Requirements

- Android Studio
- Android SDK 24+
- Firebase Project
- Google Services Configuration (`google-services.json`)

## Installation

1. Clone the repository:

```bash
git clone https://github.com/Zkiva1/Mechinet.git
```

2. Open the project in Android Studio.

3. Create a Firebase project.

4. Enable:
   - Authentication
   - Firestore Database
   - Storage
   - Cloud Messaging

5. Download your `google-services.json` file and place it inside:

```text
app/google-services.json
```

6. Sync Gradle and run the project.

## Future Improvements

- Group chats
- Favorite Mechinot
- Dark mode support
- Hebrew/English localization

Developed as a project focused on improving communication and information accessibility between prospective students and Israeli Mechinot.
