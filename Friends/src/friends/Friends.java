package friends;

import java.util.ArrayList;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Integer> vBFS = new ArrayList<>();
		ArrayList<String> pBFS = new ArrayList<>();
		vBFS.add(g.map.get(p1));
		pBFS.add(p1);

		// holds the boolean values for whether or not each person has been visited
		boolean[] visit = new boolean[g.members.length];

		ArrayList<Integer> vDFS = new ArrayList<>();
		ArrayList<String> pDFS = new ArrayList<>();
		vDFS.add(g.map.get(p1));
		pDFS.add(p1);

		// declare queue to utilize the BFS algorithm, then add the first member
		Queue<Person> bfsQ = new Queue<>();
		bfsQ.enqueue(g.members[g.map.get(p1)]);

		if((!g.map.containsKey(p1)) || (!g.map.containsKey(p2))) {
			return new ArrayList<>();
		}

		Queue<ArrayList<String>> foo = new Queue<>();

		// declare and enqueue a new ArrayList for this queue
		ArrayList<String> bar = new ArrayList<>();
		bar.add(g.members[g.map.get(p1)].name);
		foo.enqueue(bar);

		if( !p1.equals(p2) ) {
			// perform a BFS & DFS
			hm1(g, g.members[g.map.get(p1)].first, g.map.get(p2), pBFS, vBFS);

			hm2(g, g.members[g.map.get(p1)].first, g.map.get(p2), pDFS, vDFS);
		}

		// repeat bfs procedure while the queue isn't empty
		while(!bfsQ.isEmpty()) {
			Person person = bfsQ.dequeue();
			visit[g.map.get(person.name)] = true;
			ArrayList<String> c = foo.dequeue();

			Friend friend = g.members[g.map.get(person.name)].first;

			// iterate through the person's friends
			while(friend != null) {
				// ensure they haven't been visited yet
				if(!visit[friend.fnum]) {
					// BFS procedure
					ArrayList<String> next = (ArrayList) c.clone();
					next.add(g.members[friend.fnum].name);
					if (friend.fnum == g.map.get(p2)) {
						return next;
					}
					bfsQ.enqueue(g.members[friend.fnum]);
					foo.enqueue(next);
				}
				// check next friends
				friend = friend.next;
			}
		}

		pBFS = (pBFS.size() == Math.pow(g.members.length, 2)) ? new ArrayList<>() : pBFS;
		pDFS = (pDFS.size() == Math.pow(g.members.length, 2)) ? new ArrayList<>() : pDFS;

		System.out.println( "\nBFS: " + pBFS + "\n");
		System.out.println( "\nDFS: " + pDFS + "\n");

		if ((pBFS.size() > 0) && (pDFS.size() > 0)) {
			return (pBFS.size() < pDFS.size()) ? pBFS : pDFS;
		}
		else if ((pBFS.size() == 0) && (pDFS.size() > 0)) {
			return pDFS;
		}
		else if((pDFS.size() == 0) && (pBFS.size() > 0)) {
			return pBFS;
		}
		else {
			return new ArrayList<>();
		}
	}

	// BFS algorithm
	private static void hm1( Graph g, Friend s, int t, ArrayList<String> p, ArrayList<Integer> v) {

		int maxArrSize = (int)(Math.pow( g.members.length, 2 ));
		if (p.size() < maxArrSize) {
			p.add(g.members[s.fnum].name);
		}
		else {
			return;
		}

		v.add(s.fnum);

		if(s.fnum == t) {
			return;
		}

		if (s.next != null && !v.contains(s.next.fnum)) {
			Friend ptr;
			for (ptr = s; v.contains(ptr.fnum); ptr = ptr.next) {}
			p.remove(p.size() - 1);
			hm1(g, ptr, t, p, v);
		} else {
			try {
				Friend ptr;
				for (ptr = g.members[s.fnum].first; v.contains(ptr.fnum); ptr = ptr.next) {}
				hm1(g, ptr, t, p, v);
			} catch( NullPointerException npe ) {
				hm1(g, g.members[s.fnum].first, t, p, v);
			}
		}
	}

	// DFS algorithm
	private static void hm2( Graph g, Friend s, int t, ArrayList<String> p, ArrayList<Integer> v) {

		int maxArrSize = (int)(Math.pow(g.members.length, 2));
		if (p.size() < maxArrSize) {
			p.add(g.members[s.fnum].name);
		}
		else {
			return;
		}

		v.add(s.fnum);

		if( s.fnum == t ) {
			return;
		}

		if( g.members[s.fnum].first != null && !v.contains(g.members[s.fnum].first.fnum)) {
			Friend ptr;
			for( ptr = s; v.contains( ptr.fnum ); ptr = g.members[s.fnum].first ) {}
			hm2(g, ptr, t, p, v);
		} else {
			try {
				Friend ptr;
				for (ptr = s.next; v.contains(ptr.fnum); ptr = ptr.next) { }
				p.remove(p.size() - 1);
				hm2(g, ptr, t, p, v);
			} catch( NullPointerException npe ) {
				hm2(g, g.members[s.fnum].first, t, p, v);
			}
		}
	}


	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		/** COMPLETE THIS METHOD **/

		// represents whether or not each person has been visited
		boolean[] visit = new boolean[g.members.length];
		ArrayList<ArrayList<String>> c = new ArrayList<>();

		for(Person p : g.members) {
			//if not a student or we've visited we don't have to worry about it and can continue the loop
			if(visit[g.map.get(p.name)] || !p.student) {
				continue;
			}

			//represents a new clique to be added to the list
			ArrayList<String> clique = new ArrayList<>();
			hm3(g, clique, school, p, visit);

			if(clique.size() > 0) {
				c.add(clique);
			}
		}
		return c;
	}

	private static void hm3( Graph g, ArrayList<String> c, String s, Person p, boolean[] v ) {
		int pInt = g.map.get( p.name );
		if( !v[pInt] && p.student && p.school.equals( s ) )
			c.add( p.name );
		v[pInt] = true;
		for( Friend friend = p.first; friend != null; friend = friend.next ) {
			int i = friend.fnum;
			Person friendP = g.members[i];
			if( !v[i] && friendP.student && friendP.school.equals(s) )
				hm3(g, c, s, g.members[i], v );
		}
	}

	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		/** COMPLETE THIS METHOD **/
		ArrayList<String> connectors = new ArrayList<>();
		boolean[] v = new boolean[g.members.length];
		int[] dfsN = new int[g.members.length];
		int[] b = new int[g.members.length];
		boolean[] bBool = new boolean[g.members.length];

		for( Person p : g.members ) {
			int pInt = g.map.get( p.name );
			if( v[pInt] )
				continue;

			hm4( g, v, connectors, p, dfsN, b, bBool, pInt, pInt );
		}

		return connectors;
		
	}

	private static void hm4( Graph g, boolean[] v, ArrayList<String> c, Person p, int[] dfsN, int[] b, boolean[] bBool, int before, int first ) {
		int i = g.map.get(p.name);
		if( v[i] )
			return;
		v[i] = true;
		dfsN[i] = dfsN[before] + 1;
		b[i] = dfsN[i];

		for( Friend f = p.first; f != null; f = f.next ) {
			if( v[f.fnum] )
				b[i] = Math.min( b[i], dfsN[f.fnum] );
			else {
				hm4(g, v, c, g.members[f.fnum], dfsN, b, bBool, i, first );

				if( !connectors.contains(p.name) && dfsN[i] <= b[f.fnum] ) {
					if( i != first || bBool[i] )
						c.add( p.name );
				}

				if( dfsN[i] > b[f.fnum] )
					b[i] = Math.min( b[i], b[f.fnum] );

				bBool[i] = true;
			}
		}
	}
}

