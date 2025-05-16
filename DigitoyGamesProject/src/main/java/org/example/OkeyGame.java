package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class OkeyGame {

    // Taşları tutan list
    private List<Integer> tiles;
    // Oyuncuları tutan list
    private List<List<Integer>> players;

    // Gösterge taş
    private int indicatorTile;
    // Okey taş
    private int okeyTile;
    // Okey oyunu için constructor görevi gören fonksiyon.
    public OkeyGame() {
        tiles = new ArrayList<>();
        players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(new ArrayList<>());
        }
    }
    private static final String[] COLORS = {"Sarı", "Mavi", "Siyah", "Kırmızı"};
    private static final int TOTAL_TILES_PER_COLOR = 13;
    private static final int TOTAL_TILES = 106;
    private static final int FAKE_OKEY = 52;

    // Taşları oluşturan ve rastgele bir şekilde karışmasını sağlayan fonksiyon.
    public void startInitialTiles() {
        for (int i = 0; i < 52; i++) {
            tiles.add(i);
            tiles.add(i);
        }
        tiles.add(FAKE_OKEY);
        tiles.add(FAKE_OKEY);
        Collections.shuffle(tiles);
    }

    // Burada gösterge ve okey seçimi yapılır.
    public void selectIndicatorAndOkeyTiles() {
        Random rand = new Random();
        do {
            indicatorTile = tiles.get(rand.nextInt(tiles.size()));
        } while (indicatorTile == FAKE_OKEY);

        int color = indicatorTile / TOTAL_TILES_PER_COLOR;
        int number = indicatorTile % TOTAL_TILES_PER_COLOR + 1;
        okeyTile = color * TOTAL_TILES_PER_COLOR + (number % TOTAL_TILES_PER_COLOR);
    }

    // Taşların dağılımının yapıldığı yerdir
    public void shareTiles () {
        Random rand = new Random();
        int startingPlayer = rand.nextInt(4);

        int tileIndex = 0;
        for (int i = 0; i < 15; i++) {
            players.get(startingPlayer).add(tiles.get(tileIndex++));
            for (int j = 1; j < 4; j++) {
                if (i < 14) {
                    players.get((startingPlayer + j) % 4).add(tiles.get(tileIndex++));
                }
            }
        }

        for (List<Integer> hand : players) {
            Collections.sort(hand);
        }
    }

    // Taşın rengini ve numarasını veren fonksiyondur.
    private String formatTile(int tile) {
        if (tile == FAKE_OKEY) return "Sahte-Okey";
        int color = tile / TOTAL_TILES_PER_COLOR;
        int number = tile % TOTAL_TILES_PER_COLOR + 1;
        return COLORS[color] + "-" + number;
    }

    // Seri ve çifte giderken aynı renk kontrolünü yapan fonksiyondur.
    private boolean isSameColor(int tile1, int tile2) {
        return tile1 / TOTAL_TILES_PER_COLOR == tile2 / TOTAL_TILES_PER_COLOR;
    }

    private int getNumber(int tile) {
        return (tile % TOTAL_TILES_PER_COLOR) + 1;
    }

    // Okey taşlarını oyun kurallarına göre kullanılabilecek taşlara dönüştürür.
    private List<Integer> convertOkeyTilesToNormalTiles(List<Integer> hand) {
        List<Integer> converted = new ArrayList<>();
        for (Integer tile : hand) {
            //  Sahte Okey'in erçek Okey'e dönüşmesi durumu
            if (tile == FAKE_OKEY) {
                converted.add(okeyTile);
            }
            // Okey taşının Gösterge + 1 değerini alması durumu
            else if (tile == okeyTile) {
                int color = indicatorTile / TOTAL_TILES_PER_COLOR;
                // Taşın 13 olması durumu. Bu durumda 1 sayılır
                int number = (indicatorTile % TOTAL_TILES_PER_COLOR + 1) % (TOTAL_TILES_PER_COLOR + 1);
                if (number == 0) number = 1;
                converted.add(color * TOTAL_TILES_PER_COLOR + (number - 1));
            } else {
                // Taşın normal taş olduğu durum
                converted.add(tile);
            }
        }
        return converted;
    }
    // Oyuncunun elindeki taşlardan oluşturulabilecek seri kombinasyonlarını hesaplar.
    // Taşlar sıralanarak ardaşık ve ayrı renkte olan taşlar gruplandırılır.
    private int calculateSeriesScore(List<Integer> hand) {
        List<Integer> converted = convertOkeyTilesToNormalTiles(hand);
        Collections.sort(converted);
        int score = 0;
        List<Integer> tempHand = new ArrayList<>(converted);
        // Taşlar üzerinde seri arama döngüsü
        while (tempHand.size() >= 3) {
            boolean found = false;
            // i değeri baştan başlayarak 3 lü gruplar olarak olabilecek bütün kombinasyonları hesaplar.

            for (int i = 0; i <= tempHand.size() - 3; i++) {
                int tile1 = tempHand.get(i);
                int tile2 = tempHand.get(i+1);
                int tile3 = tempHand.get(i+2);
                // Aynı renk kontrolü yapılır seri olması için
                if (isSameColor(tile1, tile2) && isSameColor(tile2, tile3)) {
                    int num1 = getNumber(tile1);
                    int num2 = getNumber(tile2);
                    int num3 = getNumber(tile3);
                    // 4. Seri kontrolü yapılır. ardaşık 3 sayı kontrolü
                    if ((num1+1 == num2 && num2+1 == num3) ||
                            (num1 == 13 && num2 == 1 && num3 == 2)) {
                        // Eğer seri bulunduysa score değeri 3 arttırılır.
                        score += 3;
                        // Bulunan seri elden çıkarılır.
                        tempHand.remove(i+2);
                        tempHand.remove(i+1);
                        tempHand.remove(i);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) break;
        }
        return score;
    }

    // Oyuncunun elindeki taşlardan oluşturulabilecek çift kombinasyonlarını hesaplayan fonksiyondur.
    // Taşlar sıralanarak aynı değerde olanlar yan yana gelir ve çiftler tespit edilmiş olur.
    // Her çift bulunduğunda score artmış olur.
    private int calculatePairsScore(List<Integer> hand) {
        List<Integer> converted = convertOkeyTilesToNormalTiles(hand);
        Collections.sort(converted);
        int score = 0;
        List<Integer> tempHand = new ArrayList<>(converted);

        while (tempHand.size() >= 2) {
            boolean found = false;
            for (int i = 0; i <= tempHand.size() - 2; i++) {
                // Çift bulunma durumu ve score değerinin arttırılması.
                if (tempHand.get(i).equals(tempHand.get(i+1))) {
                    score += 2;
                    // Çift olacak taşlar listeden çıkarılır.
                    tempHand.remove(i+1);
                    tempHand.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) break;
        }
        return score;
    }


    public void printOptimalArrangement(int playerIndex) {
        List<Integer> hand = players.get(playerIndex);
        List<Integer> converted = convertOkeyTilesToNormalTiles(hand);
        Collections.sort(converted);
        List<Integer> tempHand = new ArrayList<>(converted);

        System.out.println("\n"+(playerIndex + 1)+". Oyuncu için en iyi dağılım:");

        // seri gidilme kontrolü yapılır
        List<List<Integer>> seriesList = new ArrayList<>();
        while (tempHand.size() >= 3) {
            boolean found = false;
            for (int i = 0; i <= tempHand.size() - 3; i++) {
                int tile1 = tempHand.get(i);
                int tile2 = tempHand.get(i+1);
                int tile3 = tempHand.get(i+2);
                 if (isSameColor(tile1, tile2) && isSameColor(tile2, tile3)) {
                    int num1 = getNumber(tile1);
                    int num2 = getNumber(tile2);
                    int num3 = getNumber(tile3);

                    if ((num1+1 == num2 && num2+1 == num3) ||
                            (num1 == 13 && num2 == 1 && num3 == 2)) {
                        List<Integer> series = Arrays.asList(tile1, tile2, tile3);
                        seriesList.add(series);
                        tempHand.removeAll(series);
                        found = true;
                        break;
                    }
                }
            }
            if (!found) break;
        }

        // Sonra çifte olarak gidilebilir mi kontrolü yapılır.
        List<List<Integer>> pairsList = new ArrayList<>();
        while (tempHand.size() >= 2) {
            boolean found = false;
            for (int i = 0; i <= tempHand.size() - 2; i++) {
                if (tempHand.get(i).equals(tempHand.get(i+1))) {
                    List<Integer> pair = Arrays.asList(tempHand.get(i), tempHand.get(i+1));
                    pairsList.add(pair);
                    tempHand.remove(i+1);
                    tempHand.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) break;
        }

        // Seri giderken 3 uzunlukta çifte giderken 2 uzunlukta sonuçlar çıkacağı için puan elde etme aşamasında score hesaplanır ve hesaplanan
        // sonuca göre seri ve çifte dizilim yapılacağı kararı alınır.
        int seriesScore = seriesList.size() * 3;
        int pairsScore = pairsList.size() * 2;

        if (seriesScore >= pairsScore) {
            System.out.println("Uygulanacak strateji seri gitmek ve scoru: "+seriesScore);
            for (List<Integer> series : seriesList) {
                System.out.println("Yapıbilecek seriler: " + series.stream()
                        .map(this::formatTile)
                        .collect(Collectors.joining(" | ")));
            }
        } else {
            System.out.println("Uygulanacak strateji çifte gitmek ve scoru: "+pairsScore);
            for (List<Integer> pair : pairsList) {
                System.out.println("Yapılabilecek çifteler: " + pair.stream()
                        .map(this::formatTile)
                        .collect(Collectors.joining(" | ")));
            }
        }

        System.out.println("Kullanılamayan taşlar: " + tempHand.stream()
                .map(this::formatTile)
                .collect(Collectors.joining(", ")));
    }

    public void evaluateAndDisplayHands() {
        System.out.println("====================================");
        System.out.println("Gösterge taş: " + formatTile(indicatorTile));
        System.out.println("Okey taşı: " + formatTile(okeyTile));
        System.out.println("---------------------------");

        for (int i = 0; i < players.size(); i++) {
            System.out.println("\n"+(i + 1) +". oyuncunun sahip olduğu taşlar: ");
            System.out.println(players.get(i).stream()
                    .map(this::formatTile)
                    .collect(Collectors.joining(", ")));

            printOptimalArrangement(i);
        }

        determineWinner();
        System.out.println("====================================");
    }

    // Bütün oyuncular üzerinde dönen ve kazananma potansiyeli yüksek olan oyuncuları hesaplayan fonksiyon.
    private void determineWinner() {
        int maxScore = -1;
        List<Integer> winners = new ArrayList<>();
        // Seri ve çifte gitmeye göre iki durum için de hesaplama yapılır.
        for (int i = 0; i < players.size(); i++) {
            int score = Math.max(
                    calculateSeriesScore(players.get(i)),
                    calculatePairsScore(players.get(i))
            );
            // En yüksek skoru bulmak için olan kısım.
            if (score > maxScore) {
                maxScore = score;
                winners.clear();
                winners.add(i);
            } else if (score == maxScore) {
                winners.add(i);
            }
        }

        System.out.println("\n=== Kazanmaya en yakın oyuncu ===");
        System.out.print("Oyuncu ");
        for (int winner : winners) {
            System.out.print((winner + 1) + " ");
        }
        System.out.println(maxScore+" puan ile kazanmaya en yakın oyuncudur.");
    }


}
