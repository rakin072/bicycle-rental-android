# Bicycle Rental System

## Overview
The Bicycle Rental System is an Android application that allows users to rent bicycles easily. The app provides functionalities for user registration, login, browsing available bicycles, managing rentals, and messaging between users. It integrates with Firebase for backend services, including authentication and real-time database operations.

## Features
- User authentication (login, registration, password recovery)
- Browse and rent bicycles
- Manage user profiles and bicycle listings
- View rental history and details
- Chat functionality between users
- Notifications for rental updates and messages

## Directory Structure
```
bicycle-rental-android
├── app
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── example
│   │   │   │           └── bicyclerentalsystem
│   │   │   │               ├── MainActivity.java
│   │   │   │               ├── models
│   │   │   │               │   ├── Bicycle.java
│   │   │   │               │   ├── Rental.java
│   │   │   │               │   └── User.java
│   │   │   │               ├── activities
│   │   │   │               │   ├── LoginActivity.java
│   │   │   │               │   ├── RegisterActivity.java
│   │   │   │               │   ├── HomeActivity.java
│   │   │   │               │   ├── BicycleListActivity.java
│   │   │   │               │   └── RentalHistoryActivity.java
│   │   │   │               ├── adapters
│   │   │   │               │   ├── BicycleAdapter.java
│   │   │   │               │   └── RentalAdapter.java
│   │   │   │               ├── fragments
│   │   │   │               │   ├── ProfileFragment.java
│   │   │   │               │   └── MapFragment.java
│   │   │   │               ├── services
│   │   │   │               │   └── FirebaseService.java
│   │   │   │               └── utils
│   │   │   │                   ├── DatabaseHelper.java
│   │   │   │                   └── Constants.java
│   │   │   ├── res
│   │   │   │   ├── layout
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   ├── activity_register.xml
│   │   │   │   │   ├── activity_home.xml
│   │   │   │   │   ├── activity_bicycle_list.xml
│   │   │   │   │   ├── item_bicycle.xml
│   │   │   │   │   └── item_rental.xml
│   │   │   │   ├── values
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   └── styles.xml
│   │   │   │   └── drawable
│   │   │   ├── AndroidManifest.xml
│   │   │   └── google-services.json
│   │   └── test
│   │       └── java
│   │           └── com
│   │               └── example
│   │                   └── bicyclerentalsystem
│   │                       └── ExampleUnitTest.java
│   └── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

## Getting Started
1. Clone the repository:
   ```
   git clone https://github.com/rakin072/Rakin_2207082_Bicycle-Rental-Mini-System.git
   ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Configure Firebase by adding your `google-services.json` file to the `app/src/main` directory.
5. Build and run the application on an Android device or emulator.

## Dependencies
- Firebase Authentication
- Firebase Realtime Database
- AndroidX libraries
- Retrofit (for network operations)
- Glide (for image loading)

## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License
This project is licensed under the MIT License. See the LICENSE file for details.