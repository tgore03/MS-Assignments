import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.swing.text.Document;
import javax.swing.text.html.HTML;


// You should call this code as follows:
//
//   java WebSearch directoryName searchStrategyName
//   (or jview, in J++)
//
//   where <directoryName> is the name of corresponding intranet
//   and <searchStrategyName> is one of {breadth, depth, best, beam}.

// The PARTIAL code below contains code for fetching and parsing
// the simple web pages we're using, as well as the fragments of
// a solution.  BE SURE TO READ ALL THE COMMENTS.

// Feel free to alter or discard whatever code you wish;
// the only requirement is that your main class be called WebSearch
// and that it accept the two arguments described above
// (if you wish you can add additional OPTIONAL arguments, but they
// should default to the values "hardwired" in below).

public class WWWebSearch 
{
	static LinkedList<SearchNode> OPEN; // Feel free to choose your own data structures for searching,
	static HashSet<String> CLOSED;      // and be sure to read documentation about them.
        static String path = "";            // Store the path followed to reach the goal.
        static PriorityQueue<SearchNode> CHILDLIST;

	static final boolean DEBUGGING = false; // When set, report what's happening.
        static final boolean SOUT = false; //Execute custom print statements
	// WARNING: lots of info is printed.

	static int beamWidth = 2; // If searchStrategy = "beam",
        static int priority;  //1 for BEST & -1 for BEAM
	// limit the size of OPEN to this value.
	// The setSize() method in the Vector
	// class can be used to accomplish this.

	static final String START_NODE     = "page1.html";

	// A web page is a goal node if it includes 
	// the following string.
	static final String GOAL_PATTERN   = "QUERY1 QUERY2 QUERY3 QUERY4";
        static final String WORD_PATTERN = "query";
        
        static String directoryName, searchStrategyName;

	public static void main(String args[])
	{ 
		if (args.length != 2)
		{
			System.out.println("You must provide the directoryName and searchStrategyName (BREADTH DEPTH BEST BEAM).  Please try again.");
		}
		else
		{
			directoryName = args[0]; // Read the search strategy to use.
			searchStrategyName = args[1]; // Read the search strategy to use.

			if (searchStrategyName.equalsIgnoreCase("breadth") ||
					searchStrategyName.equalsIgnoreCase("depth")   ||
					searchStrategyName.equalsIgnoreCase("best")    ||
					searchStrategyName.equalsIgnoreCase("beam"))
			{
                            if(searchStrategyName.equalsIgnoreCase("BEST")){
                                priority = 1;  
                            } else if(searchStrategyName.equalsIgnoreCase("BEAM")){
                                priority = -1;
                                try{
                                    Scanner sc = new Scanner(new InputStreamReader(System.in));
                                    System.out.println("Enter the Beam Width");
                                    beamWidth = sc.nextInt();
                                    if(beamWidth <1) {
                                        System.out.println("Invalid beam width");
                                        return;
                                    }
                                } catch (Exception e){
                                    System.out.println("Invalid Beam Width");
                                    return;
                                }
                            }
				performSearch(START_NODE, directoryName, searchStrategyName);
			}
			else
			{
				System.out.println("The valid search strategies are:");
				System.out.println("  BREADTH DEPTH BEST BEAM");
			}
		}

		Utilities.waitHere("Press ENTER to exit.");
	}

