package ealvatag.issues;

import ealvatag.AbstractTestCase;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.tag.FieldKey;
import ealvatag.tag.NullTag;
import ealvatag.tag.id3.AbstractID3v2Frame;
import ealvatag.tag.id3.ID3v23Frame;
import ealvatag.tag.id3.ID3v23Frames;
import ealvatag.tag.id3.ID3v23Tag;
import ealvatag.tag.id3.framebody.FrameBodyTCOP;

import java.io.File;

/**
 * Test read large mp3 with extended header
 */
public class Issue269Test extends AbstractTestCase
{

    /**
     * Test read mp3 that says it has extended header but doesnt really
     */
    public void testReadMp3WithExtendedHeaderFlagSetButNoExtendedHeader()
    {
        File orig = new File("testdata", "test46.mp3");
        if (!orig.isFile())
        {
            System.err.println("Unable to test file - not available");
            return;
        }

        File testFile = null;
        Exception exceptionCaught = null;
        try
        {
            testFile = AbstractTestCase.copyAudioToTmp("test46.mp3");

            //Read File okay
            AudioFile af = AudioFileIO.read(testFile);
            System.out.println(af.getTag().or(NullTag.INSTANCE).toString());
            assertEquals("00000",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.BPM));
            assertEquals("thievery corporation - Om Lounge",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ARTIST));

