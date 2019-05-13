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

import javax.swing.*;
import java.awt.*;

/**
 * Stack
 * <p>
 * based on EsotericSoftware/TableLayout. Added new features and improved for Swing.
 * </p>
 *
 * @author Michal Nikodim (michal.nikodim@gmail.com), original made by Nathan Sweet
 * @see <a href="https://github.com/EsotericSoftware/tablelayout">EsotericSoftware/TableLayout</a>
 */
class Stack extends JPanel {
    Stack() {
        super(new LayoutManager() {
            public void layoutContainer(Container parent) {
                int width = parent.getWidth();
                int height = parent.getHeight();
                for (int i = 0, n = parent.getComponentCount(); i < n; i++) {
                    parent.getComponent(i).setLocation(0, 0);
                    parent.getComponent(i).setSize(width, height);
                }
            }

            public Dimension preferredLayoutSize(Container parent) {
                Dimension size = new Dimension();
                for (int i = 0, n = parent.getComponentCount(); i < n; i++) {
                    Dimension pref = parent.getComponent(i).getPreferredSize();
                    size.width = Math.max(size.width, pref.width);
                    size.height = Math.max(size.height, pref.height);
                }
                return size;
            }

            public Dimension minimumLayoutSize(Container parent) {
                Dimension size = new Dimension();
                for (int i = 0, n = parent.getComponentCount(); i < n; i++) {
                    Dimension min = parent.getComponent(i).getMinimumSize();
                    size.width = Math.max(size.width, min.width);
                    size.height = Math.max(size.height, min.height);
                }
                return size;
            }

            public void addLayoutComponent(String name, Component comp) {
            }

            public void removeLayoutComponent(Component comp) {
            }
        });
    }

}
