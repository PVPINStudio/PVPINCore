# PVPINCore APIDocs

本项目所有注释及文档均为英语编写而成。开发者必须掌握英语。否则请使用 PVPIN Blockly Editor。

All comments and the api docs of this project are written in English. If you can't understand English, go to use the PVPIN Blockly Editor.



## For Java Users

Not Finished Yet.

多数 Java API 都在 com.pvpin.pvpincore.api 包下。一部分 API 需要直接自 impl 包调用。



## For JavaScript Users

参见 src/main/javascript/api.js。

有一个带注释和示例的版本，目前仅供内部使用。

你可以先在 PVPIN Blockly Editor 里搭积木，这是最完善的示例。

下面提供一些简单的实例：

    scriptManager
        .registerListener(
            "org.bukkit.event.server.ServerCommandEvent",
            Java.type("org.bukkit.event.EventPriority").NORMAL,
            false,
            function (event) {
                var str = event.getCommand();
            }
        );

    scriptManager.registerCommand(
        "jstest",
        function (sender, cmd, args) {
            log("Test");
        },
        function (sender, cmd, args) {
            return ["tab"];
        }
    );

    var task = scriptManager.registerTask().interval(60).delay(0).callback(function () {
        
    }).buildAndRun();

    scriptManager.readPersistentDataFromFile();
    var map = scriptManager.getDataMap();
    map.put("a", "b");
    map.put("b", [0, 1, 2, 3, 4, 5, 6]);
    scriptManager.savePersistentDataToFile();
    scriptManager.readPersistentDataFromFile();
    var newMap = scriptManager.getDataMap();
