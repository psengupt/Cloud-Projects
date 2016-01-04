import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Reducer {

	public static void main(String[] args) {
		try {
			// reader for taking in the system output
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			//PrintWriter writer = new PrintWriter("D:\\Output.txt");
			String input;
			String word = null;
			String currentWord = null;
			int currentCount = 0;

			int totalCount = 0;
			HashMap<String, Integer> values = new HashMap();
			// initializing my HashMap that stores the count per day
			for (int i = 1; i < 31; i++) {
				if (i < 10) {

					values.put("2014110" + i, 0);
					// System.out.println("Initializing "+" "+"2014110"+i+" "+values.get("2014110"+i));
				} else {
					values.put("201411" + i, 0);
				}

			}
			Map<String, Integer> tree = null; 
			while ((input = br.readLine()) != null) {
				try {
					String[] parts = input.split("\t");
					word = parts[0];
					// System.out.println(word);
					int count = Integer.parseInt(parts[2]);
					// int date = Integer.parseInt(parts[1]);
					if ((currentWord != null) && (currentWord.equals(word))) {
						totalCount = totalCount + count;
						currentCount = values.get(parts[1]);
						// System.out.println("The count of "+word+" "+currentCount);
						currentCount = currentCount + count;
						values.put(parts[1], currentCount);
					}

					else {
						// System.out.println("The total count of "+currentWord+" "+currentCount);
						if (currentWord != null) {
							if (totalCount > 100000) {
								// printing out the record
								System.out.printf("%d\t%s\t", totalCount,
										currentWord);
								tree = new TreeMap<String, Integer>(values);
								Iterator it = tree.entrySet().iterator();
								while (it.hasNext()) {
									Map.Entry pairs = (Map.Entry) it.next();
									System.out.printf("%s:%d\t",
											pairs.getKey(), pairs.getValue());
									// count++;
									it.remove(); // avoids a
													// ConcurrentModificationException
								}
								System.out.println();
							}
							totalCount = 0;
						}
						currentWord = word;
						currentCount = count;
						totalCount = totalCount + count;
						values = null;
						values = new HashMap<String, Integer>();
						// initializing my HashMap
						for (int i = 1; i < 31; i++) {
							if (i < 10)
								values.put("2014110" + i, 0);
							else
								values.put("201411" + i, 0);
						}
						tree = new TreeMap<String, Integer>(values);
						values.put(parts[1], currentCount);
						
					}

					// currentDate = date;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (currentWord != null && (totalCount > 100000)) {
				System.out.printf("%d\t%s\t", totalCount, currentWord);
				tree = new TreeMap<String, Integer>(values);
				Iterator it = tree.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					System.out.printf("%s:%d\t", pairs.getKey(),
							pairs.getValue());
					// count++;
					it.remove(); // avoids a ConcurrentModificationException
				}
				System.out.println();
			}
			br.close();
			//writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
