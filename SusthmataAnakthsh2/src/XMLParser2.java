import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;

public class XMLParser2 {

	// BufferedReader in;
	Scanner in;
	StreamResult out;
	TransformerHandler th;

	

	public void begin() {
		try {
			
			in = new Scanner(new FileReader(new File("cran.all.1400")));
			out = new StreamResult("cran_all_1400.xml");
			openXml();

			String line = "";
			// cutting the text to segments using delimiter I
			in.useDelimiter(".I");
			// giving i\each line to process
			while (in.hasNext()) {
				line = in.next();

				String i = line.substring(1, line.indexOf(".T"));
				String t = line.substring(line.indexOf(".T") + 2, line.indexOf(".A"));
				String a = line.substring(line.indexOf(".A") + 2, line.indexOf(".B"));
				String b = line.substring(line.indexOf(".B") + 2, line.indexOf(".W"));
				String w = line.substring(line.indexOf(".W") + 2, line.length() - 1);

				
				th.startElement(null, null, "qry", null);
				process((i.replace("\n", "\t").trim()), "ID");
				process((t.replace("\n", "\t").trim()), "TITLE");
				process((a.replace("\n", "\t").trim()), "AUTHOR");
				process((b.replace("\n", "\t").trim()), "DEPARTMENT");
				process((w.replace("\n", "\t").trim()), "DESCRIPTION");
				th.endElement(null, null, "qry");

			}
			in.close();
			closeXml();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openXml() throws ParserConfigurationException, TransformerConfigurationException, SAXException {

		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		th = tf.newTransformerHandler();

		// pretty XML output
		Transformer serializer = th.getTransformer();
		serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");

		th.setResult(out);
		th.startDocument();
		th.startElement(null, null, "Document", null);
	}

	public void process(String s, String title) throws SAXException {
		th.startElement(null, null, title, null);
		th.characters(s.toCharArray(), 0, s.length());
		th.endElement(null, null, title);
	}

	public void closeXml() throws SAXException {
		th.endElement(null, null, "Document");
		th.endDocument();
	}
}