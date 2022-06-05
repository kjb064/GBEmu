package emulator;

/**
 * Class to represent the internal information area of a cartridge. All header
 * information is within $100-014F of the ROM data.
 */
public class ROMHeader {

	/**
	 * 
	 * TODO determine if the byte data type should be used. Java uses signed byte,
	 * so really stores only 7 bits plus sign. Consider whether
	 * Byte.toUnsignedInteger() is useful?
	 *
	 */

	/**
	 * Built in boot procedure jumps to this address ($100) before jumping to the
	 * main program in the cartridge.
	 */
	private byte entryPoint[] = new byte[4];

	/**
	 * Bytes that define the bitmap of the Nintendo logo stored at $0104-0133.
	 */
	private byte nintendoLogo[] = new byte[0x30];

	/**
	 * Value at $0134-0143; Title of the game in upper case ASCII. 16 characters
	 * max.
	 */
	private String title;

	/**
	 * Value at $0144-0145; specifies a 2 character licensee code for "newer" games
	 * only.
	 */
	private String newLicenseeCode;

	/**
	 * Value at $014B; specifies game company/publisher. $33 indicates the new
	 * licensee code should be used instead.
	 */
	private byte oldLicenseeCode;

	/**
	 * Flag at $0146 that specifies whether the game supports Super GameBoy (SGB)
	 * functions.
	 */
	private boolean sgbFlag;

	/**
	 * Value at $0147; which Memory Bank Controller is used (if any) and if further
	 * external hardware exists in the cartridge.
	 */
	private String cartridgeType;

	/**
	 * Value at $0148; specifies ROM size of the cartridge.
	 */
	private byte romSizeCode;

	/**
	 * Value at $0149; specifies size of external RAM in the cartridge (if any).
	 */
	private byte ramSizeCode;

	/**
	 * Value at $014A; specifies if the game is sold in Japan ($00) or anywhere else
	 * ($01).
	 */
	private byte destinationCode;

	/**
	 * Value at $014C; specifies version number of the game (usually $00).
	 */
	private byte versionNumber;

	/**
	 * Value at $014D; Contains an 8 bit checksum across the cartridge header bytes
	 * $0134-014C.
	 */
	private byte headerChecksum;

	/**
	 * Value at $014E-014F; Contains a 16 bit checksum (upper byte first) across the
	 * whole cartridge ROM.
	 */
	private char globalChecksum;

	public ROMHeader(byte[] data) {
		// TODO entry point
		// TODO nintendo logo
		setTitle(data);
		setSGBFlag(data);
		setROMSizeCode(data);
		setRAMSizeCode(data);
		setLicenseeCode(data);
		setCartridgeType(data);
		setVersionNumber(data);
		setHeaderChecksum(data);
		setGlobalChecksum(data);
		setDestinationCode(data);

		// validate header checksum
		int x = 0;
		for (int i = 0x0134; i <= 0x014C; i++) {
			x = x - data[i] - 1;
		}
		System.out.println("Checksum passed?: " + ((x & 0xFF) == this.headerChecksum));
	}

