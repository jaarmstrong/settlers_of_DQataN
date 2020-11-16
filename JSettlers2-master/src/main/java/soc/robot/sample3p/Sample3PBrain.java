/*
 * Java Settlers - An online multiplayer version of the game Settlers of Catan
 * This file Copyright (C) 2017-2019 Jeremy D Monin <jeremy@nand.net>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The maintainer of this program can be reached at jsettlers@nand.net
 */
package soc.robot.sample3p;

import soc.game.*;
import soc.message.SOCMessage;
import soc.robot.SOCRobotBrain;
import soc.robot.SOCRobotClient;
import soc.robot.SOCRobotNegotiator;
import soc.util.CappedQueue;
import soc.util.SOCRobotParameters;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Sample of a trivially simple "third-party" subclass of {@link SOCRobotBrain}
 * Instantiated by {@link Sample3PClient}.
 *<P>
 * Trivial behavioral changes from standard {@code SOCRobotBrain}:
 *<UL>
 * <LI> When sitting down, greet the game members: {@link #setOurPlayerData()}
 * <LI> Reject trades unless we're offered clay or sheep: {@link #considerOffer(SOCTradeOffer)}
 *</UL>
 *
 * @author Jeremy D Monin
 * @since 2.0.00
 */
public class Sample3PBrain extends SOCRobotBrain
{
    /**
     * Standard brain constructor; for javadocs see
     * {@link SOCRobotBrain#SOCRobotBrain(SOCRobotClient, SOCRobotParameters, SOCGame, CappedQueue)}.
     */

    private Socket servercon;
    private DataInputStream serverin;
    private DataOutputStream serverout;


    public Sample3PBrain(SOCRobotClient rc, SOCRobotParameters params, SOCGame ga, CappedQueue<SOCMessage> mq)
    {
        super(rc, params, ga, mq);
    }

    /**
     * {@inheritDoc}
     *<P>
     * After the standard actions of {@link SOCRobotBrain#setOurPlayerData()},
     * sends a "hello" chat message as a sample action using {@link SOCRobotClient#sendText(SOCGame, String)}.
     *<P>
     * If the for-bots extra game option {@link SOCGameOption#K__EXT_BOT} was set at the server command line,
     * prints its value to {@link System#err}. A third-party bot might want to use that option's value
     * to configure its behavior or debug settings.
     *<P>
     *<B>I18N Note:</B> Robots don't know what languages or locales the human players can read:
     * It would be unfair for a bot to ever send text that the players must understand
     * for gameplay. So this sample bot's "hello" is not localized.
     */
    @Override
    public void setOurPlayerData()
    {
        /*
        try{
            servercon = new Socket("localhost", 2004);
            servercon.setSoTimeout(300000);
            serverin = new DataInputStream(servercon.getInputStream());
            serverout = new DataOutputStream(servercon.getOutputStream());
            serverout.writeUTF("I just set player's data?");
            serverout.flush();
            serverout.close();  
            servercon.close();
            }
         catch(Exception e){
            System.err.println("Whoops!");
         }
         */   

        super.setOurPlayerData();

        final String botName = client.getNickname();
        client.sendText(game, "Hello from sample bot " + botName + "!");

        final String optExtBot = game.getGameOptionStringValue(SOCGameOption.K__EXT_BOT);
        if (optExtBot != null)
            System.err.println("Bot " + botName + ": __EXT_BOT is: " + optExtBot);
    }

    /**
     * Consider a trade offer; reject if we aren't offered clay or sheep.
     *<P>
     * {@inheritDoc}
     */

    protected String extractOfferGiveData(SOCTradeOffer offer){
        //Get & format which resources we'll give away
        String giveData = "";
        SOCResourceSet give = offer.getGiveSet();
        giveData += Integer.toString(give.getAmount(SOCResourceConstants.CLAY));
        giveData += ",";
        giveData += Integer.toString(give.getAmount(SOCResourceConstants.WOOD));
        giveData += ",";
        giveData += Integer.toString(give.getAmount(SOCResourceConstants.SHEEP));
        giveData += ",";
        giveData += Integer.toString(give.getAmount(SOCResourceConstants.ORE));
        giveData += ",";
        giveData += Integer.toString(give.getAmount(SOCResourceConstants.WHEAT));

        return giveData;
    }

    protected String extractOfferGetData(SOCTradeOffer offer){
        //Get & format which resources we'll get in return
        String getData = "";
        SOCResourceSet get = offer.getGetSet();
        getData += Integer.toString(get.getAmount(SOCResourceConstants.CLAY));
        getData += ",";
        getData += Integer.toString(get.getAmount(SOCResourceConstants.WOOD));
        getData += ",";
        getData += Integer.toString(get.getAmount(SOCResourceConstants.SHEEP));
        getData += ",";
        getData += Integer.toString(get.getAmount(SOCResourceConstants.ORE));
        getData += ",";
        getData += Integer.toString(get.getAmount(SOCResourceConstants.WHEAT));
        return getData;
    }

    protected int getPlayerVP(int player){
        return game.getPlayer(player).getPublicVP();
    }

    protected String formatPlayerResources(int player){
        SOCResourceSet resources = game.getPlayer(player).getResources();
        String resData = "";
        resData += Integer.toString(resources.getAmount(SOCResourceConstants.CLAY));
        resData += ",";
        resData += Integer.toString(resources.getAmount(SOCResourceConstants.WOOD));
        resData += ",";
        resData += Integer.toString(resources.getAmount(SOCResourceConstants.SHEEP));
        resData += ",";
        resData += Integer.toString(resources.getAmount(SOCResourceConstants.ORE));
        resData += ",";
        resData += Integer.toString(resources.getAmount(SOCResourceConstants.WHEAT));

        return resData;

    }

//    @Override
//    protected int considerOffer(SOCTradeOffer offer)
//    {
//
//        if (! offer.getTo()[getOurPlayerNumber()])
//        {
//            return SOCRobotNegotiator.IGNORE_OFFER;
//        }
//
//        String decision = "0";
//
//        try{
//
//            //Get & format my victory points
//            String my_vp = Integer.toString(getPlayerVP(getOurPlayerNumber()));
//            String opp_vp = Integer.toString(getPlayerVP(offer.getFrom()));
//
//            //Get & format current resource set for opponent
//            String opponent_resources = formatPlayerResources(offer.getFrom());
//
//            //Get & format current resource set for our agent
//            String my_resources = formatPlayerResources(getOurPlayerNumber());
//
//            //Get & format trade contents
//            String getData = extractOfferGetData(offer);
//            String giveData = extractOfferGiveData(offer);
//
//
//            //Pass game state info to DQN server
//            servercon = new Socket("localhost", 2004);
//            servercon.setSoTimeout(300000);
//            serverin = new DataInputStream(servercon.getInputStream());
//            serverout = new DataOutputStream(servercon.getOutputStream());
//            String msg = "trade|" + my_vp + "|" + opp_vp + "|" + my_resources + "|" + opponent_resources + "|" + getData + "|" + giveData;
//            serverout.writeUTF(msg);
//
//            //Receive decision from DQN server
//            decision = serverin.readLine();
//
//            serverout.flush();
//            serverout.close();
//            serverin.close();
//            servercon.close();
//        }
//         catch(Exception e){
//            System.err.println("Whoops! Connection with server lost ... ");
//         }
//
//        if (decision.contains("0")){
//            System.err.println(decision);
//            System.err.println("Rejecting offer ... ");
//           return SOCRobotNegotiator.REJECT_OFFER;
//        }
//        else{
//            System.err.println(decision);
//            System.err.println("Accepting offer ... ");
//            return SOCRobotNegotiator.ACCEPT_OFFER;
//        }
//
//        /*
//        final SOCResourceSet res = offer.getGiveSet();
//        if (! (res.contains(SOCResourceConstants.CLAY) || res.contains(SOCResourceConstants.SHEEP)))
//        {
//            return SOCRobotNegotiator.REJECT_OFFER;
//        }
//
//        return super.considerOffer(offer);
//        */
//    }

    @Override
    protected void saveMessage(String message) {
        messageHistory.add(message);
    }

    @Override
    protected void placeIfExpectPlacing()
    {
        // msg = init_settlement | playerIndex | tileInfo[74] | nodeOwnership[256] | nodeValidity[256] | roadOwnership[256]
        // playerIndex
        // Just an integer

        // tileInfo
        // 74 total (37 tiles, 2 numbers per tile)
        // Loop over tiles 0-37
        // Get type -> add to string
        // Delimiter
        // Get dice roll -> add to string

        // nodeOwnership
        // 256 total
        // range: 0-8
        // To know who has what currently
        // 2 values per player, one for city, one for settlement
        // 0 = unclaimed
        // 1-2 = player 1, settlement = 1, city = 2
        // etc...

        // nodeValidity
        // 256 total
        // range: 0-1 (binary)
        // 0 = not valid
        // 1 = valid
        // For filtering outputs, to get the highest valued node that's a valid choice

        // roadOwnership
        // 256 total
        // range: 0-4
        // 0 = unowned
        // 1-4 = player ID of owner
        // To know which direction players can expand

        if (waitingForGameState)
            return;

        String serverDecision;
        switch (game.getGameState())
        {
            case SOCGame.START1A:
                {
                    expectSTART1A = false;
//                    System.out.println("START1A");
                    if ((!waitingForOurTurn) && ourTurn && (!(expectPUTPIECE_FROM_START1A && (counter < 4000)))) {
                        expectPUTPIECE_FROM_START1A = true;
                        counter = 0;
                        waitingForGameState = true;
//                        System.out.println("Inside 1A");
                        serverDecision = sendMessageToDQN("init_settlement");

                        if (!serverDecision.equals("None")) {
                            try {
                                final int node = Integer.parseInt(serverDecision);
//                                System.out.println(node);
//                                sendMessageToDQN("just_checking");
                                placeFirstSettlement(node);
//                                sendMessageToDQN("just_checking");
                            } catch (Exception e) {
                                System.err.println("Whoops! Couldn't convert returned message into integer when building first settlement ... ");
                            }
                        } else {  // Empty message returned, make random choice
                            System.err.println("No decision received for first settlement, using built in system ...");
                            super.placeIfExpectPlacing();
                        }
                    }
                }
                break;

            case SOCGame.START2A:
                {
                    expectSTART2A = false;
//                    System.out.println("START2A");
                    if ((!waitingForOurTurn) && ourTurn && (!(expectPUTPIECE_FROM_START2A && (counter < 4000)))) {
                        expectPUTPIECE_FROM_START2A = true;
                        counter = 0;
                        waitingForGameState = true;
//                        System.out.println("Inside 2A");
                        serverDecision = sendMessageToDQN("init_settlement");

                        if (!serverDecision.equals("None")) {
                            try {
                                final int node = Integer.parseInt(serverDecision);
//                                System.out.println(node);
//                                sendMessageToDQN("just_checking");
                                placeInitSettlement(node);
//                                sendMessageToDQN("just_checking");
                            } catch (Exception e) {
                                System.err.println("Whoops! Couldn't convert returned message into integer when building second settlement ... ");
                            }
                        } else {  // Empty message returned, make random choice
                            System.err.println("No decision received for second settlement, using built in system ...");
                            super.placeIfExpectPlacing();
                        }
                    }
                }
            break;

            default:
                super.placeIfExpectPlacing();
        }
    }

    protected String sendMessageToDQN(String messageTag)
    {
        Integer id = getOurPlayerNumber();
        String stringID =  id.toString();

        String tileInfo = generateTileInfo();
        String nodeOwnership = generateNodeOwnership();
        String nodeValidity = generateNodeValidity();
        String roadOwnership = generateRoadOwnership();

        String msg = messageTag + "|" + stringID + "|" + tileInfo + "|" + nodeOwnership + "|" + nodeValidity + "|" + roadOwnership;
        String serverReturn = "";

        try {
            servercon = new Socket("localhost", 2004);
            servercon.setSoTimeout(300000);
            serverin = new DataInputStream(servercon.getInputStream());
            serverout = new DataOutputStream(servercon.getOutputStream());
            serverout.writeUTF(msg);

            //Receive decision from DQN server
            serverReturn = serverin.readLine();

            if (serverReturn.equals("random_settlement")) {
                Integer randomSettlement = getRandomSettlement();
                serverReturn = randomSettlement.toString();
            }

            serverout.flush();
            serverout.close();
            serverin.close();
            servercon.close();
        } catch (Exception e) {
            System.err.println("Whoops! Connection with server lost ... ");
        }

        saveMessage(msg);
        saveMessage(serverReturn);

        return serverReturn;
    }

    protected String generateTileInfo()
    {
        String tileInfo = "";
        Integer type, roll;
        SOCBoard board = game.getBoard();

        for (int i = 0; i < 37; i++) {
            type = board.getHexTypeFromNumber(i);
            roll = board.getNumberOnHexFromNumber(i);
            tileInfo += type.toString() + "|" + roll.toString();
            if (i < 36) {
                tileInfo += "|";
            }
        }

        return tileInfo;
    }

    protected String generateNodeOwnership()
    {
        String nodeOwnership = "";
        SOCBoard board = game.getBoard();
        List<SOCSettlement> ownedSettlements =  board.getSettlements();
        List<SOCCity> ownedCities = board.getCities();
        List<Integer> nodes = new ArrayList<Integer>(Collections.nCopies(256, 0));

        for (SOCSettlement settlement : ownedSettlements) {
            int coord = settlement.getCoordinates();
            Integer playerID = ((settlement.getPlayerNumber() + 1) * 2) - 1;  // 1, 3, 5, 7

            nodes.set(coord, playerID);
        }

        for (SOCCity city : ownedCities) {
            int coord = city.getCoordinates();
            System.out.print(coord);
            Integer playerID = (city.getPlayerNumber() + 1) * 2;  // 2, 4, 6, 8

            nodes.set(coord, playerID);
        }

        for (int i = 0; i < nodes.size(); i++) {
            nodeOwnership += nodes.get(i).toString();
            if (i < nodes.size() - 1) {
                nodeOwnership += "|";
            }
        }

        return nodeOwnership;
    }

    protected String generateNodeValidity()
    {
        String nodeValidity = "";
        SOCBoard board = game.getBoard();
        List<SOCSettlement> ownedSettlements =  board.getSettlements();
        List<SOCCity> ownedCities = board.getCities();
        List<Integer> nodes = new ArrayList<Integer>(Collections.nCopies(256, 1));

        for (SOCSettlement settlement : ownedSettlements) {
            int coord = settlement.getCoordinates();
//            List<Integer> adjacentNodes = board.getAdjacentNodesToNode(coord);
//
//            for (Integer node : adjacentNodes) {
//                nodes.set(node, 0);
//            }

            nodes.set(coord, 0);
        }

        for (SOCCity city : ownedCities) {
            int coord = city.getCoordinates();
//            List<Integer> adjacentNodes = board.getAdjacentNodesToNode(coord);
//
//            for (Integer node : adjacentNodes) {
//                nodes.set(node, 0);
//            }

            nodes.set(coord, 0);
        }

        for (int i = 0; i < nodes.size(); i++) {
            nodeValidity += nodes.get(i).toString();
            if (i < nodes.size() - 1) {
                nodeValidity += "|";
            }
        }

        return nodeValidity;
    }

    protected String generateRoadOwnership()
    {
        String roadOwnership = "";
        SOCBoard board = game.getBoard();
        List<SOCRoutePiece> ownedRoads = board.getRoadsAndShips();
        List<Integer> roads = new ArrayList<Integer>(Collections.nCopies(256, 1));

        for (SOCRoutePiece road : ownedRoads) {
            int coord = road.getCoordinates();
            Integer playerID = road.getPlayerNumber() + 1;

            roads.set(coord, playerID);
        }

        for (int i = 0; i < roads.size(); i++) {
            roadOwnership += roads.get(i).toString();
            if (i < roads.size() - 1) {
                roadOwnership += "|";
            }
        }

        return roadOwnership;
    }

    protected Integer getRandomSettlement()
    {
        final int[] potentialSettlements = ourPlayerData.getPotentialSettlements_arr();
        Random random = new Random();
        return potentialSettlements[random.nextInt(potentialSettlements.length)];
    }
}
