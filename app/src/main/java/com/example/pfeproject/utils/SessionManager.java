package com.example.pfeproject.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.example.pfeproject.model.Command;
import com.example.pfeproject.model.Panier;
import com.example.pfeproject.model.Product;
import com.example.pfeproject.model.TotalPoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class SessionManager {
    private static final String TAG = "TAG_manager";

    private static final String userTotalPointKey = "UserPointList";
    private static final String userTotalBasketShopKey = "UserBasketList";
    private static final String userCommandKey = "UserCommandList";

    private Editor editor;
    private SharedPreferences prefs;
    private Context _context;
    private String token = "";
    private boolean logged = false;
    private int PRIVATE_MODE = 0;
    private ArrayList<TotalPoint> points;
    private ArrayList<Panier> userBasketShop;

    public SessionManager(Context _context) {
        this._context = _context;
        prefs = _context.getSharedPreferences(Types.MyPREFERENCES, PRIVATE_MODE);
        editor = prefs.edit();
    }

    public void CreateUserSession(String token, String user_id) {
        editor.putBoolean(Types.logged, true);
        editor.putString(Types.userToken, token);
        editor.putString(Types.userId, user_id);
        editor.apply();
    }

    public void InitiateNotLoggedUser() {
        editor.putBoolean(Types.logged, false);
        editor.apply();
    }

    public void UserLogOut() {
        editor.clear();
        editor.apply();
        InitiateNotLoggedUser();
    }


    //  ----------------------------------------------  start manage user Total point
    public void saveOrUpdateUserPointList(ArrayList<TotalPoint> pointArrayList) {
//        1 : panier.size() > 0 :=> user point lezem yetna7aw
//        restpoint = getUserTotalPointPerEntreprise - panier.getUserTotalPointPerEntreprise
//        2 : panier.size() == 0
//        restpoint = getUserTotalPointPerEntreprise
        ArrayList<Panier> paniers = retrieveUserBasketShop();
        Command userLastCommand = null;
        TotalPoint userPointFromCmd = null;
//        2 :
//        if pointArrayList is null => update else save
        Log.d(TAG, "saveOrUpdateUserPointList:  saveOrUpdateUserPointList ? " + (pointArrayList == null ? "update" : "save"));
        if (pointArrayList == null)
            pointArrayList = retrieveUserPoints();

        if (paniers.size() == 0) {
            if (pointArrayList.size() > 0) {
//                2.1 : check if he has command saved => last item has user Rest point
                if (retrieveUserCommand().size() > 0) {
                    userLastCommand = retrieveUserCommand().get(retrieveUserCommand().size() - 1);
                    if (userLastCommand != null) {
                        Log.d(TAG, "saveOrUpdateUserPointList: userLastCmd != null ");
                        for (TotalPoint totalPoint : pointArrayList) {
//                        entreprise exist in last command => retrieve entrprise rest point
                            if (retrieveUserPointFromLastCommandByEntrId(totalPoint.getIdEntreprise()) != null) {
//                                rest point = ( newTotalPoint - oldTotalPoint ) + restPoint
//                                exp rest = (20 000 - 15 000) + 7 000
                                TotalPoint entTotalPoint = retrieveUserPointFromLastCommandByEntrId(totalPoint.getIdEntreprise());
                                int difftTotal = 0;
                                int userRestPoint = 0;
                                Log.i(TAG, "saveOrUpdateUserPointList: \nInteger.parseInt(totalPoint.getTotalpoints()) = "+totalPoint.getTotalpoints()+
                                        " >= Integer.parseInt(entTotalPoint.getTotalpoints()) ="+entTotalPoint.getTotalpoints());

                                if (Integer.parseInt(totalPoint.getTotalpoints()) >= Integer.parseInt(entTotalPoint.getTotalpoints())) {
                                    difftTotal = Integer.parseInt(totalPoint.getTotalpoints()) - Integer.parseInt(entTotalPoint.getTotalpoints());
                                    userRestPoint = difftTotal + entTotalPoint.getRestpoints();
                                    Log.d(TAG, "saveOrUpdateUserPointList: ........new >= old => \ndiff = " + difftTotal + " \nrest = " + userRestPoint);
                                }else {
                                    difftTotal = Integer.parseInt(totalPoint.getTotalpoints());
                                    userRestPoint =  entTotalPoint.getRestpoints(); //difftTotal+ ;
                                    Log.i(TAG, "saveOrUpdateUserPointList: entTotalPoint.getRestpoints() = "+entTotalPoint.getRestpoints());
                                    Log.d(TAG, "saveOrUpdateUserPointList: ........new < old => \ndiff = " + difftTotal + " \nrest = " + userRestPoint);
                                }

                                totalPoint.setRestpoints(userRestPoint);
                                Log.d(TAG, "saveOrUpdateUserPointList: userRestPoint " + totalPoint.getRestpoints());
                            } else
                                totalPoint.setRestpoints(Integer.parseInt(totalPoint.getTotalpoints()));
                            Log.d(TAG, "saveOrUpdateUserPointList: userLastCommand != null && totalPoint.setRestpoints = " + totalPoint.getRestpoints());
                        }
                    } else {
                        for (TotalPoint totalPoint : pointArrayList)
                            totalPoint.setRestpoints(Integer.parseInt(totalPoint.getTotalpoints()));
                        Log.d(TAG, "saveOrUpdateUserPointList: userLastCommand == null && totalPoint size = " + pointArrayList.size());
                    }
                } else {
                    for (TotalPoint totalPoint : pointArrayList)
                        totalPoint.setRestpoints(Integer.parseInt(totalPoint.getTotalpoints()));
                }

                Log.d(TAG, "saveOrUpdateUserPointList: panier.size = 0  pointarray.size = " + pointArrayList.size());
            } else
                Log.d(TAG, "saveOrUpdateUserPointList: panier.size = 0  pointarray.size = 0 ");
        } else
//        1 :
        {
            for (TotalPoint totalPoint : pointArrayList) {
                Panier p = retrieveUserBasketShopItem(totalPoint.getIdEntreprise());
                int sumTotalProductPoint = 0;
                int restUserPointPerEntreprise = 0;

                if (retrieveUserCommand().size() > 0) {
                    userLastCommand = retrieveUserCommand().get(retrieveUserCommand().size() - 1);
                    userPointFromCmd = retrieveUserPointFromLastCommandByEntrId(totalPoint.getIdEntreprise());
                }
                if (p != null) {
                    for (Product userBasketProduct : p.getProducts())
                        sumTotalProductPoint += userBasketProduct.getDiscountPoints();

                    if (userPointFromCmd != null) {
                        int difftTotal = Integer.parseInt(totalPoint.getTotalpoints()) - Integer.parseInt(userPointFromCmd.getTotalpoints());
                        restUserPointPerEntreprise = difftTotal + userPointFromCmd.getRestpoints() - sumTotalProductPoint;

                        if (Integer.parseInt(totalPoint.getTotalpoints()) < Integer.parseInt(userPointFromCmd.getTotalpoints())) {
                            difftTotal = Integer.parseInt(totalPoint.getTotalpoints());
                            restUserPointPerEntreprise =( difftTotal - userPointFromCmd.getRestpoints()) < 0 ? difftTotal :  difftTotal - userPointFromCmd.getRestpoints();
                            Log.d(TAG, "saveOrUpdateUserPointList: ........new < old => \ndiff = " + difftTotal + " \nrest = " + restUserPointPerEntreprise);
                        }
//                        restUserPointPerEntreprise = ( Integer.parseInt(totalPoint.getTotalpoints()) - Integer.parseInt(userPointFromCmd.getTotalpoints()) ) + (userPointFromCmd.getRestpoints() - sumTotalProductPoint);
                        Log.d(TAG, "saveOrUpdateUserPointList: panier.size > 0 & p!= null userPointFromCmd != null \nsomme =  " + sumTotalProductPoint + " total= " + totalPoint.getTotalpoints() + " rest = " + restUserPointPerEntreprise);
                    } else {
                        restUserPointPerEntreprise = Integer.parseInt(totalPoint.getTotalpoints()) - sumTotalProductPoint;
                        Log.d(TAG, "saveOrUpdateUserPointList: panier.size > 0 & p!= null userPointFromCmd == null  \nsomme =  " + sumTotalProductPoint + " total= " + totalPoint.getTotalpoints() + " rest = " + restUserPointPerEntreprise);
                    }

                    totalPoint.setRestpoints(restUserPointPerEntreprise);

                } else if (userPointFromCmd != null)
                {
                    int difftTotal = Integer.parseInt(totalPoint.getTotalpoints()) - Integer.parseInt(userPointFromCmd.getTotalpoints());
                    restUserPointPerEntreprise = difftTotal + userPointFromCmd.getRestpoints();

                    if (Integer.parseInt(totalPoint.getTotalpoints()) < Integer.parseInt(userPointFromCmd.getTotalpoints())) {
                        difftTotal = Integer.parseInt(totalPoint.getTotalpoints());
                        restUserPointPerEntreprise = (difftTotal - userPointFromCmd.getRestpoints() )< 0 ? difftTotal : difftTotal - userPointFromCmd.getRestpoints() ;
                        Log.d(TAG, "saveOrUpdateUserPointList: ........new < old => \ndiff = " + difftTotal + " \nrest = " + restUserPointPerEntreprise);
                    }

//                    restUserPointPerEntreprise = ( Integer.parseInt(totalPoint.getTotalpoints()) - Integer.parseInt(userPointFromCmd.getTotalpoints()) )  + userPointFromCmd.getRestpoints();
                    totalPoint.setRestpoints(restUserPointPerEntreprise);
                    Log.d(TAG, "saveOrUpdateUserPointList: panier.size > 0 & p== null userPointFromCmd != null \nentrId =  " + userPointFromCmd.getIdEntreprise() + "\nrestPoint =" + userPointFromCmd.getRestpoints());
                } else
                    totalPoint.setRestpoints(Integer.parseInt(totalPoint.getTotalpoints()));
            }
        }


        for (TotalPoint p : pointArrayList)
            Log.i(TAG, "saveOrUpdateUserPointList: AFTER.....................................\nE_id :" + p.getIdEntreprise() + "\ntotal :" + p.getTotalpoints() + "\nrest" + p.getRestpoints());

//        updateTotalPointWithEarned(pointArrayList);
        saveOnlyUserPoint(pointArrayList);
    }

    public void saveOnlyUserPoint(ArrayList<TotalPoint> points) {
        Gson gson = new Gson();
        String value = gson.toJson(points);
        editor.putString(userTotalPointKey, value);
        editor.apply();
    }

    public ArrayList<TotalPoint> retrieveUserPoints() {
//        if user has command with state "waiting" => all point user is saved in last item of command
//        command.size == 0 => no command found => get rest point from sharedPref

//        ArrayList<Command> commands = retrieveUserCommand();
//        Log.d(TAG, "retrieveUserPoints: .... check commands.size() " + commands.size());

//        if (commands.size() > 0)
//            points = commands.get(commands.size() - 1).getTotalPointsPerEntreprise();
//        else {
        String json = prefs.getString(userTotalPointKey, "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<TotalPoint>>() {
        }.getType();
        points = gson.fromJson(json, type);
        if (points == null)
            points = new ArrayList<>();
//        }
        return points;
    }

    public TotalPoint retrieveUserEntreprisePoint(String entreprise_id) {
        ArrayList<TotalPoint> points = retrieveUserPoints();
        TotalPoint totalPoint = null;
        if (points.size() > 0) {
            for (TotalPoint point : points) {
                if (point.getIdEntreprise().equals(entreprise_id))
                    totalPoint = point;
            }
            if (totalPoint != null)
                Log.d(TAG, "retrieveUserEntreprisePoint:\ntotal point EsId = " + totalPoint.getIdEntreprise() + "\ntotal point =" + totalPoint.getTotalpoints() + "\nrestPoint =" + totalPoint.getRestpoints());
        }
        return totalPoint;
    }

    public int retrieveUserTotalRestPoint() {
        ArrayList<TotalPoint> points = retrieveUserPoints();
        int total = 0;
        if (points.size() > 0) {
            for (TotalPoint point : points)
                total += point.getRestpoints();
        }
        Log.d(TAG, "retrieveUserTotalRestPoint: total =" + total);
        return total;
    }

    public int retrieveUserEntreprisePointPositionByEntId(String entreprise_id) {
        ArrayList<TotalPoint> points = retrieveUserPoints();
        int position = -1;
        if (points.size() > 0) {
            for (int i = 0; i < points.size(); i++) {
                if (points.get(i).getIdEntreprise().equals(entreprise_id))
                    position = i;
            }
        }
        return position;
    }

    //  ----------------------------------------------  end manage user Total point


    //  ----------------------------------------------  start manage user Command

    public void checkAndSaveUserCommand(Command[] commands, int total) {
        try {
            Log.i(TAG, "checkAndSaveUserCommand: ...");
//        retrieve existed command list "savedCommand"
//        get "commands" user command list (all) from api
            ArrayList<Command> savedCommand = retrieveUserCommand();
            ArrayList<TotalPoint> userTotalPoints = retrieveUserPoints();
            Command myCommand = null;
//        if command !exist in shared (myCommand == null && cmd state == waiting) => add new Command
//        if command exist in shared (myCommand != null)=> update command state
//          if command state == confirm / cancled remove command from array

                for (Command cmd : commands) {
                    myCommand = retrieveCommandById(cmd.getId());
                    if (myCommand == null && cmd.getStatus().equals(Types.InProgress)) {
//                update cmd total point

                        myCommand = cmd;
                        myCommand.setTotalPointsPerEntreprise(userTotalPoints);
                        myCommand.setCmdTotalPoint(total);
                        savedCommand.add(myCommand);
                        Log.d(TAG, "checkAndSaveUserCommand: cmd.setTotalPointsPerEntreprise(userTotalPoints) .size " + userTotalPoints.size());
                    } else {
                        if ((cmd.getStatus().equals(Types.Confirmed) || cmd.getStatus().equals(Types.Cancled)) && (retrieveCommandPositionById(cmd.getId()) != -1)) {
                            savedCommand.remove(retrieveCommandPositionById(cmd.getId()));
                            Log.i(TAG, "checkAndSaveUserCommand: commmand removed ...");
                        }

                    }
                }
                saveUserCommandStat(savedCommand);
        } catch (Exception e) {
            Log.e(TAG, "checkAndSaveUserCommand: Exception ..." + e.getMessage());
        }

    }

    public void saveUserCommandStat(ArrayList<Command> commands) {
        Gson gson = new Gson();
        String json = gson.toJson(commands);
        editor.putString(userCommandKey, json);
        editor.apply();
    }

    public TotalPoint retrieveUserPointFromLastCommandByEntrId(String entreprise_id) {
        TotalPoint point = null;
        try {
            ArrayList<Command> cmd = retrieveUserCommand();
            if (cmd.size() > 0) {
                Log.i(TAG, "retrieveUserPointFromLastCommandByEntrId: size here = " + cmd.size());
                Command command = cmd.get(cmd.size() - 1);
                Log.i(TAG, "retrieveUserPointFromLastCommandByEntrId: .................. command == null "+(command == null));
                if (command.getTotalPointsPerEntreprise() != null) {
                    for (TotalPoint totalPoint : command.getTotalPointsPerEntreprise())
                        if (totalPoint.getIdEntreprise().equals(entreprise_id))
                            point = totalPoint;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "retrieveUserPointFromLastCommandByEntrId: Exception " + e.getMessage());
        }
        Log.i(TAG, "retrieveUserPointFromLastCommandByEntrId: point = "+point);
        return point;
    }

    public Command retrieveCommandById(String cmdId) {
        Command existCmd = null;
        ArrayList<Command> cmd = retrieveUserCommand();
        for (int i = 0; i < cmd.size(); i++) {
            if (cmd.get(i).getId().equals(cmdId))
                existCmd = cmd.get(i);
        }
        return existCmd;
    }

    public int retrieveCommandPositionById(String cmdId) {
        int position = -1;
        ArrayList<Command> cmd = retrieveUserCommand();
        for (int i = 0; i < cmd.size(); i++) {
            if (cmd.get(i).getId().equals(cmdId))
                position = i;
        }
        return position;
    }

    public ArrayList<Command> retrieveUserCommand() {
        String json = prefs.getString(userCommandKey, "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Command>>() {
        }.getType();
        ArrayList<Command> commands = gson.fromJson(json, type);
        if (commands == null)
            commands = new ArrayList<>();
        return commands;
    }

    public int retrieveCommandTotalPointById(String cmdId) {
        int total = 0;
        ArrayList<Command> commands = retrieveUserCommand();
        if (commands.size() > 0) {
            for (Command c : commands)
                if (c.getId().equals(cmdId))
                    total = c.getCmdTotalPoint();
        }
        return total;
    }

    public void deleteUnusedCommand(Command[] userCommand) {
        ArrayList<Command> saveCmd = retrieveUserCommand();
        if (saveCmd.size() > 0) {
            Log.d(TAG, "deleteUnusedCommand: array of command size before .......... = " + saveCmd.size());
            for (Command userCmd : userCommand) {
                Log.d(TAG, "deleteUnusedCommand: position of command ..... =" + retrieveCommandPositionById(userCmd.getId()));
                Log.i(TAG, "deleteUnusedCommand: condition to remove ......... = " + ((userCmd.getStatus().equals(Types.Confirmed) || userCmd.getStatus().equals(Types.Cancled)) && (retrieveCommandPositionById(userCmd.getId()) != -1)));
                if ((userCmd.getStatus().equals(Types.Confirmed) || userCmd.getStatus().equals(Types.Cancled)) && (retrieveCommandPositionById(userCmd.getId()) != -1))
                    saveCmd.remove(retrieveCommandPositionById(userCmd.getId()));
            }
            Log.d(TAG, "deleteUnusedCommand: array of command size after .......... = " + saveCmd.size());
            saveUserCommandStat(saveCmd);
        } else
//            if saveCmd.size == 0 & userCommand.length > 0 => no saved command in shared pref => save
//        else remove confirmed or cancled cmd
            if (userCommand.length > 0) {
                saveCmd = new ArrayList<>();
                for (Command c : userCommand) {
                    if (c.getStatus().equals(Types.InProgress))
                        saveCmd.add(c);
                }
                saveUserCommandStat(saveCmd);
            }
    }

    //  ----------------------------------------------  end manage user Command

    //  ----------------------------------------------  start manage user basket shop
    public void saveUserBasketShopList(ArrayList<Panier> paniers) {
        Gson gson = new Gson();
        String json = gson.toJson(paniers);
        editor.putString(userTotalBasketShopKey, json);
        editor.apply();
    }

    public ArrayList<Panier> retrieveUserBasketShop() {
        Log.d(TAG, "retrieveUserBasketShop: ..");
        String json = prefs.getString(userTotalBasketShopKey, "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Panier>>() {
        }.getType();
        ArrayList<Panier> userBasketShop = gson.fromJson(json, type);
        if (userBasketShop == null)
            userBasketShop = new ArrayList<>();
        return userBasketShop;
    }

    public int retrievebasketShopTotalPoint() {
        int total = 0;
        ArrayList<Panier> userPanier = retrieveUserBasketShop();
        if (userPanier.size() > 0) {
            for (Panier item : userPanier) {
                for (Product productItem : item.getProducts()) {
                    total += productItem.getDiscountPoints();
                    Log.d(TAG, "retrievebasketShopTotalPoint: total point ... " + total);
                }
            }
        }
        return total;
    }

    public Panier retrieveUserBasketShopItem(String entreprise_id) {
        ArrayList<Panier> basketShop = retrieveUserBasketShop();
        Panier panier = null;
        if (basketShop.size() > 0) {
            for (Panier userPanierItem : basketShop) {
                if (userPanierItem.getEntreprise_id().equals(entreprise_id))
                    panier = userPanierItem;
            }
        }
        return panier;
    }

    public int retrievePositionOfUserBasketShopItem(String entreprise_id) {
        ArrayList<Panier> basketShop = retrieveUserBasketShop();
        int position = -1;
        if (basketShop.size() > 0) {
            for (int i = 0; i < basketShop.size(); i++) {
                if (basketShop.get(i).getEntreprise_id().equals(entreprise_id))
                    position = i;
            }
        }
        return position;
    }

    public boolean AddItemToBasketShop(String entreprise_id, String entreprise_name, Product product) {
        boolean articleExist = false;
        boolean success = false;
//        2 cas :
//        1er entreprise exist => add product fel list product where panier.entreprise_id = givenEntreprise_id
//        mais : article exist ou nn ???
//        1.1 exist : Toast else 1.2 : addArticle
//        2er entreprise !exist => new basket shop object

        try {
            Panier userBasketItem = retrieveUserBasketShopItem(entreprise_id);
//          1 :
            if (userBasketItem != null) {
                Log.d(TAG, "AddItemToBasketShop: userBasketItem != VIDE before change " + userBasketItem.getProducts().length);
                Product[] userBasketExistedProduct = userBasketItem.getProducts();
                for (Product p : userBasketExistedProduct) {
                    if (Integer.parseInt(p.getId()) == Integer.parseInt(product.getId()))
                        articleExist = true;
                }
                Log.d(TAG, "AddItemToBasketShop: item exist: " + articleExist + " !exist " + !articleExist);
                if (articleExist == false) {
                    Log.i(TAG, "AddItemToBasketShop: userBasketItem != VIDE ...... " + userBasketItem.getProducts().length);
                    ArrayList<Product> ItemListProduct = new ArrayList<>();
                    Collections.addAll(ItemListProduct, userBasketItem.getProducts());
                    ItemListProduct.add(product);

                    Product[] products = new Product[ItemListProduct.size()];
                    products = ItemListProduct.toArray(products);

                    userBasketItem.setProducts(products);
//                   userBasketExistedProduct[userBasketExistedProduct.length] = product;
                    ArrayList<Panier> paniers = retrieveUserBasketShop();
                    paniers.set(retrievePositionOfUserBasketShopItem(entreprise_id), userBasketItem);
                    saveUserBasketShopList(paniers);

                    Log.i(TAG, "AddItemToBasketShop: userBasketItem != VIDE after change " + userBasketItem.getProducts().length + "\npanier size " + paniers.size());
                    success = true;
                }

            } else {
//            2 :
                Log.d(TAG, "AddItemToBasketShop: userBasketItem => VIDE before change ");
                Panier panier = new Panier(entreprise_id, entreprise_name, new Product[]{product});
                ArrayList<Panier> userBasketShop = retrieveUserBasketShop();
                userBasketShop.add(panier);

                saveUserBasketShopList(userBasketShop);
                Log.d(TAG, "AddItemToBasketShop: userBasketItem => VIDE after change Product " + panier.getProducts().length);
                success = true;
            }
            Log.e(TAG, "AddItemToBasketShop: success = " + success);
            if (success) {
//            update userRestPointPerEntreprise = oldRest - newProduct
                TotalPoint userPoint = retrieveUserEntreprisePoint(entreprise_id);
                if (userPoint != null) {
                    int restPoint = userPoint.getRestpoints() - retrieveUserBasketShopItem(entreprise_id).getProducts()[retrieveUserBasketShopItem(entreprise_id).getProducts().length - 1].getDiscountPoints();
//                            userBasketItem.getProducts()[userBasketItem.getProducts().length - 1].getDiscountPoints();
                    userPoint.setRestpoints(restPoint);
                    Log.d(TAG, "AddItemToBasketShop: userPoint.setRestpoints = " + restPoint);
                    Log.d(TAG, "AddItemToBasketShop: entreprise_id " + entreprise_id);

                    ArrayList<TotalPoint> totalPoints = retrieveUserPoints();
//             add 09       totalPoints.set(retrievePositionOfUserBasketShopItem(entreprise_id), userPoint);
                    totalPoints.set(retrieveUserEntreprisePointPositionByEntId(entreprise_id), userPoint);
                    saveOrUpdateUserPointList(totalPoints);

                } else {
                    Log.e(TAG, "AddItemToBasketShop: userPoint is NULL ");
                }

            } else
                Log.e(TAG, "AddItemToBasketShop: !sucess");


        } catch (Exception e) {
            Log.e(TAG, "AddItemToBasketShop: Exception e = " + e.getMessage());
        }
        return success;
    }

    public void removeBasketShopItem(String entreprise_id, String product_id) {
        Panier itemToRemove = retrieveUserBasketShopItem(entreprise_id);
        boolean remove = false;
        int productToRemovePoint = 0;
        Log.d(TAG, "removeBasketShopItem: is null ? " + (itemToRemove == null));
        if (itemToRemove != null) {
//            get Product position from list product => update itemToremoveLiSTproduct
            ArrayList<Panier> userPanier = retrieveUserBasketShop();
            if (itemToRemove.getProducts().length == 1) {
                productToRemovePoint = itemToRemove.getProducts()[0].getDiscountPoints();
                removeShopItemFromShopBasket(entreprise_id);
            } else {
                productToRemovePoint = itemToRemove.getProducts()[retrievePositionOfUserBasketShopItem(entreprise_id)].getDiscountPoints();
                Log.d(TAG, "removeBasketShopItem: item removed position ...." + retrievePositionOfUserBasketShopItem(entreprise_id));
                removeProductItemFromBasketShop(itemToRemove, product_id, retrievePositionOfUserBasketShopItem(entreprise_id));
            }
//            update userRestPoint= oldPoint+productPoint
//            ArrayList<TotalPoint> totalPointArrayList = retrieveUserPoints();
            TotalPoint userTotalPoint = retrieveUserEntreprisePoint(entreprise_id);

            Log.d(TAG, "removeBasketShopItem: user old restPoint = " + userTotalPoint.getRestpoints());

            int restPoint = userTotalPoint.getRestpoints() + productToRemovePoint;
            userTotalPoint.setRestpoints(restPoint);

            Log.d(TAG, "removeBasketShopItem: user new rest point " + restPoint);
            Log.d(TAG, "removeBasketShopItem: Total \nproduct id = " + product_id + " .. \nproductToRemovePoint = " + productToRemovePoint +
                    " ... \nitem array length " + itemToRemove.getProducts().length + " ... rest point = " + restPoint);
            Log.d(TAG, "removeBasketShopItem: panierarraySize = " + retrieveUserBasketShop().size());

            saveOrUpdateUserPointList(null);
        }

    }

    public void removeShopItemFromShopBasket(String entreprise_id) {
        ArrayList<Panier> paniers = retrieveUserBasketShop();
        Log.d(TAG, "removeItemFromShopBasket: panier size before " + paniers.size());
        for (int i = 0; i < paniers.size(); i++) {
            if (paniers.get(i).getEntreprise_id().equals(entreprise_id))
                paniers.remove(i);
        }
        saveUserBasketShopList(paniers);
        Log.d(TAG, "removeItemFromShopBasket: panier size after " + paniers.size());

    }

    public void removeProductItemFromBasketShop(Panier itemToRemove, String product_id, int item_position) {
//        array of item shop basket of unique entreprise
        ArrayList<Product> ItemListProduct = new ArrayList<>();
        Log.d(TAG, "removeProductItemFromBasketShop: position " + (retrieveUserBasketShop().indexOf(itemToRemove)));
        Collections.addAll(ItemListProduct, itemToRemove.getProducts());
        Log.d(TAG, "removeProductItemFromBasketShop: ItemListProduct length before change " + ItemListProduct.size());
//        search product to remove
        for (int i = 0; i < ItemListProduct.size(); i++) {
            if (ItemListProduct.get(i).getId().equals(product_id))
                ItemListProduct.remove(i);
        }
        Log.d(TAG, "removeProductItemFromBasketShop: ItemListProduct length after change " + ItemListProduct.size());
        Product[] products = new Product[ItemListProduct.size()];
        products = ItemListProduct.toArray(products);
        itemToRemove.setProducts(products);
        Log.d(TAG, "removeProductItemFromBasketShop: itemToRemove products length after change " + itemToRemove.getProducts().length);

        ArrayList<Panier> paniers = retrieveUserBasketShop();
        paniers.set(item_position, itemToRemove);
        Log.d(TAG, "removeProductItemFromBasketShop: final change panier size " + paniers.size());
        saveUserBasketShopList(paniers);


    }

    public void deleteAllItemInsideBasketShop() {
        Log.i(TAG, "deleteAllItemInsideBasketShop: ....");
        editor.remove(userTotalBasketShopKey);
        editor.apply();
    }
    //  ----------------------------------------------  end manage user basket shop


    public String getTokenUser() {
        return prefs.getString(Types.userToken, null);
    }

    public String getUserId() {
        return prefs.getString(Types.userId, null);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Types.logged, false);
    }

    public boolean FirstLoggedIn() {
        return prefs.contains(Types.logged);
    }


}
