package Askhsh2;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


public class LuceneExercise_solution {

    public static void main(String[] args) throws Exception {
        // INitiliaze Lucene ant all neede components
        StandardAnalyzer analyzer = new StandardAnalyzer();
        String indexLocation = "index";
        //Directory index = new RAMDirectory();
        Directory index = FSDirectory.open(new File(indexLocation));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
        config.setOpenMode(OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(index, config);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        //Parse Documents and insert them in Lucene
        Document doc = dBuilder.parse("E:\\Programing\\workspace\\SusthmataAnakthsh2\\SusthmataAnakthsh2\\documents.xml");
        Element rootElement = doc.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node ni = nodes.item(i);
            // make sure it's an element node.
            if (ni.getNodeType() == Node.ELEMENT_NODE) {

                Element ei = (Element) ni; //type cast to element
                String ID = ei.getElementsByTagName("I").item(0).getTextContent();
                String TITLE = ei.getElementsByTagName("T").item(0).getTextContent();
                String AUTHOR = ei.getElementsByTagName("A").item(0).getTextContent();
                String DEPARTMENT = ei.getElementsByTagName("B").item(0).getTextContent();
                String DESCRIPTION = ei.getElementsByTagName("W").item(0).getTextContent();
                addDoc(writer, ID, TITLE, AUTHOR, DEPARTMENT, DESCRIPTION);
            }
        }
        writer.close();
        // Parse Queries XML
        dbFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse("E:\\Programing\\workspace\\SusthmataAnakthsh2\\SusthmataAnakthsh2\\queries.xml");
        rootElement = doc.getDocumentElement();
        nodes = rootElement.getChildNodes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node ni = nodes.item(i);
            // make sure it's an element node.
            if (ni.getNodeType() == Node.ELEMENT_NODE) {
                // get EACH QUERY
                Element ei = (Element) ni; //type cast to element
                String ID = ei.getElementsByTagName("I").item(0).getTextContent();
                String TITLE = ei.getElementsByTagName("W").item(0).getTextContent();
                //Give the query to the parser
                QueryParser qp = new QueryParser("TITLE", analyzer);
                String qs[] = {TITLE, "XML parser"};
                //Limit results to 500 per query
                int hitsPerPage = 500;
                IndexReader reader = null;
                try {
                    reader = DirectoryReader.open(index);
                    IndexSearcher searcher = new IndexSearcher(reader);
                    for (String q : qs) {
                        Query query = qp.parse(q);
                        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
                        searcher.search(query, collector);
                        ScoreDoc[] hits = collector.topDocs().scoreDocs;
                        //System.out.println("Query: '" + q + "' Found " + hits.length + " hits.");
                        for (int k = 0; k < hits.length; ++k) {
                            int docId = hits[k].doc;
                            org.apache.lucene.document.Document d = searcher.doc(docId);
                            sb.append("Query: " + ID + " Document: " + d.get("ID") + " Score :" + hits[k].score + "\n");
//        				System.out.println((k + 1) + ".\t"
//        						+"ID :"+ d.get("ID")+"\n"
//        						+"TITLE :"+ d.get("TITLE")+"\n"
//        						+"AUTHOR :"+ d.get("AUTHOR")+"\n"
//        						+"DEPARTMENT :"+ d.get("DEPARTMENT")+"\n"
//        						+"\n\tScore: "+hits[k].score);
                        }
                    }
                } catch (ParseException e) {
                    System.out.println("Exception caught :" + e.getMessage());
                }
                reader.close();
            }
        }

        writer.close();
        PrintWriter Filewriter = new PrintWriter("E:\\Programing\\workspace\\SusthmataAnakthsh2\\SusthmataAnakthsh2\\relevance.txt", "UTF-8");
        Filewriter.println(sb.toString());
        writer.close();


    }

    private static void addDoc(IndexWriter writer, String ID, String TITLE, String AUTHOR, String DEPARTMENT, String DESCRIPTION) throws IOException {
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
        Field tf = new StringField("ID", ID, Field.Store.YES);
        doc.add(tf);
        tf = new TextField("TITLE", TITLE, Field.Store.YES);
        doc.add(tf);
        tf = new StoredField("AUTHOR", AUTHOR);
        doc.add(tf);
        tf = new StoredField("DEPARTMENT", DEPARTMENT);
        doc.add(tf);
        tf = new TextField("DESCRIPTION", DESCRIPTION, Field.Store.YES);
        doc.add(tf);
        writer.addDocument(doc);
    }

}
