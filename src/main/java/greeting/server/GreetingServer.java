package greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) {
        Server server = ServerBuilder
                .forPort(50051)
                .addService(new GreetingServerImpl())
                .build();
        try {
            server.start();
            System.out.println("Server Started on port 50051.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread( () -> {
                            System.out.println("Received Shutdown request");
                            server.shutdown();
                            System.out.println("Server Stopped");
                        })
                );

        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
