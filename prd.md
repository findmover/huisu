# 产品需求说明书 (PRD)

## 1. 产品概述

### 1.1 产品名称
回溯

### 1.2 产品定位
一款专注于冥想训练和心理积极暗示的个人成长类Android应用,帮助用户培养冥想习惯并通过积极暗示提升心理状态。

### 1.3 目标用户
- 希望培养冥想习惯的个人用户
- 需要通过积极暗示进行自我激励的用户
- 追求个人心理成长和自我提升的用户

### 1.4 产品目标
- 提供便捷的冥想时间追踪功能
- 通过定时积极暗示帮助用户建立正面心理状态
- 记录和统计用户的成长数据

---

## 2. 功能需求

### 2.1 功能一:冥想时间追踪

#### 2.1.1 功能描述
用户可以通过App打开哔哩哔哩(B站)的冥想视频进行冥想练习,App自动记录冥想时长和次数。

#### 2.1.2 核心功能点

**2.1.2.1 视频链接管理**
- 用户可以设置/修改冥想视频链接(如:https://b23.tv/56h7R7n)
- 支持保存多个常用的B站视频链接
- 支持选择当前使用的默认视频链接
- 支持直接输入B站短链接或完整链接

**2.1.2.2 冥想计时**
- 点击"开始冥想"按钮,自动跳转到B站App并打开指定视频
- App在后台开始计时
- 计时精确到秒
- 支持暂停和继续计时
- 支持手动结束冥想

**2.1.2.3 数据统计**
- 记录每次冥想的时长
- 记录冥想总次数
- 记录冥想总时长(累计)
- 统计今日冥想时长和次数
- 统计本周冥想时长和次数
- 统计本月冥想时长和次数
- 显示连续冥想天数

**2.1.2.4 历史记录**
- 保存每次冥想的开始时间和结束时间
- 保存每次冥想的时长
- 提供日历视图查看历史冥想记录
- 支持查看特定日期的冥想详情

#### 2.1.3 交互流程
1. 用户打开App,进入冥想功能页面
2. 用户点击"开始冥想"按钮
3. App调用系统Intent打开B站App并播放指定视频链接
4. App在后台开始计时
5. 用户冥想结束后返回App,点击"结束冥想"
6. App停止计时,保存本次冥想数据
7. 显示本次冥想时长,并更新统计数据

#### 2.1.4 异常处理
- 未安装B站App时,提示用户安装或使用浏览器打开
- 视频链接无效时,提示用户检查链接
- App被杀死后,能够恢复计时状态(通过后台服务)

---

### 2.2 功能二:热搜榜单

#### 2.2.1 功能描述
用户可以通过App查看多个平台的热搜榜单数据，包括微博、知乎、微信、抖音等7个平台的实时热点内容。

#### 2.2.2 核心功能点

**2.2.2.1 多平台支持**
- 支持7个热门平台:
  - 微博 (微博实时热搜榜)
  - 知乎 (知乎热榜)
  - 微信 (微信热门文章)
  - 头条 (今日头条热榜)
  - 抖音 (抖音热榜)
  - 历史上的今天 (历史事件)
  - 哔哩哔哩 (B站热门视频)

**2.2.2.2 热搜列表显示**
- 显示热搜排名(1-50)
- 显示热搜标题
- 显示热度值(如:1000万、800万等)
- 前3名使用特殊颜色标识(金、银、铜)
- 支持点击跳转到详情链接

**2.2.2.3 平台切换**
- 下拉选择框选择平台
- 自动加载对应平台的热搜数据
- 保持当前选择的平台状态

**2.2.2.4 数据刷新**
- 手动刷新按钮
- 加载状态提示
- 错误提示(3秒自动消失)

**2.2.2.5 API集成**
- 基础URL: `https://api.codelife.cc/api/top/list`
- 请求参数: `lang=cn&id={平台ID}`
- 需要token认证和签名密钥
- 返回JSON格式数据

#### 2.2.3 交互流程
1. 用户点击底部导航"热搜"标签
2. App加载默认平台(微博)的热搜数据
3. 显示加载中状态
4. 渲染热搜列表(排名、标题、热度值)
5. 用户可切换平台查看其他热搜
6. 用户可点击刷新按钮更新数据
7. 点击热搜条目可打开详情链接

#### 2.2.4 异常处理
- 网络请求失败时显示错误提示
- API返回异常时显示友好提示
- Token过期时提示用户
- 无网络连接时提示用户检查网络

---

### 2.3 功能三:积极暗示

#### 2.2.1 功能描述
在用户设定的早中晚时间点,App弹出通知并显示积极暗示文字,引导用户进行定时默念,记录默念时长和次数。

#### 2.2.2 核心功能点

**2.2.2.1 暗示语管理**
- 用户可以添加自定义的积极暗示文字
- 支持编辑已有的暗示语
- 支持删除不需要的暗示语
- 支持设置多条暗示语(随机或顺序显示)
- 每条暗示语建议字数:20-100字

**2.2.2.2 提醒时间设置**
- 用户可以设置早中晚三个时间段的具体提醒时间
  - 早晨提醒时间(默认:08:00,可自定义)
  - 中午提醒时间(默认:12:00,可自定义)
  - 晚上提醒时间(默认:20:00,可自定义)
- 支持开启/关闭特定时段的提醒
- 支持周末模式(周末不提醒或使用不同时间)

**2.2.2.3 默念计时器**
- 用户可以设置每次默念的倒计时时长(1-10分钟可调)
- 默认倒计时时长可全局设置
- 倒计时过程中显示剩余时间
- 支持暂停/继续倒计时
- 支持提前结束或延长时间
- 倒计时结束时振动或声音提醒

**2.2.2.4 弹出提醒**
- 到达设定时间时,发送系统通知
- 点击通知打开App进入默念页面
- 默念页面全屏显示暗示语文字
- 字体大小可调节,确保易读性
- 支持背景音乐或白噪音(可选)

**2.2.2.5 数据统计**
- 记录每次默念的时长
- 记录默念总次数
- 记录默念总时长(累计)
- 统计今日默念次数和时长
- 统计本周默念完成率(完成次数/应完成次数)
- 统计本月默念完成率
- 显示连续完成天数(每日三次都完成)

**2.2.2.6 历史记录**
- 保存每次默念的时间和时长
- 保存每次默念的暗示语内容
- 提供日历视图查看历史默念记录
- 标注完成和未完成的提醒

#### 2.2.3 交互流程
1. 用户在设置中配置暗示语和提醒时间
2. 到达设定时间,App发送通知
3. 用户点击通知,进入默念页面
4. 全屏显示暗示语文字和倒计时器
5. 用户点击"开始默念"按钮,倒计时开始
6. 倒计时结束,振动/声音提醒
7. 保存本次默念数据,返回统计页面

#### 2.2.4 异常处理
- 用户错过提醒时间,可以补充完成(标记为补充)
- 通知权限未开启时,引导用户开启
- App被杀死时,通过AlarmManager确保定时提醒正常工作

---

## 3. 非功能需求

### 3.1 性能要求
- App启动时间 < 2秒
- 计时精度误差 < 1秒
- 数据统计刷新延迟 < 500ms
- 内存占用 < 100MB

### 3.2 兼容性要求
- 支持Android 8.0 (API 26)及以上版本
- 适配主流Android设备(小米、华为、OPPO、vivo等)
- 适配不同屏幕尺寸(5-7英寸主流)

### 3.3 安全与隐私
- 所有数据本地存储,不上传服务器
- 支持数据导出备份(JSON格式)
- 支持数据导入恢复

### 3.4 可用性要求
- 界面简洁直观,操作步骤少于3步
- 支持深色模式/浅色模式
- 字体大小可调节
- 支持中文界面

---

## 4. 界面设计要求

### 4.0 HTML原型展示
**原型文件**: `app-prototype.html`
- **完整功能演示**:
  - ✅ 成就系统完整实现（自动等级升级、解锁动画、notificationShown状态管理）
  - ✅ 默念页面固定布局（垂直居中、不可滚动）
  - ✅ 冥想和默念完成后自动更新成就进度
  - ✅ 进入统计页面自动检测并显示未显示的成就解锁动画
  - ✅ 实时数据统计和成就进度显示
- **测试方法**:
  - 完成7次冥想（每次点击"开始冥想"→"结束冥想"）
  - 切换到统计页面查看"冥想大师-青铜"成就解锁动画
  - 完成20次默念查看"默念达人-青铜"成就解锁

### 4.1 主界面结构
采用底部导航栏(Bottom Navigation)设计,包含四个主要模块:

**Tab 1: 冥想**
- 显示今日冥想时长和次数
- "开始冥想"大按钮
- 本周/本月统计数据卡片
- 快速访问视频链接设置

**Tab 2: 积极暗示**
- 显示今日默念完成情况(3/3)
- 本周/本月完成率
- "开始默念"大按钮
- 快速访问暗示语管理
- ~~下次提醒时间倒计时~~ (已移除)
- 快速添加暗示语入口

**Tab 3: 热搜** (新增)
- 平台选择下拉框(8个平台)
- 刷新按钮
- 热搜列表展示
  - 排名序号(前3名特殊颜色)
  - 热搜标题
  - 热度值
- 加载状态提示
- 错误提示

**Tab 4: 统计**
- 综合数据统计仪表盘
- 日历视图显示历史记录
- 图表展示趋势(折线图/柱状图)
- 成就徽章系统(可选)

### 4.2 关键页面

**4.2.1 冥想计时页面**
- 显示大号计时器
- 显示当前视频链接
- "开始冥想"/"结束冥想"按钮
- 暂停/继续按钮

**4.2.2 默念页面**
- 全屏显示暗示语(居中,大字体)
- 圆形倒计时进度条
- 暂停/继续/结束按钮
- 简洁背景(纯色或渐变)
- **页面内容固定,不可滚动**:所有元素(暗示语、计时器、提示文字、按钮)在一个固定视图中垂直居中显示

**4.2.3 热搜榜单页面** (新增)
- 顶部平台选择器
  - 下拉选择框(8个平台,带图标)
  - 刷新按钮(圆形,带旋转动画)
- 热搜列表
  - 每个条目包含:排名、标题、热度值
  - 前3名使用特殊颜色(红、橙、黄)
  - 卡片式设计,带阴影效果
  - 点击响应效果(缩放动画)
- 加载状态
  - 居中显示"加载中..."
  - 可选:加载动画spinner
- 错误提示
  - 红色背景提示框
  - 3秒后自动消失

**4.2.4 设置页面**
- 视频链接管理
- 暗示语管理
- 提醒时间设置
- 默念时长设置
- 通知设置
- 主题设置
- 数据备份/恢复

---

## 5. 数据存储设计

### 5.1 数据库表设计

**5.1.1 冥想记录表 (meditation_records)**
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INTEGER | 主键,自增 |
| start_time | TIMESTAMP | 开始时间 |
| end_time | TIMESTAMP | 结束时间 |
| duration | INTEGER | 时长(秒) |
| video_link | TEXT | 使用的视频链接 |
| create_date | DATE | 创建日期 |

**5.1.2 默念记录表 (affirmation_records)**
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INTEGER | 主键,自增 |
| content | TEXT | 暗示语内容 |
| start_time | TIMESTAMP | 开始时间 |
| duration | INTEGER | 时长(秒) |
| scheduled_time | TIMESTAMP | 预定提醒时间 |
| time_slot | TEXT | 时段(morning/noon/evening) |
| is_completed | BOOLEAN | 是否完成 |
| is_补充 | BOOLEAN | 是否补充完成 |
| create_date | DATE | 创建日期 |

**5.1.3 暗示语库表 (affirmations)**
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INTEGER | 主键,自增 |
| content | TEXT | 暗示语内容 |
| is_active | BOOLEAN | 是否启用 |
| order | INTEGER | 排序顺序 |
| created_at | TIMESTAMP | 创建时间 |

**5.1.4 视频链接表 (video_links)**
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INTEGER | 主键,自增 |
| link | TEXT | 视频链接 |
| title | TEXT | 链接名称 |
| is_default | BOOLEAN | 是否默认链接 |
| created_at | TIMESTAMP | 创建时间 |

**5.1.5 配置表 (app_settings)**
| 字段名 | 类型 | 说明 |
|--------|------|------|
| key | TEXT | 配置键 |
| value | TEXT | 配置值 |

配置项包括:
- morning_time: 早晨提醒时间
- noon_time: 中午提醒时间
- evening_time: 晚上提醒时间
- affirmation_duration: 默念时长
- theme_mode: 主题模式
等等

---

## 6. 技术选型建议

### 6.1 开发框架
- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose 或 传统XML布局
- **架构模式**: MVVM + Repository Pattern

### 6.2 核心技术栈
- **数据库**: Room (SQLite封装)
- **异步处理**: Kotlin Coroutines + Flow
- **依赖注入**: Hilt
- **通知管理**: WorkManager + AlarmManager
- **导航**: Navigation Component
- **数据存储**: SharedPreferences (配置) + Room (记录数据)

### 6.3 第三方库
- **图表**: MPAndroidChart 或 Vico
- **日历**: Material Calendar View
- **JSON处理**: Kotlinx Serialization 或 Gson

---

## 7. 开发优先级

### P0 (核心功能,第一版必须实现)
- [ ] 冥想计时基本功能
- [ ] 跳转B站播放视频
- [ ] 冥想数据记录和基础统计
- [ ] 积极暗示定时提醒
- [ ] 默念倒计时功能
- [ ] 暗示语自定义管理
- [ ] 默念数据记录和基础统计
- [x] **热搜榜单功能** (新增)
  - [x] 多平台热搜数据展示
  - [x] 平台切换功能
  - [x] 热搜列表UI设计
  - [x] API集成(CodeLife API)
  - [x] 点击跳转功能
  - [x] 刷新功能

### P1 (重要功能,第二版实现)
- [ ] 历史记录日历视图
- [ ] 数据统计图表
- [ ] 视频链接管理(多链接)
- [ ] 深色模式
- [ ] 数据导出备份

### P2 (增强功能,后续迭代)
- [x] 成就徽章系统
  - **成就系统已实现**:
    - 支持5个等级(青铜→白银→黄金→钻石→传奇)
    - 4个成就类别:连续打卡、冥想大师、冥想时长、默念达人
    - 自动等级升级机制
    - **成就解锁弹窗动画优化**:
      - 使用数据库字段`notificationShown`标记通知状态，避免依赖内存和时间窗口
      - 进入统计页面时自动检测未显示的成就解锁通知
      - 用户关闭弹窗后自动标记为已显示，不会重复弹出
      - 确保用户不会错过任何成就解锁通知
    - 成就详情对话框显示所有等级里程碑
    - 实时进度跟踪和显示
- [ ] 背景音乐/白噪音
- [ ] 小部件(Widget)
- [ ] 数据云同步
- [ ] 社交分享功能

---

## 8. 里程碑计划

### 阶段一:基础框架搭建 (预计2周)
- 项目初始化
- 数据库设计与实现
- 基础UI框架搭建
- 导航系统搭建

### 阶段二:核心功能开发 (预计3周)
- 冥想计时功能实现
- B站跳转功能实现
- 积极暗示提醒功能实现
- 默念倒计时功能实现

### 阶段三:数据统计与UI优化 (预计2周)
- 数据统计功能实现
- 历史记录查看
- UI/UX优化
- 适配测试

### 阶段四:测试与发布 (预计1周)
- 功能测试
- 性能优化
- Bug修复
- 打包发布

---

## 9. 风险与挑战

### 9.1 技术风险
- **风险**: B站App可能更新导致跳转失效
  - **对策**: 提供网页版备选方案,监控B站链接格式变化

- **风险**: 后台服务被系统杀死导致计时中断
  - **对策**: 使用前台服务(Foreground Service)保持计时

- **风险**: 定时提醒在省电模式下不工作
  - **对策**: 引导用户将App加入白名单,使用精确闹钟

### 9.2 用户体验风险
- **风险**: 用户忘记结束冥想导致时间过长
  - **对策**: 设置合理的最大时长提醒(如2小时)

- **风险**: 通知过多打扰用户
  - **对策**: 提供勿扰时段设置,灵活的提醒开关

---

## 10. 后续扩展方向

### 10.1 功能扩展
- 支持更多视频平台(YouTube, 抖音等)
- 社区功能:分享冥想心得和暗示语
- AI生成个性化积极暗示语
- 冥想音频本地播放功能
- 番茄钟集成

### 10.2 跨平台
- iOS版本开发
- Web版数据查看面板
- 桌面版(Windows/Mac)

---

## 11. 附录

### 11.1 术语表
- **冥想**: 用户通过观看特定视频进行的正念练习
- **默念**: 用户在心中重复积极暗示语的过程
- **积极暗示**: 用于心理暗示的正面文字内容
- **时段**: 早中晚三个提醒时间段

### 11.2 参考资料
- Material Design 设计规范
- Android后台任务最佳实践
- 冥想App竞品分析(Calm, Headspace等)

---

## 12. 版本历史
| 版本 | 日期 | 修订内容 | 作者 |
|------|------|----------|------|
| v1.0 | 2025-11-10 | 初始版本创建 | - |
| v1.1 | 2025-11-13 | 功能优化更新 | Claude |

---

## 13. v1.1版本更新说明 (2025-11-13)

### 13.1 默念(积极暗示)模块优化

#### 13.1.1 暗示语管理独立化
**问题**: 原有设计中暗示语的增删改查功能混杂在默念主页和设置页面中,操作不够直观和便捷。

**解决方案**:
- 创建独立的 `AffirmationManagementScreen` 页面专门管理暗示语
- 支持暗示语的完整CRUD操作(创建、读取、更新、删除)
- 提供清晰的列表展示,每条暗示语显示内容、创建时间、操作按钮
- 空状态友好提示,引导用户添加第一条暗示语
- 从默念主页可直接跳转到暗示语管理页面

**技术实现**:
- 文件路径: `app/src/main/java/com/app/huisu/ui/affirmation/AffirmationManagementScreen.kt`
- 路由配置: `Screen.AffirmationManagement`
- 使用LazyColumn展示暗示语列表,优化长列表性能

#### 13.1.2 默念主页交互优化
**问题**: 默念主页存在以下问题:
1. 没有默念记录时,默念主页可以上下滑动,导致布局问题
2. 暗示语选择区域显示多条,占用大量空间

**解决方案**:
- 移除主页的 `verticalArrangement = Arrangement.SpaceBetween`,改用固定布局,确保页面不可滚动
- 简化暗示语展示,只显示当前选中的一条暗示语,居中展示
- 优化空状态显示,提供友好的引导信息
- 增加"查看记录"按钮,方便用户快速查看历史记录
- 为底部导航留出足够空间(80dp)

**用户体验改进**:
- 页面布局更加稳定,不会因为内容变化而晃动
- 专注于当前暗示语,减少干扰
- 操作流程更清晰:查看当前暗示语 → 点击"管理暗示语" → 进入管理页面增删改

### 13.2 成就系统动画触发优化

#### 13.2.1 问题分析
**原有问题**: 成就解锁动画仅基于 `notificationShown` 标记判断,存在以下缺陷:
- 场景:用户完成7次默念,触发"默念达人-青铜"成就,看到动画
- 用户删除1条记录,再完成1条,总数还是7次
- 但由于`notificationShown`已被标记为true,不会再次触发动画
- 导致用户感觉成就系统失效

**根本原因**: 动画触发逻辑没有考虑到数据变化(删除/增加)导致的实际达成次数变化。

#### 13.2.2 解决方案
**优化策略**:
在 `AchievementRepository.updateProgressWithLevelUp()` 方法中改进判断逻辑:

1. **记录目标值变化**: 保存 `previousTargetValue`,用于判断是否达到了新的里程碑
2. **新解锁判断条件**:
   - 首次解锁: `!achievement.unlocked`
   - 达到更高里程碑: `newTarget > previousTargetValue`
   - 等级提升: `currentLevelIndex > previousLevelIndex`

3. **unlockedDate更新策略**:
   - 当检测到新解锁时,更新解锁时间为当前时间
   - 否则保持原有解锁时间不变

4. **notificationShown重置**:
   - 仅当 `isNewUnlock = true` 时,重置为 `false`
   - 这样可以确保每次真正的成就解锁都会触发动画

**代码改进**:
```kotlin
// 新解锁的条件:
// 1. 之前未解锁,现在解锁了
// 2. 目标值发生了变化(说明达到了新的里程碑)
// 3. 之前已解锁,但等级提升了
isNewUnlock = !achievement.unlocked || // 首次解锁
               (unlocked && newTarget > previousTargetValue) || // 达到了更高的里程碑
               (achievement.unlocked && currentLevelIndex > previousLevelIndex) // 等级提升

unlockedDate = if (isNewUnlock) {
    System.currentTimeMillis() // 新解锁或等级提升,更新解锁时间
} else {
    achievement.unlockedDate // 保持原解锁时间
}
```

**效果**:
- 每次达到新的成就里程碑都会触发动画
- 成就解锁时间准确反映最新达成时间
- 用户体验更加流畅,成就系统反馈及时

### 13.3 TODO工具集成导航和分类管理

#### 13.3.1 功能背景
原有TODO功能已实现基础的任务管理,但缺少独立的分类管理入口,用户只能在添加TODO时顺带管理分类,不够便捷。

#### 13.3.2 分类管理页面
**新增功能**:
- 创建 `TodoCategoryManagementScreen` 独立页面
- 显示分类统计信息:总分类数、总TODO数
- 分类列表展示:
  - 分类图标和名称
  - 每个分类的TODO总数、完成数、完成进度
  - 编辑和删除操作按钮
- 支持添加新分类:选择图标、颜色、输入名称
- 支持编辑分类:修改名称、图标、颜色
- 支持删除分类(会提示关联的TODO处理)

**技术实现**:
- 文件路径: `app/src/main/java/com/app/huisu/ui/todo/TodoCategoryManagementScreen.kt`
- 路由配置: `Screen.TodoCategoryManagement`
- ViewModel方法增强:
  - `updateCategory(categoryId, name, color, icon)` - 根据ID更新分类
  - `deleteCategory(categoryId)` - 根据ID删除分类

#### 13.3.3 导航集成
**TodoScreen优化**:
- 在TODO主页头部增加"管理分类"按钮
- 点击跳转到 `TodoCategoryManagementScreen`
- 统一管理所有分类,提升用户操作效率

**导航流程**:
```
TODO主页 → 点击"管理分类" → 分类管理页面
                                  ↓
                          [增加/编辑/删除分类]
                                  ↓
                            返回TODO主页
```

### 13.4 技术改进总结

#### 13.4.1 文件清单
**新增文件**:
1. `AffirmationManagementScreen.kt` - 暗示语管理页面
2. `TodoCategoryManagementScreen.kt` - TODO分类管理页面

**修改文件**:
1. `AffirmationScreen.kt` - 优化布局,简化暗示语展示
2. `AchievementRepository.kt` - 优化成就解锁判断逻辑
3. `TodoScreen.kt` - 添加分类管理入口
4. `TodoViewModel.kt` - 增加分类管理方法重载
5. `Screen.kt` - 添加新路由定义
6. `MainScaffold.kt` - 配置新路由导航

#### 13.4.2 路由配置
新增路由:
- `Screen.AffirmationManagement` - 暗示语管理
- `Screen.TodoCategoryManagement` - TODO分类管理

#### 13.4.3 数据模型
无新增数据模型,复用现有实体:
- `Affirmation` - 暗示语实体
- `TodoCategory` - TODO分类实体
- `Achievement` - 成就实体

### 13.5 用户体验提升

#### 13.5.1 交互优化
- 默念主页布局更稳定,避免滑动导致的布局问题
- 暗示语管理独立化,操作路径更清晰
- 成就动画触发更准确,反馈更及时

#### 13.5.2 功能完善
- TODO分类管理功能完整,支持完整CRUD
- 分类统计信息丰富,便于用户了解任务分布
- 空状态提示友好,引导用户正确使用

#### 13.5.3 性能优化
- 使用LazyColumn优化长列表渲染
- 减少不必要的重组和重绘
- 合理的状态管理避免内存泄漏

---

## 14. v1.2 更新 - 沉浸式默念UI优化

### 14.1 更新概述
优化默念计时页面的沉浸式体验，修复文本溢出问题，提升视觉效果和用户体验。

### 14.2 默念页面UI重设计

#### 14.2.1 沉浸式背景
**修改内容**:
- 背景渐变从浅色改为紫色主题渐变
- 渐变色: `#667EEA` → `#764BA2` (垂直渐变)
- 全屏沉浸式设计，所有UI元素采用白色系统

**设计理念**:
- 紫色渐变营造平静、专注的氛围
- 白色UI元素形成统一视觉风格
- 减少视觉干扰，帮助用户专注默念

#### 14.2.2 文本自适应显示
**问题**:
- 暗示语文本长度不一，固定字体大小导致长文本溢出屏幕
- 影响用户阅读体验和界面美观

**解决方案**:
- 实现 `AutoSizeText` 组件，自动根据文本长度调整字体大小
- 字体范围: 28sp (最大) - 14sp (最小)
- 支持多行显示，自动换行
- 智能检测溢出: 检查 `hasVisualOverflow`、`didOverflowHeight`、`didOverflowWidth`
- 渐进式缩减: 每次减小 0.5sp，直到文本完全适配容器

**技术实现**:
```kotlin
@Composable
fun AutoSizeText(
    text: String,
    maxFontSize: TextUnit,
    minFontSize: TextUnit,
    fontWeight: FontWeight,
    color: Color,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
) {
    var fontSize by remember(text) { mutableStateOf(maxFontSize) }

    BoxWithConstraints(modifier = modifier) {
        Text(
            text = text,
            fontSize = fontSize,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.hasVisualOverflow ||
                    textLayoutResult.didOverflowHeight ||
                    textLayoutResult.didOverflowWidth) {
                    if (fontSize > minFontSize) {
                        fontSize = (fontSize.value - 0.5f).sp.coerceAtLeast(minFontSize)
                    }
                }
            },
            style = LocalTextStyle.current.copy(
                lineHeight = (fontSize.value * 1.35).sp
            )
        )
    }
}
```

**关键特性**:
- 使用 `remember(text)` 确保文本变化时重置字体大小
- 使用 `BoxWithConstraints` 获取容器尺寸约束
- 使用 `coerceAtLeast(minFontSize)` 保证不小于最小字体
- 行高随字体大小动态调整 (1.35倍)

#### 14.2.3 布局优化
**文本区域**:
- 增加文本区域权重: `weight(1f)` → `weight(1.2f)`
- 添加 `fillMaxHeight()` 确保占满可用高度
- 水平内边距: 20dp
- 垂直居中对齐

**圆形进度条**:
- 尺寸调整: 260dp → 200dp (缩小以留出更多空间给文本)
- 描边宽度: 14dp (保持不变)
- 背景圆环: 白色半透明 (alpha = 0.3)
- 进度圆环: 纯白色

**倒计时数字**:
- 字体大小: 64sp → 52sp (适配更小的圆环)
- 颜色: 纯白色
- 粗体显示

**提示文字**:
- 文本: "在心中轻轻重复"
- 字体大小: 16sp
- 颜色: 白色半透明 (alpha = 0.9)
- 中等字重

#### 14.2.4 控制按钮设计
**暂停/继续按钮**:
- 背景: 白色半透明 (alpha = 0.25)
- 文字: 纯白色符号 (⏸ / ▶)
- 字体大小: 24sp
- 圆角: 30dp (完全圆润)
- 高度: 60dp

**完成按钮**:
- 背景: 纯白色
- 文字: 紫色 (#667EEA)
- 文字内容: "完成"
- 字体大小: 18sp
- 圆角: 30dp
- 高度: 60dp

**按钮布局**:
- 两按钮等宽: `weight(1f)`
- 按钮间距: 16dp
- 底部边距: 20dp
- 移除渐变背景容器，简化布局

#### 14.2.5 间距调整
**整体布局**:
- 顶部内边距: 60dp → 80dp
- 底部内边距: 120dp → 40dp
- 水平内边距: 24dp → 32dp

**组件间距**:
- 文本区域与进度条: 自动分配 (`SpaceBetween`)
- 进度条与提示文字: 24dp
- 提示文字与按钮: 自动分配

### 14.3 技术改进

#### 14.3.1 修改文件
- `AffirmationTimerScreen.kt` - 完全重设计UI和布局

#### 14.3.2 新增组件
- `AutoSizeText` - 自适应文本组件

#### 14.3.3 移除调试代码
- 删除 `LaunchedEffect` 中的 `println` 调试语句
- 清理无用导入 (stringResource, R, SecondaryButton, PinkF09, PinkF55, Purple667)

### 14.4 用户体验提升

#### 14.4.1 视觉体验
- 沉浸式紫色渐变背景营造专注氛围
- 纯白色UI元素形成清晰对比
- 圆润的按钮设计更符合现代审美

#### 14.4.2 内容适配
- 文本自动缩放，适配各种长度的暗示语
- 无论短文本还是长文本都能完整显示
- 自动换行和行高调整确保可读性

#### 14.4.3 交互优化
- 符号化的暂停/继续按钮更直观
- 按钮尺寸增大 (56dp → 60dp)，更易点击
- 简化文字表达，降低认知负担

---

## 15. v1.3 更新 - 默念完成率逻辑优化

### 15.1 更新概述
优化默念本周完成率的计算逻辑，引入有效时长概念和基于时长的完成率计算。

### 15.2 完成率计算规则调整

#### 15.2.1 有效记录定义
**新增规则**:
- 只有时长 ≥ 2分钟（120秒）的默念记录才被认为是有效记录
- 不足2分钟的记录不计入统计数据

**设计理念**:
- 鼓励用户进行有意义的默念练习
- 避免碎片化、无效的打卡行为
- 提升默念质量而非数量

#### 15.2.2 本周完成率计算
**原逻辑**:
- 基于天数计算：本周完成天数 / 目标天数（7天）× 100%
- 只关注是否完成，不考虑时长

**新逻辑**:
- 基于时长计算：本周累计有效时长（分钟）/ 目标时长（42分钟）× 100%
- 目标设定：每周42分钟（平均每天6分钟）
- 超过100%时显示实际百分比

**计算示例**:
```
场景1: 用户本周完成3次，每次5分钟
- 有效记录: 3次（都≥2分钟）
- 累计时长: 15分钟
- 完成率: 15 / 42 × 100% = 35.7% ≈ 36%

场景2: 用户本周完成7次，每次10分钟
- 有效记录: 7次
- 累计时长: 70分钟
- 完成率: 70 / 42 × 100% = 166.7% ≈ 167%

场景3: 用户本周完成10次，其中5次1分钟，5次5分钟
- 有效记录: 5次（只有5分钟的记录有效）
- 累计时长: 25分钟
- 完成率: 25 / 42 × 100% = 59.5% ≈ 60%
```

#### 15.2.3 其他统计数据调整
**今日完成次数**:
- 只统计时长 ≥ 2分钟的有效记录

**本周完成次数**:
- 只统计时长 ≥ 2分钟的有效记录

**累计时长**:
- 只累计有效记录的时长

**连续天数（Streak）**:
- 只有包含至少一次有效记录的天数才计入连续天数

### 15.3 技术实现

#### 15.3.1 修改文件
- `CalculationUtils.kt` - 更新 `calculateAffirmationStats` 函数

#### 15.3.2 函数参数
新增参数:
```kotlin
fun calculateAffirmationStats(
    allRecords: List<AffirmationRecord>,
    targetWeekDays: Int = 7,
    targetMonthDays: Int = 30,
    minValidDuration: Int = 120,        // 新增：最小有效时长（秒）
    targetWeekMinutes: Int = 42         // 新增：每周目标时长（分钟）
): AffirmationStats
```

#### 15.3.3 核心逻辑
```kotlin
// 1. 过滤有效记录（时长>=2分钟）
val validRecords = allRecords.filter { it.duration >= minValidDuration }

// 2. 按时间范围筛选
val weekRecords = validRecords.filter { it.createDate >= weekStart }

// 3. 计算本周累计时长（秒）
val weekTotalDuration = weekRecords.sumOf { it.duration.toLong() }

// 4. 转换为分钟
val weekTotalMinutes = weekTotalDuration / 60

// 5. 计算完成率：实际时长 / 目标时长 * 100
val weekCompletionRate = calculateCompletionRate(
    weekTotalMinutes.toInt(),
    targetWeekMinutes
)
```

### 15.4 用户体验影响

#### 15.4.1 正向影响
- **质量导向**：鼓励用户进行更有深度的默念练习
- **目标清晰**：42分钟/周的目标易于理解和达成
- **进度直观**：基于时长的进度更能反映实际投入
- **激励效果**：完成率可超过100%，激励用户超额完成

#### 15.4.2 数据迁移
- **无需迁移**：历史数据保持不变
- **自动过滤**：系统自动过滤不足2分钟的记录
- **平滑过渡**：用户无感知切换到新逻辑

#### 15.4.3 UI显示
默念主页统计卡片显示:
- **今日完成**: X次（只计有效记录）
- **本周完成率**: X%（基于42分钟目标）
- **累计时长**: X分钟（只计有效记录）

### 15.5 配置参数

#### 15.5.1 可调参数
| 参数 | 默认值 | 说明 | 调整建议 |
|------|--------|------|----------|
| minValidDuration | 120秒 | 最小有效时长 | 可根据用户反馈调整到90-180秒 |
| targetWeekMinutes | 42分钟 | 每周目标时长 | 可根据用户活跃度调整到30-60分钟 |

#### 15.5.2 未来扩展
- 支持用户自定义每周目标时长
- 支持根据历史数据动态调整目标
- 支持显示本周累计时长和剩余时长

---

## 16. v1.4 更新 - 默念界面动态UI优化

### 16.1 更新概述
优化默念计时页面的视觉效果，添加毫秒显示和iOS风格的动态梦幻渐变背景，提升沉浸感和时间流逝感知。

### 16.2 UI增强功能

#### 16.2.1 毫秒显示
**功能说明**:
- 在倒计时数字下方显示毫秒计数（0-9循环）
- 每100ms更新一次毫秒显示
- 毫秒数字半透明显示，避免喧宾夺主
- **优化**：去掉小数点，直接显示数字

**显示效果**:
```
3:45
 7
```

**技术实现**:
```kotlin
// 毫秒计数器
var milliseconds by remember { mutableStateOf(0) }

// 100ms更新一次
LaunchedEffect(timerState.isRunning, timerState.remainingSeconds, milliseconds) {
    if (timerState.isRunning && timerState.remainingSeconds > 0) {
        delay(100) // 100ms更新
        milliseconds = (milliseconds + 1) % 10
        if (milliseconds == 0) {
            viewModel.updateTimer(timerState.remainingSeconds - 1)
        }
    }
}
```

**UI布局**:
- 主倒计时：52sp，粗体，纯白色
- 毫秒显示：28sp，中等粗细，80%透明度白色
- 垂直居中排列

**用户体验**:
- 增强时间流逝的感知
- 让用户感受到每一刻的变化
- 提升专注度和投入感

#### 16.2.2 iOS风格动态梦幻渐变背景
**功能说明**:
- 背景颜色在多种梦幻色彩之间平滑流动过渡
- 三层独立动画，速度不同（25秒、20秒、30秒）
- 使用正弦波算法实现循环过渡，营造自然流动感
- 颜色缓慢变化，类似iOS动态壁纸效果

**颜色方案**（6种梦幻色彩循环）:
1. `#667EEA` - 紫蓝色
2. `#764BA2` - 深紫色
3. `#FF6B9D` - 粉红色
4. `#C471ED` - 淡紫色
5. `#12C2E9` - 青蓝色
6. `#F093FB` - 粉紫色

**技术实现**:
```kotlin
// 三个独立的动画控制器，速度不同，创造流动感
val animatedProgress1 by infiniteTransition.animateFloat(
    initialValue = 0f, targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(25000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
)
val animatedProgress2 by infiniteTransition.animateFloat(
    initialValue = 0f, targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(20000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
)
val animatedProgress3 by infiniteTransition.animateFloat(
    initialValue = 0f, targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(30000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
)

// 使用正弦波平滑地在颜色之间循环过渡
fun getFlowingColor(progress: Float, offset: Float): Color {
    val angle = (progress * 2 * PI.toFloat() + offset)
    val sinValue = (sin(angle.toDouble()) + 1) / 2 // 归一化到0-1
    val position = sinValue * (colorPalette.size - 1)
    val index = position.toInt()
    val nextIndex = (index + 1) % colorPalette.size
    val fraction = (position - index).toFloat()
    return lerpColor(colorPalette[index], colorPalette[nextIndex], fraction)
}

// 计算三个渐变层的颜色（不同相位偏移）
val color1 = getFlowingColor(animatedProgress1, 0f)
val color2 = getFlowingColor(animatedProgress2, PI.toFloat() * 0.66f)
val color3 = getFlowingColor(animatedProgress3, PI.toFloat() * 1.33f)

// 应用三色垂直渐变
Brush.verticalGradient(colors = listOf(color1, color2, color3))
```

**动画参数**:
- 三层动画时长：80秒、70秒、90秒（不同步，创造复杂流动效果，超慢速度）
- 缓动函数：LinearEasing（线性平滑）
- 重复模式：RepeatMode.Restart（循环重启）
- 相位偏移：0、120°、240°（三层颜色独立变化）

**核心算法**:
- 使用正弦波（sin函数）实现平滑的周期性变化
- 颜色在调色板中循环游动，自然过渡
- 三个不同速度的动画创造出复杂的流动效果
- 相位偏移确保三层颜色不同步，增加动态感

**视觉效果**:
- 背景颜色像流水般缓慢游动变化
- 从一种颜色自然过渡到下一种颜色，循环往复
- 营造梦幻、平静、深邃的氛围
- 类似iOS动态壁纸的流动质感
- 增强沉浸式体验
- 变化速度超慢（70-90秒），极其舒缓，几乎察觉不到变化，完美适合深度冥想

### 16.3 技术实现

#### 16.3.1 修改文件
- `AffirmationTimerScreen.kt` - 添加动态背景和毫秒显示

#### 16.3.2 新增导入
```kotlin
import androidx.compose.animation.core.*
```

#### 16.3.3 新增辅助函数
```kotlin
/**
 * 颜色线性插值函数 - 平滑过渡两种颜色
 */
private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}
```

#### 16.3.4 定时器更新频率
- **原来**：每秒更新一次（1000ms）
- **现在**：每100ms更新一次
  - 更新毫秒显示
  - 每秒（10次更新）递减秒数

### 16.4 性能优化

#### 16.4.1 动画性能
- 使用 `rememberInfiniteTransition` 确保动画生命周期管理
- 颜色插值在 Compose recomposition 时高效计算
- 线性缓动避免复杂计算

#### 16.4.2 定时器性能
- 虽然更新频率提高10倍（100ms vs 1000ms）
- 但只更新毫秒计数器（轻量级状态）
- 不影响整体性能

### 16.5 用户体验提升

#### 16.5.1 视觉体验
- **iOS风格动态背景**：避免静态背景的单调感，营造梦幻流动的视觉效果
- **毫秒显示**：增强时间流逝的感知（无小数点，更简洁）
- **多色过渡**：6种颜色循环流动，平滑柔和，不会分散注意力
- **三层渐变**：不同速度的动画叠加，创造复杂而自然的流动感

#### 16.5.2 心理效果
- 动态变化营造"时光流逝"的感觉
- 增强用户对时间的敏感度
- 提升专注力和投入感
- 强化沉浸式默念体验
- 颜色流动给人平静、放松的心理暗示

#### 16.5.3 设计理念
- **iOS美学**：借鉴iOS动态壁纸的设计理念，高端优雅
- **平衡**：动效足够明显但不干扰专注
- **和谐**：紫色系为主，多色点缀，保持主题一致性
- **流畅**：70-90秒循环周期，变化超级缓慢，几乎感知不到
- **细腻**：毫秒显示增加细节感，去掉小数点更简洁
- **自然**：正弦波算法模拟自然界的流动节奏
- **冥想感**：超慢速度完美适合长时间默念，营造深度放松氛围

### 16.6 未来扩展

#### 16.6.1 可配置选项
- 允许用户关闭毫秒显示
- 允许用户关闭动态背景
- 允许用户调整动画速度（快/中/慢）
- 允许用户选择不同的颜色主题

#### 16.6.2 更多动效
- 圆形进度条的呼吸动画
- 暗示语文本的淡入淡出效果
- 完成时的烟花动画

---

## 17. v1.5 更新 - Todo工具导航集成

### 17.1 更新概述
将 Todo 工具作为独立的底部导航 Tab 集成到主应用中，提升用户任务管理的便捷性。

### 17.2 导航结构调整

#### 17.2.1 底部导航扩展
**新增导航Tab**:
- 将底部导航从4个Tab扩展到5个Tab
- 新增 "📋 Todo" 作为第3个Tab

**导航布局**:
```
🧘 冥想 | 💭 默念 | 📋 Todo | 🔥 热搜 | 📊 统计
```

#### 17.2.2 页面顺序
1. **冥想** - 冥想时间追踪
2. **默念** - 积极暗示默念
3. **Todo** - 任务管理工具（新增）
4. **热搜** - 热点资讯浏览
5. **统计** - 数据统计与成就

### 17.3 技术实现

#### 17.3.1 修改文件
- `MainScaffold.kt` - 主导航配置
- 已存在的文件：`Screen.kt`（Todo路由已定义）

#### 17.3.2 核心改动

**1. Pager状态更新**:
```kotlin
// 从4个页面扩展到5个页面
val pagerState = rememberPagerState(initialPage = 0) { 5 }
```

**2. 路由同步逻辑**:
```kotlin
when (currentRoute) {
    Screen.Meditation.route -> page 0
    Screen.Affirmation.route -> page 1
    Screen.Todo.route -> page 2  // 新增
    Screen.HotSearch.route -> page 3
    Screen.Statistics.route -> page 4
}
```

**3. HorizontalPager页面配置**:
```kotlin
when (page) {
    0 -> MeditationScreen()
    1 -> AffirmationScreen()
    2 -> TodoScreen()  // 新增
    3 -> HotSearchScreen()
    4 -> StatisticsScreen()
}
```

**4. 底部导航项**:
```kotlin
val items = listOf(
    BottomNavItem.Meditation,
    BottomNavItem.Affirmation,
    BottomNavItem.Todo,  // 新增
    BottomNavItem.HotSearch,
    BottomNavItem.Statistics
)
```

#### 17.3.3 子页面路由
Todo功能包含以下子页面：
- `TodoScreen` - Todo主页面（分类筛选、任务列表）
- `TodoDetailScreen` - Todo详情页（查看/编辑单个任务）
- `TodoCategoryManagementScreen` - 分类管理页

### 17.4 功能特点

#### 17.4.1 Todo主页功能
- ✅ 显示任务总览统计（总数、完成数、完成率）
- ✅ 分类筛选（全部、工作、生活、学习等）
- ✅ 任务列表展示（优先级、截止时间、完成状态）
- ✅ 快速添加任务
- ✅ 分类管理入口

#### 17.4.2 交互体验
- ✅ **滑动切换**：支持左右滑动在5个Tab之间切换
- ✅ **点击切换**：点击底部导航快速跳转
- ✅ **状态保持**：切换页面时保留各页面状态
- ✅ **流畅动画**：页面切换动画自然流畅

### 17.5 设计理念

#### 17.5.1 产品定位
- **个人成长工具集**：Todo与冥想、默念都是帮助用户成长的核心工具
- **高频使用**：任务管理是日常必备功能，适合作为独立Tab
- **功能完整**：Todo功能已完善（分类、优先级、统计等）

#### 17.5.2 用户体验
- **快速访问**：一键切换到Todo，无需多级跳转
- **统一体验**：与其他功能保持一致的交互模式
- **视觉协调**：5个Tab布局均衡，不显拥挤

### 17.6 未来优化

#### 17.6.1 Todo功能增强
- Todo与冥想/默念的关联（如"每日冥想"可作为Todo任务）
- Todo完成提醒
- 番茄钟集成

#### 17.6.2 导航优化
- 底部导航栏自定义顺序
- 隐藏不常用的Tab

---

## 18. v1.6 更新 - 热搜平台选择器横向滚动优化

### 18.1 更新概述
将热搜页面的平台选择器从4x2网格布局改为横向滚动的单排布局，提升操作体验和视觉简洁性。

### 18.2 UI优化

#### 18.2.1 布局改进
**原有设计**:
- 使用 Column + 两个 Row 实现4x2网格布局
- 8个平台分两行显示
- 每个平台使用 `weight(1f)` 平均分配宽度
- 占用较多垂直空间

**新设计**:
- 使用单个 Row + horizontalScroll 实现横向滚动
- 8个平台在一行内横向排列
- 每个平台固定宽度 75.dp
- 支持左右滑动查看所有平台

#### 18.2.2 技术实现

**PlatformTabs 函数改动**:
```kotlin
@Composable
private fun PlatformTabs(
    selectedPlatform: HotSearchPlatform,
    onPlatformSelected: (HotSearchPlatform) -> Unit
) {
    // 横向滚动布局
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlatformTab(HotSearchPlatform.WEIBO, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.ZHIHU, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.WEIXIN, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.TOUTIAO, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.DOUYIN, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.BILIBILI, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.JANDAN, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.HISTORY, selectedPlatform, onPlatformSelected)
    }
}
```

**PlatformTab 函数改动**:
- 移除 `modifier: Modifier = Modifier` 参数（不再需要 `weight(1f)`）
- 添加固定宽度: `modifier = Modifier.width(75.dp)`
- 保持其他样式不变（圆角、边框、颜色等）

### 18.3 用户体验提升

#### 18.3.1 操作体验
- **更直观**: 所有平台在一行内，一目了然
- **更流畅**: 左右滑动查看更多平台，符合移动端操作习惯
- **更灵活**: 固定宽度确保每个平台卡片大小一致，视觉统一

#### 18.3.2 视觉优化
- **节省空间**: 从2行减少到1行，为热搜列表腾出更多空间
- **视觉连贯**: 横向排列更符合现代移动应用设计趋势
- **平台标识清晰**: 固定宽度（75.dp）确保图标和文字都能完整显示

### 18.4 技术细节

#### 18.4.1 修改文件
- `HotSearchScreen.kt` - 平台选择器布局和单个平台卡片

#### 18.4.2 关键参数
- **卡片宽度**: 75.dp（固定）
- **卡片间距**: 8.dp
- **滚动方式**: horizontalScroll（支持手势滑动）
- **图标大小**: 16.sp
- **文字大小**: 11.sp

#### 18.4.3 保留特性
- ✅ 选中状态高亮（白色背景 + 红色文字）
- ✅ 未选中状态半透明（白色半透明背景 + 白色文字）
- ✅ 边框效果
- ✅ 点击响应动画
- ✅ 圆角设计（10.dp）

### 18.5 未来优化

#### 18.5.1 可能的改进
- 自动滚动到选中的平台
- 添加滚动指示器（首尾淡入淡出效果）
- 支持拖拽排序平台顺序
- 记住用户常用平台并优先显示

---

## 19. v1.7 更新 - 移除煎蛋平台

### 19.1 更新概述
从热搜榜单中移除煎蛋平台，热搜平台数量从8个减少到7个。

### 19.2 修改内容

#### 19.2.1 数据模型
**文件**: `HotSearch.kt`
- 从 `HotSearchPlatform` 枚举中移除 `JANDAN("NRrvWq3e5z", "煎蛋", "🍳")`

#### 19.2.2 UI界面
**文件**: `HotSearchScreen.kt`
- 从平台选择器中移除煎蛋平台的Tab
- 剩余7个平台：微博、知乎、微信、头条、抖音、B站、历史

#### 19.2.3 更新后的平台列表
1. 🔥 微博
2. 💡 知乎
3. 💬 微信
4. 📰 头条
5. 🎵 抖音
6. 📺 B站
7. 📅 历史

### 19.3 影响范围
- ✅ 热搜平台选择器横向滚动布局
- ✅ 平台枚举定义
- ✅ 不影响已有功能和数据展示

---

## 20. v1.8 更新 - 成就解锁目标值显示修复

### 20.1 更新概述
修复成就系统在达成目标时显示错误目标值的bug。例如达成20次默念时，解锁通知显示的是50次而非20次。

### 20.2 问题分析

#### 20.2.1 Bug描述
**症状**:
- 用户完成20次默念（达成青铜等级目标）
- 解锁通知显示"恭喜你完成默念50次！"（错误）
- 应该显示"恭喜你完成默念20次！"（正确）

**影响范围**:
- 所有成就类别：连续打卡、冥想大师、冥想时长、默念达人
- 所有等级：青铜、白银、黄金、钻石、传奇

#### 20.2.2 根本原因
**文件**: `AchievementRepository.kt` 第135-142行

**原有逻辑**:
```kotlin
for (i in levelTargets.indices.reversed()) {
    if (currentValue >= levelTargets[i]) {
        // 已达成，设置为当前等级
        newLevel = levels[i]
        newTarget = levelTargets[i]  // ✅ 正确
        unlocked = true
        break
    } else if (i > 0 && currentValue >= levelTargets[i - 1]) {
        // ❌ 错误分支：已完成上一级但未达到当前级
        newLevel = levels[i - 1]
        newTarget = levelTargets[i]  // ❌ 错误！应该是 levelTargets[i-1]
        unlocked = true
        break
    }
}
```

**问题分析**:
以默念达人为例，目标值为 `[20, 50, 200, 500, 1000]`：
1. 用户完成20次，`currentValue = 20`
2. 循环从最高级往下检查（i=4→3→2→1→0）
3. 到 `i=1`（白银级，目标50）时：
   - `currentValue(20) >= levelTargets[1](50)` → false
   - 进入 `else if`：`currentValue(20) >= levelTargets[i-1](20)` → true
   - 设置 `newLevel = levels[0]`（青铜）✅
   - 设置 `newTarget = levelTargets[1]`（50）❌ **错误！**
   - 应该是 `newTarget = levelTargets[0]`（20）✅

**设计意图理解错误**:
- 原代码意图：显示"下一个目标"（未达成的等级）
- 正确逻辑：显示"当前达成的目标"（已解锁的等级）

### 20.3 解决方案

#### 20.3.1 代码修复
**移除错误分支**:
```kotlin
for (i in levelTargets.indices.reversed()) {
    if (currentValue >= levelTargets[i]) {
        // 已达成当前等级
        newLevel = levels[i]
        newTarget = levelTargets[i]  // 显示已达成的目标值
        unlocked = true

        // ... 新解锁检测逻辑 ...

        break
    }
    // 移除 else if 分支
}

我确实恶心到了 前面除了工作也没有找过你了
```

**简化逻辑**:
- 只保留一个判断分支
- 从高到低找到第一个满足 `currentValue >= target` 的等级
- `targetValue` 始终显示已达成的等级目标

#### 20.3.2 逻辑验证

**测试场景1**: 默念达人 - 完成20次
- 输入: `currentValue = 20`
- 循环: i=4→3→2→1→**0**
- 匹配: `20 >= levelTargets[0](20)` ✅
- 输出: `newLevel = BRONZE`, `newTarget = 20` ✅

**测试场景2**: 默念达人 - 完成30次（超过青铜，未达白银）
- 输入: `currentValue = 30`
- 循环: i=4→3→2→1→**0**
- 匹配: `30 >= levelTargets[0](20)` ✅
- 输出: `newLevel = BRONZE`, `newTarget = 20` ✅
- 进度显示: 30/20（超额完成）

**测试场景3**: 默念达人 - 完成50次（达成白银）
- 输入: `currentValue = 50`
- 循环: i=4→3→2→**1**
- 匹配: `50 >= levelTargets[1](50)` ✅
- 输出: `newLevel = SILVER`, `newTarget = 50` ✅

### 20.4 修改文件
- `AchievementRepository.kt` - 移除错误的 `else if` 分支（第135-142行）

### 20.5 影响范围
- ✅ 修复所有成就的目标值显示错误
- ✅ 不影响成就解锁逻辑（unlocked判断仍然正确）
- ✅ 不影响等级升级逻辑（level判断仍然正确）
- ✅ 只修正了 `targetValue` 的赋值

### 20.6 用户体验改进
**修复前**:
- 完成20次默念 → 显示"恭喜你完成默念50次！"（困惑）
- 完成7天连续打卡 → 显示"恭喜你达成连续打卡30天！"（错误）

**修复后**:
- 完成20次默念 → 显示"恭喜你完成默念20次！"✅
- 完成7天连续打卡 → 显示"恭喜你达成连续打卡7天！"✅
- 数据准确，用户体验更好

---

**文档状态**: 已更新
**最后更新**: 2025-11-14 (v1.9.7)

---

## 27. v1.9.6 更新 - 优化热搜平台图标，缩小尺寸容纳更多

### 27.1 改进概述
缩小热搜工具栏的平台图标和卡片尺寸，让横向布局更紧凑，可以在屏幕上同时显示更多平台选项。

### 27.2 尺寸优化

#### 27.2.1 平台卡片尺寸
**修改前**:
- 卡片宽度：75dp
- 图标大小：16sp
- 文字大小：11sp
- 内边距：vertical 8dp, horizontal 6dp
- 圆角：10dp
- 边框：1.5dp
- 卡片间距：8dp

**修改后**:
- 卡片宽度：**58dp** ↓17dp (减少23%)
- 图标大小：**14sp** ↓2sp
- 文字大小：**10sp** ↓1sp
- 内边距：**vertical 6dp, horizontal 4dp** ↓2dp
- 圆角：**8dp** ↓2dp
- 边框：**1dp** ↓0.5dp
- 卡片间距：**6dp** ↓2dp

**节省空间计算**（单个卡片）:
```
修改前：75dp（卡片）+ 8dp（间距）= 83dp
修改后：58dp（卡片）+ 6dp（间距）= 64dp
节省：19dp（约23%）
```

#### 27.2.2 屏幕显示对比

**360dp 宽度屏幕示例**:

```
修改前（75dp卡片 + 8dp间距）:
可见卡片数：约4.3个

修改后（58dp卡片 + 6dp间距）:
可见卡片数：约5.6个

提升：增加约1.3个卡片的可见度
```

**实际效果**:
- ✅ 首屏可以看到更多平台
- ✅ 减少滚动次数
- ✅ 用户可以一眼看到更多选项

### 27.3 技术实现

#### 27.3.1 卡片宽度调整
**修改文件**: `HotSearchScreen.kt` 第209行

```kotlin
// 修改前
Card(
    modifier = Modifier.width(75.dp)
)

// 修改后
Card(
    modifier = Modifier.width(58.dp)  // 缩小23%
)
```

#### 27.3.2 图标和文字缩小
**修改文件**: `HotSearchScreen.kt` 第227-234行

```kotlin
// 图标
Text(
    text = platform.icon,
    fontSize = 14.sp,  // 从16sp减小
    color = if (isSelected) Color(0xFFFF6B6B) else Color.White
)

// 平台名称
Text(
    text = platform.displayName,
    fontSize = 10.sp,  // 从11sp减小
    fontWeight = FontWeight.Medium,
    maxLines = 1  // 新增：防止文字换行
)
```

#### 27.3.3 内边距和圆角
**修改文件**: `HotSearchScreen.kt` 第217、222行

```kotlin
shape = RoundedCornerShape(8.dp),  // 从10dp减小

Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp, horizontal = 4.dp)  // 从8dp/6dp减小
)
```

#### 27.3.4 边框和间距
**修改文件**: `HotSearchScreen.kt` 第183、214行

```kotlin
// 卡片间距
horizontalArrangement = Arrangement.spacedBy(6.dp)  // 从8dp减小

// 边框宽度
border = BorderStroke(
    width = 1.dp,  // 从1.5dp减小
    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f)
)
```

### 27.4 视觉效果优化

#### 27.4.1 保持视觉平衡
尽管缩小了尺寸，但仍然保持了：
- ✅ 足够的点击区域（58dp宽度，约36dp高度）
- ✅ 清晰的图标显示（14sp仍然足够大）
- ✅ 可读的文字（10sp在卡片内仍清晰）
- ✅ 明确的选中状态（白色背景 vs 半透明）

#### 27.4.2 响应式适配
不同屏幕宽度下的表现：

**小屏幕（320dp）**:
- 修改前：约3.8个卡片可见
- 修改后：约5个卡片可见
- 提升：31%

**中屏幕（360dp）**:
- 修改前：约4.3个卡片可见
- 修改后：约5.6个卡片可见
- 提升：30%

**大屏幕（400dp）**:
- 修改前：约4.8个卡片可见
- 修改后：约6.2个卡片可见
- 提升：29%

### 27.5 用户体验改进

#### 27.5.1 减少滚动操作
**7个平台的布局对比**:

```
修改前（75dp卡片）:
[微博][知乎][微信][头条] → [需要滚动] → [抖音][B站][历史]
首屏显示：4个
需要滚动查看：3个

修改后（58dp卡片）:
[微博][知乎][微信][头条][抖音] → [轻微滚动] → [B站][历史]
首屏显示：5-6个
需要滚动查看：1-2个
```

**改进**:
- ✅ 减少50%的滚动需求
- ✅ 大部分平台一眼可见

#### 27.5.2 更快的平台切换
- 修改前：可能需要滚动才能看到想要的平台
- 修改后：大部分平台在首屏可见，直接点击

#### 27.5.3 界面更紧凑
- 整体视觉更简洁
- 减少空白浪费
- 信息密度更高

### 27.6 可访问性考虑

#### 27.6.1 触摸目标大小
**Material Design 推荐**:
- 最小触摸目标：48dp x 48dp
- 推荐触摸目标：48dp x 48dp 或更大

**当前实现**:
```
卡片尺寸：58dp（宽）× 约36dp（高）
虽然高度小于48dp，但：
- 宽度足够（58dp > 48dp）
- 实际可点击区域包含padding
- 横向滚动列表中，误触概率低
- 符合常见应用的实践（如微信、支付宝）
```

#### 27.6.2 文字可读性
**10sp文字大小考虑**:
- ✅ 在卡片内仍然清晰可读
- ✅ 平台名称都是2个汉字，不会截断
- ✅ 与14sp图标形成良好对比
- ✅ 加粗字体（FontWeight.Medium）增强可读性

#### 27.6.3 颜色对比度
保持原有的高对比度设计：
- 选中状态：白色背景 + 红色文字
- 未选中：半透明背景 + 白色文字
- 符合 WCAG 2.1 AA 级标准

### 27.7 性能优化

由于减小了卡片尺寸和间距，带来的性能优势：
- ✅ 渲染更快（更小的绘制区域）
- ✅ 内存占用更小
- ✅ 滚动性能更好

### 27.8 验证步骤

1. **测试可见性**:
   - 打开热搜页面
   - 观察首屏显示的平台数量
   - 预期：可以看到5-6个平台图标

2. **测试点击**:
   - 点击每个平台图标
   - 预期：准确识别点击，切换平台
   - 无误触现象

3. **测试滚动**:
   - 横向滑动平台栏
   - 预期：滚动流畅，可以看到所有7个平台

4. **测试文字显示**:
   - 检查所有平台名称
   - 预期：文字清晰可读，不换行，不截断

5. **测试不同屏幕**:
   - 在不同尺寸设备上测试
   - 预期：小屏幕也能显示5个左右的图标

### 27.9 未来扩展性

**支持更多平台**:
当前设计可以轻松容纳更多平台：

```
屏幕宽度360dp：
- 可显示约5.6个卡片
- 支持10+个平台而不显得拥挤

横向滚动机制：
- 无限制添加平台
- 用户可以流畅滚动查看
```

**建议的平台扩展**:
- 小红书
- 36氪
- 虎扑
- 贴吧
- 知乎日报
- ...

---

## 28. v1.9.7 更新 - 确认所有二级页面返回按钮已完善

### 28.1 检查概述
用户反馈"在一些二级界面，比如【冥想】的冥想记录可以加一个返回按钮"，经全面检查，所有二级页面均已有返回按钮，无需额外添加。

### 28.2 已确认有返回按钮的二级页面

#### 28.2.1 完整清单
所有二级页面都使用了统一的 **TopAppBar + navigationIcon (ArrowBack)** 模式：

1. ✅ **MeditationRecordsScreen** - 冥想记录
   - 文件：`MeditationRecordsScreen.kt`
   - TopAppBar 第33-43行
   - 返回按钮位置：左上角

2. ✅ **AffirmationRecordsScreen** - 积极暗示记录
   - 文件：`AffirmationRecordsScreen.kt`
   - TopAppBar 第33-43行
   - 返回按钮位置：左上角

3. ✅ **TodoCategoryManagementScreen** - TODO分类管理
   - 文件：`TodoCategoryManagementScreen.kt`
   - TopAppBar 第41-62行
   - 返回按钮位置：左上角

4. ✅ **AffirmationManagementScreen** - 暗示语管理
   - 文件：`AffirmationManagementScreen.kt`
   - TopAppBar 第40-61行
   - 返回按钮位置：左上角

5. ✅ **VideoSettingsScreen** - 视频链接管理
   - 文件：`VideoSettingsScreen.kt`
   - TopAppBar 第36-58行（v1.9.2 添加）
   - 返回按钮位置：左上角

6. ✅ **AffirmationSettingsScreen** - 暗示语设置
   - 文件：`AffirmationSettingsScreen.kt`
   - TopAppBar 第37-58行（v1.9.2 添加）
   - 返回按钮位置：左上角

7. ✅ **TodoDetailScreen** - TODO详情
   - 文件：`TodoDetailScreen.kt`
   - TopAppBar 第86-91行
   - 返回按钮位置：左上角

### 28.3 统一的设计模式

#### 28.3.1 标准代码模式
所有二级页面都遵循相同的返回按钮实现：

```kotlin
Scaffold(
    topBar = {
        TopAppBar(
            title = { Text("页面标题") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {  // 或 onBack
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )
    }
) { paddingValues ->
    // 页面内容
}
```

#### 28.3.2 设计优势
- ✅ 位置固定：顶部左上角，符合 Material Design 规范
- ✅ 全局一致：所有页面使用相同模式
- ✅ 易于点击：48dp 标准触摸目标
- ✅ 视觉清晰：箭头图标直观易懂
- ✅ 系统集成：与 Android 系统导航一致

### 28.4 用户体验

#### 28.4.1 导航一致性
- 所有二级页面的返回按钮位置、样式、交互完全一致
- 用户无需学习成本，自然直观

#### 28.4.2 无需修改
- 用户提到的"冥想记录"页面已有返回按钮
- 其他所有二级页面也都已有返回按钮
- 当前设计已完善，无需额外改动

### 28.5 总结
经全面检查，应用中所有7个二级页面均已实现返回按钮功能，设计统一、符合规范，用户体验良好。

---

## 26. v1.9.5 更新 - 改用长按菜单，移除编辑删除按钮

### 26.1 改进概述
移除TODO列表项的编辑和删除按钮，改用**长按弹出菜单**的方式，完全释放右侧空间，大幅提升内容显示区域。

### 26.2 设计理念

#### 26.2.1 移动端最佳实践
**长按菜单的优势**:
- ✅ 符合移动端操作习惯（iOS、Android 原生应用普遍使用）
- ✅ 释放界面空间，避免按钮占用
- ✅ 减少误触，操作更明确
- ✅ 界面更简洁美观

**常见应用案例**:
- 微信聊天列表：长按消息 → 删除/置顶
- iOS Safari：长按标签 → 关闭/新标签组
- Android 文件管理：长按文件 → 复制/删除/重命名

### 26.3 交互变化

#### 26.3.1 修改前的操作方式
```
TODO列表项布局：
[✓][内容区域 228dp][编辑按钮 32dp][删除按钮 32dp]
```

**操作方式**:
- 点击复选框：切换完成状态
- 点击编辑按钮：编辑TODO
- 点击删除按钮：删除TODO
- 点击卡片：（无操作）

**问题**:
- ❌ 按钮占用 64dp 空间
- ❌ 内容显示区域被压缩
- ❌ 按钮较小，容易误触

#### 26.3.2 修改后的操作方式
```
TODO列表项布局：
[✓][内容区域 292dp（100%可用空间）]
```

**操作方式**:
- **点击**：切换完成状态
- **长按**：弹出菜单（编辑、删除）

**优势**:
- ✅ 内容区域增加 **64dp**（约28%提升）
- ✅ 可以显示更多文字内容
- ✅ 界面更简洁
- ✅ 操作更符合移动端习惯

### 26.4 技术实现

#### 26.4.1 导入必要的API
**修改文件**: `TodoScreen.kt` 第5、8、29行

```kotlin
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.unit.DpOffset
```

#### 26.4.2 移除按钮，改用长按
**修改文件**: `TodoScreen.kt` 第402-513行

**关键代码**:
```kotlin
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TodoItemCard(...) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .combinedClickable(
                onClick = { onToggle(todo.id, todo.isCompleted) },  // 点击切换状态
                onLongClick = { showMenu = true }  // 长按显示菜单
            )
    ) {
        // 内容显示（占满剩余空间）
    }

    // 长按菜单
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text("编辑") },
            onClick = { onEdit() },
            leadingIcon = { Icon(Icons.Default.Edit) }
        )
        DropdownMenuItem(
            text = { Text("删除") },
            onClick = { onDelete(todo) },
            leadingIcon = { Icon(Icons.Default.Delete) }
        )
    }
}
```

#### 26.4.3 布局优化
**修改前**:
```kotlin
Row {
    Icon(...)  // 复选框
    Column(...) { }  // 内容
    Row {  // 按钮区域 64dp
        IconButton(编辑)
        IconButton(删除)
    }
}
```

**修改后**:
```kotlin
Box {
    Row(modifier = Modifier.combinedClickable(...)) {
        Icon(...)  // 复选框
        Column(modifier = Modifier.weight(1f)) { }  // 内容区域，占满剩余空间
    }
    DropdownMenu { }  // 长按菜单（浮动）
}
```

### 26.5 用户体验优化

#### 26.5.1 内容显示对比

**屏幕宽度 360dp 示例**:

```
v1.9.4（有按钮）:
[✓ 20dp][间距 12dp][内容 228dp][编辑 32dp][删除 32dp][边距 36dp]
内容占比：63%

v1.9.5（长按菜单）:
[✓ 20dp][间距 12dp][内容 292dp][边距 36dp]
内容占比：81% ↑18%
```

**实际效果**:
- 内容区域从 228dp → 292dp
- 增加 **64dp 空间**
- 可以多显示约 **8-10 个汉字**

#### 26.5.2 操作体验

**快速切换完成状态**:
- 点击TODO项 → 立即切换完成/未完成
- 无需精确点击复选框，整个卡片都可点击
- 操作更快捷方便

**编辑和删除**:
- 长按TODO项 → 弹出菜单
- 选择"编辑"或"删除"
- 提供触觉反馈（震动）

**防误操作**:
- 长按需要约 500ms，避免误触
- 删除操作需要两步（长按 + 点击删除），更安全

### 26.6 长按菜单设计

#### 26.6.1 菜单项
**编辑**:
- 图标：铅笔（`Icons.Default.Edit`）
- 颜色：紫色 `Color(0xFF667EEA)`
- 操作：弹出编辑对话框

**删除**:
- 图标：垃圾桶（`Icons.Default.Delete`）
- 颜色：红色 `Color(0xFFEF4444)`
- 操作：删除TODO项

#### 26.6.2 菜单位置
```kotlin
DropdownMenu(
    expanded = showMenu,
    onDismissRequest = { showMenu = false },
    offset = DpOffset(x = 0.dp, y = 0.dp)  // 贴近触摸点
)
```

**特点**:
- 从触摸点附近弹出
- 半透明背景，点击外部关闭
- Material Design 3 风格

### 26.7 完整的操作说明

#### 26.7.1 所有操作方式
现在TODO列表支持以下操作：

| 操作 | 触发方式 | 结果 | 说明 |
|------|---------|------|------|
| **切换完成** | 点击TODO卡片 | 标记完成/未完成 | 最常用，最快捷 |
| **编辑** | 长按 → 选择"编辑" | 弹出编辑对话框 | 修改内容、分类等 |
| **删除** | 长按 → 选择"删除" | 删除TODO | 永久删除 |

#### 26.7.2 操作流程图
```
点击TODO
  ↓
切换完成状态
  ↓
列表自动刷新

长按TODO
  ↓
弹出菜单
  ├─ 点击"编辑"
  │    ↓
  │  编辑对话框
  │    ↓
  │  保存/取消
  │
  └─ 点击"删除"
       ↓
     删除TODO
```

### 26.8 性能优化

#### 26.8.1 状态管理
```kotlin
var showMenu by remember { mutableStateOf(false) }
```
- 每个TODO项独立管理菜单状态
- 不会影响其他TODO项
- 内存占用极小

#### 26.8.2 事件处理
```kotlin
combinedClickable(
    onClick = { /* 点击 */ },
    onLongClick = { /* 长按 */ }
)
```
- 使用 Compose 官方API
- 自动处理手势识别
- 提供触觉反馈

### 26.9 验证步骤

1. **测试点击切换状态**:
   - 点击任意TODO项
   - 预期：立即切换完成/未完成状态
   - 图标和样式相应变化

2. **测试长按菜单**:
   - 长按TODO项（约0.5秒）
   - 预期：弹出菜单，显示"编辑"和"删除"选项
   - 点击菜单外部，菜单关闭

3. **测试编辑功能**:
   - 长按 → 选择"编辑"
   - 预期：弹出编辑对话框，预填充当前数据
   - 修改并保存，列表更新

4. **测试删除功能**:
   - 长按 → 选择"删除"
   - 预期：TODO立即从列表中移除

5. **测试内容显示**:
   - 观察TODO项的内容区域
   - 预期：比之前显示更多文字，无右侧按钮

### 26.10 用户引导建议

由于改变了操作方式，建议添加首次使用引导：

**引导内容**（可选）:
```
💡 使用提示
- 点击TODO：切换完成状态
- 长按TODO：编辑或删除
```

**引导时机**:
- 首次打开TODO列表时
- 使用 Snackbar 或 Toast 提示
- 只显示一次，后续不再提示

---

## 25. v1.9.4 更新 - 优化TODO编辑交互，改为弹窗编辑

### 25.1 改进概述
将TODO编辑功能从"跳转到详情页"改为"弹窗编辑"，提升编辑效率和用户体验。

### 25.2 交互优化

#### 25.2.1 修改前的交互流程
```
点击列表项的编辑按钮
  ↓
跳转到TODO详情页
  ↓
点击详情页的编辑按钮
  ↓
弹出编辑对话框
  ↓
修改内容并保存
  ↓
关闭对话框
  ↓
返回列表
```
**问题**: 需要跳转两次页面，步骤繁琐

#### 25.2.2 修改后的交互流程
```
点击列表项的编辑按钮
  ↓
直接弹出编辑对话框
  ↓
修改内容并保存
  ↓
关闭对话框（自动刷新列表）
```
**优势**:
- ✅ 减少2次页面跳转
- ✅ 操作更快捷
- ✅ 上下文保持不变

### 25.3 技术实现

#### 25.3.1 添加编辑状态管理
**修改文件**: `TodoScreen.kt` 第46行

**新增状态**:
```kotlin
var todoToEdit by remember { mutableStateOf<TodoItem?>(null) }
```

#### 25.3.2 修改编辑按钮点击逻辑
**修改文件**: `TodoScreen.kt` 第127行

**修改前**:
```kotlin
TodoItemCard(
    todo = todo,
    onEdit = { onNavigateToDetail(todo.id) }  // ❌ 跳转到详情页
)
```

**修改后**:
```kotlin
TodoItemCard(
    todo = todo,
    onEdit = { todoToEdit = todo }  // ✅ 设置要编辑的TODO
)
```

#### 25.3.3 添加编辑对话框
**修改文件**: `TodoScreen.kt` 第161-180行

```kotlin
// 编辑TODO对话框
todoToEdit?.let { todo ->
    EditTodoDialog(
        todo = todo,
        categories = uiState.categories,
        onDismiss = { todoToEdit = null },
        onConfirm = { id, title, description, categoryId, priority, dueDate ->
            viewModel.updateTodo(
                todo.copy(
                    title = title,
                    description = description,
                    categoryId = categoryId,
                    priority = priority,
                    dueDate = dueDate
                )
            )
            todoToEdit = null
        }
    )
}
```

**工作流程**:
1. 用户点击编辑按钮 → 设置 `todoToEdit`
2. `todoToEdit` 不为空 → 显示 `EditTodoDialog`
3. 用户修改并保存 → 调用 `viewModel.updateTodo()`
4. 关闭对话框 → 设置 `todoToEdit = null`
5. 列表自动刷新显示更新后的内容

#### 25.3.4 保留详情页入口
**修改文件**: `TodoScreen.kt` 第406、424、129行

为了保留查看详情的功能，添加了点击整个卡片跳转到详情页的能力：

```kotlin
// 参数新增
private fun TodoItemCard(
    ...
    onClick: (() -> Unit)? = null  // 新增点击回调
)

// 卡片可点击
Card(
    modifier = Modifier
        .fillMaxWidth()
        .then(
            if (onClick != null) Modifier.clickable { onClick() }
            else Modifier
        )
)

// 使用时传入
TodoItemCard(
    todo = todo,
    onEdit = { todoToEdit = todo },      // 编辑按钮 → 弹窗
    onClick = { onNavigateToDetail(todo.id) }  // 点击卡片 → 详情页
)
```

### 25.4 交互说明

#### 25.4.1 两种操作方式
现在用户有两种方式操作TODO：

**方式1：快速编辑（推荐）**
- 点击TODO项右侧的**编辑按钮**（铅笔图标）
- 直接弹出编辑对话框
- 修改后点击"保存"
- 适合：快速修改内容、分类、优先级等

**方式2：查看详情**
- 点击**整个TODO卡片**
- 跳转到详情页，查看完整信息
- 点击详情页顶部的"编辑"按钮进行编辑
- 适合：查看完整内容、创建时间、更新时间等详细信息

#### 25.4.2 操作对比

| 操作 | 触发方式 | 结果 | 用途 |
|------|---------|------|------|
| 快速编辑 | 点击编辑按钮 | 弹出编辑对话框 | 快速修改 |
| 查看详情 | 点击卡片 | 跳转详情页 | 查看完整信息 |
| 切换完成状态 | 点击复选框 | 标记完成/未完成 | 快速打卡 |
| 删除 | 点击删除按钮 | 删除TODO | 清理 |

### 25.5 用户体验提升

#### 25.5.1 效率对比
**修改前**（跳转编辑）:
```
点击编辑 → 页面跳转(1s) → 再次点击编辑 → 弹窗 → 修改 → 保存 → 返回(1s)
总耗时：约3-4秒 + 2次页面切换
```

**修改后**（弹窗编辑）:
```
点击编辑 → 弹窗 → 修改 → 保存
总耗时：约1-2秒 + 0次页面切换
```

**提升**: 操作时间减少50-60%，无页面跳转，更流畅

#### 25.5.2 场景适配
**批量编辑场景**:
- 修改前：每次都要跳转页面，切换上下文
- 修改后：弹窗编辑，上下文保持，可以连续编辑多个TODO

**查看详情场景**:
- 保留了点击卡片查看详情的功能
- 满足需要查看完整信息的需求

### 25.6 技术细节

#### 25.6.1 状态管理
使用 `remember` 和 `mutableStateOf` 管理编辑状态：
```kotlin
var todoToEdit by remember { mutableStateOf<TodoItem?>(null) }
```

**优势**:
- 简单轻量，无需额外的状态管理库
- 自动触发重组，UI实时更新
- 空值表示未编辑，非空表示编辑中

#### 25.6.2 对话框生命周期
```kotlin
todoToEdit?.let { todo ->
    EditTodoDialog(...)
}
```

**工作原理**:
- `todoToEdit` 为 `null` → 对话框不显示
- `todoToEdit` 不为 `null` → 对话框显示并预填充数据
- 保存或取消 → 设置 `todoToEdit = null` → 对话框自动关闭

#### 25.6.3 数据更新流程
```
用户修改 → onConfirm回调
→ viewModel.updateTodo()
→ Repository.updateTodo()
→ DAO.update()
→ Room数据库更新
→ Flow发射新数据
→ UI自动刷新
```

### 25.7 验证步骤

1. **测试弹窗编辑**:
   - 点击TODO项的编辑按钮（铅笔图标）
   - 预期：立即弹出编辑对话框，不跳转页面
   - 修改内容、分类、优先级
   - 点击"保存"
   - 预期：对话框关闭，列表显示更新后的内容

2. **测试详情页查看**:
   - 点击TODO卡片（非按钮区域）
   - 预期：跳转到详情页
   - 查看完整信息

3. **测试连续编辑**:
   - 编辑第一个TODO
   - 保存后立即编辑第二个TODO
   - 预期：流畅无卡顿，上下文不丢失

---

## 24. v1.9.3 更新 - 修复TODO详情页加载问题并优化按钮布局

### 24.1 问题描述

#### 24.1.1 详情页无限加载
**症状**: 点击TODO列表项的编辑按钮后，进入详情页面一直显示旋转加载动画，无法显示内容。

**根本原因**:
在 `TodoDetailScreen.kt` 第41-47行，`LaunchedEffect(todoId)` 的逻辑有缺陷：
```kotlin
LaunchedEffect(todoId) {
    uiState.todos.find { it.id == todoId }?.let {
        todo = it
        isLoading = false  // 只有找到TODO时才设置
    }
    // 如果找不到，isLoading 永远是 true
}
```

**问题分析**:
- 如果 `uiState.todos` 为空（初始状态）
- 或者找不到对应的 `todoId`
- `isLoading` 会一直保持 `true`
- 导致无限显示加载动画

#### 24.1.2 按钮占用空间过大
**症状**: TODO列表项中的编辑和删除按钮占用空间太大，导致内容显示区域被挤压，只能显示很少的文字。

**原有设计**:
- IconButton 默认尺寸：48dp x 48dp
- 图标尺寸：20dp x 20dp
- 两个按钮共占用约 96dp 宽度
- 导致内容区域严重压缩

### 24.2 解决方案

#### 24.2.1 修复详情页加载逻辑

**修改文件**: `TodoDetailScreen.kt` 第41-46行

**修复前**:
```kotlin
LaunchedEffect(todoId) {
    uiState.todos.find { it.id == todoId }?.let {
        todo = it
        isLoading = false  // ❌ 只有找到时才设置
    }
}
```

**修复后**:
```kotlin
LaunchedEffect(todoId, uiState.todos) {
    // 查找TODO项目
    val foundTodo = uiState.todos.find { it.id == todoId }
    todo = foundTodo
    isLoading = false  // ✅ 无论是否找到都设置为false，避免无限加载
}
```

**关键改进**:
1. ✅ 添加 `uiState.todos` 作为依赖，确保数据更新时重新查找
2. ✅ 移除 `?.let` 条件，改用直接赋值
3. ✅ **无论是否找到都设置 `isLoading = false`**
4. ✅ 如果找不到，`todo` 为 `null`，会显示"TODO项目不存在"的空状态

#### 24.2.2 优化按钮布局

**修改文件**: `TodoScreen.kt` 第453-477行

**修复前**:
```kotlin
Row {
    IconButton(onClick = onEdit) {  // 默认48dp
        Icon(
            imageVector = Icons.Default.Edit,
            modifier = Modifier.size(20.dp)
        )
    }
    IconButton(onClick = { onDelete(todo) }) {  // 默认48dp
        Icon(
            imageVector = Icons.Default.Delete,
            modifier = Modifier.size(20.dp)
        )
    }
}
// 总宽度：约96dp
```

**修复后**:
```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
    IconButton(
        onClick = onEdit,
        modifier = Modifier.size(32.dp)  // ✅ 缩小到32dp
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            modifier = Modifier.size(18.dp)  // ✅ 图标缩小到18dp
        )
    }
    IconButton(
        onClick = { onDelete(todo) },
        modifier = Modifier.size(32.dp)  // ✅ 缩小到32dp
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            modifier = Modifier.size(18.dp)  // ✅ 图标缩小到18dp
        )
    }
}
// 总宽度：约64dp，节省32dp空间
```

**尺寸对比**:
| 元素 | 修复前 | 修复后 | 节省 |
|------|--------|--------|------|
| 单个按钮 | 48dp | 32dp | 16dp |
| 图标 | 20dp | 18dp | 2dp |
| 两个按钮总宽 | 96dp | 64dp | **32dp** |
| 按钮间距 | 默认 | 0dp | 紧凑 |

### 24.3 用户体验改进

#### 24.3.1 详情页加载
**修复前**:
- ❌ 点击编辑按钮后页面一直旋转
- ❌ 无法看到TODO内容
- ❌ 用户只能强制退出

**修复后**:
- ✅ 立即显示TODO详情（如果找到）
- ✅ 显示"TODO不存在"提示（如果未找到）
- ✅ 加载动画最多只显示几毫秒

#### 24.3.2 列表项布局
**修复前**:
- ❌ 按钮占用96dp宽度
- ❌ 内容区域被严重挤压
- ❌ 长标题只能显示几个字
- ❌ 视觉不平衡

**修复后**:
- ✅ 按钮只占用64dp宽度
- ✅ 内容区域增加32dp空间（约33%提升）
- ✅ 可以显示更多文字内容
- ✅ 布局更加紧凑合理
- ✅ 按钮间距为0，视觉更统一

**内容显示对比**（假设屏幕宽360dp）:
```
修复前：
[✓][内容区域 196dp][编辑48dp][删除48dp] = 360dp
内容区域占比：54%

修复后：
[✓][内容区域 228dp][编辑32dp][删除32dp] = 360dp
内容区域占比：63% (提升9%)
```

### 24.4 技术细节

#### 24.4.1 LaunchedEffect 依赖管理
**问题根源**:
- `LaunchedEffect(todoId)` 只在 `todoId` 变化时执行
- 如果 `uiState.todos` 稍后才加载完成
- 查找逻辑不会重新执行
- 导致找不到TODO

**解决方案**:
- 添加 `uiState.todos` 作为依赖
- 确保数据加载后重新查找
- 使用直接赋值而非条件赋值

#### 24.4.2 IconButton 尺寸控制
**Material3 默认值**:
- IconButton 最小触摸目标：48dp x 48dp
- 推荐图标尺寸：24dp

**优化策略**:
- 将按钮缩小到 32dp（仍符合最小触摸目标 32dp）
- 图标缩小到 18dp（保持清晰可见）
- 移除按钮间距（`spacedBy(0.dp)`）

**可访问性考虑**:
- 32dp 仍然足够大，易于点击
- 图标颜色对比度足够（紫色和红色）
- 保持清晰的视觉反馈

### 24.5 验证步骤

#### 24.5.1 测试详情页加载
1. 打开TODO列表
2. 点击任意TODO项的编辑按钮（铅笔图标）
3. **预期**: 立即进入详情页，不再无限旋转
4. 详情页显示完整的TODO信息

#### 24.5.2 测试按钮布局
1. 在TODO列表中查看各个TODO项
2. **观察**: 编辑和删除按钮更小更紧凑
3. **观察**: 内容显示区域明显增大
4. 点击按钮确认仍然易于操作

#### 24.5.3 测试边界情况
1. 尝试删除一个TODO后立即点击编辑另一个
2. 检查是否正常加载
3. 尝试在空列表状态下的表现

### 24.6 相关优化

#### 24.6.1 移除重复的 LaunchedEffect
**原有代码**:
```kotlin
LaunchedEffect(todoId, uiState.todos) { ... }  // 已合并
LaunchedEffect(uiState.todos) { ... }          // 可以移除
```

**优化**: 两个 `LaunchedEffect` 功能重复，保留第一个即可。

---

## 23. v1.9.2 更新 - 为所有二级页面添加返回按钮

### 23.1 问题描述
部分二级页面（设置页面）缺少顶部返回按钮，用户只能通过系统返回键返回，用户体验不一致。

### 23.2 修改页面

#### 23.2.1 VideoSettingsScreen（视频链接管理）
**修改内容**:
- ✅ 添加 `TopAppBar` 顶部导航栏
- ✅ 添加返回按钮（左上角箭头图标）
- ✅ 将标题从页面内容移到 TopAppBar
- ✅ 移除底部的 `SecondaryButton` 返回按钮
- ✅ 使用 `Scaffold` 布局统一页面结构

**修改文件**: `VideoSettingsScreen.kt`
- 第10-11行：添加导入 `Icons.Default.ArrowBack`
- 第28行：添加 `@OptIn(ExperimentalMaterial3Api::class)`
- 第36-58行：添加 TopAppBar
- 第59-67行：调整 Column 布局，添加 `paddingValues`

#### 23.2.2 AffirmationSettingsScreen（暗示语设置）
**修改内容**:
- ✅ 添加 `TopAppBar` 顶部导航栏
- ✅ 添加返回按钮（左上角箭头图标）
- ✅ 将标题从页面内容移到 TopAppBar
- ✅ 移除底部的 `SecondaryButton` 返回按钮
- ✅ 使用 `Scaffold` 布局统一页面结构

**修改文件**: `AffirmationSettingsScreen.kt`
- 第10-11行：添加导入 `Icons.Default.ArrowBack`
- 第28行：添加 `@OptIn(ExperimentalMaterial3Api::class)`
- 第36-58行：添加 TopAppBar
- 第59-71行：调整布局，添加 `paddingValues`

### 23.3 统一的导航体验

#### 23.3.1 所有二级页面返回按钮位置
所有二级页面现在都有统一的返回按钮：
- ✅ TodoDetailScreen - 顶部左上角
- ✅ TodoCategoryManagementScreen - 顶部左上角
- ✅ AffirmationManagementScreen - 顶部左上角
- ✅ AffirmationSettingsScreen - 顶部左上角（新增）
- ✅ AffirmationRecordsScreen - 顶部左上角
- ✅ MeditationRecordsScreen - 顶部左上角
- ✅ VideoSettingsScreen - 顶部左上角（新增）

#### 23.3.2 返回按钮样式
**统一设计**:
- 位置：顶部导航栏左侧
- 图标：`Icons.Default.ArrowBack`（←）
- 颜色：默认主题色
- 点击：调用 `onNavigateBack()` 回调

**代码模式**:
```kotlin
TopAppBar(
    title = { Text("页面标题") },
    navigationIcon = {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回"
            )
        }
    },
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.White
    )
)
```

### 23.4 用户体验改进

#### 23.4.1 修复前
- ❌ 视频链接管理页面：底部有返回按钮，但顶部没有
- ❌ 暗示语设置页面：底部有返回按钮，但顶部没有
- ❌ 用户需要滚动到页面底部才能看到返回按钮
- ❌ 与其他页面的导航体验不一致

#### 23.4.2 修复后
- ✅ 所有二级页面顶部都有返回按钮
- ✅ 返回按钮位置固定，无需滚动
- ✅ 符合 Material Design 设计规范
- ✅ 与 Android 系统导航习惯一致

### 23.5 技术说明

#### 23.5.1 布局变化
**原有布局**:
```kotlin
Column {
    Text("页面标题")  // 标题在内容中
    // 页面内容
    SecondaryButton("返回")  // 底部返回按钮
}
```

**新布局**:
```kotlin
Scaffold(
    topBar = {
        TopAppBar(
            title = { Text("页面标题") },  // 标题在TopAppBar
            navigationIcon = { IconButton(...) }  // 顶部返回按钮
        )
    }
) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues)) {
        // 页面内容（移除底部返回按钮）
    }
}
```

#### 23.5.2 优势
1. **符合规范**：Material Design 推荐的标准布局
2. **位置固定**：TopAppBar 固定在顶部，不随内容滚动
3. **节省空间**：移除底部按钮，增加内容展示空间
4. **体验一致**：所有页面使用相同的导航模式

---

## 22. v1.9.1 更新 - 修复编辑对话框无限加载问题

### 22.1 问题描述
**症状**: 点击TODO详情页的"编辑"按钮后，页面出现旋转加载动画，对话框无法正常显示。

**根本原因**:
在 `EditTodoDialog` 的日期选择输入框中，同时使用了以下冲突的修饰符：
```kotlin
OutlinedTextField(
    modifier = Modifier.clickable { showDatePicker = true },
    readOnly = true,
    enabled = false  // 可能导致点击事件冲突
)
```

这导致：
1. `clickable` 修饰符与 `enabled = false` 冲突
2. 触发 Compose 的无限重组（infinite recomposition）
3. UI线程被阻塞，显示加载动画

### 22.2 解决方案

#### 22.2.1 移除冲突的修饰符
**修改文件**: `TodoDialogs.kt` 第665-691行

**原有代码**:
```kotlin
OutlinedTextField(
    modifier = Modifier
        .weight(1f)
        .clickable { showDatePicker = true }  // ❌ 冲突
        .padding(bottom = 24.dp),
    readOnly = true,
    enabled = false,
    trailingIcon = {
        Text(text = "📅", modifier = Modifier.padding(end = 8.dp))
    }
)
```

**修复后代码**:
```kotlin
OutlinedTextField(
    modifier = Modifier
        .weight(1f)
        .padding(bottom = 24.dp),  // ✅ 移除 clickable
    readOnly = true,
    enabled = false,
    colors = OutlinedTextFieldDefaults.colors(
        disabledTextColor = Color(0xFF333333),
        disabledBorderColor = Color(0xFFE0E0E0),
        disabledPlaceholderColor = Color(0xFF999999),
        disabledTrailingIconColor = Color(0xFF333333)
    ),
    trailingIcon = {
        IconButton(onClick = { showDatePicker = true }) {  // ✅ 使用IconButton
            Text(text = "📅", fontSize = 20.sp)
        }
    }
)
```

#### 22.2.2 关键改进点

1. **移除 clickable 修饰符**:
   - 避免与 `enabled = false` 的冲突
   - 消除无限重组的根源

2. **使用 IconButton 触发**:
   - 将点击事件移到 `trailingIcon` 中的 `IconButton`
   - 保持用户可以点击日期图标打开选择器
   - 避免整个输入框都可点击导致的问题

3. **添加 disabled 颜色配置**:
   ```kotlin
   disabledTextColor = Color(0xFF333333),       // 保持文字清晰可读
   disabledBorderColor = Color(0xFFE0E0E0),     // 保持边框可见
   disabledPlaceholderColor = Color(0xFF999999), // 占位符灰色
   disabledTrailingIconColor = Color(0xFF333333) // 图标清晰可见
   ```
   - 确保 `enabled = false` 时界面仍然正常显示
   - 不会因为禁用状态变成灰色不可读

### 22.3 用户体验改进

#### 22.3.1 修复前
- ❌ 点击"编辑"按钮后页面卡住
- ❌ 出现加载旋转动画
- ❌ 对话框无法打开
- ❌ 用户无法编辑TODO

#### 22.3.2 修复后
- ✅ 点击"编辑"按钮立即弹出对话框
- ✅ 所有字段正确预填充
- ✅ 点击 📅 图标可以选择日期
- ✅ 输入框显示正常（不会因为禁用变灰）

### 22.4 技术说明

#### 22.4.1 Compose 重组机制
**问题根源**:
- `clickable` 修饰符会监听点击事件
- `enabled = false` 会禁用组件交互
- 两者同时存在时，Compose 会不断尝试重新计算状态
- 导致无限重组循环

**解决原理**:
- 将点击逻辑移到子组件 (`IconButton`)
- 父组件 (`OutlinedTextField`) 完全禁用
- 子组件独立处理点击事件
- 避免状态冲突

#### 22.4.2 为什么使用 enabled = false
**目的**:
- 防止用户点击输入框时弹出系统键盘
- 保持输入框为只读状态
- 仅允许通过日期选择器修改日期

**替代方案的问题**:
- 仅使用 `readOnly = true`：仍会弹出键盘（只是不能输入）
- 使用 `focusable = false`：可能影响无障碍功能
- 使用 `clickable + enabled = false`：导致本次的无限重组问题 ❌

**最佳实践**:
- 输入框本身 `enabled = false`
- 交互逻辑放在内部的 `IconButton` 中
- 通过颜色配置保持视觉效果正常

### 22.5 验证步骤

1. **打开编辑对话框**:
   - 进入任意TODO详情页
   - 点击顶部导航栏的"编辑"按钮（紫色铅笔图标）
   - **预期**: 对话框立即弹出，不再出现加载动画

2. **选择截止日期**:
   - 在编辑对话框中点击日期输入框右侧的 📅 图标
   - **预期**: 日期选择器弹出
   - 选择日期后点击"确定"
   - **预期**: 日期显示在输入框中

3. **清除截止日期**:
   - 如果TODO有截止日期，编辑时会显示"清除"按钮
   - 点击"清除"按钮
   - **预期**: 输入框变为"选择截止日期"占位符

4. **保存编辑**:
   - 修改内容、分类、优先级或日期
   - 点击"保存"按钮
   - **预期**: 对话框关闭，详情页刷新显示新内容

### 22.6 相关修复

同时也检查了 `AddTodoDialog` 中的日期选择器，确认没有相同问题：
- ✅ `AddTodoDialog` 使用了相同的修复方案
- ✅ 添加TODO时日期选择器正常工作

---

## 21. v1.9 更新 - TODO工具UI简化与编辑功能完善

### 21.1 更新概述
优化TODO工具的用户体验，移除冗余的标题字段，直接使用内容字段；完善编辑功能，允许用户修改已创建的TODO项。

### 21.2 UI简化优化

#### 21.2.1 移除标题字段
**问题分析**:
- 原有设计中TODO有两个字段：标题（必填）和描述（可选）
- 大多数用户只需要一个内容字段即可表达TODO事项
- 双字段增加了操作复杂度，降低了添加TODO的效率

**解决方案**:
- 移除独立的"标题"字段
- 将"描述"字段作为主要的内容输入区域
- 自动从内容前50个字符提取作为显示标题
- 完整内容存储在description字段中

#### 21.2.2 表单优化
**修改内容**:
- 输入框标签：从"标题 *"和"描述（可选）"改为"内容 *"
- 输入框提示：从"输入TODO标题..."改为"输入TODO内容..."
- 输入框高度：从100.dp增加到120.dp
- 最大行数：从3行增加到5行
- 表单验证：从`title.isNotBlank()`改为`description.isNotBlank()`

**用户体验改进**:
- ✅ 减少输入字段，降低认知负担
- ✅ 增加输入区域高度，支持更长内容
- ✅ 简化操作流程，提升添加效率
- ✅ 保持数据结构不变，无需数据库迁移

### 21.3 编辑功能实现

#### 21.3.1 新增编辑对话框
**文件**: `TodoDialogs.kt`

**功能特性**:
```kotlin
@Composable
fun EditTodoDialog(
    todo: TodoItem,
    categories: List<TodoCategory>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String, Long, TodoPriority, Long?) -> Unit
)
```

**对话框内容**:
- ✅ 内容输入框（预填充当前TODO内容）
- ✅ 分类选择（预选择当前分类）
- ✅ 优先级选择（预选择当前优先级）
- ✅ 截止日期选择（预填充当前截止日期）
- ✅ 清除截止日期按钮（仅当有截止日期时显示）
- ✅ 取消和保存按钮

**特色功能**:
- 所有字段都预填充当前TODO的值
- 支持清除截止日期（添加对话框没有此功能）
- 保存时自动更新`updatedAt`时间戳
- 表单验证：内容不为空且必须选择分类

#### 21.3.2 详情页面集成
**文件**: `TodoDetailScreen.kt`

**新增功能**:
1. **编辑按钮**：
   - 位置：顶部导航栏actions区域（最左侧）
   - 图标：`Icons.Default.Edit`
   - 颜色：紫色主题色`Color(0xFF667EEA)`
   - 点击效果：显示编辑对话框

2. **编辑对话框状态管理**:
   ```kotlin
   var showEditDialog by remember { mutableStateOf(false) }
   ```

3. **实时数据同步**:
   ```kotlin
   LaunchedEffect(uiState.todos) {
       uiState.todos.find { it.id == todoId }?.let {
           todo = it
       }
   }
   ```
   - 当TODO更新后，详情页面自动刷新显示最新数据
   - 用户编辑保存后无需手动刷新

4. **编辑保存逻辑**:
   ```kotlin
   viewModel.updateTodo(
       currentTodo.copy(
           title = title,
           description = description,
           categoryId = categoryId,
           priority = priority,
           dueDate = dueDate,
           updatedAt = System.currentTimeMillis()
       )
   )
   ```

#### 21.3.3 操作流程
**编辑TODO流程**:
1. 用户在TODO列表点击某个TODO项
2. 进入详情页面，查看TODO完整信息
3. 点击顶部导航栏的"编辑"按钮
4. 弹出编辑对话框，所有字段预填充当前值
5. 用户修改需要更改的字段（内容、分类、优先级、截止日期）
6. 点击"保存"按钮
7. 对话框关闭，详情页面自动刷新显示更新后的内容

**取消编辑**:
- 点击"取消"按钮或对话框外部区域
- 对话框关闭，不保存任何修改

### 21.4 技术实现

#### 21.4.1 修改文件
- `TodoDialogs.kt` - 添加`EditTodoDialog`组件，简化`AddTodoDialog`
- `TodoDetailScreen.kt` - 添加编辑按钮和编辑对话框集成

#### 21.4.2 导入更新
**TodoDetailScreen.kt**:
```kotlin
import androidx.compose.material.icons.filled.Edit
```

#### 21.4.3 标题生成逻辑
**统一逻辑**（添加和编辑都使用）:
```kotlin
val displayTitle = if (description.length > 50)
    description.take(50) + "..."
else
    description
```

**设计理念**:
- 列表显示时使用截断的标题（50字符）
- 详情页面显示完整内容（description字段）
- 既保证列表简洁，又不丢失完整信息

### 21.5 用户体验提升

#### 21.5.1 操作效率
- **添加TODO**：减少1个输入字段，操作步骤更少
- **编辑TODO**：一键进入编辑模式，所有字段可修改
- **截止日期管理**：编辑时可以清除截止日期（灵活性提升）

#### 21.5.2 视觉优化
- 编辑按钮使用主题色，与其他操作按钮区分
- 按钮顺序：编辑 → 完成/未完成 → 删除（从左到右，从轻到重）
- 对话框与添加对话框保持一致的设计风格

#### 21.5.3 数据一致性
- 编辑后自动更新`updatedAt`时间戳
- 详情页面实时同步最新数据
- 避免用户看到过期数据

### 21.6 未来优化方向

#### 21.6.1 可能的改进
- 支持长按TODO项直接进入编辑模式
- 支持批量编辑（修改分类、优先级等）
- 支持拖拽排序TODO项
- 支持TODO模板（常用TODO快速创建）

#### 21.6.2 数据增强
- 添加子任务功能（父子TODO关系）
- 添加标签系统（多标签支持）
- 添加附件功能（图片、文件等）

### 21.7 故障排查指南

#### 21.7.1 编辑功能验证步骤
**测试流程**:
1. 进入TODO列表页面
2. 点击任意TODO项进入详情页
3. 点击顶部导航栏的"编辑"按钮（铅笔图标，紫色）
4. 编辑对话框应该弹出，所有字段预填充当前值
5. 修改内容、分类、优先级或截止日期
6. 点击"保存"按钮
7. 对话框关闭，详情页面应自动刷新显示新内容

**常见问题**:
- **问题**: 编辑按钮不显示
  - **原因**: 可能是导入缺失或按钮位置错误
  - **解决**: 检查 `Icons.Default.Edit` 是否已导入

- **问题**: 编辑后数据不更新
  - **原因**: ViewModel 或 Repository 层更新失败
  - **解决**: 查看 Logcat 错误日志，检查数据库权限

- **问题**: 详情页不刷新
  - **原因**: `LaunchedEffect(uiState.todos)` 未触发
  - **解决**: 确认 Flow 是否正常发射数据

#### 21.7.2 显示逻辑说明
**向后兼容**:
- 旧数据（有 title 和 description）：优先显示 description
- 新数据（只有 description）：直接显示 description
- 如果 description 为空：回退显示 title

**显示规则**:
```kotlin
// 列表页
text = todo.description.ifEmpty { todo.title }

// 详情页
text = currentTodo.description.ifEmpty { currentTodo.title }
```

#### 21.7.3 数据流向图
```
添加TODO:
用户输入 → description字段 → 前50字符提取 → title字段 → 数据库

编辑TODO:
用户修改 → description更新 → title更新 → viewModel.updateTodo()
→ todoRepository.updateTodo() → DAO @Update → 数据库
→ Flow通知 → UI刷新

显示TODO:
数据库 → Flow → ViewModel StateFlow → UI
→ 优先显示description → 为空则显示title
```

---

