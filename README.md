# PVPINCore Introduction

---

PVPINCore 是由 PVPIN Studio 团队成员开发的 Bukkit 插件。

感谢 `ThatRarityEG` 在开发时提供的无私帮助。

本插件提供了 JavaScript 代码的加载功能，并能基于本地文件加载编写的 JavaScript 插件。同时允许玩家在游戏内直接执行一段 JavaScript 代码。（此功能慎用！慎用！慎用！）

本插件主要使用 Java 编写而成。同时提供一系列 API 以供 JavaScript 调用，包括对于事件监听、指令、任务调度器、持久化等功能的简单封装。禁止了 JavaScript 所有 I/O 操作。包括 NodeJS 和 通过 `java.io` 等多种读写文件的方法都无效。

本插件使用 GraalJS v21.2.0 加载 JavaScript 代码。



PVPINCore is a Bukkit Plugin developed by `William_Shi`, `Rain_Effect`, `MiaoWoo` and `Eustia_Saint` from PVPIN Studio.

A special thanks goes to `ThatRarityEG` in acknowledgement of her kind help. 

This plugin can be used to load custom JavaScript plugins in local `.js` files. Players may also execute their own JavaScript code using commands in game. CAUTION! You NEVER know what malicious function calls may a player write!  Bear the consequences YOURSELF!

Though written in Java, this plugin provides a series of APIs designed for JavaScript to use. These include simple wrappers for event listeners, command executors, task schedulers and persistence holders. All I/O access is denied, including multiple ways to read or write files such as NodeJS I/Os or `java.io` methods.

This plugin uses GraalJS 21.2.0 to load JavaScript code.



## Installation

向服务器内安装 PVPINCore 插件，详见 [INSTALLATION.md](docs/INSTALLATION.md) 。



If you want to install our PVPINCore plugin on your server, you may go to [INSTALLATION.md](docs/INSTALLATION.md) for details.



## Usage

---

PVPINCore 插件提供的全部指令的使用方法，详见 [USAGE.md](docs/USAGE.md) 。



If you want to know the usage of all the commands provided by our PVPINCore plugin, you may go to [USAGE.md](docs/USAGE.md) for details.



## APIDOCS

基于 PVPINCore 所提供的 API 开发 JavaScript 插件或 Java 插件的指导，详见 [APIDOCS.md](docs/APIDOCS.md) 。



If you want to know how to use the APIs provided by our PVPINCore plugin to develop a JavaScript or Java plugin that depends on PVPINCore by yourself, you may go to [APIDOCS.md](docs/APIDOCS.md) for details.

