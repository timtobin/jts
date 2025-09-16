package org.locationtech.jts.geom;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import test.jts.GeometryTestCase;

public class GeometryOpGCUnsupportedTest extends GeometryTestCase {
	static String WKT_GC = "GEOMETRYCOLLECTION (POLYGON ((100 200, 200 200, 200 100, 100 100, 100 200)), LINESTRING (150 250, 250 250))";
	static String WKT_POLY = "POLYGON ((50 50, 50 150, 150 150, 150 50, 50 50))";

	@Test
	public void testBoundary() {
		final Geometry a = read(WKT_GC);
		final Geometry b = read(WKT_POLY);

		(new FailureChecker() {
			void operation() {
				a.getBoundary();
			}
		}).check(IllegalArgumentException.class);
	}

	@Test
	public void testDifference() {
		final Geometry a = read(WKT_GC);
		final Geometry b = read(WKT_POLY);

		(new FailureChecker() {
			void operation() {
				a.difference(b);
			}
		}).check(IllegalArgumentException.class);

		(new FailureChecker() {
			void operation() {
				b.difference(a);
			}
		}).check(IllegalArgumentException.class);
	}

	@Test
	public void testRelate() {
		final Geometry a = read(WKT_GC);
		final Geometry b = read(WKT_POLY);

		(new FailureChecker() {
			void operation() {
				a.relate(b);
			}
		}).check(IllegalArgumentException.class);

		(new FailureChecker() {
			void operation() {
				b.relate(a);
			}
		}).check(IllegalArgumentException.class);
	}

	@Test
	public void testSymDifference() {
		final Geometry a = read(WKT_GC);
		final Geometry b = read(WKT_POLY);

		(new FailureChecker() {
			void operation() {
				a.symDifference(b);
			}
		}).check(IllegalArgumentException.class);

		(new FailureChecker() {
			void operation() {
				b.symDifference(a);
			}
		}).check(IllegalArgumentException.class);
	}

	@Test
	public void testUnion() {
		final Geometry a = read(WKT_GC);
		final Geometry b = read(WKT_POLY);

		(new FailureChecker() {
			void operation() {
				a.union(b);
			}
		}).check(IllegalArgumentException.class);

		(new FailureChecker() {
			void operation() {
				b.union(a);
			}
		}).check(IllegalArgumentException.class);
	}

	abstract static class FailureChecker {

		void check(Class exClz) {
			assertTrue(isError(exClz));
		}

		boolean isError(Class exClz) {
			try {
				operation();
				return false;
			} catch (Throwable t) {
				if (t.getClass() == exClz)
					return true;
			}
			return false;
		}

		/** An operation which should throw an exception of the specified class */
		abstract void operation();
	}
}
