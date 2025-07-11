package mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.HousesDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.house.HouseStatus;
import com.aionemu.gameserver.model.templates.housing.Building;
import com.aionemu.gameserver.model.templates.housing.BuildingType;
import com.aionemu.gameserver.model.templates.housing.HouseAddress;
import com.aionemu.gameserver.model.templates.housing.HousingLand;

/**
 * @author Rolandas
 */
public class MySQL5HousesDAO extends HousesDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5HousesDAO.class);

	private static final String SELECT_HOUSES_QUERY = "SELECT * FROM houses WHERE address <> 2001 AND address <> 3001";
	private static final String SELECT_STUDIOS_QUERY = "SELECT * FROM houses WHERE address = 2001 OR address = 3001";

	private static final String ADD_HOUSE_QUERY = "INSERT INTO houses (id, address, building_id, player_id, acquire_time, settings, status, fee_paid, next_pay, sell_started) "
		+ " VALUES (?,?,?,?,?,?,?,?,?,?)";

	private static final String UPDATE_HOUSE_QUERY = "UPDATE houses SET building_id=?, player_id=?, acquire_time=?, settings=?, status=?, fee_paid=?, next_pay=?, sell_started=? WHERE id=?";
	private static final String DELETE_HOUSE_QUERY = "DELETE FROM houses WHERE player_id=?";

	@Override
	public int[] getUsedIDs() {
		PreparedStatement statement = DB.prepareStatement("SELECT DISTINCT id FROM houses",
			ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		try {
			ResultSet rs = statement.executeQuery();
			rs.last();
			int count = rs.getRow();
			rs.beforeFirst();
			int[] ids = new int[count];
			for (int i = 0; i < count; i++) {
				rs.next();
				ids[i] = rs.getInt(1);
			}
			return ids;
		}
		catch (SQLException e) {
			log.error("Can't get list of id's from houses table", e);
		}
		finally {
			DB.close(statement);
		}

		return new int[0];
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

	@Override
	public boolean isIdUsed(int houseObjectId) {
		PreparedStatement s = DB.prepareStatement("SELECT count(id) as cnt FROM houses WHERE ? = houses.id");
		try {
			s.setInt(1, houseObjectId);
			ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt(1) > 0;
		}
		catch (SQLException e) {
			log.error("Can't check if house " + houseObjectId + ", is used, returning possitive result", e);
			return true;
		}
		finally {
			DB.close(s);
		}
	}

	@Override
	public void storeHouse(House house) {
		if (house.getPersistentState() == PersistentState.NEW)
			insertNewHouse(house);
		else
			updateHouse(house);
	}
	
	private void insertNewHouse(House house) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(ADD_HOUSE_QUERY);

			stmt.setInt(1, house.getObjectId());
			stmt.setInt(2, house.getAddress().getId());
			stmt.setInt(3, house.getBuilding().getId());
			stmt.setInt(4, house.getOwnerId());
			if (house.getAcquiredTime() == null)
				stmt.setNull(5, Types.TIMESTAMP);
			else
				stmt.setTimestamp(5, house.getAcquiredTime());

			stmt.setInt(6, house.getSettingFlags());
			stmt.setString(7, house.getStatus().toString());
			stmt.setInt(8, house.isFeePaid() ? 1 : 0);

			if (house.getNextPay() == null)
				stmt.setNull(9, Types.TIMESTAMP);
			else
				stmt.setTimestamp(9, house.getNextPay());

			if (house.getSellStarted() == null)
				stmt.setNull(10, Types.TIMESTAMP);
			else
				stmt.setTimestamp(10, house.getSellStarted());

			stmt.execute();
			stmt.close();
			house.setPersistentState(PersistentState.UPDATED);
		}
		catch (Exception e) {
			log.error("Could not store studio data. " + e.getMessage(), e);
			return;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return;
	}
	
	private void updateHouse(House house) {

		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_HOUSE_QUERY);

			stmt.setInt(1, house.getBuilding().getId());
			stmt.setInt(2, house.getOwnerId());
			if (house.getAcquiredTime() == null)
				stmt.setNull(3, Types.TIMESTAMP);
			else
				stmt.setTimestamp(3, house.getAcquiredTime());

			stmt.setInt(4, house.getSettingFlags());
			stmt.setString(5, house.getStatus().toString());
			stmt.setInt(6, house.isFeePaid() ? 1 : 0);

			if (house.getNextPay() == null)
				stmt.setNull(7, Types.TIMESTAMP);
			else
				stmt.setTimestamp(7, house.getNextPay());

			if (house.getSellStarted() == null)
				stmt.setNull(8, Types.TIMESTAMP);
			else
				stmt.setTimestamp(8, house.getSellStarted());
			stmt.setInt(9, house.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not store house data. " + e.getMessage(), e);
			return;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return;
	}

	@Override
	public Map<Integer, House> loadHouses(Collection<HousingLand> lands, boolean studios) {
		Map<Integer, House> houses = new HashMap<Integer, House>();
		Map<Integer, HouseAddress> addressesById = new HashMap<Integer, HouseAddress>();
		Map<Integer, List<Building>> buildingsForAddress = new HashMap<Integer, List<Building>>();
		for (HousingLand land : lands) {
			for (HouseAddress address : land.getAddresses()) {
				addressesById.put(address.getId(), address);
				buildingsForAddress.put(address.getId(), land.getBuildings());
			}
		}

		HashMap<Integer, Integer> addressHouseIds = new HashMap<Integer, Integer>();

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(studios ? SELECT_STUDIOS_QUERY : SELECT_HOUSES_QUERY);
			ResultSet rset = stmt.executeQuery();

			while (rset.next()) {
				int houseId = rset.getInt("id");
				int buildingId = rset.getInt("building_id");
				HouseAddress address = addressesById.get(rset.getInt("address"));
				Building building = null;
				for (Building b : buildingsForAddress.get(address.getId())) {
					if (b.getId() == buildingId) {
						building = b;
						break;
					}
				}

				House house = null;
				if (addressHouseIds.containsKey(address.getId())) {
					log.warn("Duplicate house address " + address.getId() + "!");
					continue;
				}
				else {
					house = new House(houseId, building, address, 0);
					if (building.getType() == BuildingType.PERSONAL_FIELD)
						addressHouseIds.put(address.getId(), houseId);
				}

				house.setOwnerId(rset.getInt("player_id"));
				house.setAcquiredTime(rset.getTimestamp("acquire_time"));
				house.setSettingFlags(rset.getInt("settings"));
				house.setStatus(HouseStatus.valueOf(rset.getString("status")));
				house.setFeePaid(rset.getInt("fee_paid") != 0);
				house.setNextPay(rset.getTimestamp("next_pay"));
				house.setSellStarted(rset.getTimestamp("sell_started"));

				int id = studios ? house.getOwnerId() : address.getId();
				houses.put(id, house);
			}
			rset.close();
		}
		catch (Exception e) {
			log.error("Could not restore House data from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(stmt, con);
		}
		return houses;
	}

   @Override
	public void deleteHouse(int playerId) {
		Connection con = null;

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_HOUSE_QUERY);

			stmt.setInt(1, playerId);
			stmt.execute();
		}
		catch (SQLException e) {
			log.error("Delete House failed", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}
}