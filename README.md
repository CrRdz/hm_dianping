# hm_dianping
English | [中文](./README_CN.md)

A simplified clone of **Dianping** (similar to Yelp/Dianping apps).  
This project implements key features such as SMS login, store reviews, coupon flash sales, daily check-ins, user follow system, follower/blog subscriptions, and more.  
Users can browse recommended content on the homepage, search for nearby merchants, view store details and reviews, post exploration blogs, and grab limited-time flash sale products.

- **Backend**: located in the `master` branch  
- **Frontend**: located in the `frontend` branch  

---

## 🚀 Project Introduction

The goal of this project is to replicate the core business features of Dianping, developed using a frontend-backend separated architecture.  
Main modules include:

- User registration and login (with SMS verification support)
- Merchant information display and search
- Review system
- User follow/unfollow
- Coupon flash sale
- Store check-in
- Map-based merchant recommendations

---

## 🛠 Tech Stack

### Backend (`master` branch)
- **Framework**: Spring Boot, MyBatis-Plus  
- **Database**: MySQL  
- **Cache**: Redis (used for session storage, verification codes, cache warm-up, distributed locks, etc.)  
- **Message Queue**: Redis  
- **Tools**: Maven / RESP / JMeter  

### Frontend (`frontend` branch)
- **Framework**: Vue 3 + ElementUI  
- **Build Tool**: Vite  
- **UI Library**: Element Plus  
- **Deployment**: Nginx  

---

## 📦 Clone Repository

### 1. Clone repository (default branch: `master`)
```bash
git clone https://github.com/CrRdz/hm_dianping.git
```
### 2. Check remote branches
```bash
cd hm_dianping
git branch -r
```
## 🔧 Backend (master branch)
### 1. Switch to backend branch
```bash
git checkout master
```
### 2. Configure database (MySQL) and Redis
- Import the `sql` file to initialize the database
- Update `application.yml` with your database connection info
```bash
cd hm_dianping
git branch -r
```
### 3. Start backend service
```bash
mvn spring-boot:run
```
Default backend runs at: http://localhost:8081

## 💻 Frontend (frontend branch)
### 1. Switch to frontend branch
```bash
git checkout frontend
```
### 2. Check remote branches
```bash
cd nginx-1.18.0
start nginx.exe
```
Default frontend runs at: http://localhost:8080

## 🤝 Contributing
Contributions are welcome! Feel free to open an Issue or submit a Pull Request to improve the project.
## 🗺️ Roadmap
- [ ]  Implement SMS login using personal email service / Aliyun SMS service
- [ ]  Optimize flash sale with RabbitMQ instead of Redis queue under high concurrency, reducing DB pressure 
- [ ]  Apply token bucket algorithm for traffic limiting during flash sales
- [ ]  Use Redis ZSET + time window strategy for user request rate limiting