package com.yokogawa.radiquest.ris.core;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Shogo TANIAI
 * @since 2006/12/26
 */
public class XmlParser {

	/**
	 * 指定誇示エンコードでバイト配列からDOMを作成する。
	 * 
	 * @param buf
	 *            xmlバッファ
	 * 
	 * @param encode
	 *            文字エンコード
	 * 
	 * @return DOM
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document readXmlBuffer(byte[] buf, String encode)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();

		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		InputStreamReader isr = new InputStreamReader(bais, encode);
		InputSource inputSource = new InputSource(isr);
		Document doc = null;
		doc = builder.parse(inputSource);

		return doc;
	}

	/**
	 * @param xmlFilePath
	 *            XMLファイルのフルパス
	 * 
	 * @param encode
	 *            エンコード
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document readXmlFile(String xmlFilePath, String encode)
			throws ParserConfigurationException, SAXException, IOException {
		return readXmlFile(new File(xmlFilePath), encode);
	}

	/**
	 * 指定XMLからDOMを作成する。
	 * 
	 * @param xmlFile
	 *            XMLファイル
	 * 
	 * @param encode
	 *            エンコード
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document readXmlFile(File xmlFile, String encode)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();

		FileInputStream fis = new FileInputStream(xmlFile);
		InputStreamReader isr = new InputStreamReader(fis, encode);
		InputSource inputSource = new InputSource(isr);
		Document doc = null;
		if (xmlFile.exists())
			doc = builder.parse(inputSource);

		return doc;
	}

	/**
	 * 指定されたノード下に含まれる属性名を検索してそのノードの文字列を返す。
	 * 
	 * @param n
	 *            要素ノード
	 * @param attrName
	 *            属性名文字列
	 * 
	 * @return 指定された名前が取得できた場合は、値文字列、それ以外はnull
	 */
	public String getAttributeNodeValue(Node n, String attrName) {
		if (n == null || attrName == null)
			return null;

		// 引数が属性で指定された名前を同じならばその値を返す。
		if (n.getNodeName().compareTo(attrName) == 0
				&& n.getNodeType() == Node.ATTRIBUTE_NODE)
			return n.getNodeValue();

		// 属性ノードを取得
		NamedNodeMap attr = n.getAttributes();

		return getTargetAttrValue(attr, attrName);
	}

	/**
	 * 属性マップから指定した属性名の値を返す。
	 * 
	 * @param attrMap
	 *            属性マップ
	 * 
	 * @param attrName
	 *            属性名
	 * 
	 * @return 属性の値
	 */
	private String getTargetAttrValue(NamedNodeMap attrMap, String attrName) {
		Node attr = null;
		attr = attrMap.getNamedItem(attrName);
		if (attr == null)
			return null;

		return attr.getNodeValue();
	}

	/**
	 * 指定ノードを文字列に変換する。
	 * 
	 * @param node
	 *            ノードオブジェクト
	 * @return
	 */
	public String nodeToString(Node node) {
		StringBuffer buff = new StringBuffer();

		if (node == null)
			return null;

		if (node.getNodeType() == Node.TEXT_NODE)
			return node.getNodeValue();

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			buff.append("<").append(node.getNodeName());

			// 属性をもっているか
			NamedNodeMap map = node.getAttributes();
			if (map != null) {
				for (int i = 0; i < map.getLength(); i++) {
					buff.append(" ").append(map.item(i).getNodeName());
					buff.append("=\"").append(map.item(i).getNodeValue())
							.append("\"");
				}
			}

			// ノードの下に子ノードがあるか
			if (node.hasChildNodes()) {
				buff.append(">");
				for (Node child = node.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					buff.append(nodeToString(child));
				}

				buff.append("</").append(node.getNodeName()).append(">");
			} else {
				buff.append("/>");
			}
		}

