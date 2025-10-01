<img width="2000" height="1000" alt="image(6)" src="https://github.com/user-attachments/assets/76ecdb11-ead6-4598-9d0c-4327503df5ec" />

---

# API

## Maven

- Add jitpack to repositories and Neptune to dependencies:

```xml
 <repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
 </repositories>
 <dependency>
     <groupId>com.github.Devlrxxh.Neptune</groupId>
     <artifactId>API</artifactId>
     <version>{latest-commit-hash}</version> <!-- change this to the latest commit hash -->
     <scope>provided</scope>
 </dependency>
```

- Also make sure you have added Neptune as a dependency in your plugin.yml:

```yml
depend: [Neptune]
```

## Usage

```java
NeptuneAPI neptune = NeptuneAPIProvider.getAPI();
neptune.getProfileService();
neptune.getMatchService();
neptune.getKitService();
neptune.getScoreboardService();
neptune.getArenaService();
neptune.getDivisionService();
neptune.getCosmeticService();
```
