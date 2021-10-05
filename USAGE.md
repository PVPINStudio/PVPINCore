# PVPINCore Command Usage

指令均提供补全功能，会自动提供补全。

All the commands provide tab completes, so you won't need to check the file name / plugin name by yourself.

## Load File Command

 `/pvpincore loadfile <文件名>` -- 加载指定`.js`文件

`/pvpincore loadfile <File Name>` -- Load the specified `.js` file as a JavaScript plugin

这一指令用于加载本地文件里的 JavaScript 插件。这些插件必须位于 `plugins/PVPINCore/js` 文件夹中。文件名全都是类似 `abc.js` 的不包含任何相对路径的文件名（Simple Name）。请勿采用文件软连接等方式向文件夹内添加文件，这样的文件无法被正常读取。

This command is used to load JavaScript plugins based on local files. All those plugins must be located in the `plugins/PVPINCore/js` folder, and the file name should be simple names like `abc.js` free of relevant file paths. NEVER use soft links to add files to this folder because these files can NOT be loaded normally.

 ## Disable Command

`/pvpincore disable <插件名>` -- 卸载指定名称的 JavaScript 插件

`/pvpincore disable <Plugin Name>` --Unload a JavaScript plugin by its name

这一指令用于按照名称卸载 JavaScript 插件。补全列表将会显示所有已加载的插件。

This command is used to unload a JavaScript plugin by its name. The tab complete list will show all the plugins that have been loaded.

## Eval Command

指令也可用于临时运行一段 JavaScript 语句。

`/pvpincore eval <JavaScript>` -- 执行一段JavaScript语句

`/pvpincore eval` -- 读取手持成书中全部内容并作为一段 JavaScript 语句执行

服务器管理员需要特别注意，玩家可能编写出恶意代码。此功能必须谨慎使用！

You may also temporarily load some JavaScript code using the following command:

`/pvpincore eval <JavaScript>` -- Directly execute some JavaScript code.

`/pvpincore eval` -- Reads the contents of the written book in the command sender's hand, and execute the string content as a piece of JavaScript code. 

NOTICE that malicious code may be written by players, so operators MUST be cautious when enabling this command.

## Reset Command

`/pvpincore reset <玩家名>` -- 卸载指定玩家执行的所有 JavaScript

`/pvpincore resetall` -- 卸载所有 JavaScript 插件并重新加载所有 `.js` 文件

前一指令用于清空某一玩家通过 `eval` 指令执行的所有 JavaScript 代码，以减小服务器负担。

后一指令用于卸载所有 JavaScript 插件，再加载所有 `plugins/PVPINCore/js` 文件夹内的 `.js` 文件。

`/pvpincore reset <Player Name>` -- Unload all JavaScript plugins temporarily executed by a player

`/pvpincore resetall` -- Unload all JavaScript plugins and reload all `.js` file based plugins

The former command is used to unload all plugins executed by a player using `eval` command, thus relieving the server's burden.

The latter command is used to unload all JavaScript plugins and reload all `.js` files in the `plugins/PVPINCore/js` folder. 