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
package org.web3j.corda.console

import picocli.CommandLine

class CordaCommandMain {
    companion object {
        private val LOGO = """
                      _      _____ _                     _
                     | |    |____ (_)                   | |
        __      _____| |__      / /_    ___      _ __ __| | __ _
        \ \ /\ / / _ \ '_ \     \ \ |  / __/ @|red _|@  | '__/ _` |/ _` |
         \ V  V /  __/ |_) |.___/ / | | (_  @|red (_)|@ | | | (_| | (_| |
          \_/\_/ \___|_.__/ \____/| |  \___\    |_|  \__,_|\__,_|
                                 _/ |
                                |__/

        """.trimIndent()

        @JvmStatic
        fun main(vararg args: String) {
            println(CommandLine.Help.Ansi.AUTO.string(LOGO))
            CommandLine(CordaCommand()).execute(*args)
        }
    }
}
