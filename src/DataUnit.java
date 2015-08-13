import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DataUnit implements Serializable{

	public int dataSize;
    byte [] data;

    public DataUnit(byte[] data, int size) {
        this.data = data;
        dataSize = size;
        if (size < data.length){
            byte[] reducedData = java.util.Arrays.copyOf(data, size);
            this.data=reducedData;
        }
    }

    public DataUnit() {
        super();
    }

    private static String sha1(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    
    public String getId(){
        try {
            return sha1(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
