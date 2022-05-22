package greeting.server;

import com.proto.greeting.*;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class GreetingServerImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Override
    public void greet(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        responseObserver
                .onNext(GreetingResponse.newBuilder()
                        .setResult("Hello Hritik").build()
                );
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        GreetingResponse response = GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build();
        for (int i = 0; i <= 10; i++) {
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetingRequest> longGreet(StreamObserver<GreetingResponse> responseObserver) {
        StringBuilder sb = new StringBuilder();
        return new StreamObserver<GreetingRequest>() {
            @Override
            public void onNext(GreetingRequest value) {
                sb.append("Hello ");
                sb.append(value.getFirstName() + " ");
                sb.append("!\n");
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult(sb.toString()).build());
                responseObserver.onCompleted();
            }
        };

    }

    @Override
    public StreamObserver<GreetingRequest> greetEveryone(StreamObserver<GreetingResponse> responseObserver) {
        return new StreamObserver<GreetingRequest>() {
            @Override
            public void onNext(GreetingRequest value) {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult(
                        "Hello " + value.getFirstName()
                ).build());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void squareRoot(SqrtRequest request, StreamObserver<SqrtResponse> responseObserver) {
        int number = request.getNumber();

        if (number<0){
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Can't Process negative")
                            .augmentDescription("Number : "+number)
                    .asRuntimeException());

            return;
        }
        responseObserver.onNext(SqrtResponse.newBuilder().setResult(Math.sqrt(number)).build());
        responseObserver.onCompleted();

    }

    @Override
    public void greetWithDeadline(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        Context context = Context.current();
        try{
            for (int i =0 ; i< 3 ; ++i){
                if (context.isCancelled()){
                    return;
                }
                Thread.sleep(100);
            }
            responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello "+ request.getFirstName()).build());
            responseObserver.onCompleted();
        }catch (InterruptedException e){
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }
}
