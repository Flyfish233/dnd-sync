# 勿扰同步 DNDSync

此应用程序的开发目的是实现我Pixel手机和Galaxy Watch 4之间的免打扰（DND）同步，因为只有与三星手机配对时才能使用此选项。

如果在手机和手表上安装了该应用程序，它可以根据用户的偏好实现单向同步或双向同步。我还添加了自动切换就寝模式的功能。使用场景：晚上我将手机设置为免打扰，希望手表自动启用就寝模式。
此功能通过辅助功能服务实现，因为我找不到如何以编程方式启用它（非常感谢任何提示）。

注意：为了使就寝模式切换工作，重要的是在手表上将就寝模式按钮放在快速设置的第一页，并且在第一行的中间按钮上！

这个项目的一部分受到[blundens](https://github.com/blunden/DoNotDisturbSync)的启发，请查看他们的GitHub了解更多信息。

_**在Pixel 3a XL上与Galaxy Watch 4（40mm）配对进行了测试**_

<a href="https://youtu.be/rHy6kCBNOzA
" target="_blank"><img src="http://img.youtube.com/vi/rHy6kCBNOzA/0.jpg"
alt="DNDSync演示" width="480" height="360" border="10" /></a>

视频链接：https://youtu.be/rHy6kCBNOzA

##           

## 设置

_目前该应用程序尚未在Play商店中。需要手动安装。需要使用ADB。_

* 从发布部分下载.apk文件（_dndsync_mobile.apk_和_dndsync_wear.apk_）

### 手机

<img src="/images/mobile.png" width="300">

* 在手机上安装 _dndsync_mobile.apk_：`adb install dndsync_mobile.apk`
* 打开应用程序，并通过点击菜单项 _免打扰权限_ 授予DND访问权限。
  这将打开权限屏幕。此权限是必需的，以便应用程序可以读取和写入DND状态。
  如果没有此权限，同步将无法工作。
* 使用开关 _同步勿扰模式_ 可以启用或禁用同步。如果启用，手机上的DND更改将导致手表上的DND更改。

### 手表

<p float="left">
  <img src="/images/wear_1.png" width="200" />
  <img src="/images/wear_2.png" width="200" /> 
  <img src="/images/wear_3.png" width="200" />
  <img src="/images/wear_4.png" width="200" />
</p>

由于手表操作系统缺少DND访问权限屏幕，设置手表要复杂一些。我找到了一种通过ADB启用权限的方法。

注意：这仅在我的Galaxy Watch 4上进行了测试，可能无法在其他设备上工作！

* 通过adb将手表连接到计算机（手表和计算机必须在同一网络中！）
    * 启用开发者选项：转到设置 -> 关于手表 -> 软件 -> 点击软件版本5次 -> 开启开发者模式（可以以同样的方式禁用它）
    * 启用 _ADB调试_ 和 _通过WIFI进行调试_（在设置 -> 开发者选项中）
    * 记下手表的IP地址和端口，类似于 `192.168.0.100:5555`
    * 使用 `adb connect 192.168.0.100:5555` 连接到手表（_**插入您的IP！**_）
* 安装应用程序：`adb install dndsync_wear.apk`
* 授予DND访问权限  
  `adb shell cmd notification allow_listener de.rhaeus.dndsync/de.rhaeus.dndsync.DNDNotificationService`  
  这允许应用程序监听DND更改并更改DND设置
* 滚动到权限部分，检查DND权限是否显示为“已授权”（您可能需要点击菜单项以更新）
* _**重要：完成后禁用ADB调试，因为它会消耗电池！**_
* 如果您想使用就寝模式功能，您必须为应用程序启用辅助功能服务。点击 _辅助功能_ 将在手表上打开设置。
  转到 _已安装的应用程序_ 并启用 _DNDSync_。应用程序将使用此功能在屏幕上模拟以下触摸事件：
  向下滑动以打开快速设置面板，点击第一行的中间图标（将就寝模式放在此处），最后关闭面板。
  您可以通过在应用程序中启用 _就寝模式_ 设置来启用此功能。
* 如果在应用程序中启用 _同步勿扰模式_ 设置，手表上的DND更改将导致手机上的DND更改
* 如果在应用程序中启用 _震动_ 设置，手表在接收到来自手机的DND同步请求时会震动

# 注意

所有功劳归原始开发者（rhaeus）所有，感谢他们开发了这个出色的应用程序。

# DNDSync

This App was developed to enable Do Not Disturb (DND) synchronization between my Pixel phone and the
Galaxy Watch 4
since this option was only available if paired with a Samsung phone.

If installed on phone and watch it enables either a 1-way sync or a 2-way sync, depending on the
preferences.
I also added the functionality to automatically toggle Bedtime Mode. Use case: At night I put my
phone into DND and I want my watch to automatically enable Bedtime Mode.
This functionality is realized via an Accessibility Service, since I couldn't find how to enable it
programmatically (any hints are highly appreciated).

NOTE: For Bedtime mode toggle to work it is important that on the watch the Bedtime Mode button is
on the first page of quick settings and the middle button in the first row!

Part of this project is inspired by [blundens](https://github.com/blunden/DoNotDisturbSync) work,
please check their GitHub if you want to know more.

_**Tested on Pixel 3a XL paired with a Galaxy Watch 4 (40mm)**_

<a href="https://youtu.be/rHy6kCBNOzA
" target="_blank"><img src="http://img.youtube.com/vi/rHy6kCBNOzA/0.jpg"
alt="DNDSync demo" width="480" height="360" border="10" /></a>

Video link: https://youtu.be/rHy6kCBNOzA

##           

## Setup

_For now the App is not in the Play Store. Manual installation is required. The use of ADB is
required._

* Download the .apk files from the release section (_dndsync_mobile.apk_ and _dndsync_wear.apk_)

### Phone

<img src="/images/mobile.png" width="300">

* Install _dndsync_mobile.apk_ on the phone `adb install dndsync_mobile.apk`
* Open the App and grant the permission for DND Access by clicking on the menu entry _DND
  Permission_.
  This will open the permission screen. This Permission is required so that the app can read and
  write DND state.
  Without this permission the sync will not work.
* With the switch _Sync DND state to watch_ you can enable and disable the sync. If enabled a DND
  change on the phone will lead to DND change on the watch.

### Watch

<p float="left">
  <img src="/images/wear_1.png" width="200" />
  <img src="/images/wear_2.png" width="200" /> 
  <img src="/images/wear_3.png" width="200" />
  <img src="/images/wear_4.png" width="200" />
</p>

Setting up the watch is a bit more tricky since the watch OS lacks the permission screen for DND
access. I found a way to enable the permission via ADB.

Note: This is only tested on my Galaxy Watch 4 and it might not work on other devices!

* Connect the watch to your computer via adb (watch and computer have to be in the same network!)
    * enable Developer Options: Go to Settings -> About watch -> Software -> tap the Software
      version 5 times -> developer mode is on (you can disable it in the same way)
    * enable _ADB debugging_ and _Debug over WIFI_ (in Settings -> Developer Options)
    * note the watch IP address and port, something like `192.168.0.100:5555`
    * connect to the watch with `adb connect 192.168.0.100:5555` (_**insert your value!**_)
* install the app `adb install dndsync_wear.apk`
* grant permission for DND access  
  `adb shell cmd notification allow_listener de.rhaeus.dndsync/de.rhaeus.dndsync.DNDNotificationService`  
  This allows the app to listen to DND changes and changing the DND setting
* scroll to the permission section and check if DND permission says _access granted_ (you might need
  to tap on the menu entry for it to update)
* _**IMPORTANT: Disable ADB debugging after you are done because it drains the battery!**_
* If you want to use the Bedtime mode feature you have to enable the Accessibility service for the
  app. Clicking on _Accessibility Service_ will open the setting on your watch.
  Go to _Installed Services_ and enable _DNDSync_. The App will use this to simulate the following
  touch events on the screen:
  swipe down to open Quick Settings Panel, click the middle icon of the first row (put Bedtime Mode
  here) and finally close the panel.
  You can enable this by enabling the _Bedtime Mode_ Setting in the App.
* If you enable the setting _Sync DND_ in the App a DND change on the watch will lead to a DND
  change on the phone
* If you enable the setting _Vibration_ in the App the watch will vibrate when it receives a DND
  sync request from the phone

# Note

All credit goes to the original developer (rhaeus) for the great app.
