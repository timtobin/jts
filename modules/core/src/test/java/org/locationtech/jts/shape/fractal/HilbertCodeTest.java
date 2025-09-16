package org.locationtech.jts.shape.fractal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.locationtech.jts.shape.fractal.HilbertCode.*;
import static org.locationtech.jts.shape.fractal.MortonCode.level;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

public class HilbertCodeTest {

	private void checkDecode(int order, int index, int x, int y) {
		Coordinate p = decode(order, index);
		// System.out.println(p);
		assertEquals((int) p.getX(), x);
		assertEquals((int) p.getY(), y);
	}

	private void checkDecodeEncode(int level, int index) {
		Coordinate p = decode(level, index);
		int encode = encode(level, (int) p.getX(), (int) p.getY());
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
		checkDecode(1, 0, 0, 0);

		checkDecode(1, 0, 0, 0);
		checkDecode(1, 1, 0, 1);

		checkDecode(3, 0, 0, 0);
		checkDecode(3, 1, 0, 1);

		checkDecode(4, 0, 0, 0);
		checkDecode(4, 1, 1, 0);
		checkDecode(4, 24, 6, 2);
		checkDecode(4, 255, 15, 0);

		checkDecode(5, 124, 8, 6);
	}

	@Test
	public void testDecodeEncode() {
		checkDecodeEncodeForLevel(4);
		checkDecodeEncodeForLevel(5);
	}

	@Test
	public void testLevel() {
		assertEquals(level(1), 0);

		assertEquals(level(2), 1);
		assertEquals(level(3), 1);
		assertEquals(level(4), 1);

		assertEquals(level(5), 2);
		assertEquals(level(13), 2);
		assertEquals(level(15), 2);
		assertEquals(level(16), 2);

		assertEquals(level(17), 3);
		assertEquals(level(63), 3);
		assertEquals(level(64), 3);

		assertEquals(level(65), 4);
		assertEquals(level(255), 4);
		assertEquals(level(255), 4);
		assertEquals(level(256), 4);
	}

	@Test
	public void testSize() {
		assertEquals(size(0), 1);
		assertEquals(size(1), 4);
		assertEquals(size(2), 16);
		assertEquals(size(3), 64);
		assertEquals(size(4), 256);
		assertEquals(size(5), 1024);
		assertEquals(size(6), 4096);
	}
}
