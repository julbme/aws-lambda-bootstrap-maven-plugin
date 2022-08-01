/**
 * MIT License
 *
 * Copyright (c) 2017-2022 Julb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.julb.maven.plugins;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Generates a bootstrap file for AWS Lambda custom runtime.
 * <br>
 *
 * @author Julb.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class AWSLambdaBootstrapGenerate extends AbstractMojo {

    /**
     * Gives access to the Maven project information.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    /**
     * The runtime file.
     */
    @Parameter(property = "runtimeFileName", required = true, defaultValue = "${project.artifactId}")
    String runtimeFileName;

    /**
     * The runtime options.
     */
    @Parameter(property = "runtimeOptions")
    String runtimeOptions;

    /**
     * File output.
     */
    @Parameter(
            property = "outputFile",
            required = true,
            defaultValue = "${project.build.directory}/aws-lambda/bootstrap")
    String outputFile;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            getLog().debug(String.format("runtime file name: %s", runtimeFileName));
            getLog().debug(String.format("runtime options: %s", runtimeOptions));
            getLog().debug(String.format("bootstrap output file: %s", outputFile));

            // check inputs
            if (runtimeFileName == null || runtimeFileName.trim().isEmpty()) {
                throw new IllegalArgumentException("runtimeFileName is required.");
            }
            if (outputFile == null || outputFile.trim().isEmpty()) {
                throw new IllegalArgumentException("outputFile is required.");
            }

            // get path
            Path outputFilePath = Path.of(outputFile);

            // create directory
            getLog().debug("creating output directory.");
            outputFilePath.getParent().toFile().mkdirs();

            // write content
            getLog().debug("write bootstrap content.");
            String runtimeExec = String.format("./%s", runtimeFileName);
            if (runtimeOptions != null) {
                runtimeExec += " " + runtimeOptions;
            }
            List<String> lines = Arrays.asList("#!/bin/sh", "", "cd $LAMBDA_TASK_ROOT", "", runtimeExec);
            Files.write(outputFilePath, lines, StandardCharsets.UTF_8);

            // set file permissions
            getLog().debug("setting file permissions to 0755.");
            Set<PosixFilePermission> permissions = EnumSet.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.GROUP_READ,
                    PosixFilePermission.GROUP_EXECUTE,
                    PosixFilePermission.OTHERS_READ,
                    PosixFilePermission.OTHERS_EXECUTE);
            Files.setPosixFilePermissions(outputFilePath, permissions);
        } catch (IOException e) {
            getLog().error("Unable to generate bootstrap file.", e);
            throw new MojoExecutionException(e);
        }
    }
}
