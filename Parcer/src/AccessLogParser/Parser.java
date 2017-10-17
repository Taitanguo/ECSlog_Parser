package AccessLogParser;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.*;


/**
 * Created by guos6 on 10/13/2017.
 */
public class Parser{
    private final InputStream stream;

    //constructor
    public Parser(InputStream inputFileFromRemoteServer) {
        stream = inputFileFromRemoteServer;
    }

    public synchronized void processTheLastLine() throws IOException {
        try(Scanner scanner = new Scanner (stream)) {
            String result = "";
            while (scanner.hasNextLine()) {
                result = scanner.nextLine();
            }
            processLine(result);
        }
    }

    public void searchBasedOnRequestId(String request_id) throws IOException {
        try (Scanner scanner = new Scanner (stream)) {
            String result;
            while (scanner.hasNextLine()) {
                result = scanner.nextLine();
                if (result.indexOf(request_id) > -1) {
                    processLine(result);
                    break;
                }
            }
            scanner.close();
        }
    }

    protected synchronized void processLine(String curLine) {
        Scanner scanner;
        scanner = new Scanner(curLine);
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

            log("TIMESTAMP : " + TIMESTAMP.trim() + ", REQUEST_ID : " + REQUEST_ID.trim() + ", LOCAL_IP : " + LOCAL_IP.trim() + ", REMOTE_IP : " + REMOTE_IP.trim() + ", USER_NAME : " + USER_NAME.trim() + ", HTTP_METHOD : " + HTTP_METHOD.trim() + ", NAMESPACE : " + NAMESPACE.trim() + ", BUCKET : " + BUCKET.trim() + ", OBJECT_NAME : " + OBJECT_NAME.trim() + ", QUERY_STRING : " + QUERY_STRING.trim() + ", PROTOCOL : " + PROTOCOL.trim() + ", STATUS_CODE : " + STATUS_CODE.trim() + ", TOTAL_TIME : " + TOTAL_TIME.trim() + ", CONTENT_READ : " + CONTENT_READ.trim() + ", CONTENT_COUNT : " + CONTENT_COUNT.trim() + ", STORAGE_PROCESSING_TIME: " + STORAGE_PROCESSING_TIME.trim());
        } else {
            log("Empty or invalid line. Unable to process.");
        }
        scanner.close();
    }

    private static void log(Object aObject) {
        System.out.println(String.valueOf(aObject));
    }

    public static void main(String[] args) throws IOException{
        String user = "admin";
        String password = "ChangeMe";
        System.out.println("Please Input HostIp:");
        Scanner scanner = new Scanner(System.in);
        String host = scanner.next();
        System.out.println("Please Input Rerquest_Id(no or Id):");
        String request_id = scanner.next();
        scanner.close();
        int port=22;
        String remoteFile="/opt/emc/caspian/fabric/agent/services/object/main/log/dataheadsvc-access.log";

        try
        {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Establishing Connection...");
            session.connect();
            System.out.println("Connection established.");
            System.out.println("Crating SFTP Channel.");
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            System.out.println("SFTP Channel created.");


            InputStream dataheadsvc_access= null;
            dataheadsvc_access = sftpChannel.get(remoteFile);
            //search last line
            Parser parser = new Parser(dataheadsvc_access);
            parser.processTheLastLine();
            //search function, notice: input stream cannot be reused
            if (!request_id.equals("no")) {
                dataheadsvc_access = sftpChannel.get(remoteFile);
                Parser parserForSearch = new Parser(dataheadsvc_access);
                parserForSearch.searchBasedOnRequestId(request_id);
            }
            log("Done");

            session.disconnect();
            sftpChannel.quit();
        }
        catch(Exception e){System.err.print(e);}
    }
}
