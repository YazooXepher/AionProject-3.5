/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 * 
 * Credits goes to all Open Source Core Developer Groups listed below
 * Please do not change here something, ragarding the developer credits, except the "developed by XXXX".
 * Even if you edit a lot of files in this source, you still have no rights to call it as "your Core".
 * Everybody knows that this Emulator Core was developed by Aion Lightning 
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package quest.pernon;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 *
 */
public class _28830InteriorDecorator extends QuestHandler {

    private static final int questId = 28830;
    private static final Set<Integer> butlers;

    static {
        butlers = new HashSet<Integer>();
        butlers.add(810022);
        butlers.add(810023);
        butlers.add(810024);
        butlers.add(810025);
        butlers.add(810026);
    }

    public _28830InteriorDecorator() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(830585).addOnQuestStart(questId);

        Iterator<Integer> iter = butlers.iterator();
        while (iter.hasNext()) {
            int butlerId = iter.next();
            qe.registerQuestNpc(butlerId).addOnTalkEvent(questId);
        }
        qe.registerQuestNpc(830651).addOnTalkEvent(questId);
        qe.registerQuestHouseItem(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestDialog dialog = env.getDialog();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        House house = player.getActiveHouse();

        if (house == null) {
            return false;
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 830585) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case ACCEPT_QUEST:
                    case ACCEPT_QUEST_SIMPLE:
                        return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START && butlers.contains(targetId) && qs.getQuestVarById(0) == 0) {
            if (house.getButler().getNpcId() != targetId) {
                return false;
            }
            switch (dialog) {
                case USE_OBJECT:
                    return sendQuestDialog(env, 1352);
                case STEP_TO_1:
                    return defaultCloseDialog(env, 0, 1);
            }
        } else if (qs.getStatus() == QuestStatus.REWARD && targetId == 830651) {
            switch (dialog) {
                case USE_OBJECT:
                    return sendQuestDialog(env, 2375);
                case SELECT_REWARD:
                    return sendQuestDialog(env, 5);
                case SELECT_NO_REWARD:
                    sendQuestEndDialog(env);
                    return true;
			}
		}else if (qs.getStatus() == QuestStatus.START && targetId == 830651) {
			switch (dialog) {
				case USE_OBJECT:
					changeQuestStep(env, 1, 1, true);
					return sendQuestDialog(env, 2375);
				case SELECT_REWARD:
					return sendQuestDialog(env, 5);
				case SELECT_NO_REWARD:
					sendQuestEndDialog(env);
					return true;
            }
        }

        return false;
    }

    @Override
    public boolean onHouseItemUseEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
            changeQuestStep(env, 1, 1, true);
        }
        return false;
    }
}
