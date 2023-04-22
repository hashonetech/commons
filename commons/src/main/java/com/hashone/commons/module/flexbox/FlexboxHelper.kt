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

import android.graphics.drawable.Drawable
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CompoundButton
import androidx.annotation.VisibleForTesting
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Arrays
import java.util.Collections

/**
 * Offers various calculations for Flexbox to use the common logic between the classes such as
 * [FlexboxLayout] and [FlexboxLayoutManager].
 */
internal class FlexboxHelper(private val mFlexContainer: FlexContainer) {
    /**
     * Holds the 'frozen' state of children during measure. If a view is frozen it will no longer
     * expand or shrink regardless of flex grow/flex shrink attributes.
     */
    private var mChildrenFrozen: BooleanArray? = null

    /**
     * Map the view index to the flex line which contains the view represented by the index to
     * look for a flex line from a given view index in a constant time.
     * Key: index of the view
     * Value: index of the flex line that contains the given view
     *
     * E.g. if we have following flex lines,
     *
     *
     * FlexLine(0): itemCount 3
     * FlexLine(1): itemCount 2
     *
     * this instance should have following entries
     *
     *
     * [0, 0, 0, 1, 1, ...]
     *
     */
    var mIndexToFlexLine: IntArray? = null

    /**
     * Cache the measured spec. The first 32 bit represents the height measure spec, the last
     * 32 bit represents the width measure spec of each flex item.
     * E.g. an entry is created like `(long) heightMeasureSpec << 32 | widthMeasureSpec`
     *
     * To retrieve a widthMeasureSpec, call [.extractLowerInt] or
     * [.extractHigherInt] for a heightMeasureSpec.
     */
    var mMeasureSpecCache: LongArray? = null

    /**
     * Cache a flex item's measured width and height. The first 32 bit represents the height, the
     * last 32 bit represents the width of each flex item.
     * E.g. an entry is created like the following code.
     * `(long) view.getMeasuredHeight() << 32 | view.getMeasuredWidth()`
     *
     * To retrieve a width value, call [.extractLowerInt] or
     * [.extractHigherInt] for a height value.
     */
    private var mMeasuredSizeCache: LongArray? = null

    /**
     * Create an array, which indicates the reordered indices that
     * [FlexItem.getOrder] attributes are taken into account.
     * This method takes a View before that is added as the parent ViewGroup's children.
     *
     * @param viewBeforeAdded          the View instance before added to the array of children
     * Views of the parent ViewGroup
     * @param indexForViewBeforeAdded  the index for the View before added to the array of the
     * parent ViewGroup
     * @param paramsForViewBeforeAdded the layout parameters for the View before added to the array
     * of the parent ViewGroup
     * @return an array which have the reordered indices
     */
    fun createReorderedIndices(
        viewBeforeAdded: View?, indexForViewBeforeAdded: Int,
        paramsForViewBeforeAdded: ViewGroup.LayoutParams, orderCache: SparseIntArray
    ): IntArray {
        val childCount = mFlexContainer.flexItemCount
        val orders = createOrders(childCount)
        val orderForViewToBeAdded = Order()
        if (viewBeforeAdded != null
            && paramsForViewBeforeAdded is FlexItem
        ) {
            orderForViewToBeAdded.order = (paramsForViewBeforeAdded as FlexItem).order
        } else {
            orderForViewToBeAdded.order = FlexItem.ORDER_DEFAULT
        }
        if (indexForViewBeforeAdded == -1 || indexForViewBeforeAdded == childCount) {
            orderForViewToBeAdded.index = childCount
        } else if (indexForViewBeforeAdded < mFlexContainer.flexItemCount) {
            orderForViewToBeAdded.index = indexForViewBeforeAdded
            for (i in indexForViewBeforeAdded until childCount) {
                orders[i].index++
            }
        } else {
            // This path is not expected since OutOfBoundException will be thrown in the ViewGroup
            // But setting the index for fail-safe
            orderForViewToBeAdded.index = childCount
        }
        orders.add(orderForViewToBeAdded)
        return sortOrdersIntoReorderedIndices(childCount + 1, orders, orderCache)
    }

    /**
     * Create an array, which indicates the reordered indices that
     * [FlexItem.getOrder] attributes are taken into account.
     *
     * @return @return an array which have the reordered indices
     */
    fun createReorderedIndices(orderCache: SparseIntArray): IntArray {
        val childCount = mFlexContainer.flexItemCount
        val orders: List<Order> = createOrders(childCount)
        return sortOrdersIntoReorderedIndices(childCount, orders, orderCache)
    }

    private fun createOrders(childCount: Int): MutableList<Order> {
        val orders: MutableList<Order> = ArrayList(childCount)
        for (i in 0 until childCount) {
            val child = mFlexContainer.getFlexItemAt(i)
            val flexItem = child?.layoutParams as FlexItem
            val order = Order()
            order.order = flexItem.order
            order.index = i
            orders.add(order)
        }
        return orders
    }

    /**
     * Returns if any of the children's [FlexItem.getOrder] attributes are
     * changed from the last measurement.
     *
     * @return `true` if changed from the last measurement, `false` otherwise.
     */
    fun isOrderChangedFromLastMeasurement(orderCache: SparseIntArray): Boolean {
        val childCount = mFlexContainer.flexItemCount
        if (orderCache.size() != childCount) {
            return true
        }
        for (i in 0 until childCount) {
            val view = mFlexContainer.getFlexItemAt(i) ?: continue
            val flexItem = view.layoutParams as FlexItem
            if (flexItem.order != orderCache.get(i)) {
                return true
            }
        }
        return false
    }

    private fun sortOrdersIntoReorderedIndices(
        childCount: Int, orders: List<Order>,
        orderCache: SparseIntArray
    ): IntArray {
        Collections.sort(orders)
        orderCache.clear()
        val reorderedIndices = IntArray(childCount)
        for ((i, order) in orders.withIndex()) {
            reorderedIndices[i] = order.index
            orderCache.append(order.index, order.order)
        }
        return reorderedIndices
    }

    /**
     * Calculate how many flex lines are needed in the flex container.
     * This method should calculate all the flex lines from the existing flex items.
     *
     * @see .calculateFlexLines
     */
    fun calculateHorizontalFlexLines(
        result: FlexLinesResult, widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        calculateFlexLines(
            result, widthMeasureSpec, heightMeasureSpec, Int.MAX_VALUE,
            0, RecyclerView.NO_POSITION, null
        )
    }

    /**
     * Calculate how many flex lines are needed in the flex container.
     * Stop calculating it if the calculated amount along the cross size reaches the argument
     * as the needsCalcAmount.
     *
     * @param result            an instance of [FlexLinesResult] that is going to contain a
     * list of flex lines and the child state used by
     * [View.setMeasuredDimension].
     * @param widthMeasureSpec  the width measure spec imposed by the flex container
     * @param heightMeasureSpec the height measure spec imposed by the flex container
     * @param needsCalcAmount   the amount of pixels where flex line calculation should be stopped
     * this is needed to avoid the expensive calculation if the
     * calculation is needed only the small part of the entire flex
     * container. (E.g. If the flex container is the
     * [FlexboxLayoutManager], the calculation only needs the
     * visible area, imposing the entire calculation may cause bad
     * performance
     * @param fromIndex         the index of the child from which the calculation starts
     * @param existingLines     If not null, calculated flex lines will be added to this instance
     */
    fun calculateHorizontalFlexLines(
        result: FlexLinesResult, widthMeasureSpec: Int,
        heightMeasureSpec: Int, needsCalcAmount: Int, fromIndex: Int,
        existingLines: ArrayList<FlexLine>?
    ) {
        calculateFlexLines(
            result, widthMeasureSpec, heightMeasureSpec, needsCalcAmount,
            fromIndex, RecyclerView.NO_POSITION, existingLines
        )
    }

    /**
     * Calculate how many flex lines are needed in the flex container.
     * This method calculates the amount of pixels as the `needsCalcAmount` in addition to
     * the
     * flex lines which includes the view who has the index as the `toIndex` argument.
     * (First calculate to the toIndex, then calculate the amount of pixels as needsCalcAmount)
     *
     * @param result            an instance of [FlexLinesResult] that is going to contain a
     * list of flex lines and the child state used by
     * [View.setMeasuredDimension].
     * @param widthMeasureSpec  the width measure spec imposed by the flex container
     * @param heightMeasureSpec the height measure spec imposed by the flex container
     * @param needsCalcAmount   the amount of pixels where flex line calculation should be stopped
     * this is needed to avoid the expensive calculation if the
     * calculation is needed only the small part of the entire flex
     * container. (E.g. If the flex container is the
     * [FlexboxLayoutManager], the calculation only needs the
     * visible area, imposing the entire calculation may cause bad
     * performance
     * @param toIndex           the index of the child to which the calculation ends (until the
     * flex line which include the which who has that index). If this
     * and needsCalcAmount are both set, first flex lines are calculated
     * to the index, calculate the amount of pixels as the needsCalcAmount
     * argument in addition to that
     */
    fun calculateHorizontalFlexLinesToIndex(
        result: FlexLinesResult,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        needsCalcAmount: Int,
        toIndex: Int,
        existingLines: ArrayList<FlexLine>?
    ) {
        calculateFlexLines(
            result, widthMeasureSpec, heightMeasureSpec, needsCalcAmount,
            0, toIndex, existingLines
        )
    }

    /**
     * Calculate how many flex lines are needed in the flex container.
     * This method should calculate all the flex lines from the existing flex items.
     *
     * @param result            an instance of [FlexLinesResult] that is going to contain a
     * list of flex lines and the child state used by
     * [View.setMeasuredDimension].
     * @param widthMeasureSpec  the width measure spec imposed by the flex container
     * @param heightMeasureSpec the height measure spec imposed by the flex container
     * @see .calculateFlexLines
     */
    fun calculateVerticalFlexLines(
        result: FlexLinesResult,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        calculateFlexLines(
            result, heightMeasureSpec, widthMeasureSpec, Int.MAX_VALUE,
            0, RecyclerView.NO_POSITION, null
        )
    }

