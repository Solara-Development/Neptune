<div align="center">

<img src="https://github.com/user-attachments/assets/76ecdb11-ead6-4598-9d0c-4327503df5ec" alt="Neptune Practice Core" width="100%"/>

# 💻 API

**Integrate Neptune into your own plugins with the developer API.**

[![Discord](https://img.shields.io/badge/Discord-Join%20Server-7289da?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/f6rUtpy6y4)
[![BuiltByBit](https://img.shields.io/badge/BuiltByBit-Purchase-7289da?style=for-the-badge&logo=builtbybit&logoColor=white)](https://builtbybit.com/resources/neptune-practice-core.44588/)

</div>

---

## 📦 Installation

### Maven

**1.** Add JitPack to your `pom.xml` repositories:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

**2.** Add Neptune as a dependency:

```xml
<dependency>
    <groupId>com.github.Devlrxxh.Neptune</groupId>
    <artifactId>API</artifactId>
    <version>{latest-commit-hash}</version> <!-- Replace with the latest commit hash -->
    <scope>provided</scope>
</dependency>
```

> [!TIP]
> Find the latest commit hash on the [Neptune GitHub repository](https://github.com/Devlrxxh/Neptune).

---

**3.** Declare Neptune as a dependency in your `plugin.yml`:

```yml
depend: [Neptune]
```

---

## 🚀 Usage

Obtain the API instance and access any of the available services:

```java
NeptuneAPI neptune = NeptuneAPIProvider.getAPI();
```

---

## 🛠️ Available Services

| Method | Description |
|---|---|
| `neptune.getProfileService()` | Access and manage player profiles |
| `neptune.getMatchService()` | Interact with active and historical matches |
| `neptune.getKitService()` | Manage kits and kit configurations |
| `neptune.getScoreboardService()` | Control scoreboard rendering |
| `neptune.getArenaService()` | Access and manage arenas |
| `neptune.getDivisionService()` | Retrieve division data and thresholds |
| `neptune.getCosmeticService()` | Manage cosmetics and their assignments |

---

> [!IMPORTANT]
> Need help integrating the API? Join our [Discord server](https://discord.gg/f6rUtpy6y4) for developer support.
