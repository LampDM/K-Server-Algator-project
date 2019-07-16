
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
        String s = String.format("k=%d,r=%d,dims=%d",this.ks.length,this.req.length,this.dimRange.length);
        return s;
    }
}
