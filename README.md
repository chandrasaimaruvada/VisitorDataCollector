# CustomerDataCollector

A Spring Boot-based web application that allows users to fill out a digital form using a mobile-friendly interface accessed via QR code. Submitted data is securely sent to a **Google Sheet**, and a **backup sheet** is automatically created to ensure data integrity. Admins can log in to view, filter, search, and export submitted records.

---

## 🔗 Live Demo
> Coming soon (e.g., Render, Vercel, or custom domain link)

---

## 📸 Flow Diagram

```mermaid
graph TD
    A[User scans QR Code] --> B[HTML Form Opens in Mobile]
    B --> C[User submits data]
    C --> D[Spring Boot Controller]
    D --> E[Google Sheets API]
    E --> F[Main Sheet]
    E --> G[Backup Sheet (Read-only)]
    H[Admin Login] --> I[Admin Dashboard]
    I --> J[View/Search/Filter]
    I --> K[Export to Excel/CSV]
```

---

## 🚀 Features

- 📱 **Mobile-Responsive Form** via QR Code
- ✍️ **Data Submission** to Google Sheet (live tracking)
- 🔒 **Admin Login** with session security
- 📊 **Admin Dashboard**: View, search, filter records
- 📤 **Export data** to Excel or CSV
- 🛡️ **Backup Sheet** creation for data protection (read-only)
- 🔔 **Email Notification** (optional)
- 🌐 Deployable on Render, Heroku, or any cloud platform

---

##App View

<img width="1470" alt="image" src="https://github.com/user-attachments/assets/74bcf421-117b-4d89-9098-eee9ecf4070f" />

<img width="1470" alt="image" src="https://github.com/user-attachments/assets/7dba554d-f609-463f-95ad-6c2fdaa2a613" />

<img width="1470" alt="image" src="https://github.com/user-attachments/assets/e6c049d5-d414-4fd3-8817-6c10d3d8a07d" />

<img width="1470" alt="image" src="https://github.com/user-attachments/assets/32ef6692-e6a9-4051-8ae6-a2d05f6c6d9f" />





## 🛠️ Tech Stack

| Layer | Technology |
|------|------------|
| Backend | Java, Spring Boot |
| Frontend | HTML, CSS, Thymeleaf |
| API Integration | Google Sheets API |
| Deployment | Render.com |
| Build Tool | Maven |
| Database | Google Sheets (NoSQL style) |

---

## 📂 Project Structure

```
CustomerDataCollector
├── src
│   ├── main
│   │   ├── java/com/example/customerdatacollector
│   │   │   ├── controller
│   │   │   ├── service
│   │   │   ├── model
│   │   │   └── config
│   │   └── resources
│   │       ├── static
│   │       ├── templates
│   │       └── application.properties
│   └── test/java
├── credentials.json
├── pom.xml
└── README.md
```

---

## ⚙️ Setup Instructions

1. **Clone the Repository**
```bash
git clone https://github.com/yourusername/CustomerDataCollector.git
cd CustomerDataCollector
```

2. **Add your `credentials.json`**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a project and enable the Google Sheets API
   - Download the OAuth 2.0 client credentials JSON file and place it in the root directory

3. **Update `application.properties`**
```properties
google.sheet.id=your_main_sheet_id_here
backup.sheet.id=your_backup_sheet_id_here
admin.email=admin@example.com
admin.password=secure_password
```

4. **Run the App**
```bash
./mvnw spring-boot:run
```

5. **Access the App**
   - Form via: `http://localhost:8080/`
   - Admin via: `http://localhost:8080/admin`

---

## 👨‍💻 Admin Dashboard

- **Login**: Enter admin credentials
- **Dashboard**:
  - View all submitted data
  - Filter by name/date
  - Export records as Excel or CSV
- **Analytics** (optional): Track submissions per day

---

## 📌 To-Do / Future Enhancements

- [ ] Add charts for data visualization
- [ ] SMS or WhatsApp notification integration
- [ ] Form validation improvements
- [ ] CAPTCHA for spam protection

---

## 🛡️ Security

- Admin section is protected via session management
- Backup sheet is made read-only to avoid accidental data loss
- Google OAuth tokens are stored securely

---

## 🤝 Contributions

Feel free to fork this repository and submit pull requests. For major changes, please open an issue first.

---

## 📄 License

[MIT](LICENSE)

---

## 🙏 Acknowledgements

- Google Sheets API Documentation
- Spring Boot Community
- Render.com for free cloud hosting
