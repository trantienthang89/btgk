import java.io.*;
import java.util.concurrent.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

class Student {
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

class AgeCalculator implements Runnable {
    private BlockingQueue<Student> studentQueue;
    private BlockingQueue<String> resultQueue;

    public AgeCalculator(BlockingQueue<Student> studentQueue, BlockingQueue<String> resultQueue) {
        this.studentQueue = studentQueue;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Student student = studentQueue.take();
                if (student == null || student.id.equals("EOF")) {
                    resultQueue.put("EOF");
                    break;
                }
                int age = calculateAge(student.dateOfBirth);
                int sum = calculateSumOfDigits(student.dateOfBirth);
                boolean isPrime = isPrime(sum);
                resultQueue.put("<Student>\n<id>" + encode(student.id) + "</id>\n<age>" + encode(String.valueOf(age)) + "</age>\n<sum>" + encode(String.valueOf(sum)) + "</sum>\n<isDigit>" + encode(String.valueOf(isPrime)) + "</isDigit>\n</Student>");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int calculateAge(String dob) {
        LocalDate birthDate = LocalDate.parse(dob, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate currentDate = LocalDate.now();
        return currentDate.getYear() - birthDate.getYear();
    }

    private int calculateSumOfDigits(String dob) {
        int sum = 0;
        for (char c : dob.toCharArray()) {
            if (Character.isDigit(c)) {
                sum += Character.getNumericValue(c);
            }
        }
        return sum;
    }

    private boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    private String encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
}

public class Main {
    public static void main(String[] args) {
        BlockingQueue<Student> studentQueue = new LinkedBlockingQueue<>();
        BlockingQueue<String> resultQueue = new LinkedBlockingQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new Reader("student.xml", studentQueue));
        executor.execute(new AgeCalculator(studentQueue, resultQueue));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("kq.xml"));
            writer.write("<Students>\n");
            while (true) {
                String result = resultQueue.take();
                if (result.equals("EOF")) {
                    writer.write("</Students>");
                    writer.close();
                    break;
                }
                writer.write(result + "\n");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}

class Reader implements Runnable {
    private String filename;
    private BlockingQueue<Student> studentQueue;

    public Reader(String filename, BlockingQueue<Student> studentQueue) {
        this.filename = filename;
        this.studentQueue = studentQueue;
    }

    @Override
    public void run() {
        try {
            File inputFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Student");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String id = eElement.getAttribute("id");
                    String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                    String address = eElement.getElementsByTagName("address").item(0).getTextContent();
                    String dob = eElement.getElementsByTagName("dateOfBirth").item(0).getTextContent();
                    studentQueue.put(new Student(id, name, address, dob));
                }
            }
            // Add EOF signal to indicate end of processing
            studentQueue.put(new Student("EOF", "", "", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
