// 页面加载完成后，初始化游戏状态
window.onload = function () {
    refreshGameState();
    // 回车执行指令
    document.getElementById("cmdInput").addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
            sendCommand();
        }
    });
};

// 发送指令到后端接口
function sendCommand() {
    let input = document.getElementById("cmdInput").value.trim();
    if (!input) {
        alert("请输入指令！");
        return;
    }

    // 调用后端接口
    fetch("/game/command?input=" + encodeURIComponent(input))
        .then(res => res.json())
        .then(result => {
            // 追加日志
            appendLog(result.message);
            // 刷新全局状态
            refreshGameState();
            // 清空输入框
            document.getElementById("cmdInput").value = "";
        });
}

// 快捷按钮触发指令
function quickCmd(cmd) {
    document.getElementById("cmdInput").value = cmd;
    sendCommand();
}

// 刷新游戏所有状态（房间、血量、负重、背包）
function refreshGameState() {
    fetch("/game/state")
        .then(res => res.json())
        .then(state => {
            // 刷新状态栏
            document.getElementById("roomName").innerText = state.currentRoomName;
            document.getElementById("hp").innerText = state.health;
            document.getElementById("weight").innerText = state.currentWeight + "/" + state.maxWeight;
            document.getElementById("score").innerText = state.score;

            // 刷新背包
            let bagDom = document.getElementById("bagContent");
            if (state.inventoryItems.length === 0) {
                bagDom.innerText = "背包为空";
            } else {
                bagDom.innerHTML = "";
                state.inventoryItems.forEach(item => {
                    bagDom.innerHTML += item + "<br>";
                });
            }
        });
}

// 追加日志信息
function appendLog(text) {
    let log = document.getElementById("logContent");
    log.innerHTML += "> " + text + "<br>";
    // 滚动到最底部
    log.scrollTop = log.scrollHeight;
}