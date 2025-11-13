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
**最后更新**: 2025-11-13 (v1.8)
