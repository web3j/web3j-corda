/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.corda.util

import java.io.File
import java.net.URLClassLoader
import java.nio.file.Files

/**
 * Kotlin compiler wrapper around `org.jetbrains.kotlin.cli.jvm.K2JVMCompiler`.
 */
class KCompiler(private val outputDir: File = Files.createTempDirectory(null).toFile()) {

    init {
        if (!outputDir.exists()) outputDir.mkdirs()
    }

    fun compile(sourceFile: File): List<File> {
        require(sourceFile.exists()) { "Source file not found: ${sourceFile.absolutePath}" }

        val packagePath = readPackageName(sourceFile).replace('.', File.separatorChar)
        val classFile = packagePath + File.separatorChar + sourceFile.name.replace(".kt", ".class")
        val outputFile = File(outputDir.absolutePath, classFile)

        // Check if already compiled
        if (outputFile.exists()) {
            return findInnerClasses(outputFile)
        }

        return createProcess(sourceFile, packagePath).run {
            if (waitFor() == 0) {
                findInnerClasses(outputFile)
            } else {
                listOf()
            }
        }
    }

    private fun readPackageName(sourceFile: File): String {
        return Files.lines(sourceFile.toPath())
            .filter { PACKAGE_REGEX.containsMatchIn(it) }
            .findFirst().map {
                // We know for sure that the match will succeed
                PACKAGE_REGEX.find(it)!!.groupValues[1]
            }.orElse("")
    }

    private fun findInnerClasses(sourceFile: File): List<File> {
        val name = sourceFile.name.removeSuffix(".class")

        // List folder files to find files like Class1$Inner1$...
        return (sourceFile.parentFile.listFiles { file: File ->
            file.name.contains(name)
        } ?: arrayOf()).toList()
    }

    private fun buildClassPath(): String {
        val systemUrls = (javaClass.classLoader as URLClassLoader).urLs.filter {
            File(it.file).exists()
        }.joinToString(":") {
            it.toExternalForm().replace("file:", "")
        }

        return outputDir.absolutePath + ':' + systemUrls
    }

    private fun createProcess(sourceFile: File, packagePath: String): Process {

        // Compile the whole directory to avoid a new process per class
        val sourceDir = File(sourceFile.parentFile.absolutePath.removeSuffix(packagePath))
        val classpath = buildClassPath()

        val cmd = listOf(
            JAVA_BINARY,
            "-Djava.awt.headless=true",
            "-cp", classpath,
            "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler",
            "-cp", classpath,
            "-no-reflect",
            "-no-stdlib",
            sourceDir.absolutePath
        )

        return ProcessBuilder(cmd).apply {
            directory(outputDir)
            redirectErrorStream(true)
        }.start().apply {
            inputStream.use { println(it.reader().readText()) }
            errorStream.use { println(it.reader().readText()) }
        }
    }

    companion object {
        private val JAVA_BINARY = File(File(System.getProperty("java.home"), "bin"), "java").absolutePath
        private val PACKAGE_REGEX = "\\s*package ([a-zA-Z0-9._]+)".toRegex()
    }
}
