import java.util.*;
// The archetype map node is intended for location and categorization of assets into their best-fit archetypes.
public class ArchetypeMapNode{
	private Map<Archetype,ArchetypeMapNode>m_parents;
	private Map<Archetype,ArchetypeMapNode>m_children;
	private Archetype m_archetype;
	private ArchetypeMapNode m_root;
	private Map<Archetype,ArchetypeMapNode>m_index;
	private List<Asset>m_assets;
	public ArchetypeMapNode(Archetype archetype){
		m_archetype=archetype;
		m_children=new HashMap<Archetype,ArchetypeMapNode>();
		m_parents=new HashMap<Archetype,ArchetypeMapNode>();
		m_assets=new LinkedList<Asset>();
	}
	public void createIndex(){
		m_index=new HashMap<Archetype,ArchetypeMapNode>();
	}
	public static ArchetypeMapNode createTree(Asset asset){
		List<Ace>aces=asset.getAces();
		ArchetypeMapNode node=new ArchetypeMapNode(aces.get(0).getArchetype().getRoot());
		node.createIndex();
		node.addAsset(asset);
		return node;
	}
	public Map<Archetype,ArchetypeMapNode>getIndex(){
		if(getRoot()==this){return m_index;}
		return getRoot().getIndex();
	}
	public Map<Archetype,ArchetypeMapNode>getParents(){return m_parents;}
	public Map<Archetype,ArchetypeMapNode>getChildren(){return m_children;}
	public ArchetypeMapNode getRoot(){
		if(m_parents==null||m_parents.size()==0){return this;}
		return ((ArchetypeMapNode)m_parents.get((Archetype)m_parents.keySet().iterator().next())).getRoot();
	}
	public void addAsset(Asset asset){
		for(Ace archetype:asset.getAces()){
			ArchetypeMapNode node=assertNode(archetype.getArchetype());
			if(node==this){m_assets.add(asset);continue;}
			node.addAssetToList(asset);
		}
	}
	public void addAssetToList(Asset asset){m_assets.add(asset);}
	public List<Asset>getAllAssets(){
		List<Asset>assets=new LinkedList<Asset>();
		assets.addAll(m_assets);
		for(ArchetypeMapNode node:m_children.values()){
			assets.addAll(node.getAllAssets());
		}
		return assets;
	}
	public List<Asset>getAssetsOfType(Archetype type){
		ArchetypeMapNode node=assertNode(type);
		return node.getAllAssets();
	}
	public ArchetypeMapNode getNode(Archetype type){
		if(m_index!=null){return(ArchetypeMapNode)m_index.get(type);}
		return getRoot().getNode(type);
	}
	public ArchetypeMapNode addNode(Archetype type){
		ArchetypeMapNode node=getNode(type);
		if(node==null){
			node=new ArchetypeMapNode(type);
			getRoot().getIndex().put(type,node);
		}
		node.addParent(this);
		m_children.put(type, node);
		return node;
	}
	public ArchetypeMapNode assertNode(Archetype type){
		if(getRoot().getNode(type)==null){
			if(type.getParents().size()==0){return getRoot().addNode(type);
			}else{
				for(int i=0;i<type.getParents().size();i++){
					assertNode(type.getParents().get(i).getArchetype()).addNode(type);
				}
			}
		}
		return getRoot().getNode(type);
	}
	public void addParent(ArchetypeMapNode node){m_parents.put(node.getArchetype(), node);}
	public Archetype getArchetype(){return m_archetype;}
}