	static void performSearch(String startNode, String directoryName, String searchStrategy)
	{
            if(SOUT) System.out.println("");
            if(SOUT) System.out.println("");
		int nodesVisited = 0;

		OPEN   = new LinkedList<SearchNode>();
		CLOSED = new HashSet<String>();
                CHILDLIST = new PriorityQueue<SearchNode>();

		OPEN.add(new SearchNode(startNode));

		while (!OPEN.isEmpty())
		{
			SearchNode currentNode = pop(OPEN);
                        if(SOUT) System.out.println("\ncurrent node = " + currentNode.getNodeName());
			String currentURL = currentNode.getNodeName();
                        path += currentURL.toString() + ", ";   //Add currently visited node to path.
			nodesVisited++;

                        
                        // Change this part to obtain text from http.
                        String urlname = "http://www.cs.iastate.edu/";
                        URL url;
                        try{
                            url = new URL(urlname);
                            URLConnection uc = url.openConnection();
                            
                            InputStreamReader inputStreamReader = new InputStreamReader(uc.getInputStream());
                            Document d = 
                            
                        } catch(MalformedURLException mle){
                            if(SOUT) System.out.println("URL incorrect");
                        } catch(IOException ie){
                            if(SOUT) System.out.println("IO Exception");
                        }
                        
                        
			// Go and fetch the contents of this file.
			String contents = Utilities.getFileContents(directoryName
					+ File.separator
					+ currentURL);
                        if(SOUT) System.out.println("contents = " + contents);

			if (isaGoalNode(contents))
			{
				// Report the solution path found
				// (You might also wish to write a method that
				// counts the solution-path's length, and then print that
				// number here.)
                                System.out.println("\n\nGoal Node Found");
				currentNode.reportSolutionPath();
				break;
			}

			// Remember this node was visited.
			CLOSED.add(currentURL);

			addNewChildrenToOPEN(currentNode, contents, searchStrategy);

			// Provide a status report.
			if (DEBUGGING) System.out.println("Nodes visited = " + nodesVisited
					+ " |OPEN| = " + OPEN.size());
                        
                        if(searchStrategyName.equalsIgnoreCase("BEAM") && OPEN.isEmpty()){
                            //Trasfer the first Beam width values from priority queue to OPEN;
                            for(int i=0; i<beamWidth && i<CHILDLIST.size(); i++){
                                SearchNode node = CHILDLIST.remove();
                                OPEN.add(node);
                            }
                        }
		}

		System.out.println(" Visited " + nodesVisited + " nodes, starting @" +
				" " + directoryName + File.separator + startNode +
				", using: " + searchStrategy + " search.");
                System.out.println("\npath followed : \n" + path);
                
	}

