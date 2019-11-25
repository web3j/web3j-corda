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
package org.web3j.corda.model.core.contracts

import javax.annotation.Generated

/**
 *
 * @param txId Base 58 Encoded Secure Hash
 * @param attachmentHash Base 58 Encoded Secure Hash
 * @param originalExceptionClassName
 * @param originalErrorId
 * @param errorId
 * @param message
 * @param originalMessage
 * @param localizedMessage
 */
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-09-25T12:12:09.607Z"
)
data class TransactionVerificationException_InvalidAttachmentException(
    /* Base 58 Encoded Secure Hash */
    val txId: String,
    /* Base 58 Encoded Secure Hash */
    val attachmentHash: String,
    val originalExceptionClassName: String? = null,
    val originalErrorId: Long? = null,
    val errorId: Long? = null,
    val message: String? = null,
    val originalMessage: String? = null,
    val localizedMessage: String? = null
)
