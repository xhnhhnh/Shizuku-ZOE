# Shizuku-ZOE

[English](#english) | [中文](#中文)

---

<a name="english"></a>
## English

A fork of [Shizuku](https://github.com/RikkaApps/Shizuku) with enhanced UI and modern design.

### What's New in ZOE

- **Enhanced UI Design**: Modern Material You design with improved card styles
- **Animation Effects**: Smooth animations when cards appear
- **Customizable Themes**: Multiple card styles (Default, Elevated, Outlined, Filled)
- **Improved Color System**: Extended color palette with better dark mode support
- **UI Settings**: New settings for animations, card elevation, and card style

### Supported Languages

This fork supports the following languages only:
- **English** (default)
- **简体中文** (Simplified Chinese)
- **繁體中文** (Traditional Chinese)

### Original Project

This project is a fork of Shizuku. For the original project, please visit:
- Original Repository: https://github.com/RikkaApps/Shizuku
- Original Author: RikkaApps

### Background

When developing apps that requires root, the most common method is to run some commands in the su shell. For example, there is an app that uses the `pm enable/disable` command to enable/disable components.

This method has very big disadvantages:

1. **Extremely slow** (Multiple process creation)
2. Needs to process texts (**Super unreliable**)
3. The possibility is limited to available commands
4. Even if ADB has sufficient permissions, the app requires root privileges to run

Shizuku uses a completely different way. See detailed description below.

### User guide & Download

<https://shizuku.rikka.app/>

### How does Shizuku work?

First, we need to talk about how app use system APIs. For example, if the app wants to get installed apps, we all know we should use `PackageManager#getInstalledPackages()`. This is actually an interprocess communication (IPC) process of the app process and system server process, just the Android framework did the inner works for us.

Android uses `binder` to do this type of IPC. `Binder` allows the server-side to learn the uid and pid of the client-side, so that the system server can check if the app has the permission to do the operation.

Usually, if there is a "manager" (e.g., `PackageManager`) for apps to use, there should be a "service" (e.g., `PackageManagerService`) in the system server process. We can simply think if the app holds the `binder` of the "service", it can communicate with the "service". The app process will receive binders of system services on start.

Shizuku guides users to run a process, Shizuku server, with root or ADB first. When the app starts, the `binder` to Shizuku server will also be sent to the app.

The most important feature Shizuku provides is something like be a middle man to receive requests from the app, sent them to the system server, and send back the results. You can see the `transactRemote` method in `rikka.shizuku.server.ShizukuService` class, and `moe.shizuku.api.ShizukuBinderWrapper` class for the detail.

So, we reached our goal, to use system APIs with higher permission. And to the app, it is almost identical to the use of system APIs directly.

### Developer guide

#### API & sample

https://github.com/RikkaApps/Shizuku-API

#### Migrating from pre-v11

> Existing applications still works, of course.

https://github.com/RikkaApps/Shizuku-API#migration-guide-for-existing-applications-use-shizuku-pre-v11

#### Attention

1. ADB permissions are limited

   ADB has limited permissions and different on various system versions. You can see permissions granted to ADB [here](https://github.com/aosp-mirror/platform_frameworks_base/blob/master/packages/Shell/AndroidManifest.xml).

   Before calling the API, you can use `ShizukuService#getUid` to check if Shizuku is running user ADB, or use `ShizukuService#checkPermission` to check if the server has sufficient permissions.

2. Hidden API limitation from Android 9

   As of Android 9, the usage of the hidden APIs is limited for normal apps. Please use other methods (such as <https://github.com/LSPosed/AndroidHiddenApiBypass>).

3. Android 8.0 & ADB

   At present, the way Shizuku service gets the app process is to combine `IActivityManager#registerProcessObserver` and `IActivityManager#registerUidObserver` (26+) to ensure that the app process will be sent when the app starts. However, on API 26, ADB lacks permissions to use `registerUidObserver`, so if you need to use Shizuku in a process that might not be started by an Activity, it is recommended to trigger the send binder by starting a transparent activity.

4. Direct use of `transactRemote` requires attention

   * The API may be different under different Android versions, please be sure to check it carefully. Also, the `android.app.IActivityManager` has the aidl form in API 26 and later, and `android.app.IActivityManager$Stub` exists only on API 26.

   * `SystemServiceHelper.getTransactionCode` may not get the correct transaction code, such as `android.content.pm.IPackageManager$Stub.TRANSACTION_getInstalledPackages` does not exist on API 25 and there is `android.content.pm.IPackageManager$Stub.TRANSACTION_getInstalledPackages_47` (this situation has been dealt with, but it is not excluded that there may be other circumstances). This problem is not encountered with the `ShizukuBinderWrapper` method.

### Developing Shizuku itself

#### Build

- Clone with `git clone --recurse-submodules`
- Run gradle task `:manager:assembleDebug` or `:manager:assembleRelease`

The `:manager:assembleDebug` task generates a debuggable server. You can attach a debugger to `shizuku_server` to debug the server. Be aware that, in Android Studio, "Run/Debug configurations" - "Always install with package manager" should be checked, so that the server will use the latest code.

---

<a name="中文"></a>
## 中文

[Shizuku](https://github.com/RikkaApps/Shizuku) 的分支，具有增强的 UI 和现代化设计。

### ZOE 版本新增功能

- **增强的 UI 设计**：现代化的 Material You 设计，改进的卡片样式
- **动画效果**：卡片出现时的平滑动画
- **可自定义主题**：多种卡片样式（默认、悬浮、描边、填充）
- **改进的颜色系统**：扩展的颜色调色板，更好的暗色模式支持
- **UI 设置**：新增动画、卡片阴影和卡片样式的设置选项

### 支持的语言

此分支仅支持以下语言：
- **English** (默认)
- **简体中文**
- **繁體中文**

### 原始项目

本项目是 Shizuku 的分支。原始项目请访问：
- 原始仓库：https://github.com/RikkaApps/Shizuku
- 原作者：RikkaApps

### 背景

在开发需要 root 的应用时，最常见的方法是在 su shell 中运行一些命令。例如，有一个应用使用 `pm enable/disable` 命令来启用/禁用组件。

这种方法有很大的缺点：

1. **极慢**（多次进程创建）
2. 需要处理文本（**非常不可靠**）
3. 可能性仅限于可用的命令
4. 即使 ADB 有足够的权限，应用也需要 root 权限才能运行

Shizuku 使用完全不同的方式。请参阅下面的详细说明。

### 用户指南与下载

<https://shizuku.rikka.app/>

### Shizuku 是如何工作的？

首先，我们需要谈谈应用如何使用系统 API。例如，如果应用想要获取已安装的应用，我们都知道应该使用 `PackageManager#getInstalledPackages()`。这实际上是应用进程和系统服务器进程之间的进程间通信（IPC）过程，只是 Android 框架为我们做了内部工作。

Android 使用 `binder` 来进行这种类型的 IPC。`Binder` 允许服务器端了解客户端的 uid 和 pid，以便系统服务器可以检查应用是否有权限执行该操作。

通常，如果有一个"管理器"（例如 `PackageManager`）供应用使用，那么系统服务器进程中应该有一个"服务"（例如 `PackageManagerService`）。我们可以简单地认为，如果应用持有"服务"的 `binder`，它就可以与"服务"通信。应用进程在启动时会接收系统服务的 binder。

Shizuku 引导用户首先使用 root 或 ADB 运行一个进程，即 Shizuku 服务器。当应用启动时，到 Shizuku 服务器的 `binder` 也会被发送给应用。

Shizuku 提供的最重要功能是作为一个中间人，接收来自应用的请求，将其发送到系统服务器，并将结果返回。您可以在 `rikka.shizuku.server.ShizukuService` 类中查看 `transactRemote` 方法，在 `moe.shizuku.api.ShizukuBinderWrapper` 类中查看详细信息。

因此，我们达到了目标，即以更高的权限使用系统 API。对于应用来说，这几乎与直接使用系统 API 相同。

### 开发者指南

#### API 与示例

https://github.com/RikkaApps/Shizuku-API

#### 从 pre-v11 迁移

> 现有应用程序当然仍然有效。

https://github.com/RikkaApps/Shizuku-API#migration-guide-for-existing-applications-use-shizuku-pre-v11

#### 注意事项

1. ADB 权限有限

   ADB 的权限有限，且在不同系统版本上有所不同。您可以在[这里](https://github.com/aosp-mirror/platform_frameworks_base/blob/master/packages/Shell/AndroidManifest.xml)查看授予 ADB 的权限。

   在调用 API 之前，您可以使用 `ShizukuService#getUid` 检查 Shizuku 是否以用户 ADB 运行，或使用 `ShizukuService#checkPermission` 检查服务器是否有足够的权限。

2. Android 9 起的隐藏 API 限制

   从 Android 9 开始，普通应用对隐藏 API 的使用受到限制。请使用其他方法（如 <https://github.com/LSPosed/AndroidHiddenApiBypass>）。

3. Android 8.0 与 ADB

   目前，Shizuku 服务获取应用进程的方式是结合 `IActivityManager#registerProcessObserver` 和 `IActivityManager#registerUidObserver`（26+）来确保应用启动时应用进程会被发送。但是，在 API 26 上，ADB 缺少使用 `registerUidObserver` 的权限，因此如果您需要在一个可能不是由 Activity 启动的进程中使用 Shizuku，建议通过启动一个透明 Activity 来触发发送 binder。

4. 直接使用 `transactRemote` 需要注意

   * API 在不同的 Android 版本下可能不同，请务必仔细检查。此外，`android.app.IActivityManager` 在 API 26 及更高版本中有 aidl 形式，而 `android.app.IActivityManager$Stub` 仅存在于 API 26。

   * `SystemServiceHelper.getTransactionCode` 可能无法获取正确的事务代码，例如 `android.content.pm.IPackageManager$Stub.TRANSACTION_getInstalledPackages` 在 API 25 上不存在，而是 `android.content.pm.IPackageManager$Stub.TRANSACTION_getInstalledPackages_47`（这种情况已经处理，但不排除可能存在其他情况）。使用 `ShizukuBinderWrapper` 方法不会遇到此问题。

### 开发 Shizuku 本身

#### 构建

- 使用 `git clone --recurse-submodules` 克隆
- 运行 gradle 任务 `:manager:assembleDebug` 或 `:manager:assembleRelease`

`:manager:assembleDebug` 任务会生成一个可调试的服务器。您可以将调试器附加到 `shizuku_server` 来调试服务器。请注意，在 Android Studio 中，应勾选 "Run/Debug configurations" - "Always install with package manager"，以便服务器使用最新代码。

---

## License

All code files in this project are licensed under Apache 2.0

Under Apache 2.0 section 6, specifically:

* You are **FORBIDDEN** to use `manager/src/main/res/mipmap*/ic_launcher*.png` image files, unless for displaying Shizuku itself.

* You are **FORBIDDEN** to use `Shizuku` as app name or use `moe.shizuku.privileged.api` as application id or declare `moe.shizuku.manager.permission.*` permission.

### Attribution

This project is a fork of [Shizuku](https://github.com/RikkaApps/Shizuku) by [RikkaApps](https://github.com/RikkaApps), licensed under the Apache License 2.0.
