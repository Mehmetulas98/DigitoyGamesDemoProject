package org.example;

public class main {
    public static void main(String[] args) {
        // Burası program çalışınca asıl çalışan yer ve Obje yapısı ile hem oyun oluşturma kolaylığı hem de OOP yapısını uyguladım.
        OkeyGame okeyGame = new OkeyGame();
        // Burası taşların oluşturulmasını ve random bir şekilde karıştırılmasını sağlayan fonksiyonu çağırır.
        okeyGame.startInitialTiles();
        // game objesi üzerinden gösterge ve okey seçimi yapılan fonksiyondur.
        okeyGame.selectIndicatorAndOkeyTiles();
        // Burada oluşturulan taşlar oyunculara dağıtılır.
        okeyGame.shareTiles();
        // Oyuncularım sahip olduğu taşlar üzerinden seri-çift gitme durumuna göre puanlama yapar ve console üzerinde gösteren fonksiyon.
        okeyGame.evaluateAndDisplayHands();
    }

}