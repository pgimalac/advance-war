package fr.main.view.views;

import java.awt.*;
import javax.swing.*;

import fr.main.view.controllers.EditorController;

public class EditorView extends View {

  private EditorController controller;

  boolean isListening = false;

  public class Map extends JPanel {

    public void paintComponent (Graphics g) {
      controller.world.draw(g, controller.camera.getX(), 
          controller.camera.getY(), 
          controller.camera.getOffsetX(),
          controller.camera.getOffsetY());

      g.setColor(Color.black);
      for (int i = 0; i < 4; i++)
        g.fillPolygon(controller.arrows[i][0], controller.arrows[i][1], 3);
    }

  }

  private class Tools extends JPanel {

    JSlider width, height, seed;
    
    public Tools () {
      setLayout(new GridLayout(7, 1));
      add(new JLabel("Add tools here"));

      width  = new JSlider(JSlider.HORIZONTAL, 15, 500, 50);
      height = new JSlider(JSlider.HORIZONTAL, 15, 500, 50);
      seed   = new JSlider(JSlider.HORIZONTAL, 10, 500, 50);

      add(new JLabel("Width:"));
      add(width);

      add(new JLabel("Height:"));
      add(height);

      add(new JLabel("Seed:"));
      add(seed);
    }
  }

  public final Map map;
  private Tools tools;

  public EditorView (EditorController controller) {
    super(controller);
    this.controller = controller;

    map = new Map ();
    tools = new Tools();

    setLayout(new BorderLayout());
    add(map, BorderLayout.CENTER);
    add(tools, BorderLayout.EAST);

    controller.new Adaptater(tools.width, tools.height, tools.seed);
    map.addMouseMotionListener(controller);
    map.addMouseListener(controller);
  }

}
