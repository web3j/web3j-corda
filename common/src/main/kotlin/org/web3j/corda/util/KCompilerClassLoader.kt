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
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Optional

/**
 * Class loader with Kotlin compilation capabilities.
 */
class KCompilerClassLoader

/**
 * Creates a class loader from the given source URLs.
 *
 * @param compiler Kotlin compiler to use on class loading.
 * @param urls Classpath URLs to compile the Java sources.
 */
constructor(
    private val urls: Array<URL>,
    private val compiler: KCompiler = KCompiler()
) : ClassLoader(KCompilerClassLoader::class.java.classLoader) {

    override fun findClass(name: String): Class<*> {

        val containerClass = name
            .replace('.', File.separatorChar)
            .split("$").first()

        return findSourceFile(containerClass)
            .map { compiler.compile(it) }
            .flatMap { findInnerClass(it, name) }
            .flatMap { readBytes(it) }
            .map { defineClass(name, it, 0, it.size) }
            .orElseThrow { ClassNotFoundException(name) }
    }

    private fun findSourceFile(path: String): Optional<File> {
        for (url in urls) {
            val file = File(url.file, "$path.kt")
            if (file.exists()) {
                return Optional.of(file)
            }
        }
        return Optional.empty()
    }

    private fun findInnerClass(classFiles: List<File>, name: String): Optional<File> {
        return classFiles.firstOrNull { file ->
            file.name.endsWith(name.split(".").last().split("$").last() + ".class")
        }.toOptional()
    }

    private fun readBytes(file: File): Optional<ByteArray> {
        return try {
            Optional.of(Files.readAllBytes(Paths.get(file.toURI())))
        } catch (e: IOException) {
            Optional.empty()
        }
    }
}
