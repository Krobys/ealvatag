package ealvatag.issues;

import ealvatag.AbstractTestCase;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.tag.FieldKey;
import ealvatag.tag.id3.ID3v23Tag;

import java.io.File;

/**
 *
 */
public class Issue417Test extends AbstractTestCase
{
    /**
     * Multiple WOAR frames ARE allowed
     *
     * @throws Exception
     */
    public void testWOARMultiples() throws Exception
    {
        Exception caught = null;
        try
        {
            File orig = new File("testdata", "01.mp3");
            if (!orig.isFile())
            {
                System.err.println("Unable to test file - not available");
                return;
            }

            File testFile = AbstractTestCase.copyAudioToTmp("01.mp3");
            AudioFile af = AudioFileIO.read(testFile);
            af.getTagOrSetNewDefault().setField(FieldKey.URL_OFFICIAL_ARTIST_SITE, "http://test1.html");
            assertTrue(af.getTag() instanceof ID3v23Tag);
            af.save();
            af = AudioFileIO.read(testFile);
            assertEquals("http://test1.html", af.getTag().getFirst(FieldKey.URL_OFFICIAL_ARTIST_SITE));
            af.getTag().addField(FieldKey.URL_OFFICIAL_ARTIST_SITE,"http://test2.html");
            af.getTag().addField(FieldKey.URL_OFFICIAL_ARTIST_SITE,"http://test3.html");
            af.getTag().addField(FieldKey.URL_OFFICIAL_ARTIST_SITE,"http://test4.html");
            af.save();
            af = AudioFileIO.read(testFile);
            assertEquals("http://test1.html",af.getTag().getFieldAt(FieldKey.URL_OFFICIAL_ARTIST_SITE, 0));
            assertEquals("http://test1.html", af.getTag().getFirst(FieldKey.URL_OFFICIAL_ARTIST_SITE));
            assertEquals("http://test2.html",af.getTag().getFieldAt(FieldKey.URL_OFFICIAL_ARTIST_SITE, 1));

            //No of WOAR Values
            assertEquals(4,af.getTag().getAll(FieldKey.URL_OFFICIAL_ARTIST_SITE).size());

            //Actual No Of Fields used to store WOAR frames
            assertEquals(4, af.getTag().getFields(FieldKey.URL_OFFICIAL_ARTIST_SITE).size());


        }
        catch(Exception e)
        {
            caught=e;
            e.printStackTrace();
        }
        assertNull(caught);
    }
}