    /**
     * Calculate how many flex lines are needed in the flex container.
     * Stop calculating it if the calculated amount along the cross size reaches the argument
     * as the needsCalcAmount.
     *
     * @param result            an instance of [FlexLinesResult] that is going to contain a
     * list of flex lines and the child state used by
     * [View.setMeasuredDimension].
     * @param widthMeasureSpec  the width measure spec imposed by the flex container
     * @param heightMeasureSpec the height measure spec imposed by the flex container
     * @param needsCalcAmount   the amount of pixels where flex line calculation should be stopped
     * this is needed to avoid the expensive calculation if the
     * calculation is needed only the small part of the entire flex
     * container. (E.g. If the flex container is the
     * [FlexboxLayoutManager], the calculation only needs the
     * visible area, imposing the entire calculation may cause bad
     * performance
     * @param fromIndex         the index of the child from which the calculation starts
     * @param existingLines     If not null, calculated flex lines will be added to this instance
     */
    fun calculateVerticalFlexLines(
        result: FlexLinesResult, widthMeasureSpec: Int,
        heightMeasureSpec: Int, needsCalcAmount: Int, fromIndex: Int,
        existingLines: ArrayList<FlexLine>?
    ) {
        calculateFlexLines(
            result, heightMeasureSpec, widthMeasureSpec, needsCalcAmount,
            fromIndex, RecyclerView.NO_POSITION, existingLines
        )
    }

    /**
     * Calculate how many flex lines are needed in the flex container.
     * This method calculates the amount of pixels as the `needsCalcAmount` in addition to
     * the
     * flex lines which includes the view who has the index as the `toIndex` argument.
     * (First calculate to the toIndex, then calculate the amount of pixels as needsCalcAmount)
     *
     * @param result            an instance of [FlexLinesResult] that is going to contain a
     * list of flex lines and the child state used by
     * [View.setMeasuredDimension].
     * @param widthMeasureSpec  the width measure spec imposed by the flex container
     * @param heightMeasureSpec the height measure spec imposed by the flex container
     * @param needsCalcAmount   the amount of pixels where flex line calculation should be stopped
     * this is needed to avoid the expensive calculation if the
     * calculation is needed only the small part of the entire flex
     * container. (E.g. If the flex container is the
     * [FlexboxLayoutManager], the calculation only needs the
     * visible area, imposing the entire calculation may cause bad
     * performance
     * @param toIndex           the index of the child to which the calculation ends (until the
     * flex line which include the which who has that index). If this
     * and needsCalcAmount are both set, first flex lines are calculated
     * to the index, calculate the amount of pixels as the needsCalcAmount
     * argument in addition to that
     */
    fun calculateVerticalFlexLinesToIndex(
        result: FlexLinesResult,
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        needsCalcAmount: Int,
        toIndex: Int,
        existingLines: ArrayList<FlexLine>?
    ) {
        calculateFlexLines(
            result, heightMeasureSpec, widthMeasureSpec, needsCalcAmount,
            0, toIndex, existingLines
        )
    }

    /**
     * Calculates how many flex lines are needed in the flex container layout by measuring each
     * child.
     * Expanding or shrinking the flex items depending on the flex grow and flex shrink
     * attributes are done in a later procedure, so the views' measured width and measured
     * height may be changed in a later process.
     *
     * @param result           an instance of [FlexLinesResult] that is going to contain a
     * list of flex lines and the child state used by
     * [View.setMeasuredDimension].
     * @param mainMeasureSpec  the main axis measure spec imposed by the flex container,
     * width for horizontal direction, height otherwise
     * @param crossMeasureSpec the cross axis measure spec imposed by the flex container,
     * height for horizontal direction, width otherwise
     * @param needsCalcAmount  the amount of pixels where flex line calculation should be stopped
     * this is needed to avoid the expensive calculation if the
     * calculation is needed only the small part of the entire flex
     * container. (E.g. If the flex container is the
     * [FlexboxLayoutManager], the calculation only needs the
     * visible area, imposing the entire calculation may cause bad
     * performance
     * @param fromIndex        the index of the child from which the calculation starts
     * @param toIndex          the index of the child to which the calculation ends (until the
     * flex line which include the which who has that index). If this
     * and needsCalcAmount are both set, first flex lines are calculated
     * to the index, calculate the amount of pixels as the needsCalcAmount
     * argument in addition to that
     * @param existingLines    If not null, calculated flex lines will be added to this instance
     */
    fun calculateFlexLines(
        result: FlexLinesResult, mainMeasureSpec: Int,
        crossMeasureSpec: Int, needsCalcAmount: Int, fromIndex: Int, toIndex: Int,
        existingLines: ArrayList<FlexLine>?
    ) {
        val isMainHorizontal = mFlexContainer.isMainAxisDirectionHorizontal
        val mainMode = View.MeasureSpec.getMode(mainMeasureSpec)
        val mainSize = View.MeasureSpec.getSize(mainMeasureSpec)
        var childState = 0
        val flexLines: ArrayList<FlexLine> = (existingLines ?: ArrayList())
        result.mFlexLines = flexLines
        var reachedToIndex = toIndex == RecyclerView.NO_POSITION
        val mainPaddingStart = getPaddingStartMain(isMainHorizontal)
        val mainPaddingEnd = getPaddingEndMain(isMainHorizontal)
        val crossPaddingStart = getPaddingStartCross(isMainHorizontal)
        val crossPaddingEnd = getPaddingEndCross(isMainHorizontal)
        var largestSizeInCross = Int.MIN_VALUE

        // The amount of cross size calculated in this method call.
        var sumCrossSize = 0

        // The index of the view in the flex line.
        var indexInFlexLine = 0
        var flexLine = FlexLine()
        flexLine.firstIndex = fromIndex
        flexLine.mainSize = mainPaddingStart + mainPaddingEnd
        val childCount = mFlexContainer.flexItemCount
        for (i in fromIndex until childCount) {
            val child = mFlexContainer.getReorderedFlexItemAt(i)
            if (child == null) {
                if (isLastFlexItem(i, childCount, flexLine)) {
                    addFlexLine(flexLines, flexLine, i, sumCrossSize)
                }
                continue
            } else if (child.visibility == View.GONE) {
                flexLine.mGoneItemCount++
                flexLine.itemCount++
                if (isLastFlexItem(i, childCount, flexLine)) {
                    addFlexLine(flexLines, flexLine, i, sumCrossSize)
                }
                continue
            } else if (child is CompoundButton) {
                evaluateMinimumSizeForCompoundButton(child as CompoundButton)
            }
            val flexItem = child.layoutParams as FlexItem
            if (flexItem.alignSelf == AlignItems.STRETCH) {
                flexLine.mIndicesAlignSelfStretch.add(i)
            }
            var childMainSize = getFlexItemSizeMain(flexItem, isMainHorizontal)
            if (flexItem.flexBasisPercent != FlexItem.FLEX_BASIS_PERCENT_DEFAULT
                && mainMode == View.MeasureSpec.EXACTLY
            ) {
                childMainSize = Math.round(mainSize * flexItem.flexBasisPercent)
                // Use the dimension from the layout if the mainMode is not
                // MeasureSpec.EXACTLY even if any fraction value is set to
                // layout_flexBasisPercent.
            }
            var childMainMeasureSpec: Int
            var childCrossMeasureSpec: Int
            if (isMainHorizontal) {
                childMainMeasureSpec = mFlexContainer.getChildWidthMeasureSpec(
                    mainMeasureSpec,
                    mainPaddingStart + mainPaddingEnd +
                            getFlexItemMarginStartMain(flexItem, true) +
                            getFlexItemMarginEndMain(flexItem, true),
                    childMainSize
                )
                childCrossMeasureSpec = mFlexContainer.getChildHeightMeasureSpec(
                    crossMeasureSpec,
                    crossPaddingStart + crossPaddingEnd +
                            getFlexItemMarginStartCross(flexItem, true) +
                            getFlexItemMarginEndCross(flexItem, true)
                            + sumCrossSize,
                    getFlexItemSizeCross(flexItem, true)
                )
                child.measure(childMainMeasureSpec, childCrossMeasureSpec)
                updateMeasureCache(i, childMainMeasureSpec, childCrossMeasureSpec, child)
            } else {
                childCrossMeasureSpec = mFlexContainer.getChildWidthMeasureSpec(
                    crossMeasureSpec,
                    crossPaddingStart + crossPaddingEnd +
                            getFlexItemMarginStartCross(flexItem, false) +
                            getFlexItemMarginEndCross(flexItem, false) + sumCrossSize,
                    getFlexItemSizeCross(flexItem, false)
                )
                childMainMeasureSpec = mFlexContainer.getChildHeightMeasureSpec(
                    mainMeasureSpec,
                    mainPaddingStart + mainPaddingEnd +
                            getFlexItemMarginStartMain(flexItem, false) +
                            getFlexItemMarginEndMain(flexItem, false),
                    childMainSize
                )
                child.measure(childCrossMeasureSpec, childMainMeasureSpec)
                updateMeasureCache(i, childCrossMeasureSpec, childMainMeasureSpec, child)
            }
            mFlexContainer.updateViewCache(i, child)

            // Check the size constraint after the first measurement for the child
            // To prevent the child's width/height violate the size constraints imposed by the
            // {@link FlexItem#getMinWidth()}, {@link FlexItem#getMinHeight()},
            // {@link FlexItem#getMaxWidth()} and {@link FlexItem#getMaxHeight()} attributes.
            // E.g. When the child's layout_width is wrap_content the measured width may be
            // less than the min width after the first measurement.
            checkSizeConstraints(child, i)
            childState = View.combineMeasuredStates(
                childState, child.measuredState
            )
            if (isWrapRequired(
                    child, mainMode, mainSize, flexLine.mainSize,
                    getViewMeasuredSizeMain(child, isMainHorizontal)
                            + getFlexItemMarginStartMain(flexItem, isMainHorizontal) +
                            getFlexItemMarginEndMain(flexItem, isMainHorizontal),
                    flexItem, i, indexInFlexLine, flexLines.size
                )
            ) {
                if (flexLine.itemCountNotGone > 0) {
                    addFlexLine(flexLines, flexLine, if (i > 0) i - 1 else 0, sumCrossSize)
                    sumCrossSize += flexLine.crossSize
                }
                if (isMainHorizontal) {
                    if (flexItem.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                        // This case takes care of the corner case where the cross size of the
                        // child is affected by the just added flex line.
                        // E.g. when the child's layout_height is set to match_parent, the height
                        // of that child needs to be determined taking the total cross size used
                        // so far into account. In that case, the height of the child needs to be
                        // measured again note that we don't need to judge if the wrapping occurs
                        // because it doesn't change the size along the main axis.
                        childCrossMeasureSpec = mFlexContainer.getChildHeightMeasureSpec(
                            crossMeasureSpec,
                            mFlexContainer.paddingTop + mFlexContainer.paddingBottom
                                    + flexItem.marginTop
                                    + flexItem.marginBottom + sumCrossSize,
                            flexItem.height
                        )
                        child.measure(childMainMeasureSpec, childCrossMeasureSpec)
                        checkSizeConstraints(child, i)
                    }
                } else {
                    if (flexItem.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                        // This case takes care of the corner case where the cross size of the
                        // child is affected by the just added flex line.
                        // E.g. when the child's layout_width is set to match_parent, the width
                        // of that child needs to be determined taking the total cross size used
                        // so far into account. In that case, the width of the child needs to be
                        // measured again note that we don't need to judge if the wrapping occurs
                        // because it doesn't change the size along the main axis.
                        childCrossMeasureSpec = mFlexContainer.getChildWidthMeasureSpec(
                            crossMeasureSpec,
                            mFlexContainer.paddingLeft + mFlexContainer.paddingRight
                                    + flexItem.marginLeft
                                    + flexItem.marginRight + sumCrossSize,
                            flexItem.width
                        )
                        child.measure(childCrossMeasureSpec, childMainMeasureSpec)
                        checkSizeConstraints(child, i)
                    }
                }
                flexLine = FlexLine()
                flexLine.itemCount = 1
                flexLine.mainSize = mainPaddingStart + mainPaddingEnd
                flexLine.firstIndex = i
                indexInFlexLine = 0
                largestSizeInCross = Int.MIN_VALUE
            } else {
                flexLine.itemCount++
                indexInFlexLine++
            }
            flexLine.mAnyItemsHaveFlexGrow =
                flexLine.mAnyItemsHaveFlexGrow or (flexItem.flexGrow != FlexItem.FLEX_GROW_DEFAULT)
            flexLine.mAnyItemsHaveFlexShrink =
                flexLine.mAnyItemsHaveFlexShrink or (flexItem.flexShrink != FlexItem.FLEX_SHRINK_NOT_SET)
            if (mIndexToFlexLine != null) {
                mIndexToFlexLine!![i] = flexLines.size
            }
            flexLine.mainSize += (getViewMeasuredSizeMain(child, isMainHorizontal)
                    + getFlexItemMarginStartMain(flexItem, isMainHorizontal) +
                    getFlexItemMarginEndMain(flexItem, isMainHorizontal))
            flexLine.totalFlexGrow += flexItem.flexGrow
            flexLine.totalFlexShrink += flexItem.flexShrink
            mFlexContainer.onNewFlexItemAdded(child, i, indexInFlexLine, flexLine)
            largestSizeInCross = Math.max(
                largestSizeInCross,
                getViewMeasuredSizeCross(child, isMainHorizontal) +
                        getFlexItemMarginStartCross(flexItem, isMainHorizontal) +
                        getFlexItemMarginEndCross(flexItem, isMainHorizontal) +
                        mFlexContainer.getDecorationLengthCrossAxis(child)
            )
            // Temporarily set the cross axis length as the largest child in the flexLine
            // Expand along the cross axis depending on the mAlignContent property if needed
            // later
            flexLine.crossSize = Math.max(flexLine.crossSize, largestSizeInCross)
            if (isMainHorizontal) {
                if (mFlexContainer.flexWrap != FlexWrap.WRAP_REVERSE) {
                    flexLine.mMaxBaseline = Math.max(
                        flexLine.mMaxBaseline,
                        child.baseline + flexItem.marginTop
                    )
                } else {
                    // if the flex wrap property is WRAP_REVERSE, calculate the
                    // baseline as the distance from the cross end and the baseline
                    // since the cross size calculation is based on the distance from the cross end
                    flexLine.mMaxBaseline = Math.max(
                        flexLine.mMaxBaseline, child.measuredHeight - child.baseline
                                + flexItem.marginBottom
                    )
                }
            }
            if (isLastFlexItem(i, childCount, flexLine)) {
                addFlexLine(flexLines, flexLine, i, sumCrossSize)
                sumCrossSize += flexLine.crossSize
            }
            if (toIndex != RecyclerView.NO_POSITION && flexLines.size > 0 && flexLines[flexLines.size - 1].mLastIndex >= toIndex && i >= toIndex && !reachedToIndex) {
                // Calculated to include a flex line which includes the flex item having the
                // toIndex.
                // Let the sumCrossSize start from the negative value of the last flex line's
                // cross size because otherwise flex lines aren't calculated enough to fill the
                // visible area.
                sumCrossSize = -flexLine.crossSize
                reachedToIndex = true
            }
            if (sumCrossSize > needsCalcAmount && reachedToIndex) {
                // Stop the calculation if the sum of cross size calculated reached to the point
                // beyond the needsCalcAmount value to avoid unneeded calculation in a
                // RecyclerView.
                // To be precise, the decoration length may be added to the sumCrossSize,
                // but we omit adding the decoration length because even without the decorator
                // length, it's guaranteed that calculation is done at least beyond the
                // needsCalcAmount
                break
            }
        }
        result.mChildState = childState
    }

