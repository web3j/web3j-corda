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
package org.web3j.corda.model.core.crypto

import javax.annotation.Generated

/**
 *
 * @param bytes
 * @param by Base 58 Encoded Public Key
 * @param signatureMetadata
 * @param partialMerkleTree
 */
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-10-15T13:26:15.483Z"
)
data class TransactionSignature(
    val bytes: String,
    /* Base 58 Encoded Public Key */
    val by: String,
    val signatureMetadata: org.web3j.corda.model.core.crypto.SignatureMetadata? = null,
    val partialMerkleTree: org.web3j.corda.model.core.crypto.PartialMerkleTree? = null
)
