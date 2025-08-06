<!-- Badges -->
[![Status](https://img.shields.io/badge/status-development-orange.svg)](https://github.com/MuhammadJuzairiSafitli/virtual-class-universitas-nurdin-hamzah)
[![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![API Level](https://img.shields.io/badge/API%20Level-25%2B-brightgreen.svg)](https://developer.android.com/about/versions/nougat)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-red.svg)](https://developer.android.com/topic/libraries/architecture/viewmodel)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

<p align="center">
  <img src="https://i.ibb.co.com/P050Rn7/logo-unh.png" alt="University Logo" border="0" height="180">
</p>

# 🚀 Virtual Class Android App: Empowering Digital Learning

An intuitive and performant Android application designed to streamline the online learning
experience for students and lecturers. Developed as a final project by **Muhammad Juzairi Safitli
** (Student ID: 21111073), this app is built with native Kotlin, emphasizing a modern UX/UI and
robust local data management for initial functional testing.

---

## 💡 Project Overview

The Virtual Class Android App aims to bridge the gap in distance learning by providing a
comprehensive platform for academic interactions. It focuses on delivering a responsive user
experience, optimal performance, and seamless data synchronization via a local Room Database, making
online education accessible and efficient for testing purposes.

---

## 📚 Table of Contents

1. [Key Features](#-key-features)
2. [Demo & Screenshots](#-demo--screenshots)
3. [UI/UX Design](#-uiux-design)
4. [Technology Stack](#-technology-stack)
5. [Project Architecture](#-project-architecture)
6. [Prerequisites](#-prerequisites)
7. [Installation & Running](#-installation--running)
8. [Dependencies](#-dependencies)
9. [Contributing](#-contributing)
10. [License](#-license)

---

## 🚀 Key Features

* **User Authentication**: Secure and distinct login/registration flows for Students (via Student
  ID) and Lecturers (via Employee ID).
* **Dynamic Dashboard**: A personalized overview displaying upcoming assignments, today's class
  schedule, and relevant attendance statistics at a glance.
* **Assignment Management**: Robust functionality to view a comprehensive list of assignments, delve
  into their details, and facilitate submission of coursework.
* **Interactive Class Schedule**: A clear and concise display of the weekly course timetable,
  helping users stay organized.
* **Virtual Classroom Access**: Seamless entry into virtual learning environments, offering access
  to course materials, engaging discussion forums, and comprehensive participant lists.
* **Real-time Automatic Attendance**: Innovative real-time attendance tracking for accurate and
  effortless record-keeping.
* **Personalized Settings**: Options to edit user profiles, toggle between light and dark themes for
  enhanced readability, and securely log out.

---

## 🎬 Demo & Screenshots

<p align="center">
  <h3>COMING SOON</h3>
  <p>Live demos and detailed screenshots will be uploaded here to showcase the application's functionality and user interface.</p>
<!--   <img src="./screenshots/dashboard.png" alt="Dashboard" width="200" />
  <img src="./screenshots/assignments.png" alt="Assignments" width="200" />
  <img src="./screenshots/virtual_class.png" alt="Virtual Classroom" width="200" /> -->
</p>

---

## 🎨 UI/UX Design

<p align="center">
  <h3>COMING SOON</h3>
  <p>The user interface and user experience design prototypes, developed using Figma, will be made available here.</p>
</p>

---

## 🛠️ Technology Stack

* **Programming Language**: Kotlin (Native Android)
* **IDE**: Android Studio
* **Build System**: Gradle Kotlin DSL
* **UI Toolkit**: Standard XML layouts
* **Asynchronous Operations**: Kotlin Coroutines, Flow
* **Dependency Injection**: Koin
* **Database**: Room Persistence Library (for local caching/data management)
* **Image Loading**: Glide
* **State Management**: Android Architecture Components (ViewModel, LiveData)
* **Navigation**: Android Jetpack Navigation Component
* **Backend/Data Management**: Local Room Database (for functional testing)
* **Design Tools**: Figma

---

## 📐 Project Architecture

This project follows the **MVVM (Model-View-ViewModel)** architectural pattern to ensure a clear
separation of concerns, testability, and maintainability.

* **Model**: Represents the data layer, including data sources (local database) and repositories.
* **View**: Comprises the UI components (Fragments, Activities) that observe changes from the
  ViewModel.
* **ViewModel**: Acts as a bridge between the Model and the View, exposing data streams and handling
  UI-related logic.

---

## ⚙️ Prerequisites

* **Android Studio**: Dolphin | 2021.3.1 or higher
* **JDK**: Version 11 or higher
* **Android SDK**: Minimum API Level 25 (Android 7.1 Nougat)
* **Local Database**: Data will be managed through Room Persistence Library.

---

## 🚧 Installation & Running

Follow these steps to set up and run the project on your local machine:

```bash
# Clone the repository
git clone https://github.com/username/virtual-class-nh.git
cd virtual-class-nh

# Open the project in Android Studio
# After opening, Android Studio will automatically sync the Gradle files.
# If sync fails, go to File > Sync Project with Gradle Files.

# Run the Application
# Connect an Android device or start an AVD (Android Virtual Device) emulator.
# Click the 'Run' button (green triangle) in Android Studio.
```

---

## 📦 Dependencies

Key dependencies used in this project include:

* **AndroidX Libraries**: Core components, AppCompat, ConstraintLayout, Lifecycle, Navigation.
* **Kotlin Coroutines**: For asynchronous programming.
* **Koin**: For dependency injection.
* **Room**: For local database persistence.
* **Glide**: For efficient image loading.
* **Material Design Components**: For modern UI elements.

(Full list of dependencies can be found in the respective `build.gradle.kts` files of each module.)

---

## 🤝 Contributing

Contributions are welcome! If you have suggestions for improvements or find any issues, please feel
free to:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature-name`).
3. Make your changes.
4. Commit your changes (`git commit -m 'feat: Add new feature'`).
5. Push to the branch (`git push origin feature/your-feature-name`).
6. Open a Pull Request.

---

## 📄 License

This project is licensed under the MIT License - see
the [LICENSE](https://opensource.org/licenses/MIT) file for details.