/**
 *  Amended @author : Paul Taylor
 *  Initial @author : Eric Farng
 *
 *  Version @version:$Id$
 *
 *  MusicTag Copyright (C)2003,2004
 *
 *  This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 *  or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 *  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 *  you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Description:
 *
 */
package org.jaudiotagger.tag.datatype;

import org.jaudiotagger.audio.mp3.*;
import org.jaudiotagger.tag.AbstractTagFrameBody;

public class Lyrics3TimeStamp extends AbstractDataType
{
    /**
     * DOCUMENT ME!
     */
    private long minute = 0;

    /**
     * DOCUMENT ME!
     */
    private long second = 0;

    /**
     * Todo this is wrong
     */
    public void readString(String s)
    {
    }

    /**
     * Creates a new ObjectLyrics3TimeStamp datatype.
     *
     * @param identifier DOCUMENT ME!
     */
    public Lyrics3TimeStamp(String identifier, AbstractTagFrameBody frameBody)
    {
        super(identifier, frameBody);
    }

    public Lyrics3TimeStamp(String identifier)
    {
        super(identifier, null);
    }

    public Lyrics3TimeStamp(Lyrics3TimeStamp copy)
    {
        super(copy);
        this.minute = copy.minute;
        this.second = copy.second;
    }

    public void setMinute(long minute)
    {
        this.minute = minute;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public long getMinute()
    {
        return minute;
    }

    public void setSecond(long second)
    {
        this.second = second;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public long getSecond()
    {
        return second;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getSize()
    {
        return 7;
    }

    /**
     * Creates a new ObjectLyrics3TimeStamp datatype.
     *
     * @param timeStamp       DOCUMENT ME!
     * @param timeStampFormat DOCUMENT ME!
     */
    public void setTimeStamp(long timeStamp, byte timeStampFormat)
    {
        /**
         * @todo convert both types of formats
         */
        timeStamp = timeStamp / 1000;
        minute = timeStamp / 60;
        second = timeStamp % 60;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public boolean equals(Object obj)
    {
        if ((obj instanceof Lyrics3TimeStamp) == false)
        {
            return false;
        }

        Lyrics3TimeStamp object = (Lyrics3TimeStamp) obj;

        if (this.minute != object.minute)
        {
            return false;
        }

        if (this.second != object.second)
        {
            return false;
        }

        return super.equals(obj);
    }

    /**
     * DOCUMENT ME!
     *
     * @param timeStamp DOCUMENT ME!
     * @param offset    DOCUMENT ME!
     * @throws NullPointerException      DOCUMENT ME!
     * @throws IndexOutOfBoundsException DOCUMENT ME!
     */
    public void readString(String timeStamp, int offset)
    {
        if (timeStamp == null)
        {
            throw new NullPointerException("Image is null");
        }

        if ((offset < 0) || (offset >= timeStamp.length()))
        {
            throw new IndexOutOfBoundsException("Offset to timeStamp is out of bounds: offset = " + offset + ", timeStamp.length()" + timeStamp.length());
        }

        timeStamp = timeStamp.substring(offset);

        if (timeStamp.length() == 7)
        {
            minute = Integer.parseInt(timeStamp.substring(1, 3));
            second = Integer.parseInt(timeStamp.substring(4, 6));
        }
        else
        {
            minute = 0;
            second = 0;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString()
    {
        return writeString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String writeString()
    {
        String str;
        str = "[";

        if (minute < 0)
        {
            str += "00";
        }
        else
        {
            if (minute < 10)
            {
                str += '0';
            }

            str += Long.toString(minute);
        }

        str += ':';

        if (second < 0)
        {
            str += "00";
        }
        else
        {
            if (second < 10)
            {
                str += '0';
            }

            str += Long.toString(second);
        }

        str += ']';

        return str;
    }

    public void readByteArray(byte[] arr, int offset)
    {
        readString(arr.toString(), offset);
    }

    public byte[] writeByteArray()
    {
        return writeString().getBytes();
    }

}
