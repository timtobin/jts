/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jts.index.strtree;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.locationtech.jts.index.ItemVisitor;
import org.locationtech.jts.util.Assert;

/**
 * Base class for STRtree and SIRtree. STR-packed R-trees are described in: P.
 * Rigaux, Michel Scholl and Agnes Voisard. <i>Spatial Databases With
 * Application To GIS.</i> Morgan Kaufmann, San Francisco, 2002.
 *
 * <p>
 * This implementation is based on {@link Boundable}s rather than
 * {@link AbstractNode}s, because the STR algorithm operates on both nodes and
 * data, both of which are treated as Boundables.
 *
 * <p>
 * This class is thread-safe. Building the tree is synchronized, and querying is
 * stateless.
 *
 * @see STRtree
 * @see SIRtree
 * @version 1.7
 */
public abstract class AbstractSTRtree implements Serializable {

	private static final int DEFAULT_NODE_CAPACITY = 10;

	/** */
	@Serial
	private static final long serialVersionUID = -3886435814360241337L;

	protected static int compareDoubles(double a, double b) {
		return Double.compare(a, b);
	}

	private boolean built = false;

	/** Set to <tt>null</tt> when index is built, to avoid retaining memory. */
	private ArrayList itemBoundables = new ArrayList();

	private final int nodeCapacity;

	protected AbstractNode root;

	/** Constructs an AbstractSTRtree with the default node capacity. */
	public AbstractSTRtree() {
		this(DEFAULT_NODE_CAPACITY);
	}

	/**
	 * Constructs an AbstractSTRtree with the specified maximum number of child
	 * nodes that a node may have
	 *
	 * @param nodeCapacity
	 *            the maximum number of child nodes in a node
	 */
	public AbstractSTRtree(int nodeCapacity) {
		Assert.isTrue(nodeCapacity > 1, "Node capacity must be greater than 1");
		this.nodeCapacity = nodeCapacity;
	}

	/**
	 * Constructs an AbstractSTRtree with the specified maximum number of child
	 * nodes that a node may have, and the root node
	 *
	 * @param nodeCapacity
	 *            the maximum number of child nodes in a node
	 * @param root
	 *            the root node that links to all other nodes in the tree
	 */
	public AbstractSTRtree(int nodeCapacity, AbstractNode root) {
		this(nodeCapacity);
		built = true;
		this.root = root;
		this.itemBoundables = null;
	}

	/**
	 * Constructs an AbstractSTRtree with the specified maximum number of child
	 * nodes that a node may have, and all leaf nodes in the tree
	 *
	 * @param nodeCapacity
	 *            the maximum number of child nodes in a node
	 * @param itemBoundables
	 *            the list of leaf nodes in the tree
	 */
	public AbstractSTRtree(int nodeCapacity, ArrayList itemBoundables) {
		this(nodeCapacity);
		this.itemBoundables = itemBoundables;
	}

	protected List boundablesAtLevel(int level) {
		ArrayList boundables = new ArrayList();
		boundablesAtLevel(level, root, boundables);
		return boundables;
	}

	/**
	 * @param level
	 *            -1 to get items
	 */
	private void boundablesAtLevel(int level, AbstractNode top, Collection boundables) {
		Assert.isTrue(level > -2);
		if (top.getLevel() == level) {
			boundables.add(top);
			return;
		}
		for (Object o : top.getChildBoundables()) {
			Boundable boundable = (Boundable) o;
			if (boundable instanceof AbstractNode node) {
				boundablesAtLevel(level, node, boundables);
			} else {
				Assert.isTrue(boundable instanceof ItemBoundable);
				if (level == -1) {
					boundables.add(boundable);
				}
			}
		}
	}

	/**
	 * Creates parent nodes, grandparent nodes, and so forth up to the root node,
	 * for the data that has been inserted into the tree. Can only be called once,
	 * and thus can be called only after all of the data has been inserted into the
	 * tree.
	 */
	public synchronized void build() {
		if (built)
			return;
		root = itemBoundables.isEmpty() ? createNode(0) : createHigherLevels(itemBoundables, -1);
		// the item list is no longer needed
		itemBoundables = null;
		built = true;
	}

	/**
	 * Creates the levels higher than the given level
	 *
	 * @param boundablesOfALevel
	 *            the level to build on
	 * @param level
	 *            the level of the Boundables, or -1 if the boundables are item
	 *            boundables (that is, below level 0)
	 * @return the root, which may be a ParentNode or a LeafNode
	 */
	private AbstractNode createHigherLevels(List boundablesOfALevel, int level) {
		Assert.isTrue(!boundablesOfALevel.isEmpty());
		List parentBoundables = createParentBoundables(boundablesOfALevel, level + 1);
		if (parentBoundables.size() == 1) {
			return (AbstractNode) parentBoundables.getFirst();
		}
		return createHigherLevels(parentBoundables, level + 1);
	}

