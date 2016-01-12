package Askhsh2;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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

public class LuceneIntro {
	 public static void main(String[] args) throws Exception {
		 StandardAnalyzer analyzer = new StandardAnalyzer();
		 
		 String indexLocation="index";
		 //Directory index = new RAMDirectory();
		 Directory index=FSDirectory.open(new File(indexLocation));		 
		 IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
		 config.setOpenMode(OpenMode.CREATE);
		 
		 IndexWriter writer = new IndexWriter(index, config);		 
		 addDoc(writer, "Lucene in Action");
		 addDoc(writer, "Lucene for Dummies");
		 addDoc(writer, "Managing Gigabytes");
		 addDoc(writer, "The Art of Computer Science");
		 writer.close();
		 
		 String qstr = args.length > 0 ? args[0] : "lucene";
		 Query q = new QueryParser("title", analyzer).parse(qstr);
		 
		 int hitsPerPage = 10;
		 IndexReader reader = DirectoryReader.open(index);
		 IndexSearcher searcher = new IndexSearcher(reader);
		 TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage,true);
		 searcher.search(q, collector);
		 ScoreDoc[] hits = collector.topDocs().scoreDocs;
		 
		 System.out.println("Found " + hits.length + " hits.");
		 for(int i=0;i<hits.length;++i) {
			 int docId = hits[i].doc;
			 Document d = searcher.doc(docId);
			 System.out.println((i + 1) + ". " + d.get("title")+" Score: "+hits[i].score);
		 }
		 		 
	 }
	 
	//η addDoc() προσθέτει ένα string στο ευρετήριο
	 private static void addDoc(IndexWriter writer, String value) throws IOException{
		 Document doc = new Document();
		 TextField title = new TextField("title", value, Field.Store.YES);
		 doc.add(title);
		 writer.addDocument(doc);
	 }
}
