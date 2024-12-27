// import { newEditor } from "editor-on-java";

// let editor = newEditor("arg")

// console.log(`Hello, ${editor}`);
console.log(`Hello`);

const { readFileSync } = require('fs');

const { Client } = require('ssh2');

const conn = new Client();
conn.on('ready', () => {
  console.log('Client :: ready');
  conn.exec('uptime', (err, stream) => {
    if (err) throw err;
    stream.on('close', (code, signal) => {
      console.log('Stream :: close :: code: ' + code + ', signal: ' + signal);
      conn.end();
    }).on('data', (data) => {
      console.log('STDOUT: ' + data);
    }).stderr.on('data', (data) => {
      console.log('STDERR: ' + data);
    });
  });
}).connect({
  host: '172.29.85.42',
  port: 22,
  username: 'kirill',
  password: 'gbpltw'

 //  privateKey: readFileSync('/path/to/my/key')
});
