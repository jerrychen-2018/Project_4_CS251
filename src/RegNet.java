import java.util.ArrayList;
import java.util.Arrays;

public class RegNet
{
    //creates a regional network
    //G: the original graph
    //max: the budget
    public static Graph run(Graph G, int max) 
    {
    	System.out.println("max: " + max);

	    ArrayList<Edge> remain = G.sortedEdges();
	    UnionFind uf = new UnionFind(G.V());
		Graph mst = new Graph(G.V());
		mst.setCodes(G.getCodes());

	    while ((!remain.isEmpty()) ) {
	        Edge edge = remain.remove(0);
	        int x_set = uf.find(edge.ui());
	        int y_set = uf.find(edge.vi());

	        if (x_set != y_set) {
	            mst.addEdge(edge);
	            uf.union(x_set, y_set);
            }
        }

	    while (mst.totalWeight() > max) {
	    	ArrayList<Edge> mstEdges = mst.sortedEdges();
			mst = mst.connGraph();
			for (int i = mstEdges.size() - 1; i >= 0; i--) {
				Graph temp = mst;
				Edge e = mstEdges.get(i);
				temp.removeEdge(e);
				temp = temp.connGraph();
				if (isConnected(temp)) {
					mst = temp;
					break;
				}
				else {
					temp.addEdge(e);
					mst = temp;
				}
			}
		}

	/*
		ArrayList<Edge> sortMSTEdges = mst.edges();
		index = sortMSTEdges.size() - 1;
		while (mst.totalWeight() > max && (index >= 0)) {
			Edge biggestEdge = sortMSTEdges.get(index);
			if ((mst.deg(biggestEdge.u)) > 1 && (mst.deg(biggestEdge.v) > 1)) {
				index--;
				continue;
			}
			mst.removeEdge(biggestEdge);
			mst = mst.connGraph();
			index--;
		}
		mst = mst.connGraph();
	*/

		ArrayList<int[]> list = new ArrayList<>();

		for (int i = 0; i < mst.V(); i++) {
			int[] stop_arr = new int[mst.V()];
			boolean[] visited = new boolean[mst.V()];
			dfs(mst, i, 0, stop_arr, visited);
			for (int j = i + 1; j < mst.V(); j++) {
				int[] arr = new int[4];
				arr[0] = i;
				arr[1] = j;
				arr[2] = stop_arr[j];
				Edge e = G.getEdge(i, j);
				arr[3] = e.w;
				if (arr[2] != 0)
					list.add(arr);
			}
		}

		int[][] stop_res = new int[list.size()][3];
		for (int i = 0; i < list.size(); i++) {
			stop_res[i] = list.get(i);
		}
		Arrays.sort(stop_res, (a, b)->b[2] == a[2] ? Integer.compare(b[3], a[3]) : Integer.compare(a[2], b[2]));
		for (int i = stop_res.length - 1; i>= 0; i--) {
			int[] arr = stop_res[i];
			Edge e = G.getEdge(arr[0], arr[1]);
			if (e.w + mst.totalWeight() > max) {
				continue;
			}
			else {
				mst.addEdge(e);
			}
		}


	    return mst;
    }

    public static void dfs(Graph g, int start, int num_stop, int[] stop_arr, boolean[] visited) {
    	ArrayList<Integer> list = g.adj(start);
    	for (int i: list) {
    		if (!visited[i]) {
    			stop_arr[i] = num_stop;
    			visited[i] = true;
    			dfs(g, i, num_stop + 1, stop_arr, visited);
			}
		}
	}

	public static void connDFS(ArrayList<Integer>[] adjList, int v, boolean[] visited) {
		visited[v] = true;
		for (int i = 0; i < adjList[v].size(); i++) {
			int neighbor = adjList[v].get(i);
			if (!visited[neighbor]) {
				connDFS(adjList, neighbor, visited);
			}
		}
	}

	public static boolean isConnected(Graph g) {
    	boolean[] visited = new boolean[g.V()];
		ArrayList<Integer>[] adjList = g.getAdjList();
    	connDFS(adjList, 0, visited);
    	int count = 0;
    	for (int i = 0; i < visited.length; i++) {
    		if (visited[i]) {
    			count++;
			}
		}

    	return count == g.V();
	}
}