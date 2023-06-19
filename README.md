# Описание работы
### BooleanSearchEngine
Класс BooleanSearchEngine выполняет основную логику программы - в конструкторе выполняет индексацию слов по пдф'никам и страницам, 
а так же последующий поиск и предоставление хранимой информации отдельным методом search(word).
```java
public class BooleanSearchEngine implements SearchEngine {
    private HashMap<String, List<PageEntry>> dictionary;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        HashMap<String, List<PageEntry>> temp_dictionary = new HashMap<>();
        for (File pdf : Objects.requireNonNull(pdfsDir.listFiles())) {
            var doc = new PdfDocument(new PdfReader(pdf));
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                var words = PdfTextExtractor.getTextFromPage(doc.getPage(i + 1)).split("\\P{IsAlphabetic}+");

                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }

                for (Map.Entry<String,Integer> entry : freqs.entrySet()) {
                    List<PageEntry> extendedList = temp_dictionary.getOrDefault(entry.getKey(), new ArrayList<>());
                    extendedList.add(new PageEntry(pdf.getName(), i + 1, entry.getValue()));
                    temp_dictionary.put(entry.getKey(), extendedList);
                }
            }
        }
        this.dictionary = temp_dictionary;
    }

    @Override
    public List<PageEntry> search(String word) {
        for (Map.Entry<String, List<PageEntry>> entry : dictionary.entrySet()) {
            if (entry.getKey().equals(word)) {
                List<PageEntry> list = entry.getValue();
                Collections.sort(list);
                return list;
            }
        }
        return Collections.emptyList();
    }
}
```
### Main
Класс Main выполняет роль сервера слушает по 8989 порту подключение от Client, принимает на вход одно слово, 
и передает на выход Json строку с результатом.
```java
public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (ServerSocket serverSocket = new ServerSocket(8989)) {
            while (true) {
                try (   Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ) {
                    out.println(gson.toJson(engine.search(in.readLine())));
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}
```
### Client
Класс Client выполняет роль клиента, подключается к 8989 порту и отправляет на выход одно слово. 
Принимает на вход Json строку и выводит в консоль.
```java
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
```
### PageEntry
Класс PageEntry описывает хранимую информацию и метод сравнения, удовлетворяющий обратной сортировке 
на основе числа вхождений искомого результата в страницу и документ.
```java
public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry o) {
        return Integer.compare(o.count,this.count);
    }

    @Override
    public String toString() {
        return "pdf=" + this.pdfName + " page=" + this.page + " count=" + this.count +"\n";
    }
}
```
# Результат работы программы
```json
Введите слово для поиска
БиЗнес
[
  {
    "pdfName": "Этапы оценки проекта_ понятия, методы и полезные инструменты.pdf",
    "page": 4,
    "count": 6
  },
  {
    "pdfName": "Этапы оценки проекта_ понятия, методы и полезные инструменты.pdf",
    "page": 12,
    "count": 6
  },
  {
    "pdfName": "Этапы оценки проекта_ понятия, методы и полезные инструменты.pdf",
    "page": 5,
    "count": 3
  },
  {
    "pdfName": "1. DevOps_MLops.pdf",
    "page": 5,
    "count": 2
  },
  {
    "pdfName": "Что такое блокчейн.pdf",
    "page": 1,
    "count": 2
  },
  {
    "pdfName": "Что такое блокчейн.pdf",
    "page": 3,
    "count": 2
  },
  {
    "pdfName": "1. DevOps_MLops.pdf",
    "page": 3,
    "count": 1
  },
  {
    "pdfName": "1. DevOps_MLops.pdf",
    "page": 4,
    "count": 1
  },
  {
    "pdfName": "Как управлять рисками IT-проекта.pdf",
    "page": 2,
    "count": 1
  },
  {
    "pdfName": "Продвижение игр.pdf",
    "page": 7,
    "count": 1
  },
  {
    "pdfName": "Что такое блокчейн.pdf",
    "page": 2,
    "count": 1
  },
  {
    "pdfName": "Что такое блокчейн.pdf",
    "page": 4,
    "count": 1
  },
  {
    "pdfName": "Что такое блокчейн.pdf",
    "page": 5,
    "count": 1
  },
  {
    "pdfName": "Что такое блокчейн.pdf",
    "page": 7,
    "count": 1
  },
  {
    "pdfName": "Что такое блокчейн.pdf",
    "page": 9,
    "count": 1
  },
  {
    "pdfName": "Этапы оценки проекта_ понятия, методы и полезные инструменты.pdf",
    "page": 2,
    "count": 1
  },
  {
    "pdfName": "Этапы оценки проекта_ понятия, методы и полезные инструменты.pdf",
    "page": 11,
    "count": 1
  }
]
```