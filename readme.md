# Spring Parking CLI

A simple CLI parking lot management app built with Java and Spring Boot.

---

## Requirements
- Java 17+ (download: https://adoptium.net/)
- Maven (if building from source) (download: https://maven.apache.org/)

---

## Build from Source (requires Maven)

Clone the repo:

`git clone <repo-url>`

Go to project directory:

`cd spring-parking`

Build the project with Maven:

`mvn clean package`

This will generate `spring-parking.jar` in the `target` folder.

---

## Download Prebuilt Release

Go to the GitHub Releases page:

https://github.com/ian-tabs/spring-parking/releases/

---

## How to Run

To run the app, provide a command file. Flags are optional:

java -jar spring-parking.jar <commands-file> [options]

Options:
- `--help` or `-h` : Show help message
- `--version` or `-v` : Show version info

Example:

`java -jar target/spring-parking.jar commands.txt`

Assuming file is still in target folder after `mvn package`