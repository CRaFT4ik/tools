package ru.er_log.client;

import ru.er_log.utils.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client
{
    private final int PORT;
    private final String IP;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String IP, int PORT)
    {
        this.IP = IP;
        this.PORT = PORT;
        start();
    }

    private void start()
    {
        try
        {
            InetAddress addr = InetAddress.getByName(IP);

            Log.out("Connecting to " + IP + ":" + PORT + "...");
            socket = new Socket(addr, PORT);

            Log.out("Connected");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException x)
        {
            Log.err("I\\O error while connecting:", "", x.getMessage());
        }
    }

    public void write(String line)
    {
        out.write(line);
        out.flush();
    }

    public String read() throws IOException
    {
        return in.readLine();
    }

    public void close()
    {
        try
        {
            socket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
