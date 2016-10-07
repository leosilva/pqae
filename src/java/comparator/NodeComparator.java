package comparator;

import java.util.Comparator;

import architecturevisualization.Node;

public class NodeComparator implements Comparator<Node> {

	@Override
	public int compare(Node n1, Node n2) {
		if (n1 == null) {
			return -1;
		} else if (n2 == null) {
			return 1;
		} else if (n1.getMember().equals(n2.getMember())) {
	        return 0;
	    }
	    return n1.getMember().compareTo(n2.getMember());
	}

}