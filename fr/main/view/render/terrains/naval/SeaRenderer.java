package fr.main.view.render.terrains.naval;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import fr.main.view.MainFrame;
import fr.main.view.render.Renderer;
import fr.main.model.terrains.naval.Sea;


public class SeaRenderer extends Sea implements Renderer {

  private String imagePath;
  private transient Image image;
  private transient static SeaRenderer instance;

  public SeaRenderer(String imagePath) {
    this.imagePath = imagePath;
    update();
  }


    @Override
    public String getFilename () {
        return imagePath;
    }

    @Override
    public void setImage (Image image) {
        this.image = image;
    }

  public void draw (Graphics g, int x, int y) {
    if(this.image == null) {
      g.setColor (Color.cyan);
      g.fillRect (x, y, MainFrame.UNIT, MainFrame.UNIT);
    }

    Graphics2D g2d = (Graphics2D) g;
    g2d.drawImage(image, x, y, MainFrame.UNIT, MainFrame.UNIT, null);
  }

  public static SeaRenderer get() {
    if (instance == null) instance = new SeaRenderer("assets/terrains/sea.png");
    return instance;
  }

}

