## PVPINCore Introduction

---

PVPINCore 是由 PVPINStudio 团队成员 `William_Shi`、 `Rain_Effect`、`MiaoWoo`、`Eustia_Saint` 共同开发的 Bukkit 插件。

本插件提供了 JavaScript 代码的加载功能，并能加载用户编写的 JavaScript 插件。

本插件提供了一系列 API 以供 JavaScript 调用，包括对于事件监听、指令、任务调度器、持久化等功能的简单封装。

本插件主要使用 Java 编写而成，但同时提供了一系列供 JavaScript 调用的方法。

本插件使用 GraalJS v21.2.0 并以此加载 JavaScript 代码。

PVPINCore is a Bukkit Plugin developed by `William_Shi`, `Rain_Effect`, `MiaoWoo` and `Eustia_Saint` from PVPINStudio.

This plugin can be used to load custom JavaScript plugins written by users.

Though written in Java, this plugin provides a series of APIs easy for JavaScript to use.

This plugin uses GraalJS 21.2.0 to load JavaScript code.

## Usage

---

将插件置于服务端内 `plugins` 文件夹中，并开启服务器，稍后将会生成 `plugins/PVPINCore/js` 文件夹。

将已经编写好的 JavaScript 插件移动到该文件夹中，重启服务器即自动加载。

同时也可以通过指令加载、卸载插件。处于安全考虑，目前仅允许服务器后台通过指令控制插件加载、卸载。

`/pvpincore js reload` -- 重载所有 JavaScript 插件

`/pvpincore js enable <文件名>` -- 加载指定 `.js` 文件

`/pvpincore js disable <插件名>` -- 卸载指定插件

指令均提供补全功能，会自动检测 `js` 文件夹并提供文件名补全，但请勿采用文件软连接等方式向文件夹内添加文件，这样的文件无法被正常读取。

Download the plugin jar and put it into the `plugins` folder, and start the server.

`plugins/PVPINCore/js` folder will be automatically generated in a while.

Then you may add custom JavaScript plugins into the folder and restart your server.

All `.js` files in that folder are loaded on start, and you may also enable/disable plugins manually using commands.

`/pvpincore js reload` -- Reload all JavaScript plugins

`/pvpincore js enable <File Name>` -- load a `.js` file by name

`/pvpincore js disable <Plugin Name>` -- unload a JavaScript plugin by name

Due to safety concerns, all `pvpincore js` commands are currently only entitled to the console. NO player is allowed to execute them.

All the commands provide tab completes, so you won't need to check the file name / plugin name by yourself.
