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

import android.os.Parcelable
import android.view.View

/**
 * An interface that has the common behavior as a flex item contained in a flex container.
 * Known classes that implement this interface are [FlexboxLayout.LayoutParams] and
 * [FlexboxLayoutManager.LayoutParams].
 */
internal interface FlexItem : Parcelable {
    /**
     * @return the width attribute of the flex item.
     *
     * The attribute is about how wide the view wants to be. Can be one of the
     * constants MATCH_PARENT(-1) or WRAP_CONTENT(-2), or an exact size.
     */
    /**
     * Sets the width attribute of the flex item.
     *
     * @param width the width attribute. Can be one of the
     * constants MATCH_PARENT(-1) or WRAP_CONTENT(-2), or an exact size.
     */
    var width: Int
    /**
     * @return the height attribute of the flex item.
     *
     * The attribute is about how wide the view wants to be. Can be one of the
     * constants MATCH_PARENT(-1) or WRAP_CONTENT(-2), or an exact size.
     */
    /**
     * Sets the height attribute of the flex item.
     *
     * @param height the height attribute. Can be one of the
     * constants MATCH_PARENT(-1) or WRAP_CONTENT(-2), or an exact size.
     */
    var height: Int
    /**
     * @return the order attribute of the flex item.
     *
     * The attribute can change the ordering of the children views are laid out.
     * By default, children are displayed and laid out in the same order as they appear in the
     * layout XML. If not specified, [.ORDER_DEFAULT] is set as a default value.
     */
    /**
     * Sets the order attribute to the flex item
     *
     * @param order the order attribute
     */
    var order: Int
    /**
     * @return the flex grow attribute of the flex item
     *
     * The attribute determines how much this child will grow if positive free space is
     * distributed relative to the rest of other flex items included in the same flex line.
     * If not specified, [.FLEX_GROW_DEFAULT] is set as a default value.
     */
    /**
     * Sets the flex grow attribute to the flex item
     *
     * @param flexGrow the flex grow attribute
     */
    var flexGrow: Float
    /**
     * @return the flex shrink attribute of the flex item
     *
     * The attribute determines how much this child will shrink if negative free space is
     * distributed relative to the rest of other flex items included in the same flex line.
     * If not specified, [.FLEX_SHRINK_DEFAULT] is set as a default value.
     */
    /**
     * Sets the flex shrink attribute to the flex item
     *
     * @param flexShrink the flex shrink attribute
     */
    var flexShrink: Float
    /**
     * @return the align self attribute of the flex item
     *
     * The attribute determines the alignment along the cross axis (perpendicular to the
     * main axis). The alignment in the same direction can be determined by the
     * align items attribute in the parent, but if this is set to other than
     * [AlignSelf.AUTO], the cross axis alignment is overridden for this child.
     * The value needs to be one of the values in ([AlignSelf.AUTO],
     * [AlignItems.STRETCH], [AlignItems.FLEX_START], [ ][AlignItems.FLEX_END], [AlignItems.CENTER], or [AlignItems.BASELINE]).
     * If not specified, [AlignSelf.AUTO] is set as a default value.
     */
    /**
     * Sets the align self attribute to the flex item
     *
     * @param alignSelf the order attribute
     */
    @get:AlignSelf
    var alignSelf: Int
    /**
     * @return the minimum width attribute of the flex item
     *
     * The attribute determines the minimum width the child can shrink to.
     */
    /**
     * Sets the minimum width attribute to the flex item
     *
     * @param minWidth the order attribute
     */
    var minWidth: Int
    /**
     * @return the minimum height attribute of the flex item
     *
     * The attribute determines the minimum height the child can shrink to.
     */
    /**
     * Sets the minimum height attribute to the flex item
     *
     * @param minHeight the order attribute
     */
    var minHeight: Int
    /**
     * @return the maximum width attribute of the flex item
     *
     * The attribute determines the maximum width the child can expand to.
     */
    /**
     * Sets the maximum width attribute to the flex item
     *
     * @param maxWidth the order attribute
     */
    var maxWidth: Int
    /**
     * @return the maximum height attribute of the flex item
     */
    /**
     * Sets the maximum height attribute to the flex item
     *
     * @param maxHeight the order attribute
     */
    var maxHeight: Int
    /**
     * @return the wrapBefore attribute of the flex item
     *
     * The attribute forces a flex line wrapping. i.e. if this is set to `true` for a
     * flex item, the item will become the first item of the new flex line. (A wrapping happens
     * regardless of the flex items being processed in the the previous flex line)
     * This attribute is ignored if the flex_wrap attribute is set as nowrap.
     * The equivalent attribute isn't defined in the original CSS Flexible Box Module
     * specification, but having this attribute is useful for Android developers to flatten
     * the layouts when building a grid like layout or for a situation where developers want
     * to put a new flex line to make a semantic difference from the previous one, etc.
     */
    /**
     * Sets the wrapBefore attribute to the flex item
     *
     * @param wrapBefore the order attribute
     */
    var isWrapBefore: Boolean
    /**
     * @return the flexBasisPercent attribute of the flex item
     *
     * The attribute determines the initial flex item length in a fraction format relative to its
     * parent.
     * The initial main size of this child View is trying to be expanded as the specified
     * fraction against the parent main size.
     * If this value is set, the length specified from layout_width
     * (or layout_height) is overridden by the calculated value from this attribute.
     * This attribute is only effective when the parent's MeasureSpec mode is
     * MeasureSpec.EXACTLY. The default value is -1, which means not set.
     */
    /**
     * Sets the flex basis percent attribute to the flex item
     *
     * @param flexBasisPercent the order attribute
     */
    var flexBasisPercent: Float

    /**
     * @return the left margin of the flex item.
     */
    val marginLeft: Int

    /**
     * @return the top margin of the flex item.
     */
    val marginTop: Int

    /**
     * @return the right margin of the flex item.
     */
    val marginRight: Int

    /**
     * @return the bottom margin of the flex item.
     */
    val marginBottom: Int

    /**
     * @return the start margin of the flex item depending on its resolved layout direction.
     */
    val marginStart: Int

    /**
     * @return the end margin of the flex item depending on its resolved layout direction.
     */
    val marginEnd: Int

    companion object {
        /** The default value for the order attribute  */
        const val ORDER_DEFAULT = 1

        /** The default value for the flex grow attribute  */
        const val FLEX_GROW_DEFAULT = 0f

        /** The default value for the flex shrink attribute  */
        const val FLEX_SHRINK_DEFAULT = 1f

        /** The value representing the flex shrink attribute is not set   */
        const val FLEX_SHRINK_NOT_SET = 0f

        /** The default value for the flex basis percent attribute  */
        const val FLEX_BASIS_PERCENT_DEFAULT = -1f

        /** The maximum size of the max width and max height attributes  */
        const val MAX_SIZE = Int.MAX_VALUE and View.MEASURED_SIZE_MASK
    }
}