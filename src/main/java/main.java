import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * Created by Itay Carmi and Mor Avitan on 26/11/19
 */

public class main {

    private static ArrayList<String> names = new ArrayList<>();
    private static HashMap<String, Integer> countSubstringMap = new HashMap<>();

    public static void main(String[] args) {
//        Parser parser = parseNames();
//        readNamesFromFile(parser.getNamesFile());
        readNamesFromFile("names.txt");
//        CountSpecificString("ar");
//        CountAllStrings(2);
//        CountMaxString(2);
    }


    /**
     * This function parse the names from: https://www.behindthename.com using the Parser object.
     * @return - the Parser object being used to parse the above site.
     */
    private static Parser parseNames() {
        Parser parser = new Parser("https://www.behindthename.com/names/usage/english");
        parser.parse();
        return parser;
    }

    /**
     * This function reads the names from @filePath and adds them to @names ArrayList
     * @param filePath - the path of the file which contains the parsed names from https://www.behindthename.com
     * website.
     */
    private static void readNamesFromFile(String filePath) {
        File file = new File(filePath);
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null)
                names.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function iterate over all the names in the @names ArrayList and count the number of appearances of @string
     * @param string
     * @return the number of appearances of @string in the @names Arraylist
     */
    public static void CountSpecificString(String string) {
        int counter = 0;
        for (String name : names) {
            if (name.contains(string)) {
                counter++;
            }
        }
        System.out.println(counter);
    }

    /**
     * This function uses countSubstrings function to count the number of substring with a length equal to @length.
     * Afterwards this function prints the founded substring in the following format: 'substring':'numberOfAppearances'
     * @param length - the length of the substring
     */
    public static void CountAllStrings(int length) {
        // counting substring with a length equal to length @param and not ignore cases while compering substrings
        countSubstrings(length, false);
        // print all the founds substring and the number of their appearances
        for (Map.Entry<String, Integer> entry : countSubstringMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    /**
     * This function iterate over all the string in @names and count the number of
     * substring with the length equal to @length
     * @param length - the length of the substring
     * @param ignoreCases boolean indicating if the function should/shouldn't ignore case while searching for substrings
     */
    private static void countSubstrings(int length, boolean ignoreCases) {

        countSubstringMap = new HashMap<>();
        // iterate over names ArrayList and count the number of substring with length equal to length @param
        for (String name : names) {
            if (ignoreCases) {
                name = name.toLowerCase();
            }
            for (int i = 0; i < name.length(); i++) {
                if (i + length > name.length()) {
                    break;
                }
                String substring = name.substring(i, i + length);
                // check if the substring already exist in the HashMap
                if (countSubstringMap.containsKey(substring)) {
                    countSubstringMap.put(substring, countSubstringMap.get(substring) + 1);
                } else {
                    countSubstringMap.put(substring, 1);
                }
            }
        }
    }

    /**
     * This function finds the most common string (or strings) in the @names @ArrayList with length equal to @length.
     * After finding the common string (or strings) the function is printing all the founded string(s).
     * @param length
     */
    public static void CountMaxString(int length) {
        countSubstrings(length, true);

        // get the max value from the countSubstringMap variable
        HashMap<String,Integer> sortedHashMap = sortByValue(countSubstringMap);
        Map.Entry<String, Integer> entry = sortedHashMap.entrySet().iterator().next();
        int max = entry.getValue();

        // print all the substring with number of appearances equal to max
        for (Map.Entry<String, Integer> entrySet : countSubstringMap.entrySet()) {
            if (entrySet.getValue() == max) {
                System.out.println(entrySet.getKey());
            }
        }
    }

    /**
     * This function sort a given HashMap by it's value.
     * This function has been taken from: https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/
     *
     * @param hashMap
     * @return a sorted HashMap by it's value
     */
    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hashMap) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list = new LinkedList<>(hashMap.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to HashMap
        HashMap<String, Integer> ans = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            ans.put(aa.getKey(), aa.getValue());
        }
        return ans;
    }


    public static void AllIncludesString(String string) {

    }

    public static void GenerateName(String string) {

    }


}
