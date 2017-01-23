package ealvatag.issues;

import ealvatag.AbstractTestCase;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.tag.FieldKey;
import ealvatag.tag.NullTag;
import ealvatag.tag.Tag;
import ealvatag.tag.TagOptionSingleton;

import java.io.File;

/**
 * Truncation haling for string data and picture data
 */
public class Issue265Test extends AbstractTestCase
{

    /**
     * Try an d write too large a file
     */
    public void testWriteTooLargeStringToFile()
    {
        File orig = new File("testdata", "test7.wma");
        if (!orig.isFile())
        {
            System.err.println("Unable to test file - not available");
            return;
        }

        Exception exceptionCaught = null;
        try
        {
            TagOptionSingleton.getInstance().setTruncateTextWithoutErrors(false);

            File testFile = AbstractTestCase.copyAudioToTmp("test7.wma");
            AudioFile f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            assertEquals(0, tag.getFields(FieldKey.COVER_ART).size());


            //Now createField artwork field
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 34000;i++)
            {
                sb.append("x");
            }
            tag.setField(FieldKey.ARTIST,sb.toString());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNotNull(exceptionCaught);
        assertTrue(exceptionCaught instanceof IllegalArgumentException);
    }



    /**
     * Try and write too large a file, automtically truncated if option set
     */
    public void testWriteTruncateStringToFile()
    {
        File orig = new File("testdata", "test7.wma");
        if (!orig.isFile())
        {
            System.err.println("Unable to test file - not available");
            return;
        }

        Exception exceptionCaught = null;
        try
        {
            File testFile = AbstractTestCase.copyAudioToTmp("test7.wma");
            AudioFile f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            assertEquals(0, tag.getFields(FieldKey.COVER_ART).size());

            //Enable value
            TagOptionSingleton.getInstance().setTruncateTextWithoutErrors(true);

            //Now createField artwork field
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 34000;i++)
            {
                sb.append("x");
            }
            tag.setField(FieldKey.ARTIST,sb.toString());
            f.save();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
        * Try an d write too large a file
        */
       public void testWriteTooLargeStringToFileContentDesc()
       {
           File orig = new File("testdata", "test7.wma");
           if (!orig.isFile())
           {
               System.err.println("Unable to test file - not available");
               return;
           }

           Exception exceptionCaught = null;
           try
           {
               File testFile = AbstractTestCase.copyAudioToTmp("test7.wma");
               AudioFile f = AudioFileIO.read(testFile);
               Tag tag = f.getTag().or(NullTag.INSTANCE);

               TagOptionSingleton.getInstance().setTruncateTextWithoutErrors(false);
               StringBuffer sb = new StringBuffer();
               for (int i = 0; i < 34000;i++)
               {
                   sb.append("x");
               }
               tag.setField(FieldKey.TITLE,sb.toString());

           }
           catch (Exception e)
           {
               e.printStackTrace();
               exceptionCaught = e;
           }
           assertNotNull(exceptionCaught);
           assertTrue(exceptionCaught instanceof IllegalArgumentException);
       }



       /**
        * Try and write too large a file, automtically truncated if option set
        */
       public void testWriteTruncateStringToFileContentDesc()
       {
           File orig = new File("testdata", "test7.wma");
           if (!orig.isFile())
           {
               System.err.println("Unable to test file - not available");
               return;
           }

           Exception exceptionCaught = null;
           try
           {
               File testFile = AbstractTestCase.copyAudioToTmp("test7.wma");
               AudioFile f = AudioFileIO.read(testFile);
               Tag tag = f.getTag().or(NullTag.INSTANCE);

               //Enable value
               TagOptionSingleton.getInstance().setTruncateTextWithoutErrors(true);

               //Now createField artwork field
               StringBuffer sb = new StringBuffer();
               for (int i = 0; i < 34000;i++)
               {
                   sb.append("x");
               }
               tag.setField(FieldKey.TITLE,sb.toString());
               f.save();

           }
           catch (Exception e)
           {
               e.printStackTrace();
               exceptionCaught = e;
           }
           assertNull(exceptionCaught);
       }

}
