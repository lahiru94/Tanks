package tank.game.components;

import it.randomtower.engine.ME;
import it.randomtower.engine.ResourceManager;
import it.randomtower.engine.entity.Entity;
import org.newdawn.slick.*;

/**
 *
 * @author Gobinath
 */
public class Coin extends Cell {

    public static String COIN = "coin";
    private final Image coin;
    private final int life;
    private final int value;
    int timelived = 0;
    private final int fireRate = 50;
    private final int miliCount = 0;
    private final int milis = fireRate;
    private final int miliStep = milis / 5;
    private int a;
    private int b;

    public Coin(int a, int b, float x, float y, int value, int life) {
        super(x, y);
        this.a = a;
        this.b = b;
        this.value = value;
        this.life = life;
        this.x = super.x;
        this.y = super.y;
        this.depth = 5;
        coin = ResourceManager.getImage("coin");
        setGraphic(coin);
        setHitBox(0, 0, coin.getWidth(), coin.getHeight());
        addType(COIN);
    }

    public int getValue() {
        return value;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    
    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        super.render(container, g);
        g.setColor(Color.black);
        g.drawString(value + "", x + 5, y + 10);
        g.drawString(life + "", x + 5, y + 30);
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        super.update(container, delta);

        timelived += delta;
        if (timelived >= life) {
            ME.world.remove(this);
        }
    }

    @Override
    public void collisionResponse(Entity other) {
        ME.world.remove(this);
    }

}
