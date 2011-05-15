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

	private Map<Archetype, ArchetypeMapNode> m_parents;
	private Map<Archetype, ArchetypeMapNode> m_children;
	private Archetype m_archetype;
	private ArchetypeMapNode m_root;
	private Map<Archetype, ArchetypeMapNode> m_index;

	private List<Asset> m_assets;

	public ArchetypeMapNode(Archetype archetype) {
		this.m_archetype = archetype;
		this.m_children = new HashMap<Archetype, ArchetypeMapNode>();
		this.m_parents = new HashMap<Archetype, ArchetypeMapNode>();
		this.m_assets = new LinkedList<Asset>();
	}

	public void addAsset(Asset asset) {
		for (Ace archetype : asset.getAces()) {
			ArchetypeMapNode node = this.assertNode(archetype.getArchetype());
			if (node == this) {
				this.m_assets.add(asset);
				continue;
			}
			node.addAssetToList(asset);
		}
	}

	public void addAssetToList(Asset asset) {
		this.m_assets.add(asset);
	}

	public ArchetypeMapNode addNode(Archetype type) {
		ArchetypeMapNode node = this.getNode(type);
		if (node == null) {
			node = new ArchetypeMapNode(type);
			this.getRoot().getIndex().put(type, node);
		}
		node.addParent(this);
		this.m_children.put(type, node);
		return node;
	}

	public void addParent(ArchetypeMapNode node) {
		this.m_parents.put(node.getArchetype(), node);
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
		this.m_index = new HashMap<Archetype, ArchetypeMapNode>();
	}

	public List<Asset> getAllAssets() {
		List<Asset> assets = new LinkedList<Asset>();
		assets.addAll(this.m_assets);
		for (ArchetypeMapNode node : this.m_children.values()) {
			assets.addAll(node.getAllAssets());
		}
		return assets;
	}

	public Archetype getArchetype() {
		return this.m_archetype;
	}

	public List<Asset> getAssetsOfType(Archetype type) {
		ArchetypeMapNode node = this.assertNode(type);
		return node.getAllAssets();
	}

	public Map<Archetype, ArchetypeMapNode> getChildren() {
		return this.m_children;
	}

	public Map<Archetype, ArchetypeMapNode> getIndex() {
		if (this.getRoot() == this) {
			return this.m_index;
		}
		return this.getRoot().getIndex();
	}

	public ArchetypeMapNode getNode(Archetype type) {
		if (this.m_index != null) {
			return this.m_index.get(type);
		}
		return this.getRoot().getNode(type);
	}

	public Map<Archetype, ArchetypeMapNode> getParents() {
		return this.m_parents;
	}

	public ArchetypeMapNode getRoot() {
		if (this.m_parents == null || this.m_parents.size() == 0) {
			return this;
		}
		return (this.m_parents.get(this.m_parents.keySet().iterator().next())).getRoot();
	}
}
