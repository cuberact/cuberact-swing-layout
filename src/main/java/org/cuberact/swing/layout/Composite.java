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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.cuberact.swing.layout.Cell.CENTER;

/**
 * Composite
 * <p>
 * based on EsotericSoftware/TableLayout. Added new features and improved for Swing.
 * </p>
 *
 * @author Michal Nikodim (michal.nikodim@gmail.com), original made by Nathan Sweet
 * @see <a href="https://github.com/EsotericSoftware/tablelayout">EsotericSoftware/TableLayout</a>
 */

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Composite extends JComponent implements Iterable<Cell<? extends Component>> {

    private CompositeLayout layout;

    public Composite() {
        //empty constructor
    }

    public Composite(Color background) {
        setBackground(background);
    }

    public Composite(Border border) {
        setBorder(border);
    }

    public Composite(Border border, Color background) {
        setBorder(border);
        setBackground(background);
    }

    public Cell<JLabel> addCell(String text) {
        return addCell(new JLabel(text));
    }

    public Cell<JLabel> addCell(ImageIcon icon, String text) {
        return addCell(new JLabel(text, icon, JLabel.LEFT));
    }

    public Cell<?> addCell() {
        return addCell((Component) null);
    }

    public <T extends Component> Cell<T> addCell(T widget) {
        initLayoutIfNull();
        return layout.add(widget);
    }

    public Cell<Stack> addStack(Component... widgets) {
        Stack stack = new Stack();
        for (final Component component : widgets) {
            stack.add(component);
        }
        return addCell(stack);
    }

    public Cell<?> row() {
        initLayoutIfNull();
        return layout.row();
    }

    public Cell<?> defaults() {
        initLayoutIfNull();
        return layout.defaults();
    }

    public <T extends Component> Cell<T> getCell(T widget) {
        return layout != null ? layout.getCell(widget) : null;
    }

    public List<Cell<? extends Component>> getCells() {
        initLayoutIfNull();
        return layout.getCells();
    }

    /**
     * The cell alignment constant is used, but it is the alignment of the Composite relative to its parent
     * <p>
     * {@link Cell#CENTER}
     * {@link Cell#TOP}
     * {@link Cell#RIGHT}
     * {@link Cell#BOTTOM}
     * {@link Cell#LEFT}
     */
    public Composite align(int align) {
        initLayoutIfNull();
        layout.align(align);
        return this;
    }

    public Composite pad(int pad) {
        return pad(pad, pad, pad, pad);
    }

    public Composite pad(int top, int left, int bottom, int right) {
        initLayoutIfNull();
        layout.pad(top, left, bottom, right);
        return this;
    }

    public Composite padTop(int top) {
        initLayoutIfNull();
        layout.padTop(top);
        return this;
    }

    public Composite padLeft(int left) {
        initLayoutIfNull();
        layout.padLeft(left);
        return this;
    }

    public Composite padBottom(int bottom) {
        initLayoutIfNull();
        layout.padBottom(bottom);
        return this;
    }

    public Composite padRight(int right) {
        initLayoutIfNull();
        layout.padRight(right);
        return this;
    }

    public int getAlign() {
        return layout != null ? layout.getAlign() : CENTER;
    }

    public Insets getPad() {
        return layout != null ? layout.getPad() : new Insets(0, 0, 0, 0);
    }

    public int getColumns() {
        return layout != null ? layout.getColumns() : 0;
    }

    public int getRows() {
        return layout != null ? layout.getRows() : 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isBackgroundSet()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        super.paintComponent(g);
    }

    @Override
    public void setBackground(final Color bg) {
        setOpaque(bg != null && bg.getAlpha() == 255);
        super.setBackground(bg);
    }

    @Override
    public void remove(Component widget) {
        super.remove(widget);
    }

    @Override
    public void remove(final int index) {
        if (layout != null) {
            layout.removeCell(index);
        }
        super.remove(index);
    }

    @Override
    public void removeAll() {
        if (layout != null) {
            layout.removeAllCells();
            super.removeAll();
        }
    }

    /**
     * Only for emergency purpose (swing drag, toolbar floating closed, etc).
     * Use addCell instead
     */
    @Deprecated
    @Override
    public Component add(final Component comp, final int index) {
        addCell(comp);
        return comp;
    }

    /**
     * Only for emergency purpose (swing drag, toolbar floating closed, etc).
     * Use addCell instead
     */
    @Deprecated
    @Override
    public Component add(final Component comp) {
        addCell(comp);
        return comp;
    }

    /**
     * Only for emergency purpose (swing drag, toolbar floating closed, etc).
     * Use addCell instead
     */
    @Deprecated
    @Override
    public Component add(final String name, final Component comp) {
        addCell(comp);
        return comp;
    }

    /**
     * Only for emergency purpose (swing drag, toolbar floating closed, etc).
     * Use addCell instead
     */
    @Deprecated
    @Override
    public void add(final Component comp, final Object constraints) {
        addCell(comp);
    }

    /**
     * Only for emergency purpose (swing drag, toolbar floating closed, etc).
     * Use addCell instead
     */
    @Deprecated
    @Override
    public void add(final Component comp, final Object constraints, final int index) {
        addCell(comp);
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (layout != null) {
            layout.invalidateSize();
        }
    }

    private void initLayoutIfNull() {
        if (layout == null) {
            layout = new CompositeLayout(this);
            setLayout(new CompositeLayoutManager(layout));
        }
    }

    @Override
    public Iterator<Cell<? extends Component>> iterator() {
        if (layout == null) return Collections.emptyListIterator();
        return layout.getCells().iterator();
    }

    private static class CompositeLayoutManager implements LayoutManager {
        private final CompositeLayout layout;
        private Dimension minSize = new Dimension();
        private Dimension prefSize = new Dimension();

        private CompositeLayoutManager(CompositeLayout layout) {
            this.layout = layout;
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            layout.layout();
            return layout.getPrefSize();
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            layout.layout();
            return layout.getMinSize();
        }

        @Override
        public void layoutContainer(Container parent) {
            layout.layout();
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
            //nothing
        }

        @Override
        public void removeLayoutComponent(Component comp) {
            //nothing
        }
    }
}
