package actionsim.chord;

import java.nio.ByteBuffer;

import org.apache.commons.codec.digest.DigestUtils;

public class ChordId implements Comparable<ChordId> {

	public static final ChordId MAX = fill(0xFF);
	public static final ChordId MIN = fill(0x00);
	
	private static ChordId fill(int byteVal) {
		
		byte[] fillBytes = new byte[ChordConfiguration.chordIdBytes];
		
		for(int i=0; i < fillBytes.length; i++) {
			
			fillBytes[i] = (byte) byteVal;
		}
		
		return new ChordId(fillBytes);
	}
	
	private byte[] bytes;
	
	private String hexText = "";
	
	private String keyText;
	
	public ChordId(String keyText) {

		this.keyText = keyText;
		
		byte[] allBytes = DigestUtils.sha1(keyText); //20 bytes
		
		bytes = new byte[ChordConfiguration.chordIdBytes];

		System.arraycopy(allBytes, 0, bytes, 0, ChordConfiguration.chordIdBytes);
		
		this.hexText = Integer.toHexString(ByteBuffer.wrap(bytes).getInt());
		
		while(hexText.length() < 8) {
			
			hexText = "0" + hexText;
		}
	}
	
	private ChordId(byte[] bytes) {
		
		this.bytes = bytes;
		
		this.hexText = Integer.toHexString(ByteBuffer.wrap(bytes).getInt());
		
		while(hexText.length() < 8) {
			
			hexText = "0" + hexText;
		}
	}
	
	public boolean gt(ChordId otherId) {
	
		return compareTo(otherId) > 0;
	}
	
	public boolean gte(ChordId otherId) {
		
		return compareTo(otherId) >= 0;
	}
	
	public boolean lt(ChordId otherId) {
		
		return compareTo(otherId) < 0;
	}
	
	public boolean lte(ChordId otherId) {
		
		return compareTo(otherId) <= 0;
	}

	@Override
	public int compareTo(ChordId o) {
		
		for (int i = 0; i < this.bytes.length; i++) {
			
			if ((byte)(this.bytes[i] - 128) < (byte)(o.bytes[i] - 128)) {
				
				return -1; // this ID is smaller
			} 
			else if ((byte)(this.bytes[i] - 128) > (byte)(o.bytes[i] -128)) {
				
				return 1; // this ID is greater
			}
		}
		
		return 0;
	}
	
	/**
	 * Calculates the ID which is 2^powerOfTwo bits greater than the current ID
	 * modulo the maximum ID and returns it.
	 * 
	 * @param powerOfTwo
	 *            Power of two which is added to the current ID. Must be a value
	 *            of the interval [0, length-1], including both extremes.
	 * @return ID which is 2^powerOfTwo bits greater than the current ID modulo
	 *         the maximum ID.
	 */
	public ChordId addPowerOfTwo(int powerOfTwo) {
		
		if (powerOfTwo < 0 || powerOfTwo >= (this.bytes.length * 8)) {
			
			throw new IllegalArgumentException(
					"The power of two is out of range! It must be in the interval "
							+ "[0, length-1]");
		}
		
		// copy ID
		
		byte[] copy = new byte[this.bytes.length];
		System.arraycopy(this.bytes, 0, copy, 0, this.bytes.length);

		// determine index of byte and the value to be added
		int indexOfByte = this.bytes.length - 1 - (powerOfTwo / 8);
		byte[] toAdd = { 1, 2, 4, 8, 16, 32, 64, -128 };
		byte valueToAdd = toAdd[powerOfTwo % 8];
		byte oldValue;

		do {
			// add value
			oldValue = copy[indexOfByte];
			copy[indexOfByte] += valueToAdd;

			// reset value to 1 for possible overflow situation
			valueToAdd = 1;
		}
		// check for overflow - occurs if old value had a leading one, i.e. it
		// was negative, and new value has a leading zero, i.e. it is zero or
		// positive; indexOfByte >= 0 prevents running out of the array to the
		// left in case of going over the maximum of the ID space
		while (oldValue < 0 && copy[indexOfByte] >= 0 && indexOfByte-- > 0);

		return new ChordId(copy);
	}
	
	/**
	 * @param left
	 * @param right
	 * @return id ∈ (left, right)
	 */
	public boolean isIn(ChordId left, ChordId right) {

		return isIn(left, right, false);
	}
	
	/**
	 * @param left
	 * @param right
	 * @param rightClosed
	 * @return id ∈ (left, right] or id ∈ (left, right)
	 */
	public boolean isIn(ChordId left, ChordId right, boolean rightClosed) {
		
		if(rightClosed) {
			
			if((left.lt(right) && gt(left) && lte(right))) {

				return true;
			}
			else if (left.gt(right) && ((gt(left) && lte(ChordId.MAX)) || (gt(ChordId.MIN) && lte(right)))) {
				
				return true;
			}
			
			return false; 
		}
		else {
			
			if((left.lt(right) && gt(left) && lt(right))) {

				return true;
			}
			else if (left.gt(right) && ((gt(left) && lte(ChordId.MAX)) || (gte(ChordId.MIN) && lt(right)))) {
				
				return true;
			}
			
			return false;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return obj instanceof ChordId && ((ChordId)obj).compareTo(this) == 0;
	}
	
	@Override
	public int hashCode() {
		
		return hexText.hashCode();
	}
	
	@Override
	public String toString() {
		
		return keyText == null ? hexText : keyText;
	}
	
	public String getKeyText() {
		
		return keyText;
	}
	
	public String getHexText() {
		
		return hexText;
	}
}
