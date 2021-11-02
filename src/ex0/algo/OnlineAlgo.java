package ex0.algo;
import java.util.Comparator;
import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.*;
/**
This class represents our online algorithm for elevator allocation. It tries to make the best
 elevator allocation for each call by calculating the distance in time from each elevator to the call.
 */
public class OnlineAlgo implements ElevatorAlgo {

    public static final int UP = 1, LEVEL = 0, DOWN = -1, ERROR = -2;
    private int _direction;
    private Building _building;
    //A data structure which holds the list of elevators with priority character of speed.
    PriorityQueue<Elevator> fastestElv;
    //elv that taking passenger from top to bottom
    ArrayList<ElvAlgo> downElv = new ArrayList<ElvAlgo>();
    //elv that taking passenger from bottom to top
    ArrayList<ElvAlgo> upElv = new ArrayList<ElvAlgo>();




    public OnlineAlgo(Building b) {
        _building = b;
        _direction = UP;
//       if(b.numberOfElevetors()>1){ -- need to add algo for 1  elv
        fastestElv = speedest(b);
        for (int i = 0; i < b.numberOfElevetors(); i++) {
            if (i % 2 == 0) {
                ElvAlgo tmp = new ElvAlgo(b.getElevetor(fastestElv.poll().getID()),new downComparator());//downComparator()
                downElv.add(downElv.size(), tmp);
                //we need to change this value according to the experience and data
                // that we will gather from the test case
                tmp.setStartingPoint((int)(_building.maxFloor()-Math.abs(_building.maxFloor()*0.3)));
            } else {
                ElvAlgo tmp = new ElvAlgo(b.getElevetor(fastestElv.poll().getID()),new upComparator());
                upElv.add(upElv.size(), tmp);
                //we need to change this value according to the experience and data
                // that we will gather from the test case
                tmp.setStartingPoint((int)(_building.minFloor()+Math.abs(_building.minFloor()*0.2)));
            }
        }
    }

    @Override
    public Building getBuilding() {
        return null;
    }

    @Override
    public String algoName() {
        return null;
    }


    public PriorityQueue<Elevator> speedest(Building b) {
        PriorityQueue<Elevator> pq = new PriorityQueue<Elevator>(b.numberOfElevetors(), new Comparator<Elevator>() {
            @Override
            public int compare(Elevator e1, Elevator e2) {
                return (int) (e1.getSpeed() - e2.getSpeed());
            }
        });

        for (int i = 0; i < b.numberOfElevetors(); i++) {
            pq.add(b.getElevetor(i));
        }
        return pq;
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
        int elevatorId = 0;
        //up
        if (c.getSrc() < c.getDest()) {
            ElvAlgo e = fastestToCallUp(c);
            elevatorId = e.getID();
        } else {
            ElvAlgo e = fastestToCallDown(c);
            elevatorId = e.getID();
        }
        return elevatorId;
    }

