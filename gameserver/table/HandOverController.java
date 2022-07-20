package com.mynet.gameserver.table;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.model.HandOverCardModel;
import com.mynet.gameserver.model.HandOverResultModel;
import com.mynet.gameserver.okey.CardHandler;
import com.mynet.gameserver.okey.CardMap;
import com.mynet.gameserver.okey.OkeyCard;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.model.HandOverErrorModel;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.builders.ErrorResponseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class HandOverController {
    private static HandOverController instance;

    public static HandOverController getInstance(){
        if(instance == null){
            instance = new HandOverController();
        }

        return instance;
    }

    public HandOverResultModel checkValidCards(Table table, List<HandOverCardModel> finishedHand, OkeyCard finishingCard) throws Exception{
        HandOverResultModel result = new HandOverResultModel();
        CardHandler chandler = table.getCardHandler();
        String hand = chandler.getSideHand(table.getGameTurn());

        ArrayList<OkeyCard> myFinishedCards = new ArrayList<>();
        String[] finishedCards;

//        if(finishedHand.endsWith("%"))
//            finishedCards = StringUtil.processRawString(finishedHand, "%");
//        else
//            finishedCards = StringUtil.processRawString(finishedHand, "~");

        if (finishedHand.size() != 14) {
            result.setErrorMessage("cardSizeLessThen14;" + finishedHand.size());
            result.setLogText("cardSizeLessThen14");
            result.setDeckErrorText(finishedHand.size() + "");
            result.setCode(-1);

            ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.LESS_THAN_14).createErrorResponse();
            result.setError(response);
            return result;
        }

        for (HandOverCardModel finishedCard : finishedHand) {
//            String[] fCard = StringUtil.processRawString(finishedCard, "_");

            OkeyCard newCard = new OkeyCard(finishedCard);

            myFinishedCards.add(new OkeyCard(newCard.getCardId(), newCard.getCardNumber(), newCard.getCardType(), newCard.getBucket()));

            String cardString = newCard.getCardType() + ":" + newCard.getCardNumber() + ":" + newCard.getBucket();
            if (!hand.contains(cardString)) {
                result.setErrorMessage("thisCardIsNotInUSerHand;" + cardString);
                result.setLogText("thisCardIsNotInUSerHand");
                result.setDeckErrorText(cardString);
                result.setCode(-99);

                List<HandOverCardModel> deck = new ArrayList<>();
                deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(newCard), newCard.getXPos(), newCard.getYPos()));
                HandOverErrorModel error = new HandOverErrorModel(deck);
                ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.NOT_IN_USER_HAND).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
                result.setError(response);

                return result;
            }
        }

        Vector<OkeyCard> myCardsInServer = chandler.getSideVector(table.getGameTurn());

        OkeyCard myShouldBeLastCard = extractLastCard(myCardsInServer, myFinishedCards);
        if(myShouldBeLastCard == null){
            result.setErrorMessage("myShouldBeLastCardNULL;" + finishingCard.toString());
            result.setLogText("myShouldBeLastCardNULL");
            result.setDeckErrorText(finishingCard.toString());
            result.setCode(-95);

            List<HandOverCardModel> deck = new ArrayList<>();
            deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(finishingCard), finishingCard.getXPos(), finishingCard.getYPos()));
            HandOverErrorModel error = new HandOverErrorModel(deck);
            ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.LAST_CARD_NULL).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
            result.setError(response);

            return result;
        } else if(myShouldBeLastCard.getCardNumber() != finishingCard.getCardNumber() || myShouldBeLastCard.getCardType() != finishingCard.getCardType()){
            result.setErrorMessage("lastCardDoesntMatchServer;" + finishingCard.toString());
            result.setLogText("lastCardDoesntMatchServer");
            result.setDeckErrorText(finishingCard.toString());
            result.setCode(-99);

            List<HandOverCardModel> deck = new ArrayList<>();
            deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(finishingCard), finishingCard.getXPos(), finishingCard.getYPos()));
            HandOverErrorModel error = new HandOverErrorModel(deck);
            ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.DOESNT_MATCH_SERVER).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
            result.setError(response);

            return result;
        }

        result.setCode(0);
        return result;

    }

    public HandOverResultModel checkHandComplete(List<HandOverCardModel> finishedHand, OkeyCard okey) throws Exception {

        HandOverResultModel result = new HandOverResultModel();

        int fakeokeyBucket = 3;
        // Kart listesi olustur
        List<OkeyCard> cards = new ArrayList<>();
//        String[] finishedCards;

//        if(finishedHand.endsWith("%"))
//            finishedCards = StringUtil.processRawString(finishedHand, "%");
//        else
//            finishedCards = StringUtil.processRawString(finishedHand, "~");

        int x, y;

        OkeyCard[][] positions = new OkeyCard[2][14];

        String controledCards = "";

        for (HandOverCardModel finishedCard : finishedHand) {
            boolean addedToList = false;


            OkeyCard newCard = new OkeyCard(finishedCard);

            String cardString = newCard.getCardType() + ":" + newCard.getCardNumber() + ":" + newCard.getBucket();

            if (controledCards.indexOf(cardString) > 0) {
                result.setCode(-99);
                result.setErrorMessage("duplicatedCard;" + cardString);
                result.setLogText("duplicatedCard");
                result.setDeckErrorText(cardString);

                List<HandOverCardModel> deck = new ArrayList<>();
                deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(newCard), newCard.getXPos(), newCard.getYPos()));
                HandOverErrorModel error = new HandOverErrorModel(deck);
                ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.DUPLICATE_CARD).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
                result.setError(response);

                return result;
            }

            positions[newCard.getYPos() - 1][newCard.getXPos() - 1] = newCard;

            if (!newCard.isOkey(okey) && newCard.getCardType() == 4) {
                newCard.setCardType(okey.getCardType());
                newCard.setCardNumber(okey.getCardNumber());
                newCard.setFakeOkey(1, newCard.getBucket());
                newCard.setBucket(fakeokeyBucket);
                fakeokeyBucket++;
            }

            for (OkeyCard card : cards) {
                if (((card.getYPos() > newCard.getYPos()))
                        || ((card.getYPos() == newCard.getYPos()) && (card
                        .getXPos() > newCard.getXPos()))) {
                    cards.add(cards.indexOf(card), newCard);
                    addedToList = true;
                    break;
                }
            }
            if (!addedToList) {
                cards.add(newCard);
            }

            controledCards = controledCards + "%" + cardString;
        }

        List<List<OkeyCard>> cardGroups = new ArrayList<>();
        List<OkeyCard> cardGroup = new ArrayList<>();
        for (x = 0; x < 2; x++) {
            for (y = 0; y < 14; y++) {

                if (positions[x][y] != null) {
                    cardGroup.add(positions[x][y]);

                } else {
                    if (!cardGroup.isEmpty()) {
                        cardGroups.add(cardGroup);
                        cardGroup = new ArrayList<>();
                    }
                }
            }
        }

        OkeyCard lastCard;

        // Kontroller
        // Normal bitis

        int currentLongest = 0;

        for (List<OkeyCard> tc : cardGroups) {

            currentLongest = Math.max(tc.size(), currentLongest);


            int okeyCount = 0;

            List<OkeyCard> realPer = swapToRealPer(tc);

            if (tc.size() < 3) {
                result.setErrorMessage("thisCardGoupLessThen3;" + realPer);
                result.setLogText("thisCardGoupLessThen3");
                result.setDeckErrorText(realPer.toString());
                result.setCode(-4);

                List<HandOverCardModel> deck = new ArrayList<>();
                for(OkeyCard card: realPer){
                    deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(card), card.getXPos(), card.getYPos()));
                }
                HandOverErrorModel error = new HandOverErrorModel(deck);
                ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.GROUP_LESS_THAN_3).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
                result.setError(response);

                return result;
            }

            int firstOkeyCount = 0;
            int fcIndex = 0;
            int firstType = tc.get(0).getCardType();
            // Ilk tas okey mi?
            if (tc.get(0).isRealOkey(okey)) {
                fcIndex = 1;
                firstType = tc.get(1).getCardType();
                okeyCount--;
                firstOkeyCount++;
                // Ikinci onceki okey mi?
                if (tc.get(1).isRealOkey(okey)) {
                    fcIndex = 2;
                    firstType = tc.get(2).getCardType();
                    okeyCount--;
                    firstOkeyCount++;
                }
            }

            boolean lastCardsOkey = false;

            OkeyCard lc = tc.get(tc.size() - 1);

            int lcIndex = tc.size() - 1;

            int lastOkeyCount = 0;

            // Sonuncu okey mi?
            if (lc.isRealOkey(okey)) {
                lc = tc.get(tc.size() - 2);
                lcIndex = tc.size() - 2;
                lastOkeyCount++;


                // Sondan onceki okey mi?
                if (lc.isRealOkey(okey)) {
                    lc = tc.get(tc.size() - 3);
                    lcIndex = tc.size() - 3;
                    lastOkeyCount++;
                }

                lastCardsOkey = true;
            }
            int lastType = lc.getCardType();
            if (firstType == lastType) {

                // Renk ayni set kontrol

                int firstCardNumber = tc.get(fcIndex).getCardNumber();
                int lastCardNumber = tc.get(lcIndex).getCardNumber();

                HandOverResultModel check = checkOkeysAtTheBeginningAndEnd(result, tc.size(), realPer, firstOkeyCount, fcIndex, lcIndex, lastOkeyCount, firstCardNumber, lastCardNumber);
                if (check != null) return check;


                lastCard = null;
                int lastDiff = 0;
                for (OkeyCard card : tc) {

                    if(lcIndex > 0 && lastCardsOkey && lc.getCardNumber() == 1){
                        result.setCode(-6);
                        result.setErrorMessage("thisCardNotInPer;" + realPer);
                        result.setLogText("thisCardNotInPer");
                        result.setDeckErrorText(realPer.toString());

                        List<HandOverCardModel> deck = new ArrayList<>();
                        for(OkeyCard okeyCard: realPer){
                            deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(okeyCard), okeyCard.getXPos(), okeyCard.getYPos()));
                        }
                        HandOverErrorModel error = new HandOverErrorModel(deck);
                        ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.NOT_IN_PER).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
                        result.setError(response);

                        return result;
                    }

                    // okey
                    if (card.isOkey(okey) && card.getBucket() < 3 /* Sahte okey degilse*/) {
                        okeyCount++;
                        continue;
                    }

                    // ilk kart
                    if (lastCard == null) {
                        lastCard = card;
                        continue;
                    }

                    // renk farkli
                    if (lastCard.getCardType() != card.getCardType()) {
                        result.setCode(-5);
                        result.setErrorMessage("thisCardNotSameColor;" + realPer);
                        result.setLogText("thisCardNotSameColor");
                        result.setDeckErrorText(realPer.toString());

                        List<HandOverCardModel> deck = new ArrayList<>();
                        for(OkeyCard okeyCard: realPer){
                            deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(okeyCard), okeyCard.getXPos(), okeyCard.getYPos()));
                        }
                        HandOverErrorModel error = new HandOverErrorModel(deck);
                        ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.NOT_SAME_COLOR).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
                        result.setError(response);

                        return result;
                    }

                    // siradaki mi
                    int cn = card.getCardNumber();

                    if (lastCard.getCardNumber() >= 11 && cn == 1) {
                        card.setCardNumber(14);
                        cn = 14;
                    }

                    int diff = cn - lastCard.getCardNumber();

                    if (lastCard.getCardNumber() == 1 && cn >= 11 && tc.get(0) == lastCard) {
                        diff = cn - 14;
                    }

                    if (okeyCount == 0 && ((diff == 1) || (diff == -1))) {
                        lastCard = card;

                        if (lastDiff == 0) {
                            lastDiff = diff;
                        }

                        if (lastDiff == diff) {
                            continue;
                        }
                    }
                    if ((diff == 2 && okeyCount == 1) || (diff == -2 && okeyCount == 1)) {
                        lastCard = card;
                        okeyCount--;
                        continue;
                    }
                    if ((diff == 3 && okeyCount == 2) || (diff == -3 && okeyCount == 2)) {
                        lastCard = card;
                        okeyCount = 0;
                        continue;
                    }
                    result.setCode(-6);
                    result.setErrorMessage("thisCardNotInPer;" + realPer);
                    result.setLogText("thisCardNotInPer");
                    result.setDeckErrorText(realPer.toString());

                    List<HandOverCardModel> deck = new ArrayList<>();
                    for(OkeyCard okeyCard: realPer){
                        deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(okeyCard), okeyCard.getXPos(), okeyCard.getYPos()));
                    }
                    HandOverErrorModel error = new HandOverErrorModel(deck);
                    ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.NOT_IN_PER).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
                    result.setError(response);

                    return result;
                }
            } else {
                // Renk farki set kontrol
                if(tc.size() > 4){
                    result.setCode(-6);
                    result.setErrorMessage("thisCardNotInPer;" + realPer);
                    result.setLogText("thisCardNotInPer");
                    result.setDeckErrorText(realPer.toString());

                    List<HandOverCardModel> deck = new ArrayList<>();
                    for(OkeyCard okeyCard: realPer){
                        deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(okeyCard), okeyCard.getXPos(), okeyCard.getYPos()));
                    }
                    HandOverErrorModel error = new HandOverErrorModel(deck);
                    ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.NOT_IN_PER).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
                    result.setError(response);

                    return result;
                }

                if (tc.size() == 3 || tc.size() == 4) {
                    List<Integer> seen = new ArrayList<>();
                    lastCard = null;
                    for (OkeyCard card : tc) {
                        // okey
                        if (card.isOkey(okey) && card.getBucket() < 3) {
                            continue;
                        }

                        // ilk kart
                        if (lastCard == null) {
                            lastCard = card;
                            Integer renk = card.getCardType();
                            seen.add(renk);
                            continue;
                        }

                        // renk
                        if (card.getCardNumber() == lastCard.getCardNumber()) {
                            Integer renk = card.getCardType();
                            if (seen.contains(renk)) {
                                result.setCode(-7);
                                return result;
                            } else {
                                seen.add(renk);
                                lastCard = card;
                            }
                            continue;
                        }

                        result.setErrorMessage("thisCardNotInSameColorPer;" + realPer);
                        result.setLogText("thisCardNotInSameColorPer");
                        result.setDeckErrorText(realPer.toString());
                        result.setCode(-8);

                        List<HandOverCardModel> deck = new ArrayList<>();
                        for(OkeyCard okeyCard: realPer){
                            deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(okeyCard), okeyCard.getXPos(), okeyCard.getYPos()));
                        }
                        HandOverErrorModel error = new HandOverErrorModel(deck);
                        ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.NOT_IN_SAME_COLOR_PER).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
                        result.setError(response);

                        return result;
                    }
                } else {

                    result.setErrorMessage("perLessThen3;" + realPer);
                    result.setLogText("perLessThen3");
                    result.setDeckErrorText(realPer.toString());
                    result.setCode(-9);

                    List<HandOverCardModel> deck = new ArrayList<>();
                    for(OkeyCard okeyCard: realPer){
                        deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(okeyCard), okeyCard.getXPos(), okeyCard.getYPos()));
                    }
                    HandOverErrorModel error = new HandOverErrorModel(deck);
                    ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.PER_LESS_THAN_3).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
                    result.setError(response);

                    return result;
                }
            }
        }

        result.setLongestTile(currentLongest);
        result.setCode(0);
        return result;
    }

    private HandOverResultModel checkOkeysAtTheBeginningAndEnd(HandOverResultModel result, int size, List<OkeyCard> realPer, int firstOkeyCount, int fcIndex, int lcIndex, int lastOkeyCount, int firstCardNumber, int lastCardNumber) {
        boolean asc = firstCardNumber < lastCardNumber;

        int fIndex = fcIndex;
        int lIndex = lcIndex;
        int fNumber = firstCardNumber;
        int lNumber= lastCardNumber;
        int fOCount = firstOkeyCount;
        int lOCount = lastOkeyCount;

        if(!asc) {
            fIndex = size - lcIndex - 1;
            lIndex = size - fcIndex - 1;
            fNumber = lastCardNumber;
            lNumber = firstCardNumber;
            fOCount = lastOkeyCount;
            lOCount = firstOkeyCount;
        }

        //okey-okey-2-3
        //okey-okey-1-2
        //okey-1-2
        if (fIndex > 0 && fNumber <= fOCount && size > fIndex + 1) {
            result.setCode(-6);
            result.setErrorMessage("thisCardNotInPer;" + realPer);
            result.setLogText("thisCardNotInPer");
            result.setDeckErrorText(realPer.toString());

            List<HandOverCardModel> deck = new ArrayList<>();
            for (OkeyCard okeyCard : realPer) {
                deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(okeyCard), okeyCard.getXPos(), okeyCard.getYPos()));
            }
            HandOverErrorModel error = new HandOverErrorModel(deck);
            ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.NOT_IN_PER).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
            result.setError(response);

            return result;
        }

        //12-13-okey-okey
        if (lIndex > 0 && lNumber == 13 && lOCount == 2) {
            result.setCode(-6);
            result.setErrorMessage("thisCardNotInPer;" + realPer);
            result.setLogText("thisCardNotInPer");
            result.setDeckErrorText(realPer.toString());

            List<HandOverCardModel> deck = new ArrayList<>();
            for (OkeyCard okeyCard : realPer) {
                deck.add(new HandOverCardModel(CardMap.getInstance().getCardId(okeyCard), okeyCard.getXPos(), okeyCard.getYPos()));
            }
            HandOverErrorModel error = new HandOverErrorModel(deck);
            ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.NOT_IN_PER).setData(NetworkMessage.getGson().toJson(error)).createErrorResponse();
            result.setError(response);

            return result;
        }

        return null;
    }

    private List<OkeyCard> swapToRealPer(List<OkeyCard> tc) {

        List<OkeyCard> ntc = new ArrayList<>();
        int i = 0;

        for (OkeyCard card : tc) {
            if (card.getFakeOkey() == 1) {

                String[] fCard = new String[5];

                //[i+"","4","14"];
                fCard[0] = card.getRealBucket() + "";
                fCard[1] = "4";
                fCard[2] = "14";
                fCard[3] = card.getXPos() + "";
                fCard[4] = card.getYPos() + "";

                OkeyCard newCard = new OkeyCard(fCard);
                ntc.add(newCard);
                i++;
            } else {
                ntc.add(card);
            }

        }

        return ntc;
    }

    public OkeyCard extractLastCard(Vector<OkeyCard> allCards, ArrayList<OkeyCard> finishingCards){
        OkeyCard myShouldBeLastCard = null;
        for(OkeyCard c : allCards){
            boolean found = false;
            for(OkeyCard c2 : finishingCards){
                if(c.isEqual(c2)){
                    found = true;
                    break;
                }
            }

            if(!found){
                myShouldBeLastCard = c;
                break;
            }
        }

        return myShouldBeLastCard;
    }
}
