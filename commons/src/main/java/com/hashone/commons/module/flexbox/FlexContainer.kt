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
 * An interface that has the common behavior as the flex container such as [FlexboxLayout]
 * and [FlexboxLayoutManager].
 */
internal interface FlexContainer {
    /**
     * @return the number of flex items contained in the flex container.
     */
    val flexItemCount: Int

    /**
     * Returns a flex item as a View at the given index.
     *
     * @param index the index
     * @return the view at the index
     */
    fun getFlexItemAt(index: Int): View?

    /**
     * Returns a flex item as a View, which is reordered by taking the order attribute into
     * account.
     *
     * @param index the index of the view
     * @return the reordered view, which order attribute is taken into account.
     * If the index is negative or out of bounds of the number of contained views,
     * returns `null`.
     * @see FlexItem.getOrder
     */
    fun getReorderedFlexItemAt(index: Int): View?

    /**
     * Adds the view to the flex container as a flex item.
     *
     * @param view the view to be added
     */
    fun addView(view: View?)

    /**
     * Adds the view to the specified index of the flex container.
     *
     * @param view  the view to be added
     * @param index the index for the view to be added
     */
    fun addView(view: View?, index: Int)

    /**
     * Removes all the views contained in the flex container.
     */
    fun removeAllViews()

    /**
     * Removes the view at the specified index.
     *
     * @param index the index from which the view is removed.
     */
    fun removeViewAt(index: Int)
    /**
     * @return the flex direction attribute of the flex container.
     * @see FlexDirection
     */
    /**
     * Sets the given flex direction attribute to the flex container.
     *
     * @param flexDirection the flex direction value
     * @see FlexDirection
     */
    @get:FlexDirection
    var flexDirection: Int
    /**
     * @return the flex wrap attribute of the flex container.
     * @see FlexWrap
     */
    /**
     * Sets the given flex wrap attribute to the flex container.
     *
     * @param flexWrap the flex wrap value
     * @see FlexWrap
     */
    @get:FlexWrap
    var flexWrap: Int
    /**
     * @return the justify content attribute of the flex container.
     * @see JustifyContent
     */
    /**
     * Sets the given justify content attribute to the flex container.
     *
     * @param justifyContent the justify content value
     * @see JustifyContent
     */
    @get:JustifyContent
    var justifyContent: Int
    /**
     * @return the align content attribute of the flex container.
     * @see AlignContent
     */
    /**
     * Sets the given align content attribute to the flex container.
     *
     * @param alignContent the align content value
     */
    @get:AlignContent
    var alignContent: Int
    /**
     * @return the align items attribute of the flex container.
     * @see AlignItems
     */
    /**
     * Sets the given align items attribute to the flex container.
     *
     * @param alignItems the align items value
     * @see AlignItems
     */
    @get:AlignItems
    var alignItems: Int
    /**
     * @return the flex lines composing this flex container. The overridden method should return a
     * copy of the original list excluding a dummy flex line (flex line that doesn't have any flex
     * items in it but used for the alignment along the cross axis) so that any changes of the
     * returned list are not reflected to the original list.
     */
    /**
     * Sets the list of the flex lines that compose the flex container to the one received as an
     * argument.
     *
     * @param flexLines the list of flex lines
     */
    var flexLines: ArrayList<FlexLine>

    /**
     * Returns true if the main axis is horizontal, false otherwise.
     *
     * @return true if the main axis is horizontal, false otherwise
     */
    val isMainAxisDirectionHorizontal: Boolean

    /**
     * Returns the length of decoration (such as dividers) of the flex item along the main axis.
     *
     * @param view            the view from which the length of the decoration is retrieved
     * @param index           the absolute index of the flex item within the flex container
     * @param indexInFlexLine the relative index of the flex item within the flex line
     * @return the length of the decoration. Note that the length of the flex item itself is not
     * included in the result.
     */
    fun getDecorationLengthMainAxis(view: View?, index: Int, indexInFlexLine: Int): Int

    /**
     * Returns the length of decoration (such as dividers) of the flex item along the cross axis.
     *
     * @param view the view from which the length of the decoration is retrieved
     * @return the length of the decoration. Note that the length of the flex item itself is not
     * included in the result.
     */
    fun getDecorationLengthCrossAxis(view: View?): Int

    /**
     * @return the top padding of the flex container.
     */
    val paddingTop: Int

    /**
     * @return the left padding of the flex container.
     */
    val paddingLeft: Int

    /**
     * @return the right padding of the flex container.
     */
    val paddingRight: Int

    /**
     * @return the bottom padding of the flex container.
     */
    val paddingBottom: Int

    /**
     * @return the start padding of this view depending on its resolved layout direction.
     */
    val paddingStart: Int

    /**
     * @return the end padding of this view depending on its resolved layout direction.
     */
    val paddingEnd: Int

    /**
     * Returns the child measure spec for its width.
     *
     * @param widthSpec      the measure spec for the width imposed by the parent
     * @param padding        the padding along the width for the parent
     * @param childDimension the value of the child dimension
     */
    fun getChildWidthMeasureSpec(widthSpec: Int, padding: Int, childDimension: Int): Int

    /**
     * Returns the child measure spec for its height.
     *
     * @param heightSpec     the measure spec for the height imposed by the parent
     * @param padding        the padding along the height for the parent
     * @param childDimension the value of the child dimension
     */
    fun getChildHeightMeasureSpec(heightSpec: Int, padding: Int, childDimension: Int): Int

    /**
     * @return the largest main size of all flex lines including decorator lengths.
     */
    val largestMainSize: Int

    /**
     * @return the sum of the cross sizes of all flex lines including decorator lengths.
     */
    val sumOfCrossSize: Int

    /**
     * Callback when a new flex item is added to the current container
     *
     * @param view            the view as a flex item which is added
     * @param index           the absolute index of the flex item added
     * @param indexInFlexLine the relative index of the flex item added within the flex line
     * @param flexLine        the flex line where the new flex item is added
     */
    fun onNewFlexItemAdded(view: View?, index: Int, indexInFlexLine: Int, flexLine: FlexLine?)

    /**
     * Callback when a new flex line is added to the current container
     *
     * @param flexLine the new added flex line
     */
    fun onNewFlexLineAdded(flexLine: FlexLine?)
    /**
     * @return the current value of the maximum number of flex lines. If not set, [.NOT_SET]
     * is returned.
     */
    /**
     *
     * @param maxLine the int value, which specifies the maximum number of flex lines
     */
    var maxLine: Int

    /**
     * @return the list of the flex lines including dummy flex lines (flex line that doesn't have
     * any flex items in it but used for the alignment along the cross axis), which aren't included
     * in the [FlexContainer.getFlexLines].
     */
    val flexLinesInternal: ArrayList<FlexLine>

    /**
     * Update the view cache in the flex container.
     *
     * @param position the position of the view to be updated
     * @param view     the view instance
     */
    fun updateViewCache(position: Int, view: View?)

    companion object {
        const val NOT_SET = -1
    }
}