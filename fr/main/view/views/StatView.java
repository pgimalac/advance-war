package fr.main.view.views;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import fr.main.view.controllers.StatController;

public class StatView extends View {

    private StatController controller;

    private class StatPanel extends JPanel {

        public int[][] data;
        Color[] colors = new Color[]{
            Color.red,
            Color.blue,
            Color.green,
            Color.yellow
        };

        private boolean lign;
        private int max = 0;
        private int[][] coords;

        public StatPanel (int[][] data, boolean lign) {
            this.data = data;
            this.lign = lign;

            for (int i = 0; i < data.length; i++)
                for (int j = 0; j < data[i].length; j++)
                    if (max < data[i][j]) max = data[i][j];

            coords = new int[data.length + 1][data[0].length];

            for (int i = 0; i < data.length; i++)
                for (int j = 0; j < data[i].length; j++)
                    coords[i + 1][j] = coords[i][j] + data[i][j];
                    // the first coord is full of 0
            // if every number is 0, we set it to 1 to avoid arithmeticException (divide by 0)
            for (int i = 0; i < data[0].length; i++)
                if (coords[coords.length - 1][i] == 0)
                    for (int j = 1; j <= data.length; j++)
                        coords[j][i] = j;
        }

        public void paintLign(Graphics g, int[][] data, int gap){
            int x;
            ((Graphics2D)g).setStroke(new BasicStroke(3));
            for (int i = 0; i < data.length; i++) { // each player
                g.setColor(colors[i]);
                x = 0;
                for (int j = 1; j < data[i].length; j++) {
                    g.drawLine(x, (max - data[i][j - 1]) * this.getHeight() / max, x + gap, (max - data[i][j]) * this.getHeight() / max);
                    x += gap;
                }
            }

            g.setColor(Color.black);
            int h = gap * (data[0].length - 1);
            g.drawLine(h, 0, h, getHeight());
            if (max >= 10)
                for (int i = 1; i < 11; i++) {
                    int v = i == 10 ? 0 : getHeight() * (10 - i) / 10;
                    g.drawLine(h - 10, v, h, v);
                    g.drawString(max * i / 10 + "", h + 3, v + 10);
                }
            else
                for (int i = 1; i <= max; i++){
                    int v = getHeight() * (max - i) / max;
                    g.drawLine(h - 10, v, h, v);
                    g.drawString(i + "", h + 3, v + 10);
                }
        }

        public void paintGraph(Graphics g, int[][] data, int gap){
            int[] x = new int[2 * coords[0].length];
            int[] y = new int[2 * coords[0].length];
            for (int i = 0; i < data[0].length; i++){
                x[i]                = i * gap;
                x[x.length - 1 - i] = i * gap;
            }

            for (int i = 1; i < coords.length; i++){
                g.setColor(colors[i - 1]);
                for (int j = 0; j < coords[i].length; j++)
                    y[j] = y[y.length - 1 - j];
                for (int j = 0; j < coords[i].length; j++)
                    y[y.length - 1 - j] = coords[i][j] * this.getHeight() / coords[coords.length - 1][j];
                g.fillPolygon(x, y, x.length);
            }

            g.setColor(Color.black);
            int h = gap * (data[0].length - 1);
            g.drawLine(h, 0, h, getHeight());
            for (int i = 1; i < 11; i++) {
                int v = i == 10 ? 0 : getHeight() * (10 - i) / 10;
                g.drawLine(h - 10, v, h, v);
                g.drawString((i * 10) + "%", h + 3, v + 10);
            }
        }

        public void paintComponent(Graphics g) {
            int x, gap = this.getWidth() / data[0].length;

            if (lign) paintLign (g, data, gap);
            else      paintGraph(g, data, gap);

            g.setColor(Color.black);
            for (int day = 0; day < data[0].length; day++) {
                x = day * gap;
                g.drawLine(x, getHeight(), x, getHeight() - 10);
                g.drawString("day " + (day + 1), x, getHeight() - 10);
            }
        }

        public void changeGraph(){
            lign = !lign;
        }
    }

    public StatView (StatController controller) {
        super(controller);
        this.controller = controller;
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Units",     null, new StatPanel (controller.units,    false), "Evolution of all players units");
        tabs.addTab("Buildings", null, new StatPanel (controller.buildings, true), "Evolution of all players buildings");
        tabs.addTab("Funds",     null, new StatPanel (controller.funds,     true), "Evolution of all players funds");

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);

        JButton menu = new JButton("Back to menu"),
                lign = new JButton("Switch chart");

        JPanel buttons = new JPanel();
        buttons.add(menu);
        buttons.add(lign);

        add(buttons, BorderLayout.SOUTH);

        menu.addActionListener(controller.menu);
        lign.addActionListener(e -> ((StatPanel)tabs.getSelectedComponent()).changeGraph());
    }

}
