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

import android.view.View

/**
 * Holds properties related to a single flex line. This class is not expected to be changed outside
 * of the [FlexboxLayout], thus only exposing the getter methods that may be useful for
 * other classes using the [FlexboxLayout].
 */
class FlexLine internal constructor() {
    var mLeft = Int.MAX_VALUE
    var mTop = Int.MAX_VALUE
    var mRight = Int.MIN_VALUE
    var mBottom = Int.MIN_VALUE
    /**
     * @return the size of the flex line in pixels along the main axis of the flex container.
     */
    /** @see .getMainSize
     */
    var mainSize = 0

    /**
     * The sum of the lengths of dividers along the main axis. This value should be lower
     * than the value of [.mMainSize].
     */
    var mDividerLengthInMainSize = 0
    /**
     * @return the size of the flex line in pixels along the cross axis of the flex container.
     */
    /** @see .getCrossSize
     */
    var crossSize = 0
    /**
     * @return the count of the views contained in this flex line.
     */
    /** @see .getItemCount
     */
    var itemCount = 0

    /** Holds the count of the views whose visibilities are gone  */
    var mGoneItemCount = 0
    /**
     * @return the sum of the flexGrow properties of the children included in this flex line
     */
    /** @see .getTotalFlexGrow
     */
    var totalFlexGrow = 0f
    /**
     * @return the sum of the flexShrink properties of the children included in this flex line
     */
    /** @see .getTotalFlexShrink
     */
    var totalFlexShrink = 0f

    /**
     * The largest value of the individual child's baseline (obtained by View#getBaseline()
     * if the [FlexContainer.getAlignItems] value is not [AlignItems.BASELINE]
     * or the flex direction is vertical, this value is not used.
     * If the alignment direction is from the bottom to top,
     * (e.g. flexWrap == WRAP_REVERSE and flexDirection == ROW)
     * store this value from the distance from the bottom of the view minus baseline.
     * (Calculated as view.getMeasuredHeight() - view.getBaseline - LayoutParams.bottomMargin)
     */
    var mMaxBaseline = 0

    /**
     * The sum of the cross size used before this flex line.
     */
    var mSumCrossSizeBefore = 0

    /**
     * Store the indices of the children views whose alignSelf property is stretch.
     * The stored indices are the absolute indices including all children in the Flexbox,
     * not the relative indices in this flex line.
     */
    var mIndicesAlignSelfStretch: ArrayList<Int> = ArrayList()

    /**
     * @return the first view's index included in this flex line.
     */
    var firstIndex = 0
    var mLastIndex = 0

    /**
     * Set to true if any [FlexItem]s in this line have [FlexItem.getFlexGrow]
     * attributes set (have the value other than [FlexItem.FLEX_GROW_DEFAULT])
     */
    var mAnyItemsHaveFlexGrow = false

    /**
     * Set to true if any [FlexItem]s in this line have [FlexItem.getFlexShrink]
     * attributes set (have the value other than [FlexItem.FLEX_SHRINK_NOT_SET])
     */
    var mAnyItemsHaveFlexShrink = false

    /**
     * @return the count of the views whose visibilities are not gone in this flex line.
     */
    val itemCountNotGone: Int
        get() = itemCount - mGoneItemCount

    /**
     * Updates the position of the flex line from the contained view.
     *
     * @param view             the view contained in this flex line
     * @param leftDecoration   the length of the decoration on the left of the view
     * @param topDecoration    the length of the decoration on the top of the view
     * @param rightDecoration  the length of the decoration on the right of the view
     * @param bottomDecoration the length of the decoration on the bottom of the view
     */
    fun updatePositionFromView(
        view: View, leftDecoration: Int, topDecoration: Int,
        rightDecoration: Int, bottomDecoration: Int
    ) {
        val flexItem = view.layoutParams as FlexItem
        mLeft = mLeft.coerceAtMost(view.left - flexItem.marginLeft - leftDecoration)
        mTop = mTop.coerceAtMost(view.top - flexItem.marginTop - topDecoration)
        mRight = mRight.coerceAtLeast(view.right + flexItem.marginRight + rightDecoration)
        mBottom = mBottom.coerceAtLeast(view.bottom + flexItem.marginBottom + bottomDecoration)
    }
}