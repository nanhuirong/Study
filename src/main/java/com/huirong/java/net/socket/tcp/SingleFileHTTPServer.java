package com.huirong.java.net.socket.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by huirong on 17-3-21.
 */
public class SingleFileHTTPServer {
    private static final Logger logger = Logger.getLogger(SingleFileHTTPServer.class.toString());
    private final byte[] content;
    private final byte[] header;
    private final int port;
    private final String encoding;

    public SingleFileHTTPServer(byte[] data, String mimeType, int port, String encoding) {
        this.content = data;
        this.port = port;
        this.encoding = encoding;
        String header = "HTTP/1.0 200 OK\r\n"
                + "Server: OneFile 2.0\r\n"
                + "Content-length: " + this.content.length + "\r\n"
                + "Content-type: " + mimeType + "; charset=" + encoding + "\r\n\r\n";
        this.header = header.getBytes(Charset.forName("US-ASCII"));
    }

    public SingleFileHTTPServer(String data, String mimeType, int port, String encoding)
            throws UnsupportedEncodingException {
        this(data.getBytes(encoding), encoding, port, encoding);
    }

    public void start(){
        ExecutorService pool = Executors.newFixedThreadPool(100);
        try (ServerSocket server = new ServerSocket(this.port)){
            logger.info("accept connections on port" + server.getLocalPort());
            logger.info("data to be sent:");
            logger.info(new String(this.content, encoding));
            while (true){
                try {
                    Socket socket = server.accept();
                    pool.submit(new HTTPHandler(socket));
                }catch (IOException ex){
                    logger.log(Level.WARNING, "Exception accepting connection", ex);
                }catch (RuntimeException ex){
                    logger.log(Level.SEVERE, "Unexpected error");
                }
            }
        }catch (IOException ex){
            logger.log(Level.SEVERE, "Could not start server", ex);
        }
    }

    private class HTTPHandler implements Callable<Void>{
        private final Socket connection;

        public HTTPHandler(Socket connection) {
            this.connection = connection;
        }

        @Override
        public Void call() throws Exception {
            try {
                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                InputStream in = new BufferedInputStream(connection.getInputStream());
                //只读取第一行
                StringBuilder request = new StringBuilder(80);
                while (true){
                    int c = in.read();
                    if (c == '\r' || c == '\n' || c == -1)  break;
                    request.append((char)c);
                }
                //如果是http1.0或以后的版本, 发送一个MIME首部
                if (request.toString().indexOf("HTTP/") != -1)
                    out.write(header);
                out.write(content);
                out.flush();
            }catch (IOException ex){
                logger.log(Level.WARNING, "ERROR writing to client", ex);
            }finally {
                connection.close();
            }
            return null;
        }
    }

    public static void main(String[] args) {
        int port = 10000;
        String encoding = "UTF-8";
        String data = "nanhuirong";
        String mimeType = "mime";
        try {
            SingleFileHTTPServer server = new SingleFileHTTPServer(data, mimeType, port, encoding);
            server.start();
        }catch (Exception ex){

        }

    }
}
