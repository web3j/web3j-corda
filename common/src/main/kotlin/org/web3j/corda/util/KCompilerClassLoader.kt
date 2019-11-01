/*
 * Copyright 2019 Web3 Labs LTD.
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
import java.io.IOException
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Optional
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler

/**
 * Class loader with Kotlin compilation capabilities.
 */
class KCompilerClassLoader

/**
 * Creates a class loader from the given source URLs.
 *
 * @param outputDir Directory where classes will be compiled.
 * @param urls Classpath URLs to compile the Java sources.
 */
constructor(
    private val urls: Array<URL>,
    private val outputDir: File
) : ClassLoader(KCompilerClassLoader::class.java.classLoader) {

    init {
        if (!outputDir.exists()) outputDir.mkdirs()
    }

    override fun findClass(name: String): Class<*> {
        return compileClass(name)
            .flatMap(this::readBytes)
            .map { defineClass(name, it, 0, it.size) }
            .orElseThrow { ClassNotFoundException(name) }
    }

    private fun compileClass(qualifiedName: String): Optional<File> {
        val pathToClass = qualifiedName.replace(".", File.separator)
        val containerClass = pathToClass.split("$").first()
        val outputFile = File(outputDir, "$pathToClass.class")

        // Check if already compiled
        if (outputFile.exists()) {
            return Optional.of(outputFile)
        }

        val sourceFile = findSourceFile(containerClass)
        if (!sourceFile.exists()) {
            return Optional.empty()
        }

        val classpath = buildClassPath()
        val cmd = listOf(
            JAVA_BINARY,
            "-Djava.awt.headless=true",
            "-cp", classpath,
            K2JVMCompiler::class.qualifiedName!!,
            "-cp", classpath,
            "-no-reflect",
            "-no-stdlib",
            sourceFile.absolutePath
        )

        val process = createProcess(cmd, outputDir).apply { readOutput() }

        return if (process.waitFor() == 0) {
            Optional.of(outputFile)
        } else {
            Optional.empty()
        }
    }

    private fun findSourceFile(path: String): File {
        for (url in urls) {
            val file = File(url.file, "$path.kt")
            if (file.exists()) {
                return file
            }
        }
        // Try to find the Kotlin file in generated files
        return File(outputDir, "$path.kt")
    }

    private fun buildClassPath(): String {
        val systemUrls = (javaClass.classLoader as URLClassLoader).urLs
        return buildClassPath(*urls) + ':' + buildClassPath(*systemUrls)
    }

    private fun buildClassPath(vararg urls: URL): String {
        return urls.map(URL::toExternalForm).joinToString(":") {
            it.replace("file:", "")
        }
    }

    private fun createProcess(cmd: List<String>, projectDir: File): Process {
        val builder = ProcessBuilder(cmd)
        builder.directory(projectDir)
        builder.redirectErrorStream(true)
        return builder.start()
    }

    private fun Process.readOutput() {
        inputStream.use { println(it.reader().readText()) }
        errorStream.use { println(it.reader().readText()) }
    }

    private fun readBytes(file: File): Optional<ByteArray> {
        return try {
            Optional.of(Files.readAllBytes(Paths.get(file.toURI())))
        } catch (e: IOException) {
            Optional.empty()
        }
    }

    companion object {
        private val JAVA_BINARY = File(File(System.getProperty("java.home"), "bin"), "java").absolutePath
    }
}
