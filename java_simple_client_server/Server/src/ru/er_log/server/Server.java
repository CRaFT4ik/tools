package ru.er_log.server;

import ru.er_log.utils.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server
{
    private static final int socketCloseTimeout = 10000;

    private final int PORT;
    private final List<Client> clientsConnected = Collections.synchronizedList(new ArrayList<>());

    private ServerSocket serverSocket;

    private boolean serverStopFlag = false;

    public Server(int PORT)
    {
        this.PORT = PORT;
        start();
    }

    private void start()
    {
        try
        {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex)
        {
            Log.err("I\\O error while creating server socket", ":", ex.getMessage());
        }

        Thread t1 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!serverStopFlag)
                {
                    try
                    {
                        accept();
                        Thread.sleep(250);
                    } catch (IOException | InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread t2 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!serverStopFlag)
                {
                    synchronized (clientsConnected)
                    {
                        Iterator<Client> i = clientsConnected.iterator();
                        while (i.hasNext())
                        {
                            Client client = i.next();
                            if (client.socket.isClosed())
                            {
                                disconnectClient(client, i);
                                continue;
                            }

                            String line = client.readLine();
                            if (line != null) parse(client, line);
                        }
                    }

//                    try
//                    {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }
                }
            }
        });

        Thread t3 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Iterator<Client> i;
                long currentTime;
                while (!serverStopFlag)
                {
                    synchronized(clientsConnected)
                    {
                        currentTime = System.currentTimeMillis();
                        i = clientsConnected.iterator();
                        while (i.hasNext())
                        {
                            Client client = i.next();
                            if (currentTime - client.timeLastActive > socketCloseTimeout)
                                disconnectClient(client, i);
                        }
                    }

                    try
                    {
                        Thread.sleep(socketCloseTimeout / 10);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        t1.setName("Thread_NewConnectionsAccept");
        t1.start();

        t2.setName("Thread_ParseClientData");
        t2.start();

        t3.setName("Thread_CheckLostConnections");
        t3.start();

        Log.out("Server launched on port " + PORT);
    }

    private void parse(Client client, String request)
    {
        Log.debug("Parsing string from client [" + client.id + "]: " + request);
    }

    private void accept() throws IOException
    {
        Socket socket = serverSocket.accept();
        Client client = new Client(socket);
        clientsConnected.add(client);

        Log.out("Client [" + client.id + "] connected: " + client.socket.getInetAddress().getHostAddress() + ":" + client.socket.getPort());
    }

    public void close()
    {
        try
        {
            serverStopFlag = true;
            serverSocket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void disconnectClient(Client client, Iterator<Client> i)
    {
        if (client == null) return;

        try
        {
            client.in.close();
            client.out.close();
            if (!client.socket.isClosed())
                client.socket.close();
            i.remove();
            Log.out("Client [" + client.id + "] disconnected (live time: " + String.format("%.1f", (System.currentTimeMillis() - client.timeConnected) / 1000f) + " sec)");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public Client getClientById(int id)
    {
        synchronized (clientsConnected)
        {
            for (Client client : clientsConnected)
                if (client.id == id) return client;
        }

        return null;
    }

    public static class Client
    {
        private static Integer id_counter = 0;

        private final int id;
        private final Socket socket;

        private final BufferedReader in;
        private final PrintWriter out;

        private final long timeConnected;
        private long timeLastActive;

        private Client(Socket socket) throws IOException
        {
            this.socket = socket;
            synchronized (id_counter)
            {
                id_counter++;
                id = id_counter;
            };

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            timeConnected = System.currentTimeMillis();
            timeLastActive = timeConnected;
        }

        public int getId()
        {
            return id;
        }

        public void sendLine(String line)
        {
            out.write(line);
            out.flush();
        }

        public String readLine()
        {
            String line = null;
            try { line = in.readLine(); }
            catch (IOException e) { e.printStackTrace(); }

            if (line != null)
                timeLastActive = System.currentTimeMillis();

            return line;
        }
    }
}

