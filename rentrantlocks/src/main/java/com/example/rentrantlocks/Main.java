package com.example.rentrantlocks;

import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


// Application will 2 separate threads one for UI so that application stays responsive
// and a background thread that will fetch the prices of assets from a exchange remote server (simulation via thread.sleep method).
// The background thread will update the prices in a shared PricesContainer object, and the UI thread will read the prices from this object.

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cryptocurrency Prices");

        GridPane grid = createGrid();
        Map<String, Label> cryptoLabels = createCryptoPriceLabels();

        addLabelsToGrid(cryptoLabels, grid);

        double width = 300;
        double height = 250;

        StackPane root = new StackPane();

        Rectangle background = createBackgroundRectangleWithAnimation(width, height);

        root.getChildren().add(background);
        root.getChildren().add(grid);

        primaryStage.setScene(new Scene(root, width, height));

        PricesContainer pricesContainer = new PricesContainer();

        PriceUpdater priceUpdater = new PriceUpdater(pricesContainer);

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                pricesContainer.getLock().lock(); // This will block the UI thread until the background thread releases the lock.
                // We need to make sure that we read the prices from the PricesContainer only when the background thread is not updating them.
                // Otherwise, blocking UI thread will cause the application to freeze.
                // This is case where we use reentrant lock, dodge the blocking of UI threads while PriceContainer object is locked.
                try {
                    Label bitcoinLabel = cryptoLabels.get("BTC");
                    bitcoinLabel.setText(String.valueOf(pricesContainer.getBitCoinPrice()));

                    Label etherLabel = cryptoLabels.get("ETH");
                    etherLabel.setText(String.valueOf(pricesContainer.getEthereumPrice()));

                    Label litecoinLabel = cryptoLabels.get("LTC");
                    litecoinLabel.setText(String.valueOf(pricesContainer.getLiteCoinPrice()));

                    Label bitcoinCashLabel = cryptoLabels.get("BCH");
                    bitcoinCashLabel.setText(String.valueOf(pricesContainer.getDogeCoinPrice()));

                    Label rippleLabel = cryptoLabels.get("XRP");
                    rippleLabel.setText(String.valueOf(pricesContainer.getRipplePrice()));
                } finally {
                    pricesContainer.getLock().unlock();
                }
            }
        };

        addWindowResizeListener(primaryStage, background);

        animationTimer.start();

        priceUpdater.start();

        primaryStage.show();
    }

    private void addWindowResizeListener(Stage stage, Rectangle background) {
        ChangeListener<Number> stageSizeListener = ((observable, oldValue, newValue) -> {
            background.setHeight(stage.getHeight());
            background.setWidth(stage.getWidth());
        });
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    private Map<String, Label> createCryptoPriceLabels() {
        Label bitcoinPrice = new Label("0");
        bitcoinPrice.setId("BTC");

        Label etherPrice = new Label("0");
        etherPrice.setId("ETH");

        Label liteCoinPrice = new Label("0");
        liteCoinPrice.setId("LTC");

        Label bitcoinCashPrice = new Label("0");
        bitcoinCashPrice.setId("BCH");

        Label ripplePrice = new Label("0");
        ripplePrice.setId("XRP");

        Map<String, Label> cryptoLabelsMap = new HashMap<>();
        cryptoLabelsMap.put("BTC", bitcoinPrice);
        cryptoLabelsMap.put("ETH", etherPrice);
        cryptoLabelsMap.put("LTC", liteCoinPrice);
        cryptoLabelsMap.put("BCH", bitcoinCashPrice);
        cryptoLabelsMap.put("XRP", ripplePrice);

        return cryptoLabelsMap;
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private void addLabelsToGrid(Map<String, Label> labels, GridPane grid) {
        int row = 0;
        for (Map.Entry<String, Label> entry : labels.entrySet()) {
            String cryptoName = entry.getKey();
            Label nameLabel = new Label(cryptoName);
            nameLabel.setTextFill(Color.BLUE);
            nameLabel.setOnMousePressed(event -> nameLabel.setTextFill(Color.RED));
            nameLabel.setOnMouseReleased((EventHandler) event -> nameLabel.setTextFill(Color.BLUE));

            grid.add(nameLabel, 0, row);
            grid.add(entry.getValue(), 1, row);

            row++;
        }
    }

    private Rectangle createBackgroundRectangleWithAnimation(double width, double height) {
        Rectangle backround = new Rectangle(width, height);
        FillTransition fillTransition = new FillTransition(Duration.millis(1000), backround, Color.LIGHTGREEN, Color.LIGHTBLUE);
        fillTransition.setCycleCount(Timeline.INDEFINITE);
        fillTransition.setAutoReverse(true);
        fillTransition.play();
        return backround;
    }

    @Override
    public void stop() {
        System.exit(0);
    }


    public static class PricesContainer {

        private Lock lock = new ReentrantLock();

        private double bitCoinPrice;
        private double ethereumPrice;
        private double liteCoinPrice;
        private double ripplePrice;
        private double dogeCoinPrice;


        public Lock getLock() {
            return lock;
        }

        public void setLock(Lock lock) {
            this.lock = lock;
        }

        public double getBitCoinPrice() {
            return bitCoinPrice;
        }

        public void setBitCoinPrice(double bitCoinPrice) {
            this.bitCoinPrice = bitCoinPrice;
        }

        public double getEthereumPrice() {
            return ethereumPrice;
        }

        public void setEthereumPrice(double ethereumPrice) {
            this.ethereumPrice = ethereumPrice;
        }

        public double getLiteCoinPrice() {
            return liteCoinPrice;
        }

        public void setLiteCoinPrice(double liteCoinPrice) {
            this.liteCoinPrice = liteCoinPrice;
        }

        public double getRipplePrice() {
            return ripplePrice;
        }

        public void setRipplePrice(double ripplePrice) {
            this.ripplePrice = ripplePrice;
        }

        public double getDogeCoinPrice() {
            return dogeCoinPrice;
        }

        public void setDogeCoinPrice(double dogeCoinPrice) {
            this.dogeCoinPrice = dogeCoinPrice;
        }
    }

    public static class PriceUpdater extends Thread {
        private final PricesContainer pricesContainer;
        private final Random random = new Random();
        public PriceUpdater(PricesContainer pricesContainer) {
            this.pricesContainer = pricesContainer;
        }

        @Override
        public void run() {
            while (true) {
                pricesContainer.getLock().lock();
                // We need make sure that update of prices is atomic operation,
                // so we use lock to ensure that no other thread can read or write to pricesContainer while we are updating it.
                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pricesContainer.setBitCoinPrice(random.nextDouble() * 10000);
                    pricesContainer.setEthereumPrice(random.nextDouble() * 1000);
                    pricesContainer.setLiteCoinPrice(random.nextDouble() * 500);
                    pricesContainer.setRipplePrice(random.nextDouble() * 2);
                    pricesContainer.setDogeCoinPrice(random.nextDouble() * 1);
                } finally {
                    pricesContainer.getLock().unlock();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}