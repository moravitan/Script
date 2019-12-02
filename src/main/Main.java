package main;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Itay Carmi and Mor Avitan on 26/11/19
 */

public class Main {

    // contain all the names from the "https://www.behindthename.com/names/usage/english" after being parsed
    private static ArrayList<String> parsedNames = new ArrayList<>();

    private static HashMap<String, Integer> countSubstringMap = new HashMap<>();
    // the website to parse the names from
    private static String webLink = "https://www.behindthename.com/names/usage/english";
    // temporary HashSet to keep the names in the parsing process
    private HashSet<String> names;


    public static void main(String[] args) {
        readNamesFromFile();
        // The following set of conditions calls to the relevant function according to user input
        // as long as the names file was loaded first
        if (args[0].equals("CountSpecificString")) {
            countSpecificString(args[1]);
        }
        if (args[0].equals("CountAllStrings")) {
            countAllStrings(Integer.valueOf(args[1]));
        }
        if (args[0].equals("CountMaxString")) {
            countMaxString(Integer.valueOf(args[1]));
        }
        if (args[0].equals("AllIncludesString")) {
            allIncludesString(args[1]);
        }
        if (args[0].equals("GenerateName")) {
            generateName();
        }
    }

    /**
     * This function parse the source code of @webLink.
     * This function iterate over 14 pages of web using the getEnglishNames function.
     * After receiving the content from each page the function is searching for the names which appear between:
     * "<span class=\"listname\">" and "<span class=\"listgender\">" tags.
     */
    private void parse() {

        try {
            int pageNumber = 1;
            String namesByPage = getEnglishNames(pageNumber);
            while (namesByPage != null) {
                while (namesByPage.length() > 0) {
                    String nameListTag = StringUtils.substringBetween(namesByPage, "<span class=\"listname\">",
                            "<span class=\"listgender\">");
                    String nameTag = StringUtils.substringBetween(nameListTag, "<a href=\"/name/", "/a>");
                    if (nameTag == null) {
                        break;
                    }
                    String name = nameTag.substring(nameTag.indexOf(">") + 1, nameTag.indexOf("<"));
                    // if the founded name is a legal name (which contains only A-z letters or spaces
                    if (StringUtils.isAlpha(name) || (!StringUtils.isAlpha(name) && name.contains(" "))) {
                        namesByPage = namesByPage.substring(namesByPage.indexOf(name) + name.length());
                        name = name.trim();
                        // changing the name format to be a legal English format
                        // for e.g ABBI will become Abbi
                        name = name.toLowerCase();
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                        if (name.contains(" ")) {
                            name = name.substring(0, name.indexOf(' ') + 1) + name.substring(name.indexOf(' ') + 1,
                                    name.indexOf(' ') + 2).toUpperCase() + name.substring(name.indexOf(' ') + 2);
                        }
                        this.names.add(name);
                    } else {
                        namesByPage = namesByPage.substring(namesByPage.indexOf(name) + name.length());
                    }
                }
                pageNumber++;
                namesByPage = getEnglishNames(pageNumber);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeNamesToFile();
    }

    /**
     * This functions sort @names using TreeSet and write the content of the set to a file named: 'names.txt'
     */
    private void writeNamesToFile() {
        PrintWriter writer = null;
        TreeSet<String> sortedNames = new TreeSet<>(this.names);
        try {
            writer = new PrintWriter("names.txt", "UTF-8");
            for (String name : sortedNames) {
                writer.println(name);

            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * This function opens a connection link to a website with that address stored in @webLink
     * in the following link: #webLink + "/" + pageNumber
     * While reading the web page this function seek for the relevant tag which contain the english name
     * which display in the website.
     *
     * @param pageNumber - page number of the @webLink to open
     * @return - a string which contain the <div> tag in the @webLink which contain the names in the web page.
     * @throws IOException - in case of an unsuccessful connection
     */
    private String getEnglishNames(int pageNumber) throws IOException {
        // parse only the first 14 pages in the website
        if (pageNumber == 15) {
            return null;
        }
        BufferedReader br = null;
        StringBuilder namesByLetter = new StringBuilder();

        try {

            URL url = new URL(this.webLink + "/" + pageNumber);
            br = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = br.readLine()) != null) {
                if (StringUtils.startsWith(line, "<div class=\"browsename\">")) {
                    namesByLetter.append(line);
                    namesByLetter.append(System.lineSeparator());
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return namesByLetter.toString();
    }


    /**
     * This function reads the names from the names.txt file and adds them to @names ArrayList
     */
    private static void readNamesFromFile() {
        BufferedReader br;
        try {
            Path currentRelativePath = Paths.get("");
            String filePath = currentRelativePath.toAbsolutePath().toString() + "/names.txt";
            File file = new File(filePath);
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                parsedNames.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function iterate over all the names in the @names ArrayList and count the number of appearances of @string
     *
     * @param string
     * @return the number of appearances of @string in the @names Arraylist
     */
    public static void countSpecificString(String string) {
        int counter = 0;
        for (String name : parsedNames) {
            if (name.contains(string)) {
                counter++;
            }
        }
        System.out.println(counter);
    }

    /**
     * This function uses countSubstrings function to count the number of substring with a length equal to @length.
     * Afterwards this function prints the founded substring in the following format: 'substring':'numberOfAppearances'
     *
     * @param length - the length of the substring
     */
    public static void countAllStrings(int length) {
        // counting substring with a length equal to length @param and not ignore cases while compering substrings
        countSubstrings(length, false);
        for (Map.Entry<String, Integer> entry : countSubstringMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    /**
     * This function iterate over all the string in @names and count the number of
     * substring with the length equal to @length
     *
     * @param length      - the length of the substring
     * @param ignoreCases boolean indicating if the function should/shouldn't ignore case while searching for substrings
     */
    private static void countSubstrings(int length, boolean ignoreCases) {
        countSubstringMap = new HashMap<>();
        // iterate over names ArrayList and count the number of substring with length equal to length @param
        for (String name : parsedNames) {
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
     *
     * @param length
     */
    public static void countMaxString(int length) {
        countSubstrings(length, true);

        // get the max value from the countSubstringMap variable
        HashMap<String, Integer> sortedHashMap = sortByValue(countSubstringMap);
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
     * This function has been taken from: https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values.
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

    /**
     * This function finds all the names that includes in the given string or strings.
     * After finding the names the function is printing all the founded name(s).
     *
     * @param string
     */
    public static void allIncludesString(String string) {
        for (String name : parsedNames) {
            if ((string.toLowerCase()).contains((name.toLowerCase()))) {
                System.out.println(name);
            }
        }
    }

    /**
     * This function finds the most popular letter which appear given @lastLetter in @names
     *
     * @param lastLetter the letter we check
     * @return the most popular letter
     */
    private static String returnTheMostPopularLetter(String lastLetter) {
        Map.Entry<String, Integer> res = null;
        boolean upperCase = false;
        if (lastLetter == "")
            upperCase = true;
        for (Map.Entry<String, Integer> entrySet : countSubstringMap.entrySet()) {
            if (upperCase) {
                if (res == null || (entrySet.getKey()).startsWith(lastLetter) && entrySet.getValue().compareTo(res.getValue()) > 0
                        && Character.isUpperCase(entrySet.getKey().charAt(0)))
                    res = entrySet;
            } else {
                if (res == null || (entrySet.getKey()).startsWith(lastLetter) && entrySet.getValue().compareTo(res.getValue()) > 0)
                    res = entrySet;
            }

        }
        return res.getKey().substring(res.getKey().length() - 1);
    }

    /**
     * This function automatically creates name by a random length.
     */
    public static void generateName() {
        countSubstrings(1, false);
        String result = "";
        result += returnTheMostPopularLetter("").toUpperCase();
        int random = (int) (Math.random() * 8 + 1);
        countSubstrings(2, false);
        for (int i = 0; i < random; i++) {
            result += returnTheMostPopularLetter(result.substring(result.length() - 1));
        }
        System.out.println(result);
    }


}
