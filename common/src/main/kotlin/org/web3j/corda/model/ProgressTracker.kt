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
package org.web3j.corda.model

/**
 *
 * @param inputSteps
 * @param steps
 * @param stepIndex
 * @param stepsTreeIndex
 * @param parent
 * @param currentStep
 * @param changes
 * @param allStepsLabels
 * @param hasEnded
 * @param currentStepRecursive
 * @param topLevelTracker
 * @param allSteps
 */
data class ProgressTracker(
    val steps: kotlin.collections.List<ProgressTracker_Step>,
    val stepIndex: kotlin.Int,
    val stepsTreeIndex: kotlin.Int,
    val allStepsLabels: kotlin.collections.List<Pair>,
    val hasEnded: kotlin.Boolean,
    val topLevelTracker: ProgressTracker,
    val allSteps: kotlin.collections.List<ProgressTracker_Step>,
    val inputSteps: kotlin.collections.List<ProgressTracker_Step>? = null,
    val parent: ProgressTracker? = null,
    val currentStep: ProgressTracker_Step? = null,
    val changes: kotlin.Any? = null,
    val currentStepRecursive: ProgressTracker_Step? = null
)
