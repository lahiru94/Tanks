package tank.game;

import it.randomtower.engine.ResourceManager;
import it.randomtower.engine.World;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import tank.game.astar.Path;
import tank.game.astar.ShortestPathFinder;
import tank.game.components.*;

/**
 *
 * @author Gobinath
 */
public class TankWorld extends World {

    public static int GRIDSIZE = Manifest.GRIDSIZE;
    private Image background, arenaImage;
    static public Cell[][] grid;
    private Controller controller;
    private final ArrayList<Player> players = new ArrayList<>();
    private int AIplayerNo;
    private ShortestPathFinder pathFinder;
    private final BlockingQueue<Coin> queue = new ArrayBlockingQueue<>(150);
    private Player player;

    public TankWorld(int id, GameContainer gc) {
        super(id, gc);
    }

    private void setup(StateBasedGame game) throws IOException {
        controller = Controller.getInstance();
        grid = new Cell[GRIDSIZE][GRIDSIZE];
        createArena();
        String reciveData;
        controller.sendMessage(Manifest.INITIALREQUEST);
        do {
            reciveData = controller.receiveMessage();
            if (reciveData.equals("PLAYERS_FULL") || reciveData.equals("GAME_ALREADY_STARTED")) {
                System.out.println("PLAYERS_FULL or GAME_ALREADY_STARTED");
                System.exit(0);
            }
            if ((!reciveData.equals("ALREADY_ADDED")) && (!(reciveData.charAt(0) == 'I')) && ((!reciveData.equals("GAME_ALREADY_STARTED")))) {
                controller.sendMessage(Manifest.INITIALREQUEST);
            }

        } while (!(reciveData.charAt(0) == 'I'));

        String[] section = reciveData.split(":");  //break into sections
        AIplayerNo = Integer.parseInt(section[1].charAt(1) + "");   //set AIplayer number 
        setBricks(section[2].split(";")); //set bricks
        setStones(section[3].split(";")); //set stones
        setWater(section[4].split(";"));  //set water

        do {
            reciveData = controller.receiveMessage();
        } while (!(reciveData.charAt(0) == 'S'));
        setPlayers(reciveData.split(":")); //set players from the initiation message
        initializeAStar();
    }

    private void initializeAStar() {
        int[][] obstacleMap = new int[Manifest.GRIDSIZE][Manifest.GRIDSIZE];
        for (int i = 0; i < Manifest.GRIDSIZE; i++) {
            for (int j = 0; j < Manifest.GRIDSIZE; j++) {
                Cell e;
                e = grid[i][j];
                if (e instanceof Brick || e instanceof Water
                        || e instanceof Stone) {
                    obstacleMap[j][i] = 1;
                }
            }
        }
        pathFinder = new ShortestPathFinder(Manifest.GRIDSIZE, Manifest.GRIDSIZE, obstacleMap);
        collectCoin();
    }

    private void setBricks(String[] bricks) {
        for (String brick : bricks) {
            String[] coordinates = brick.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            grid[x][y] = new Brick(grid[x][y].getPosX(), grid[x][y].getPosY());
            add(grid[x][y]);
        }
    }

    private void setStones(String[] stones) {
        for (String stone : stones) {
            String[] coordinates = stone.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            grid[x][y] = new Stone(grid[x][y].getPosX(), grid[x][y].getPosY());
            add(grid[x][y]);
        }

    }

    private void setWater(String[] waters) {
        for (String water : waters) {
            String[] coordinates = water.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            grid[x][y] = new Water(grid[x][y].getPosX(), grid[x][y].getPosY());
            add(grid[x][y]);
        }
    }

    @Override
    public void init(GameContainer gc, StateBasedGame game) throws SlickException {
        super.init(gc, game);
        gc.setAlwaysRender(true);
        gc.setUpdateOnlyWhenVisible(false);
        createBackground();
        container.setAlwaysRender(true);
        try {
            setup(game);
        } catch (IOException ex) {
        }
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame game, int delta) throws SlickException {
        super.update(gc, game, delta);
        String reciveData = controller.receiveMessage();
        String[] section = reciveData.split(":");
        switch (section[0]) {
            case "C":
                setCoins(section);
                break;
            case "L":
                setLifePacks(section);
                break;
            case "G":
                updatePlayers(section);
                break;
        }
    }

