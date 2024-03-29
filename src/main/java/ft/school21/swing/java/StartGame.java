package ft.school21.swing.java;

import ft.school21.swing.java.controller.Controller;
import ft.school21.swing.java.database.Heroes;
import ft.school21.swing.java.database.ImplementDB;
import ft.school21.swing.java.model.GameActions;
import ft.school21.swing.java.model.Map;
import ft.school21.swing.java.model.PlayArmor.RagsArmor;
import ft.school21.swing.java.model.PlayHelm.RagsHelm;
import ft.school21.swing.java.model.Repositor.Classes;
import ft.school21.swing.java.model.Weapons.Bow;
import ft.school21.swing.java.view.ChoiceGame;
import ft.school21.swing.java.view.Console;
import ft.school21.swing.java.view.Gui;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

public class StartGame {

    private static ChoiceGame choiceGame;
//    private static SessionFactory sessionFactory = null;
    public static String commandGui;
    public static boolean gui;

    public static void Game(String arg)
    {
        if (arg.equals("console"))
        {
            choiceGame = new ChoiceGame(new Console());
            gui = false;
        }
        else
        {
            choiceGame = new ChoiceGame(new Gui());
            gui = true;
        }

        ArrayList<GameActions> players = null;
//        try {
//            sessionFactory = ImplementDB.getImplementDB().getFactory();
            players = ImplementDB.getImplementDB().getAllHeroesDB();

//        }
//        finally {
//            sessionFactory.close();
//        }
        Main.flagGui = false;
        choiceGame.getView().drawStartMenu();
//        while (true) {
//            if (Main.flagGui)
//                break;
//        }
        Inf();
        Main.flagGui = false;
        choiceGame.getView().ChoicePlayer(players);
        Inf();
        if (Main.flagGui == false) {
            Scanner scanner = new Scanner(System.in);
                GameActions newPl = CommandScanner(scanner.nextLine().toLowerCase(), players);
                if (newPl != null) {
                    WorkMap(newPl);
                }
                else
                {
                    System.err.println("invalid id");
                }
                scanner.close();
        }
        else
        {
            GameActions newPl = CommandScanner(commandGui, players);
            if (newPl != null) {
                WorkMap(newPl);
            }
        }
    }

    public static void Inf()
    {
        if (gui) {
            while (true) {
                if (Main.flagGui)
                    break;
            }
        }
    }

    private static void WorkMap(GameActions players) {
        Map map = new Map(players);
        Controller controller = new Controller();
        while (true)
        {
            int posx = players.getPosX();
            int posy = players.getPosY();
            if (players.getLevel() >= 5)
            {
                choiceGame.getView().gameOver();
                break;
            }
            Main.flagGui = false;
            choiceGame.getView().ShowMap(map, players);
            Inf();
            if (controller.MovePlayer(players, map, choiceGame))
            {
                if (map.getMapSymbol(players.getPosY(), players.getPosX()) == 'E')
                {
                    if (controller.ChoiceBattle(choiceGame))
                    {
                        Main.flagGui = false;
                        choiceGame.getView().WindowBattle();
                        Inf();
                        if (controller.RandomBattle(map, players, choiceGame))
                        {
                            choiceGame.getView().youWin();
                            map.setMapSymbol(players.getPosY(), players.getPosX(), '\u00b7');
                        }
                        else
                        {
                            choiceGame.getView().youDied();
                            ImplementDB.getImplementDB().DeleteHero(players.getId());
                            System.exit(-1);
                        }
                    }
                    else
                    {
                        choiceGame.getView().youRunAway();
                        players.setPosX(posx);
                        players.setPosY(posy);
                    }
                }
            }
        }
    }