    /**
     * Compound buttons (ex. {[android.widget.CheckBox]}, [android.widget.ToggleButton])
     * have a button drawable with minimum height and width specified for them.
     * To align the behavior with CSS Flexbox we want to respect these minimum measurement to avoid
     * these drawables from being cut off during calculation. When the compound button has a minimum
     * width or height already specified we will not make any change since we assume those were
     * voluntarily set by the user.
     *
     * @param compoundButton the compound button that need to be evaluated
     */
    private fun evaluateMinimumSizeForCompoundButton(compoundButton: CompoundButton) {
        val flexItem = compoundButton.layoutParams as FlexItem
        val minWidth = flexItem.minWidth
        val minHeight = flexItem.minHeight
        val drawable: Drawable? = CompoundButtonCompat.getButtonDrawable(compoundButton)
        val drawableMinWidth = drawable?.minimumWidth ?: 0
        val drawableMinHeight = drawable?.minimumHeight ?: 0
        flexItem.minWidth = if (minWidth == FlexContainer.NOT_SET) drawableMinWidth else minWidth
        flexItem.minHeight =
            if (minHeight == FlexContainer.NOT_SET) drawableMinHeight else minHeight
    }

    /**
     * Returns the container's start padding in the main axis. Either start or top.
     *
     * @param isMainHorizontal is the main axis horizontal
     * @return the start padding in the main axis
     */
    private fun getPaddingStartMain(isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            mFlexContainer.paddingStart
        } else mFlexContainer.paddingTop
    }

    /**
     * Returns the container's end padding in the main axis. Either end or bottom.
     *
     * @param isMainHorizontal is the main axis horizontal
     * @return the end padding in the main axis
     */
    private fun getPaddingEndMain(isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            mFlexContainer.paddingEnd
        } else mFlexContainer.paddingBottom
    }

    /**
     * Returns the container's start padding in the cross axis. Either start or top.
     *
     * @param isMainHorizontal is the main axis horizontal.
     * @return the start padding in the cross axis
     */
    private fun getPaddingStartCross(isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            mFlexContainer.paddingTop
        } else mFlexContainer.paddingStart
    }

    /**
     * Returns the container's end padding in the cross axis. Either end or bottom.
     *
     * @param isMainHorizontal is the main axis horizontal
     * @return the end padding in the cross axis
     */
    private fun getPaddingEndCross(isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            mFlexContainer.paddingBottom
        } else mFlexContainer.paddingEnd
    }

    /**
     * Returns the view's measured size in the main axis. Either width or height.
     *
     * @param view             the view
     * @param isMainHorizontal is the main axis horizontal
     * @return the view's measured size in the main axis
     */
    private fun getViewMeasuredSizeMain(view: View, isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            view.measuredWidth
        } else view.measuredHeight
    }

    /**
     * Returns the view's measured size in the cross axis. Either width or height.
     *
     * @param view             the view
     * @param isMainHorizontal is the main axis horizontal
     * @return the view's measured size in the cross axis
     */
    private fun getViewMeasuredSizeCross(view: View, isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            view.measuredHeight
        } else view.measuredWidth
    }

    /**
     * Returns the flexItem's size in the main axis. Either width or height.
     *
     * @param flexItem         the flexItem
     * @param isMainHorizontal is the main axis horizontal
     * @return the flexItem's size in the main axis
     */
    private fun getFlexItemSizeMain(flexItem: FlexItem, isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            flexItem.width
        } else flexItem.height
    }

    /**
     * Returns the flexItem's size in the cross axis. Either width or height.
     *
     * @param flexItem         the flexItem
     * @param isMainHorizontal is the main axis horizontal
     * @return the flexItem's size in the cross axis
     */
    private fun getFlexItemSizeCross(flexItem: FlexItem, isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            flexItem.height
        } else flexItem.width
    }

    /**
     * Returns the flexItem's start margin in the main axis. Either start or top.
     * For the backward compatibility for API level < 17, the horizontal margin is returned using
     * [FlexItem.getMarginLeft] (ViewGroup.MarginLayoutParams#getMarginStart isn't available
     * in API level < 17). Thus this method needs to be used with [.getFlexItemMarginEndMain]
     * not to misuse the margin in RTL.
     *
     *
     * @param flexItem         the flexItem
     * @param isMainHorizontal is the main axis horizontal
     * @return the flexItem's start margin in the main axis
     */
    private fun getFlexItemMarginStartMain(flexItem: FlexItem, isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            flexItem.marginLeft
        } else flexItem.marginTop
    }

    /**
     * Returns the flexItem's end margin in the main axis. Either end or bottom.
     * For the backward compatibility for API level < 17, the horizontal margin is returned using
     * [FlexItem.getMarginRight] (ViewGroup.MarginLayoutParams#getMarginEnd isn't available
     * in API level < 17). Thus this method needs to be used with
     * [.getFlexItemMarginStartMain] not to misuse the margin in RTL.
     *
     * @param flexItem         the flexItem
     * @param isMainHorizontal is the main axis horizontal
     * @return the flexItem's end margin in the main axis
     */
    private fun getFlexItemMarginEndMain(flexItem: FlexItem, isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            flexItem.marginRight
        } else flexItem.marginBottom
    }

    /**
     * Returns the flexItem's start margin in the cross axis. Either start or top.
     * For the backward compatibility for API level < 17, the horizontal margin is returned using
     * [FlexItem.getMarginLeft] (ViewGroup.MarginLayoutParams#getMarginStart isn't available
     * in API level < 17). Thus this method needs to be used with
     * [.getFlexItemMarginEndCross] to not to misuse the margin in RTL.
     *
     * @param flexItem         the flexItem
     * @param isMainHorizontal is the main axis horizontal
     * @return the flexItem's start margin in the cross axis
     */
    private fun getFlexItemMarginStartCross(flexItem: FlexItem, isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            flexItem.marginTop
        } else flexItem.marginLeft
    }

    /**
     * Returns the flexItem's end margin in the cross axis. Either end or bottom.
     * For the backward compatibility for API level < 17, the horizontal margin is returned using
     * [FlexItem.getMarginRight] (ViewGroup.MarginLayoutParams#getMarginEnd isn't available
     * in API level < 17). Thus this method needs to be used with
     * [.getFlexItemMarginStartCross] to not to misuse the margin in RTL.
     *
     * @param flexItem         the flexItem
     * @param isMainHorizontal is the main axis horizontal
     * @return the flexItem's end margin in the cross axis
     */
    private fun getFlexItemMarginEndCross(flexItem: FlexItem, isMainHorizontal: Boolean): Int {
        return if (isMainHorizontal) {
            flexItem.marginBottom
        } else flexItem.marginRight
    }

    /**
     * Determine if a wrap is required (add a new flex line).
     *
     * @param view          the view being judged if the wrap required
     * @param mode          the width or height mode along the main axis direction
     * @param maxSize       the max size along the main axis direction
     * @param currentLength the accumulated current length
     * @param childLength   the length of a child view which is to be collected to the flex line
     * @param flexItem      the LayoutParams for the view being determined whether a new flex line
     * is needed
     * @param index         the index of the view being added within the entire flex container
     * @param indexInFlexLine the index of the view being added within the current flex line
     * @param flexLinesSize the number of the existing flexlines size
     * @return `true` if a wrap is required, `false` otherwise
     * @see FlexContainer.getFlexWrap
     * @see FlexContainer.setFlexWrap
     */
    private fun isWrapRequired(
        view: View, mode: Int, maxSize: Int, currentLength: Int,
        childLength: Int, flexItem: FlexItem, index: Int, indexInFlexLine: Int, flexLinesSize: Int
    ): Boolean {
        var childLength = childLength
        if (mFlexContainer.flexWrap == FlexWrap.NOWRAP) {
            return false
        }
        if (flexItem.isWrapBefore) {
            return true
        }
        if (mode == View.MeasureSpec.UNSPECIFIED) {
            return false
        }
        val maxLine = mFlexContainer.maxLine
        // Judge the condition by adding 1 to the current flexLinesSize because the flex line
        // being computed isn't added to the flexLinesSize.
        if (maxLine != FlexContainer.NOT_SET && maxLine <= flexLinesSize + 1) {
            return false
        }
        val decorationLength =
            mFlexContainer.getDecorationLengthMainAxis(view, index, indexInFlexLine)
        if (decorationLength > 0) {
            childLength += decorationLength
        }
        return maxSize < currentLength + childLength
    }

    private fun isLastFlexItem(
        childIndex: Int, childCount: Int,
        flexLine: FlexLine
    ): Boolean {
        return childIndex == childCount - 1 && flexLine.itemCountNotGone != 0
    }

    private fun addFlexLine(
        flexLines: MutableList<FlexLine>, flexLine: FlexLine, viewIndex: Int,
        usedCrossSizeSoFar: Int
    ) {
        flexLine.mSumCrossSizeBefore = usedCrossSizeSoFar
        mFlexContainer.onNewFlexLineAdded(flexLine)
        flexLine.mLastIndex = viewIndex
        flexLines.add(flexLine)
    }

    /**
     * Checks if the view's width/height don't violate the minimum/maximum size constraints imposed
     * by the [FlexItem.getMinWidth], [FlexItem.getMinHeight],
     * [FlexItem.getMaxWidth] and [FlexItem.getMaxHeight] attributes.
     *
     * @param view  the view to be checked
     * @param index index of the view
     */
    private fun checkSizeConstraints(view: View, index: Int) {
        var needsMeasure = false
        val flexItem = view.layoutParams as FlexItem
        var childWidth = view.measuredWidth
        var childHeight = view.measuredHeight
        if (childWidth < flexItem.minWidth) {
            needsMeasure = true
            childWidth = flexItem.minWidth
        } else if (childWidth > flexItem.maxWidth) {
            needsMeasure = true
            childWidth = flexItem.maxWidth
        }
        if (childHeight < flexItem.minHeight) {
            needsMeasure = true
            childHeight = flexItem.minHeight
        } else if (childHeight > flexItem.maxHeight) {
            needsMeasure = true
            childHeight = flexItem.maxHeight
        }
        if (needsMeasure) {
            val widthSpec = View.MeasureSpec.makeMeasureSpec(childWidth, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec
                .makeMeasureSpec(childHeight, View.MeasureSpec.EXACTLY)
            view.measure(widthSpec, heightSpec)
            updateMeasureCache(index, widthSpec, heightSpec, view)
            mFlexContainer.updateViewCache(index, view)
        }
    }
    /**
     * Determine the main size by expanding (shrinking if negative remaining free space is given)
     * an individual child in each flex line if any children's mFlexGrow (or mFlexShrink if
     * remaining
     * space is negative) properties are set to non-zero.
     *
     * @param widthMeasureSpec  horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec vertical space requirements as imposed by the parent
     * @see FlexContainer.setFlexDirection
     * @see FlexContainer.getFlexDirection
     */
    /**
     * @see .determineMainSize
     */
    @JvmOverloads
    fun determineMainSize(widthMeasureSpec: Int, heightMeasureSpec: Int, fromIndex: Int = 0) {
        ensureChildrenFrozen(mFlexContainer.flexItemCount)
        if (fromIndex >= mFlexContainer.flexItemCount) {
            return
        }
        val mainSize: Int
        val paddingAlongMainAxis: Int
        val flexDirection = mFlexContainer.flexDirection
        when (mFlexContainer.flexDirection) {
            FlexDirection.ROW, FlexDirection.ROW_REVERSE -> {
                val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
                val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
                val largestMainSize = mFlexContainer.largestMainSize
                mainSize = if (widthMode == View.MeasureSpec.EXACTLY) {
                    widthSize
                } else {
                    Math.min(largestMainSize, widthSize)
                }
                paddingAlongMainAxis = (mFlexContainer.paddingLeft
                        + mFlexContainer.paddingRight)
            }

            FlexDirection.COLUMN, FlexDirection.COLUMN_REVERSE -> {
                val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
                val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
                mainSize = if (heightMode == View.MeasureSpec.EXACTLY) {
                    heightSize
                } else {
                    mFlexContainer.largestMainSize
                }
                paddingAlongMainAxis = (mFlexContainer.paddingTop
                        + mFlexContainer.paddingBottom)
            }

            else -> throw IllegalArgumentException("Invalid flex direction: $flexDirection")
        }
        var flexLineIndex = 0
        if (mIndexToFlexLine != null) {
            flexLineIndex = mIndexToFlexLine!![fromIndex]
        }
        val flexLines = mFlexContainer.flexLinesInternal
        var i = flexLineIndex
        val size = flexLines.size
        while (i < size) {
            val flexLine = flexLines[i]
            if (flexLine.mainSize < mainSize && flexLine.mAnyItemsHaveFlexGrow) {
                expandFlexItems(
                    widthMeasureSpec, heightMeasureSpec, flexLine,
                    mainSize, paddingAlongMainAxis, false
                )
            } else if (flexLine.mainSize > mainSize && flexLine.mAnyItemsHaveFlexShrink) {
                shrinkFlexItems(
                    widthMeasureSpec, heightMeasureSpec, flexLine,
                    mainSize, paddingAlongMainAxis, false
                )
            }
            i++
        }
    }

    private fun ensureChildrenFrozen(size: Int) {
        if (mChildrenFrozen == null) {
            mChildrenFrozen = BooleanArray(Math.max(size, INITIAL_CAPACITY))
        } else if (mChildrenFrozen!!.size < size) {
            val newCapacity = mChildrenFrozen!!.size * 2
            mChildrenFrozen = BooleanArray(Math.max(newCapacity, size))
        } else {
            Arrays.fill(mChildrenFrozen, false)
        }
    }

    /**
     * Expand the flex items along the main axis based on the individual mFlexGrow attribute.
     *
     * @param widthMeasureSpec     the horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec    the vertical space requirements as imposed by the parent
     * @param flexLine             the flex line to which flex items belong
     * @param maxMainSize          the maximum main size. Expanded main size will be this size
     * @param paddingAlongMainAxis the padding value along the main axis
     * @param calledRecursively    true if this method is called recursively, false otherwise
     * @see FlexContainer.getFlexDirection
     * @see FlexContainer.setFlexDirection
     * @see FlexItem.getFlexGrow
     */
    private fun expandFlexItems(
        widthMeasureSpec: Int, heightMeasureSpec: Int, flexLine: FlexLine,
        maxMainSize: Int, paddingAlongMainAxis: Int, calledRecursively: Boolean
    ) {
        if (flexLine.totalFlexGrow <= 0 || maxMainSize < flexLine.mainSize) {
            return
        }
        val sizeBeforeExpand = flexLine.mainSize
        var needsReexpand = false
        val unitSpace = (maxMainSize - flexLine.mainSize) / flexLine.totalFlexGrow
        flexLine.mainSize = paddingAlongMainAxis + flexLine.mDividerLengthInMainSize

        // Setting the cross size of the flex line as the temporal value since the cross size of
        // each flex item may be changed from the initial calculation
        // (in the measureHorizontal/measureVertical method) even this method is part of the main
        // size determination.
        // E.g. If a TextView's layout_width is set to 0dp, layout_height is set to wrap_content,
        // and layout_flexGrow is set to 1, the TextView is trying to expand to the vertical
        // direction to enclose its content (in the measureHorizontal method), but
        // the width will be expanded in this method. In that case, the height needs to be measured
        // again with the expanded width.
        var largestCrossSize = 0
        if (!calledRecursively) {
            flexLine.crossSize = Int.MIN_VALUE
        }
        var accumulatedRoundError = 0f
        for (i in 0 until flexLine.itemCount) {
            val index = flexLine.firstIndex + i
            val child = mFlexContainer.getReorderedFlexItemAt(index)
            if (child == null || child.visibility == View.GONE) {
                continue
            }
            val flexItem = child.layoutParams as FlexItem
            val flexDirection = mFlexContainer.flexDirection
            if (flexDirection == FlexDirection.ROW || flexDirection == FlexDirection.ROW_REVERSE) {
                // The direction of the main axis is horizontal
                var childMeasuredWidth = child.measuredWidth
                if (mMeasuredSizeCache != null) {
                    // Retrieve the measured width from the cache because there
                    // are some cases that the view is re-created from the last measure, thus
                    // View#getMeasuredWidth returns 0.
                    // E.g. if the flex container is FlexboxLayoutManager, the case happens
                    // frequently
                    childMeasuredWidth = extractLowerInt(mMeasuredSizeCache!![index])
                }
                var childMeasuredHeight = child.measuredHeight
                if (mMeasuredSizeCache != null) {
                    // Extract the measured height from the cache
                    childMeasuredHeight = extractHigherInt(mMeasuredSizeCache!![index])
                }
                if (!mChildrenFrozen!![index] && flexItem.flexGrow > 0f) {
                    var rawCalculatedWidth = (childMeasuredWidth
                            + unitSpace * flexItem.flexGrow)
                    if (i == flexLine.itemCount - 1) {
                        rawCalculatedWidth += accumulatedRoundError
                        accumulatedRoundError = 0f
                    }
                    var newWidth = Math.round(rawCalculatedWidth)
                    if (newWidth > flexItem.maxWidth) {
                        // This means the child can't expand beyond the value of the mMaxWidth
                        // attribute.
                        // To adjust the flex line length to the size of maxMainSize, remaining
                        // positive free space needs to be re-distributed to other flex items
                        // (children views). In that case, invoke this method again with the same
                        // fromIndex.
                        needsReexpand = true
                        newWidth = flexItem.maxWidth
                        mChildrenFrozen!![index] = true
                        flexLine.totalFlexGrow -= flexItem.flexGrow
                    } else {
                        accumulatedRoundError += rawCalculatedWidth - newWidth
                        if (accumulatedRoundError > 1.0) {
                            newWidth += 1
                            accumulatedRoundError -= 1.0.toFloat()
                        } else if (accumulatedRoundError < -1.0) {
                            newWidth -= 1
                            accumulatedRoundError += 1.0.toFloat()
                        }
                    }
                    val childHeightMeasureSpec = getChildHeightMeasureSpecInternal(
                        heightMeasureSpec, flexItem, flexLine.mSumCrossSizeBefore
                    )
                    val childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        newWidth,
                        View.MeasureSpec.EXACTLY
                    )
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                    childMeasuredWidth = child.measuredWidth
                    childMeasuredHeight = child.measuredHeight
                    updateMeasureCache(
                        index, childWidthMeasureSpec, childHeightMeasureSpec,
                        child
                    )
                    mFlexContainer.updateViewCache(index, child)
                }
                largestCrossSize = Math.max(
                    largestCrossSize, childMeasuredHeight
                            + flexItem.marginTop + flexItem.marginBottom
                            + mFlexContainer.getDecorationLengthCrossAxis(child)
                )
                flexLine.mainSize += (childMeasuredWidth + flexItem.marginLeft
                        + flexItem.marginRight)
            } else {
                // The direction of the main axis is vertical
                var childMeasuredHeight = child.measuredHeight
                if (mMeasuredSizeCache != null) {
                    // Retrieve the measured height from the cache because there
                    // are some cases that the view is re-created from the last measure, thus
                    // View#getMeasuredHeight returns 0.
                    // E.g. if the flex container is FlexboxLayoutManager, that case happens
                    // frequently
                    childMeasuredHeight = extractHigherInt(mMeasuredSizeCache!![index])
                }
                var childMeasuredWidth = child.measuredWidth
                if (mMeasuredSizeCache != null) {
                    // Extract the measured width from the cache
                    childMeasuredWidth = extractLowerInt(mMeasuredSizeCache!![index])
                }
                if (!mChildrenFrozen!![index] && flexItem.flexGrow > 0f) {
                    var rawCalculatedHeight = (childMeasuredHeight
                            + unitSpace * flexItem.flexGrow)
                    if (i == flexLine.itemCount - 1) {
                        rawCalculatedHeight += accumulatedRoundError
                        accumulatedRoundError = 0f
                    }
                    var newHeight = Math.round(rawCalculatedHeight)
                    if (newHeight > flexItem.maxHeight) {
                        // This means the child can't expand beyond the value of the mMaxHeight
                        // attribute.
                        // To adjust the flex line length to the size of maxMainSize, remaining
                        // positive free space needs to be re-distributed to other flex items
                        // (children views). In that case, invoke this method again with the same
                        // fromIndex.
                        needsReexpand = true
                        newHeight = flexItem.maxHeight
                        mChildrenFrozen!![index] = true
                        flexLine.totalFlexGrow -= flexItem.flexGrow
                    } else {
                        accumulatedRoundError += rawCalculatedHeight - newHeight
                        if (accumulatedRoundError > 1.0) {
                            newHeight += 1
                            accumulatedRoundError -= 1.0.toFloat()
                        } else if (accumulatedRoundError < -1.0) {
                            newHeight -= 1
                            accumulatedRoundError += 1.0.toFloat()
                        }
                    }
                    val childWidthMeasureSpec = getChildWidthMeasureSpecInternal(
                        widthMeasureSpec,
                        flexItem, flexLine.mSumCrossSizeBefore
                    )
                    val childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        newHeight,
                        View.MeasureSpec.EXACTLY
                    )
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                    childMeasuredWidth = child.measuredWidth
                    childMeasuredHeight = child.measuredHeight
                    updateMeasureCache(
                        index, childWidthMeasureSpec, childHeightMeasureSpec,
                        child
                    )
                    mFlexContainer.updateViewCache(index, child)
                }
                largestCrossSize = Math.max(
                    largestCrossSize, childMeasuredWidth
                            + flexItem.marginLeft + flexItem.marginRight
                            + mFlexContainer.getDecorationLengthCrossAxis(child)
                )
                flexLine.mainSize += (childMeasuredHeight + flexItem.marginTop
                        + flexItem.marginBottom)
            }
            flexLine.crossSize = Math.max(flexLine.crossSize, largestCrossSize)
        }
        if (needsReexpand && sizeBeforeExpand != flexLine.mainSize) {
            // Re-invoke the method with the same flex line to distribute the positive free space
            // that wasn't fully distributed (because of maximum length constraint)
            expandFlexItems(
                widthMeasureSpec, heightMeasureSpec, flexLine, maxMainSize,
                paddingAlongMainAxis, true
            )
        }
    }

    /**
     * Shrink the flex items along the main axis based on the individual mFlexShrink attribute.
     *
     * @param widthMeasureSpec     the horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec    the vertical space requirements as imposed by the parent
     * @param flexLine             the flex line to which flex items belong
     * @param maxMainSize          the maximum main size. Shrank main size will be this size
     * @param paddingAlongMainAxis the padding value along the main axis
     * @param calledRecursively    true if this method is called recursively, false otherwise
     * @see FlexContainer.getFlexDirection
     * @see FlexContainer.setFlexDirection
     * @see FlexItem.getFlexShrink
     */
    private fun shrinkFlexItems(
        widthMeasureSpec: Int, heightMeasureSpec: Int, flexLine: FlexLine,
        maxMainSize: Int, paddingAlongMainAxis: Int, calledRecursively: Boolean
    ) {
        val sizeBeforeShrink = flexLine.mainSize
        if (flexLine.totalFlexShrink <= 0 || maxMainSize > flexLine.mainSize) {
            return
        }
        var needsReshrink = false
        val unitShrink = (flexLine.mainSize - maxMainSize) / flexLine.totalFlexShrink
        var accumulatedRoundError = 0f
        flexLine.mainSize = paddingAlongMainAxis + flexLine.mDividerLengthInMainSize

        // Setting the cross size of the flex line as the temporal value since the cross size of
        // each flex item may be changed from the initial calculation
        // (in the measureHorizontal/measureVertical method) even this method is part of the main
        // size determination.
        // E.g. If a TextView's layout_width is set to 0dp, layout_height is set to wrap_content,
        // and layout_flexGrow is set to 1, the TextView is trying to expand to the vertical
        // direction to enclose its content (in the measureHorizontal method), but
        // the width will be expanded in this method. In that case, the height needs to be measured
        // again with the expanded width.
        var largestCrossSize = 0
        if (!calledRecursively) {
            flexLine.crossSize = Int.MIN_VALUE
        }
        for (i in 0 until flexLine.itemCount) {
            val index = flexLine.firstIndex + i
            val child = mFlexContainer.getReorderedFlexItemAt(index)
            if (child == null || child.visibility == View.GONE) {
                continue
            }
            val flexItem = child.layoutParams as FlexItem
            val flexDirection = mFlexContainer.flexDirection
            if (flexDirection == FlexDirection.ROW || flexDirection == FlexDirection.ROW_REVERSE) {
                // The direction of main axis is horizontal
                var childMeasuredWidth = child.measuredWidth
                if (mMeasuredSizeCache != null) {
                    // Retrieve the measured width from the cache because there
                    // are some cases that the view is re-created from the last measure, thus
                    // View#getMeasuredWidth returns 0.
                    // E.g. if the flex container is FlexboxLayoutManager, the case happens
                    // frequently
                    childMeasuredWidth = extractLowerInt(mMeasuredSizeCache!![index])
                }
                var childMeasuredHeight = child.measuredHeight
                if (mMeasuredSizeCache != null) {
                    // Extract the measured height from the cache
                    childMeasuredHeight = extractHigherInt(mMeasuredSizeCache!![index])
                }
                if (!mChildrenFrozen!![index] && flexItem.flexShrink > 0f) {
                    var rawCalculatedWidth = (childMeasuredWidth
                            - unitShrink * flexItem.flexShrink)
                    if (i == flexLine.itemCount - 1) {
                        rawCalculatedWidth += accumulatedRoundError
                        accumulatedRoundError = 0f
                    }
                    var newWidth = Math.round(rawCalculatedWidth)
                    if (newWidth < flexItem.minWidth) {
                        // This means the child doesn't have enough space to distribute the negative
                        // free space. To adjust the flex line length down to the maxMainSize,
                        // remaining
                        // negative free space needs to be re-distributed to other flex items
                        // (children views). In that case, invoke this method again with the same
                        // fromIndex.
                        needsReshrink = true
                        newWidth = flexItem.minWidth
                        mChildrenFrozen!![index] = true
                        flexLine.totalFlexShrink -= flexItem.flexShrink
                    } else {
                        accumulatedRoundError += rawCalculatedWidth - newWidth
                        if (accumulatedRoundError > 1.0) {
                            newWidth += 1
                            accumulatedRoundError -= 1f
                        } else if (accumulatedRoundError < -1.0) {
                            newWidth -= 1
                            accumulatedRoundError += 1f
                        }
                    }
                    val childHeightMeasureSpec = getChildHeightMeasureSpecInternal(
                        heightMeasureSpec, flexItem, flexLine.mSumCrossSizeBefore
                    )
                    val childWidthMeasureSpec =
                        View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY)
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                    childMeasuredWidth = child.measuredWidth
                    childMeasuredHeight = child.measuredHeight
                    updateMeasureCache(
                        index, childWidthMeasureSpec, childHeightMeasureSpec,
                        child
                    )
                    mFlexContainer.updateViewCache(index, child)
                }
                largestCrossSize = Math.max(
                    largestCrossSize, childMeasuredHeight +
                            flexItem.marginTop + flexItem.marginBottom +
                            mFlexContainer.getDecorationLengthCrossAxis(child)
                )
                flexLine.mainSize += (childMeasuredWidth + flexItem.marginLeft
                        + flexItem.marginRight)
            } else {
                // The direction of main axis is vertical
                var childMeasuredHeight = child.measuredHeight
                if (mMeasuredSizeCache != null) {
                    // Retrieve the measured height from the cache because there
                    // are some cases that the view is re-created from the last measure, thus
                    // View#getMeasuredHeight returns 0.
                    // E.g. if the flex container is FlexboxLayoutManager, that case happens
                    // frequently
                    childMeasuredHeight = extractHigherInt(mMeasuredSizeCache!![index])
                }
                var childMeasuredWidth = child.measuredWidth
                if (mMeasuredSizeCache != null) {
                    // Extract the measured width from the cache
                    childMeasuredWidth = extractLowerInt(mMeasuredSizeCache!![index])
                }
                if (!mChildrenFrozen!![index] && flexItem.flexShrink > 0f) {
                    var rawCalculatedHeight = (childMeasuredHeight
                            - unitShrink * flexItem.flexShrink)
                    if (i == flexLine.itemCount - 1) {
                        rawCalculatedHeight += accumulatedRoundError
                        accumulatedRoundError = 0f
                    }
                    var newHeight = Math.round(rawCalculatedHeight)
                    if (newHeight < flexItem.minHeight) {
                        // Need to invoke this method again like the case flex direction is vertical
                        needsReshrink = true
                        newHeight = flexItem.minHeight
                        mChildrenFrozen!![index] = true
                        flexLine.totalFlexShrink -= flexItem.flexShrink
                    } else {
                        accumulatedRoundError += rawCalculatedHeight - newHeight
                        if (accumulatedRoundError > 1.0) {
                            newHeight += 1
                            accumulatedRoundError -= 1f
                        } else if (accumulatedRoundError < -1.0) {
                            newHeight -= 1
                            accumulatedRoundError += 1f
                        }
                    }
                    val childWidthMeasureSpec = getChildWidthMeasureSpecInternal(
                        widthMeasureSpec,
                        flexItem, flexLine.mSumCrossSizeBefore
                    )
                    val childHeightMeasureSpec =
                        View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.EXACTLY)
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                    childMeasuredWidth = child.measuredWidth
                    childMeasuredHeight = child.measuredHeight
                    updateMeasureCache(
                        index, childWidthMeasureSpec, childHeightMeasureSpec,
                        child
                    )
                    mFlexContainer.updateViewCache(index, child)
                }
                largestCrossSize = Math.max(
                    largestCrossSize, childMeasuredWidth +
                            flexItem.marginLeft + flexItem.marginRight +
                            mFlexContainer.getDecorationLengthCrossAxis(child)
                )
                flexLine.mainSize += (childMeasuredHeight + flexItem.marginTop
                        + flexItem.marginBottom)
            }
            flexLine.crossSize = Math.max(flexLine.crossSize, largestCrossSize)
        }
        if (needsReshrink && sizeBeforeShrink != flexLine.mainSize) {
            // Re-invoke the method with the same fromIndex to distribute the negative free space
            // that wasn't fully distributed (because some views length were not enough)
            shrinkFlexItems(
                widthMeasureSpec, heightMeasureSpec, flexLine,
                maxMainSize, paddingAlongMainAxis, true
            )
        }
    }

    private fun getChildWidthMeasureSpecInternal(
        widthMeasureSpec: Int, flexItem: FlexItem,
        padding: Int
    ): Int {
        var childWidthMeasureSpec = mFlexContainer.getChildWidthMeasureSpec(
            widthMeasureSpec,
            mFlexContainer.paddingLeft + mFlexContainer.paddingRight +
                    flexItem.marginLeft + flexItem.marginRight + padding,
            flexItem.width
        )
        val childWidth = View.MeasureSpec.getSize(childWidthMeasureSpec)
        if (childWidth > flexItem.maxWidth) {
            childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                flexItem.maxWidth,
                View.MeasureSpec.getMode(childWidthMeasureSpec)
            )
        } else if (childWidth < flexItem.minWidth) {
            childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                flexItem.minWidth,
                View.MeasureSpec.getMode(childWidthMeasureSpec)
            )
        }
        return childWidthMeasureSpec
    }

    private fun getChildHeightMeasureSpecInternal(
        heightMeasureSpec: Int, flexItem: FlexItem,
        padding: Int
    ): Int {
        var childHeightMeasureSpec = mFlexContainer.getChildHeightMeasureSpec(
            heightMeasureSpec,
            mFlexContainer.paddingTop + mFlexContainer.paddingBottom
                    + flexItem.marginTop + flexItem.marginBottom + padding,
            flexItem.height
        )
        val childHeight = View.MeasureSpec.getSize(childHeightMeasureSpec)
        if (childHeight > flexItem.maxHeight) {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                flexItem.maxHeight,
                View.MeasureSpec.getMode(childHeightMeasureSpec)
            )
        } else if (childHeight < flexItem.minHeight) {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                flexItem.minHeight,
                View.MeasureSpec.getMode(childHeightMeasureSpec)
            )
        }
        return childHeightMeasureSpec
    }

    /**
     * Determines the cross size (Calculate the length along the cross axis).
     * Expand the cross size only if the height mode is MeasureSpec.EXACTLY, otherwise
     * use the sum of cross sizes of all flex lines.
     *
     * @param widthMeasureSpec      horizontal space requirements as imposed by the parent
     * @param heightMeasureSpec     vertical space requirements as imposed by the parent
     * @param paddingAlongCrossAxis the padding value for the FlexboxLayout along the cross axis
     * @see FlexContainer.getFlexDirection
     * @see FlexContainer.setFlexDirection
     * @see FlexContainer.getAlignContent
     * @see FlexContainer.setAlignContent
     */
    fun determineCrossSize(
        widthMeasureSpec: Int, heightMeasureSpec: Int,
        paddingAlongCrossAxis: Int
    ) {
        // The MeasureSpec mode along the cross axis
        val mode: Int
        // The MeasureSpec size along the cross axis
        val size: Int
        val flexDirection = mFlexContainer.flexDirection
        when (flexDirection) {
            FlexDirection.ROW, FlexDirection.ROW_REVERSE -> {
                mode = View.MeasureSpec.getMode(heightMeasureSpec)
                size = View.MeasureSpec.getSize(heightMeasureSpec)
            }

            FlexDirection.COLUMN, FlexDirection.COLUMN_REVERSE -> {
                mode = View.MeasureSpec.getMode(widthMeasureSpec)
                size = View.MeasureSpec.getSize(widthMeasureSpec)
            }

            else -> throw IllegalArgumentException("Invalid flex direction: $flexDirection")
        }
        val flexLines = mFlexContainer.flexLinesInternal
        if (mode == View.MeasureSpec.EXACTLY) {
            val totalCrossSize = mFlexContainer.sumOfCrossSize + paddingAlongCrossAxis
            if (flexLines.size == 1) {
                flexLines[0].crossSize = size - paddingAlongCrossAxis
                // alignContent property is valid only if the Flexbox has at least two lines
            } else if (flexLines.size >= 2) {
                when (mFlexContainer.alignContent) {
                    AlignContent.STRETCH -> {
                        if (totalCrossSize >= size) {
                            return
                        }
                        val freeSpaceUnit = (size - totalCrossSize) / flexLines.size.toFloat()
                        var accumulatedError = 0f
                        var i = 0
                        val flexLinesSize = flexLines.size
                        while (i < flexLinesSize) {
                            val flexLine = flexLines[i]
                            var newCrossSizeAsFloat = flexLine.crossSize + freeSpaceUnit
                            if (i == flexLines.size - 1) {
                                newCrossSizeAsFloat += accumulatedError
                                accumulatedError = 0f
                            }
                            var newCrossSize = Math.round(newCrossSizeAsFloat)
                            accumulatedError += newCrossSizeAsFloat - newCrossSize
                            if (accumulatedError > 1) {
                                newCrossSize += 1
                                accumulatedError -= 1f
                            } else if (accumulatedError < -1) {
                                newCrossSize -= 1
                                accumulatedError += 1f
                            }
                            flexLine.crossSize = newCrossSize
                            i++
                        }
                    }

                    AlignContent.SPACE_AROUND -> {
                        if (totalCrossSize >= size) {
                            // If the size of the content is larger than the flex container, the
                            // Flex lines should be aligned center like ALIGN_CONTENT_CENTER
                            mFlexContainer.flexLines = constructFlexLinesForAlignContentCenter(
                                flexLines, size,
                                totalCrossSize
                            ) as ArrayList<FlexLine>
                            return
                        }
                        // The value of free space along the cross axis which needs to be put on top
                        // and below the bottom of each flex line.
                        var spaceTopAndBottom = size - totalCrossSize
                        // The number of spaces along the cross axis
                        val numberOfSpaces = flexLines.size * 2
                        spaceTopAndBottom = spaceTopAndBottom / numberOfSpaces
                        val newFlexLines: MutableList<FlexLine> = ArrayList()
                        val dummySpaceFlexLine = FlexLine()
                        dummySpaceFlexLine.crossSize = spaceTopAndBottom
                        for (flexLine in flexLines) {
                            newFlexLines.add(dummySpaceFlexLine)
                            newFlexLines.add(flexLine)
                            newFlexLines.add(dummySpaceFlexLine)
                        }
                        mFlexContainer.flexLines = newFlexLines as ArrayList<FlexLine>
                    }

                    AlignContent.SPACE_BETWEEN -> {
                        if (totalCrossSize >= size) {
                            return
                        }
                        // The value of free space along the cross axis between each flex line.
                        var spaceBetweenFlexLine = (size - totalCrossSize).toFloat()
                        val numberOfSpaces = flexLines.size - 1
                        spaceBetweenFlexLine /= numberOfSpaces.toFloat()
                        var accumulatedError = 0f
                        val newFlexLines: MutableList<FlexLine> = ArrayList()
                        var i = 0
                        val flexLineSize = flexLines.size
                        while (i < flexLineSize) {
                            val flexLine = flexLines[i]
                            newFlexLines.add(flexLine)
                            if (i != flexLines.size - 1) {
                                val dummySpaceFlexLine = FlexLine()
                                if (i == flexLines.size - 2) {
                                    // The last dummy space block in the flex container.
                                    // Adjust the cross size by the accumulated error.
                                    dummySpaceFlexLine.crossSize = Math
                                        .round(spaceBetweenFlexLine + accumulatedError)
                                    accumulatedError = 0f
                                } else {
                                    dummySpaceFlexLine.crossSize = Math
                                        .round(spaceBetweenFlexLine)
                                }
                                accumulatedError += (spaceBetweenFlexLine
                                        - dummySpaceFlexLine.crossSize)
                                if (accumulatedError > 1) {
                                    dummySpaceFlexLine.crossSize += 1
                                    accumulatedError -= 1f
                                } else if (accumulatedError < -1) {
                                    dummySpaceFlexLine.crossSize -= 1
                                    accumulatedError += 1f
                                }
                                newFlexLines.add(dummySpaceFlexLine)
                            }
                            i++
                        }
                        mFlexContainer.flexLines = newFlexLines as ArrayList<FlexLine>
                    }

                    AlignContent.CENTER -> {
                        mFlexContainer.flexLines = constructFlexLinesForAlignContentCenter(
                            flexLines, size,
                            totalCrossSize
                        ) as ArrayList<FlexLine>
                    }

                    AlignContent.FLEX_END -> {
                        val spaceTop = size - totalCrossSize
                        val dummySpaceFlexLine = FlexLine()
                        dummySpaceFlexLine.crossSize = spaceTop
                        flexLines.add(0, dummySpaceFlexLine)
                    }

                    AlignContent.FLEX_START -> {}
                }
            }
        }
    }

    private fun constructFlexLinesForAlignContentCenter(
        flexLines: List<FlexLine>,
        size: Int, totalCrossSize: Int
    ): List<FlexLine> {
        var spaceAboveAndBottom = size - totalCrossSize
        spaceAboveAndBottom = spaceAboveAndBottom / 2
        val newFlexLines: MutableList<FlexLine> = ArrayList()
        val dummySpaceFlexLine = FlexLine()
        dummySpaceFlexLine.crossSize = spaceAboveAndBottom
        var i = 0
        val flexLineSize = flexLines.size
        while (i < flexLineSize) {
            if (i == 0) {
                newFlexLines.add(dummySpaceFlexLine)
            }
            val flexLine = flexLines[i]
            newFlexLines.add(flexLine)
            if (i == flexLines.size - 1) {
                newFlexLines.add(dummySpaceFlexLine)
            }
            i++
        }
        return newFlexLines
    }

    /**
     * Expand the view if the [FlexContainer.getAlignItems] attribute is set to [ ][AlignItems.STRETCH] or [FlexItem.getAlignSelf] is set as
     * [AlignItems.STRETCH].
     *
     * @param fromIndex the index from which value, stretch is calculated
     * @see FlexContainer.getFlexDirection
     * @see FlexContainer.setFlexDirection
     * @see FlexContainer.getAlignItems
     * @see FlexContainer.setAlignItems
     * @see FlexItem.getAlignSelf
     */
    @JvmOverloads
    fun stretchViews(fromIndex: Int = 0) {
        if (fromIndex >= mFlexContainer.flexItemCount) {
            return
        }
        val flexDirection = mFlexContainer.flexDirection
        if (mFlexContainer.alignItems == AlignItems.STRETCH) {
            var flexLineIndex = 0
            if (mIndexToFlexLine != null) {
                flexLineIndex = mIndexToFlexLine!![fromIndex]
            }
            val flexLines = mFlexContainer.flexLinesInternal
            var i = flexLineIndex
            val size = flexLines.size
            while (i < size) {
                val flexLine = flexLines[i]
                var j = 0
                val itemCount = flexLine.itemCount
                while (j < itemCount) {
                    val viewIndex = flexLine.firstIndex + j
                    if (j >= mFlexContainer.flexItemCount) {
                        j++
                        continue
                    }
                    val view = mFlexContainer.getReorderedFlexItemAt(viewIndex)
                    if (view == null || view.visibility == View.GONE) {
                        j++
                        continue
                    }
                    val flexItem = view.layoutParams as FlexItem
                    if (flexItem.alignSelf != AlignSelf.AUTO &&
                        flexItem.alignSelf != AlignItems.STRETCH
                    ) {
                        j++
                        continue
                    }
                    when (flexDirection) {
                        FlexDirection.ROW, FlexDirection.ROW_REVERSE -> stretchViewVertically(
                            view,
                            flexLine.crossSize,
                            viewIndex
                        )

                        FlexDirection.COLUMN, FlexDirection.COLUMN_REVERSE -> stretchViewHorizontally(
                            view,
                            flexLine.crossSize,
                            viewIndex
                        )

                        else -> throw IllegalArgumentException(
                            "Invalid flex direction: $flexDirection"
                        )
                    }
                    j++
                }
                i++
            }
        } else {
            for (flexLine in mFlexContainer.flexLinesInternal) {
                for (index in flexLine.mIndicesAlignSelfStretch) {
                    val view = mFlexContainer.getReorderedFlexItemAt(index)!!
                    when (flexDirection) {
                        FlexDirection.ROW, FlexDirection.ROW_REVERSE -> stretchViewVertically(
                            view,
                            flexLine.crossSize,
                            index
                        )

                        FlexDirection.COLUMN, FlexDirection.COLUMN_REVERSE -> stretchViewHorizontally(
                            view,
                            flexLine.crossSize,
                            index
                        )

                        else -> throw IllegalArgumentException(
                            "Invalid flex direction: $flexDirection"
                        )
                    }
                }
            }
        }
    }

    /**
     * Expand the view vertically to the size of the crossSize (considering the view margins)
     *
     * @param view      the View to be stretched
     * @param crossSize the cross size
     * @param index     the index of the view
     */
    private fun stretchViewVertically(view: View, crossSize: Int, index: Int) {
        val flexItem = view.layoutParams as FlexItem
        var newHeight = crossSize - flexItem.marginTop - flexItem.marginBottom -
                mFlexContainer.getDecorationLengthCrossAxis(view)
        newHeight = newHeight.coerceAtLeast(flexItem.minHeight)
        newHeight = newHeight.coerceAtMost(flexItem.maxHeight)
        val childWidthSpec: Int
        val measuredWidth: Int = if (mMeasuredSizeCache != null) {
            // Retrieve the measured height from the cache because there
            // are some cases that the view is re-created from the last measure, thus
            // View#getMeasuredHeight returns 0.
            // E.g. if the flex container is FlexboxLayoutManager, that case happens
            // frequently
            extractLowerInt(mMeasuredSizeCache!![index])
        } else {
            view.measuredWidth
        }
        childWidthSpec = View.MeasureSpec.makeMeasureSpec(
            measuredWidth,
            View.MeasureSpec.EXACTLY
        )
        val childHeightSpec = View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.EXACTLY)
        view.measure(childWidthSpec, childHeightSpec)
        updateMeasureCache(index, childWidthSpec, childHeightSpec, view)
        mFlexContainer.updateViewCache(index, view)
    }

    /**
     * Expand the view horizontally to the size of the crossSize (considering the view margins)
     *
     * @param view      the View to be stretched
     * @param crossSize the cross size
     * @param index     the index of the view
     */
    private fun stretchViewHorizontally(view: View, crossSize: Int, index: Int) {
        val flexItem = view.layoutParams as FlexItem
        var newWidth = (crossSize - flexItem.marginLeft - flexItem.marginRight
                - mFlexContainer.getDecorationLengthCrossAxis(view))
        newWidth = Math.max(newWidth, flexItem.minWidth)
        newWidth = Math.min(newWidth, flexItem.maxWidth)
        val childHeightSpec: Int
        val measuredHeight: Int
        measuredHeight = if (mMeasuredSizeCache != null) {
            // Retrieve the measured height from the cache because there
            // are some cases that the view is re-created from the last measure, thus
            // View#getMeasuredHeight returns 0.
            // E.g. if the flex container is FlexboxLayoutManager, that case happens
            // frequently
            extractHigherInt(mMeasuredSizeCache!![index])
        } else {
            view.measuredHeight
        }
        childHeightSpec = View.MeasureSpec.makeMeasureSpec(
            measuredHeight,
            View.MeasureSpec.EXACTLY
        )
        val childWidthSpec = View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY)
        view.measure(childWidthSpec, childHeightSpec)
        updateMeasureCache(index, childWidthSpec, childHeightSpec, view)
        mFlexContainer.updateViewCache(index, view)
    }

    /**
     * Place a single View when the layout direction is horizontal
     * ([FlexContainer.getFlexDirection] is either [FlexDirection.ROW] or
     * [FlexDirection.ROW_REVERSE]).
     *
     * @param view     the View to be placed
     * @param flexLine the [FlexLine] where the View belongs to
     * @param left     the left position of the View, which the View's margin is already taken
     * into account
     * @param top      the top position of the flex line where the View belongs to. The actual
     * View's top position is shifted depending on the flexWrap and alignItems
     * attributes
     * @param right    the right position of the View, which the View's margin is already taken
     * into account
     * @param bottom   the bottom position of the flex line where the View belongs to. The actual
     * View's bottom position is shifted depending on the flexWrap and alignItems
     * attributes
     * @see FlexContainer.getAlignItems
     * @see FlexContainer.setAlignItems
     * @see FlexItem.getAlignSelf
     */
    fun layoutSingleChildHorizontal(
        view: View, flexLine: FlexLine, left: Int, top: Int, right: Int,
        bottom: Int
    ) {
        val flexItem = view.layoutParams as FlexItem
        var alignItems = mFlexContainer.alignItems
        if (flexItem.alignSelf != AlignSelf.AUTO) {
            // Expecting the values for alignItems and mAlignSelf match except for ALIGN_SELF_AUTO.
            // Assigning the mAlignSelf value as alignItems should work.
            alignItems = flexItem.alignSelf
        }
        val crossSize = flexLine.crossSize
        when (alignItems) {
            AlignItems.FLEX_START, AlignItems.STRETCH -> if (mFlexContainer.flexWrap != FlexWrap.WRAP_REVERSE) {
                view.layout(
                    left, top + flexItem.marginTop, right,
                    bottom + flexItem.marginTop
                )
            } else {
                view.layout(
                    left, top - flexItem.marginBottom, right,
                    bottom - flexItem.marginBottom
                )
            }

            AlignItems.BASELINE -> if (mFlexContainer.flexWrap != FlexWrap.WRAP_REVERSE) {
                var marginTop = flexLine.mMaxBaseline - view.baseline
                marginTop = Math.max(marginTop, flexItem.marginTop)
                view.layout(left, top + marginTop, right, bottom + marginTop)
            } else {
                var marginBottom = flexLine.mMaxBaseline - view.measuredHeight + view
                    .baseline
                marginBottom = Math.max(marginBottom, flexItem.marginBottom)
                view.layout(left, top - marginBottom, right, bottom - marginBottom)
            }

            AlignItems.FLEX_END -> if (mFlexContainer.flexWrap != FlexWrap.WRAP_REVERSE) {
                view.layout(
                    left,
                    top + crossSize - view.measuredHeight - flexItem.marginBottom,
                    right, top + crossSize - flexItem.marginBottom
                )
            } else {
                // If the flexWrap == WRAP_REVERSE, the direction of the
                // flexEnd is flipped (from top to bottom).
                view.layout(
                    left,
                    top - crossSize + view.measuredHeight + flexItem.marginTop,
                    right, bottom - crossSize + view.measuredHeight + flexItem
                        .marginTop
                )
            }

            AlignItems.CENTER -> {
                val topFromCrossAxis = (crossSize - view.measuredHeight
                        + flexItem.marginTop - flexItem.marginBottom) / 2
                if (mFlexContainer.flexWrap != FlexWrap.WRAP_REVERSE) {
                    view.layout(
                        left, top + topFromCrossAxis,
                        right, top + topFromCrossAxis + view.measuredHeight
                    )
                } else {
                    view.layout(
                        left, top - topFromCrossAxis,
                        right, top - topFromCrossAxis + view.measuredHeight
                    )
                }
            }
        }
    }

    /**
     * Place a single View when the layout direction is vertical
     * ([FlexContainer.getFlexDirection] is either [FlexDirection.COLUMN] or
     * [FlexDirection.COLUMN_REVERSE]).
     *
     * @param view     the View to be placed
     * @param flexLine the [FlexLine] where the View belongs to
     * @param isRtl    `true` if the layout direction is right to left, `false`
     * otherwise
     * @param left     the left position of the flex line where the View belongs to. The actual
     * View's left position is shifted depending on the isLayoutRtl and alignItems
     * attributes
     * @param top      the top position of the View, which the View's margin is already taken
     * into account
     * @param right    the right position of the flex line where the View belongs to. The actual
     * View's right position is shifted depending on the isLayoutRtl and alignItems
     * attributes
     * @param bottom   the bottom position of the View, which the View's margin is already taken
     * into account
     * @see FlexContainer.getAlignItems
     * @see FlexContainer.setAlignItems
     * @see FlexItem.getAlignSelf
     */
    fun layoutSingleChildVertical(
        view: View, flexLine: FlexLine, isRtl: Boolean,
        left: Int, top: Int, right: Int, bottom: Int
    ) {
        val flexItem = view.layoutParams as FlexItem
        var alignItems = mFlexContainer.alignItems
        if (flexItem.alignSelf != AlignSelf.AUTO) {
            // Expecting the values for alignItems and mAlignSelf match except for ALIGN_SELF_AUTO.
            // Assigning the mAlignSelf value as alignItems should work.
            alignItems = flexItem.alignSelf
        }
        val crossSize = flexLine.crossSize
        when (alignItems) {
            AlignItems.FLEX_START, AlignItems.STRETCH, AlignItems.BASELINE -> if (!isRtl) {
                view.layout(
                    left + flexItem.marginLeft, top,
                    right + flexItem.marginLeft, bottom
                )
            } else {
                view.layout(
                    left - flexItem.marginRight, top,
                    right - flexItem.marginRight, bottom
                )
            }

            AlignItems.FLEX_END -> if (!isRtl) {
                view.layout(
                    left + crossSize - view.measuredWidth - flexItem.marginRight,
                    top,
                    right + crossSize - view.measuredWidth - flexItem.marginRight,
                    bottom
                )
            } else {
                // If the flexWrap == WRAP_REVERSE, the direction of the
                // flexEnd is flipped (from left to right).
                view.layout(
                    left - crossSize + view.measuredWidth + flexItem.marginLeft,
                    top,
                    right - crossSize + view.measuredWidth + flexItem.marginLeft,
                    bottom
                )
            }

            AlignItems.CENTER -> {
                val lp: MarginLayoutParams = view.layoutParams as MarginLayoutParams
                val leftFromCrossAxis: Int = (crossSize - view.measuredWidth
                        + MarginLayoutParamsCompat.getMarginStart(lp)
                        - MarginLayoutParamsCompat.getMarginEnd(lp)) / 2
                if (!isRtl) {
                    view.layout(left + leftFromCrossAxis, top, right + leftFromCrossAxis, bottom)
                } else {
                    view.layout(left - leftFromCrossAxis, top, right - leftFromCrossAxis, bottom)
                }
            }
        }
    }

    fun ensureMeasuredSizeCache(size: Int) {
        if (mMeasuredSizeCache == null) {
            mMeasuredSizeCache = LongArray(Math.max(size, INITIAL_CAPACITY))
        } else if (mMeasuredSizeCache!!.size < size) {
            var newCapacity = mMeasuredSizeCache!!.size * 2
            newCapacity = Math.max(newCapacity, size)
            mMeasuredSizeCache = Arrays.copyOf(mMeasuredSizeCache, newCapacity)
        }
    }

    fun ensureMeasureSpecCache(size: Int) {
        if (mMeasureSpecCache == null) {
            mMeasureSpecCache = LongArray(Math.max(size, INITIAL_CAPACITY))
        } else if (mMeasureSpecCache!!.size < size) {
            var newCapacity = mMeasureSpecCache!!.size * 2
            newCapacity = Math.max(newCapacity, size)
            mMeasureSpecCache = Arrays.copyOf(mMeasureSpecCache, newCapacity)
        }
    }

    /**
     * @param longValue the long value that consists of width and height measure specs
     * @return the int value which consists from the lower 8 bits
     * @see .makeCombinedLong
     */
    fun extractLowerInt(longValue: Long): Int {
        return longValue.toInt()
    }

    /**
     * @param longValue the long value that consists of width and height measure specs
     * @return the int value which consists from the higher 8 bits
     * @see .makeCombinedLong
     */
    fun extractHigherInt(longValue: Long): Int {
        return (longValue shr 32).toInt()
    }

    /**
     * Make a long value from the a width measure spec and a height measure spec.
     * The first 32 bit is used for the height measure spec and the last 32 bit is used for the
     * width measure spec.
     *
     * @param widthMeasureSpec  the width measure spec to consist the result long value
     * @param heightMeasureSpec the height measure spec to consist the result long value
     * @return the combined long value
     * @see .extractLowerInt
     * @see .extractHigherInt
     */
    @VisibleForTesting
    fun makeCombinedLong(widthMeasureSpec: Int, heightMeasureSpec: Int): Long {
        // Suppress sign extension for the low bytes
        return heightMeasureSpec.toLong() shl 32 or (widthMeasureSpec.toLong() and MEASURE_SPEC_WIDTH_MASK)
    }

    private fun updateMeasureCache(
        index: Int, widthMeasureSpec: Int, heightMeasureSpec: Int,
        view: View
    ) {
        if (mMeasureSpecCache != null) {
            mMeasureSpecCache!![index] = makeCombinedLong(
                widthMeasureSpec,
                heightMeasureSpec
            )
        }
        if (mMeasuredSizeCache != null) {
            mMeasuredSizeCache!![index] = makeCombinedLong(
                view.measuredWidth,
                view.measuredHeight
            )
        }
    }

    fun ensureIndexToFlexLine(size: Int) {
        if (mIndexToFlexLine == null) {
            mIndexToFlexLine = IntArray(Math.max(size, INITIAL_CAPACITY))
        } else if (mIndexToFlexLine!!.size < size) {
            var newCapacity = mIndexToFlexLine!!.size * 2
            newCapacity = Math.max(newCapacity, size)
            mIndexToFlexLine = Arrays.copyOf(mIndexToFlexLine, newCapacity)
        }
    }

    /**
     * Clear the from flex lines and the caches from the index passed as an argument.
     *
     * @param flexLines    the flex lines to be cleared
     * @param fromFlexItem the index from which, flex lines are cleared
     */
    fun clearFlexLines(flexLines: ArrayList<FlexLine?>, fromFlexItem: Int) {
        assert(mIndexToFlexLine != null)
        assert(mMeasureSpecCache != null)
        var fromFlexLine = mIndexToFlexLine!![fromFlexItem]
        if (fromFlexLine == RecyclerView.NO_POSITION) {
            fromFlexLine = 0
        }

        // Deleting from the last to avoid unneeded copy it happens when deleting the middle of the
        // item in the ArrayList
        if (flexLines.size > fromFlexLine) {
            flexLines.subList(fromFlexLine, flexLines.size).clear()
        }
        var fillTo = mIndexToFlexLine!!.size - 1
        if (fromFlexItem > fillTo) {
            Arrays.fill(mIndexToFlexLine, RecyclerView.NO_POSITION)
        } else {
            Arrays.fill(mIndexToFlexLine, fromFlexItem, fillTo, RecyclerView.NO_POSITION)
        }
        fillTo = mMeasureSpecCache!!.size - 1
        if (fromFlexItem > fillTo) {
            Arrays.fill(mMeasureSpecCache, 0)
        } else {
            Arrays.fill(mMeasureSpecCache, fromFlexItem, fillTo, 0)
        }
    }

    /**
     * A class that is used for calculating the view order which view's indices and order
     * properties from Flexbox are taken into account.
     */
    private class Order : Comparable<Order> {
        /** [View]'s index  */
        var index = 0

        /** order property in the Flexbox  */
        var order = 0
        override fun compareTo(another: Order): Int {
            return if (order != another.order) {
                order - another.order
            } else index - another.index
        }

        override fun toString(): String {
            return "Order{" +
                    "order=" + order +
                    ", index=" + index +
                    '}'
        }
    }

    internal class FlexLinesResult {
        @JvmField
        var mFlexLines: ArrayList<FlexLine>? = null

        @JvmField
        var mChildState = 0
        fun reset() {
            mFlexLines = null
            mChildState = 0
        }
    }

    companion object {
        private const val INITIAL_CAPACITY = 10
        private const val MEASURE_SPEC_WIDTH_MASK = 0xffffffffL
    }
}