	protected abstract AbstractNode createNode(int level);

	/**
	 * Sorts the childBoundables then divides them into groups of size M, where M is
	 * the node capacity.
	 */
	protected List createParentBoundables(List childBoundables, int newLevel) {
		Assert.isTrue(!childBoundables.isEmpty());
		ArrayList parentBoundables = new ArrayList();
		parentBoundables.add(createNode(newLevel));
		ArrayList sortedChildBoundables = new ArrayList(childBoundables);
		sortedChildBoundables.sort(getComparator());
		for (Object sortedChildBoundable : sortedChildBoundables) {
			Boundable childBoundable = (Boundable) sortedChildBoundable;
			if (lastNode(parentBoundables).getChildBoundables().size() == getNodeCapacity()) {
				parentBoundables.add(createNode(newLevel));
			}
			lastNode(parentBoundables).addChildBoundable(childBoundable);
		}
		return parentBoundables;
	}

	protected int depth() {
		if (isEmpty()) {
			return 0;
		}
		build();
		return depth(root);
	}

	protected int depth(AbstractNode node) {
		int maxChildDepth = 0;
		for (Object o : node.getChildBoundables()) {
			Boundable childBoundable = (Boundable) o;
			if (childBoundable instanceof AbstractNode abstractNode) {
				int childDepth = depth(abstractNode);
				if (childDepth > maxChildDepth)
					maxChildDepth = childDepth;
			}
		}
		return maxChildDepth + 1;
	}

	protected abstract Comparator getComparator();

	/**
	 * @return a test for intersection between two bounds, necessary because
	 *         subclasses of AbstractSTRtree have different implementations of
	 *         bounds.
	 * @see IntersectsOp
	 */
	protected abstract IntersectsOp getIntersectsOp();

	ArrayList getItemBoundables() {
		return itemBoundables;
	}

	/**
	 * Returns the maximum number of child nodes that a node may have.
	 *
	 * @return the node capacity
	 */
	public int getNodeCapacity() {
		return nodeCapacity;
	}

	/**
	 * Gets the root node of the tree.
	 *
	 * @return the root node
	 */
	public AbstractNode getRoot() {
		build();
		return root;
	}

	protected void insert(Object bounds, Object item) {
		Assert.isTrue(!built, "Cannot insert items into an STR packed R-tree after it has been built.");
		itemBoundables.add(new ItemBoundable(bounds, item));
	}

	/**
	 * Tests whether the index contains any items. This method does not build the
	 * index, so items can still be inserted after it has been called.
	 *
	 * @return true if the index does not contain any items
	 */
	public boolean isEmpty() {
		if (!built)
			return itemBoundables.isEmpty();
		return root.isEmpty();
	}

	/**
	 * Gets a tree structure (as a nested list) corresponding to the structure of
	 * the items and nodes in this tree.
	 *
	 * <p>
	 * The returned {@link List}s contain either {@link Object} items, or Lists
	 * which correspond to subtrees of the tree Subtrees which do not contain any
	 * items are not included.
	 *
	 * <p>
	 * Builds the tree if necessary.
	 *
	 * @return a List of items and/or Lists
	 */
	public List itemsTree() {
		build();

		List valuesTree = itemsTree(root);
		if (valuesTree == null)
			return new ArrayList();
		return valuesTree;
	}

	private List itemsTree(AbstractNode node) {
		List valuesTreeForNode = new ArrayList();
		for (Object o : node.getChildBoundables()) {
			Boundable childBoundable = (Boundable) o;
			if (childBoundable instanceof AbstractNode abstractNode) {
				List valuesTreeForChild = itemsTree(abstractNode);
				// only add if not null (which indicates an item somewhere in this tree
				if (valuesTreeForChild != null)
					valuesTreeForNode.add(valuesTreeForChild);
			} else if (childBoundable instanceof ItemBoundable boundable) {
				valuesTreeForNode.add(boundable.getItem());
			} else {
				Assert.shouldNeverReachHere();
			}
		}
		if (valuesTreeForNode.size() <= 0)
			return null;
		return valuesTreeForNode;
	}

	protected AbstractNode lastNode(List nodes) {
		return (AbstractNode) nodes.getLast();
	}

	/** Also builds the tree, if necessary. */
	protected List query(Object searchBounds) {
		build();
		ArrayList matches = new ArrayList();
		if (isEmpty()) {
			// Assert.isTrue(root.getBounds() == null);
			return matches;
		}
		if (getIntersectsOp().intersects(root.getBounds(), searchBounds)) {
			queryInternal(searchBounds, root, matches);
		}
		return matches;
	}

