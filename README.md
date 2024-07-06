# Twig

> [!IMPORTANT]
> Twig is not supported by me anymore due to the complexity required to maintain it for relatively little pay off.

Twig is a WIP lightweight game library powered by JavaFX. It is the most cursed API I have ever created.

Twig's features were built to facilitate a simple game project for my University course and as such are not the most
robust or fully fleshed out.

Twig should not be used in production, if you need a JavaFX based game library I suggest you check
out [FXGL](https://github.com/AlmasB/FXGL).

## Installation

Twig is not published to the Maven Central Repository.

To use Twig for your own purposes clone the repository and open it's containing folder in the terminal.

Execute the following to add Twig to your local Maven repository:

```
mvn install
```

To include Twig in your project add the following to your pom.xml file:

```xml
<dependencies>
    <!--Your other dependencies here...-->

    <dependency>
        <groupId>com.neo</groupId>
        <artifactId>Twig</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
