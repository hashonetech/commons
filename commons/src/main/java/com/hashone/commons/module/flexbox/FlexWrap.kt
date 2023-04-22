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
 * This attribute controls whether the flex container is single-line or multi-line, and the
 * direction of the cross axis.
 */
@IntDef(value = [FlexWrap.NOWRAP, FlexWrap.WRAP, FlexWrap.WRAP_REVERSE])
@Retention(RetentionPolicy.SOURCE)
annotation class FlexWrap {
    companion object {
        /** The flex container is single-line.  */
        const val NOWRAP = 0

        /** The flex container is multi-line.  */
        const val WRAP = 1

        /**
         * The flex container is multi-line. The direction of the
         * cross axis is opposed to the direction as the [.WRAP]
         */
        const val WRAP_REVERSE = 2
    }
}