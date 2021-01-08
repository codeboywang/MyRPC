package RPC.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SerialUtil {
    static ByteArrayOutputStream out = new ByteArrayOutputStream();
    /**
     * Get Object's serialized bytes
     * @param obj An object to be serialized.
     * @return byte[] of obj
     */
    public synchronized static byte[] serailizeObj(Object obj){
        // reset out
        out.reset();
        ObjectOutputStream oout = null;
        byte[] objBytes = null;
        try {
            oout = new ObjectOutputStream(out);
            oout.writeObject(obj);
            objBytes= out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objBytes;
    }
}
