package mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.dao.ChallengeTasksDAO;
import com.aionemu.gameserver.dao.MySQL5DAOUtils;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.challenge.ChallengeQuest;
import com.aionemu.gameserver.model.challenge.ChallengeTask;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.templates.challenge.ChallengeQuestTemplate;
import com.aionemu.gameserver.model.templates.challenge.ChallengeType;

/**
 * @author ViAl
 */
public class MySQL5ChallengeTasksDAO extends ChallengeTasksDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5ChallengeTasksDAO.class);

	private static final String SELECT_QUERY = "SELECT * FROM `challenge_tasks` WHERE `owner_id` = ? AND `owner_type` = ?";
	private static final String INSERT_QUERY = "INSERT INTO `challenge_tasks` (`task_id`, `quest_id`, `owner_id`, `owner_type`, `complete_count`, `complete_time`) VALUES (?, ?, ?, ?, ?, ?);";
	private static final String UPDATE_QUERY = "UPDATE `challenge_tasks` SET `complete_count` = ?, `complete_time`= ? WHERE `task_id` = ? AND `quest_id` = ? AND `owner_id` = ?";

	@Override
	public Map<Integer, ChallengeTask> load(int ownerId, ChallengeType type) {
		FastMap<Integer, ChallengeTask> tasks = new FastMap<Integer, ChallengeTask>().shared();
		Connection conn = null;
		try {
			conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, ownerId);
			stmt.setString(2, type.toString());
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int taskId = rset.getInt("task_id");
				int questId = rset.getInt("quest_id");
				int completeCount = rset.getInt("complete_count");
				Timestamp date = rset.getTimestamp("complete_time");
				ChallengeQuestTemplate template = DataManager.CHALLENGE_DATA.getQuestByQuestId(questId);
				ChallengeQuest quest = new ChallengeQuest(template, completeCount);
				quest.setPersistentState(PersistentState.UPDATED);
				if (!tasks.containsKey(taskId)) {
					Map<Integer, ChallengeQuest> quests = new HashMap<Integer, ChallengeQuest>(2);
					quests.put(quest.getQuestId(), quest);
					ChallengeTask task = new ChallengeTask(taskId, ownerId, quests, date);
					tasks.put(taskId, task);
				}
				else {
					tasks.get(taskId).getQuests().put(questId, quest);
				}
			}
			rset.close();
			stmt.close();
		}
		catch (SQLException e) {
			log.error("Error while loading challenge task. " + e);
		}
		finally {
			DatabaseFactory.close(conn);
		}
		return tasks;
	}

	@Override
	public void storeTask(ChallengeTask task) {
		for (ChallengeQuest quest : task.getQuests().values()) {
			switch (quest.getPersistentState()) {
				case NEW:
					insertQuestEntry(task, quest);
					break;
				case UPDATE_REQUIRED:
					updateQuestEntry(task, quest);
					break;
			}
		}
	}

	private void insertQuestEntry(ChallengeTask task, ChallengeQuest quest) {
		Connection conn = null;
		try {
			conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, task.getTaskId());
			stmt.setInt(2, quest.getQuestId());
			stmt.setInt(3, task.getOwnerId());
			stmt.setString(4, task.getTemplate().getType().toString());
			stmt.setInt(5, quest.getCompleteCount());
			stmt.setTimestamp(6, task.getCompleteTime());
			stmt.executeUpdate();
			stmt.close();
			quest.setPersistentState(PersistentState.UPDATED);
		}
		catch (SQLException e) {
			log.error("Error while inserting challenge task. " + e);
		}
		finally {
			DatabaseFactory.close(conn);
		}
	}

	private void updateQuestEntry(ChallengeTask task, ChallengeQuest quest) {
		Connection conn = null;
		try {
			conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, quest.getCompleteCount());
			stmt.setTimestamp(2, task.getCompleteTime());
			stmt.setInt(3, task.getTaskId());
			stmt.setInt(4, quest.getQuestId());
			stmt.setInt(5, task.getOwnerId());
			stmt.executeUpdate();
			stmt.close();
			quest.setPersistentState(PersistentState.UPDATED);
		}
		catch (SQLException e) {
			log.error("Error while updating challenge task. " + e);
		}
		finally {
			DatabaseFactory.close(conn);
		}
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}