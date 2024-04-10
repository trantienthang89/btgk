import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.util.Base64;

public class ResultDecoder {
    public static void main(String[] args) {
        decodeResultsFromFile("kq.xml");
    }

    private static void decodeResultsFromFile(String filename) {
        try {
            File inputFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("Student");

            System.out.println("Decoded Results:");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = decode(element.getElementsByTagName("id").item(0).getTextContent());
                    String age = decode(element.getElementsByTagName("age").item(0).getTextContent());
                    String sum = decode(element.getElementsByTagName("sum").item(0).getTextContent());
                    String isDigit = decode(element.getElementsByTagName("isDigit").item(0).getTextContent());
                    
                    System.out.println("Student ID: " + id);
                    System.out.println("Age: " + age);
                    System.out.println("Sum: " + sum);
                    System.out.println("Is Digit Prime: " + isDigit);
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String decode(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }
}
