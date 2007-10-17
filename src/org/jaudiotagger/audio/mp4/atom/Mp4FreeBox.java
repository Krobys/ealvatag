package org.jaudiotagger.audio.mp4.atom;

import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.audio.mp4.Mp4NotMetaFieldKey;

import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * FreeBox ( padding)
 *
 * <p>There are usually two free boxes, one beneath the meta atom and one toplevel atom
 */
public class Mp4FreeBox extends AbstractMp4Box
{
    /**
     * Construct a new FreeBox containing datasize padding (i.e doesnt include header size)
     *
     * @param datasize padding size
     */
    public Mp4FreeBox(int datasize)
    {
        try
        {
            //Header
            header = new Mp4BoxHeader();
            ByteArrayOutputStream headerBaos = new ByteArrayOutputStream();
            headerBaos.write(Utils.getSizeBigEndian( Mp4BoxHeader.HEADER_LENGTH + datasize));
            headerBaos.write(Utils.getDefaultBytes(Mp4NotMetaFieldKey.FREE.getFieldName()));
            header.update(ByteBuffer.wrap(headerBaos.toByteArray()));

            //Body
            ByteArrayOutputStream freeBaos = new ByteArrayOutputStream();
            for(int i=0;i<datasize;i++)
            {
                freeBaos.write(0x0);
            }
            dataBuffer = ByteBuffer.wrap(freeBaos.toByteArray());
        }
        catch(IOException ioe)
        {
            //This should never happen as were not actually writing to/from a file
            throw new RuntimeException(ioe);
        }
    }

}
