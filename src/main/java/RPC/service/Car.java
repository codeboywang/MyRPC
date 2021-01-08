package RPC.service;

public class Car implements Movable {
    static int MAX_DIS = 100;
    static int MAX_CAPACITY = 5;
    int remainedDis;
    String name;

    public Car(String name){
        this.name = name;
        remainedDis = MAX_DIS;
    }

    @Override
    public boolean move(int count, int distance) {
        System.out.println(this.toString() + "is moving...");
        if( MAX_CAPACITY < count ||remainedDis < distance){
            return false;
        }
        remainedDis -= distance;
        return true;
    }

    @Override
    public String toString() {
        return "Car{" +
                "remainedDis=" + remainedDis +
                ", name='" + name + '\'' +
                '}';
    }
}
