package tank.game;

import it.randomtower.engine.ResourceManager;
import java.io.IOException;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author Gobinath
 */
public class TankGame extends StateBasedGame {

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new TankGame("Tank Game"));
        app.setDisplayMode(1280, 650, false);
        app.setTargetFrameRate(30);
        app.start();
    }

    public TankGame(String name) {
        super("Tank Game");
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        try {
            ResourceManager.loadResources("res/resources.xml");
        } catch (IOException ex) {
        }
        addState(new TankWorld(1, gc));
        enterState(1);
    }

}
