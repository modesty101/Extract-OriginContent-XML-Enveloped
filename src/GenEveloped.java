import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Collections;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * XML-Enveloped-Signature 예제 변형
 * 그리고, 원본 값 추출하기
 * 
 * @source <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/xmldsig/XMLDigitalSignature.html#wp511438"/>
 * @author <a href="mailto:modesty101@daum.net">김동규</a>
 * @since 2017
 */
public class GenEveloped {
	public static void main(String[] args) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(new FileInputStream(args[0]));

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
		kpg.initialize(512);
		KeyPair kp = kpg.generateKeyPair();
		DOMSignContext dsc = new DOMSignContext(kp.getPrivate(), doc.getDocumentElement());
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
				Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null,
				null);

		SignedInfo si = fac.newSignedInfo(
				fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
						(C14NMethodParameterSpec) null),
				fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null), Collections.singletonList(ref));
		
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		KeyValue kv = kif.newKeyValue(kp.getPublic());
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
		
		XMLSignature signature = fac.newXMLSignature(si, ki);
		
		signature.sign(dsc);
		
		// output the resulting document
		OutputStream os;
		if (args.length > 1) {
			os = new FileOutputStream(args[1]);
		} else {
			os = System.out;
		}

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		trans.transform(new DOMSource(doc), new StreamResult(os));
	}
}
