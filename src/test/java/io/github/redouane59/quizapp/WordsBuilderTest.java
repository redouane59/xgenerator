package io.github.redouane59.quizapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.github.redouane59.quizapp.model.Word;
import io.github.redouane59.quizapp.model.Words;
import io.github.redouane59.quizapp.model.WordsBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WordsBuilderTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void testBuildWordsFromCsvFile() throws IOException {
    String     csvContent = "input,output,type\nhello,bonjour,noun\nworld,monde,noun\n";
    File       csvFile    = temporaryFolder.newFile("test.csv");
    FileWriter fileWriter = new FileWriter(csvFile);
    fileWriter.write(csvContent);
    fileWriter.close();

    Words words = WordsBuilder.build(csvFile.getAbsolutePath());

    Set<Word> expectedWords = new HashSet<>();
    expectedWords.add(new Word("hello", "bonjour", "noun"));
    expectedWords.add(new Word("world", "monde", "noun"));

    assertEquals(expectedWords, words.getContent());
  }

  @Test
  public void testBuildFromCSVContent() throws IOException {
    String csvContent = "word,definition,type\n" +
                        "apple,a fruit,fruit\n" +
                        "banana,a fruit,fruit\n" +
                        "carrot,a vegetable,vegetable\n";

    Words words = WordsBuilder.buildFromCSV(csvContent);
    assertNotNull(words.getContent());
    assertEquals(3, words.getContent().size());

  }


}
