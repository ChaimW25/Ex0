package ex0.algo;

import ex0.Building;
import ex0.simulator.Simulator_A;
import org.junit.Test;

import static org.junit.Assert.*;

public class OnlineAlgoTest {
    Building B1;
    Building B5;
    Building B9;

    OnlineAlgo OnlineAlgo1;
    OnlineAlgo OnlineAlgo9;
    OnlineAlgo OnlineAlgo5;

    public OnlineAlgoTest() {
        Simulator_A.initData(1, null);
        B1 = Simulator_A.getBuilding();
        Simulator_A.initData(5, null);
        B5 = Simulator_A.getBuilding();
        Simulator_A.initData(9, null);
        B9 = Simulator_A.getBuilding();
    OnlineAlgo1 = new OnlineAlgo(B1);
    OnlineAlgo5 = new OnlineAlgo(B5);
    OnlineAlgo9 = new OnlineAlgo(B9);}


    @Test
    public void getBuilding() {
        assertEquals(B9.maxFloor(),OnlineAlgo9.getBuilding().maxFloor());
        assertEquals(B5.maxFloor(),OnlineAlgo5.getBuilding().maxFloor());
    }

    @Test
    public void algoName() {
        assertEquals("Ex0_OOP_Online_Algo",OnlineAlgo9.algoName());

    }

    @Test
    public void allocateAnElevator() {
        assertEquals(-5,B5.getElevetor(0).getPos());
        B5.getElevetor(0).goTo(3);
        assertEquals(1,OnlineAlgo5.getBuilding().getElevetor(0).getState());
        assertEquals(-2,B1.getElevetor(0).getPos());
        B1.getElevetor(0).goTo(3);
        assertEquals(1,OnlineAlgo1.getBuilding().getElevetor(0).getState());

    }


    @Test
    public void cmdElevator() {
        assertEquals(0,B5.getElevetor(4).getState());
        B5.getElevetor(4).goTo(3);
        OnlineAlgo5.cmdElevator(4);
        assertEquals(1,B5.getElevetor(4).getState());
        assertEquals(0,B9.getElevetor(0).getState());
        B9.getElevetor(0).goTo(5);
        OnlineAlgo9.cmdElevator(0);
        assertEquals(1,B9.getElevetor(0).getState());
    }



    }





