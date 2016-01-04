 import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.Map.Entry;

public class Mapper {

	public static void main(String args[]) {
		// reading the folder to get the files in the folder
		 //File folder = new
		 //File("C:\\Users\\Paroma Sengupta\\Downloads\\testArrayFiles");
		 //File[] files = folder.listFiles();

		 //for (File f : files) {
		//if(f.isFile()) {
		BufferedReader inputStream;
		String fileName = System.getenv("mapreduce_map_input_file");
		//String[] filePart = fileName.split("-");
		String[] datePart = fileName.split("pagecounts-");
		String filePart = (String)(datePart[1].subSequence(0, 8));
		//System.out.println(fileName);
		// reading the file
		try {
			//inputStream = new BufferedReader(new FileReader(f));
			inputStream = new BufferedReader(new InputStreamReader(System.in));
			String strLine;// for each line in the files
			String[] stringArray;
			while ((strLine = inputStream.readLine()) != null) {
				Pattern pat = Pattern.compile("en\\s\\d{1,}\\s.*");
				Matcher match = pat.matcher((strLine));
				// System.out.println(strLine);
				stringArray = strLine.split(" ");
				if ((strLine.startsWith("en ")) && (stringArray.length==4)) {
					
					if ((stringArray[1].startsWith(("Media:"))
							|| (stringArray[1].startsWith("Special:"))
							|| (stringArray[1].startsWith("Talk:"))
							|| (stringArray[1].startsWith("User:"))
							|| (stringArray[1].startsWith("User_talk:"))
							|| (stringArray[1].startsWith("Project:"))
							|| (stringArray[1].startsWith("Project_talk:"))
							|| (stringArray[1].startsWith("File:"))
							|| (stringArray[1].startsWith("File_talk:"))
							|| (stringArray[1].startsWith("MediaWiki:"))
							|| (stringArray[1].startsWith("MediaWiki_talk:"))
							|| (stringArray[1].startsWith("Template:"))
							|| (stringArray[1].startsWith("Template_talk:"))
							|| (stringArray[1].startsWith("Help:"))
							|| (stringArray[1].startsWith("Help_talk:"))
							|| (stringArray[1].startsWith("Category:"))
							|| (stringArray[1].startsWith("Category_talk:"))
							|| (stringArray[1].startsWith("Portal:"))
							|| (stringArray[1].startsWith("Wikipedia:")) || (stringArray[1]
								.startsWith("Wikipedia_talk:")))) {
						continue;
					} else {
						Pattern pat1 = Pattern.compile("^[a-z].*");
						// [.+]+\\s[.+]+\\s[\\d{1,}]);
						match = pat1.matcher((stringArray[1]));
						if (!(match.matches())) {
							if (!((stringArray[1].endsWith(".jpg"))
									|| (stringArray[1].endsWith(".gif"))
									|| (stringArray[1].endsWith(".png"))
									|| (stringArray[1].endsWith(".JPG"))
									|| (stringArray[1].endsWith(".GIF"))
									|| (stringArray[1].endsWith(".PNG"))
									|| (stringArray[1].endsWith(".txt")) || (stringArray[1]
										.endsWith(".ico")))) {
								if (!((stringArray[1].equals("404_error/"))
										|| (stringArray[1].equals("Main_Page"))
										|| (stringArray[1]
												.equals("Hypertext_Transfer_Protocol")) || (stringArray[1]
											.equals("Search")))) {
									// writer.printf("%s\t%s\n",stringArray[1],stringArray[2]);
									String word = stringArray[1];
									int accessnumber = Integer
											.parseInt(stringArray[2]);

									System.out.println(word + "\t"
											+ filePart + "\t"
											+ stringArray[2]);
									// System.out.println(stringArray[0]+" "+stringArray[1]+" "+stringArray[2]+" "+stringArray[3]);

									// map.put(stringArray[1], accessnumber);
								}
							}
						}
					
					}
				}
			}
			inputStream.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}}

	// }

	// writer.close();
	// }

	

// HashMap<String, Integer> map = new HashMap<String, Integer>();
// ValueComparator bvc = new ValueComparator(map);
// TreeMap<String,Integer> sorted_map = new
// TreeMap<String,Integer>(bvc);

// InputStream gStream = new GZIPInputStream(inputstr);
/*
 * Reader rdr = new InputStreamReader(inputstr); BufferedReader br = new
 * BufferedReader(rdr); PrintWriter writer = new PrintWriter("D:\\Output.txt",
 * "UTF-8"); String strLine; String[] stringArray; while ((strLine =
 * br.readLine()) != null) { Pattern pat = Pattern.compile("en\\s\\d{1,}\\s.*");
 */
// [.+]+\\s[.+]+\\s[\\d{1,}]);

// System.out.println(strLine);
// System.out.println(strLine);

// Display.
/*
 * Map resultant = sortByValue(map);
 * 
 * 
 * 
 * System.out.println("First loop" + if1); System.out.println("2nd" + if2);
 * System.out.println("3rd" + if3); System.out.println("4th" + if4);
 * 
 * System.out.println("5th " + count);
 */

/*
 * static Map sortByValue(Map map) { List list = new LinkedList(map.entrySet());
 * Collections.sort(list, new Comparator() { public int compare(Object o1,
 * Object o2) { return ((Comparable) ((Map.Entry) (o1)).getValue())
 * .compareTo(((Map.Entry) (o2)).getValue()); } });
 * 
 * Map result = new LinkedHashMap(); for (Iterator it = list.iterator();
 * it.hasNext();) { Map.Entry entry = (Map.Entry) it.next();
 * result.put(entry.getKey(), entry.getValue()); } return result; }
 * 
 * }
 */

