package ealvatag.issues;

import ealvatag.AbstractTestCase;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.tag.FieldKey;

import java.io.File;

/**
 * Converting FrameBodyUnsupported with known identifier to FrameBodyIPLS (v23) causing NoSuchMethodException.
 * Not really sure why this is happening but we should check and take action instead of failing as we currently do
 */
public class Issue285Test extends AbstractTestCase
{
    public void testSavingOggFile()
    {
        File orig = new File("testdata", "test57.ogg");
        if (!orig.isFile())
        {
            System.err.println("Unable to test file - not available");
            return;
        }



        File testFile = null;
        Exception exceptionCaught = null;
        try
        {
            testFile = AbstractTestCase.copyAudioToTmp("test57.ogg");

            //OggFileReader ofr = new OggFileReader();
            //ofr.summarizeOggPageHeaders(testFile);

            AudioFile af = AudioFileIO.read(testFile);
            af.getTag().setField(FieldKey.COMMENT,"TEST");
            af.save();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionCaught = e;
        }

        assertNull(exceptionCaught);
    }


}
