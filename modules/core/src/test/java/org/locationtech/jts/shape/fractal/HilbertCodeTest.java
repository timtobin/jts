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