            af.getTag().or(NullTag.INSTANCE).setField(FieldKey.ALBUM,"FRED");
            af.save();
            af = AudioFileIO.read(testFile);
            System.out.println(af.getTag().or(NullTag.INSTANCE).toString());
            assertEquals("FRED",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ALBUM));


        }
        catch(Exception e)
        {
            e.printStackTrace();
            exceptionCaught=e;
        }

        assertNull(exceptionCaught);
    }

     /**
     * Test read mp3 with extended header and crc-32 check
     */
    public void testReadID3v23Mp3WithExtendedHeaderAndCrc()
    {
        File orig = new File("testdata", "test47.mp3");
        if (!orig.isFile())
        {
            System.err.println("Unable to test file - not available");
            return;
        }

        File testFile = null;
        Exception exceptionCaught = null;
        try
        {
            testFile = AbstractTestCase.copyAudioToTmp("test47.mp3");

            //Read File okay
            AudioFile af = AudioFileIO.read(testFile);
            System.out.println(af.getTag().or(NullTag.INSTANCE).toString());
            assertEquals("tonight (instrumental)",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.TITLE));
            assertEquals("Young Gunz",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ARTIST));

            ID3v23Tag id3v23Tag = (ID3v23Tag)af.getTag().or(NullTag.INSTANCE);
            assertEquals(156497728,id3v23Tag.getCrc32());
            assertEquals(0,id3v23Tag.getPaddingSize());

            af.getTag().or(NullTag.INSTANCE).setField(FieldKey.ALBUM,"FRED");
            af.save();
            af = AudioFileIO.read(testFile);
            System.out.println(af.getTag().or(NullTag.INSTANCE).toString());
            assertEquals("FRED",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ALBUM));


        }
        catch(Exception e)
        {
            e.printStackTrace();
            exceptionCaught=e;
        }

        assertNull(exceptionCaught);
    }

    /**
     * Test read and stores encrypted frames separately, and that they are preserved when writing frame
     *
     */
    public void testReadMp3WithEncryptedField()
    {
        File orig = new File("testdata", "test48.mp3");
        if (!orig.isFile())
        {
            System.err.println("Unable to test file - not available");
            return;
        }

        File testFile = null;
        Exception exceptionCaught = null;
        try
        {
            testFile = AbstractTestCase.copyAudioToTmp("test48.mp3");

            //Read File okay
            AudioFile af = AudioFileIO.read(testFile);
            System.out.println(af.getTag().or(NullTag.INSTANCE).toString());
            assertEquals("Don't Leave Me",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.TITLE));
            assertEquals("All-American Rejects",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ARTIST));

            ID3v23Tag id3v23Tag = (ID3v23Tag)af.getTag().or(NullTag.INSTANCE);
            assertEquals(0,id3v23Tag.getPaddingSize());

            AbstractID3v2Frame frame1 = (AbstractID3v2Frame)id3v23Tag.getFrame(ID3v23Frames.FRAME_ID_V3_COPYRIGHTINFO);
            assertNotNull(frame1);
            assertEquals("",((FrameBodyTCOP)frame1.getBody()).getText());

            //This frame is marked as encrypted(Although it actually isn't) so we cant decode it we should just store it
            //as a special encrypted frame.
            ID3v23Frame frame = (ID3v23Frame)id3v23Tag.getFrame(ID3v23Frames.FRAME_ID_V3_ENCODEDBY);
            assertNull(frame);
            frame = (ID3v23Frame)id3v23Tag.getEncryptedFrame(ID3v23Frames.FRAME_ID_V3_ENCODEDBY);
            assertNotNull(frame);
            assertEquals(0x22,frame.getEncryptionMethod());
            assertEquals(0,frame.getGroupIdentifier());
            assertEquals(0,frame.getStatusFlags().getOriginalFlags());
            //ealvatag converts to this because encodeby frame should be updated if the audio is changed and so
            //this falg should be set
            assertEquals(0x40,frame.getStatusFlags().getWriteFlags());
            af.getTag().or(NullTag.INSTANCE).setField(FieldKey.ALBUM,"FRED");
            af.save();
            af = AudioFileIO.read(testFile);
            id3v23Tag = (ID3v23Tag)af.getTag().or(NullTag.INSTANCE);
            System.out.println(af.getTag().or(NullTag.INSTANCE).toString());
            assertEquals("FRED",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ALBUM));

            //The frame is preserved and still encrypted
            frame = (ID3v23Frame)id3v23Tag.getFrame(ID3v23Frames.FRAME_ID_V3_ENCODEDBY);
            assertNull(frame);
            frame = (ID3v23Frame)id3v23Tag.getEncryptedFrame(ID3v23Frames.FRAME_ID_V3_ENCODEDBY);
            assertNotNull(frame);
            //Encryption Method Preserved
            assertEquals(0x22,frame.getEncryptionMethod());
            assertEquals(0,frame.getGroupIdentifier());
            //Note fiel preservation flag now set
            assertEquals(0x40,frame.getStatusFlags().getOriginalFlags());
            assertEquals(0x40,frame.getStatusFlags().getWriteFlags());

        }
        catch(Exception e)
        {
            e.printStackTrace();
            exceptionCaught=e;
        }
        assertNull(exceptionCaught);
    }

    /**
        * Test read mp3 with extended header and crc-32 check
        */
       public void testReadID3v24Mp3WithExtendedHeaderAndCrc()
       {
           File orig = new File("testdata", "test47.mp3");
           if (!orig.isFile())
           {
               System.err.println("Unable to test file - not available");
               return;
           }

           File testFile = null;
           Exception exceptionCaught = null;
           try
           {
               testFile = AbstractTestCase.copyAudioToTmp("test47.mp3");

               //Read File okay
               AudioFile af = AudioFileIO.read(testFile);
               System.out.println(af.getTag().or(NullTag.INSTANCE).toString());
               assertEquals("tonight (instrumental)",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.TITLE));
               assertEquals("Young Gunz",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ARTIST));

               ID3v23Tag id3v23Tag = (ID3v23Tag)af.getTag().or(NullTag.INSTANCE);
               assertEquals(156497728,id3v23Tag.getCrc32());
               assertEquals(0,id3v23Tag.getPaddingSize());

               af.getTag().or(NullTag.INSTANCE).setField(FieldKey.ALBUM,"FRED");
               af.save();
               af = AudioFileIO.read(testFile);
               System.out.println(af.getTag().or(NullTag.INSTANCE).toString());
               assertEquals("FRED",af.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ALBUM));


           }
           catch(Exception e)
           {
               e.printStackTrace();
               exceptionCaught=e;
           }

           assertNull(exceptionCaught);
       }

}