	// This method reads the page's contents and
	// collects the 'children' nodes (ie, the hyperlinks on this page).
	// The parent node is also passed in so that 'backpointers' can be
	// created (in order to later extract solution paths).
	static void addNewChildrenToOPEN(SearchNode parent, String contents, String searchStrategy)
	{
		// StringTokenizer's are a nice class built into Java.
		// Be sure to read about them in some Java documentation.
		// They are useful when one wants to break up a string into words (tokens).
		StringTokenizer st = new StringTokenizer(contents);
                int childno = 0;
                
                //Priority Queue to store all children nodes and their fn values for BEST FIRST SEARCH
                if(searchStrategyName.equalsIgnoreCase("BEST")){
                    CHILDLIST = new PriorityQueue<SearchNode>();
                }
                
                //Variables to calculate the fn for each link
                int winpage=0, winlink=0, winseq=0, wpos=0, consno=0; // No. of Words in Page, No. of words in hypertext, No. of words in sequence, occurance of word, next expected sequence number
                double fn;
                
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
                           
			// Look for the hyperlinks on the current page.

			// (Lots a print statments and error checks are in here,
			// both as a form of documentation and as a debugging tool should you
			// create your own intranets.)

			// At the start of some hypertext?  Otherwise, ignore this token.
                        if(token.toLowerCase().contains(WORD_PATTERN)){
                            winpage++;
                            wpos++;
                        }
			if (token.equalsIgnoreCase("<A"))
			{       
				String hyperlink; // The name of the child node.
                                childno++;
                                winlink=0;
                                
				if (DEBUGGING) System.out.println("\nEncountered a HYPERLINK. Child No: " + childno);

				// Read: HREF = page#.html >

				token = st.nextToken();
				while (!token.equalsIgnoreCase("HREF"))
				{
					if(SOUT) System.out.println("Expecting 'HREF' and got: " + token);
                                        token = st.nextToken();
				}

				token = st.nextToken();
				while (!token.equalsIgnoreCase("="))
				{
					if(SOUT) System.out.println("Expecting '=' and got: " + token);
                                        token = st.nextToken();
				}

				// Now we should be at the name of file being linked to.
				hyperlink = st.nextToken();
				if (!hyperlink.startsWith("page"))
				{
					if(SOUT) System.out.println("Expecting 'page#.html' and got: " + hyperlink);
				}
                                
				token = st.nextToken();
				while (!token.equalsIgnoreCase(">"))
				{
					if(SOUT) System.out.println("Expecting '>' and got: " + token);
                                        token = st.nextToken();
				}
                                
				if (DEBUGGING) System.out.println(" - found a link to " + hyperlink);

				//////////////////////////////////////////////////////////////////////
				// Have collected a child node; now have to decide what to do with it.
				//////////////////////////////////////////////////////////////////////

				if (alreadyInOpen(hyperlink))
				{ // If already in OPEN, we'll ignore this hyperlink
					// (Be sure to read the "Technical Note" below.)
					if (DEBUGGING) System.out.println(" - this node is in the OPEN list.");
				}
				else if (CLOSED.contains(hyperlink))
				{ // If already in CLOSED, we'll also ignore this hyperlink.
					if (DEBUGGING) System.out.println(" - this node is in the CLOSED list.");
				}
				else 
				{ // Collect the hypertext if this is a previously unvisited node.
					// (This is only needed for HEURISTIC SEARCH, but collect in
					// all cases for simplicity.)
					String hypertext = ""; // The text associated with this hyperlink.

					do
					{
						token = st.nextToken();
						if (!token.equalsIgnoreCase("</A>")) hypertext += " " + token;
                                                if(token.toLowerCase().contains("query")) {
                                                    winlink++;
                                                    wpos++;
                                                    int no;
                                                    if((no=Integer.parseInt(token.substring(5))) <5){
                                                        if(no == consno+1){
                                                            consno++;
                                                            winseq++;
                                                        } else {
                                                            if(winseq==0) winseq=1;
                                                            consno=no;
                                                        }
                                                    }
                                                        
                                                }
					}
					while (!token.equalsIgnoreCase("</A>"));

					if (DEBUGGING) System.out.println("   with hypertext: " + hypertext);

					//////////////////////////////////////////////////////////////////////
					// At this point, you have a new child (hyperlink) and you have to
					// insert it into OPEN according to the search strategy being used.
					// Your heuristic function for best-first search should accept as 
					// arguments both "hypertext" (ie, the text associated with this 
					// hyperlink) and "contents" (ie, the full text of the current page).
					//////////////////////////////////////////////////////////////////////

					// Technical note: in best-first search,
					// if a page contains TWO (or more) links to the SAME page,
					// it is acceptable if only the FIRST one is inserted into OPEN,
					// rather than the better-scoring one.  For simplicity, once a node
					// has been placed in OPEN or CLOSED, we won't worry about the
					// possibility of later finding of higher score for it.
					// Since we are scoring the hypertext POINTING to a page,
					// rather than the web page itself, we are likely to get
					// different scores for given web page.  Ideally, we'd
					// take this into account when sorting OPEN, but you are
					// NOT required to do so (though you certainly are welcome
					// to handle this issue).

					// HINT: read about the insertElementAt() and addElement()
					// methods in the Vector class.
                                        
                                        //calculate the fn value for the link

                                        
                                        
                                        switch(searchStrategy.toUpperCase()){
                                            case "BREADTH": 
                                                if(SOUT)System.out.println("BREADTH CASE Followed");
                                                OPEN.add(new SearchNode(hyperlink));
                                                break;
                                            case "DEPTH": 
                                                if(SOUT)System.out.println("DEPTH CASE Followed");
                                                OPEN.add(new SearchNode(hyperlink));
                                                break;
                                            case "BEAM":
                                            case "BEST":
                                                if(SOUT)System.out.println("winpage: " + winpage + " winlink: " + winlink + " winseq: " + winseq + " wpos: " + wpos);
                                                fn = ((winpage + 2*winlink)* winseq) - 0.1*(wpos-winlink+1);
                                                if(fn<1) fn=1;
                                                fn = 1/fn;
                                                if(SOUT) System.out.println("fn = " + fn);
                                                CHILDLIST.add(new SearchNode(hyperlink, fn));
                                                
                                                //clean the variables for next link
                                                winlink=0;
                                                winseq=0;
                                                break;
                                        }
				}
			}
		}
                
