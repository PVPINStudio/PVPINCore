var scriptManager = Java.type("com.pvpin.pvpincore.modules.PVPINCore").getScriptManagerInstance();

export function log(msg) {
    scriptManager.log(msg);
}

export function registerEventListener(eventType, priority, ignoreCancelled, callback) {
    return scriptManager.registerListener(eventType, priority, ignoreCancelled, callback);
}

export function unregisterEventListener(listener) {
    listener.unregister();
}

export function registerCommand(name, cmdCallback, tabCallback) {
    scriptManager.registerCommand(name, cmdCallback, tabCallback);
}

export function unregisterCommand(name) {
    scriptManager.unregisterCommand(name);
}

export function runTaskTimer(interval, async, callback) {
    return scriptManager.registerTask().callback(callback).interval(interval).buildAndRun();
}

export function runTaskLater(delay, async, callback) {
    return scriptManager.registerTask().callback(callback).delay(delay).buildAndRun();
}

export function cancelTask(task) {
    task.cancel();
}

export function sendActionBar(player, msg) {
    Java.type("com.pvpin.pvpincore.impl.nms.entity.PlayerNMSUtils").sendActionBar(msg, player);
}
