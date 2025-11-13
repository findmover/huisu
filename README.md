# 回溯 - Android应用开发文档

## 项目概述

回溯是一款专注于冥想训练和心理积极暗示的个人成长类Android应用。

## 技术栈

- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构模式**: MVVM + Repository Pattern
- **依赖注入**: Hilt
- **数据库**: Room
- **异步处理**: Kotlin Coroutines + Flow
- **数据存储**: DataStore Preferences
- **导航**: Navigation Compose

## 项目结构

```
com.app.huisu/
├── data/
│   ├── dao/              # 数据访问对象
│   ├── database/         # 数据库配置
│   ├── entity/           # 数据实体
│   ├── preferences/      # DataStore配置
│   └── repository/       # 数据仓库层
├── di/                   # 依赖注入模块
├── service/              # 后台服务
├── receiver/             # 广播接收器
├── ui/
│   ├── affirmation/      # 积极暗示模块
│   ├── components/       # 通用UI组件
│   ├── meditation/       # 冥想模块
│   ├── navigation/       # 导航配置
│   ├── statistics/       # 统计模块
│   └── theme/            # 主题配置
├── util/                 # 工具类
├── HuiSuApplication.kt   # Application类
└── MainActivity.kt       # 主Activity

## 核心功能

### 1. 冥想模块
- 计时器功能
- B站视频链接跳转
- 数据统计(今日/本周/本月/总计)
- 视频链接管理

### 2. 积极暗示模块
- 定时提醒(早中晚)
- 倒计时默念
- 暗示语自定义管理
- 完成率统计

### 3. 统计模块
- 趋势图表
- 完成日历
- 成就徽章
- 综合数据展示

## 编译说明

### 环境要求
- Android Studio Hedgehog | 2023.1.1+
- JDK 17+
- Android SDK 34
- Gradle 8.2+

### 构建步骤

1. **克隆或打开项目**
   ```bash
   cd D:\myApp
   ```

2. **同步Gradle**
   在Android Studio中点击 "Sync Project with Gradle Files"

3. **构建项目**
   ```bash
   ./gradlew build
   ```

4. **运行应用**
   - 连接Android设备或启动模拟器
   - 点击 Run 按钮或执行:
   ```bash
   ./gradlew installDebug
   ```

## 权限说明

应用需要以下权限:
- `INTERNET`: 打开B站链接
- `POST_NOTIFICATIONS`: 发送通知
- `SCHEDULE_EXACT_ALARM`: 定时提醒
- `FOREGROUND_SERVICE`: 前台服务(冥想计时)
- `WAKE_LOCK`: 保持唤醒

## 数据库结构

### meditation_records (冥想记录)
- id, start_time, end_time, duration, video_link, create_date

### affirmation_records (默念记录)
- id, content, start_time, duration, scheduled_time, time_slot, is_completed, is_makeup, create_date

### affirmations (暗示语库)
- id, content, is_active, order, created_at

### video_links (视频链接)
- id, link, title, is_default, created_at

## 后续开发计划

- [ ] 完善视频链接管理界面
- [ ] 完善暗示语设置界面
- [ ] 实现数据导出/导入功能
- [ ] 添加深色模式
- [ ] 集成图表库展示数据可视化
- [ ] 优化通知调度逻辑
- [ ] 添加Widget小部件
- [ ] 实现成就系统

## 许可证

本项目为个人学习项目

## 联系方式

如有问题请参考PRD文档或联系开发者
```
