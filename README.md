# Native-reflexy

NOTE: This plugin only supports `java-11-openjdk`

## Why Native-reflexy
Native-reflexy is a maven plugin that automatically generates reflection and 
proxy config for your native-java project. When deploying java applications on native platforms
such like GraalVM, it requires a close-world environment such that AOT compilation is possible, because
any class or interface that you access via reflection or dynamic proxy must be reachable at runtime, so
it is necessary to do some registration. For details, 
please refer to [Graal Documentation](https://www.graalvm.org/reference-manual/native-image/).

GraalVM is powerful enough to automatically register some classes for you by analyzing your code and check useses
of methods such like `Class.forName("com.example.SomeClass")`, but it requires the parameter to be static, otherwise you
have to register by yourself, using either programmatic way (by invoking Graal's API) or define then in json files
and pass them as arguments when compiling the code. However, the config file is like manually declare every single class
that might me used, which could be painful when the project contains thousands of classes to reflect. That's when this maven plugin
comes in.

Native-reflexy plugin allows you to declare packages where all classes in that package will be registered as reflective.

## Usage
Simply include this plugin in your project with classes and packages defined.
By default, the config file will be placed under src/main/resources/xm-reflect-config.json.
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.xmbsmdsj</groupId>
            <artifactId>native-reflexy</artifactId>
            <version>1.0-SNAPSHOT</version>
            <configuration>
                <reflectClasses>
                    <param>com.fasterxml.jackson.core.filter.FilteringParserDelegate</param>
                </reflectClasses>
                <reflectPackages>
                    <param>com.fasterxml.jackson.core.format</param>
                </reflectPackages>
            </configuration>
            <executions>
                <execution>
                    <phase>compile</phase>
                    <goals>
                        <goal>reflection</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

If your are using quarkus framework, simple add this to build args
```
-H:ResourceConfigurationFiles=xm-reflect-config.json
```
and you are good to go :)

# Configuration References

|Option|Type|Detail|
|---|---|---|
|reflectClasses|`List<String>`|List of classes to register for reflection|
|reflectPackages|`List<String>`|List of packages where all classes are registered for reflection|
|excludeClasses|`List<String>`|Classes to exclude when registering reflect classes. Note that proxy registrations are **NOT** counted|
|proxyInterfaces|`List<List<String>>`|Interfaces used in dynamic proxy.|
|reflectConfigFile|`String`|Location of reflect-config.json. If file name is given without path, the file is placed under `src/main/resources` path by default. Otherwise, the path and filename will be directly used(discouraged)|
|proxyConfigFile|`String`|Location of proxy-config.json. If file name is given without path, the file is placed under `src/main/resources` path by default. Otherwise, the path and filename will be directly used(discouraged)|