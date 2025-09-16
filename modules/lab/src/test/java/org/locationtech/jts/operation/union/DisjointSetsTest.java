package org.locationtech.jts.operation.union;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.operation.union.DisjointSets.Subsets;

public class DisjointSetsTest {
	public void checkIntsModulo(int[] nums, int modulus, String[] setsExpected) {
		DisjointSets dset = new DisjointSets(nums.length);
		for (int i = 1; i < nums.length; i++) {
			for (int j = 0; j < i; j++) {
				if (nums[j] % modulus == nums[i] % modulus) {
					dset.merge(i, j);
				}
			}
		}
		String[] sets = dumpSets(nums, dset);
		assertEquals(setsExpected.length, sets.length);
		for (int i = 0; i < sets.length; i++) {
			assertEquals(setsExpected[i], sets[i]);
		}
	}

	private String[] dumpSets(int[] nums, DisjointSets dset) {
		Subsets subsets = dset.subsets();
		int nSet = subsets.getCount();
		// System.out.println("# Sets = " + nSet);
		String[] setStr = new String[nSet];
		for (int s = 0; s < nSet; s++) {
			// System.out.println("---- Set " + s);
			int size = subsets.getSize(s);
			String str = "";
			for (int si = 0; si < size; si++) {
				int itemIndex = subsets.getItem(s, si);
				if (si > 0)
					str += ",";
				str += nums[itemIndex];
			}
			setStr[s] = str;

			// System.out.println(setStr);
		}
		return setStr;
	}

	@Test
	public void testEmpty() {
		int[] nums = new int[]{};
		checkIntsModulo(nums, 3, new String[]{});
	}

	@Test
	public void testIntsModulo2() {
		int[] nums = new int[]{11, 22, 3, 45, 5, 62, 7};
		checkIntsModulo(nums, 2, new String[]{"22,62", "11,3,45,5,7"});
	}

	@Test
	public void testIntsModulo3() {
		int[] nums = new int[]{11, 22, 3, 45, 5, 62, 7};
		checkIntsModulo(nums, 3, new String[]{"3,45", "11,5,62", "22,7"});
	}

	@Test
	public void testSingleItem() {
		int[] nums = new int[]{11};
		checkIntsModulo(nums, 3, new String[]{"11",});
	}
}
