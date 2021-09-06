import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args){
        int numRows = 4;
        List<List<Integer>> result= new ArrayList<>();
        List<Integer> firstFloor = new ArrayList<>();
        firstFloor.add(1);
        result.add(firstFloor);
        int floor = 1;
        while (floor < numRows){
            floor++;
            List<Integer> lastFloor = new ArrayList<Integer>();
            List<Integer> lastFloorResult = result.get(result.size()-1);
            for (int i = 0; i < floor; i++) {
                int value;
                if (i==0||i==floor-1){
                    value = lastFloorResult.get(i==0?0:i-1);
                }else {
                    value = lastFloorResult.get(i-1) + lastFloorResult.get(i);
                }
                lastFloor.add(value);
            }
            result.add(lastFloor);
        }
        System.out.println(result);
    }
}
