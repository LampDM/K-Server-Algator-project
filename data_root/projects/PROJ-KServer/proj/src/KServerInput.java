
import si.fri.algotest.execute.AbstractInput;

/**
 * @author ...
 */
public class KServerInput extends AbstractInput {

    int[][] ks;
    int[][] req;
    int x;
    int y;
    
    public KServerInput(int[][] servers, int[][] requests, int xaxis, int yaxis) {
        this.ks = servers;
        this.req = requests;
        this.x = xaxis;
        this.y = yaxis;
    }

    @Override
    public String toString() {
        
        // TODO: provide a handy KServerInput string representation (include only important data)
        return super.toString();
    }
}
