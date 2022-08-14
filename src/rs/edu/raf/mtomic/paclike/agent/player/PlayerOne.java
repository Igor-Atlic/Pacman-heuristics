package rs.edu.raf.mtomic.paclike.agent.player;

import rs.edu.raf.mtomic.paclike.FieldState;
import rs.edu.raf.mtomic.paclike.GameState;
import rs.edu.raf.mtomic.paclike.agent.AvailableStruct;
import rs.edu.raf.mtomic.paclike.agent.DistanceMove;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static rs.edu.raf.mtomic.paclike.MathUtils.distance;

public class PlayerOne extends Player {
    private Runnable nextMove;
    private final double h1;
    private final double h2;


    public PlayerOne(GameState gameState,double h1, double h2){
        super(gameState);
         this.h1 = h1;
         this.h2 = h2;
    }

    @Override
    protected Runnable generateNextMove() {
        ArrayList<AvailableStruct> availableFields = getAvailableFields();
        ArrayList<Double> danger = new ArrayList<>();
        if (spriteCenterX % 8 == 4 && spriteCenterY % 8 == 4){

            for (int i = 0; i<4; i++){
                int targetX = gameState.getAgents().get(i).getGridX();
                int targetY = gameState.getAgents().get(i).getGridY();
                double dis = distance(this.getGridX(), this.getGridY(), targetX, targetY);
                danger.add(dis);
            }

            danger.sort(Double::compareTo);

            if ( danger.get(danger.size()-1) < h2){

                nextMove = bezi(availableFields);
                return nextMove;
            }

            if (h1 >= new Random().nextDouble()*10){
                nextMove = jedi(availableFields);
            }else{
                nextMove = bezi(availableFields);
            }


        }

        return nextMove;
    }

    private Runnable jedi(ArrayList<AvailableStruct> availableFields){

        ArrayList<Runnable> move = new ArrayList<>();
        Random r = new Random();
        Runnable move2 = availableFields.get(0).method;
        FieldState[][] fields = gameState.getFields();
        boolean flag = false;
        for (AvailableStruct availableField : availableFields) {
            int x = availableField.gridPosition.getKey();
            int y = availableField.gridPosition.getValue();
            FieldState field = fields[x][y];
            if (field.ordinal() == 2) {
                move.add(availableField.method);
            }
        }
        if (!move.isEmpty()){
            int next = r.nextInt(100)%move.size();
            return move.get(next);
        }else{
            for ( AvailableStruct a: availableFields) {
                ArrayList<AvailableStruct> tem2 = getAvailableFields2(a.gridPosition.getKey(),a.gridPosition.getValue());
                for (AvailableStruct availableStruct : tem2) {
                    int x = availableStruct.gridPosition.getKey();
                    int y = availableStruct.gridPosition.getValue();
                    FieldState field = fields[x][y];
                    if (field.ordinal() == 2) {
                        move2 = a.method;
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    break;
                }
            }

            if (!flag){
                move2 = bezi(availableFields);
            }
            return move2;
        }
    }

    private Runnable bezi(ArrayList<AvailableStruct> availableFields){

        ArrayList<DistanceMove> move = new ArrayList<>();
        for (int i = 0; i<4; i++){
            int targetX = gameState.getAgents().get(i).getGridX();
            int targetY = gameState.getAgents().get(i).getGridY();
            AvailableStruct tem = availableFields.stream()
                    .max(Comparator.comparingDouble(x -> distance(x.gridPosition.getKey(), x.gridPosition.getValue(), targetX, targetY)))
                    .orElse(availableFields.get(0));

            DistanceMove d = new DistanceMove(distance(tem.gridPosition.getKey(), tem.gridPosition.getValue(), targetX, targetY),tem.method);
            move.add(d);
        }


        DistanceMove d1 = move.stream().max(Comparator.comparingDouble(x -> x.dis)).get();

        return d1.method;


    }

    public double getH1() {
        return h1;
    }


    public double getH2() {
        return h2;
    }

}