    public ElvAlgo fastestToCallUp(CallForElevator c) {//do in the symetric way!
        //initializing e AlgoVal
        for (ElvAlgo e : upElv) {
            e.setAlgoVal(0);
        }
        //calculating for each elv the time to reach the wanted floor
        for (ElvAlgo e : upElv) {
            int currFloor = e.getPos();
            //if we did not pass the floor yet
            if (e.getPos() <= c.getSrc()) {
                for (int floor_num : e.getFloorToStop()) {
                    if (floor_num <= c.getSrc()) {
                        e.setAlgoVal(e.getAlgoVal() + (floor_num - currFloor) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                        currFloor = floor_num;
                    }
                }
                if (currFloor != c.getSrc()) {
                    e.setAlgoVal(e.getAlgoVal() + (c.getSrc() - currFloor) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                }
            }
            //the elev already passed the requested floor thus we will check all the time that it take to reach her last destination,
            //and then we will add the waiting list until we reach the requested floor
            else {
                for (int floor_num : e.getFloorToStop()) {
                    e.setAlgoVal(e.getAlgoVal() + (floor_num - currFloor) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                    currFloor = floor_num;
                }
                if (!e.getWaitingList().isEmpty()) {

                    if (c.getSrc() >= e.getWaitingList().peek()) {
                        e.setAlgoVal(e.getAlgoVal() + (currFloor - e.getWaitingList().peek()) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                        currFloor = e.getWaitingList().peek();
                        for (int floor_num : e.getWaitingList()) {
                            if (c.getSrc() >= e.getWaitingList().peek()) {
                                e.setAlgoVal(e.getAlgoVal() + (floor_num - currFloor) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                                currFloor = floor_num;
                            }
                        }
                        e.setAlgoVal(e.getAlgoVal() + (c.getSrc() - currFloor) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                    }
                    e.setAlgoVal(e.getAlgoVal() + (currFloor - c.getSrc()) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                }
            }
        }
        //now ew check who got the lowest algoval - meaning the fastest route to the requested floor
        double tempSmallest = upElv.get(0).getAlgoVal();
        ElvAlgo fastestElv = upElv.get(0);
        for (ElvAlgo e : upElv) {
            if (tempSmallest > e.getAlgoVal()) {
                tempSmallest = e.getAlgoVal();
                fastestElv = e;
            }
        }
        if (c.getSrc() >= fastestElv.getPos()) {
            fastestElv.getFloorToStop().add(c.getSrc());
            fastestElv.getFloorToStop().add(c.getDest());
        } else {
            fastestElv.getWaitingList().add(c.getSrc());
            fastestElv.getWaitingList().add(c.getDest());
        }
        return fastestElv;
    }


    public ElvAlgo fastestToCallDown(CallForElevator c) {//do in the symetric way!
        //initializing e AlgoVal
        for (ElvAlgo e : downElv) {
            e.setAlgoVal(Integer.MAX_VALUE);//not sure where it's from
        }
        //calculating for each elv the time to reach the wanted floor
        for (ElvAlgo e : downElv) {
            int currFloor = e.getPos();
            //if we did not pass the floor yet
            if (e.getPos() >= c.getSrc()) {
                for (int floor_num : e.getFloorToStop()) {
                    if (floor_num >= c.getSrc()) {
                        e.setAlgoVal(e.getAlgoVal() + (currFloor - floor_num) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                        currFloor = floor_num;
                    }
                }
                if (currFloor != c.getSrc()) {
                    e.setAlgoVal(e.getAlgoVal() + (currFloor - c.getSrc()) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                }
            }
            //the elev already passed the requested floor thus we will check all the time that it take to reach her last destination,
            //and then we will add the waiting list until we reach the requested floor
            else {
                for (int floor_num : e.getFloorToStop()) {
                    e.setAlgoVal(e.getAlgoVal() + (currFloor - floor_num) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                    currFloor = floor_num;
                }
                if (!e.getWaitingList().isEmpty()) {

                    if (c.getSrc() <= e.getWaitingList().peek()) {
                        e.setAlgoVal(e.getAlgoVal() + (e.getWaitingList().peek() - currFloor) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                        currFloor = e.getWaitingList().peek();
                        for (int floor_num : e.getWaitingList()) {
                            if (c.getSrc() <= e.getWaitingList().peek()) {
                                e.setAlgoVal(e.getAlgoVal() + (currFloor - floor_num) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                                currFloor = floor_num;
                            }
                        }
                        e.setAlgoVal(e.getAlgoVal() + (currFloor - c.getSrc()) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                    }
                    e.setAlgoVal(e.getAlgoVal() + (currFloor - c.getSrc()) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                }
            }
        }
        //now ew check who got the lowest algoval - meaning the fastest route to the requested floor
        double tempSmallest = downElv.get(0).getAlgoVal();
        ElvAlgo fastestElv = downElv.get(0);
        for (ElvAlgo e : downElv) {
            if (tempSmallest > e.getAlgoVal()) {
                tempSmallest = e.getAlgoVal();
                fastestElv = e;
            }
        }
        if (c.getSrc() <= fastestElv.getPos()) {
            if (!fastestElv.getFloorToStop().contains(c.getSrc())) {
                fastestElv.getFloorToStop().add(c.getSrc());
            }
            if (!fastestElv.getFloorToStop().contains(c.getDest())) {
                fastestElv.getFloorToStop().add(c.getDest());
            }
        } else {
            if (!fastestElv.getWaitingList().contains(c.getSrc())) {
                fastestElv.getWaitingList().add(c.getSrc());
            }
            if (!fastestElv.getWaitingList().contains(c.getDest())) {
                fastestElv.getWaitingList().add(c.getDest());
            }
        }
        return fastestElv;
    }


    @Override
    public void cmdElevator(int elevID) {
        ElvAlgo elev = null;
        //diraction 0 for down, 1 for up
        int diraction = 0;
        //find the right elvAlgo with the given id
        for (ElvAlgo e : downElv) {
            if (e.getID() == elevID) {
                elev = e;
                break;
            }
        }
        //find the right elvAlgo with the given id if we did not find it in downElv
        if (elev == null) {
            diraction = 1;
            for (ElvAlgo e : upElv) {
                if (e.getID() == elevID) {
                    elev = e;
                    break;
                }
            }
        }
        //the ele
        int state = elev.getState();
        int currPos = elev.getPos();
        if (state != ERROR) {
            if (state == LEVEL) {
                //we check if the list does not empty
                if (!elev.getFloorToStop().isEmpty()) {

                    if (currPos == elev.getFloorToStop().peek()) {
                        //removing the first floor in our list
                        elev.getFloorToStop().poll();
                        //we then call again to the function to get further instruction
                        cmdElevator(elevID);
                    }
                    //the currPos != to the first val
                    else {

                        elev.goTo(elev.getFloorToStop().peek());
                    }
                }
                //the list getFloorToStop() isEmpty()
                else {
                    //both of the list are empty, so we will send the elv to the starting point
                    if (elev.getWaitingList().isEmpty()) {
                        if (currPos != elev.getStartingPoint()) {
                            elev.goTo(elev.getStartingPoint());
                        }
                    } else {
                        //we update our list to the waiting list
                        elev.getFloorToStop().addAll(elev.getWaitingList());
                        elev.getWaitingList().clear();
                        cmdElevator(elevID);
                    }
                }
            }
            //the state of the elv is error in this case -- we call maintenance :) --
            else {
                //do nothing the elv broke
            }

        }

    }

    public class upComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    public class downComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    }
}
