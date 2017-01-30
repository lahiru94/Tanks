package tank.game.components;

import it.randomtower.engine.entity.Entity;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import tank.game.Manifest;

/**
 *
 * @author Gobinath
 */
public class Cell extends Entity {

    final static int gap = Manifest.gap;
    final static int startX = Manifest.startX;
    final static int startY = Manifest.startY;

    private final float posX;
    private final float posY;

    public Cell(float x, float y) {
        super(x, y);
        posX = x;
        posY = y;
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        super.update(container, delta);

    }

    public float getPosX() {
        return x;
    }

    public float getPosY() {
        return y;
    }
}
