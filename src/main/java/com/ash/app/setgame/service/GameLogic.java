package com.ash.app.setgame.service;

import com.ash.app.setgame.model.Card;
import com.ash.app.setgame.model.Color;
import com.ash.app.setgame.model.Shading;
import com.ash.app.setgame.model.Symbol;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameLogic {

    private List<Card> cardList;
    private List<String> originalInput;
    private List<int[]> validCardSets;

    // converts the list of strings to list of CARDs
    public void convertInputToCardList(List<String> input) throws Exception {
        originalInput = input;
        cardList = new ArrayList();
        //first line contains size of input
        int inputSize = Integer.parseInt(input.get(0));
        //System.out.println(inputSize);

        //verifying if size specified is correct
        if(inputSize != input.size()-1) {
            throw new Exception("Number of Cards provided are not equal to specified number");
        }

        for(int i=1; i<=inputSize; i++) {
            //System.out.println(input.get(i));
            cardList.add(convertStringToCard(input.get(i)));
        }

        //System.out.println("Converted inputs");
    }

    private Card convertStringToCard(String cardStr) {
        String[] cardStrSplit = cardStr.split(" ");
        Card card = new Card();
        card.setColor(Color.valueOf(cardStrSplit[0]));

        findOtherCardDetails(cardStrSplit[1], card);

        return card;
    }

    private void findOtherCardDetails(String cardStr2, Card card) {
        char c = cardStr2.charAt(0);
        if(c=='A' || c=='a' || c=='@') {
            card.setSymbol(Symbol.A);
            if(c=='a') {
                card.setShading(Shading.lower_case);
            }
            else if(c=='A') {
                card.setShading(Shading.upper_case);
            }
            else {
                card.setShading(Shading.symbol_case);
            }
        }
        else if(c=='S' || c=='s' || c=='$') {
            card.setSymbol(Symbol.S);
            if(c=='s') {
                card.setShading(Shading.lower_case);
            }
            else if(c=='S') {
                card.setShading(Shading.upper_case);
            }
            else {
                card.setShading(Shading.symbol_case);
            }
        }
        else {
            card.setSymbol(Symbol.H);
            if(c=='h') {
                card.setShading(Shading.lower_case);
            }
            else if(c=='H') {
                card.setShading(Shading.upper_case);
            }
            else {
                card.setShading(Shading.symbol_case);
            }
        }

        card.setNumber(cardStr2.length());
    }

    public void findValidCardSets() {
        validCardSets = new ArrayList<>();
        //checking 1 by 1 the validity of SETs
        for(int i=0; i<cardList.size(); i++) {
            for(int j=i+1; j<cardList.size(); j++) {
                for(int k=j+1; k<cardList.size(); k++) {
                    if(isValidSet(cardList.get(i), cardList.get(j), cardList.get(k))) {
                        //inserting the indexes of the cards rather than cards itself: This helps me in calculating disjoint SET
                        validCardSets.add(new int[]{i, j, k});
                    }
                }
            }
        }

        //System.out.println("Number of Valid Card Sets : "+validCardSets.size());
        System.out.println(validCardSets.size());
    }

    public void findLargestDisjointCollectionOfSets() {
        // key is the card and value is the indexes of the row where this card occurs
        Map<Integer, Set<Integer>> cardToOccurMap = new HashMap<>();
        Set<int[]> largestDisjointSet = new HashSet<>();

        for(int i=0; i<validCardSets.size(); i++) {
            int[] set = validCardSets.get(i);
            for(int j=0; j<set.length; j++) {
                cardToOccurMap.putIfAbsent(set[j], new HashSet<>());
                cardToOccurMap.get(set[j]).add(i);
            }
        }

        // sort map based on increasing count
        // size of map values denote the number of times the card appears
        List<Map.Entry<Integer, Set<Integer>>> list = new LinkedList<>(cardToOccurMap.entrySet());
        Collections.sort(list, (a, b) -> a.getValue().size() - b.getValue().size());

        // put data from sorted list to map
        Map<Integer, Set<Integer>> map = new LinkedHashMap<>(); // clear the old values to reuse the same map
        for(Map.Entry<Integer, Set<Integer>> entry: list) {
            map.put(entry.getKey(), entry.getValue());
        }

        Set<Integer> visitedRows = new HashSet<>();
        //the cards that have been visited
        Set<Integer> visitedNums = new HashSet<>();
        for(Map.Entry<Integer, Set<Integer>> entry: map.entrySet()) {
            if(visitedNums.contains(entry.getKey())) {
                continue;
            }

            // iterate over all the rows where this card is present
            for(int idx: entry.getValue()) {
                if(!visitedRows.contains(idx)) {
                    // check if the row includes any of the visitedNums, if yes, this row cannot be included
                    boolean proceed = true;
                    int[] set = validCardSets.get(idx);
                    for(int j=0; j<set.length; j++) {
                        if(visitedNums.contains(set[j])) {
                            proceed = false;
                            break;
                        }
                    }
                    if(!proceed) {
                        continue;
                    }

                    largestDisjointSet.add(set);
                    visitedRows.add(idx);
                    // add all the cards in this row to visitedNums
                    for(int j=0;j<set.length;j++) {
                        visitedNums.add(set[j]);
                    }
                }
            }
        }

        //System.out.println("Largest Disjoint Collection of SETs : "+largestDisjointSet.size());
        System.out.println(largestDisjointSet.size());
        System.out.println("");
        for(int[] set : largestDisjointSet) {
            for(int cardIdx : set) {
                System.out.println(originalInput.get(cardIdx+1));
            }
            System.out.println("");
        }
    }

    private boolean isValidSet(Card c1, Card c2, Card c3) {
        if(!allColorsEqual(c1.getColor(), c2.getColor(), c3.getColor()) && !allColorsDifferent(c1.getColor(), c2.getColor(), c3.getColor())) {
            return false;
        }
        if(!allSymbolsEqual(c1.getSymbol(), c2.getSymbol(), c3.getSymbol()) && !allSymbolsDifferent(c1.getSymbol(), c2.getSymbol(), c3.getSymbol())){
            return false;
        }
        if(!allShadingsEqual(c1.getShading(), c2.getShading(), c3.getShading()) && !allShadingsDifferent(c1.getShading(), c2.getShading(), c3.getShading())) {
            return false;
        }
        if(!allNumbersEqual(c1.getNumber(), c2.getNumber(), c3.getNumber()) && !allNumbersDifferent(c1.getNumber(), c2.getNumber(), c3.getNumber())) {
            return false;
        }
        return true;
    }

    private boolean allColorsEqual(Color c1, Color c2, Color c3) {
        return c1==c2 && c1==c3;
    }

    private boolean allSymbolsEqual(Symbol s1, Symbol s2, Symbol s3) {
        return s1==s2 && s1==s3;
    }

    private boolean allShadingsEqual(Shading sh1, Shading sh2, Shading sh3) {
        return sh1==sh2 && sh1==sh3;
    }

    private boolean allNumbersEqual(int n1, int n2, int n3) {
        return n1==n2 && n1==n3;
    }

    private boolean allColorsDifferent(Color c1, Color c2, Color c3) {
        return c1!=c2 && c1!=c3 && c2!=c3;
    }

    private boolean allSymbolsDifferent(Symbol s1, Symbol s2, Symbol s3) {
        return s1!=s2 && s1!=s3 && s2!=s3;
    }

    private boolean allShadingsDifferent(Shading sh1, Shading sh2, Shading sh3) {
        return sh1!=sh2 && sh1!=sh3 && sh2!=sh3;
    }

    private boolean allNumbersDifferent(int n1, int n2, int n3) {
        return n1!=n2 && n1!=n3 && n2!=n3;
    }
}
