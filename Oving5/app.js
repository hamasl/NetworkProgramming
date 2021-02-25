//const HTTP = require("http")
const SERVER_PORT = 8080
const fs = require("fs")
const child_process = require("child_process")
const express = require('express');
const app = express();

app.use(
   express.urlencoded({
      extended: true
   })
)

app.use(express.json())

app.get("/codeCompiler/", (req, res) => {
   res.status(200, { "Content-Type": "text/html" })
   res.sendFile("./index.html", { root: __dirname })
})


app.post("/codeCompiler/", (req, res) => {
   fs.writeFile("./cpp_code/main.cpp", req.body.code, err => {
      if (err) {
         console.log("Could not create file main.cpp");
         res.status(300)
         res.send("Error occured")
      }
      else {
         console.log("Created file main.cpp.\nBuilding docker container...");
         child_process.exec("docker build -t gcc-image . && docker run --rm gcc-image", (err, stdout, stderr) => {
            console.log("Docker container run.\nSending ompiled code")
            res.status(200)
            res.contentType("application/json")
            res.send({ code: stdout, docker: stderr })
         }
         )
      }
   }
   )
})


app.listen(SERVER_PORT, "localhost", () => console.log("Server started running...."))