	/** Also builds the tree, if necessary. */
	protected void query(Object searchBounds, ItemVisitor visitor) {
		build();
		if (isEmpty()) {
			// nothing in tree, so return
			// Assert.isTrue(root.getBounds() == null);
			return;
		}
		if (getIntersectsOp().intersects(root.getBounds(), searchBounds)) {
			queryInternal(searchBounds, root, visitor);
		}
	}

	private void queryInternal(Object searchBounds, AbstractNode node, ItemVisitor visitor) {
		List childBoundables = node.getChildBoundables();
		for (Object o : childBoundables) {
			Boundable childBoundable = (Boundable) o;
			if (!getIntersectsOp().intersects(childBoundable.getBounds(), searchBounds)) {
				continue;
			}
			if (childBoundable instanceof AbstractNode abstractNode) {
				queryInternal(searchBounds, abstractNode, visitor);
			} else if (childBoundable instanceof ItemBoundable boundable) {
				visitor.visitItem(boundable.getItem());
			} else {
				Assert.shouldNeverReachHere();
			}
		}
	}

	private void queryInternal(Object searchBounds, AbstractNode node, List matches) {
		List childBoundables = node.getChildBoundables();
		for (Object o : childBoundables) {
			Boundable childBoundable = (Boundable) o;
			if (!getIntersectsOp().intersects(childBoundable.getBounds(), searchBounds)) {
				continue;
			}
			if (childBoundable instanceof AbstractNode abstractNode) {
				queryInternal(searchBounds, abstractNode, matches);
			} else if (childBoundable instanceof ItemBoundable boundable) {
				matches.add(boundable.getItem());
			} else {
				Assert.shouldNeverReachHere();
			}
		}
	}

	private boolean remove(Object searchBounds, AbstractNode node, Object item) {
		// first try removing item from this node
		boolean found = removeItem(node, item);
		if (found)
			return true;

		AbstractNode childToPrune = null;
		// next try removing item from lower nodes
		for (Object o : node.getChildBoundables()) {
			Boundable childBoundable = (Boundable) o;
			if (!getIntersectsOp().intersects(childBoundable.getBounds(), searchBounds)) {
				continue;
			}
			if (childBoundable instanceof AbstractNode abstractNode) {
				found = remove(searchBounds, abstractNode, item);
				// if found, record child for pruning and exit
				if (found) {
					childToPrune = abstractNode;
					break;
				}
			}
		}
		// prune child if possible
		if (childToPrune != null) {
			if (childToPrune.getChildBoundables().isEmpty()) {
				node.getChildBoundables().remove(childToPrune);
			}
		}
		return found;
	}

	/** Removes an item from the tree. (Builds the tree, if necessary.) */
	protected boolean remove(Object searchBounds, Object item) {
		build();
		if (getIntersectsOp().intersects(root.getBounds(), searchBounds)) {
			return remove(searchBounds, root, item);
		}
		return false;
	}

	private boolean removeItem(AbstractNode node, Object item) {
		Boundable childToRemove = null;
		for (Object o : node.getChildBoundables()) {
			Boundable childBoundable = (Boundable) o;
			if (childBoundable instanceof ItemBoundable boundable) {
				if (boundable.getItem() == item)
					childToRemove = childBoundable;
			}
		}
		if (childToRemove != null) {
			node.getChildBoundables().remove(childToRemove);
			return true;
		}
		return false;
	}

	protected int size() {
		if (isEmpty()) {
			return 0;
		}
		build();
		return size(root);
	}

	protected int size(AbstractNode node) {
		int size = 0;
		for (Object o : node.getChildBoundables()) {
			Boundable childBoundable = (Boundable) o;
			if (childBoundable instanceof AbstractNode abstractNode) {
				size += size(abstractNode);
			} else if (childBoundable instanceof ItemBoundable) {
				size += 1;
			}
		}
		return size;
	}

	/**
	 * A test for intersection between two bounds, necessary because subclasses of
	 * AbstractSTRtree have different implementations of bounds.
	 */
	protected interface IntersectsOp {
		/**
		 * For STRtrees, the bounds will be Envelopes; for SIRtrees, Intervals; for
		 * other subclasses of AbstractSTRtree, some other class.
		 *
		 * @param aBounds
		 *            the bounds of one spatial object
		 * @param bBounds
		 *            the bounds of another spatial object
		 * @return whether the two bounds intersect
		 */
		boolean intersects(Object aBounds, Object bBounds);
	}
}
