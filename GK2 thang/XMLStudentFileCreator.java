import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLStudentFileCreator {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("1", "thang", "Quảng Trị", "2005-09-08"));
        students.add(new Student("2", "rot", "quảng trị", "2005-4-9"));

        createStudentXMLFile(students, "student.xml");
    }

    public static void createStudentXMLFile(List<Student> students, String filename) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Students");
            doc.appendChild(rootElement);

            for (Student student : students) {
                // Student element
                Element studentElement = doc.createElement("Student");
                rootElement.appendChild(studentElement);

                // Id attribute
                studentElement.setAttribute("id", student.id);

                // Name element
                Element nameElement = doc.createElement("name");
                nameElement.appendChild(doc.createTextNode(student.name));
                studentElement.appendChild(nameElement);

                // Address element
                Element addressElement = doc.createElement("address");
                addressElement.appendChild(doc.createTextNode(student.address));
                studentElement.appendChild(addressElement);

                // DateOfBirth element
                Element dobElement = doc.createElement("dateOfBirth");
                dobElement.appendChild(doc.createTextNode(student.dateOfBirth));
                studentElement.appendChild(dobElement);
            }

            // Write the content into XML file
            FileOutputStream fos = new FileOutputStream(new File(filename));
            javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(fos));
            fos.close();

            System.out.println("Student XML file created successfully: " + filename);
        } catch (ParserConfigurationException | javax.xml.transform.TransformerException | IOException e) {
            e.printStackTrace();
        }
    }

    static class Student {
        String id;
        String name;
        String address;
        String dateOfBirth;

        public Student(String id, String name, String address, String dateOfBirth) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.dateOfBirth = dateOfBirth;
        }
    }
}
