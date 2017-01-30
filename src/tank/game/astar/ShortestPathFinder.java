package tank.game.astar;

public class ShortestPathFinder {

    private final AreaMap map;
    private final ClosestGoal heuristic;
    private final AStar pathFinder;

    public ShortestPathFinder(int mapWidth, int mapHeight, int[][] obstacleMap) {
        map = new AreaMap(mapWidth, mapHeight, obstacleMap);
        heuristic = new ClosestGoal();
        pathFinder = new AStar(map, heuristic);
    }

    public Path getShortestPath(int startX, int startY, int goalX, int goalY) {
        return pathFinder.calcShortestPath(startX, startY, goalX, goalY);
    }
}
