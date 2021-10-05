# PVPINCore Installation

将插件置于服务端内 `plugins` 文件夹中，并开启服务器，稍后将会生成 `plugins/PVPINCore/js` 文件夹。

将已经编写好的 JavaScript 插件移动到该文件夹中，重启服务器即自动加载。

同时也可以通过指令加载、卸载插件。处于安全考虑，目前仅允许服务器后台通过指令控制文件内插件的加载、卸载。



Download the plugin jar and put it into the `plugins` folder, then start the server.

A `plugins/PVPINCore/js` folder will be automatically generated in a while.

Then you may add custom JavaScript plugins into the folder and restart your server.

All `.js` files in that folder are loaded on start, and you may also enable/disable plugins manually using commands.

Due to safety concerns, commands that control the lifecycle of local file based JavaScript plugins are currently only entitled to the console. NO player is allowed to execute them.

