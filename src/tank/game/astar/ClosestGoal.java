package tank.game.astar;

public class ClosestGoal  { // change name

    public float getEstimatedDistanceToGoal(int startX, int startY, int goalX, int goalY) {
        float dx = goalX - startX;
        float dy = goalY - startY;
        float result = (float) (dx * dx) + (dy * dy);
        return result;
    }

}