                //add the child nodes to LinkedList based on heuristic
                if(searchStrategyName.equalsIgnoreCase("BEST")){
                    //check the contents of queue
                    if(SOUT) System.out.println(CHILDLIST);
                    while(!CHILDLIST.isEmpty()){
                        SearchNode node = CHILDLIST.remove();
                        OPEN.add(node);
                    }
                }
                
                if(SOUT) System.out.println("");
	}

	// A GOAL is a page that contains the goalPattern set above.
	static boolean isaGoalNode(String contents)
	{
		return (contents != null && contents.indexOf(GOAL_PATTERN) >= 0);
	}

	// Is this hyperlink already in the OPEN list?
	// This isn't a very efficient way to do a lookup,
	// but its fast enough for this homework.
	// Also, this for-loop structure can be
	// be adapted for use when inserting nodes into OPEN
	// according to their heuristic score.
	static boolean alreadyInOpen(String hyperlink)
	{
		int length = OPEN.size();

		for(int i = 0; i < length; i++)
		{
			SearchNode node = OPEN.get(i);
			String oldHyperlink = node.getNodeName();

			if (hyperlink.equalsIgnoreCase(oldHyperlink)) return true;  // Found it.
		}

		return false;  // Not in OPEN.    
	}

	// You can use this to remove the first element from OPEN.
	static SearchNode pop(LinkedList<SearchNode> list)
	{
            SearchNode result = null;
            switch(searchStrategyName.toUpperCase()){
                case "BREADTH":
                    result = list.removeFirst();
                    break;
                case "DEPTH":
                    result = list.removeLast();
                    break;
                case "BEST":
                    result = list.removeLast();
                    break;
                case "BEAM":
                    result = list.removeFirst();
            }
		return result;
        }
}

/////////////////////////////////////////////////////////////////////////////////

// You'll need to design a Search node data structure.

// Note that the above code assumes there is a method called getHvalue()
// that returns (as a double) the heuristic value associated with a search node,
// a method called getNodeName() that returns (as a String)
// the name of the file (eg, "page7.html") associated with this node, and
// a (void) method called reportSolutionPath() that prints the path
// from the start node to the current node represented by the SearchNode instance.
class SearchNode implements Comparable<SearchNode>
{
	final String nodeName;
        double fn = 1;
	public SearchNode(String name) {
		nodeName = name;
	}
        
        public SearchNode(String name, double value){
            nodeName = name;
            fn = value;
        }
	public void reportSolutionPath() {
            
	}

	public String getNodeName() {
		return nodeName;
	} 
        
        public double getfn(){
            return fn;
        }

    @Override
    public int compareTo(SearchNode o) {
        if(this.fn < o.fn)
            return WWWebSearch.priority;
        else if(this.fn == o.fn)
            return 0;
        else 
            return -WWWebSearch.priority;
    }
}

/////////////////////////////////////////////////////////////////////////////////

// Some 'helper' functions follow.  You needn't understand their internal details.
// Feel free to move this to a separate Java file if you wish.
class Utilities
{
	// In J++, the console window can close up before you read it,
	// so this method can be used to wait until you're ready to proceed.
	public static void waitHere(String msg)
	{
		System.out.println("");
		System.out.println(msg);
		try { System.in.read(); } catch(Exception e) {} // Ignore any errors while reading.
	}

	// This method will read the contents of a file, returning it
	// as a string.  (Don't worry if you don't understand how it works.)
	public static synchronized String getFileContents(String fileName)
	{
		File file = new File(fileName);
		String results = null;

		try
		{
			int length = (int)file.length(), bytesRead;
			byte byteArray[] = new byte[length];

			ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream(length);
			FileInputStream       inputStream = new FileInputStream(file);
			bytesRead = inputStream.read(byteArray);
			bytesBuffer.write(byteArray, 0, bytesRead);
			inputStream.close();

			results = bytesBuffer.toString();
                        
                        
                        
                        
                        
                        
                        ///
                        BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                        System.out.println(inputLine);
                         in.close();
		}
		catch(IOException e)
		{
			System.out.println("Exception in getFileContents(" + fileName + "), msg=" + e);
		}

		return results;
	}
}
