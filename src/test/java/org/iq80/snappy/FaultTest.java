package org.iq80.snappy;

import java.util.Random;

import org.testng.annotations.Test;

public class FaultTest {

	private static final Random RANDOM = new Random();

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testLongUncompressedLengthOnCompress() {
		byte[] uncompressed = newSourceData();
		byte[] compressed = new byte[SnappyCompressor.maxCompressedLength(uncompressed.length)];
		SnappyCompressor.compress(uncompressed, 0, uncompressed.length + 10, compressed, 0);
	}

	private byte[] newSourceData() {
		byte[] source = new byte[1024];
		RANDOM.nextBytes(source);
		return source;
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void testShortCompressedLengthOnCompress() {
		byte[] uncompressed = newSourceData();
		byte[] compressed = new byte[10];
		
		// Compressed buffer just gets overrun
		SnappyCompressor.compress(uncompressed, 0, uncompressed.length, compressed, 0);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testLongCompressedLengthOnUncompress() {
		byte[] uncompressed = newSourceData();
		byte[] compressed = new byte[SnappyCompressor.maxCompressedLength(uncompressed.length)];
		SnappyCompressor.compress(uncompressed, 0, uncompressed.length, compressed, 0);

		SnappyDecompressor.uncompress(compressed, 0, compressed.length + 30);
	}

	// Short compressed length on uncompress is just corrupt data. Usually fails
	// with Corrupt Literal

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testShortUncompressedLengthOnUncompress() {
		byte[] uncompressed = newSourceData();
		byte[] compressed = new byte[SnappyCompressor.maxCompressedLength(uncompressed.length)];
		SnappyCompressor.compress(uncompressed, 0, uncompressed.length, compressed, 0);

		byte[] target = new byte[uncompressed.length - 100];
		SnappyDecompressor.uncompress(compressed, 0, compressed.length, target, 0);
	}

	@Test(expectedExceptions = CorruptionException.class)
	public void testTruncatedCompressedDataOnUncompress() {
		byte[] uncompressed = newSourceData();
		byte[] compressed = new byte[SnappyCompressor.maxCompressedLength(uncompressed.length)];
		SnappyCompressor.compress(uncompressed, 0, uncompressed.length, compressed, 0);

		byte[] truncated = new byte[compressed.length / 2];
		System.arraycopy(compressed, 0, truncated, 0, truncated.length);

		SnappyDecompressor.uncompress(truncated, 0, truncated.length);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testTruncatedCompressedDataOriginalLengthOnUncompress() {
		byte[] uncompressed = newSourceData();
		byte[] compressed = new byte[SnappyCompressor.maxCompressedLength(uncompressed.length)];
		SnappyCompressor.compress(uncompressed, 0, uncompressed.length, compressed, 0);

		byte[] truncated = new byte[compressed.length / 2];
		System.arraycopy(compressed, 0, truncated, 0, truncated.length);

		SnappyDecompressor.uncompress(truncated, 0, compressed.length);
	}


}
