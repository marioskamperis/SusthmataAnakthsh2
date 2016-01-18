package Askhsh2;/*
 * Author : Polycrhonopoulos Alexandros - p3110256
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class askisi2_ergasia2 {

    //static List<String> Terms = new ArrayList<String>();
    static Map<String, Integer> term_counter = new HashMap<String, Integer>();

    @SuppressWarnings("unused")
    public static void main(String args[]) throws Exception {
        //--------- read common words --------------------------
        System.out.print("Reading common_words...");
        ArrayList<String> common_words = readCommonWordsFromFile("cacm_corrected/common_words");
        System.out.println("[OK]");

        Integer[][] term_x_doc = getArrayTermToDocument(common_words);

        Integer[][] term_x_query = getArrayTermToQuery(common_words);

        System.out.println("ended succesfully !! ");
    }


    private static Integer[][] getArrayTermToDocument(ArrayList<String> common_words) throws SAXException, IOException, ParserConfigurationException {
        int number_of_documents = 0;
        int number_of_terms = 0;
        Map<String, List<String>> term_document = new HashMap<String, List<String>>();

        //--------- read documents.xml --------------------------
        System.out.print("Indexing documents.xml...");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse("documents.xml");  //Parse file to DOM Tree
        NodeList nList = doc.getElementsByTagName("Document");
        for (int n = 0; n < nList.getLength(); n++) {
            number_of_documents++;

            Node nNode = nList.item(n);
            Element eElement = (Element) nNode;

            String id = getTagValue("I", eElement).trim();
            String text = getTagValue("T", eElement).trim();
            text = text + getTagValue("W", eElement).trim();
            text = text + getTagValue("A", eElement).trim();

            String term = " ";
            text = text.toLowerCase();
            Pattern punctuation = Pattern.compile("\\/|\\(|\\)|@|\\||:|\\?|!|-|,|\\.|\\+|[0-9]");
            Matcher matcher = punctuation.matcher(text);
            text = matcher.replaceAll(" ");

            StringTokenizer tk = new StringTokenizer(text);
            while (tk.hasMoreTokens()) {
                term = tk.nextToken().trim();
                if (!common_words.contains(term)) {
                    int count = term_counter.containsKey(term) ? term_counter.get(term) : 0;
                    term_counter.put(term, (count + 1));
                    List<String> list = term_document.containsKey(term) ? term_document.get(term) : new ArrayList<String>();
                    list.add(id);
                    term_document.put(term, list);
                }
            }
        }
        System.out.println("[OK]");

        //------ remove terms with df < 2 ------------------------------
        Iterator<Entry<String, Integer>> it = term_counter.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();

            int x = Integer.parseInt(entry.getValue().toString());
            if (x < 2) {
                it.remove();
                term_document.remove(entry.getKey());
            }
        }
        number_of_terms = term_counter.size();
        System.out.println("# of terms : " + number_of_terms);
        System.out.println("# of docs  : " + number_of_documents);

        //-------------------------------------------------
        Integer[][] term_x_doc = new Integer[number_of_terms][number_of_documents];
        for (int i = 0; i < term_x_doc.length; i++)
            Arrays.fill(term_x_doc[i], 0);

        //--------- fill terms and term_x_doc arrays --------------------------
        int i = 0;
        int doc_id = -1;
        Iterator<Entry<String, List<String>>> td = term_document.entrySet().iterator();
        while (td.hasNext()) {
            if (i >= number_of_terms) break;

            Entry<String, List<String>> entry = td.next();

            List<String> document_ids = entry.getValue();

            for (String s : document_ids) {
                doc_id = Integer.parseInt(s);
                term_x_doc[i][doc_id - 1] = 1;
            }
            i++;
        }

        //-------------------------------------------------
        System.out.print("Writing doc_x_term.txt...");
        arrayToTextFile(term_x_doc, "term_x_doc.txt");
        System.out.println("[OK]");

        return null;
    }

    private static Integer[][] getArrayTermToQuery(ArrayList<String> Terms) throws SAXException, IOException, ParserConfigurationException {
        int number_of_queries = 0;
        int number_of_terms = 0;
        Map<String, List<String>> term_query = new HashMap<String, List<String>>();
        Map<String, Integer> indexed_query_ids = new HashMap<String, Integer>();

        for (String s : Terms) {
            term_query.put(s, new ArrayList<String>());
        }

        //--------- read queries.xml --------------------------
        System.out.print("Indexing queries.xml...");

        // 	("Writing queries_index.txt...");
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("queries_index.txt")));
        String nline = "\r\n";
        // ----------------------------------

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse("queries.xml");  //Parse file to DOM Tree
        NodeList nList = doc.getElementsByTagName("query");
        for (int n = 0; n < nList.getLength(); n++) {
            number_of_queries++;

            Node nNode = nList.item(n);
            Element eElement = (Element) nNode;

            String id = getTagValue("I", eElement).trim();
            String text = getTagValue("W", eElement).trim();

            indexed_query_ids.put(id, n);
            writer.write(n + " " + id + nline); // writing to queries_index.txt

            String term = " ";
            text = text.toLowerCase();
            Pattern punctuation = Pattern.compile("\\/|\\(|\\)|@|\\||:|\\?|!|-|,|\\.|\\+|[0-9]");
            Matcher matcher = punctuation.matcher(text);
            text = matcher.replaceAll(" ");

            StringTokenizer tk = new StringTokenizer(text);
            while (tk.hasMoreTokens()) {
                term = tk.nextToken().trim();
                // maybe not use common words in queries
                if (Terms.contains(term)) {
                    List<String> list = term_query.containsKey(term) ? term_query.get(term) : new ArrayList<String>();
                    list.add(id);
                    term_query.put(term, list);
                }
            }
        }
        System.out.println("[OK]");
        writer.close();


        number_of_terms = term_counter.size();
        System.out.println("# of terms : " + number_of_terms);
        System.out.println("# of queries  : " + number_of_queries);


        //-------------------------------------------------
        Integer[][] term_x_query = new Integer[number_of_terms][number_of_queries];
        for (int i = 0; i < term_x_query.length; i++)
            Arrays.fill(term_x_query[i], 0);

        //--------- fill terms and term_x_query arrays --------------------------

        int i = 0;
        int q_id = -1;
        Iterator<Entry<String, List<String>>> td = term_query.entrySet().iterator();
        while (td.hasNext()) {
            if (i >= number_of_terms) break;

            Entry<String, List<String>> entry = td.next();
            List<String> query_ids = entry.getValue();

            for (String s : query_ids) {
                q_id = indexed_query_ids.get(s);
                term_x_query[i][q_id] = 1;
            }
            i++;
        }


        //-------------------------------------------------
        System.out.print("Writing term_x_query.txt...");
        arrayToTextFile(term_x_query, "term_x_query.txt");
        System.out.println("[OK]");
        return null;
    }

    @SuppressWarnings({"resource", "unused"})
    private static ArrayList<String> readCommonWordsFromFile(String path) throws FileNotFoundException {
        ArrayList<String> al = new ArrayList<String>();
        Scanner s = new Scanner(new FileInputStream(path));
        String temp = " ";


        Pattern punctuation = Pattern.compile("\\/|\\(|\\)|@|\\||:|\\?|!|-|,|\\.|\\+|[0-9]");

        int cw = 0;
        while (s.hasNext()) // READ ALL WORDS
        {
            temp = s.next().trim().toLowerCase();
            Matcher matcher = punctuation.matcher(temp);
            temp = matcher.replaceAll(" ");

            al.add(temp);
            cw++;
            //System.out.println(temp);
        }
        //System.out.println("#ofcommonwords = "+cw);
        return al;
    }

    public static void arrayToTextFile(Integer[][] doc_x_term, String file) {
        String nline = "\r\n";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file)));
            for (int i = 0; i < doc_x_term.length; i++) {
                for (int j = 0; j < doc_x_term[0].length; j++) {
                    writer.write(doc_x_term[i][j] + " ");
                }
                writer.write(nline);
            }
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = nlList.item(0);

        if (nValue == null)
            return "";
        return nValue.getNodeValue();
    }
}
