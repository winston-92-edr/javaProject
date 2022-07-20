package com.mynet.shared.resource.db.work;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.db.DataSource;
import com.mynet.shared.response.UserReceivedMoneyResponse;
import com.mynet.shared.user.User;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static com.mynet.shared.db.generated.tables.OverallScores.OVERALL_SCORES;

public class UpdateUserMoney implements Callable {
    Logger logger = LoggerFactory.getLogger(UpdateUserMoney.class);

    private final User user;
    private final long money;

    public UpdateUserMoney(User user, long money) {
        this.user = user;
        this.money = money;
    }

    @Override
    public Object call() throws Exception {

        HikariDataSource dataSource = DataSource.getDataSource().get();

        GameController controller = GameController.getInstance();
        GameUser gameUser = controller.getUser(user.getId());

        Long currentMoney = user.getUserModel().money;

        try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)){
            Record record = context.select(OVERALL_SCORES.MONEY)
                    .from(OVERALL_SCORES)
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(user.getId())))
                    .fetchOne();

            currentMoney = record.get(OVERALL_SCORES.MONEY);
            user.updateMoney(currentMoney);

            if( money < 0 &&  (currentMoney + money <0)){
                int tableId = user.getUserModel().tableID;
                if(tableId > 0) {

                    Table table = GameController.getInstance().getTable(tableId);
                    user.writeUserMoneyLog("MoneyError",table.getGameId(),money,System.currentTimeMillis());
                    int side = table.getGamerSide(user.getId());
                    if(side > 0) table.removeSide(side,"money",null);
                }else{
                    user.writeUserMoneyLog("MoneyError",-1,money,System.currentTimeMillis());
                }

                controller.sendNetworkMessage(gameUser,GameCommands.USER_RECEIVED_MONEY, NetworkMessage.getGson().toJson(new UserReceivedMoneyResponse(user.getUserModel().money)));

                return false;
            }

        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

        try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)){
            int isSuccess = context.update(OVERALL_SCORES)
                    .set(OVERALL_SCORES.MONEY, OVERALL_SCORES.MONEY.add(money))
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(user.getId())))
                    .execute();

            user.updateMoney(currentMoney + money);

            controller.sendNetworkMessage(gameUser,GameCommands.USER_RECEIVED_MONEY, NetworkMessage.getGson().toJson(new UserReceivedMoneyResponse(user.getUserModel().money)));

            return isSuccess == 1;
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }

        return true;
    }
}
