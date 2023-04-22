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
 * This attribute controls the alignment along the cross axis.
 * The alignment in the same direction can be determined by the [AlignItems] attribute in the
 * parent, but if this is set to other than [AlignSelf.AUTO],
 * the cross axis alignment is overridden for this child.
 */
@IntDef(value = [AlignItems.FLEX_START, AlignItems.FLEX_END, AlignItems.CENTER, AlignItems.BASELINE, AlignItems.STRETCH, AlignSelf.AUTO])
@Retention(
    RetentionPolicy.SOURCE
)
annotation class AlignSelf {
    companion object {
        /**
         * The default value for the AlignSelf attribute, which means use the inherit
         * the [AlignItems] attribute from its parent.
         */
        const val AUTO = -1

        /** This item's edge is placed on the cross start line.  */
        const val FLEX_START = AlignItems.FLEX_START

        /** This item's edge is placed on the cross end line.  */
        const val FLEX_END = AlignItems.FLEX_END

        /** This item's edge is centered along the cross axis.  */
        const val CENTER = AlignItems.CENTER

        /** This items is aligned based on their text's baselines.  */
        const val BASELINE = AlignItems.BASELINE

        /** This item is stretched to fill the flex line's cross size.  */
        const val STRETCH = AlignItems.STRETCH
    }
}