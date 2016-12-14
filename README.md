## How to run

1. `git clone https://github.com/harborl/SimpleWeb.git`
2. `cd SimpleWeb`
3. `mvn clean package assembly:single`
4. `cd target`
5. `java -jar simple-web-1.0-jar-with-dependencies.jar 5555 4 /`
6. `Open your browser or use curl tool to access url: http://localhost:5555`

## The command line parameters
```
cmd <listening-port> <concurrent_level> <folder_path>
```
