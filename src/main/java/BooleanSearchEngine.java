import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
