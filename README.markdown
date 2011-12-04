h1. Overview

*Scaldi* is scala dependency injection framework. Basically Scala already have everything you need for dependency injection. But still
some things can be made easier. Goal of the project is to provide more standard and easy way to make dependency injection in scala
projects consuming power of scala language. With *Scaldi* you can define your application modules in pure scala (without any annotations or XML).
*Scaldi* also provides *Lift* support (*scaldi-lift* module).

In order to discover project features and look at *Scaldi* in action you can look at *scaldi-core-examples* and *scaldi-lift-examples* modules.

At the moment *Scaldi* is proof of concept. I hope you will find it helpful. Your feedback is very welcome and it would be very helpful
for the further project development!

h1. Maven configuration

In order to use *Scaldi* withing Maven2 project you should add one new repository in your *pom.xml*:

<pre><code>
    <repositories>
        <repository>
            <id>angelsmasterpiece-repo</id>
            <name>Angel's Masterpiece Maven 2 Repository</name>
            <url>https://raw.github.com/OlegIlyenko/angelsmasterpiece-maven-repo/master</url>
        </repository>
    </repositories>
</code></pre>

Now you can add this dependency (*scaldi-core*):

<pre><code>
    <dependency>
        <groupId>org.angelsmasterpiece</groupId>
        <artifactId>scaldi-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</code></pre>

If you want to use it in *Lift project* you should also add *scaldi-lift* module:

<pre><code>
    <dependency>
        <groupId>org.angelsmasterpiece</groupId>
        <artifactId>scaldi-lift</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</code></pre>
