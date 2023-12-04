package org.example;


import com.rabbitmq.client.*;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Main {
    public static String DB_URL = "jdbc:mysql://localhost:3306/albums";
    public static String USER = "root";
    // local password "MyNewPass"
    public static String PASS = "MyNewPass";
    public static String QUEUE_NAME = "reviewQueue";

    private final static int THREAD_COUNT = 10;
//    public static void main(String[] args) throws IOException, TimeoutException, SQLException, ClassNotFoundException {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//
//
//                // Set other connection properties if necessary
//
//                try (Connection connection = factory.newConnection();
//                     Channel channel = connection.createChannel()) {
//
//                    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
//                    System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");
//
//                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//                        String message = new String(delivery.getBody(), "UTF-8");
//                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//                        System.out.println(" [x] Received '" + message + "'");
//                        try {
//                            updateDatabase(message);
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        } catch (ClassNotFoundException e) {
//                            throw new RuntimeException(e);
//                        }
//                    };
//
//                    channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
//                } catch (IOException | TimeoutException e) {
//                    throw new RuntimeException(e);
//                }
//    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // Set other connection properties if necessary

        try {
            Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                Runnable task = new MessageProcessor(delivery, channel);
                executorService.submit(task);
            };

            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private static class MessageProcessor implements Runnable {
        private final Delivery delivery;
        private final Channel channel;

        public MessageProcessor(Delivery delivery, Channel channel) {
            this.delivery = delivery;
            this.channel = channel;
        }

        @Override
        public void run() {
            try {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                updateDatabase(message);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private static void updateDatabase(String message) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (java.sql.Connection dbConnection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String[] messageParts = message.split(" ");
            String likeOrNot = messageParts[0];
            String albumID = messageParts[1];

            // Determine the column to update based on likeOrNot value
            String columnToUpdate = "like".equals(likeOrNot) ? "like" : "dislike";

            // SQL query to increment the likes or dislikes count
            String query = "UPDATE albums SET `" + columnToUpdate + "` = `" + columnToUpdate + "` + 1 WHERE `id` = ?";

            try (PreparedStatement preparedStatement = (dbConnection.prepareStatement(query))) {
                preparedStatement.setString(1, albumID);
                int updatedRows = preparedStatement.executeUpdate();

                if (updatedRows == 0) {
                    System.out.println("Updating album review failed, no rows affected.");
                }
            }
        }
    }
}