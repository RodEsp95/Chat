//Allows the chat server to connect to multiple clients.

import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatConnectFile implements Runnable{
	ServerSocket server_socket;
	ArrayList<Socket> clients = new ArrayList<Socket>();
	HashMap<Socket,String> _clientsWithName = new HashMap<Socket,String>();
	
	public ChatConnectFile(ServerSocket server){
		server_socket = server;
	}
	
	@Override
	public void run() {
		try{
			while(true){
				Socket client = server_socket.accept();
				clients.add(client);
				_clientsWithName.put(client, "New client");
				ChatListenAllFile cl = new ChatListenAllFile(client, clients, _clientsWithName);
				Thread t1 = new Thread(cl);
				t1.start();
			}
		}catch (Exception e){
		}
	}
	
	
}