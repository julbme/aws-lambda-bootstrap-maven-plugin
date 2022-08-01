[![Build](https://github.com/julbme/aws-lambda-bootstrap-maven-plugin/actions/workflows/maven-build.yml/badge.svg)](https://github.com/julbme/aws-lambda-bootstrap-maven-plugin/actions/workflows/maven-build.yml)
[![Lint Commit Messages](https://github.com/julbme/aws-lambda-bootstrap-maven-plugin/actions/workflows/commitlint.yml/badge.svg)](https://github.com/julbme/aws-lambda-bootstrap-maven-plugin/actions/workflows/commitlint.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=julbme_aws-lambda-bootstrap-maven-plugin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=julbme_aws-lambda-bootstrap-maven-plugin)
![Maven Central](https://img.shields.io/maven-central/v/me.julb/aws-lambda-bootstrap-maven-plugin)

# AWS Lambda Bootstrap Maven plugin

This Maven plugin provides a goal to generate a `bootstrap` file used for AWS Lambda custom runtimes.
By default, the goal is bound to the `prepare-package` phase.

## Usage

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>me.julb</groupId>
            <artifactId>aws-lambda-bootstrap-maven-plugin</artifactId>
            <executions>
                <execution>
                    <id>generate-bootstrap-file</id>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                    <configuration>
                        <runtimeFileName></runtimeFileName><!-- defaults to ${project.artifactId} -->
                        <runtimeOptions></runtimeOptions><!-- optional -->
                        <outputFile></outputFile><!-- defaults to ${project.build.directory}/aws-lambda/bootstrap -->
                    </configuration>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>
</build>
```

The configuration above generates `target/aws-lambda/bootstrap` with `0755` posix permissions and the following content:

```bash
#!/bin/sh

cd $LAMBDA_TASK_ROOT

./#runtimeFileName# #runtimeOptions#
```

The file is then ready to be packaged with the runtime script in a zip using the [Maven Assembly plugin](https://maven.apache.org/plugins/maven-assembly-plugin/).

## Contributing

This project is totally open source and contributors are welcome.
