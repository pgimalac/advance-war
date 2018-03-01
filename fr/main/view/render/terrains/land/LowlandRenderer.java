package fr.main.view.render.terrains.land;

import java.awt.*;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import fr.main.view.MainFrame;
import fr.main.view.render.Renderer;
import fr.main.model.terrains.land.Lowland;

import javax.imageio.ImageIO;

public class LowlandRenderer extends Lowland implements Renderer {

  private String imagePath;
  private transient Image image;
  private transient static LowlandRenderer instance;

  private LowlandRenderer(String imagePath) {
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
    if(image == null) {
      g.setColor (Color.green);
      g.fillRect (x, y, MainFrame.UNIT, MainFrame.UNIT);
      return;
    }

    Graphics2D g2d = (Graphics2D) g;
    g2d.drawImage(image, x, y, null);
  }

  public static LowlandRenderer get () {
    if (instance == null) instance = new LowlandRenderer ("./assets/terrains/lowland.png");
    return instance;
  }

}

