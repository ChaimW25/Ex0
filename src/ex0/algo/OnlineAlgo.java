package ex0.algo;
import java.util.Comparator;
import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.*;

public class OnlineAlgo implements ElevatorAlgo {

    public static final int UP = 1, LEVEL = 0, DOWN = -1, ERROR = -2;
    private int _direction;
    private Building _building;
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
                tmp.setStartingPoint(_building.maxFloor());
            } else {
                ElvAlgo tmp = new ElvAlgo(b.getElevetor(fastestElv.poll().getID()),new upComparator());
                upElv.add(upElv.size(), tmp);
                //we need to change this value according to the experience and data
                // that we will gather from the test case
                tmp.setStartingPoint(_building.minFloor());
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
                    //----------------------------------------------------------------------
                    if (elev.getFloorToStop().size() > 8) {
                        System.out.printf("elev id: %d \n", elev.getID());
                        System.out.print("elev getFloorToStop()\n");
                        for (Integer e : elev.getFloorToStop()) {
                            System.out.print(e + ", ");
                        }
                        System.out.print("\n\n");

                    }

                    //----------------------------------------------------------------------
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

//    private boolean[] _firstTime;
//    PriorityQueue<Integer> pQueue = new PriorityQueue<Integer>();
//    Queue<Integer> simple = new LinkedList<Integer>();
//    ArrayList<>
//    int busy = 0;
//
//    ArrayList<PriorityQueue> ar = new ArrayList<>();
//    HashMap<Integer, Queue<Integer>> simpleQ = new HashMap<Integer, Queue<Integer>>();
//
//
//    public OnlineAlgo(Building b) {
//        _building = b;
//        _direction = UP;
//
//        _firstTime = new boolean[_building.numberOfElevetors()];
////        for (int i = 0; i < _building.numberOfElevetors(); i++) {
//////            list_stop.put(i, new ArrayList<Integer>());
////        }
//
//        for (int i = 0; i < _firstTime.length; i++) {
//            _firstTime[i] = true;
//        }
//    }
//
//    public Building getBuilding() {
//        return _building;
//    }
//
//
//    @Override
//    public String algoName() {
//        {
//            return "Ex0_OOP_Online_Algo";
//        }
//    }
//
// an idea for each request we will check that all the elev dose not reach this floor
// if the indeed reach the floor we just return this elevator (by doing that we will not send two ele to the same floor)
//    @Override
//    public int allocateAnElevator(CallForElevator c) {
//        if (_building.numberOfElevetors() > 1) {
//            double minSpeed = Double.MAX_VALUE;
//            int currIndx = 0;
//            double a = 0;
//            for (int i = 0; i < _building.numberOfElevetors(); i++) {
//
//                if (_building.getElevetor(i).getState() == ERROR) {
//                    i++;
//                } else {
//                    if (_building.getElevetor(i).getState() == 0) {
//                        a = dist(c.getSrc(), i);
//                        if (a < minSpeed) {
//                            minSpeed = a;
//                            currIndx = i;
//                        }
//                        i++;
//                    }
//
//                    if (_building.getElevetor(i).getState() == -1) {
//                        if (c.getSrc() > c.getDest()) {//if the call direction is the elev direction
//                            if (_building.getElevetor(i).getPos() > c.getSrc()) {//if our call src in thr elev way
//                                ar.get(i).add(c.getSrc());//add this call to the elev queue
//                                ar.get(i).add(c.getDest());
//                            }
//                        }
//
//                    }
//
//
//                    if (_building.getElevetor(i).getState() == 1) {
//                    }
//                    i++;
//                }
//            }
//            return currIndx;
//        } else {
//            simpleQ.put(push(c.getSrc());
//            return 0;
//        }
//    }
//
//    @Override
//    public void cmdElevator(int elev) {
//
//        Elevator curr = this.getBuilding().getElevetor(elev);
//
//
////        if (curr.getState() == Elevator.LEVEL) {
////            int dir = this.getDirection();
////            int pos = curr.getPos();
////            boolean up2down = false;
////            if (dir == UP) {
////                if (pos < curr.getMaxFloor()) {
////                    curr.goTo(pos + 1);
////                } else {
////                    _direction = DOWN;
////                    curr.goTo(pos - 1);
////                    up2down = true;
////                }
////            }
////            if (dir == DOWN && !up2down) {
////                if (pos > curr.getMinFloor()) {
////                    curr.goTo(pos - 1);
////                } else {
////                    _direction = UP;
////                    curr.goTo(pos + 1);
////                }
////            }
////        }
//    }
//
//
//    public int getDirection() {
//        return this._direction;
//    }
//
//    private double dist(int src, int dest, int elev) {
//        Elevator thisElev = this._building.getElevetor(elev);
//        int pos = thisElev.getPos(); //the floor that the elevator are right now
//        double speed = thisElev.getSpeed();
//        return speed * (Math.abs(thisElev.getPos() - src)) + thisElev.getStopTime() + thisElev.getStartTime() + thisElev.getTimeForOpen() + thisElev.getTimeForClose();
//    }
//
//
//    private double dist(int src, int elev) {
//        double ans = -1;
//        Elevator thisElev = this._building.getElevetor(elev);
//        int pos = thisElev.getPos();
//        double speed = thisElev.getSpeed();
//        int min = this._building.minFloor(), max = this._building.maxFloor();
//        double up2down = (max - min) * speed;
//        double floorTime = speed + thisElev.getStopTime() + thisElev.getStartTime() + thisElev.getTimeForOpen() + thisElev.getTimeForClose();
//        if (elev % 2 == 1) { // up
//            if (pos <= src) {
//                ans = (src - pos) * floorTime;
//            } else {
//                ans = ((max - pos) + (pos - min)) * floorTime + up2down;
//            }
//        } else {
//            if (pos >= src) {
//                ans = (pos - src) * floorTime;
//            } else {
//                ans = ((max - pos) + (pos - min)) * floorTime + up2down;
//            }
//        }
//        return (int) ans;
//    }
//
//    public static void main(String[] args) {
//
//        ElevatorAlgo e = new OnlineAlgo(Simulator_A.getBuilding());
//        lift = e.allocateAnElevator(c); //every time in allocate my queue get fuller
//        ar.get(lift).add(c.src);
//        ar.get(lift).add(c.dest);
//
//
////        ElevatorAlgo a = new OnlineAlgo(Simulator_A.getBuilding());
////        System.out.println(a.algoName());
//
//        //    String codeOwner = codeOwner();
//        //    Simulator_A.setCodeOwner(codeOwner);
//        int stage = 2;  // any case in [0,9].
//        System.out.println("Ex0 Simulator: isStarting, stage=" + stage + ") ... =  ");
//        String callFile = null; // use the predefined cases [1-9].
//        // String callFile = "data/Ex0_stage_2__.csv"; //
//        Simulator_A.initData(stage, callFile);
//
//        OnlineAlgo ex0_alg = new OnlineAlgo(Simulator_A.getBuilding());
//        Simulator_A.initAlgo(ex0_alg); // init the algorithm to be used by the simulator
//
//        Simulator_A.runSim(); // run the simulation - should NOT take more than few seconds.
//
//        long time = System.currentTimeMillis();
//        String report_name = "out/Ex0_report_case_" + stage + "_" + time + "_ID_.log";
//        Simulator_A.report(report_name); // print the algorithm results in the given case, and save the log to a file.
//        //Simulator_A.report(); // if now file  - simple prints just the results.
//        Simulator_A.writeAllCalls("out/Ex0_Calls_case_" + stage + "_.csv"); // time,src,dest,state,elevInd, dt.
//
//    }
//}
