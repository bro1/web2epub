package bro1.utils.xml;

//import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.xerces.parsers.DOMParser;
//import org.cyberneko.html.HTMLConfiguration;
import org.jdom.Document;
//import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
//import org.w3c.tidy.Tidy;
//import org.xamjwg.html.HtmlParserContext;
//import org.xamjwg.html.parser.DocumentBuilderImpl;
//import org.xamjwg.html.test.SimpleHtmlParserContext;
//import org.xml.sax.InputSource;

import bro1.utils.regex.PatternCompiler;

public class XMLUtils {
  private SAXBuilder saxBuilder;
  
  {
    saxBuilder = new SAXBuilder("org.ccil.cowan.tagsoup.Parser");
//    saxBuilder = new SAXBuilder("org.htmlparser.sax.XMLReader");
  }
  
  public String documentToString(Document doc) {
    XMLOutputter outputter = new XMLOutputter();
    return outputter.outputString(doc);
  }
  
  public Document buildDocument(String xmlString) throws Exception {
    StringReader stringReader = new StringReader(xmlString);
    Document jdomDocument = saxBuilder.build(stringReader);
    return jdomDocument;
  }

//  public Document buildDocumentCobra(String xmlString) throws Exception {
//    StringReader stringReader = new StringReader(xmlString);
//    HtmlParserContext context = new SimpleHtmlParserContext();
//    
//    DocumentBuilderImpl dbi = new DocumentBuilderImpl(context);
//    
//    InputSource i = new InputSource(stringReader);
//    org.w3c.dom.Document domdoc = dbi.parse(i);
//    DOMBuilder domBuilder = new DOMBuilder();
//    Document jdomDocument = domBuilder.build(domdoc);
//    return jdomDocument;
//  }

  
//  public Document buildDocumentD(String xmlString) throws Exception {
//    ByteArrayInputStream ba = new ByteArrayInputStream(xmlString.getBytes("utf8"));
//    Tidy d = new Tidy();
//    d.setDocType("omit");
//    org.w3c.dom.Document doc1 = d.parseDOM(ba, System.out);
//    DOMBuilder domb = new DOMBuilder();
//    Document jdomDocument = domb.build(doc1);
//    return jdomDocument;
//  }
  
//  public Document buildDocumentN(String xmlString) throws Exception {
//    
//    DOMParser parser = new DOMParser(new HTMLConfiguration());
//    
//    ByteArrayInputStream ba = new ByteArrayInputStream(xmlString.getBytes("utf8"));
//    
//    parser.parse(new InputSource(ba));
//    org.w3c.dom.Document doc1 = parser.getDocument();
//    
//    DOMBuilder domb = new DOMBuilder();
//    Document jdomDocument = domb.build(doc1);
//    return jdomDocument;
//  }

  public String cleanUpXmlNamespace(String content) {

    String xpNameSpaceDeclaration = "<?xml:namespace.*?>";
    Pattern patternNameSpaceDeclaration = Pattern.compile(xpNameSpaceDeclaration);
    Matcher matcherNameSpaceDeclaration = patternNameSpaceDeclaration.matcher(content);
    String noDeclarations = matcherNameSpaceDeclaration.replaceAll("");

    Pattern patternNameSpacedTag = PatternCompiler.compile("(</?)o:");
    Matcher matcherNameSpacedTag = patternNameSpacedTag.matcher(noDeclarations);
    String noNameSpaces = matcherNameSpacedTag.replaceAll("$1");
    
    return noNameSpaces;
  }

  public String cleanUpXmlNamespaceLrt(String content) {
    String xpNameSpaceDeclaration = "<?xml:namespace.*?>";
    Pattern patternNameSpaceDeclaration = PatternCompiler.compile(xpNameSpaceDeclaration);
    Matcher matcherNameSpaceDeclaration = patternNameSpaceDeclaration.matcher(content);
    String noDeclarations = matcherNameSpaceDeclaration.replaceAll("");

    Pattern patternNameSpacedTag = PatternCompiler.compile("(</?)st1:");
    Matcher matcherNameSpacedTag = patternNameSpacedTag.matcher(noDeclarations);
    String noNameSpaces = matcherNameSpacedTag.replaceAll("$1");
    
    return noNameSpaces;
  }
  
  
  public Document buildNormalDocument(String xmlString) throws Exception {
    
    SAXBuilder sb = new SAXBuilder();
    
    StringReader stringReader = new StringReader(xmlString);
    Document jdomDocument = sb.build(stringReader);
    return jdomDocument;
  }


  
}
