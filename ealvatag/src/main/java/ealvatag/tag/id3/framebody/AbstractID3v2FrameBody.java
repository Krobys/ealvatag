/**
 * @author : Paul Taylor
 * @author : Eric Farng
 * <p>
 * Version @version:$Id$
 * <p>
 * MusicTag Copyright (C)2003,2004
 * <p>
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public  License as
 * published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, you can get a copy from
 * http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301 USA
 * <p>
 * Description: Abstract Superclass of all Frame Bodys
 */
package ealvatag.tag.id3.framebody;

import ealvatag.audio.mp3.MP3File;
import ealvatag.tag.InvalidDataTypeException;
import ealvatag.tag.InvalidFrameException;
import ealvatag.tag.InvalidTagException;
import ealvatag.tag.datatype.AbstractDataType;
import ealvatag.tag.id3.AbstractTagFrameBody;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ealvatag.logging.ErrorMessage.INVALID_DATATYPE;
import static ealvatag.logging.ErrorMessage.exceptionMsg;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Contains the content for an ID3v2 frame, (the header is held directly within the frame
 */
@SuppressWarnings("Duplicates") public abstract class AbstractID3v2FrameBody extends AbstractTagFrameBody {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractID3v2FrameBody.class);

    protected static final String TYPE_BODY = "body";


    /**
     * Frame Body Size, originally this is size as indicated in frame header
     * when we come to writing data we recalculate it.
     */
    private int size;


    /**
     * Create Empty Body. Super Constructor sets up Object list
     */
    protected AbstractID3v2FrameBody() {
    }

    /**
     * Create Body based on another body
     *
     * @param copyObject
     */
    protected AbstractID3v2FrameBody(AbstractID3v2FrameBody copyObject) {
        super(copyObject);
    }

    /**
     * Creates a new FrameBody dataType from file. The super
     * Constructor sets up the Object list for the frame.
     *
     * @param byteBuffer from where to read the frame body from
     * @param frameSize
     *
     * @throws ealvatag.tag.InvalidTagException
     */
    protected AbstractID3v2FrameBody(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException {
        super();
        setSize(frameSize);
        this.read(byteBuffer);

    }

    protected AbstractID3v2FrameBody(Buffer buffer, int frameSize) throws InvalidTagException {
        super();
        setSize(frameSize);
        read(buffer);
    }

    /**
     * Return the ID3v2 Frame Identifier, must be implemented by concrete subclasses
     *
     * @return the frame identifier
     */
    public abstract String getIdentifier();


    /**
     * Return size of frame body,if frameBody already exist will take this value from the frame header
     * but it is always recalculated before writing any changes back to disk.
     *
     * @return size in bytes of this frame body
     */
    public int getSize() {
        return size;
    }

    /**
     * Set size based on size passed as parameter from frame header,
     * done before read
     *
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Set size based on size of the DataTypes making up the body,done after write
     */
    public void setSize() {
        size = 0;
        for (AbstractDataType object : objectList) {
            size += object.getSize();
        }
    }

    /**
     * Are two bodies equal
     *
     * @param obj
     */
    public boolean equals(Object obj) {
        return (obj instanceof AbstractID3v2FrameBody) && super.equals(obj);
    }

    /**
     * This reads a frame body from a ByteBuffer into the appropriate FrameBody class and update the position of the
     * buffer to be just after the end of this frameBody
     * <p>
     * The ByteBuffer represents the tag and its position should be at the start of this frameBody. The size as
     * indicated in the header is passed to the frame constructor when reading from file.
     *
     * @param byteBuffer file to read
     *
     * @throws InvalidFrameException if unable to construct a frameBody from the ByteBuffer
     */
    //TODO why don't we just slice byteBuffer, set limit to size and convert readByteArray to take a ByteBuffer
    //then we wouldn't have to temporary allocate space for the buffer, using lots of needless memory
    //and providing extra work for the garbage collector.
    public void read(ByteBuffer byteBuffer) throws InvalidTagException {
        int size = getSize();
        LOG.debug("Reading body for" + this.getIdentifier() + ":" + size);

        //Allocate a buffer to the size of the Frame Body and read from file
        byte[] buffer = new byte[size];
        byteBuffer.get(buffer);

        //Offset into buffer, incremented by length of previous dataType
        //this offset is only used internally to decide where to look for the next
        //dataType within a frameBody, it does not decide where to look for the next frame body
        int offset = 0;

        //Go through the ObjectList of the Frame reading the data into the
        for (AbstractDataType object : objectList) {
            //correct dataType.
            LOG.trace("offset:" + offset);

            //The read has extended further than the defined frame size (ok to extend upto
            //size because the next datatype may be of length 0.)
            if (offset > (size)) {
                LOG.warn("Invalid Size for FrameBody");
                throw new InvalidFrameException("Invalid size for Frame Body");
            }

            //Try and load it with data from the Buffer
            //if it fails frame is invalid
            try {
                object.readByteArray(buffer, offset);
            } catch (InvalidDataTypeException e) {
                LOG.warn("Problem reading datatype within Frame Body:" + e.getMessage());
                throw e;
            }
            //Increment Offset to start of next datatype.
            offset += object.getSize();
        }
    }

    public void read(Buffer buffer) throws InvalidTagException {
        int size = getSize();
        LOG.debug("Reading body for" + this.getIdentifier() + ":" + size);

        for (AbstractDataType object : objectList) {
            checkSize(size, object.getClass().getName());
            try {
                object.read(buffer, size);
            } catch (EOFException | ArrayIndexOutOfBoundsException e) {
                throw new InvalidTagException(exceptionMsg(INVALID_DATATYPE,
                                                           object.getClass(),
                                                           getIdentifier(),
                                                           e.getMessage()));
            }
            size -= object.getSize();
        }
        checkFinalSize(buffer, size);
    }

    /**
     * Log and skip if we didn't read enough, throw if we read too much
     *
     * @throws InvalidTagException if tag read too much data
     */
    @SuppressWarnings("unused")   // not using "buffer" right now because we aren't skipping. Let's leave until we can investigate
    private void checkFinalSize(final Buffer buffer, final int size) throws InvalidTagException {
        if (size > 0) {
            LOG.warn("Tag {} did not read it's entire expected size.", getIdentifier());
            // TODO: 1/26/17 I'm unsure why this could happen other than an error. However, skipping bytes causes problems for ensuing
            // frames. Why isn't size not exactly matching total frames size?
//            try {
//                buffer.skip(Math.min(size, buffer.size()));
//            } catch (EOFException e) {
//                // should never happen
//                LOG.error("EOF skipping unread tag data. ?");
//            }
        } else {
            checkSize(size, "Past last");
        }
    }

    private void checkSize(final int size, final String objectType) throws InvalidTagException {
        if (size < 0) {
            throw new InvalidTagException(exceptionMsg(INVALID_DATATYPE,
                                                       objectType,
                                                       getIdentifier(),
                                                       "Not enough data. Maybe previous data type read past it's size"));
        }
    }

    /**
     * Write the contents of this datatype to the byte array
     *
     * @param tagBuffer
     */
    public void write(ByteArrayOutputStream tagBuffer) {
        LOG.debug("Writing frame body for" + this.getIdentifier() + ":Est Size:" + size);
        //Write the various fields to file in order
        for (AbstractDataType object : objectList) {
            byte[] objectData = object.writeByteArray();
            if (objectData != null) {
                try {
                    tagBuffer.write(objectData);
                } catch (IOException ioe) {
                    //This could never happen coz not writing to file, so convert to RuntimeException
                    throw new RuntimeException(ioe);
                }
            }
        }
        setSize();
        LOG.debug("Written frame body for" + this.getIdentifier() + ":Real Size:" + size);

    }

    /**
     * Return String Representation of Datatype     *
     */
    public void createStructure() {
        MP3File.getStructureFormatter().openHeadingElement(TYPE_BODY, "");
        for (AbstractDataType nextObject : objectList) {
            nextObject.createStructure();
        }
        MP3File.getStructureFormatter().closeHeadingElement(TYPE_BODY);
    }
}