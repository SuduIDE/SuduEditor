console.log(`Hello`);

import { Worker } from 'worker_threads';

let worker = new Worker("./worker.mjs");

let requests = 0;

function dumpFolder(path, list) {
  let s = "";
  for (let i = 0; i < list.length; i++) {
    let filename = list[i].filename;
    s += i + 1 < list.length ? filename + ", " : filename;
  }
  console.log("  directory '" + path +
      "', files[" + list.length + "]: " + s);
}

function closeConnection() {
  worker.postMessage({cmd: "close", ssh: ssh});
}

function maybeClose() {
  if (--requests === 0)
    closeConnection();
}

worker.on('message', function(message) {
  console.log("message from worker", message[0]);
  switch (message[0]) {
    case "finished":
      console.log("  worker.terminate() ....")
      worker.terminate();
      break;
    case "listed":
      console.log("  listed folder ", message[1]);
      dumpFolder(message[1], message[2]);
      maybeClose();
      break;
    case "data":
      console.log("  file data ", message[1]);

      maybeClose();
      break;

  }
});

const ssh = {
  host: '172.29.85.42',
  port: 22,
  username: 'kirill',
  password: 'gbpltw'
  //  privateKey: readFileSync('/path/to/my/key')
};

requests++;
worker.postMessage({cmd: "readDir", path: '/home', ssh: ssh});

requests++;
worker.postMessage({cmd: "readDir", path: '/home/kirill', ssh: ssh});

requests++;
worker.postMessage({cmd: "readDir", path: '/usr', ssh: ssh});

requests++;
worker.postMessage({cmd: "readFile", path: '/home/kirill/.bash_history', ssh: ssh});

