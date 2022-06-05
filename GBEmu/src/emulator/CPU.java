package emulator;

import java.nio.ByteBuffer;

public class CPU {

	private class AF {
		byte A;
		byte F;

		short AF() {
			// TODO confirm endianness...
			// TODO java short not unsigned...
			ByteBuffer buffer = ByteBuffer.allocate(2);
			buffer.put(F);
			buffer.put(A);
			return buffer.getShort(0);
		}
	}

	private class BC {
		byte B;
		byte C;
	}

	private class DE {
		byte D;
		byte E;
	}

	private class HL {
		byte H;
		byte L;
	}

	/** Program Counter; points to address of next instruction in memory */
	private short PC;
	/** Stack Pointer; points to top of the stack in memory */
	private short SP;

	CPU() {
		// TODO this is first address to read from ROM; initialize here?
		this.PC = 0x100;

		// Initialize stack pointer on "power-up"
		// this.SP = 0xFFFE;
	}

	private void decode() {

	}
}
