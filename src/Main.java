
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Main {

	
	// Initialize static final set with categories. Can easily add new known categories to list here.
	private static final Set<String> CATS = new HashSet<>(Arrays.asList("Colors","Foods","Dogs"));

	public static void main(String[] args) {
		try {
			// Load the data from a file, map is category to item (name/rating)
			Map<String, List<Pair<String, Integer>>> catMap = null;
			BufferedInputStream bis = new BufferedInputStream(ClassLoader.getSystemResourceAsStream("favorite-things.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));
			String line;
			while ((line = br.readLine()) != null) { // for each line, read into the map
				// check which category
				int ic = line.indexOf(":");
				String cat = null;
				String currCat = line.substring(0, ic);
				
				//Check to see if the category list contains the category of the current line
				if(CATS.contains(currCat)){
					cat = currCat;
					
					// read the rest of the line
					ic++; // go to the next character after the colon
					String rest = line.substring(ic);
					String[] split = rest.split(","); // split by comma
					for (int i = 0; i < split.length; i++) {
						String favs = split[i];
						int id = -1;
						for (int j = 0; j < favs.length(); j++) {
							char ch = favs.charAt(j);
							if (Character.isDigit(ch)) {
								// stop, we found the digit
								id = j;
								break;
							}
						}
						String fav = favs.substring(0, id);
						Integer rat = new Integer(favs.substring(id));
						if (catMap == null) {
							catMap = new HashMap<>();
						}
						List<Pair<String, Integer>> existing = null;
						if (cat != null && catMap.containsKey(cat)) {
							existing = catMap.get(cat);
						} else {
							existing = new ArrayList<>();
							catMap.put(cat, existing);
						}
						existing.add(ImmutablePair.of(fav, rat));
					}
				}
				else { //if the category isn't known, skip processing the current line
					cat = null;
					System.out.println("Unknown cateogry " + currCat + ", cannot process");
				}
				
			}

			// Find the favorite with the highest rating and the category with the highest sum of ratings
			String highestCat = null;
			Pair<String, Integer> highestThing = null;
			String highestTotCat = null;
			int sums = 0;
			int tempSum = 0;
			for (Map.Entry<String, List<Pair<String, Integer>>> entry : catMap.entrySet()) { //iterate through the categories
				for (Pair<String, Integer> thingVal : entry.getValue()) { //iterate through the (thing,rating) pairs for the current category
					tempSum += thingVal.getRight(); //add up the ratings for this category
					if (highestThing == null || thingVal.getRight() > highestThing.getRight()) {
						highestThing = thingVal;
						highestCat = entry.getKey();
					}
				}
				if (tempSum > sums) {
					sums = tempSum;
					tempSum = 0;
					highestTotCat = entry.getKey(); //make note of which category now has the highest total rating
				}
			}
			
			System.out.println(String.format("Highest rating category: %s; thing: %s; rating: %d", highestCat, highestThing.getLeft(), highestThing.getRight()));

			System.out.println(String.format("Category with highest sum: %s; sum was %d", highestTotCat, sums));

			br.close();
		} catch (Exception e) {
			System.out.println("An error occurred");
		}
	}
}
