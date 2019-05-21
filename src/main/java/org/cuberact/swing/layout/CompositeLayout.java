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

import org.cuberact.swing.layout.Cell.Size;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.cuberact.swing.layout.Cell.BOTTOM;
import static org.cuberact.swing.layout.Cell.CENTER;
import static org.cuberact.swing.layout.Cell.LEFT;
import static org.cuberact.swing.layout.Cell.RIGHT;
import static org.cuberact.swing.layout.Cell.TOP;

/**
 * CompositeLayout
 * <p>
 * based on EsotericSoftware/TableLayout. Added new features and improved for Swing.
 * </p>
 *
 * @author Michal Nikodim (michal.nikodim@gmail.com), original made by Nathan Sweet
 * @see <a href="https://github.com/EsotericSoftware/tablelayout">EsotericSoftware/TableLayout</a>
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
class CompositeLayout {

    private final Composite composite;
    private final Cell compositeDefaults = new Cell();
    private final List<Cell<? extends Component>> cells = new ArrayList<>();
    private Cell rowDefaults;
    private Map<Cell, Size> cellSizesShortTimeCache = new HashMap<>();
    private int columns, rows;
    private boolean sizeInvalid = true;
    private int[] columnMinWidth, rowMinHeight;
    private int[] columnPrefWidth, rowPrefHeight;
    private int tableMinWidth, tableMinHeight;
    private int tablePrefWidth, tablePrefHeight;
    private int[] columnWidth, rowHeight;
    private boolean[] expandWidth, expandHeight;
    private int[] columnWeightedWidth, rowWeightedHeight;
    private int padTop, padLeft, padBottom, padRight;
    private int align = CENTER;

    CompositeLayout(Composite composite) {
        this.composite = composite;
    }

    Cell<?> defaults() {
        return compositeDefaults;
    }

    <T extends Component> Cell<T> add(T widget) {
        Cell<T> cell = new Cell<>(widget, rowDefaults != null ? rowDefaults : compositeDefaults);
        if (widget != null) composite.addImpl(widget, null, -1);
        cells.add(cell);
        return cell;
    }

    Cell<?> row() {
        if (!cells.isEmpty()) cells.get(cells.size() - 1).rowEnd(true);
        rowDefaults = new Cell(compositeDefaults);
        return rowDefaults;
    }

    void removeCell(int componentIndex) {
        int cellIndex = -1;
        int widgetIndex = -1;
        for (Cell cell : cells) {
            cellIndex++;
            if (cell.widget != null) {
                widgetIndex++;
                if (widgetIndex == componentIndex) break;
            }
        }
        if (cellIndex != -1) {
            if (cellIndex > 0 && cells.get(cellIndex).isRowEnd()) {
                cells.get(cellIndex - 1).rowEnd(true);
            }
            cells.remove(cellIndex);
        }
    }

    void removeAllCells() {
        cells.clear();
        rows = 0;
        columns = 0;
    }

    @SuppressWarnings("unchecked")
    <T extends Component> Cell<T> getCell(T widget) {
        for (Cell c : cells) {
            if (c.widget == widget) return (Cell<T>) c;
        }
        return null;
    }

    List<Cell<? extends Component>> getCells() {
        return cells;
    }

    Dimension getMinSize() {
        if (sizeInvalid) computeSize(true);
        return new Dimension(tableMinWidth, tableMinHeight);
    }

    Dimension getPrefSize() {
        if (sizeInvalid) computeSize(true);
        return new Dimension(tablePrefWidth, tablePrefHeight);
    }

    void pad(int top, int left, int bottom, int right) {
        if (padTop != top || padLeft != left || padBottom != bottom || padRight != right) {
            padTop = top;
            padLeft = left;
            padBottom = bottom;
            padRight = right;
            invalidateCompositeIfValid();
        }
    }

    void padTop(int top) {
        if (padTop != top) {
            padTop = top;
            invalidateCompositeIfValid();
        }
    }

    void padLeft(int left) {
        if (padLeft != left) {
            padLeft = left;
            invalidateCompositeIfValid();
        }
    }

    void padBottom(int bottom) {
        if (padBottom != bottom) {
            padBottom = bottom;
            invalidateCompositeIfValid();
        }
    }

    void padRight(int right) {
        if (padRight != right) {
            padRight = right;
            invalidateCompositeIfValid();
        }
    }

    void align(int align) {
        if (this.align != align) {
            this.align = align;
            invalidateCompositeIfValid();
        }
    }

    Insets getPad() {
        return new Insets(padTop, padLeft, padBottom, padRight);
    }

    int getAlign() {
        return align;
    }

    int getColumns() {
        return columns;
    }

    int getRows() {
        return rows;
    }

    int[] ensureSize(int[] array, int size) {
        if (array == null || array.length < size) return new int[size];
        for (int i = 0, n = array.length; i < n; i++) {
            array[i] = 0;
        }
        return array;
    }

    boolean[] ensureSize(boolean[] array, int size) {
        if (array == null || array.length < size) return new boolean[size];
        for (int i = 0, n = array.length; i < n; i++) {
            array[i] = false;
        }
        return array;
    }

    void layout() {
        computeLayout();
        for (Cell cell : cells) {
            Component component = cell.getWidget();
            component.setLocation(cell.widgetX, cell.widgetY);
            component.setSize(cell.widgetWidth, cell.widgetHeight);
        }
    }

    void invalidateCompositeIfValid() {
        if (composite.isValid()) {
            composite.invalidate();
        }
    }

    void invalidateSize() {
        sizeInvalid = true;
    }

    void updateCells() {
        columns = 0;
        rows = 0;
        if (!cells.isEmpty()) {
            int column = 0;
            int row = 0;
            List<List<Cell>> table = new ArrayList<>();
            List<Cell> tableRow = new ArrayList<>();
            for (Cell cell : cells) {
                cell.column = column;
                cell.row = row;
                for (int i = 0; i < cell.colspan; i++) {
                    tableRow.add(cell);
                }
                if (cell.isRowEnd()) {
                    row++;
                    column = 0;
                    table.add(tableRow);
                    tableRow = new ArrayList<>();
                } else {
                    column += cell.colspan;
                }
            }
            if (!tableRow.isEmpty()) table.add(tableRow); //add last row if is it row without Cell with rowEnd
            if (!table.isEmpty()) {
                List<Cell> tableFirstRow = table.get(0);
                for (Cell cell : tableFirstRow) {
                    cell.cellAboveIndex = -1;
                }
            }
            if (table.size() > 1) {
                for (int i = 1; i < table.size(); i++) {
                    List<Cell> tableRowAbove = table.get(i - 1);
                    List<Cell> tableRowActual = table.get(i);
                    for (int j = 0; j < tableRowActual.size(); j = j + tableRowActual.get(j).colspan) {
                        if (j >= tableRowAbove.size()) break;
                        tableRowActual.get(j).cellAboveIndex = cells.indexOf(tableRowAbove.get(j));
                    }
                }
            }
            for (List<Cell> tRow : table) {
                columns = Math.max(columns, tRow.size());
            }
            rows = table.size();
        }
    }

    @SuppressWarnings("Duplicates")
    private void computeSize(boolean flushCellSizesCacheAfter) {
        updateCells();
        fillCellSizesCache();
        Insets borderInsets = composite.getInsets();
        sizeInvalid = false;
        columnMinWidth = ensureSize(columnMinWidth, columns);
        rowMinHeight = ensureSize(rowMinHeight, rows);
        columnPrefWidth = ensureSize(columnPrefWidth, columns);
        rowPrefHeight = ensureSize(rowPrefHeight, rows);
        columnWidth = ensureSize(columnWidth, columns);
        rowHeight = ensureSize(rowHeight, rows);
        expandWidth = ensureSize(expandWidth, columns);
        expandHeight = ensureSize(expandHeight, rows);
        int spaceRightLast = 0;
        for (Cell cell : cells) {
            // Collect columns/rows that expand.
            if (cell.expandY && !expandHeight[cell.row]) {
                expandHeight[cell.row] = true;
            }
            if (cell.colspan == 1 && cell.expandX && !expandWidth[cell.column]) {
                expandWidth[cell.column] = true;
            }
            // Compute combined padding/spacing for cells. Spacing between widgets isn't additive, the larger is used. Also, no spacing around edges.
            cell.computedPadLeft = cell.padLeft + (cell.column == 0 ? 0 : max(0, cell.spaceLeft - spaceRightLast));
            cell.computedPadTop = cell.padTop;
            if (cell.cellAboveIndex != -1) {
                Cell above = cells.get(cell.cellAboveIndex);
                cell.computedPadTop += Math.max(0, cell.spaceTop - above.spaceBottom);
            }
            int spaceRight = cell.spaceRight;
            cell.computedPadRight = cell.padRight + ((cell.column + cell.colspan) == columns ? 0 : spaceRight);
            cell.computedPadBottom = cell.padBottom + (cell.row == rows - 1 ? 0 : cell.spaceBottom);
            spaceRightLast = spaceRight;
            // Determine minimum and preferred cell sizes.
            Size size = cellSizesShortTimeCache.get(cell);
            if (cell.colspan == 1) { // Spanned column min and pref width is add later.
                int hPadding = cell.computedPadLeft + cell.computedPadRight;
                columnPrefWidth[cell.column] = max(columnPrefWidth[cell.column], size.pref.width + hPadding);
                columnMinWidth[cell.column] = max(columnMinWidth[cell.column], size.min.width + hPadding);
            }
            int vPadding = cell.computedPadTop + cell.computedPadBottom;
            rowPrefHeight[cell.row] = max(rowPrefHeight[cell.row], size.pref.height + vPadding);
            rowMinHeight[cell.row] = max(rowMinHeight[cell.row], size.min.height + vPadding);
        }
        // Colspan with expand will expand all spanned columns if none of the spanned columns have expand.
        outer:
        for (Cell cell : cells) {
            if (!cell.expandX) continue;
            for (int column = cell.column, nn = column + cell.colspan; column < nn; column++) {
                if (expandWidth[column]) continue outer;
            }
            for (int column = cell.column, nn = column + cell.colspan; column < nn; column++) {
                expandWidth[column] = true;
            }
        }
        // Distribute any additional min and pref width add by colspanned cells to the columns spanned.
        for (Cell cell : cells) {
            if (cell.colspan == 1) continue;
            int spannedMinWidth = -(cell.computedPadLeft + cell.computedPadRight), spannedPrefWidth = spannedMinWidth;
            for (int column = cell.column, nn = column + cell.colspan; column < nn; column++) {
                spannedMinWidth += columnMinWidth[column];
                spannedPrefWidth += columnPrefWidth[column];
            }
            // Distribute extra space using expand, if any columns have expand.
            int totalExpandWidth = 0;
            for (int column = cell.column, nn = column + cell.colspan; column < nn; column++) {
                totalExpandWidth += expandWidth[column] ? 1 : 0;
            }
            Size size = cellSizesShortTimeCache.get(cell);
            int extraMinWidth = max(0, size.min.width - spannedMinWidth);
            int extraPrefWidth = max(0, size.pref.width - spannedPrefWidth);
            for (int column = cell.column, nn = column + cell.colspan; column < nn; column++) {
                float ratio = totalExpandWidth == 0 ? 1f / cell.colspan : expandWidth[column] ? 1 / (float) totalExpandWidth : 0;
                columnMinWidth[column] += (int) (extraMinWidth * ratio);
                columnPrefWidth[column] += (int) (extraPrefWidth * ratio);
            }
        }
        // Collect uniform size.
        int uniformMinWidth = 0, uniformMinHeight = 0;
        int uniformPrefWidth = 0, uniformPrefHeight = 0;
        for (Cell cell : cells) {
            // Collect uniform sizes.
            if (cell.uniformX == Boolean.TRUE && cell.colspan == 1) {
                int hpadding = cell.computedPadLeft + cell.computedPadRight;
                uniformMinWidth = max(uniformMinWidth, columnMinWidth[cell.column] - hpadding);
                uniformPrefWidth = max(uniformPrefWidth, columnPrefWidth[cell.column] - hpadding);
            }
            if (cell.uniformY == Boolean.TRUE) {
                int vpadding = cell.computedPadTop + cell.computedPadBottom;
                uniformMinHeight = max(uniformMinHeight, rowMinHeight[cell.row] - vpadding);
                uniformPrefHeight = max(uniformPrefHeight, rowPrefHeight[cell.row] - vpadding);
            }
        }
        // Size uniform cells to the same width/height.
        if (uniformPrefWidth > 0 || uniformPrefHeight > 0) {
            for (Cell cell : cells) {
                if (uniformPrefWidth > 0 && cell.uniformX == Boolean.TRUE && cell.colspan == 1) {
                    int hPadding = cell.computedPadLeft + cell.computedPadRight;
                    columnMinWidth[cell.column] = uniformMinWidth + hPadding;
                    columnPrefWidth[cell.column] = uniformPrefWidth + hPadding;
                }
                if (uniformPrefHeight > 0 && cell.uniformY == Boolean.TRUE) {
                    int vPadding = cell.computedPadTop + cell.computedPadBottom;
                    rowMinHeight[cell.row] = uniformMinHeight + vPadding;
                    rowPrefHeight[cell.row] = uniformPrefHeight + vPadding;
                }
            }
        }
        // Determine composite min and pref size.
        tableMinWidth = 0;
        tableMinHeight = 0;
        tablePrefWidth = 0;
        tablePrefHeight = 0;
        for (int i = 0; i < columns; i++) {
            tableMinWidth += columnMinWidth[i];
            tablePrefWidth += columnPrefWidth[i];
        }
        for (int i = 0; i < rows; i++) {
            tableMinHeight += rowMinHeight[i];
            tablePrefHeight += max(rowMinHeight[i], rowPrefHeight[i]);
        }

        int hPadding = padLeft + padRight + borderInsets.left + borderInsets.right;
        int vPadding = padTop + padBottom + borderInsets.top + borderInsets.bottom;
        tableMinWidth = tableMinWidth + hPadding;
        tableMinHeight = tableMinHeight + vPadding;
        tablePrefWidth = max(tablePrefWidth + hPadding, tableMinWidth);
        tablePrefHeight = max(tablePrefHeight + vPadding, tableMinHeight);
        if (flushCellSizesCacheAfter) cellSizesShortTimeCache.clear();
    }

    @SuppressWarnings("Duplicates")
    private void computeLayout() {
        if (sizeInvalid) {
            computeSize(false);
        } else {
            fillCellSizesCache();
        }
        Insets borderInsets = composite.getInsets();
        int hPadding = padLeft + padRight + borderInsets.left + borderInsets.right;
        int vPadding = padTop + padBottom + borderInsets.top + borderInsets.bottom;
        int totalExpandWidth = 0, totalExpandHeight = 0;
        for (int i = 0; i < columns; i++) {
            totalExpandWidth += expandWidth[i] ? 1 : 0;
        }
        for (int i = 0; i < rows; i++) {
            totalExpandHeight += expandHeight[i] ? 1 : 0;
        }
        // Size columns and rows between min and pref size using (preferred - min) size to weight distribution of extra space.
        int[] columnWeightedWidth;
        int totalGrowWidth = tablePrefWidth - tableMinWidth;
        if (totalGrowWidth == 0) {
            columnWeightedWidth = columnMinWidth;
        } else {
            int extraWidth = min(totalGrowWidth, max(0, composite.getWidth() - tableMinWidth));
            columnWeightedWidth = this.columnWeightedWidth = ensureSize(this.columnWeightedWidth, columns);
            for (int i = 0; i < columns; i++) {
                int growWidth = columnPrefWidth[i] - columnMinWidth[i];
                float growRatio = growWidth / (float) totalGrowWidth;
                columnWeightedWidth[i] = (int) (columnMinWidth[i] + extraWidth * growRatio);
            }
        }
        int[] rowWeightedHeight;
        int totalGrowHeight = tablePrefHeight - tableMinHeight;
        if (totalGrowHeight == 0) {
            rowWeightedHeight = rowMinHeight;
        } else {
            rowWeightedHeight = this.rowWeightedHeight = ensureSize(this.rowWeightedHeight, rows);
            int extraHeight = min(totalGrowHeight, max(0, composite.getHeight() - tableMinHeight));
            for (int i = 0; i < rows; i++) {
                int growHeight = rowPrefHeight[i] - rowMinHeight[i];
                float growRatio = growHeight / (float) totalGrowHeight;
                rowWeightedHeight[i] = (int) (rowMinHeight[i] + extraHeight * growRatio);
            }
        }
        // Determine widget and cell sizes (before expand or fill).
        for (Cell c : cells) {
            int spannedWeightedWidth = 0;
            for (int column = c.column, nn = column + c.colspan; column < nn; column++) {
                spannedWeightedWidth += columnWeightedWidth[column];
            }
            int weightedHeight = rowWeightedHeight[c.row];
            Size size = cellSizesShortTimeCache.get(c);
            c.widgetWidth = min(spannedWeightedWidth - c.computedPadLeft - c.computedPadRight, size.pref.width);
            c.widgetHeight = min(weightedHeight - c.computedPadTop - c.computedPadBottom, size.pref.height);
            if (c.colspan == 1) {
                columnWidth[c.column] = max(columnWidth[c.column], spannedWeightedWidth);
            }
            rowHeight[c.row] = max(rowHeight[c.row], weightedHeight);
        }
        // Distribute remaining space to any expanding columns/rows.
        if (totalExpandWidth > 0) {
            int extra = composite.getWidth() - hPadding;
            for (int i = 0; i < columns; i++) {
                extra -= columnWidth[i];
            }
            int used = 0;
            int lastIndex = 0;
            for (int i = 0; i < columns; i++) {
                if (!expandWidth[i]) continue;
                int amount = (int) (extra * (expandWidth[i] ? 1 : 0) / (float) totalExpandWidth);
                columnWidth[i] += amount;
                used += amount;
                lastIndex = i;
            }
            columnWidth[lastIndex] += extra - used;
        }
        if (totalExpandHeight > 0) {
            int extra = composite.getHeight() - vPadding;
            for (int i = 0; i < rows; i++) {
                extra -= rowHeight[i];
            }
            int used = 0;
            int lastIndex = 0;
            for (int i = 0; i < rows; i++) {
                if (!expandHeight[i]) continue;
                int amount = (int) (extra * (expandHeight[i] ? 1 : 0) / (float) totalExpandHeight);
                rowHeight[i] += amount;
                used += amount;
                lastIndex = i;
            }
            rowHeight[lastIndex] += extra - used;
        }
        // Distribute any additional width add by colSpanned cells to the columns spanned.
        for (Cell c : cells) {
            if (c.colspan == 1) continue;
            int extraWidth = 0;
            for (int column = c.column, nn = column + c.colspan; column < nn; column++) {
                extraWidth += columnWeightedWidth[column] - columnWidth[column];
            }
            extraWidth -= Math.max(0, c.computedPadLeft + c.computedPadRight);
            extraWidth /= c.colspan;
            if (extraWidth > 0) {
                for (int column = c.column, nn = column + c.colspan; column < nn; column++) {
                    columnWidth[column] += extraWidth;
                }
            }
        }
        // Determine composite size.
        int tableWidth = hPadding, tableHeight = vPadding;
        for (int i = 0; i < columns; i++) {
            tableWidth += columnWidth[i];
        }
        for (int i = 0; i < rows; i++) {
            tableHeight += rowHeight[i];
        }
        // Position composite within the container.
        int x = padLeft + borderInsets.left;
        if ((align & RIGHT) != 0) {
            x += composite.getWidth() - tableWidth;
        } else if ((align & LEFT) == 0) { // Center
            x += (composite.getWidth() - tableWidth) / 2;
        }
        int y = padTop + borderInsets.top;
        if ((align & BOTTOM) != 0) {
            y += composite.getHeight() - tableHeight;
        } else if ((align & TOP) == 0) { // Center
            y += (composite.getHeight() - tableHeight) / 2;
        }
        // Position widgets within cells.
        int currentX = x, currentY = y;
        for (Cell c : cells) {
            int spannedCellWidth = 0;
            for (int column = c.column, nn = column + c.colspan; column < nn; column++) {
                spannedCellWidth += columnWidth[column];
            }
            spannedCellWidth -= c.computedPadLeft + c.computedPadRight;
            currentX += c.computedPadLeft;
            Size size = null;
            if (c.fillX || c.fillY) {
                size = cellSizesShortTimeCache.get(c);
            }
            if (c.fillX) {
                c.widgetWidth = spannedCellWidth;
                if (size.max.width > 0) {
                    c.widgetWidth = min(c.widgetWidth, size.max.width);
                }
            }
            if (c.fillY) {
                c.widgetHeight = rowHeight[c.row] - c.computedPadTop - c.computedPadBottom;
                if (size.max.height > 0) {
                    c.widgetHeight = min(c.widgetHeight, size.max.height);
                }
            }
            if ((c.align & LEFT) != 0) {
                c.widgetX = currentX;
            } else if ((c.align & RIGHT) != 0) {
                c.widgetX = currentX + spannedCellWidth - c.widgetWidth;
            } else {
                c.widgetX = currentX + (spannedCellWidth - c.widgetWidth) / 2;
            }
            if ((c.align & TOP) != 0) {
                c.widgetY = currentY + c.computedPadTop;
            } else if ((c.align & BOTTOM) != 0) {
                c.widgetY = currentY + rowHeight[c.row] - c.widgetHeight - c.computedPadBottom;
            } else {
                c.widgetY = currentY + (rowHeight[c.row] - c.widgetHeight + c.computedPadTop - c.computedPadBottom) / 2;
            }
            if (c.isRowEnd()) {
                currentX = x;
                currentY += rowHeight[c.row];
            } else {
                currentX += spannedCellWidth + c.computedPadRight;
            }
        }
        cellSizesShortTimeCache.clear();
    }

    private void fillCellSizesCache() {
        cellSizesShortTimeCache.clear();
        for (Cell cell : cells) {
            cellSizesShortTimeCache.put(cell, cell.getSize());
        }
    }

    private static int max(int value1, int value2) {
        return value1 > value2 ? value1 : value2;
    }

    private static int min(int value1, int value2) {
        return value1 < value2 ? value1 : value2;
    }
}
