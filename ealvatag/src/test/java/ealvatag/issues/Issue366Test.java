package ealvatag.issues;

import ealvatag.AbstractTestCase;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.tag.FieldKey;
import ealvatag.tag.NullTag;

import java.io.File;

/**
 * Test deletions of ID3v1 tag
 */
public class Issue366Test extends AbstractTestCase
{
    public void testIssue() throws Exception
    {
        Exception caught = null;
        try
        {
            File orig = new File("testdata", "test91.mp3");
            if (!orig.isFile())
            {
                System.err.println("Unable to test file - not available");
                return;
            }

            File testFile = AbstractTestCase.copyAudioToTmp("test91.mp3");
            AudioFile af = AudioFileIO.read(testFile);
            assertEquals(af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.TRACK), "15");
        }
        catch(Exception e)
        {
            caught=e;
        }
        assertNull(caught);
    }
}
