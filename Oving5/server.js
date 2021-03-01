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

app.get("/", (req, res) => {
   res.status(200, { "Content-Type": "text/html" })
   res.sendFile("./index.html", { root: __dirname })
})


app.post("/", (req, res) => {
   //Changing directory so that dot notation works, cannot run __dirname when building docker
   process.chdir(__dirname)
   fs.writeFile("./dockerSys/main.cpp", req.body.code, err => {
      if (err) {
         console.log("Could not create file main.cpp");
         console.error(err.message);
         res.status(300)
         res.send("Error occured")
      }
      else {
         console.log("Created file main.cpp.\nBuilding docker container...");
         console.log(__dirname);
         child_process.exec(`docker build -t gcc-image ./ && docker run --rm gcc-image`, (err, stdout, stderr) => {

            console.log("Docker container run.\nSending compiled code")

            console.log("err:\n" + err);
            console.log("\n\n\nstdout:\n" + stdout);
            console.log("\n\n\nstderr:\n" + stderr);


            let compileError = ""
            let splitError = stderr.split("\n")
            splitError.forEach(line => {
               //Removes all lines that do not have anything to do with the error in the user submitted code
               if (line.trim().charAt(0) != "#" && line.length > 0 && !line.includes("./run.sh")) {
                  compileError += line + "\n"
               }
            });

            res.status(200)
            res.contentType("application/json")
            res.send({ code: stdout, error: compileError })
         }
         )
      }
   }
   )
})


app.listen(SERVER_PORT, "localhost", () => console.log("Server started running...."))