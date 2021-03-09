const net = require('net');
const crypto = require('crypto');

//The constant for generating the field for Sec-Websocket-Key
const keyConstant = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
class WebSocketServer {

   constructor(addresss, port) {
      this.addresss = addresss
      this.port = port
   }

   startServer(onDataRecieved) {
      this.sockets = []
      this.server = net.createServer()
      this.server.on("connection", (socket) => {
         this.sockets.push(socket)

         socket.on("connect", () => {
            console.log("Connecting to socket socket");
         })
         socket.on("data", (data) => {
            if (data.includes("GET / HTTP/1.1") && data.includes("Upgrade: websocket") && data.includes("Connection: Upgrade")) {
               socket.write(`HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: ${this.getReturnAcceptKey(data.toString())}\r\n\r\n`)
            }
            else {
               if (onDataRecieved != undefined && onDataRecieved != null) onDataRecieved(data, socket)
            }
         })
         socket.on("close", () => {
            let closingSockets = this.sockets.splice(this.sockets.indexOf(socket), 1)
            closingSockets.forEach(sock => sock.end())
            console.log(`Closed connection(s): ${closingSockets}`);
            console.log(closingSockets);
         })
      })
      this.server.listen(this.port, this.addresss)
   }

   getReturnAcceptKey(request = "") {
      const reqSplitByLine = request.split("\n")
      console.log(reqSplitByLine);
      let key = ""
      reqSplitByLine.forEach(line => {
         if (line.includes("Sec-WebSocket-Key:")) key = line.split(" ")[1]
      })
      return crypto.createHash("sha1").update(key.trim() + keyConstant.trim(), "binary").digest("base64")
   }

   /**
    * 
    * Dont care about checking if the message is masked or not, since its sent from a browser it should always be masked
    * Can in theory support larger frames than 64kB as seen under: else if(messageength == 127), but Buffer.allocate() does not support such large buffers
    * @param {Buffer} messageBuffer the massage to be read
    * @returns String with message or empty if it was non valid
    */
   read(messageBuffer) {
      //Checking for frames containing text
      if ((messageBuffer.readUInt8(0) & 15) !== 1) return ""
      let messageLength = messageBuffer.readUInt8(1) & 127
      let maskStart = 2
      //Does not handle messages over 126 bytes
      //TODO maybe unnecesary
      if (messageLength > 127) return ""
      if (messageLength == 126) {
         messageLength = messageBuffer.readUInt16BE(maskStart)
         maskStart += 2
      } else if (messageLength == 127) {
         messageLength = messageBuffer.readBigUInt64BE(maskStart)
         maskStart += 8
      }
      const dataStart = maskStart + 4

      const answer = Buffer.alloc(messageLength)
      for (let i = dataStart; i < dataStart + messageLength; i++) {
         const masked = messageBuffer.readUInt8(i)
         const key = messageBuffer.readUInt8(maskStart + ((i - dataStart) % 4))
         answer.writeUInt8(masked ^ key, i - dataStart)
      }
      return answer.toString("utf8")
   }

   /**
    * Writes a message to all connected sockets
    * Does in theory support writing frames larger than 64kB, but the Buffer.allocate method does not support such large frames
    * @param {*} message the message to be written
    * @returns nothing if the message does not match requirements of being a string
    */
   writeToAll(message = "") {
      if (message === undefined || message === null || typeof message !== "string") return
      const contentLength = Buffer.byteLength(message)
      let contentStart = 2
      let answer = Buffer.alloc(contentLength + contentStart)
      if (contentLength < 126) {
         answer.writeUInt8(contentLength, 1)
      } else if (contentLength < 65536) {
         contentStart += 2
         answer = Buffer.alloc(contentLength + contentStart)
         answer.writeUInt8(126, 1)
         answer.writeUInt16BE(contentLength, 2)
      } else {
         contentStart += 8
         answer = Buffer.alloc(contentLength + contentStart)
         answer.writeUInt8(127, 1)
         answer.writeBigUInt64BE(contentLength, 2)
      }
      //10000001 in binary indicating that this is the final frame, and that we are sending text
      answer.writeUInt8(129, 0)

      answer.write(message, contentStart)

      console.log("writeToAll: " + answer);
      this.sockets.forEach(socket => {
         if (socket.readyState === "open") {
            socket.write(answer)
         }
      });

   }
}

module.exports = { WebSocketServer }