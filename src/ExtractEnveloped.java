import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * XML-Enveloped-Signature, Origin Content
 * 원본 값 추출하기
 * 
 * @author <a href="mailto:modesty101@daum.net">김동규</a>
 * @since 2017
 */
public class ExtractEnveloped {
	public static String UI() {
		Scanner scan = new Scanner(System.in);

		System.out.print("Extract signed xml file : ");
		String fileName = scan.nextLine();

		scan.close();
		return fileName;
	}

	public static void main(String[] args) throws Exception {
		// Enveloped.. Not Enveloping!
		String fileName = ExtractEnveloped.UI();

		// Instantiate the document to be signed.
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().parse(new FileInputStream(fileName));

		// Find Signature element.
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");

		// remove signature node from DOM
		nl.item(0).getParentNode().removeChild(nl.item(0));

		// write to file.
		OutputStream os = new FileOutputStream("OriginalHappy.xml");
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		trans.transform(new DOMSource(doc), new StreamResult(os));
	}
}
