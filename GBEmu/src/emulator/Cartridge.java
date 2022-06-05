package emulator;

public class Cartridge {

	public Cartridge(byte[] data) {
		// TODO need validation of ROM data; checks for size, etc.
		ROMHeader header = new ROMHeader(data);
		System.out.println(header);
	}
}
