package ex0.algo;
import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
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
        return _building;
    }

    @Override
    public String algoName() {
        return "Ex0_OOP_Online_Algo";
    }

    /**
     * We create a new data structure of priority queue and creates speed comparator method as the priority character
     * @param b- our building field which holds a set of elevators
     * @return priority queue with priority of speed
     */

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
    /**
     * We split the calls scenario to up calls and down calls. there's a special method for each.
     * @param c the call for elevator (src, dest)
     * @return the index of the optimal time elevator for this call
     */
    @Override
    public int allocateAnElevator(CallForElevator c) {
        int elevatorId = 0;
        //if there's 1 elevator go just to the downCall
        if(_building.numberOfElevetors()==1){
            ElvAlgo e = fastestToCallDown(c);
            elevatorId=0;
        }
        //there's more than 1 elevator in the building
        else {
            //up
            if (c.getSrc() < c.getDest()) {
                ElvAlgo e = fastestToCallUp(c);
                elevatorId = e.getID();
                //down
            } else {
                ElvAlgo e = fastestToCallDown(c);
                elevatorId = e.getID();
            }
        }
        return elevatorId;
    }

    /**
     * This method search the optimal elevator allocation for up call by calculating the waiting
     * time for each elevator.
     * @param c- the input call
     * @return- the optimal elevator for this call
     */
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
                //calculating the time for stops in the floors between currFloor and all src
                for (int floor_num : e.getFloorToStop()) {
                    if (floor_num <= c.getSrc()) {
                        e.setAlgoVal(e.getAlgoVal() + (floor_num - currFloor) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                        currFloor = floor_num;
                    }
                }
                //calculating the time for the last stop to src floor
                if (currFloor != c.getSrc()) {
                    e.setAlgoVal(e.getAlgoVal() + (c.getSrc() - currFloor) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                }
            }
            //the elev already passed the requested floor thus we will check all the time that it take to reach her last destination,
            //and then we will add the waiting list until we reach the requested floor
            else {
                //calculating the time for floors left in this direction
                for (int floor_num : e.getFloorToStop()) {
                    e.setAlgoVal(e.getAlgoVal() + (floor_num - currFloor) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                    currFloor = floor_num;
                }
                if (!e.getWaitingList().isEmpty()) {

                    if (c.getSrc() >= e.getWaitingList().peek()) {
                        e.setAlgoVal(e.getAlgoVal() + (currFloor - e.getWaitingList().peek()) * e.getSpeed() + e.getStartTime() + e.getStopTime() + e.getTimeForOpen() + e.getTimeForClose());
                        currFloor = e.getWaitingList().peek();
                        //calculating the time for floors in the next round of up calls
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
        //now we check who got the lowest algoval - meaning the fastest route to the requested floor
        double tempSmallest = upElv.get(0).getAlgoVal();
        ElvAlgo fastestElv = upElv.get(0);
        for (ElvAlgo e : upElv) {
            if (tempSmallest > e.getAlgoVal()) {
                tempSmallest = e.getAlgoVal();
                fastestElv = e;
            }
        }
        //if the elevator didn't pass the src floor-> add it to this up round
        if (c.getSrc() >= fastestElv.getPos()) {
            fastestElv.getFloorToStop().add(c.getSrc());
            fastestElv.getFloorToStop().add(c.getDest());
            //if the elevator passed the src floor-> add it to next up round
        } else {
            fastestElv.getWaitingList().add(c.getSrc());
            fastestElv.getWaitingList().add(c.getDest());
        }
        return fastestElv;
    }

    /**
     * This method search the optimal elevator allocation for down call by calculating the waiting
     * time for each elevator. Similar to the method for the up calls.
     * @param c- the input call
     * @return- the optimal elevator for this call
     */
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
        //now we check who got the lowest algoval - meaning the fastest route to the requested floor
        double tempSmallest = downElv.get(0).getAlgoVal();
        ElvAlgo fastestElv = downElv.get(0);
        for (ElvAlgo e : downElv) {
            if (tempSmallest > e.getAlgoVal()) {
                tempSmallest = e.getAlgoVal();
                fastestElv = e;
            }
        }
        if (c.getSrc() <= fastestElv.getPos()) {
            fastestElv.getFloorToStop().add(c.getSrc());
            fastestElv.getFloorToStop().add(c.getDest());
            //if the elevator passed the src floor-> add it to next up round
        } else {
            fastestElv.getWaitingList().add(c.getSrc());
            fastestElv.getWaitingList().add(c.getDest());
        }
        return fastestElv;
    }

    /**
     * This method uses the elevtorAlgo API goTo as commands for each elevator
     * @param elevID
     */
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




    }
    /**
     * This comparator is for up calls priority queue. the priority-low number
     */
    public class upComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }
    /**
     * This comparator is for down calls priority queue. the priority-high number
     */
    public class downComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    }
}
