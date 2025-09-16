package org.locationtech.jts.shape.fractal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.locationtech.jts.shape.fractal.MortonCode.*;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

public class MortonCodeTest {
	private void checkDecode(int index, int x, int y) {
		Coordinate p = decode(index);
		// System.out.println(p);
		assertEquals((int) p.getX(), x);
		assertEquals((int) p.getY(), y);
	}

	private void checkDecodeEncode(int level, int index) {
		Coordinate p = decode(index);
		int encode = encode((int) p.getX(), (int) p.getY());
		assertEquals(index, encode);
	}

	private void checkDecodeEncodeForLevel(int level) {
		int n = size(level);
		for (int i = 0; i < n; i++) {
			checkDecodeEncode(level, i);
		}
	}

	@Test
	public void testDecode() {
		checkDecode(0, 0, 0);
		checkDecode(1, 1, 0);
		checkDecode(2, 0, 1);
		checkDecode(3, 1, 1);
		checkDecode(4, 2, 0);

		checkDecode(24, 4, 2);
		checkDecode(124, 14, 6);
		checkDecode(255, 15, 15);
	}

	@Test
	public void testDecodeEncode() {
		checkDecodeEncodeForLevel(4);
		checkDecodeEncodeForLevel(5);
	}

	@Test
	public void testLevel() {
		assertEquals(0, level(1));

		assertEquals(1, level(2));
		assertEquals(1, level(3));
		assertEquals(1, level(4));

		assertEquals(2, level(5));
		assertEquals(2, level(13));
		assertEquals(2, level(15));
		assertEquals(2, level(16));

		assertEquals(3, level(17));
		assertEquals(3, level(63));
		assertEquals(3, level(64));

		assertEquals(4, level(65));
		assertEquals(4, level(255));
		assertEquals(4, level(255));
		assertEquals(4, level(256));
	}

	@Test
	public void testSize() {
		assertEquals(1, size(0));
		assertEquals(4, size(1));
		assertEquals(16, size(2));
		assertEquals(64, size(3));
		assertEquals(256, size(4));
		assertEquals(1024, size(5));
		assertEquals(4096, size(6));
	}
}