		return buff.toString();
	}

	/**
	 * 指定したノード下に含まれるエレメント名のノードを検索して先頭ノードを返す。
	 * 
	 * @param n
	 *            Nodeオブジェクト
	 * @param elemName
	 *            エレメント名
	 * @return 要素が検索できた場合は、Nodeオブジェクト。 検索できなかった場合はnull。
	 */
	public Node getTargetElement(Node n, String elemName) {
		if (n == null || elemName == null)
			return null;

		// 要素ノードの場合
		if (n.getNodeType() == Node.ELEMENT_NODE) {
			// 指定されたエレメント名を比較し、
			// 同じならカレントノードを返却
			if (elemName.compareTo(n.getNodeName()) == 0) {
				return n;
			}
		}

		// 子供のノードを辿る
		for (Node child = n.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			// 再帰的にノードを辿る
			Node matchElemNode = getTargetElement(child, elemName);
			if (matchElemNode != null)
				return matchElemNode;
		}
		return null;
	}

	/**
	 * 指定されたノード下に含まれるテキストを返す。
	 * 
	 * @param n
	 *            要素ノード
	 * @return テキスト文字列
	 */
	public String getTextNodeValue(Node n) {
		if (n == null)
			return null;

		// テキストノードの場合
		if (n.getNodeType() == Node.TEXT_NODE
				|| n.getNodeType() == Node.CDATA_SECTION_NODE) {
			// テキストノードを返却
			return n.getNodeValue();
		}

		// 子要素を辿る
		for (Node child = n.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			String value = getTextNodeValue(child);
			if (value != null)
				return value;
		}
		return null;
	}

	/**
	 * テキストノードの判定。
	 * 
	 * @param n
	 *            Nodeオブジェクト
	 * @return テキストノードの場合は、true それ以外は、false
	 */
	public boolean isTextNode(Node n) {
		if (n == null)
			return false;

		return (n.getNodeType() == Node.CDATA_SECTION_NODE || n.getNodeType() == Node.TEXT_NODE);
	}

	/**
	 * エレメントノードの判定します。
	 * 
	 * @param n
	 *            Nodeオブジェクト
	 * @return エレメントノードの場合は、true それ以外は、false
	 */
	public boolean isElementNode(Node n) {
		if (n == null)
			return false;

		return (n.getNodeType() == Node.ELEMENT_NODE);
	}

	/**
	 * 指定されたノード下に含まれるエレメント名のノードを検索して先頭ノードを返す。
	 * 
	 * @param n
	 *            Nodeオブジェクト
	 * @param elemName
	 *            エレメント名
	 * @return 要素が検索できた場合は、Nodeオブジェクト。検索できなかった場合は、null
	 */
	public Node getFirstElementByTagName(Node n, String elemName) {
		if (n == null || elemName == null)
			return null;

		// 要素ノードの場合
		if (n.getNodeType() == Node.ELEMENT_NODE) {
			// 指定されたエレメント名を比較し、
			// 同じならカレントノードを返却
			if (elemName.compareTo(n.getNodeName()) == 0) {
				return n;
			}
		}

		// 子供のノードを辿る
		for (Node child = n.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			// 再帰的にノードを辿る
			Node matchElemNode = getFirstElementByTagName(child, elemName);
			if (matchElemNode != null)
				return matchElemNode;
		}
		return null;
	}

	/**
	 * 指定されたノード下に含まれるエレメント名のノードを検索して全ノードを返す。
	 * 
	 * @param n
	 *            Nodeオブジェクト
	 * @param elemName
	 *            エレメント名
	 * 
	 * @return 要素が検索できた場合は、Nodeオブジェクトリスト。
	 */
	public NodeList getElementsByTagName(Node n, String elemName) {
		return ((Element) n).getElementsByTagName(elemName);
	}

	/**
	 * xmlにスタイルシート(xsl)をかぶせてhtml出力する。<BR>
	 * このメソッドはj2sdk 1.4系でないと動作しません。
	 * 
	 * @param xml
	 *            xmlファイル
	 * @param xsl
	 *            xslファイル
	 * @param outputFile
	 *            出力先ファイル
	 * 
	 * @throws IOException
	 * @throws TransformerException
	 */
	public void transform(File xml, File xsl, File outputFile)
			throws IOException, TransformerException {
		if (!xml.exists())
			return;
		if (!xsl.exists())
			return;

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		// xsl読み込み
		Source xsltSource = new StreamSource(xsl);
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			fos = new FileOutputStream(outputFile);
			bos = new BufferedOutputStream(fos);
			Transformer transformer = transFactory.newTransformer(xsltSource);
			// xml読み込み
			Source source = new StreamSource(xml);
			StreamResult result = new StreamResult(bos);
			transformer.transform(source, result);
			// xml + xsl
		} finally {
			bos.flush();
			bos.close();

			fos.flush();
			fos.close();
		}
	}

	/**
	 * 指定された_$XXXX_を値に持つ要素を探して返す。
	 * 
	 * @param doc
	 *            DOMオブジェクト
	 * 
	 * @param textValue
	 *            _$XXXX_の文字列
	 * @return
	 */
	public Node searchNodeByValue(Document doc, String textValue) {
		Node rootNode = doc.getFirstChild();
		Node value = null;
		// DOMの子要素を探す
		for (Node n = rootNode.getFirstChild(); n != null; n = n
				.getNextSibling()) {
			// テキストノードを探す
			value = getTextNode(n);
			if (value != null && value.getNodeValue() != null
					&& value.getNodeValue().compareTo(textValue) == 0) {
				return value;
			}
			// テキストノードで見つからない場合は属性で探す
			value = getAttributeByValue(n, textValue);
			if (value != null)
				return value;
		}
		return null;
	}

	private Node getTextNode(Node n) {
		if (n == null)
			return null;

		// テキストノードの場合
		if (n.getNodeType() == Node.TEXT_NODE) {
			return n;
		}

		// 子要素を辿る
		for (Node child = n.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			Node value = getTextNode(child);
			if (value.getNodeType() == Node.TEXT_NODE)
				return value;
		}
		return null;
	}

	/**
	 * 指定されたノード下に含まれる属性名を検索してそのノードの文字列を返す。
	 * 
	 * @param n
	 *            要素ノード
	 * @param value
	 *            属性名文字列
	 * 
	 * @return 指定された名前が取得できた場合は、値文字列、それ以外はnull
	 */
	private Node getAttributeByValue(Node n, String value) {
		if (n == null || value == null)
			return null;

		// 属性ノードであり、その値が引数と同じ値か?
		if (n.getNodeType() == Node.ATTRIBUTE_NODE
				&& n.getNodeValue().compareTo(value) == 0)
			return n;

		// 属性ノードを取得
		NamedNodeMap attr = n.getAttributes();
		if (attr == null)
			return null;

		for (int i = 0; i < attr.getLength(); i++) {
			Node attrNode = attr.item(i);
			String attrValue = attrNode.getNodeValue();
			if (attrValue != null && attrValue.compareTo(value) == 0) {
				return attrNode;
			}
		}
		return null;
	}
}
