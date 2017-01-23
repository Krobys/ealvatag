package ealvatag.issues;

import ealvatag.AbstractTestCase;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.tag.FieldKey;
import ealvatag.tag.NullTag;
import ealvatag.tag.Tag;

import java.io.File;

/**
 * Test Mp4 genres can be invalid
 */
public class Issue225Test extends AbstractTestCase
{
    /**
     * Reading a file contains genre field but set to invalid value 149, because Mp4genreField always
     * store the value the genre is mapped to we return null. This is correct behaviour.
     */
    public void testReadInvalidGenre()
    {
        String genre = null;

        File orig = new File("testdata", "test30.m4a");
        if (!orig.isFile())
        {
            return;
        }

        Exception exceptionCaught = null;
        try
        {
            File testFile = AbstractTestCase.copyAudioToTmp("test30.m4a");
            AudioFile f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            genre = tag.getFirst(FieldKey.GENRE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
        assertNull(genre);

    }
}
