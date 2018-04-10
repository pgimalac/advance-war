package fr.main.view.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;

import fr.main.model.Direction;
import fr.main.model.MoveZone;
import fr.main.model.Node;
import fr.main.model.Universe;
import fr.main.model.Weather;
import fr.main.model.buildings.AbstractBuilding;
import fr.main.model.players.Player;
import fr.main.model.terrains.AbstractTerrain;
import fr.main.model.units.AbstractUnit;
import fr.main.model.units.HealerUnit;
import fr.main.model.units.TransportUnit;
import fr.main.model.units.naval.NavalUnit;
import fr.main.view.MainFrame;
import fr.main.view.controllers.GameController;
import fr.main.view.render.buildings.BuildingRenderer;
import fr.main.view.render.sprites.Sprite;
import fr.main.view.render.terrains.TerrainRenderer;
import fr.main.view.render.units.UnitRenderer;

public class UniverseRenderer extends Universe {

    public final GameController controller;
    private final Color fogColor    = new Color (0,0,0,100),
                        moveColor   = new Color (0, 255, 0, 50),
                        targetColor = new Color (255, 0, 0, 100),
                        loadColor   = new Color (0, 0, 255, 50),
                        healColor   = new Color (0, 255, 0, 50);

    private static final Font font = new Font("Helvetica", Font.PLAIN, 14);

    private final Point[][] coords;
    private final boolean[][] targets;
    private Point upperLeft = new Point(0,0), lowerRight;
    private Color tColor;

    public static class FlashMessage {

        public enum Type {
            ALERT(Color.red),
            SUCCESS(Color.green);

            public final Color color;

            private Type (Color color) {
                this.color = color;
            }
        }

        private final int x, y;
        private int time;
        private final String message;
        private final Type type;

        public FlashMessage (String message, int x, int y, int time, Type type) {
            this.time    = time;
            this.message = message;
            this.x       = x;
            this.y       = y;
            this.type    = type;
        }

    }

    private final LinkedList<FlashMessage>   flashs;
    private final LinkedList<DeathAnimation> deathAnimation;

    public UniverseRenderer (Universe.Board b, GameController controller){
        super(b);
        
        this.controller = controller;
        controller.makeView().getWeatherController().update(Weather.FOGGY);
        coords = new Point[map.board.length][map.board[0].length];
        for (int i = 0; i < map.board.length; i++)
            for (int j = 0; j < map.board[i].length; j++)
                coords[i][j] = new Point(0, 0);

        targets        = new boolean[map.board.length][map.board[0].length];
        lowerRight     = new Point(map.board.length, map.board[0].length);
        flashs         = new LinkedList<FlashMessage>();
        deathAnimation = new LinkedList<DeathAnimation>();
        TerrainRenderer.setLocations();
    }

    public UniverseRenderer (AbstractUnit[][] units, AbstractTerrain[][] map, Player[] ps, AbstractBuilding[][] buildings, GameController controller){
        this (new Universe.Board(units, ps, map, buildings), controller);
    }

    public UniverseRenderer (String mapName, GameController controller) {
        this (Universe.restaure(mapName), controller);
    }

    public UniverseRenderer (String mapName, Player[] ps, GameController controller) {
        this (Universe.restaure(mapName).setPlayers(ps), controller);
    }

    public boolean isEnabled(int x, int y){
        return targets[y][x];
    }

