# PVPINCore Installation

将插件置于服务端内 `plugins` 文件夹中，并开启服务器，将会首先从 Maven 中央仓库下载依赖，这一过程耗时取决于网络环境。稍后将会生成 `plugins/PVPINCore/js` 文件夹。

将已经编写好的 JavaScript 插件移动到该文件夹中，重启服务器即自动读取所有 `.js` 结尾的文件并加载。

同时也可以通过指令加载、卸载插件。处于安全考虑，目前仅允许服务器后台这么做。



Download the plugin jar and move it to the `plugins` folder in the server base directory, then start the server. The plugin will download dependencies from Maven Central, which may be time-consuming.

A `plugins/PVPINCore/js` folder will be automatically generated.

You may add custom JavaScript plugins to the folder and restart your server.

All `.js` files in that folder are loaded on start, and you may also enable/disable plugins manually using commands.

Due to safety concerns, commands that control the lifecycle of local file based JavaScript plugins are currently only entitled to the console. NO player is allowed to execute them.



## Configuration

配置文件位于 `PVPINCore/config` 文件夹中。`TrustedPlayers.json` 记录了所有被信任的玩家的 UUID。注意！这些被信任的玩家都可以在服务器内运行 JavaScript 语句，甚至可以添加受信任玩家！文件内默认添加了两个 UUID，它们都仅由 “a” 或 “b” 组成，是示例，几乎不可能存在这样 UUID 的玩家。

将玩家 UUID 添加到这一文件中即意味着这一玩家可以使用 `/pvpincore eval` 指令。请格外注意服务器安全！



Configuration files are in the `PVPINCore/config` folder。`TrustedPlayers.json` records UUIDs of all trusted players. The initial file includes two demo UUIDs which are all composed of a and b, impossible when it comes to UUIDs of real players. Note that those players in the list can run JavaScript codes in your server, which may even enable them to add UUIDs to the trusted players list ! Please BE CAUTIOUS !

Adding UUIDs to the file means the corresponding play will be able to use  `/pvpincore eval` command without limits.
