package ealvatag.tag.id3;

import ealvatag.AbstractTestCase;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.audio.mp3.MP3File;
import ealvatag.tag.FieldKey;
import ealvatag.tag.NullTag;
import ealvatag.tag.TagOptionSingleton;
import ealvatag.tag.id3.framebody.FrameBodyTDAT;
import ealvatag.tag.id3.framebody.FrameBodyTDRC;
import ealvatag.tag.id3.framebody.FrameBodyTIME;
import ealvatag.tag.id3.framebody.FrameBodyTYER;
import ealvatag.tag.id3.valuepair.TextEncoding;
import ealvatag.tag.reference.ID3V2Version;

import java.io.File;

/**
 */
public class FrameTDATTest extends AbstractTestCase
{
    public void testID3Specific() throws Exception
    {
        Exception e=null;
        try
        {
            ID3v23Tag tag = new ID3v23Tag();
            ID3v23Frame frame = new ID3v23Frame("TDAT");
            frame.setBody(new FrameBodyTDAT(TextEncoding.ISO_8859_1,"3006"));
            tag.addFrame(frame);
            assertEquals("3006",tag.getFirst("TDAT"));

            ID3v24Tag v24tag = new ID3v24Tag(tag);
            assertEquals(1,v24tag.getFieldCount());
            assertNotNull(v24tag.getFirst("TDAT"));
            assertEquals("-06-30",v24tag.getFirst("TDRC"));
        }
        catch(Exception ex)
        {
            e=ex;
            ex.printStackTrace();
        }
        assertNull(e);
    }

    public void testID3SpecificWithYearAndTime() throws Exception
    {
        Exception e=null;
        try
        {
            ID3v23Tag tag = new ID3v23Tag();
            ID3v23Frame frame = new ID3v23Frame("TDAT");
            frame.setBody(new FrameBodyTDAT(TextEncoding.ISO_8859_1,"3006"));
            tag.addFrame(frame);
            assertEquals("3006",tag.getFirst("TDAT"));

            ID3v23Frame frameYear = new ID3v23Frame("TYER");
            frameYear.setBody(new FrameBodyTYER(TextEncoding.ISO_8859_1,"1980"));
            tag.addFrame(frameYear);
            assertEquals("1980",tag.getFirst("TYER"));

            ID3v23Frame frameTime = new ID3v23Frame("TIME");
            frameTime.setBody(new FrameBodyTIME(TextEncoding.ISO_8859_1,"1200"));
            tag.addFrame(frameTime);
            assertEquals("1200",tag.getFirst("TIME"));
            assertEquals(3,tag.getFieldCount());

            //Create v24tag from v23, all these time frames shouod be merged into one
            ID3v24Tag v24tag = new ID3v24Tag(tag);
            assertEquals(1,v24tag.getFieldCount());
            assertNotNull(v24tag.getFirst("TDAT"));
            assertNotNull(v24tag.getFirst("TIME"));
            assertNotNull(v24tag.getFirst("TYER"));
            assertEquals("1980-06-30T12:00",v24tag.getFirst("TDRC"));

            //Now create v23tag from v24, the tdrc frame should be split up and the values of the individual
            //values should match the v23 format not simply break up the v24 string
            tag = new ID3v23Tag(v24tag);
            assertEquals(3,tag.getFieldCount());
            assertEquals("3006",tag.getFirst("TDAT"));
            assertEquals("1980",tag.getFirst("TYER"));
            assertEquals("1200",tag.getFirst("TIME"));

            //Do it again to check it works second time around
            v24tag = new ID3v24Tag(tag);
            assertEquals(1,v24tag.getFieldCount());
            assertNotNull(v24tag.getFirst("TDAT"));
            assertNotNull(v24tag.getFirst("TIME"));
            assertNotNull(v24tag.getFirst("TYER"));
            assertEquals("1980-06-30T12:00",v24tag.getFirst("TDRC"));
        }
        catch(Exception ex)
        {
            e=ex;
            ex.printStackTrace();
        }
        assertNull(e);
    }

    public void testConvertingPartialDate() throws Exception
    {
        Exception e=null;
        try
        {
            ID3v24Tag tag = new ID3v24Tag();
            ID3v24Frame frame = new ID3v24Frame("TDRC");
            frame.setBody(new FrameBodyTDRC(TextEncoding.ISO_8859_1,"2006-06"));
            tag.addFrame(frame);
            assertEquals("2006-06",tag.getFirst("TDRC"));

            ID3v23Tag v23tag = new ID3v23Tag(tag);
            assertEquals(2,v23tag.getFieldCount());
            assertNotNull(v23tag.getFirst("TYER"));
            assertEquals("2006",v23tag.getFirst("TYER"));
            assertNotNull(v23tag.getFirst("TDAT"));
            //"01" is created because cant just store month in this field in v23
            assertEquals("0106",v23tag.getFirst("TDAT"));

            tag = new ID3v24Tag(v23tag);
            //But because is MonthOnly flag set dd gets lost when convert back to v24
            assertEquals("2006-06",tag.getFirst("TDRC"));

        }
        catch(Exception ex)
        {
            e=ex;
            ex.printStackTrace();
        }
        assertNull(e);
    }

    public void testReadingID3AsV24Generic() throws Exception
    {
        File testFile = AbstractTestCase.copyAudioToTmp("testV1.mp3", new File("id3asv24.mp3"));
        TagOptionSingleton.getInstance().setID3V2Version(ID3V2Version.ID3_V23);
        AudioFile af = AudioFileIO.read(testFile);
        af.getConvertedTagOrSetNewDefault();
        af.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
        af.getTag().or(NullTag.INSTANCE).setField(FieldKey.YEAR, "2003-06-23");
        af.save();
        assertEquals(af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.YEAR),"2003-06-23");
        af = AudioFileIO.read(testFile);
        assertEquals(af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ARTIST),"fred");
        assertEquals(af.getTag().or(NullTag.INSTANCE).getAll(FieldKey.ARTIST).get(0),"fred");
        assertEquals(af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.YEAR),"2003-06-23");
        assertEquals(af.getTag().or(NullTag.INSTANCE).getAll(FieldKey.YEAR).get(0),"2003-06-23");
        assertEquals(((MP3File)af).getID3v2TagAsv24().getFirst(FieldKey.YEAR),"2003-06-23");
        assertEquals(((MP3File)af).getID3v2TagAsv24().getAll(FieldKey.YEAR).get(0),"2003-06-23");

    }


}
