package mysql5;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.dao.ItemCooldownsDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemCooldown;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ATracer
 */
public class MySQL5ItemCooldownsDAO extends ItemCooldownsDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5ItemCooldownsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `item_cooldowns` (`player_id`, `delay_id`, `use_delay`, `reuse_time`) VALUES (?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `item_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `delay_id`, `use_delay`, `reuse_time` FROM `item_cooldowns` WHERE `player_id`=?";

	private static final Predicate<ItemCooldown> itemCooldownPredicate = new Predicate<ItemCooldown>() {
		@Override
		public boolean apply(@Nullable ItemCooldown input) {
			return input != null && input.getReuseTime() - System.currentTimeMillis() > 30000;
		}
	};


	@Override
	public void loadItemCooldowns(final Player player) {
		DB.select(SELECT_QUERY, new ParamReadStH() {

			@Override
			public void setParams(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, player.getObjectId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException {
				while (rset.next()) {
					int delayId = rset.getInt("delay_id");
					int useDelay = rset.getInt("use_delay");
					long reuseTime = rset.getLong("reuse_time");

					if (reuseTime > System.currentTimeMillis())
						player.addItemCoolDown(delayId, reuseTime, useDelay);

				}
			}
		});
		player.getEffectController().broadCastEffects();
	}

	@Override
	public void storeItemCooldowns(Player player) {
		deleteItemCooldowns(player);
		Map<Integer, ItemCooldown> itemCoolDowns = player.getItemCoolDowns();

		if (itemCoolDowns == null)
			return;

		Map<Integer, ItemCooldown> map = Maps.filterValues(itemCoolDowns, itemCooldownPredicate);
		final Iterator<Map.Entry<Integer, ItemCooldown>> iterator = map.entrySet().iterator();
		if(!iterator.hasNext()){
			return;
		}

		Connection con = null;
		PreparedStatement st = null;
		try{
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			st = con.prepareStatement(INSERT_QUERY);
			
			while(iterator.hasNext()){
				Map.Entry<Integer, ItemCooldown> entry = iterator.next();
				st.setInt(1, player.getObjectId());
				st.setInt(2, entry.getKey());
				st.setInt(3, entry.getValue().getUseDelay());
				st.setLong(4, entry.getValue().getReuseTime());
				st.addBatch();
			}

			st.executeBatch();
			con.commit();
		} catch (SQLException e) {
			log.error("Error while storing item cooldows for player " + player.getObjectId(), e);
		} finally {
			DatabaseFactory.close(st, con);
		}
	}

	private void deleteItemCooldowns(final Player player) {
		DB.insertUpdate(DELETE_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, player.getObjectId());
				stmt.execute();
			}
		});
	}

	@Override
	public boolean supports(String arg0, int arg1, int arg2) {
		return MySQL5DAOUtils.supports(arg0, arg1, arg2);
	}
}