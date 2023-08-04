import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Hello world!");

        runAsync();
        supplyAsync();
        thenApply();
        thenAccept();

        System.out.println("MainThread!");
    }

    /**
     * runAsync won't return anything but, we can capture the future and
     * block the main thread using get() method
     */
    private static void runAsync() throws ExecutionException, InterruptedException {

        List<CompletableFuture<Void>> futures = new ArrayList<>(); 
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Running in separate thread than main thread " + finalI);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            });
            futures.add(async);
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0]);
        );

        System.out.println("runAsync Non-Blocking 1");
        allOf.join();
        System.out.println("runAsync Blocking 2");

    }

    /**
     * supplyAsync runs asynchronously and will return the value,
     * We can capture the result and access the value using get().
     */
    private static void supplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<String> async = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Binary Code";
        });
        System.out.println("runAsync non-blocking");
        String res = async.get();
        System.out.println("runAsync blocking " + res);

    }

    /**
     * thenApply accept the previous completable future and
     * returned result will be the input for then apply,
     * It will process and return.
     */
    private static void thenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Binary";
        });

        System.out.println("thenApply middle block");

        CompletableFuture<String> finaleFuture = future.thenApply(name -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return name + "Code";
        });

        System.out.println("thenApply before final block");
        String res = finaleFuture.get();
        System.out.println("thenApply blocking" + res);
    }

    /**
     * thenAccept will not return any value,
     * Only accept the previous completable future
     */
    private static void thenAccept() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Binary Code";
        }).thenAccept(res -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            System.out.println("Received value " + res);
        });

        System.out.println("thenAccept non-blocking");
        future.get();
        System.out.println("thenAccept blocking");
    }
}