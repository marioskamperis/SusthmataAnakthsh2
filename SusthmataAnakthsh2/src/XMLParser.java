import java.io.BufferedReader;
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

public class XMLParser {

	BufferedReader in;
	StreamResult out;
	TransformerHandler th;

	public static void main(String args[]) {
		new XMLParser().begin();
	}

	public void begin() {
		try {
			in = new BufferedReader(new FileReader("cran.qry"));
			out = new StreamResult("cran.xml");
			openXml();
			String str="";
			String text = "";
			// Putting whole document int variable text
			while ((str = in.readLine()) != null) {
				text += str;
			}
			//giving the text to Scanner and replacing EOF with tab 
			Scanner input = new Scanner(text.replace("\n", "\t"));
			
			String line = "";
			//cutting the text to segments using delimiter I
			input.useDelimiter(".I");
			//giving i\each line to process
			while (input.hasNext()) {
				line = input.next();
				 
				process(line);
			}
			in.close();
			closeXml();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openXml() throws ParserConfigurationException,
			TransformerConfigurationException, SAXException {

		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();
		th = tf.newTransformerHandler();

		// pretty XML output
		Transformer serializer = th.getTransformer();
		serializer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");

		th.setResult(out);
		th.startDocument();
		th.startElement(null, null, "Document", null);
	}

	public void process(String s) throws SAXException {
		// th.startEntity("qry");
		th.startElement(null, null, "qry", null);
		th.startElement(null, null, "I", null);
		th.characters(s.toCharArray(), 1, s.indexOf("."));
		th.endElement(null, null, "I");

		s = s.substring(s.indexOf(".W")+2, s.length());

		th.startElement(null, null, "W", null);
		th.characters(s.toCharArray(), 0, s.length());
		th.endElement(null, null, "W");

		th.endElement(null, null, "qry");
	}

	public void closeXml() throws SAXException {
		th.endElement(null, null, "Document");
		th.endDocument();
	}
}