	private void setTitle(byte[] data) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0x0134; i <= 0x0143; i++) {
			builder.append((char) data[i]);
		}
		this.title = builder.toString();
	}

	private void setSGBFlag(byte[] data) {
		// If the value is $03, the game supports SGB functions.
		this.sgbFlag = data[0x0146] == 0x03;
	}

	private void setROMSizeCode(byte[] data) {
		this.romSizeCode = data[0x0148];
	}

	private static String lookupRAMSizeViaCode(byte code) {
		switch (Byte.toUnsignedInt(code)) {
		case 0x00:
			return "0 KB";
		case 0x01:
			return "-";
		case 0x02:
			return "8 KB";
		case 0x03:
			return "32 KB";
		case 0x04:
			return "128 KB";
		case 0x05:
			return "64 KB";
		default:
			return "???";
		}
	}

	private void setRAMSizeCode(byte[] data) {
		this.ramSizeCode = data[0x0149];
	}

	private void setLicenseeCode(byte[] data) {
		// Check whether new licensee code should be used
		byte code = data[0x014B];
		if (code == 0x33) {
			// Using new code; need to read values at $0144-0145
			StringBuilder builder = new StringBuilder();
			builder.append((char) data[0x0144]);
			builder.append((char) data[0x0145]);
			this.newLicenseeCode = builder.toString();
		} else {
			this.oldLicenseeCode = code;
		}
	}

	private String lookupCatridgeType(byte type) {
		switch (Byte.toUnsignedInt(type)) {
		case 0x00:
			return "ROM ONLY";
		case 0x01:
			return "MBC1";
		case 0x02:
			return "MBC1+RAM";
		case 0x03:
			return "MBC1+RAM+BATTERY";
		case 0x05:
			return "MBC2";
		case 0x06:
			return "MBC2+BATTERY";
		case 0x08:
			return "ROM+RAM";
		case 0x09:
			return "ROM+RAM+BATTERY";
		case 0x0B:
			return "MMM01";
		case 0x0C:
			return "MMM01+RAM";
		case 0x0D:
			return "MMM01+RAM+BATTERY";
		case 0x0F:
			return "MBC3+TIMER+BATTERY";
		case 0x10:
			return "MBC3+TIMER+RAM+BATTERY";
		case 0x11:
			return "MBC3";
		case 0x12:
			return "MBC3+RAM";
		case 0x13:
			return "MBC3+RAM+BATTERY";
		case 0x19:
			return "MBC5";
		case 0x1A:
			return "MBC5+RAM";
		case 0x1B:
			return "MBC5+RAM+BATTERY";
		case 0x1C:
			return "MBC5+RUMBLE";
		case 0x1D:
			return "MBC5+RUMBLE+RAM";
		case 0x1E:
			return "MBC5+RUMBLE+RAM+BATTERY";
		case 0x20:
			return "MBC6";
		case 0x22:
			return "MBC7+SENSOR+RUMBLE+RAM+BATTERY";
		case 0xFC:
			return "POCKET CAMERA";
		case 0xFD:
			return "BANDAI TAMA5";
		case 0xFE:
			return "HuC3";
		case 0xFF:
			return "HuC1+RAM+BATTERY";
		default:
			return "???";
		}
	}

	private void setCartridgeType(byte[] data) {
		this.cartridgeType = lookupCatridgeType(data[0x0147]);
	}

	private void setVersionNumber(byte[] data) {
		this.versionNumber = data[0x014C];
	}

	private void setHeaderChecksum(byte[] data) {
		this.headerChecksum = data[0x014D];
	}

	private void setGlobalChecksum(byte[] data) {
		// TODO
	}

	private static String lookupDestinationViaCode(byte code) {
		switch (Byte.toUnsignedInt(code)) {
		case 0x00:
			return "Japanese";
		case 0x01:
			return "Non-Japanese";
		default:
			return "???";
		}
	}

	private void setDestinationCode(byte[] data) {
		this.destinationCode = data[0x014A];
	}

	@Override
	public String toString() {
		// TODO others
		StringBuilder builder = new StringBuilder();
		builder.append("Title: " + this.title + "\n");
		builder.append("Version: " + Byte.toUnsignedInt(this.versionNumber) + "\n");
		builder.append("ROM Size: " + (32 << this.romSizeCode) + "KB \n");
		builder.append("RAM Size: " + lookupRAMSizeViaCode(this.ramSizeCode) + "\n");
		builder.append("Supports SGB functions: " + this.sgbFlag + "\n");
		builder.append("Cartridge type: " + this.cartridgeType + "\n");
		builder.append("Destination: " + lookupDestinationViaCode(this.destinationCode) + "\n");
		return builder.toString();
	}
}
