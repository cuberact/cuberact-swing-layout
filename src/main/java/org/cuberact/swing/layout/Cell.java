/* ******************************************************************************
 * Copyright (c) 2011, Nathan Sweet <nathan.sweet@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package org.cuberact.swing.layout;

import java.awt.*;

/**
 * Cell
 * <p>
 * based on EsotericSoftware/TableLayout. Added new features and improved for Swing.
 * </p>
 *
 * @author Michal Nikodim (michal.nikodim@gmail.com), original made by Nathan Sweet
 * @see <a href="https://github.com/EsotericSoftware/tablelayout">EsotericSoftware/TableLayout</a>
 */
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class Cell<T extends Component> {

    public static final int CENTER = 1;
    public static final int TOP = 1 << 1;
    public static final int BOTTOM = 1 << 2;
    public static final int LEFT = 1 << 3;
    public static final int RIGHT = 1 << 4;

    private Integer minWidth, minHeight;
    private Integer prefWidth, prefHeight;
    private Integer maxWidth, maxHeight;
    private boolean rowEnd;
    final T widget;
    int spaceTop, spaceLeft, spaceBottom, spaceRight;
    int padTop, padLeft, padBottom, padRight;
    boolean fillX, fillY;
    boolean expandX, expandY;
    int align;
    int colspan;
    boolean uniformX, uniformY;
    int widgetX, widgetY;
    int widgetWidth, widgetHeight;
    int column, row;
    int cellAboveIndex = -1;
    int computedPadTop, computedPadLeft, computedPadBottom, computedPadRight;

    public Cell() { //default cell
        widget = null;
        minWidth = null;
        minHeight = null;
        prefWidth = null;
        prefHeight = null;
        maxWidth = null;
        maxHeight = null;
        spaceTop = 0;
        spaceLeft = 0;
        spaceBottom = 0;
        spaceRight = 0;
        padTop = 0;
        padLeft = 0;
        padBottom = 0;
        padRight = 0;
        fillX = false;
        fillY = false;
        align = CENTER;
        expandX = false;
        expandY = false;
        colspan = 1;
        uniformX = false;
        uniformY = false;
    }

    public Cell(Cell defaultCell) { //row cell
        this(null, defaultCell);
    }

    public Cell(T widget, Cell defaultCell) { //normal cell
        this.widget = widget;
        if (defaultCell != null) {
            this.minWidth = defaultCell.minWidth;
            this.minHeight = defaultCell.minHeight;
            this.prefWidth = defaultCell.prefWidth;
            this.prefHeight = defaultCell.prefHeight;
            this.maxWidth = defaultCell.maxWidth;
            this.maxHeight = defaultCell.maxHeight;
            this.spaceTop = defaultCell.spaceTop;
            this.spaceLeft = defaultCell.spaceLeft;
            this.spaceBottom = defaultCell.spaceBottom;
            this.spaceRight = defaultCell.spaceRight;
            this.padTop = defaultCell.padTop;
            this.padLeft = defaultCell.padLeft;
            this.padBottom = defaultCell.padBottom;
            this.padRight = defaultCell.padRight;
            this.fillX = defaultCell.fillX;
            this.fillY = defaultCell.fillY;
            this.align = defaultCell.align;
            this.expandX = defaultCell.expandX;
            this.expandY = defaultCell.expandY;
            this.colspan = defaultCell.colspan;
            this.uniformX = defaultCell.uniformX;
            this.uniformY = defaultCell.uniformY;
        }
    }

    public Cell<T> size(Integer width, Integer height) {
        minWidth = width;
        minHeight = height;
        prefWidth = width;
        prefHeight = height;
        maxWidth = width;
        maxHeight = height;
        return this;
    }

    public Cell<T> width(Integer width) {
        minWidth = width;
        prefWidth = width;
        maxWidth = width;
        return this;
    }

    public Cell<T> height(Integer height) {
        minHeight = height;
        prefHeight = height;
        maxHeight = height;
        return this;
    }

    public Cell<T> minSize(Integer width, Integer height) {
        minWidth = width;
        minHeight = height;
        return this;
    }

    public Cell<T> minWidth(Integer minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public Cell<T> minHeight(Integer minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    public Cell<T> prefSize(Integer width, Integer height) {
        prefWidth = width;
        prefHeight = height;
        return this;
    }

    public Cell<T> prefWidth(Integer prefWidth) {
        this.prefWidth = prefWidth;
        return this;
    }

    public Cell<T> prefHeight(Integer prefHeight) {
        this.prefHeight = prefHeight;
        return this;
    }

    public Cell<T> maxSize(Integer width, Integer height) {
        maxWidth = width;
        maxHeight = height;
        return this;
    }

    public Cell<T> maxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public Cell<T> maxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public Cell<T> space(int space) {
        spaceTop = space;
        spaceLeft = space;
        spaceBottom = space;
        spaceRight = space;
        return this;
    }

    public Cell<T> space(int top, int left, int bottom, int right) {
        spaceTop = top;
        spaceLeft = left;
        spaceBottom = bottom;
        spaceRight = right;
        return this;
    }

    public Cell<T> spaceTop(int spaceTop) {
        this.spaceTop = spaceTop;
        return this;
    }

    public Cell<T> spaceLeft(int spaceLeft) {
        this.spaceLeft = spaceLeft;
        return this;
    }

    public Cell<T> spaceBottom(int spaceBottom) {
        this.spaceBottom = spaceBottom;
        return this;
    }

    public Cell<T> spaceRight(int spaceRight) {
        this.spaceRight = spaceRight;
        return this;
    }

    public Cell<T> pad(int pad) {
        return pad(pad, pad, pad, pad);
    }

    public Cell<T> pad(int top, int left, int bottom, int right) {
        padTop = top;
        padLeft = left;
        padBottom = bottom;
        padRight = right;
        return this;
    }

    public Cell<T> padTop(int padTop) {
        this.padTop = padTop;
        return this;
    }

    public Cell<T> padLeft(int padLeft) {
        this.padLeft = padLeft;
        return this;
    }

    public Cell<T> padBottom(int padBottom) {
        this.padBottom = padBottom;
        return this;
    }

    public Cell<T> padRight(int padRight) {
        this.padRight = padRight;
        return this;
    }

    public Cell<T> fill() {
        fillX = true;
        fillY = true;
        return this;
    }

    public Cell<T> fillX() {
        fillX = true;
        return this;
    }

    public Cell<T> fillY() {
        fillY = true;
        return this;
    }

    public Cell<T> fill(boolean x, boolean y) {
        fillX = x;
        fillY = y;
        return this;
    }

    public Cell<T> expand() {
        expandX = true;
        expandY = true;
        return this;
    }

    public Cell<T> expandX() {
        expandX = true;
        return this;
    }

    public Cell<T> expandY() {
        expandY = true;
        return this;
    }

    public Cell<T> expand(boolean x, boolean y) {
        expandX = x;
        expandY = y;
        return this;
    }

    /**
     * {@link Cell#CENTER}
     * {@link Cell#TOP}
     * {@link Cell#RIGHT}
     * {@link Cell#BOTTOM}
     * {@link Cell#LEFT}
     */
    public Cell<T> align(int align) {
        this.align = align;
        return this;
    }

    public Cell<T> colspan(int colspan) {
        this.colspan = colspan;
        return this;
    }

    public Cell<T> uniform() {
        uniformX = true;
        uniformY = true;
        return this;
    }

    public Cell<T> uniformX() {
        uniformX = true;
        return this;
    }

    public Cell<T> uniformY() {
        uniformY = true;
        return this;
    }

    public Cell<T> uniform(boolean x, boolean y) {
        uniformX = x;
        uniformY = y;
        return this;
    }

    public Cell<T> rowEnd(boolean rowEnd) {
        this.rowEnd = rowEnd;
        return this;
    }

    public T getWidget() {
        return widget;
    }

    public boolean hasWidget() {
        return widget != null;
    }

    public Size getSize() {
        Size size = new Size();
        Dimension widgetMinSize = null;
        if (widget != null && (minWidth == null || minHeight == null)) {
            widgetMinSize = widget.getMinimumSize();
        }
        size.min.width = minWidth == null ? (widgetMinSize == null ? 0 : widgetMinSize.width) : minWidth;
        size.min.height = minHeight == null ? (widgetMinSize == null ? 0 : widgetMinSize.height) : minHeight;

        Dimension widgetPrefSize = null;
        if (widget != null && (prefWidth == null || prefHeight == null)) {
            widgetPrefSize = widget.getPreferredSize();
        }
        size.pref.width = prefWidth == null ? (widgetPrefSize == null ? 0 : widgetPrefSize.width) : prefWidth;
        size.pref.height = prefHeight == null ? (widgetPrefSize == null ? 0 : widgetPrefSize.height) : prefHeight;

        Dimension widgetMaxSize = null;
        if (widget != null && (maxWidth == null || maxHeight == null)) {
            widgetMaxSize = widget.getMaximumSize();
        }
        size.max.width = maxWidth == null ? (widgetMaxSize == null ? 0 : widgetMaxSize.width) : maxWidth;
        size.max.height = maxHeight == null ? (widgetMaxSize == null ? 0 : widgetMaxSize.height) : maxHeight;

        if (size.max.width > 0) {
            size.min.width = Math.min(size.min.width, size.max.width);
            size.pref.width = Math.min(size.pref.width, size.max.width);
        }
        if (size.max.height > 0) {
            size.min.height = Math.min(size.min.height, size.max.height);
            size.pref.height = Math.min(size.pref.height, size.max.height);
        }
        size.pref.width = Math.max(size.pref.width, size.min.width);
        size.pref.height = Math.max(size.pref.height, size.min.height);
        return size;
    }

    public static class Size {
        public final Dimension min = new Dimension();
        public final Dimension pref = new Dimension();
        public final Dimension max = new Dimension();
    }

    public int getSpaceTop() {
        return spaceTop;
    }

    public int getSpaceLeft() {
        return spaceLeft;
    }

    public int getSpaceBottom() {
        return spaceBottom;
    }

    public int getSpaceRight() {
        return spaceRight;
    }

    public int getPadTop() {
        return padTop;
    }

    public int getPadLeft() {
        return padLeft;
    }

    public int getPadBottom() {
        return padBottom;
    }

    public int getPadRight() {
        return padRight;
    }

    public boolean isFillX() {
        return fillX;
    }

    public boolean isFillY() {
        return fillY;
    }

    public boolean isExpandX() {
        return expandX;
    }

    public boolean isExpandY() {
        return expandY;
    }

    public int getAlign() {
        return align;
    }

    public int getColspan() {
        return colspan;
    }

    public boolean isUniformX() {
        return uniformX;
    }

    public boolean isUniformY() {
        return uniformY;
    }

    public boolean isRowEnd() {
        return rowEnd;
    }
}
