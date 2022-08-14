package rs.edu.raf.mtomic.paclike.agent;

public class Pair {
    private double h1;
    private double h2;
    private int trosak;

    public Pair(double h1, double h2,int trosak) {
        this.h1 = h1;
        this.h2 = h2;
        this.trosak = trosak;
    }

    public int getTrosak() {
        return trosak;
    }

    public double getH1() {
        return h1;
    }

    public double getH2() {
        return h2;
    }

    @Override
    public String toString() {
        return  " h1=" + h1 +
                " h2=" + h2 +
                " trosak=" + trosak;
    }
}
