import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
        while (true){
            try (   Socket clientSocket = new Socket("localhost",8989);
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                System.out.println("Введите слово для поиска");
                String word = new Scanner(System.in).nextLine().toLowerCase();
                out.println(word);
                String result;
                while((result = in.readLine()) != null){
                    System.out.println(result);
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
