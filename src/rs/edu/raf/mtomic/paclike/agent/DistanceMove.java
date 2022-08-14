package rs.edu.raf.mtomic.paclike.agent;

public class DistanceMove {
    public final double dis;
    public final Runnable method;

    public DistanceMove(double dis, Runnable method) {
        this.dis = dis;
        this.method = method;
    }

}
