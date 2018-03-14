package fr.main.view.render.units.air;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.awt.Image;

import fr.main.model.Player;
import fr.main.model.terrains.Terrain;
import fr.main.model.units.Path;
import fr.main.model.Direction;
import fr.main.view.MainFrame;
import fr.main.view.render.units.UnitRenderer;
import fr.main.view.render.units.UnitAnimationManager;
import fr.main.model.units.air.Fighter;

public class FighterRenderer extends Fighter implements UnitRenderer {

  private Point offset;

  private transient Image image;

  public FighterRenderer (Player p, Point location) {
    super (p, location);

    offset = new Point(0, 0);
  }

  public void draw (Graphics g, int x, int y) {
    if (image == null) g.fillRect (x + offset.x, y + offset.y, MainFrame.UNIT, MainFrame.UNIT);
    else g.drawImage (image, x + offset.x, y + offset.y + UnitAnimationManager.getOffset() - 5, MainFrame.UNIT, MainFrame.UNIT, null);
  }

  @Override
  public String getFilename() {
    return getDir() + "fighter.png";
  }

  public void setImage (Image image) {
    this.image = image;
  }

  @Override
  public Point getOffset () {
    return offset;
  }

}
