const net = require('net');
const fs = require('fs');
const WebSocketServer = require('./WebSocketServer').WebSocketServer;


// Simple HTTP server responds with a simple WebSocket client test
net.createServer((connection) => {
   connection.on('data', () => {
      fs.readFile(`${__dirname}/index.html`, (err, data) => {
         if (err) {
            connection.write(`HTTP/1.1 500 Internal Server Error`)
         }
         else {
            connection.write(`HTTP/1.1 200 OK\r\nContent-Length: ${data.length}\r\nContent-Type: text/html\r\nCharset= utf-8\r\n\r\n ${data}`)
         }
      })
   });
}).listen(3000, () => {
   console.log('HTTP server listening on port 3000');
});

//The websocket server
const wsServer = new WebSocketServer("localhost", 3001)
wsServer.startServer((data) => {
   let readData = wsServer.read(data)
   console.log(readData);
   if (readData !== "") wsServer.writeToAll(readData)
})