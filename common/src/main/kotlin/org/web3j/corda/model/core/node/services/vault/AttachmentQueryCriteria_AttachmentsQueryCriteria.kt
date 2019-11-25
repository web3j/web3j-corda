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
package org.web3j.corda.model.core.node.services.vault

import javax.annotation.Generated

/**
 *
 * @param uploaderCondition
 * @param filenameCondition
 * @param uploadDateCondition
 * @param contractClassNamesCondition
 * @param signersCondition
 * @param isSignedCondition
 * @param versionCondition
 */
@Generated(
    value = ["org.web3j.corda.codegen.CorDappClientGenerator"],
    date = "2019-09-25T12:12:09.609Z"
)
data class AttachmentQueryCriteria_AttachmentsQueryCriteria(
    val uploaderCondition: Any? = null,
    val filenameCondition: Any? = null,
    val uploadDateCondition: Any? = null,
    val contractClassNamesCondition: Any? = null,
    val signersCondition: Any? = null,
    val isSignedCondition: Any? = null,
    val versionCondition: Any? = null
)
