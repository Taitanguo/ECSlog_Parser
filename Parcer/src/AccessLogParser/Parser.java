package AccessLogParser;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by guos6 on 10/13/2017.
 */
public class Parser{
    private final Path fFilePath;
    private final static Charset ENCODING = StandardCharsets.UTF_8;
    //constructor
    public Parser(String aFileName) {
        fFilePath = Paths.get(aFileName);
    }

    public final void processTheLastLine() throws IOException {
        try(Scanner scanner = new Scanner(fFilePath, ENCODING.name())) {
            String result = "";
            while (scanner.hasNextLine()) {
                result = scanner.nextLine();
            }
            processLine(result);
        }
    }

    protected void processLine(String lastLine) {
        String[] fields = lastLine.split(" ");
        for (String s : fields) {
            System.out.println(s);
        }
        Scanner scanner;
        scanner = new Scanner(lastLine);
        scanner.useDelimiter(" ");
        if(scanner.hasNext()) {
            String TIMESTAMP = scanner.next();
            String REQUEST_ID = scanner.next();
            String LOCAL_IP = scanner.next();
            String REMOTE_IP = scanner.next();
            String USER_NAME = scanner.next();
            String HTTP_METHOD = scanner.next();
            String NAMESPACE = scanner.next();
            String BUCKET = scanner.next();
            String OBJECT_NAME = scanner.next();
            String QUERY_STRING = scanner.next();
            String PROTOCOL = scanner.next();
            String STATUS_CODE = scanner.next();
            String TOTAL_TIME = scanner.next();
            String CONTENT_READ = scanner.next();
            String CONTENT_COUNT = scanner.next();
            String STORAGE_PROCESSING_TIME = scanner.next();

            log("TIMESTAMP : " + TIMESTAMP.trim() + ", REQUEST_ID : " + REQUEST_ID.trim());
        } else {
            log("Empty or invalid line. Unable to process.");
        }
    }

    private static void log(Object aObject) {
        System.out.println(String.valueOf(aObject));
    }

    public static void main(String[] args) throws IOException{
        Parser parser = new Parser("C:\\Users\\guos6\\Desktop\\dataheadsvc-access.log");
        parser.processTheLastLine();
        log("Done.");
    }
}
