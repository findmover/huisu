# 回溯速记便利贴版

这是 Windows 可执行程序版本，不需要浏览器。窗口默认小尺寸、置顶悬浮，可以隐藏到系统托盘，像便利贴一样快速写入“速记”。

## 使用

1. 运行 `dist/HuiSuQuickNote.exe`
2. 第一次打开“设置”，确认服务地址、Token、设备 ID
3. 如果手机端云同步设置了“快照加密密码”，这里也必须填同一个密码
4. 输入正文，点“保存同步”
5. 手机端在速记页下拉刷新，或在“云同步”页面手动下载，即可看到 Windows 新增的速记

窗口默认不透明，设置里可以调节透明度。桌面端支持添加图片，图片会随 `quick_note_images` 快照表同步。

## 构建 exe

在本目录运行：

```powershell
.\build-exe.ps1
```

构建产物：

```text
desktop/sticky-note/dist/HuiSuQuickNote.exe
```

配置会保存在 exe 同目录的 `huisu_sticky_config.json`，方便整体拷贝。
