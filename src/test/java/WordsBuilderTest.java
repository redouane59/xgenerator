import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import model.Word;
import model.Words;
import model.WordsBuilder;
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


}