    @Override
    public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
        g.drawImage(background, 0, -130);
        g.drawImage(arenaImage, 20, 20);
        super.render(gc, game, g);
        setPointsTable(g);
    }

    private void setCoins(String[] section) {
        String[] coord = section[1].split(",");
        int x = Integer.parseInt(coord[0]);
        int y = Integer.parseInt(coord[1]);
        int lifetime = Integer.parseInt(section[2]);
        int value = Integer.parseInt(section[3]);
        Coin c = new Coin(x, y, grid[x][y].getPosX(), grid[x][y].getPosY(), value, lifetime);
        grid[x][y] = c;
        add(grid[x][y]);
        try {
            queue.put(c);
        } catch (InterruptedException ex) {

        }
    }

    private void setLifePacks(String[] section) {
        String[] coord = section[1].split(",");
        int x = Integer.parseInt(coord[0]);
        int y = Integer.parseInt(coord[1]);
        int lifetime = Integer.parseInt(section[2]);
        grid[x][y] = new LifePack(grid[x][y].getPosX(), grid[x][y].getPosY(), lifetime);
        add(grid[x][y]);
    }

    private void createBackground() {
        background = ResourceManager.getImage("background");
        arenaImage = ResourceManager.getImage("grid");
    }

    private void setPointsTable(Graphics g) {
        String spaceBetColumns = Manifest.pointTableColumnSpacing;
        int spaceBetRows = Manifest.pointTableRowSpacing;
        int textPositionX = Manifest.textPositionX;
        int textPositionY = Manifest.textPositionY;
        g.setLineWidth(g.getLineWidth() * 3);
        g.setColor(Color.white);
        g.drawString(String.format("%10s", "Player ID") + spaceBetColumns + String.format("%10s", "Points") + spaceBetColumns + String.format("%10s", "Coins") + spaceBetColumns + String.format("%10s", "Health"), textPositionX, textPositionY);
        for (int i = 0; i < players.size(); i++) {
            String pointsTableEntry = ((Player) players.get(i)).getPointsTableEntry();
            g.drawString(pointsTableEntry, textPositionX - 15, textPositionY + spaceBetRows * (i + 1));
        }
    }

    private void setPlayers(String[] playerSection) {
        for (int i = 1; i < playerSection.length; i++) {
            int x, y, direction, no;
            String[] data = playerSection[i].split(";");
            no = Integer.parseInt(data[0].charAt(1) + "");
            String[] position = data[1].split(",");
            x = Integer.parseInt(position[0]);
            y = Integer.parseInt(position[1]);
            direction = Integer.parseInt(data[2]);
            Player newPlayer = new Player(x, y, grid[x][y].getPosX(), grid[x][y].getPosY(), no, direction);
            if (AIplayerNo == no) {
                this.player = newPlayer;
            }
            add(newPlayer);
            players.add(newPlayer);
        }
    }

    private void updatePlayers(String[] section) {
        for (int i = 0; i < players.size(); i++) {
            Player get = (Player) players.get(i);
            get.setGlobleUpdate(section[i + 1]);
        }

    }

    private void createArena() {
        for (int i = 0; i < GRIDSIZE; i++) {
            for (int j = 0; j < GRIDSIZE; j++) {
                grid[i][j] = new Cell(Manifest.startX + Manifest.gap * i, Manifest.startY + Manifest.gap * j);
            }
        }
    }

    private void collectCoin() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Coin coin = queue.take();
                        if (player.getA() < Manifest.GRIDSIZE && player.getB() < Manifest.GRIDSIZE && coin.getA() < Manifest.GRIDSIZE && coin.getB() < Manifest.GRIDSIZE) {
                            System.out.println("Player: <" + player.getA() + " , " + player.getB() + ">  Coin: <" + coin.getA() + " , " + coin.getB() + ">");
                            Path path = pathFinder.getShortestPath(player.getB(), player.getA(), coin.getB(), coin.getA());
                            if (path != null) {
                                for (int i = 0; i < path.getLength(); i++) {
                                    int nX = path.getX(i);
                                    int nY = path.getY(i);
                                    System.out.print("<" + nX + " , " + nY + ">  ");
                                    System.out.println();

                                    if (player.getA() == nX) {
                                        if (player.getB() > nY) {
                                            controller.sendMessage("LEFT#");
                                        } else if (player.getB() < nY) {
                                            controller.sendMessage("RIGHT#");
                                        }
                                    }
                                    if (player.getB() == nY) {
                                        if (player.getA() > nX) {
                                            controller.sendMessage("UP#");
                                        } else if (player.getA() < nX) {
                                            controller.sendMessage("DOWN#");
                                        }
                                    }
                                    Thread.sleep(1100);
                                }
                            }
                        }
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }.start();
    }
}
