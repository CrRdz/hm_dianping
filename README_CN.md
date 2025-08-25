
# hm_dianping
[English](./README.md) | 中文

黑马点评（简化版大众点评）项目简介：本项目是类似于大众点评等APP的点评类项目。实现了短信登录、探店评论、优惠券秒杀、每日签到、好友关注、粉丝关注博主、积极推送博主相关博客等多个模块。用户可以在首页浏览推荐内容，搜索附近的商家，查看商家详情和评论，发布店铺探索博客，抢购商家发布的限时秒杀产品。

- **后端**：在 `master` 分支  
- **前端**：在 `frontend` 分支  

---

## 🚀 项目介绍

本项目旨在还原大众点评的核心业务功能，通过前后端分离的方式进行开发，涵盖了以下主要模块：

- 用户注册与登录（支持短信验证码登录）
- 商户信息展示与搜索
- 点评功能
- 用户关注功能
- 优惠券秒杀
- 店铺签到
- 地图定位与附近商户推荐

---

## 🛠 技术栈

### 后端（`master` 分支）
- **框架**：Spring Boot、MyBatis-Plus  
- **数据库**：MySQL  
- **缓存**：Redis（用于登录状态、验证码、缓存预热、分布式锁等）  
- **消息队列**：Redis 
- **工具**：Maven / RESP / JMETER

### 前端（`frontend` 分支）
- **框架**：Vue 3 + ElementUI
- **构建工具**：Vite  
- **UI 框架**：Element Plus 
- **部署**：Nginx

---

## 📦 克隆项目


### 1. 克隆仓库（默认会拉取 `master` 分支）
```bash
git clone https://github.com/CrRdz/hm_dianping.git
```
### 2. 查看远程分支
```bash
cd hm_dianping
git branch -r
```
## 🔧 后端（master 分支）
### 1. 切换到后端代码分支
```bash
git checkout master
```
### 2. 配置数据库（MySQL）、Redis 等服务。
导入 `sql` 文件初始化数据库

修改 `application.yml` 配置数据库连接信息
默认运行在 `http://localhost:8081`
### 3. 启动后端服务：
```bash
mvn spring-boot:run
```
## 💻 前端（frontend 分支）
### 1. 切换到前端代码分支
```bash
git checkout master
```
### 2. 启动前端服务：
```bash
cd nginx-1.18.0
start nginx.exe
```
默认运行在 `http://localhost:8080`


## 🤝 贡献
欢迎提交 Issue 或 Pull Request 来改进项目。







## 🗺️ 开发计划

- [ ]  登录使用个人用户邮箱发送短信验证码 / 使用阿里云短信服务实现短信登录功能
- [ ]  秒杀，高并发的情境下采用专业消息队列中间件RabbitMQ代替redis消息队列来优化秒杀下单，减轻数据库的压力。
- [ ]  秒杀，高并发的情境下使用令牌桶算法进行一定程度上的限流
- [ ]  使用Redis中的ZSET数据结构+时间窗口思想，进行用户限流