    public void draw (Graphics g, int x, int y, int offsetX, int offsetY) {
        g.setFont(font);
        int w = map.board.length,
                h = map.board[0].length,
                firstX = x - (offsetX < 0 ? 1 : 0),
                firstY = y - (offsetY < 0 ? 1 : 0),
                lastX  = x + w + (offsetX > 0 ? 1 : 0),
                lastY  = y + h + (offsetY > 0 ? 1 : 0);

        for (int i = firstY; i < Math.min(lastY, map.board.length); i++)
            for (int j = firstX; j < Math.min(lastX, map.board[i].length); j++) {
                coords[i][j].x = (j - x) * MainFrame.UNIT - offsetX;
                coords[i][j].y = (i - y) * MainFrame.UNIT - offsetY;

                TerrainRenderer.render(g, coords[i][j], new Point(j, i));
                if (map.buildings[i][j] != null) BuildingRenderer.render(g, coords[i][j], map.buildings[i][j]);

                if (targets[i][j]) {
                    g.setColor(tColor);
                    g.fillRect(coords[i][j].x, coords[i][j].y, MainFrame.UNIT, MainFrame.UNIT);
                }
            }

        for (int i = firstY; i < Math.min(lastY, map.board.length); i++)
            for (int j = firstX; j < Math.min(lastX, map.board[i].length); j++) {
                if (!fogwar[i][j]) {
                    g.setColor(fogColor);
                    g.fillRect(coords[i][j].x, coords[i][j].y, MainFrame.UNIT, MainFrame.UNIT);
                }

                if (map.units[i][j] != null)
                    if (map.units[i][j].getPlayer() == current || isVisibleOpponentUnit(j, i))
                        UnitRenderer.render(g, coords[i][j], map.units[i][j]);
            }

        Iterator<FlashMessage> iterator = flashs.iterator();
        while (iterator.hasNext()) {
            FlashMessage message = iterator.next();
            g.setColor(message.type.color);
            g.drawString (message.message, message.x, message.y);
            message.time -= 10;
            if (message.time <= 0) iterator.remove();
        }

        Iterator<DeathAnimation> iterator2 = deathAnimation.iterator();
        while (iterator2.hasNext()){
            DeathAnimation d = iterator2.next();
            g.drawImage(d.getImage(), d.x * MainFrame.UNIT, d.y * MainFrame.UNIT, null);
            d.time --;
            if (d.time <= 0) iterator2.remove();
        }
    }

    public void next(){
        Weather w = weather;
        super.next();
        if (w != weather && controller != null) controller.makeView().getWeatherController().update(w);
    }

    public void updateTarget (AbstractUnit unit) {
        clearTarget();
        if (controller.getMode() == GameController.Mode.UNIT) {
            MoveZone m = unit.getMoveMap();
            int moveQuantity = unit.getMoveQuantity();
            Node[][] n = m.map;
            upperLeft = m.offset;
            lowerRight = new Point(upperLeft.x + n[0].length, upperLeft.y + n.length);
            for (int j = upperLeft.y; j < lowerRight.y; j++)
                for (int i = upperLeft.x; i < lowerRight.x; i ++)
                    targets[j][i] = n[j - upperLeft.y][i - upperLeft.x].lowestCost <= moveQuantity;
            tColor = unit.getPlayer() == current ? moveColor : targetColor;
        } else if (controller.getMode() == GameController.Mode.ATTACK) {
            unit.renderTarget(targets);
            upperLeft.move(0,0);
            lowerRight.move(targets.length, targets[0].length);
            tColor = targetColor;
        } else if (controller.getMode() == GameController.Mode.HEAL || 
                   controller.getMode() == GameController.Mode.LOAD || 
                   controller.getMode() == GameController.Mode.UNLOAD_LOCATE) {
            int x = unit.getX(), y = unit.getY();
            HealerUnit healer = controller.getMode() == GameController.Mode.HEAL ? (HealerUnit)unit : null;
            TransportUnit transporter = controller.getMode() == GameController.Mode.UNLOAD_LOCATE ? (TransportUnit)unit : null;

            for (Direction d : Direction.cardinalDirections()){
                int xx = x + d.x, yy = y + d.y;
                if ((controller.getMode() == GameController.Mode.HEAL && healer.canHeal(getUnit(xx, yy))) ||
                    (controller.getMode() == GameController.Mode.LOAD && getUnit(xx, yy) instanceof TransportUnit && ((TransportUnit)getUnit(xx, yy)).canCharge(unit)) ||
                    (controller.getMode() == GameController.Mode.UNLOAD_LOCATE && transporter.canRemove(controller.getTransportUnit(), xx, yy)))
                    targets[yy][xx] = true;
            }
            upperLeft.move(Math.max(0, unit.getX() - 1), Math.max(0, unit.getY() - 1));
            lowerRight.move(Math.min(getMapWidth(), unit.getX() + 2), Math.min(getMapHeight(), unit.getY() + 2));
            tColor = controller.getMode() == GameController.Mode.HEAL ? healColor : loadColor;
        }
    }

