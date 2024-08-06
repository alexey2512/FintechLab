import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import util.DatabaseManager;
import util.Translator;

import java.io.*;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length != 1 || !(args[0].equals("requests") || args[0].equals("last") || args[0].equals("delete"))) {
                try (Scanner scanner = new Scanner(System.in)) {
                    System.out.print("Enter source language code: ");
                    String srcLang = scanner.nextLine();
                    System.out.print("Enter target language code: ");
                    String trgLang = scanner.nextLine();
                    Translator translator = new Translator(srcLang, trgLang);
                    String originalText, translatedText;
                    if (args.length == 1 && args[0].equals("files")) {
                        System.out.print("Enter file path of original text: ");
                        String srcPath = scanner.nextLine();
                        try (BufferedReader br =
                                     new BufferedReader(
                                             new InputStreamReader(
                                                     new FileInputStream(srcPath)
                                             )
                                     )
                        ) {
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line).append(System.lineSeparator());
                            }
                            originalText = sb.toString().trim();
                            translatedText = translator.parallelTranslate(originalText);
                            try (BufferedWriter bw =
                                         new BufferedWriter(
                                                 new OutputStreamWriter(
                                                         new FileOutputStream("translated.txt")
                                                 )
                                         )
                            ) {
                                bw.write(translatedText);
                            }
                        }
                    } else {
                        System.out.print("Enter text to translate: ");
                        originalText = scanner.nextLine();
                        translatedText = translator.parallelTranslate(originalText);
                        System.out.println("Translated text: " + translatedText);
                    }
                    System.out.println("TRANSLATION COMPLETE");
                    DatabaseManager manager = new DatabaseManager();
                    manager.addUserRequest(srcLang, trgLang, originalText, translatedText);
                }
            } else {
                DatabaseManager manager = new DatabaseManager();
                switch (args[0]) {
                    case "requests":
                        manager.showAllUserRequests();
                        break;
                    case "last":
                        manager.showLastUserRequest();
                        break;
                    case "delete":
                        manager.deleteAllUserRequests();
                        break;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Process interrupted: " + e.getMessage());
        } catch (ExecutionException e) {
            System.err.println("Execution error: " + e.getMessage());
        } catch (HttpClientErrorException e) {
            System.err.println("Client error occurred: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (HttpServerErrorException e) {
            System.err.println("Server error occurred: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Program error occurred: " + e.getMessage());
        }
    }
}
