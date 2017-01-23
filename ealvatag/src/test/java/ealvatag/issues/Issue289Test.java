package ealvatag.issues;

import ealvatag.AbstractTestCase;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.audio.ogg.OggFileReader;
import ealvatag.tag.FieldKey;
import ealvatag.tag.NullTag;

import java.io.File;

/**
 *File corrupt after write
 */
public class Issue289Test extends AbstractTestCase
{
    public void testSavingOggFile()
    {
        File orig = new File("testdata", "test58.ogg");
        if (!orig.isFile())
        {
            System.err.println("Unable to test file - not available");
            return;
        }



        File testFile = null;
        Exception exceptionCaught = null;
        try
        {
            testFile = AbstractTestCase.copyAudioToTmp("test58.ogg");

            OggFileReader ofr = new OggFileReader();
            //ofr.shortSummarizeOggPageHeaders(testFile);

            AudioFile af = AudioFileIO.read(testFile);
            System.out.println(af.getTag().or(NullTag.INSTANCE).toString());
            af.getTag().or(NullTag.INSTANCE).setField(FieldKey.MUSICIP_ID,"91421a81-50b9-f577-70cf-20356eea212e");
            af.save();

            af = AudioFileIO.read(testFile);
            assertEquals("91421a81-50b9-f577-70cf-20356eea212e",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.MUSICIP_ID));

            ofr.shortSummarizeOggPageHeaders(testFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionCaught = e;
        }

        assertNull(exceptionCaught);
    }


}
