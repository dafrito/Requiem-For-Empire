package com.dafrito.rfe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// The archetype map node is intended for location and categorization of assets into their best-fit archetypes.
public class ArchetypeMapNode {
	public static ArchetypeMapNode createTree(Asset asset) {
		List<Ace> aces = asset.getAces();
		ArchetypeMapNode node = new ArchetypeMapNode(aces.get(0).getArchetype().getRoot());
		node.createIndex();
		node.addAsset(asset);
		return node;
	}

	private Map<Archetype, ArchetypeMapNode> parents;
	private Map<Archetype, ArchetypeMapNode> children;
	private Archetype archetype;
	private Map<Archetype, ArchetypeMapNode> index;

	private List<Asset> assets;

	public ArchetypeMapNode(Archetype archetype) {
		this.archetype = archetype;
		this.children = new HashMap<Archetype, ArchetypeMapNode>();
		this.parents = new HashMap<Archetype, ArchetypeMapNode>();
		this.assets = new LinkedList<Asset>();
	}

	public void addAsset(Asset asset) {
		for (Ace archetype : asset.getAces()) {
			ArchetypeMapNode node = this.assertNode(archetype.getArchetype());
			if (node == this) {
				this.assets.add(asset);
				continue;
			}
			node.addAssetToList(asset);
		}
	}

	public void addAssetToList(Asset asset) {
		this.assets.add(asset);
	}

	public ArchetypeMapNode addNode(Archetype type) {
		ArchetypeMapNode node = this.getNode(type);
		if (node == null) {
			node = new ArchetypeMapNode(type);
			this.getRoot().getIndex().put(type, node);
		}
		node.addParent(this);
		this.children.put(type, node);
		return node;
	}

	public void addParent(ArchetypeMapNode node) {
		this.parents.put(node.getArchetype(), node);
	}

	public ArchetypeMapNode assertNode(Archetype type) {
		if (this.getRoot().getNode(type) == null) {
			if (type.getParents().size() == 0) {
				return this.getRoot().addNode(type);
			} else {
				for (int i = 0; i < type.getParents().size(); i++) {
					this.assertNode(type.getParents().get(i).getArchetype()).addNode(type);
				}
			}
		}
		return this.getRoot().getNode(type);
	}

	public void createIndex() {
		this.index = new HashMap<Archetype, ArchetypeMapNode>();
	}

	public List<Asset> getAllAssets() {
		List<Asset> assets = new LinkedList<Asset>();
		assets.addAll(this.assets);
		for (ArchetypeMapNode node : this.children.values()) {
			assets.addAll(node.getAllAssets());
		}
		return assets;
	}

	public Archetype getArchetype() {
		return this.archetype;
	}

	public List<Asset> getAssetsOfType(Archetype type) {
		ArchetypeMapNode node = this.assertNode(type);
		return node.getAllAssets();
	}

	public Map<Archetype, ArchetypeMapNode> getChildren() {
		return this.children;
	}

	public Map<Archetype, ArchetypeMapNode> getIndex() {
		if (this.getRoot() == this) {
			return this.index;
		}
		return this.getRoot().getIndex();
	}

	public ArchetypeMapNode getNode(Archetype type) {
		if (this.index != null) {
			return this.index.get(type);
		}
		return this.getRoot().getNode(type);
	}

	public Map<Archetype, ArchetypeMapNode> getParents() {
		return this.parents;
	}

	public ArchetypeMapNode getRoot() {
		if (this.parents == null || this.parents.size() == 0) {
			return this;
		}
		return (this.parents.get(this.parents.keySet().iterator().next())).getRoot();
	}
}
