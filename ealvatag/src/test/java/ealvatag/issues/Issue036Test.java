package ealvatag.issues;

import ealvatag.tag.FieldKey;
import ealvatag.tag.id3.ID3v22Frame;
import ealvatag.tag.id3.ID3v22Frames;
import ealvatag.tag.id3.ID3v22Tag;
import ealvatag.tag.id3.ID3v23Frame;
import ealvatag.tag.id3.ID3v23Frames;
import ealvatag.tag.id3.ID3v23Tag;
import ealvatag.tag.id3.ID3v24Frame;
import ealvatag.tag.id3.ID3v24Frames;
import ealvatag.tag.id3.ID3v24Tag;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test frame and Tag Equality
 */
public class Issue036Test {
    @Test public void testIDv24Frame() throws Exception {
        ID3v24Frame frame1 = new ID3v24Frame();
        ID3v24Frame frame2 = new ID3v24Frame();
        ID3v24Frame frame3 = new ID3v24Frame("TPE1");
        ID3v24Frame frame4 = new ID3v24Frame("TPE1");
        ID3v24Frame frame5 = new ID3v24Frame("TPE1");
        frame5.getBody().setTextEncoding((byte)1);

        Assert.assertTrue(frame1.equals(frame1));
        Assert.assertTrue(frame1.equals(frame2));
        Assert.assertFalse(frame1.equals(frame3));

        Assert.assertTrue(frame3.equals(frame3));
        Assert.assertTrue(frame3.equals(frame4));
        Assert.assertFalse(frame3.equals(frame5));
    }

    @Test public void testAllID3v24Frames() throws Exception {
        for (String frameId : ID3v24Frames.getInstanceOf().getSupportedFrames()) {
            ID3v24Frame frame1 = new ID3v24Frame(frameId);
            ID3v24Frame frame2 = new ID3v24Frame(frameId);
            Assert.assertTrue(frame1.equals(frame2));
        }
    }

    @Test public void testIDv24Tag() throws Exception {
        ID3v24Tag tag1 = new ID3v24Tag();
        ID3v24Tag tag2 = new ID3v24Tag();
        ID3v24Tag tag3 = new ID3v24Tag();
        ID3v24Tag tag4 = new ID3v24Tag();
        ID3v24Tag tag5 = new ID3v24Tag();
        tag3.addField(FieldKey.ALBUM, "Porcupine");
        tag4.addField(FieldKey.ALBUM, "Porcupine");
        tag5.addField(FieldKey.ALBUM, "Porcupine");
        tag5.addField(FieldKey.ARTIST, "Echo & the Bunnymen");

        Assert.assertEquals(tag1, tag1);
        Assert.assertEquals(tag1, tag2);
        Assert.assertNotEquals(tag1, tag3);

        Assert.assertEquals(tag3, tag3);
        Assert.assertEquals(tag3, tag4);
        Assert.assertNotEquals(tag3, tag5);
    }


    @Test public void testIDv23Frame() throws Exception {
        ID3v23Frame frame1 = new ID3v23Frame();
        ID3v23Frame frame2 = new ID3v23Frame();
        ID3v23Frame frame3 = new ID3v23Frame("TPE1");
        ID3v23Frame frame4 = new ID3v23Frame("TPE1");
        ID3v23Frame frame5 = new ID3v23Frame("TPE1");
        frame5.getBody().setTextEncoding((byte)1);

        Assert.assertEquals(frame1, frame1);
        Assert.assertEquals(frame1, frame2);
        Assert.assertNotEquals(frame1, frame3);

        Assert.assertEquals(frame3, frame3);
        Assert.assertEquals(frame3, frame4);
        Assert.assertNotEquals(frame3, frame5);
    }

    @Test public void testAllID3v23Frames() throws Exception {
        for (String frameId : ID3v23Frames.getInstanceOf().getSupportedFrames()) {
            ID3v23Frame frame1 = new ID3v23Frame(frameId);
            ID3v23Frame frame2 = new ID3v23Frame(frameId);
            Assert.assertTrue(frame1.equals(frame2));
        }
    }

    @Test public void testIDv23Tag() throws Exception {
        ID3v23Tag tag1 = new ID3v23Tag();
        ID3v23Tag tag2 = new ID3v23Tag();
        ID3v23Tag tag3 = new ID3v23Tag();
        ID3v23Tag tag4 = new ID3v23Tag();
        ID3v23Tag tag5 = new ID3v23Tag();
        tag3.addField(FieldKey.ALBUM, "Porcupine");
        tag4.addField(FieldKey.ALBUM, "Porcupine");
        tag5.addField(FieldKey.ALBUM, "Porcupine");
        tag5.addField(FieldKey.ARTIST, "Echo & the Bunnymen");

        Assert.assertTrue(tag1.equals(tag1));
        Assert.assertTrue(tag1.equals(tag2));
        Assert.assertFalse(tag1.equals(tag3));

        Assert.assertTrue(tag3.equals(tag3));
        Assert.assertTrue(tag3.equals(tag4));
        Assert.assertFalse(tag3.equals(tag5));
    }

    @Test public void testIDv22Frame() throws Exception {
        ID3v22Frame frame1 = new ID3v22Frame();
        ID3v22Frame frame2 = new ID3v22Frame();
        ID3v22Frame frame3 = new ID3v22Frame("TP1");
        ID3v22Frame frame4 = new ID3v22Frame("TP1");
        ID3v22Frame frame5 = new ID3v22Frame("TP1");
        frame5.getBody().setTextEncoding((byte)1);

        Assert.assertTrue(frame1.equals(frame1));
        Assert.assertTrue(frame1.equals(frame2));
        Assert.assertFalse(frame1.equals(frame3));

        Assert.assertTrue(frame3.equals(frame3));
        Assert.assertTrue(frame3.equals(frame4));
        Assert.assertFalse(frame3.equals(frame5));
    }

    @Test public void testAllID3v22Frames() throws Exception {
        for (String frameId : ID3v22Frames.getInstanceOf().getSupportedFrames()) {
            ID3v22Frame frame1 = new ID3v22Frame(frameId);
            ID3v22Frame frame2 = new ID3v22Frame(frameId);
            Assert.assertTrue(frame1.equals(frame2));
        }
    }

    @Test public void testIDv22Tag() throws Exception {
        ID3v22Tag tag1 = new ID3v22Tag();
        ID3v22Tag tag2 = new ID3v22Tag();
        ID3v22Tag tag3 = new ID3v22Tag();
        ID3v22Tag tag4 = new ID3v22Tag();
        ID3v22Tag tag5 = new ID3v22Tag();
        tag3.addField(FieldKey.ALBUM, "Porcupine");
        tag4.addField(FieldKey.ALBUM, "Porcupine");
        tag5.addField(FieldKey.ALBUM, "Porcupine");
        tag5.addField(FieldKey.ARTIST, "Echo & the Bunnymen");

        Assert.assertTrue(tag1.equals(tag2));
        Assert.assertFalse(tag1.equals(tag3));

        Assert.assertTrue(tag3.equals(tag4));
        Assert.assertFalse(tag3.equals(tag5));
    }
}
