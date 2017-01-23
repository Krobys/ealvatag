package ealvatag.tag.flac;

import ealvatag.AbstractTestCase;
import ealvatag.FilePermissionsTest;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.audio.flac.FlacInfoReader;
import ealvatag.audio.flac.metadatablock.MetadataBlockDataPicture;
import ealvatag.tag.FieldKey;
import ealvatag.tag.NullTag;
import ealvatag.tag.TagField;
import ealvatag.tag.TagOptionSingleton;
import ealvatag.tag.id3.valuepair.ImageFormats;
import ealvatag.tag.reference.PictureTypes;
import junit.framework.TestCase;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * basic Flac tests
 */
public class FlacWriteTest
        extends TestCase {

    private static final String ALBUM_ARTIST = "AnAlbumArtist";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TagOptionSingleton.getInstance().setToDefault();
    }

    /**
     * Write flac info to file
     */
    public void testWriteAllFieldsToFile() {
        Exception exceptionCaught = null;
        try {
            //Put artifically low just to test it out
            TagOptionSingleton.getInstance().setWriteChunkSize(40000);
            File testFile = AbstractTestCase.copyAudioToTmp("test2.flac", new File("test2write.flac"));
            AudioFile f = AudioFileIO.read(testFile);

            assertEquals("192", f.getAudioHeader().getBitRate());
            assertEquals("FLAC 16 bits", f.getAudioHeader().getEncodingType());
            assertEquals("2", f.getAudioHeader().getChannels());
            assertEquals("44100", f.getAudioHeader().getSampleRate());

            assertTrue(f.getTag().or(NullTag.INSTANCE) instanceof FlacTag);
            FlacTag tag = (FlacTag)f.getTag().or(NullTag.INSTANCE);
            assertEquals("reference libFLAC 1.1.4 20070213", tag.getFirst(FieldKey.ENCODER));
            assertEquals("reference libFLAC 1.1.4 20070213", tag.getVorbisCommentTag().getVendor());
            //No Images
            assertEquals(0, tag.getImages().size());
            FlacInfoReader infoReader = new FlacInfoReader();
            assertEquals(4, infoReader.countMetaBlocks(f.getFile()));

            tag.addField(FieldKey.ARTIST, "artist\u01ff");
            tag.addField(FieldKey.ALBUM, "album");
            tag.addField(FieldKey.TITLE, "title");
            tag.addField(FieldKey.ALBUM_ARTIST, ALBUM_ARTIST);
            assertEquals(1, tag.getFields(FieldKey.TITLE.name()).size());
            tag.addField(FieldKey.YEAR, "1971");
            assertEquals(1, tag.getFields(FieldKey.YEAR).size());
            tag.addField(FieldKey.TRACK, "2");
            tag.addField(FieldKey.GENRE, "Rock");


            tag.setField(tag.createField(FieldKey.BPM, "123"));
            tag.setField(tag.createField(FieldKey.URL_LYRICS_SITE, "http://www.lyrics.fly.com"));
            tag.setField(tag.createField(FieldKey.URL_DISCOGS_ARTIST_SITE, "http://www.discogs1.com"));
            tag.setField(tag.createField(FieldKey.URL_DISCOGS_RELEASE_SITE, "http://www.discogs2.com"));
            tag.setField(tag.createField(FieldKey.URL_OFFICIAL_ARTIST_SITE, "http://www.discogs3.com"));
            tag.setField(tag.createField(FieldKey.URL_OFFICIAL_RELEASE_SITE, "http://www.discogs4.com"));
            tag.setField(tag.createField(FieldKey.URL_WIKIPEDIA_ARTIST_SITE, "http://www.discogs5.com"));
            tag.setField(tag.createField(FieldKey.URL_WIKIPEDIA_RELEASE_SITE, "http://www.discogs6.com"));
            tag.setField(tag.createField(FieldKey.TRACK_TOTAL, "11"));
            tag.setField(tag.createField(FieldKey.DISC_TOTAL, "3"));
            //key not known to ealvatag
            tag.setField("VIOLINIST", "Sarah Curtis");

            //Add new image
            RandomAccessFile imageFile = new RandomAccessFile(new File("testdata", "coverart.png"), "r");
            byte[] imagedata = new byte[(int)imageFile.length()];
            imageFile.read(imagedata);
            tag.setField(tag.createArtworkField(imagedata,
                                                PictureTypes.DEFAULT_ID,
                                                ImageFormats.MIME_TYPE_PNG,
                                                "test",
                                                200,
                                                200,
                                                24,
                                                0));

            assertEquals("11", tag.getFirst(FieldKey.TRACK_TOTAL));
            assertEquals("3", tag.getFirst(FieldKey.DISC_TOTAL));


            f.save();
            f = AudioFileIO.read(testFile);
            assertEquals(5, infoReader.countMetaBlocks(f.getFile()));
            assertTrue(f.getTag().or(NullTag.INSTANCE) instanceof FlacTag);

            assertEquals("reference libFLAC 1.1.4 20070213", tag.getFirst(FieldKey.ENCODER));
            assertEquals("reference libFLAC 1.1.4 20070213", tag.getVorbisCommentTag().getVendor());
            tag.addField(tag.createField(FieldKey.ENCODER, "encoder"));
            assertEquals("encoder", tag.getFirst(FieldKey.ENCODER));


            tag = (FlacTag)f.getTag().or(NullTag.INSTANCE);
            assertEquals("artist\u01ff", tag.getFirst(FieldKey.ARTIST));
            assertEquals("album", tag.getFirst(FieldKey.ALBUM));
            assertEquals(ALBUM_ARTIST, tag.getFirst(FieldKey.ALBUM_ARTIST));
            assertEquals("title", tag.getFirst(FieldKey.TITLE));
            assertEquals("123", tag.getFirst(FieldKey.BPM));
            assertEquals("1971", tag.getFirst(FieldKey.YEAR));
            assertEquals("2", tag.getFirst(FieldKey.TRACK));
            assertEquals("Rock", tag.getFirst(FieldKey.GENRE));
            assertEquals(1, tag.getFields(FieldKey.GENRE).size());
            assertEquals(1, tag.getFields(FieldKey.ARTIST).size());
            assertEquals(1, tag.getFields(FieldKey.ALBUM).size());
            assertEquals(1, tag.getFields(FieldKey.TITLE).size());
            assertEquals(1, tag.getFields(FieldKey.BPM).size());
            assertEquals(1, tag.getFields(FieldKey.YEAR).size());
            assertEquals(1, tag.getFields(FieldKey.TRACK).size());
            //One Image
            assertEquals(1, tag.getFields(FieldKey.COVER_ART.name()).size());
            assertEquals(1, tag.getImages().size());
            MetadataBlockDataPicture pic = tag.getImages().get(0);
            assertEquals((int)PictureTypes.DEFAULT_ID, pic.getPictureType());
            assertEquals(ImageFormats.MIME_TYPE_PNG, pic.getMimeType());
            assertEquals("test", pic.getDescription());
            assertEquals(200, pic.getWidth());
            assertEquals(200, pic.getHeight());
            assertEquals(24, pic.getColourDepth());
            assertEquals(0, pic.getIndexedColourCount());

            assertEquals("http://www.lyrics.fly.com", tag.getFirst(FieldKey.URL_LYRICS_SITE));
            assertEquals("http://www.discogs1.com", tag.getFirst(FieldKey.URL_DISCOGS_ARTIST_SITE));
            assertEquals("http://www.discogs2.com", tag.getFirst(FieldKey.URL_DISCOGS_RELEASE_SITE));
            assertEquals("http://www.discogs3.com", tag.getFirst(FieldKey.URL_OFFICIAL_ARTIST_SITE));
            assertEquals("http://www.discogs4.com", tag.getFirst(FieldKey.URL_OFFICIAL_RELEASE_SITE));
            assertEquals("http://www.discogs5.com", tag.getFirst(FieldKey.URL_WIKIPEDIA_ARTIST_SITE));
            assertEquals("http://www.discogs6.com", tag.getFirst(FieldKey.URL_WIKIPEDIA_RELEASE_SITE));
            assertEquals("11", tag.getFirst(FieldKey.TRACK_TOTAL));
            assertEquals("3", tag.getFirst(FieldKey.DISC_TOTAL));
            assertEquals("Sarah Curtis", tag.getFirst("VIOLINIST"));

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);


    }

    public void testWriteAllFieldsToFileSmallChunkSize() {
        Exception exceptionCaught = null;
        try {
            //Put artifically low just to test it out
            TagOptionSingleton.getInstance().setWriteChunkSize(1000);
            File testFile = AbstractTestCase.copyAudioToTmp("test2.flac", new File("test2write.flac"));
            AudioFile f = AudioFileIO.read(testFile);

            assertEquals("192", f.getAudioHeader().getBitRate());
            assertEquals("FLAC 16 bits", f.getAudioHeader().getEncodingType());
            assertEquals("2", f.getAudioHeader().getChannels());
            assertEquals("44100", f.getAudioHeader().getSampleRate());

            assertTrue(f.getTag().or(NullTag.INSTANCE) instanceof FlacTag);
            FlacTag tag = (FlacTag)f.getTag().or(NullTag.INSTANCE);
            assertEquals("reference libFLAC 1.1.4 20070213", tag.getFirst(FieldKey.ENCODER));
            assertEquals("reference libFLAC 1.1.4 20070213", tag.getVorbisCommentTag().getVendor());
            //No Images
            assertEquals(0, tag.getImages().size());
            FlacInfoReader infoReader = new FlacInfoReader();
            assertEquals(4, infoReader.countMetaBlocks(f.getFile()));

            tag.addField(FieldKey.ARTIST, "artist\u01ff");
            tag.addField(FieldKey.ALBUM, "album");
            tag.addField(FieldKey.ALBUM_ARTIST, ALBUM_ARTIST);
            tag.addField(FieldKey.TITLE, "title");
            assertEquals(1, tag.getFields(FieldKey.TITLE.name()).size());
            tag.addField(FieldKey.YEAR, "1971");
            assertEquals(1, tag.getFields(FieldKey.YEAR).size());
            tag.addField(FieldKey.TRACK, "2");
            tag.addField(FieldKey.GENRE, "Rock");


            tag.setField(tag.createField(FieldKey.BPM, "123"));
            tag.setField(tag.createField(FieldKey.URL_LYRICS_SITE, "http://www.lyrics.fly.com"));
            tag.setField(tag.createField(FieldKey.URL_DISCOGS_ARTIST_SITE, "http://www.discogs1.com"));
            tag.setField(tag.createField(FieldKey.URL_DISCOGS_RELEASE_SITE, "http://www.discogs2.com"));
            tag.setField(tag.createField(FieldKey.URL_OFFICIAL_ARTIST_SITE, "http://www.discogs3.com"));
            tag.setField(tag.createField(FieldKey.URL_OFFICIAL_RELEASE_SITE, "http://www.discogs4.com"));
            tag.setField(tag.createField(FieldKey.URL_WIKIPEDIA_ARTIST_SITE, "http://www.discogs5.com"));
            tag.setField(tag.createField(FieldKey.URL_WIKIPEDIA_RELEASE_SITE, "http://www.discogs6.com"));
            tag.setField(tag.createField(FieldKey.TRACK_TOTAL, "11"));
            tag.setField(tag.createField(FieldKey.DISC_TOTAL, "3"));
            //key not known to ealvatag
            tag.setField("VIOLINIST", "Sarah Curtis");

            //Add new image
            RandomAccessFile imageFile = new RandomAccessFile(new File("testdata", "coverart.png"), "r");
            byte[] imagedata = new byte[(int)imageFile.length()];
            imageFile.read(imagedata);
            tag.setField(tag.createArtworkField(imagedata,
                                                PictureTypes.DEFAULT_ID,
                                                ImageFormats.MIME_TYPE_PNG,
                                                "test",
                                                200,
                                                200,
                                                24,
                                                0));

            assertEquals("11", tag.getFirst(FieldKey.TRACK_TOTAL));
            assertEquals("3", tag.getFirst(FieldKey.DISC_TOTAL));


            f.save();
            f = AudioFileIO.read(testFile);
            assertEquals(5, infoReader.countMetaBlocks(f.getFile()));
            assertTrue(f.getTag().or(NullTag.INSTANCE) instanceof FlacTag);

            assertEquals("reference libFLAC 1.1.4 20070213", tag.getFirst(FieldKey.ENCODER));
            assertEquals("reference libFLAC 1.1.4 20070213", tag.getVorbisCommentTag().getVendor());
            tag.addField(tag.createField(FieldKey.ENCODER, "encoder"));
            assertEquals("encoder", tag.getFirst(FieldKey.ENCODER));


            tag = (FlacTag)f.getTag().or(NullTag.INSTANCE);
            assertEquals("artist\u01ff", tag.getFirst(FieldKey.ARTIST));
            assertEquals("album", tag.getFirst(FieldKey.ALBUM));
            assertEquals(ALBUM_ARTIST, tag.getFirst(FieldKey.ALBUM_ARTIST));
            assertEquals("title", tag.getFirst(FieldKey.TITLE));
            assertEquals("123", tag.getFirst(FieldKey.BPM));
            assertEquals("1971", tag.getFirst(FieldKey.YEAR));
            assertEquals("2", tag.getFirst(FieldKey.TRACK));
            assertEquals("Rock", tag.getFirst(FieldKey.GENRE));
            assertEquals(1, tag.getFields(FieldKey.GENRE).size());
            assertEquals(1, tag.getFields(FieldKey.ARTIST).size());
            assertEquals(1, tag.getFields(FieldKey.ALBUM).size());
            assertEquals(1, tag.getFields(FieldKey.TITLE).size());
            assertEquals(1, tag.getFields(FieldKey.BPM).size());
            assertEquals(1, tag.getFields(FieldKey.YEAR).size());
            assertEquals(1, tag.getFields(FieldKey.TRACK).size());
            //One Image
            assertEquals(1, tag.getFields(FieldKey.COVER_ART.name()).size());
            assertEquals(1, tag.getImages().size());
            MetadataBlockDataPicture pic = tag.getImages().get(0);
            assertEquals((int)PictureTypes.DEFAULT_ID, pic.getPictureType());
            assertEquals(ImageFormats.MIME_TYPE_PNG, pic.getMimeType());
            assertEquals("test", pic.getDescription());
            assertEquals(200, pic.getWidth());
            assertEquals(200, pic.getHeight());
            assertEquals(24, pic.getColourDepth());
            assertEquals(0, pic.getIndexedColourCount());

            assertEquals("http://www.lyrics.fly.com", tag.getFirst(FieldKey.URL_LYRICS_SITE));
            assertEquals("http://www.discogs1.com", tag.getFirst(FieldKey.URL_DISCOGS_ARTIST_SITE));
            assertEquals("http://www.discogs2.com", tag.getFirst(FieldKey.URL_DISCOGS_RELEASE_SITE));
            assertEquals("http://www.discogs3.com", tag.getFirst(FieldKey.URL_OFFICIAL_ARTIST_SITE));
            assertEquals("http://www.discogs4.com", tag.getFirst(FieldKey.URL_OFFICIAL_RELEASE_SITE));
            assertEquals("http://www.discogs5.com", tag.getFirst(FieldKey.URL_WIKIPEDIA_ARTIST_SITE));
            assertEquals("http://www.discogs6.com", tag.getFirst(FieldKey.URL_WIKIPEDIA_RELEASE_SITE));
            assertEquals("11", tag.getFirst(FieldKey.TRACK_TOTAL));
            assertEquals("3", tag.getFirst(FieldKey.DISC_TOTAL));
            assertEquals("Sarah Curtis", tag.getFirst("VIOLINIST"));

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);


    }

    /**
     * Test deleting tag file
     */
    public void testDeleteTagFile() throws Exception {
        File testFile = AbstractTestCase.copyAudioToTmp("test.flac", new File("testdeletetag.flac"));
        AudioFile f = AudioFileIO.read(testFile);

        assertEquals("192", f.getAudioHeader().getBitRate());
        assertEquals("FLAC 16 bits", f.getAudioHeader().getEncodingType());
        assertEquals("2", f.getAudioHeader().getChannels());
        assertEquals("44100", f.getAudioHeader().getSampleRate());
        assertEquals(2, ((FlacTag)f.getTag().or(NullTag.INSTANCE)).getImages().size());
        assertTrue(f.getTag().or(NullTag.INSTANCE) instanceof FlacTag);
        assertFalse(f.getTag().or(NullTag.INSTANCE).isEmpty());

        f.deleteFileTag();
        f = AudioFileIO.read(testFile);
        assertTrue(f.getTag().or(NullTag.INSTANCE).isEmpty());
    }


    /**
     * Test Writing file that contains cuesheet
     */
    public void testWriteFileWithCueSheet() throws Exception {
        File testFile = AbstractTestCase.copyAudioToTmp("test3.flac", new File("testWriteWithCueSheet.flac"));
        AudioFile f = AudioFileIO.read(testFile);
        FlacInfoReader infoReader = new FlacInfoReader();
        assertEquals(5, infoReader.countMetaBlocks(f.getFile()));
        f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ALBUM, "BLOCK");
        f.save();
        f = AudioFileIO.read(testFile);
        assertEquals("BLOCK", f.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ALBUM));
    }

    /**
     * Test writing to file that contains an ID3 header
     */
    public void testWriteFileWithId3Header() throws Exception {
        File testFile = AbstractTestCase.copyAudioToTmp("test2.flac", new File("testWriteFlacWithId3.flac"));
        AudioFile f = AudioFileIO.read(testFile);
        FlacInfoReader infoReader = new FlacInfoReader();
        assertEquals(4, infoReader.countMetaBlocks(f.getFile()));
        f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ALBUM, "BLOCK");
        f.save();
        f = AudioFileIO.read(testFile);
        infoReader = new FlacInfoReader();
        assertEquals(4, infoReader.countMetaBlocks(f.getFile()));
        assertEquals("BLOCK", f.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ALBUM));

    }

    /**
     * Metadata size has increased so that shift required
     */
    public void testWriteFileWithId3HeaderAudioShifted() throws Exception {
        File testFile =
                AbstractTestCase.copyAudioToTmp("test2.flac", new File("testWriteFlacWithId3Shifted.flac"));
        AudioFile f = AudioFileIO.read(testFile);

        assertEquals("192", f.getAudioHeader().getBitRate());
        assertEquals("FLAC 16 bits", f.getAudioHeader().getEncodingType());
        assertEquals("2", f.getAudioHeader().getChannels());
        assertEquals("44100", f.getAudioHeader().getSampleRate());

        assertTrue(f.getTag().or(NullTag.INSTANCE) instanceof FlacTag);
        FlacTag tag = (FlacTag)f.getTag().or(NullTag.INSTANCE);
        assertEquals("reference libFLAC 1.1.4 20070213", tag.getFirst(FieldKey.ENCODER));
        assertEquals("reference libFLAC 1.1.4 20070213", tag.getVorbisCommentTag().getVendor());

        //No Images
        assertEquals(0, tag.getImages().size());
        FlacInfoReader infoReader = new FlacInfoReader();
        assertEquals(4, infoReader.countMetaBlocks(f.getFile()));

        tag.setField(FieldKey.ARTIST, "BLOCK");
        tag.addField(FieldKey.ALBUM, "album");
        tag.addField(FieldKey.TITLE, "title");
        tag.addField(FieldKey.YEAR, "1971");
        tag.addField(FieldKey.TRACK, "2");
        tag.addField(FieldKey.GENRE, "Rock");
        tag.setField(tag.createField(FieldKey.BPM, "123"));

        //Add new image
        RandomAccessFile imageFile = new RandomAccessFile(new File("testdata", "coverart.png"), "r");
        byte[] imagedata = new byte[(int)imageFile.length()];
        imageFile.read(imagedata);
        tag.setField(tag.createArtworkField(imagedata,
                                            PictureTypes.DEFAULT_ID,
                                            ImageFormats.MIME_TYPE_PNG,
                                            "test",
                                            200,
                                            200,
                                            24,
                                            0));
        f.save();
        f = AudioFileIO.read(testFile);
        assertEquals(5, infoReader.countMetaBlocks(f.getFile()));
        assertTrue(f.getTag().or(NullTag.INSTANCE) instanceof FlacTag);
        assertEquals("reference libFLAC 1.1.4 20070213", tag.getFirst(FieldKey.ENCODER));
        assertEquals("reference libFLAC 1.1.4 20070213", tag.getVorbisCommentTag().getVendor());
        tag = (FlacTag)f.getTag().or(NullTag.INSTANCE);
        assertEquals("BLOCK", tag.getFirst(FieldKey.ARTIST));
        assertEquals(1, tag.getArtworkList().size());
    }

    public void testDeleteTag() throws Exception {
        File testFile = AbstractTestCase.copyAudioToTmp("test2.flac", new File("testDelete.flac"));
        AudioFile f = AudioFileIO.read(testFile);
        f.deleteFileTag();

        f = AudioFileIO.read(testFile);
        assertTrue(f.getTag().or(NullTag.INSTANCE).isEmpty());
    }

    public void testWriteMultipleFields() throws Exception {
        File testFile = AbstractTestCase.copyAudioToTmp("test.flac", new File("testWriteMultiple.flac"));
        AudioFile f = AudioFileIO.read(testFile);
        List<TagField> tagFields = f.getTag().or(NullTag.INSTANCE).getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(0, tagFields.size());
        f.getTag().or(NullTag.INSTANCE).addField(FieldKey.ALBUM_ARTIST_SORT, "artist1");
        f.getTag().or(NullTag.INSTANCE).addField(FieldKey.ALBUM_ARTIST_SORT, "artist2");
        tagFields = f.getTag().or(NullTag.INSTANCE).getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(2, tagFields.size());
        f.save();
        f = AudioFileIO.read(testFile);
        tagFields = f.getTag().or(NullTag.INSTANCE).getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(2, tagFields.size());
    }

    public void testDeleteFields() throws Exception {
        //Delete using generic key
        File testFile = AbstractTestCase.copyAudioToTmp("test.flac", new File("testWriteMultiple.flac"));
        AudioFile f = AudioFileIO.read(testFile);
        List<TagField> tagFields = f.getTag().or(NullTag.INSTANCE).getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(0, tagFields.size());
        f.getTag().or(NullTag.INSTANCE).addField(FieldKey.ALBUM_ARTIST_SORT, "artist1");
        f.getTag().or(NullTag.INSTANCE).addField(FieldKey.ALBUM_ARTIST_SORT, "artist2");
        tagFields = f.getTag().or(NullTag.INSTANCE).getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(2, tagFields.size());
        f.getTag().or(NullTag.INSTANCE).deleteField(FieldKey.ALBUM_ARTIST_SORT);
        f.save();

        //Delete using flac id
        f = AudioFileIO.read(testFile);
        tagFields = f.getTag().or(NullTag.INSTANCE).getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(0, tagFields.size());
        f.getTag().or(NullTag.INSTANCE).addField(FieldKey.ALBUM_ARTIST_SORT, "artist1");
        f.getTag().or(NullTag.INSTANCE).addField(FieldKey.ALBUM_ARTIST_SORT, "artist2");
        tagFields = f.getTag().or(NullTag.INSTANCE).getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(2, tagFields.size());
        f.getTag().or(NullTag.INSTANCE).deleteField("ALBUMARTISTSORT");
        tagFields = f.getTag().or(NullTag.INSTANCE).getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(0, tagFields.size());
        f.save();

        f = AudioFileIO.read(testFile);
        tagFields = f.getTag().or(NullTag.INSTANCE).getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(0, tagFields.size());

    }

    // test file test102.flac missing
