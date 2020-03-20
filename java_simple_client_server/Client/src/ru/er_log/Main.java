package ru.er_log;

import ru.er_log.client.Client;

public class Main
{
    public static void main(String[] args)
    {
        Client client = new Client("127.0.0.1", 21998);
        client.write("Line #1\rLine #2\nLine #3\r");
        client.close();
    }
}
