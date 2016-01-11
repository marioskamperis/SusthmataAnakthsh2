import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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


public class LuceneExercise_solution {

	public static void main(String[] args) throws Exception {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		 
		String indexLocation="index";
		//Directory index = new RAMDirectory();
		Directory index=FSDirectory.open(new File(indexLocation));		 
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
		config.setOpenMode(OpenMode.CREATE);		 
		IndexWriter writer = new IndexWriter(index, config);		 
				
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse("cran_all_1400.xml");
		Element rootElement = doc.getDocumentElement();
        NodeList nodes=rootElement.getChildNodes();
        
        for(int i=0;i<nodes.getLength();i++){
        	Node ni=nodes.item(i);
        	// make sure it's an element node.
        	if (ni.getNodeType() == Node.ELEMENT_NODE) {   

        		Element ei=(Element)ni; //type cast to element     
        		String ID=ei.getElementsByTagName("ID").item(0).getTextContent();
        		String TITLE=ei.getElementsByTagName("TITLE").item(0).getTextContent();
        		String AUTHOR=ei.getElementsByTagName("AUTHOR").item(0).getTextContent();
        		String DEPARTMENT=ei.getElementsByTagName("DEPARTMENT").item(0).getTextContent();
        		String DESCRIPTION=ei.getElementsByTagName("DESCRIPTION").item(0).getTextContent();
        		addDoc(writer,ID,TITLE,AUTHOR,DEPARTMENT,DESCRIPTION);
        	}
        }
        writer.close();
        
        dbFactory = DocumentBuilderFactory.newInstance();
		dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse("cran.xml");
		rootElement = doc.getDocumentElement();
        nodes=rootElement.getChildNodes();
        
        for(int i=0;i<nodes.getLength();i++){
        	Node ni=nodes.item(i);
        	// make sure it's an element node.
        	if (ni.getNodeType() == Node.ELEMENT_NODE) {   

        		Element ei=(Element)ni; //type cast to element     
        		String TITLE=ei.getElementsByTagName("TITLE").item(0).getTextContent();
        		System.out.println(TITLE);
        		
        		QueryParser qp = new QueryParser("TITLE", analyzer);
                String qs[]={TITLE,"XML parser"};
                               				                	
        		int hitsPerPage = 500;
        		IndexReader reader = DirectoryReader.open(index);
        		IndexSearcher searcher = new IndexSearcher(reader);		
        		for(String q:qs){
        			Query query=qp.parse(q);
        			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage,true);
        			searcher.search(query, collector);
        			ScoreDoc[] hits = collector.topDocs().scoreDocs;		
        			System.out.println("Query: '" + q + "' Found " + hits.length + " hits.");
        			for(int k=0;k<hits.length;++k) {
        				int docId = hits[k].doc;
        				org.apache.lucene.document.Document d = searcher.doc(docId);
        				System.out.println((k + 1) + ".\t" 
        						+"ID :"+ d.get("ID")+"\n"
        						+"TITLE :"+ d.get("TITLE")+"\n"
        						+"AUTHOR :"+ d.get("AUTHOR")+"\n"
        						+"DEPARTMENT :"+ d.get("DEPARTMENT")+"\n"
        						+"\n\tScore: "+hits[k].score);
        			 }
        		}
        		reader.close();
        	}
        }
        writer.close();
        
        
        
               
	}
	
	 private static void addDoc(IndexWriter writer, String ID,String TITLE,String AUTHOR,String DEPARTMENT,String DESCRIPTION) throws IOException{
		 org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
		 Field tf = new StringField("ID",ID,Field.Store.YES);
		 doc.add(tf);
		 tf = new TextField("TITLE", TITLE,Field.Store.YES);
		 doc.add(tf);
		 tf = new StoredField("AUTHOR", AUTHOR);
		 doc.add(tf);
		 tf = new StoredField("DEPARTMENT", DEPARTMENT);
		 doc.add(tf);
		 tf = new TextField("DESCRIPTION", DESCRIPTION, Field.Store.NO);
		 doc.add(tf);
		 writer.addDocument(doc);
	 }

}