//    /**
//     * test read flac file with just streaminfo and padding header
//     */
//    public void testWriteFileThatOnlyHadStreamAndPaddingInfoHeader() throws Exception {
//        File testFile = AbstractTestCase.copyAudioToTmp("test102.flac", new File("test102.flac"));
//        AudioFile f = AudioFileIO.read(testFile);
//        FlacInfoReader infoReader = new FlacInfoReader();
//        assertEquals(2, infoReader.countMetaBlocks(f.getFile()));
//        f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
//        f.commit();
//
//        f = AudioFileIO.read(testFile);
//
//        infoReader = new FlacInfoReader();
//        assertEquals(3, infoReader.countMetaBlocks(f.getFile()));
//        assertEquals("fred", f.getTag().or(NullTag.INSTANCE).getFirst(FieldKey.ARTIST));
//    }

    public void testWriteWriteProtectedFileWithCheckDisabled() throws Exception {

        FilePermissionsTest.runWriteWriteProtectedFileWithCheckDisabled("test2.flac");
    }

    public void testWriteWriteProtectedFileWithCheckEnabled() throws Exception {

        FilePermissionsTest.runWriteWriteProtectedFileWithCheckEnabled("test2.flac");
    }

    public void testWriteReadOnlyFileWithCheckDisabled() throws Exception {

        FilePermissionsTest.runWriteReadOnlyFileWithCheckDisabled("test2.flac");
    }


}
