package ealvatag.issues;

import ealvatag.AbstractTestCase;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.audio.mp4.Mp4AtomTree;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Test writing mp4
 */
public class Issue387Test extends AbstractTestCase
{
    public void testIssue() throws Exception
    {
        Exception caught = null;
        try
        {
            File orig = new File("testdata", "test100.mp4");
            if (!orig.isFile())
            {
                System.err.println("Unable to test file - not available");
                return;
            }

            File testFile = AbstractTestCase.copyAudioToTmp("test100.mp4");
            AudioFile af = AudioFileIO.read(testFile);
            System.out.println(af.getAudioHeader());
            af.getTagOrSetNewDefault();
            af.save();

            Mp4AtomTree atomTree = new Mp4AtomTree(new RandomAccessFile(testFile, "r"));
            atomTree.printAtomTree();

        }
        catch(Exception e)
        {
            caught=e;
            e.printStackTrace();
        }
        assertNull(caught);
    }
}