    private static final int[][] directions = {
        {1,1},{1,-1},{-1,-1},{-1,1}
    };

    public boolean updateTarget(Point p){
        clearTarget();
        if (controller.getMode() == GameController.Mode.MISSILE_LAUNCHER){
            for (int i = 0 ; i <= 3 ; i++)
                for (int j = 0 ; j <= i ; j ++)
                    for (int[] tab : directions){
                        int xx = p.x + tab[0] * j, yy = p.y + tab[1] * (i - j);
                        if (isValidPosition(xx, yy))
                            targets[yy][xx] = true;
                    }
            upperLeft.move(Math.max(0, p.x - 3), Math.max(0, p.y - 3));
            lowerRight.move(Math.min(getMapWidth(), p.x + 4), Math.min(getMapHeight(), p.y + 4));
            tColor = targetColor;
            return true;
        }
        return false;
    }

    public void clearTarget () {
        for (int i = upperLeft.x; i < lowerRight.x; i++)
            for (int j = upperLeft.y; j < lowerRight.y; j++)
                targets[j][i] = false;
        upperLeft.move(0,0);
        lowerRight.move(coords[0].length, coords.length);
    }

    public void flash (String message, int x, int y, int time) {
        flashs.add(new FlashMessage(message, x, y, time, FlashMessage.Type.SUCCESS));
    }

    public void flash (String message, int x, int y, int time, FlashMessage.Type type) {
        flashs.add(new FlashMessage(message, x, y, time, type));
    }

    private static final Image[] explosion = new Image[10];
    private static final Image[] sink      = new Image[6];

    static{
        Sprite s = Sprite.get("./assets/ingame/death.png");
        sink[0] = s.getImage(5, 59, 24, 32);
        sink[1] = s.getImage(5, 93, 24, 32);
        sink[2] = s.getImage(5, 127, 24, 32);
        sink[3] = s.getImage(5, 161, 24, 32);
        sink[4] = s.getImage(5, 195, 24, 32);
        sink[5] = s.getImage(5, 229, 24, 32);

        explosion[0] = s.getImage(35, 5, 33, 32);
        explosion[1] = s.getImage(35, 34, 33, 32);
        explosion[2] = s.getImage(35, 66, 33, 32);
        explosion[3] = s.getImage(35, 96, 33, 32);
        explosion[4] = s.getImage(35, 127, 33, 32);
        explosion[5] = s.getImage(35, 159, 33, 32);
        explosion[6] = s.getImage(35, 195, 33, 32);
        explosion[7] = s.getImage(35, 227, 33, 32);
        explosion[8] = s.getImage(35, 261, 33, 32);
        explosion[9] = s.getImage(35, 295, 33, 30);
    }

    /**
     * @param pt the position of the animation to display
     * @param sink is true to display the sink animation and false for the explosion
     */
    public void displayDeathAnimation(Point pt, boolean naval){
        deathAnimation.add(new DeathAnimation(pt, naval ? sink : explosion));
    }

    class DeathAnimation{
        public final int x, y;
        public int time;
        private final Image[] animation;

        public DeathAnimation(Point location, Image[] animation){
            this.x = location.x;
            this.y = location.y;
            this.animation = animation;
            this.time     = animation.length * 5;
        }

        public Image getImage(){
            return animation[(time - 1) / 5];
        }
    }

    @Override
    public boolean setUnit(int x, int y, AbstractUnit u){
        AbstractUnit unit = getUnit(x, y);
        if (unit != null && unit.dead()){
            UnitRenderer.remove(unit);
            displayDeathAnimation(new Point(x, y), unit instanceof NavalUnit);
        }
        return super.setUnit(x, y, u);
    }
}