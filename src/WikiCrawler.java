//package pa2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Lige Liu, Zhanghao Wen
 *
 */
public class WikiCrawler {

	private static final String BASE_URL = "https://en.wikipedia.org";
	
	private String seed;
	private int max;
	private String[] topics;
	private String output;
	
	private int numOfCalling;
	
	/**
	 * 
	 * @param seed related address of seed URL (within wiki domain)
	 * @param max maximum number of pages to consider
	 * @param topics array of strings representing keywords in a topic-list
	 * @param output string representing the lename where the web graph over discovered pages are written.
	 * @throws IOException
	 */
	public WikiCrawler(String seed, int max, String[] topics, String output) throws IOException {
		this.seed = seed;
		this.max = max;
		this.topics = topics;
		this.output = output;
		numOfCalling = 0;
	}

	/**
	 * the method takes as input a document representing an entire HTML document. 
	 * It returns a list of strings consisting of links from the document. 
	 * You can assume that the document is HTML from some wiki page. The method must
	 * (a) extract only relative addresses of wiki links, i.e., only links that are of the form /wiki/XXXX
	 * (b) only extract links that appear after the rst occurrence of the html tag <p> (or <P>)
	 * (c) Must not extract any wiki link that contain characters such as \#" or \:"
	 * (d) The order in which the links in the returned array list must be exactly the same order
	 * in which they appear in the document
	 * @param document
	 * @return
	 */
	public ArrayList<String> extractLinks(String document){
		ArrayList<String> list = new ArrayList<String>();
		
		// Could delete this line if hash set is not allowed
		HashSet<String> hashset = new HashSet<String>();
		//"href=\"/wiki/"
		
		String temp = document.toLowerCase();
		int indexOfFirstPTag = temp.indexOf("<p>");
		
		//Substring after first <p>
		String doc = document.substring(indexOfFirstPTag + 3, document.length());
		
		while(true) {
			int index = doc.indexOf("href=\"/wiki/");
		
			if (index == -1)
				break;
			
			doc = doc.substring(index + 6, doc.length());
			//temp = temp.substring(index + 6, temp.length());
			
			Scanner scan = new Scanner(doc);
			String link = scan.next();
			link = link.substring(0, link.indexOf("\""));
			
			if (!link.contains("#") && !link.contains(":")) {
				
				//Delete this if statement if hash set is not allowed
				if (!hashset.contains(link)) {
					list.add(link);
					
					//Delete this ...
					hashset.add(link);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * crawls/explores the web pages starting from the seed URL. 
	 * Crawl the rst max number of pages (including the seed page), 
	 * that contains every keywords in theTopics list 
	 * (if Topics list is empty then this condition is vacuously considered true), 
	 * and are explored starting from the seed.
	 * (a) if focused is false then explore in a BFS fashion
	 * (b) if focused is true then for every page a, compute the Relevance(T; a), 
	 * and during exploration, instead of adding the pages in the FIFO queue, 
	 * 		1. add the pages and their corresponding relevance (to topic) to priority queue. 
	 * 		   The priority of a page is its relevance; 
	 * 		2. extract elements from the queue using extractMax.
	 * After the crawl is done, the edges explored in the crawl method should be written to the output le.
	 * 
	 * @param focused
	 * @throws IOException
	 */
	public void crawl(boolean focused) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		int counter = 0;
		if (focused) {
			PriorityQ queue = new PriorityQ();
			list.add(seed);
			
			String content = exploreURL(seed);
			int relevance = relevance(content);
			
			queue.add(seed, relevance);
			
			while(!queue.isEmpty()) {
				ArrayList<String> vertexies = extractLinks(exploreURL(queue.extractMax()));
				
				for (int i=0; i<vertexies.size(); i++) {
					//for(String vertex: vertexies.toArray(new String[vertexies.size()])) {
					String vertex = vertexies.get(i);
					if (!list.contains(vertex)) {
						queue.add(vertex, relevance(vertex));
						list.add(vertex);
						counter++;
						
						if (counter == max)
							break;
					}
				}
				if (counter == max)
					break;
			}
			
		}
		else {
			//BFS
			Queue<String> queue = new LinkedList<String>();
			queue.add(seed);
			list.add(seed);
			while(!queue.isEmpty()) {
				ArrayList<String> vertexies = extractLinks(exploreURL(queue.poll()));
				
				for (int i=0; i<vertexies.size(); i++) {
				//for(String vertex: vertexies.toArray(new String[vertexies.size()])) {
					String vertex = vertexies.get(i);
					if (!list.contains(vertex)) {
						queue.add(vertex);
						list.add(vertex);
						counter++;
						
						if (counter == max)
							break;
					}
				}
				if (counter == max)
					break;
			}
		}
		
		File file = new File(output);
		FileWriter writer = new FileWriter(file);
		
		writer.write(max + "\r\n");
		for (int i=0; i<list.size(); i++) {
		//for (String page: list.toArray(new String[list.size()])) {
			String page = list.get(i);
			ArrayList<String> links = extractLinks(exploreURL(page));
			
			for (int j=0; j<links.size(); j++) {
			//for (String page2: links.toArray(new String[links.size()])) {
				String page2 = links.get(j);
				if (list.contains(page2) && !page.equals(page2)) {
					writer.write(page + " " + page2 +"\r\n");
				}
			}
		}
		writer.flush();
        writer.close();
	}
	
	/**
	 * Helper method for finding relevance of topics in content
	 * 
	 * @param content
	 * @return
	 */
	private int relevance(String content) {
		int relevance = 0;
		for (String topic: topics) {
			String temp = content;
			
			int index = temp.indexOf(topic);
			while (index != -1) {
				relevance++;
				
				temp = temp.substring(index + topic.length(), temp.length());
				index = temp.indexOf(topic);
			}
		}
		
		return relevance;
	}
	
	/**
	 * Helper method for retrieving content from website
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private String exploreURL(String url) throws IOException{
		URL fullurl = new URL(BASE_URL + url);
		InputStream is = fullurl.openStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
	    StringBuilder document = new StringBuilder();
	    String line;
	    while( (line = br.readLine()) != null) {
	    	document.append(line);
	    }
	    
	    numOfCalling++;
	    numOfCalling %= 20;
	    
	    if (numOfCalling == 19) {
	    	try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    return document.toString();
	}

}
