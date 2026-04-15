package hexlet.code;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class FileReadingTest {

    /**
     * Get a path to a resource file in the src/test/resources directory.
     *
     * @param resource Resource name.
     * @return File Resource file.
     */
    protected static File getResourceFile(String resource) {
        return new File(FileReadingTest.class.getClassLoader().getResource(resource).getFile());
    }

    /**
     * Read a resource file from the src/test/resources directory.
     *
     * @param resource Resource name.
     * @return String Fixture contents.
     */
    protected static String readFixture(String resource) throws IOException {
        var file = getResourceFile(resource).toPath();
        return Files.readString(file);
    }

}
