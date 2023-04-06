package io.github.redouane59.quizapp.model;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
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

  public static Words buildFromCSV(String csvContent, char delimiter) throws IOException {
    StringReader     stringReader     = new StringReader(csvContent);
    CSVParserBuilder csvParserBuilder = new CSVParserBuilder().withSeparator(delimiter);
    CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(stringReader).withCSVParser(csvParserBuilder.build());
    CSVReader        csvReader        = csvReaderBuilder.build();
    Set<Word>        words            = new HashSet<>();

    String[] nextRecord;
    csvReader.readNext(); // ignore header
    while ((nextRecord = csvReader.readNext()) != null) {
      if (nextRecord.length > 1 && !nextRecord[0].isBlank() && !nextRecord[1].isBlank()) {
        String word       = nextRecord[0];
        String definition = nextRecord[1];

        String type = null;
        if (nextRecord.length > 2) {
          type = nextRecord[2];
        }
        words.add(new Word(word, definition, type));
      } else {
        System.err.println(nextRecord.length + "\nword only : " + csvContent);
      }
    }

    return new Words(words);
  }


}
