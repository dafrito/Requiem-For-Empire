package com.dafrito.economy;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The archetype map node is intended for location and categorization of assets
 * into their best-fit archetypes.
 * 
 * @author Aaron
 */
public class ArchetypeMapNode {
    private Map<Archetype, ArchetypeMapNode> parents = new HashMap<Archetype, ArchetypeMapNode>();
    private Map<Archetype, ArchetypeMapNode> children = new HashMap<Archetype, ArchetypeMapNode>();
    private Archetype archetype;
    private Map<Archetype, ArchetypeMapNode> index = new HashMap<Archetype, ArchetypeMapNode>();
    private List<Asset> assets = new LinkedList<Asset>();

    public ArchetypeMapNode(Archetype archetype) {
        this.archetype = archetype;
    }
    
    public static ArchetypeMapNode createTree(Asset asset) {
        List<Ace> aces = asset.getAces();
        ArchetypeMapNode node = new ArchetypeMapNode(aces.get(0).getArchetype().getRoot());
        node.addAsset(asset);
        return node;
    }

    public Map<Archetype, ArchetypeMapNode> getIndex() {
        if(this.getRoot() == this) {
            return this.index;
        }
        return this.getRoot().getIndex();
    }

    public Map<Archetype, ArchetypeMapNode> getParents() {
        return this.parents;
    }

    public Map<Archetype, ArchetypeMapNode> getChildren() {
        return this.children;
    }

    public ArchetypeMapNode getRoot() {
        if(this.parents == null || this.parents.isEmpty()) {
            return this;
        }
        // TODO: Hideous way to get the first element.
        return this.parents.get(this.parents.keySet().iterator().next()).getRoot();
    }

    public void addAsset(Asset asset) {
        for(Ace assetArchetype : asset.getAces()) {
            ArchetypeMapNode node = assertNode(assetArchetype.getArchetype());
            if(node == this) {
                this.assets.add(asset);
                continue;
            }
            node.addAssetToList(asset);
        }
    }

    public void addAssetToList(Asset asset) {
        this.assets.add(asset);
    }

    public List<Asset> getAllAssets() {
        List<Asset> cumulativeAssets = new LinkedList<Asset>();
        cumulativeAssets.addAll(this.assets);
        for(ArchetypeMapNode node : this.children.values()) {
            cumulativeAssets.addAll(node.getAllAssets());
        }
        return Collections.unmodifiableList(cumulativeAssets);
    }

    public List<Asset> getAssetsOfType(Archetype type) {
        ArchetypeMapNode node = assertNode(type);
        return node.getAllAssets();
    }

    public ArchetypeMapNode getNode(Archetype type) {
        if(this.index != null) {
            return this.index.get(type);
        }
        return getRoot().getNode(type);
    }

    public ArchetypeMapNode addNode(Archetype type) {
        ArchetypeMapNode node = getNode(type);
        if(node == null) {
            node = new ArchetypeMapNode(type);
            getRoot().getIndex().put(type, node);
        }
        node.addParent(this);
        this.children.put(type, node);
        return node;
    }

    public ArchetypeMapNode assertNode(Archetype type) {
        if(this.getRoot().getNode(type) == null) {
            if(type.getParents().size() == 0)
                return getRoot().addNode(type);
            for(int i = 0; i < type.getParents().size(); i++) {
                assertNode(type.getParents().get(i).getArchetype()).addNode(type);
            }
        }
        return this.getRoot().getNode(type);
    }

    public void addParent(ArchetypeMapNode node) {
        this.parents.put(node.getArchetype(), node);
    }

    public Archetype getArchetype() {
        return this.archetype;
    }
}
