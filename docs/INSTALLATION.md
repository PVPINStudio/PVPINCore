# PVPINCore Installation

将插件置于服务端内 `plugins` 文件夹中，并开启服务器，稍后将会生成 `plugins/PVPINCore/js` 文件夹。

将已经编写好的 JavaScript 插件移动到该文件夹中，重启服务器即自动读取所有 `.js` 结尾的文件并加载。

同时也可以通过指令加载、卸载插件。处于安全考虑，目前仅允许服务器后台这么做。



Download the plugin jar and move it to the `plugins` folder in the server base directory, then start the server.

A `plugins/PVPINCore/js` folder will be automatically generated.

You may add custom JavaScript plugins to the folder and restart your server.

All `.js` files in that folder are loaded on start, and you may also enable/disable plugins manually using commands.

Due to safety concerns, commands that control the lifecycle of local file based JavaScript plugins are currently only entitled to the console. NO player is allowed to execute them.

