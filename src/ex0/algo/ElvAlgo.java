package ex0.algo;
import java.util.*;

import ex0.Building;
import ex0.Elevator;

public class ElvAlgo implements Elevator {

    //hold the elevator that given when we construct the class
    private Elevator _e;
    //hold the list of the floor that we need to stop
    private PriorityQueue<Integer> floorToStop;
    //if we already passed the floor we add the given floor to the waiting list
    private PriorityQueue<Integer> waitingList;
    //variable that used in the algorithms
    double algoVal;

    public double getAlgoVal() {
        return algoVal;
    }

    public void setAlgoVal(double algoVal) {
        this.algoVal = algoVal;
    }

    ElvAlgo(Elevator elv){
        _e=elv;
        algoVal=0;
    }

    @Override
    public int getMinFloor() {
        return _e.getMinFloor();
    }

    @Override
    public int getMaxFloor() {
        return _e.getMaxFloor();
    }

    @Override
    public double getTimeForOpen() {
        return _e.getTimeForOpen();
    }

    @Override
    public double getTimeForClose() {
        return _e.getTimeForClose();
    }

    @Override
    public int getState() {
        return _e.getState();
    }

    @Override
    public int getPos() {
        return _e.getPos();
    }

    @Override
    public boolean goTo(int floor) {
        return _e.goTo(floor);
    }

    @Override
    public boolean stop(int floor) {
        return _e.stop(floor);
    }

    @Override
    public double getSpeed() {
        return _e.getSpeed();
    }

    @Override
    public double getStartTime() {
        return _e.getStartTime();
    }

    @Override
    public double getStopTime() {
        return _e.getStopTime();
    }

    @Override
    public int getID() {
        return _e.getID();
    }

    public Elevator get_e() {
        return _e;
    }


    public PriorityQueue<Integer> getFloorToStop() {
        return floorToStop;
    }

    public void setFloorToStop(PriorityQueue<Integer> floorToStop) {
        this.floorToStop = floorToStop;
    }

    public PriorityQueue<Integer> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(PriorityQueue<Integer> waitingList) {
        this.waitingList = waitingList;
    }
}
