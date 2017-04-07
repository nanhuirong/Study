package com.huirong.java.net.socket.tcp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by huirong on 17-3-21.
 */
public class MultithreadDaytimeServer {
    public static final int PORT = 10000;
    private static final Logger auditLogger = Logger.getLogger("requests");
    private static final Logger errorLogger = Logger.getLogger("errors");
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        try (ServerSocket server = new ServerSocket(PORT)){
            while (true){
                try {
                    Socket connect = server.accept();
                    Callable<Void> task = new DayTimeTask(connect);
                    executor.submit(task);
                }catch (IOException ex){
                    errorLogger.log(Level.SEVERE, "accept error ", ex);
                }catch (RuntimeException ex){
                    errorLogger.log(Level.SEVERE, "unexpected error " + ex.getMessage(), ex);
                }
            }
        }catch (IOException ex){
            errorLogger.log(Level.SEVERE, "couldn't start server", ex);
        }catch (RuntimeException ex){
            errorLogger.log(Level.SEVERE, "couldn't start server: " + ex.getMessage(), ex);
        }
    }
    private static class DayTimeTask implements Callable<Void>{
        private Socket connection;

        public DayTimeTask(Socket connection) {
            this.connection = connection;
        }
        @Override
        public Void call() {
            try {
                Date date = new Date();
                //先写入日志
                auditLogger.info(date + " " + connection.getReuseAddress());
                Writer writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(date.toString() + "\r\n");
                writer.flush();
            }catch (IOException ex){
                //客户端断开链接
            }finally {
                try {
                    if (connection != null){
                        connection.close();
                    }
                }catch (IOException e){
                    //
                }

            }
            return null;
        }
    }
}
