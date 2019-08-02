package org.web3j.corda.model

data class SignedTransaction(
    val signatures: List<String>,
    val references: List<String>,
    val networkParametersHash: String,
    val coreTransaction: CoreTransaction,
    val notaryChangeTransaction: Boolean,
    val missingSigners: List<String>
)

data class CoreTransaction(
    val componentGroups: List<ComponentGroup>,
    val privacySalt: String,
    val attachments: List<String>,
    val inputs: List<String>,
    val references: List<String>,
    val outputs: List<Output?>,
    val commands: List<Commands>,
    val notary: Party,
    val timeWindow: TimeWindow,
    val networkParametersHash: String,
    val id: String,
    val requiredSigningKeys: List<String>,
    val `groupHashes$core`: List<String>,
    val `groupsMerkleRoots$core`: Map<String, String>,
    val `availableComponentNonces$core`: Map<String, List<String>>,
    val `availableComponentHashes$core`: Map<String, List<String>>,
    val availableComponentGroups: List<List<Any?>?>
)
data class TimeWindow(
    val fromTime: String,
    val untilTime: String,
    val midpoint: String,
    val length: String
)
data class Commands(
    val value: Value?,
    val signers: List<String>
)
data class Value(val value: String?)
data class ComponentGroup(
    val groupIndex: Int,
    val components: List<String>
)

data class Output(
    val data: Data?,
    val contract: String?,
    val notary: Party?,
    val encumbrance: String?,
    val constraint: Constraint?
)
data class Constraint(val attachmentId: String)
data class Data(
    val amount: Money,
    val lender: Party,
    val borrower: Party,
    val paid: Money,
    val linearId: LinearId,
    val participants: List<Party>
)

data class LinearId(
    val externalId: String?,
    val id: String
)

data class Money(
    val quantity: Int,
    val displayTokenSize: Float,
    val token: String
)