    public static GameActions CommandScanner(String command, ArrayList<GameActions> players)
    {
        Controller controller = new Controller();
        if (command.equals("a"))
        {
            GameActions pl = new GameActions();
            Long max;
            try {
                max = players.get(0).getId();
                for (int i = 1; i < players.size(); ++i)
                {
                    if (max <  players.get(i).getId())
                        max = players.get(i).getId();
                }
                pl.setId(max + 1);
            }
            catch (IndexOutOfBoundsException e)
            {
                max = 1L;
                pl.setId(max);
            }
            try {
                Main.flagGui = false;
                choiceGame.getView().CreateNamePlayer();
                Inf();
                controller.EnterName(pl);
                Main.flagGui = false;
                choiceGame.getView().ChoiceRace();
                Inf();
                controller.EnterRace(pl);
                Main.flagGui = false;
                choiceGame.getView().ChoiceClass();
                Inf();
                controller.EnterClass(pl);
                Main.flagGui = false;
                pl.setPlayWeapon(new Bow());
                pl.setPlayArmor(new RagsArmor());
                pl.setPlayHelm(new RagsHelm());
                pl.setAttack(pl.getAttack());
                ImplementDB.getImplementDB().AddHeroDB(ParseActions(pl));
            }
            catch (InputMismatchException e)
            {
                System.err.println("invalid arguments");
                return null;
            }
            return pl;
        }
        else if (command.equals("b"))
        {
            choiceGame.getView().ChoiceDeletePlayer();
            controller.DeletePlayer(players);
        }
        else
        {
            try {
                int comInt = Integer.parseInt(command);
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getId() == comInt)
                    {
                        Main.flagGui = false;
                        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                        Validator validator = factory.getValidator();
                        Set<ConstraintViolation<GameActions>> violations = validator.validate(players.get(i));
                        if (!violations.isEmpty()) {
                            StringBuilder log = new StringBuilder();
                            for (ConstraintViolation<GameActions> errors : violations) {
                                log.append(errors.getMessage()).append("\n");
                            }
                            throw new ParametrsException(log.toString());
                        }
                        choiceGame.getView().DataPlayer(players.get(i));
                        Inf();
                        return players.get(i);
                    }
                }
//                System.exit(1);
            }
            catch (NumberFormatException e)
            {
//                System.exit(1);
                System.out.println("there are no letters in id");
            }
            catch (IllegalArgumentException e)
            {
                throw new ParametrsException("some parameters are null");
            }
        }
        return null;
    }

    public static Heroes ParseActions(GameActions player)
    {
        Heroes heroes = new Heroes();
        heroes.setId(player.getId());
        switch (player.getPlayRaces().getPlayName().toLowerCase())
        {
            case "darkelf":
                heroes.setRace("DarkElf");
                heroes.setAttack(player.getAttack() + player.getPlayRaces().getPlayAttack());
                heroes.setHit_points(player.getHP());
                heroes.setDefense(player.getPlayRaces().getDefense());
                break;
            case "dwarf":
                heroes.setRace("Dwarf");
                heroes.setAttack(player.getAttack() + player.getPlayRaces().getPlayAttack());
                heroes.setHit_points(player.getHP());
                heroes.setDefense(player.getPlayRaces().getDefense());
                break;
            case "elf":
                heroes.setRace("Elf");
                heroes.setAttack(player.getAttack() + player.getPlayRaces().getPlayAttack());
                heroes.setHit_points(player.getHP());
                heroes.setDefense(player.getPlayRaces().getDefense());
                break;
            case "human":
                heroes.setRace("Human");
                heroes.setAttack(player.getAttack() + player.getPlayRaces().getPlayAttack());
                heroes.setHit_points(player.getHP());
                heroes.setDefense(player.getPlayRaces().getDefense());
                break;
            case "orc":
                heroes.setRace("Orc");
                heroes.setAttack(player.getAttack() + player.getPlayRaces().getPlayAttack());
                heroes.setHit_points(player.getHP());
                heroes.setDefense(player.getPlayRaces().getDefense());
                break;
        }
        heroes.setName(player.getName());
        if (player.getPlayClasses().equals(Classes.Berserk))
            heroes.setClas("Berserk");
        else if (player.getPlayClasses().equals(Classes.Necromancer))
            heroes.setClas("Necromancer");
        else if (player.getPlayClasses().equals(Classes.Warrior))
            heroes.setClas("Warrior");
        else if (player.getPlayClasses().equals(Classes.Wizard))
            heroes.setClas("Wizard");
        heroes.setLevel(player.getLevel());
        heroes.setExperience(player.getExperience());
        if (!player.getPlayWeapon().getWeaponName().isEmpty()) {
            heroes.setWeapon(player.getPlayWeapon().getWeaponName());
            heroes.setAttack(heroes.getAttack() + player.getPlayWeapon().getWeaponAttack());
        }
        if (!player.getPlayArmor().getName().isEmpty()) {
            heroes.setArmor(player.getPlayArmor().getName());
            heroes.setDefense(heroes.getDefense() + player.getPlayArmor().getDefense());
        }
        if (!player.getPlayHelm().getName().isEmpty()) {
            heroes.setHelm(player.getPlayHelm().getName());
            heroes.setHit_points(heroes.getHit_points() + player.getPlayHelm().getStatesHP());
        }
        return heroes;
    }
}
