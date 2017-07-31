package org.densyakun.bukkit.animalcrossing.villagemanager;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.util.Vector;
public class Village implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//村のクラス
	//同じ名前の村が存在する場合があるため村の判定をするには村番地を使うこと。
	
	private int id;//村番地
	private String name;//村の名前
	private UUID mayoruuid;//村長のUUID
	private transient HashMap<UUID, Location> villagers;//プレイヤーの居住地
	private boolean ghosttown = false;//廃村かどうか
	private List<UUID> banplayers;//村への立ち入りを禁止したプレイヤー一覧
	private HashMap<String, String> metadata = new HashMap<String, String>();//メタデータ
	private transient Vector station_square;//駅前広場
	Village(int id, String name, UUID mayoruuid) {
		this(id, name, mayoruuid, new HashMap<UUID, Location>(), false, new ArrayList<UUID>());
	}
	Village(int id, String name, UUID mayoruuid, HashMap<UUID, Location> villagers, boolean ghosttown, List<UUID> banplayers) {
		this.id = id;
		this.name = name;
		this.mayoruuid = mayoruuid;
		this.villagers = villagers;
		this.ghosttown = ghosttown;
		this.banplayers = banplayers;
		init();
	}
	void ghosttown() {
		ghosttown = true;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public UUID getMayorUUID() {
		return mayoruuid;
	}
	public HashMap<UUID, Location> getVillagers() {
		return villagers;
	}
	public boolean isGhosttown() {
		return ghosttown;
	}
	public List<UUID> getBanplayers() {
		return banplayers;
	}
	@Override
	public String toString() {
		return name + "村(#" + id + ")";
	}
	public HashMap<String, String> getMetadata() {
		return metadata;
	}
	public String[] getMetadataKeys() {
		return metadata.keySet().toArray(new String[0]);
	}
	public String getMetadata(String key) {
		return metadata.get(key);
	}
	public void setMetadata(String key, String value) {
		metadata.put(key, value);
	}
	public String removeMetadata(String key) {
		return metadata.remove(key);
	}
	public Vector getVillageStationSquare() {
		return station_square;
	}
	public static int getVillagePosX(int id) {
		return id * (VillageManager.village_width + VillageManager.edge_width * 2);
	}
	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		HashMap<UUID, HashMap<String, Object>> a = new HashMap<UUID, HashMap<String, Object>>();
		Iterator<UUID> keys = villagers.keySet().iterator();
		while (keys.hasNext()) {
			UUID key = keys.next();
			a.put(key, new HashMap<String, Object>(villagers.get(key).serialize()));
		}
		stream.writeObject(a);
	}
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		@SuppressWarnings("unchecked")
		HashMap<UUID, HashMap<String, Object>> a = (HashMap<UUID, HashMap<String, Object>>) stream.readObject();
		Iterator<UUID> keys = a.keySet().iterator();
		while (keys.hasNext()) {
			UUID key = keys.next();
			villagers.put(key, Location.deserialize(a.get(key)));
		}
		init();
	}
	private void init() {
		station_square = new Vector(getVillagePosX(id) + 16 * 2 + 4.5, VillageManager.ground_height, 2.5);
	}
}
