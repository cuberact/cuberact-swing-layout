package org.cuberact.swing.layout;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.awt.AWTEvent.KEY_EVENT_MASK;
import static java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK;
import static java.awt.AWTEvent.WINDOW_EVENT_MASK;

@SuppressWarnings({"WeakerAccess", "unused"})
public class DebugGlass extends JComponent implements AWTEventListener {

    public static Set<Integer> DEBUG_ACTIVATE_KEYS = new HashSet<>(Collections.singletonList(KeyEvent.VK_ALT));
    public static int KEY_SELECT_PARENT = KeyEvent.VK_UP;
    public static int KEY_REVERT_PARENT = KeyEvent.VK_DOWN;
    public static int KEY_UNSELECT = KeyEvent.VK_DELETE;
    public static Color WIDGET_COLOR = new Color(255, 0, 255, 100);
    public static Color CELL_COLOR = new Color(0, 255, 0, 255);

    private static DebugGlassPainter DEFAULT_PAINTER = (g, c, x, y, w, h) -> {
        g.setColor(WIDGET_COLOR);
        g.fillRect(x, y, w, h);
        if (c instanceof Composite) {
            g.setColor(CELL_COLOR);
            final List<Cell<? extends Component>> cells = ((Composite) c).getCells();
            for (Cell<? extends Component> cell : cells) {
                g.drawRect(x + cell.widgetX, y + cell.widgetY, cell.widgetWidth - 1, cell.widgetHeight - 1);
            }
        }
    };

    private static AWTEventListener AUTOMATIC = event -> {
        if (event instanceof WindowEvent && event.getID() == WindowEvent.WINDOW_OPENED) {
            WindowEvent we = (WindowEvent) event;
            Window window = we.getWindow();
            if (window instanceof RootPaneContainer) {
                apply((RootPaneContainer) window, DEFAULT_PAINTER);
            }
        }
    };

    public static void enableAutomatic() {
        enableAutomatic(DEFAULT_PAINTER);
    }

    public static void enableAutomatic(DebugGlassPainter painter) {
        DEFAULT_PAINTER = painter;
        disableAutomatic();
        Toolkit.getDefaultToolkit().addAWTEventListener(AUTOMATIC, WINDOW_EVENT_MASK);
    }

    public static void disableAutomatic() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(AUTOMATIC);
    }

    public static DebugGlass apply(RootPaneContainer rootPaneContainer, DebugGlassPainter painter) {
        return new DebugGlass(rootPaneContainer, painter);
    }

    private final Set<Integer> pressedKeys = new HashSet<>();
    private final JRootPane rootPane;
    private Point mouse = new Point(0, 0);
    private WeakReference<Component> target = new WeakReference<>(null);
    private boolean debugIsActive = false;
    private final DebugGlassPainter painter;

    private DebugGlass(RootPaneContainer rootPaneContainer, DebugGlassPainter painter) {
        this.rootPane = rootPaneContainer.getRootPane();
        this.rootPane.setGlassPane(this);
        this.painter = painter;
        setOpaque(false);
        setVisible(true);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, MOUSE_MOTION_EVENT_MASK | KEY_EVENT_MASK);
        SwingUtilities.getWindowAncestor(rootPane).addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(final WindowEvent e) {
                Toolkit.getDefaultToolkit().removeAWTEventListener(DebugGlass.this);
            }
        });
    }

    @Override
    public void eventDispatched(final AWTEvent event) {
        if (isNotMyEvent(event)) return;
        if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            updateDebugIsActive(keyEvent);
            if (debugIsActive) {
                if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                    if (keyEvent.getKeyCode() == KEY_SELECT_PARENT) {
                        Component t = target.get();
                        if (t != null && t.getParent() != null && t.getParent() != rootPane) {
                            target = new WeakReference<>(t.getParent());
                            repaint();
                        }
                    } else if (keyEvent.getKeyCode() == KEY_REVERT_PARENT) {
                        Component child = getChildUnderMouse(mouse);
                        if (child != target.get()) {
                            while (child.getParent() != target.get()) {
                                child = child.getParent();
                            }
                            target = new WeakReference<>(child);
                            repaint();
                        }
                    } else if (keyEvent.getKeyCode() == KEY_UNSELECT) {
                        if (target.get() != null) {
                            target = new WeakReference<>(null);
                            repaint();
                        }
                    }
                }
            }
        } else if (debugIsActive && event instanceof MouseEvent) {
            mouse = transformLocation(((MouseEvent) event).getLocationOnScreen());
            Component child = getChildUnderMouse(mouse);
            if (target.get() != child) {
                target = new WeakReference<>(child);
                repaint();
            }
        }
    }

    public boolean contains(int x, int y) {
        return false;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        Component c = target.get();
        if (painter != null && c != null && c.isShowing()) {
            Point p = transformLocation(c.getLocationOnScreen());
            painter.paint(g, c, p.x, p.y, c.getWidth(), c.getHeight());
        }
    }

    private boolean isNotMyEvent(AWTEvent event) {
        return !(event.getSource() instanceof Component) || SwingUtilities.getRootPane((Component) event.getSource()) != rootPane;
    }

    private void updateDebugIsActive(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            pressedKeys.add(e.getKeyCode());
        } else {
            pressedKeys.remove(e.getKeyCode());
        }
        debugIsActive = pressedKeys.containsAll(DEBUG_ACTIVATE_KEYS);
    }

    private Component getChildUnderMouse(final Point p) {
        final Point cp = SwingUtilities.convertPoint(this, p, rootPane.getContentPane());
        return SwingUtilities.getDeepestComponentAt(rootPane.getContentPane(), cp.x, cp.y);
    }

    private Point transformLocation(Point screenCoords) {
        final Point glassP = this.getLocationOnScreen();
        return new Point(screenCoords.x - glassP.x, screenCoords.y - glassP.y);
    }

    public interface DebugGlassPainter {
        void paint(Graphics g, Component c, int x, int y, int width, int height);
    }

}
