import com.csz.model.Enemy;
import com.csz.model.Player;
import com.csz.model.Role;
import com.csz.model.User;
import com.csz.model.skillClass.GroupAttack;
import com.csz.model.skillClass.HeavyAttack;
import com.csz.ui.Game;
import com.csz.ui.event.Battle;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
//        LogIn logIn = new LogIn();
//        logIn.start();

        Game game = new Game();
        Player player = new Player(new User("csz", "csz123"), 51, 100, 30, 100, 5, 10);
        player.addSkillList(new HeavyAttack());
        player.addSkillList(new GroupAttack());

        ArrayList<Role> roles = new ArrayList<Role>(List.of(new Enemy("小怪1"), new Enemy("小怪2"), new Enemy("小怪3")));
        Battle battle = new Battle(player, roles);

        battle.encounterBattle();

//        Battle.roleAttributeUI(player);
//        Battle.roleAttributeUI(new Enemy());
    }
}
