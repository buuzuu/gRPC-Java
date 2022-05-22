package greeting.client;

import com.proto.greeting.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            System.out.println("Need an argument to work.");
            return;
        }

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "greet":
                doGreet(channel);
                break;
            case "greet_many_times":
                doGreetManyTimes(channel);
                break;
            case "long_greet":
                doLongGreet(channel);
                break;
            case "greet_everyone":
                doGreetEveryone(channel);
                break;
            case "sqrt":
                doSqrt(channel);
                break;
            case "greet_deadline":
                doGreetWithDeadline(channel);
                break;
            default:
                System.out.println("Keyword Invalid " + args[0]);
        }
        System.out.println("Shutting down channel");
        channel.shutdown();

    }

    private static void doGreetWithDeadline(ManagedChannel channel) {
        System.out.println("Inside Deadline");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub

                .withDeadline(Deadline.after(3,TimeUnit.SECONDS))
                .greetWithDeadline(GreetingRequest.newBuilder().setFirstName("Hritik").build());

        System.out.println("Greeting within deadline : "+ response.getResult());

        try{
            response = stub
                    .withDeadline(Deadline.after(100,TimeUnit.MILLISECONDS))
                    .greetWithDeadline(GreetingRequest.newBuilder().setFirstName("Gupta").build());
            System.out.println("Greeting  deadline EXCEEDED : "+ response.getResult());
        }catch (StatusRuntimeException e){
            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED){
                System.out.println("Deadline Exceeded");
            }else{
                System.out.println("Got an exception " );
                e.printStackTrace();
            }
        }



    }

    private static void doSqrt(ManagedChannel channel) {
        System.out.println("Inside dosqrt()");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        SqrtResponse response = stub.squareRoot(SqrtRequest.newBuilder().setNumber(25).build());
        System.out.println("Sqrt od 25 is "+response.getResult());
/*        try{
            response = stub.squareRoot(SqrtRequest.newBuilder().setNumber(-1).build());
            System.out.println("Sqrt od -1 is "+response.getResult());

        }catch (Exception e){
            e.printStackTrace();
        }*/


    }

    private static void doGreetEveryone(ManagedChannel channel) throws InterruptedException {
        System.out.println("Do Greet Everyone");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetingRequest> stream = stub.greetEveryone(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse value) {
                System.out.println(value.getResult());
            }
            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });
        Arrays.asList("sgsg","shsdhd","hythd")
                .forEach(name -> stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build()));
        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doLongGreet(ManagedChannel channel) throws InterruptedException {
        System.out.println("Long Greet");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);
        List<String> names = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        Collections.addAll(names, "Hritik", "Inder", "Parul");
        StreamObserver<GreetingRequest> stream = stub.longGreet(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse value) {
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });
        for (String s : names){
            stream.onNext(GreetingRequest.newBuilder().setFirstName(s).build());
        }
        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);

    }

    private static void doGreetManyTimes(ManagedChannel channel) {
        System.out.println("Do Greet ManyTimes");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        stub.greetManyTimes(GreetingRequest.newBuilder().setFirstName("Test").build()).forEachRemaining( response -> {
            System.out.println(response.getResult());
        });


    }

    private static void doGreet(ManagedChannel channel) {
        System.out.println("Entered doGreet()");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Parul").build());
        System.out.println("Greeting: " + response.getResult());
    }
}
