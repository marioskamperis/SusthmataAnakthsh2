package Askhsh2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Erwthma2 {

    private static final String DOCUMENTS_PATH = "E:\\Programing\\workspace\\SusthmataAnakthsh2\\SusthmataAnakthsh2\\documents.xml";
    private static final String TERMXDOCUMENT = "C:\\Users\\Marios\\Desktop\\termXdocument.txt";
    static Map<String, Integer> term_counter = new HashMap<String, Integer>();
    static int documents = 0;
    static int terms = 0;

    public static void main(String args[]) throws Exception {
        Map<String, List<String>> term_document = new HashMap<String, List<String>>();
        //Loading our documents XML File
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(DOCUMENTS_PATH);  //Parse file to DOM Tree
        NodeList nList = doc.getElementsByTagName("Document");
        for (int n = 0; n < nList.getLength(); n++) {
            documents++;

            Node nNode = nList.item(n);
            Element eElement = (Element) nNode;

            String id = getTagValue("I", eElement).trim();
            String text = getTagValue("T", eElement).trim();
            text = text + getTagValue("W", eElement).trim();
            text = text + getTagValue("A", eElement).trim();

            String term = " ";
            //Make some adjustements to the retrieved to text
            //Remove sepcial characters , make it all lowercase
            text = text.toLowerCase();
            Pattern punctuation = Pattern.compile("\\/|\\(|\\)|@|\\||:|\\?|!|-|,|\\.|\\+|[0-9]");
            Matcher matcher = punctuation.matcher(text);
            text = matcher.replaceAll(" ");
            //Tokenize everything

            StringTokenizer tk = new StringTokenizer(text);
            while (tk.hasMoreTokens()) {
                term = tk.nextToken().trim();
                int count = term_counter.containsKey(term) ? term_counter.get(term) : 0;
                term_counter.put(term, (count + 1));
                List<String> list = term_document.containsKey(term) ? term_document.get(term) : new ArrayList<String>();
                list.add(id);
                term_document.put(term, list);
            }
        }
        terms = term_counter.size();
//        System.out.println("# of terms : "+terms);
//        System.out.println("# of docs  : "+documents);

        //Initialize an Integer array.
        Integer[][] term_x_doc = new Integer[terms][documents];
        for (int i = 0; i < term_x_doc.length; i++)
            Arrays.fill(term_x_doc[i], 0);

        //We are going to populate our Term X Document array
        int i = 0;
        int doc_id = -1;
        Iterator<Entry<String, List<String>>> td = term_document.entrySet().iterator();
        while (td.hasNext()) {
            if (i >= documents) break;

            Entry<String, List<String>> entry = td.next();

            List<String> document_ids = entry.getValue();

            for (String s : document_ids) {
                doc_id = Integer.parseInt(s);
                term_x_doc[i][doc_id - 1] = 1;
            }
            i++;
        }
        //Open a s buffer Writer stream and output our file.

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(TERMXDOCUMENT)));
            for (int k = 0; k < term_x_doc.length; k++) {
                for (int j = 0; j < term_x_doc[0].length; j++) {
                    writer.write(term_x_doc[k][j] + " ");
                }
                writer.write("\r\n");
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
