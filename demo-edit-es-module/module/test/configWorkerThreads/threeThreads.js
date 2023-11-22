const srcLocation = "../../src/";
const editorApi = await import(srcLocation + "editor.js");
const editor = await editorApi.newEditor({
    containerId: "editor",
    workerUrl: srcLocation + "worker.js",
    numThreads: Math.PI
});

const initialText =
    "This is an experimental project\n" +
    "to write a portable (Web + Desktop)\n" +
    "editor in java and kotlin\n";

editor.setText(initialText + "Using three worker threads\n");
editor.focus();

