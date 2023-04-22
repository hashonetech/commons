/*
 * Copyright 2016 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hashone.commons.module.flexbox

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * The direction children items are placed inside the flex container, it determines the
 * direction of the main axis (and the cross axis, perpendicular to the main axis).
 */
@IntDef(value = [FlexDirection.ROW, FlexDirection.ROW_REVERSE, FlexDirection.COLUMN, FlexDirection.COLUMN_REVERSE])
@Retention(
    RetentionPolicy.SOURCE
)
annotation class FlexDirection {
    companion object {
        /**
         * Main axis direction -> horizontal. Main start to
         * main end -> Left to right (in LTR languages).
         * Cross start to cross end -> Top to bottom
         */
        const val ROW = 0

        /**
         * Main axis direction -> horizontal. Main start
         * to main end -> Right to left (in LTR languages). Cross start to cross end ->
         * Top to bottom.
         */
        const val ROW_REVERSE = 1

        /**
         * Main axis direction -> vertical. Main start
         * to main end -> Top to bottom. Cross start to cross end ->
         * Left to right (In LTR languages).
         */
        const val COLUMN = 2

        /**
         * Main axis direction -> vertical. Main start
         * to main end -> Bottom to top. Cross start to cross end -> Left to right
         * (In LTR languages)
         */
        const val COLUMN_REVERSE = 3
    }
}