# 📚 DisciteOmnes - Study Group & Task Management App

DisciteOmnes is a mobile application developed as part of the SS25 semester project. It enables students to create and join study groups, organize tasks, assign responsibilities, and manage deadlines. The app also includes basic cloud integration and API communication.

## 🚀 Features

- ✅ User registration & login via **Firebase Authentication**
- ✅ Create, join, and leave **study groups**
- ✅ Manage **group-specific tasks**
  - Title & description
  - Due date
  - Assignee (from group members)
  - Completion tracking
- ✅ Welcome message from an external **API** via **Retrofit**
- ✅ Real-time sync using **Firebase Realtime Database**
- ✅ Persistent user session with **SharedPreferences**
- ✅ Clean, intuitive UI using **Material Design**

---

## 🛠 Technologies Used

- **Java**
- **Android SDK**
- **Firebase Authentication**
- **Firebase Realtime Database**
- **Retrofit2**
- **Json**
- **Material Components**
- **Gradle**

---

## 🧪 Testing & Stability

- All core functionalities were tested for group and task workflows.
- Minimal crash risk due to defensive UI checks and `isAdded()` verification on fragments.
- Retrofit gracefully handles API failures with fallback Toast messages.

---

## 📦 Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/disciteomnes.git
   ```
2. Open in **Android Studio**
3. Add your `google-services.json` for Firebase setup
4. Build & run the app on an emulator or Android device

---

## 🔐 Firebase Structure

```
/users
  userId/
    name
    email
    ...

/groups
  groupId/
    name
    description
    members/
      memberId: true

/tasks
  taskId/
    title
    description
    completed
    groupId
    assignedTo
    assignedToName
    dueDate
```

---

## 🌐 API Integration (Retrofit)

- Base URL: `https://run.mocky.io/`
- Endpoint: `/v3/<mock-id>`  
- Example response:
  ```json
  { "message": "Welcome to DisciteOmnes!" }
  ```

---

## 👥 Authors

- **Matey Radkov**
