package portScanner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PortScanner {

    public static void main(String[] args) {
        List<Integer> openPorts = portScan("192.168.1.2");
        openPorts.forEach(port -> System.out.println("port " + port + " is open"));
    }

    public static List<Integer> portScan(String ip) {
        ConcurrentLinkedQueue<Integer> openPorts = new ConcurrentLinkedQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        AtomicInteger port = new AtomicInteger(0);
        while (port.get() < 65535) {
            final int currentPort = port.getAndIncrement();
            executorService.submit(() -> {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, currentPort), 200);
                    socket.close();
                    openPorts.add(currentPort);
                    System.out.println(ip + " ,port open: " + currentPort);
                }
                catch (IOException e) {
                }

            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Integer> openPortList = new ArrayList<>();
        System.out.println("openPortsQueue: " + openPorts.size());
        while (!openPorts.isEmpty()) {
            openPortList.add(openPorts.poll());
        }
        return openPortList;
    }

}
