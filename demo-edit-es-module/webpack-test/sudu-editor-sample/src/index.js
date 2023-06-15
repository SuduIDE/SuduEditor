import { newEditor } from "sudu-editor";

function addPreText(divId, text) {
    let pre = document.createElement("pre");
    pre.innerHTML = text;
    let style = pre.style;
    style.setProperty("padding-left", "10px");
    style.setProperty("padding-right", "10px");
    document.getElementById(divId).appendChild(pre);
}

function main() {
    for (const x of ["editor1", "editor2"]) {
        newEditor({containerId: x, workerUrl: "worker.js"}).then(
            result => {
                if (x === "editor1") result.focus();
                else result.setTheme("light")
                console.log("Editor " + x + " started")
            },
            error => addPreText(x, error)
        )
    }
}

document.addEventListener("DOMContentLoaded", main)
