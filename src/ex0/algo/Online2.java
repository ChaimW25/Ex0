//package ex0.algo;
//import ex0.Building;
//import ex0.CallForElevator;
//import ex0.Elevator;
//import ex0.simulator.Simulator_A;
//
//import java.util.*;
//public class Online2 implements ElevatorAlgo {
//
//
//
//        public static final int UP=1, LEVEL = 0, DOWN = -1, ERROR = -2;
//
//        private int _direction;
//        private Building _building;
//        private LinkedList<CallForElevator>[] _firstTime; //we build an array list of calls
//        private CallForElevator[] n;
//
//
//
//        public Online2(Building b) { //our algorithm constructor
//            _building = b;
//            _direction = UP; //default value for direction
//            _firstTime = new LinkedList[_building.numberOfElevetors()]; // there's no need with size in array list
//            n =new CallForElevator[_building.numberOfElevetors()];
//        }
//        //returns the building of the OnlineAlgo
//        public Building getBuilding() {return _building;}
//
//        //returns the name of the algorithm
//        @Override
//        public String algoName() {
//            {
//                return "Ex0_OOP_Online_Algo";
//            }
//        }
//
//
//        //gets a call which includes src and dest and allocate the optic elevator for this call
//        @Override
//        public int allocateAnElevator(CallForElevator c) {
//            double minSpeed = dist(c.getSrc(),0);//first initalization of the time distance from the first elevator
//            double a;
//            int currIndx = 0;//new integer value that will return the index of the optimal elevator for the call
//
//            for (int i = 0; i < _building.numberOfElevetors(); i++)
//            {// a loop on all the elevators
//
//                if (_building.getElevetor(i).getState()==ERROR){}//if the elev in 'error' state,continue.
//
//                else if (_building.getElevetor(i).getState()==LEVEL)//if the elev is in 'level' state- check the distance to thr src floor
//                {
//                    a=dist( c.getSrc(), i);//calculate the dist between elev and c's src
//                    if(a<minSpeed)//
//                    {
//                        minSpeed=a;
//                        currIndx=i;
//                    }
//                }
//
//                else if (_building.getElevetor(i).getState()==DOWN)
//                {
//                    if (c.getSrc()>c.getDest())//
//                    {//if the call direction is the elev direction
//                        if (_building.getElevetor(i).getPos() > c.getSrc())
//                        {//if our call src in the elev way
//                            a = dist(c.getSrc(), i);
//                            if (a < minSpeed)
//                            {
//                                minSpeed = a;
//                                currIndx = i;
//                            }
//                        }
//                        else {a = NotOnTheWay(c.getSrc(),i);}//not in the way
//
//                    }
//                }
//
//                else //the elevator direction =UP
//                {
//                    if (c.getSrc()<c.getDest())//if the call direction is the elev direction
//                    {
//                        if (_building.getElevetor(i).getPos()<c.getSrc())//if our call src in thr elev way
//                        {
//                            a = dist(c.getSrc(), i);
//                            if (a < minSpeed) {
//                                minSpeed = a;
//                                currIndx = i;
//                            }
//                        }
//                        else
//                        {
//                            a = NotOnTheWay(c.getSrc(),i);
//                            if (a < minSpeed) {
//                                minSpeed = a;
//                                currIndx = i;
//                            }
//                        }
//                    }
//                }
//
//            }
//            _firstTime[currIndx].add(c);
//
//
//            return currIndx;
//        }
//
//        @Override
//        public void cmdElevator(int elev) {
//            int count = 0, CallScr, CallDest;
//            Elevator curr = this.getBuilding().getElevetor(elev);
//            while (_firstTime[elev].get(count) != null) {
//                CallScr = _firstTime[elev].get(count).getSrc();
//                CallDest = _firstTime[elev].get(count).getDest();
//                if (curr.getState() == 0) {
//                    curr.goTo(CallScr);
//                    curr.goTo(CallDest);
//                    _firstTime[elev].get(count).getState() =3;
//                }
//                count++;
//
//            }
//
//        }
//
//        public int getDirection() {return this._direction;}
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
//            ans = Math.abs(pos-src)*floorTime;
////            if (pos <= src) {
////                ans = (src - pos) * floorTime;
////            } else {
////                ans = ((max - pos) + (pos - min)) * floorTime + up2down;
////            }
////        } else {
////            if (pos >= src) {
////                ans = (pos - src) * floorTime;
////            } else {
////                ans = ((max - pos) + (pos - min)) * floorTime + up2down;
////            }
//        }
//        return  ans;
//    }
//
////        private double dist(int src,int dest, int elev) {
////            double ans = -1;
////            Elevator thisElev = this._building.getElevetor(elev);
////            int pos = src;
////            double speed = thisElev.getSpeed();
////            int min = this._building.minFloor(), max = this._building.maxFloor();
////            double up2down = (max-min)*speed;
////            double floorTime = speed+thisElev.getStopTime()+thisElev.getStartTime()+thisElev.getTimeForOpen()+thisElev.getTimeForClose();
////            if(elev%2==1) { // up
////                if(pos<=src) {ans = (src-pos)*floorTime;}
////                else {
////                    ans = ((max-pos) + (pos-min))*floorTime + up2down;
////                }
////            }
////            else {
////                if(pos>=src) {ans = (pos-src)*floorTime;}
////                else {
////                    ans = ((max-pos) + (pos-min))*floorTime + up2down;
////                }
////            }
////            return (int) ans;
////        }
//
//        private  int NotOnTheWay(int floor,int elev) {
//            int a;
//            int count = 0;
//            CallForElevator n = (CallForElevator) _firstTime[elev].get(count);
//            int pose = 0;
//            double ans = 0;
//
//            Elevator elevtor = this._building.getElevetor(elev);
//            while ((_firstTime[elev].get(count) != null)||(((CallForElevator) _firstTime[elev].get(count)).getState()!=3)){
//                if (_building.getElevetor(elev).getState() == -1) //if on the way
//                {
//                    if (n.getSrc() > n.getDest())//if the call direction is the elev direction
//                    {
//                        if (_building.getElevetor(elev).getPos() > n.getSrc()) //if our call src in thr elev way
//                        {
//                            ans += dist(n.getDest(), pose, elev);
//                            return (int) ans;
//                        }
//                    }
//                }
//
//                ans += dist(n.getSrc(), pose, elev);//0-5
//                pose = n.getSrc();
//                ans += dist(n.getDest(), pose, elev);//5-7
//                pose = n.getDest();//7
//                count++;
//
//            }
//            return (int)ans;
//        }
//}
