## ▶️ How to Run the Project

### 1. Build the JAR
```bash
./gradlew clean bootJar
```
### 2. Run docker
```bash
docker-compose up --build
```  

## If new migrations added run this
```bash
./gradlew clean build
docker-compose up --build
```
