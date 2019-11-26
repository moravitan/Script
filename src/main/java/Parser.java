import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * This class parse a specific website link content into a list of English name.
 * Created by Itay Carmi and Mor Avitan on 26/11/19
 */
public class Parser {

    private String webLink;
    private HashSet<String> names;
    private String namesFilePath;


    /**
     * Constructor
     * @param webLink - website address to be parsed.
     */
    public Parser(String webLink) {

        this.webLink = webLink;
        this.names = new HashSet<>();
    }

    /**
     * This function parse the source code of @webLink.
     * This function iterate over 14 pages of web using the getEnglishNames function.
     * After reciveing the content from each page the function is searching for the names which appear between:
     * "<span class=\"listname\">" and "<span class=\"listgender\">" tags.
     *
     * @return
     */
    public void parse() {

        try {
            int pageNumber = 1;
            String namesByPage = getEnglishNames(pageNumber);
            while (namesByPage != null) {
                while (namesByPage.length() > 0) {
                    String nameListTag = StringUtils.substringBetween(namesByPage,"<span class=\"listname\">",
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
                        if (name.contains(" ")){
                            name = name.substring(0,name.indexOf(' ') + 1) + name.substring(name.indexOf(' ') + 1, name.indexOf(' ') + 2).toUpperCase()
                                    + name.substring(name.indexOf(' ') + 2);
                        }
                        this.names.add(name);
                    }
                    else{
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
            this.namesFilePath = "names.txt";
            writer = new PrintWriter("names.txt", "UTF-8");
            for (String name: sortedNames) {
                writer.println(name);

            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * This function opens a connection link to a website with that address stored in @webLink
     * in the following link: #webLink + "/" + pageNumber
     * While reading the web page this function seek for the relevant tag which contain the english name
     * which display in the website.
     * @param pageNumber - page number of the @webLink to open
     * @return - a string which contain the <div> tag in the @webLink which contain the names in the web page.
     * @throws IOException - in case of an unsuccessful connection
     */
    private String getEnglishNames(int pageNumber) throws IOException {

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
                // find the line which contain the <browsename> tag
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


    public HashSet<String> getNames() {
        return names;
    }

    public String getNamesFile() {
        return namesFilePath;
    }
}

