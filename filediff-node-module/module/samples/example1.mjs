﻿import { createDiffEngine } from "../src/diffEngine.mjs";

let module = await createDiffEngine("../src/diffEngineWorker.mjs");

let jobCount = 0;

function mayBeExit() {
  if (--jobCount === 0)
    module.dispose();
}

console.log("got module: ", module.constructor.name);

jobCount++;
const fibFuture = module.fib(5).then(
    msg => {
      console.log("got foo result: " + msg);
      mayBeExit();
    }
);

if (process.argv.length === 3) {
  const dirname = process.argv[2];
  jobCount++;
  module.testFS(dirname, () => {
    console.log("testFs.onComplete");
    mayBeExit();
  });
} else if (process.argv.length === 4) {
  const dir1 = process.argv[2];
  const dir2 = process.argv[3];
  jobCount++;
  module.testFS2(dir1, dir2, () => {
    console.log("testFS2.onComplete");
    mayBeExit();
  });

} else {
  console.log("not running testFS");
}
