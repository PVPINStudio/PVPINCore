var scriptManager = Java.type("com.pvpin.pvpincore.modules.PVPINCore").getScriptManagerInstance();

function log(msg) {
    scriptManager.log(msg);
}

function registerEventListener(eventType, priority, ignoreCancelled, callback) {
    scriptManager.registerListener(eventType, priority, ignoreCancelled, callback);
}

function registerCommand(name, cmdCallback, tabCallback) {
    scriptManager.registerCommand(name, cmdCallback, tabCallback);
}

function unregisterCommand(name) {
    scriptManager.unregisterCommand(name);
}

function runTaskTimer(interval, async, callback) {
    return scriptManager.registerTask().callback(callback).interval(interval).buildAndRun();
}

function runTaskLater(delay, async, callback) {
    return scriptManager.registerTask().callback(callback).delay(delay).buildAndRun();
}

function cancelTask(task) {
    task.cancel();
}

function getDataMap() {
    return scriptManager.getDataMap();
}

function readPersistentDataFromFile() {
    scriptManager.readPersistentDataFromFile();
}

function savePersistentDataToFile() {
    scriptManager.savePersistentDataToFile();
}

function sendActionBar(player, msg) {
    Java.type("com.pvpin.pvpincore.impl.nms.entity.PlayerNMSUtils").sendActionBar(msg, player);
}
