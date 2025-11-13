# Dino the Annihilator - Catch the Balls Game

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-007396?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

**A thrilling space-themed game where a dinosaur character catches planets using a lasso!**

[Features](#-features) • [Installation](#-installation) • [Documentations](#-documentations)

</div>

---

## Description

**Dino the Annihilator** is an action-packed arcade game built with Java Swing. Control a dinosaur character navigating through space, catching planets (skill balls) with a lasso while avoiding dangerous gas planets. The game features smooth animations, background music, and a MySQL-powered leaderboard system.

### Game Objective
- **Catch blue planets** (water planets) using your lasso to earn points
- **Avoid red planets** (gas planets) that decrease your HP
- **Survive as long as possible** and climb the leaderboard
- **Master the timing** to maximize your score

---

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17+ |
| GUI Framework | Java Swing |
| Database | MySQL 8.0+ |
| Design Pattern | MVVM (Model-View-ViewModel) |
| Audio | Java Sound API (javax.sound.sampled) |
| Graphics | Java AWT & BufferedImage |

---

## Installation

### Prerequisites
- **Java JDK 17** or higher
- **MySQL Server 8.0** or higher
- **Windows OS** (for `run.bat` script)

### Setup Steps

1. **Clone the Repository**
```bash
git clone https://github.com/yourusername/dino-the-annihilator.git
cd dino-the-annihilator
```
2. **Setup MySQL Database**
```
CREATE DATABASE catch_the_balls_db;
USE catch_the_balls_db;

CREATE TABLE players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    skor INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
3. **Configure Database Connection**
Edit DatabaseConfig.java:
```
public static final String DB_URL = "jdbc:mysql://localhost:3306/catch_the_balls_db";
public static final String DB_USER = "your_username";
public static final String DB_PASSWORD = "your_password";
```
4. **Run the Game**
```
# Windows
run.bat

# Or manually
javac -cp "lib/mysql-connector-j-9.2.0.jar;." src\model\*.java src\view\*.java src\viewmodel\*.java src\config\*.java src\Main.java
java -cp "lib/mysql-connector-j-9.2.0.jar;src" Main
```
## Documentations
<img width="785" height="592" alt="image" src="https://github.com/user-attachments/assets/910f6c1e-2f74-4681-8302-8857a25b2d23" />
<img width="1263" height="706" alt="image" src="https://github.com/user-attachments/assets/7c7d5c47-285f-48b0-9383-48755e445e04" />
<img width="1265" height="711" alt="image" src="https://github.com/user-attachments/assets/a6f87abd-0d66-4e25-a4e1-2a6588e13828" />

https://youtu.be/97CwQZHXnnQ?si=h2mI_JLefX54ZQIT
