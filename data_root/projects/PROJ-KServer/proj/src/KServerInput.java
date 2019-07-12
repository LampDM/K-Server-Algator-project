
import si.fri.algotest.execute.AbstractInput;

/**
 * @author ...
 */
public class KServerInput extends AbstractInput {

    int[][] ks;
    int[][] req;
    int[] dimRange;
    
    public KServerInput(int[][] servers, int[][] requests, int[] dimRange) {
        this.ks = servers;
        this.req = requests;
        this.dimRange = dimRange;
    }

    @Override
    public String toString() {
        
        // TODO: provide a handy KServerInput string representation (include only important data)
        return super.toString();
    }
}
