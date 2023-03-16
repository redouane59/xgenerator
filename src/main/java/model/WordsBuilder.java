package model;

import com.opencsv.CSVReader;
import java.io.FileReader;
import lombok.SneakyThrows;

public class WordsBuilder {

  @SneakyThrows
  public static Words build(String filePath) {
    Words words = new Words();

    try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
      // Ignore the header row
      String[] headers = reader.readNext();

      String[] line;
      while ((line = reader.readNext()) != null) {
        Word word;
        if (line.length == 3) {
          word = new Word(line[0], line[1], line[2]);
        } else {
          word = new Word(line[0], line[1]);
        }
        words.getContent().add(word);

      }
    }

    return words;
  }
}
