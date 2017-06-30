/*
 * Allows server to forward a message from a client
 * to all other connected clients.
 * If a request for a file is made, then
 * the request is sent to the client specified
 * by the requester.
 * If a client exits the chat, all other clients are
 * notified that the client has left the chat.
 */

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;

public class ChatListenAllFile implements Runnable{
	
	Socket client_socket;
	ArrayList<Socket> clientList;
	String _name = null;
	HashMap<Socket,String> clientWithName = new HashMap<Socket,String>();
	
	public ChatListenAllFile(Socket client){
		client_socket = client;
	}
	
	public ChatListenAllFile(Socket client, ArrayList<Socket> lst){
		client_socket = client;
		clientList = lst;
	}
	
	public ChatListenAllFile(Socket client, ArrayList<Socket> lst, HashMap<Socket,String> lstNames){
		client_socket = client;
		clientList = lst;
		clientWithName = lstNames;
	}
	
	
	@Override
	public void run(){
		
		try{
			DataInputStream input = new DataInputStream(client_socket.getInputStream());
			String line = "Placeholder";
			while(line.length() != 0){
				line = input.readUTF();
				
				if(line.equals("New client's name is:")){
					line = input.readUTF();
					_name = line;
					System.out.println(_name + " has joined the chat.");
					clientWithName.put(client_socket, _name);
					for(Socket c: clientList){
						if(!c.equals(client_socket)){
							DataOutputStream output = new DataOutputStream(c.getOutputStream());
							output.writeUTF("New client has joined the chat:");
							output.writeUTF(_name);
						}
					}
				}
				if (line.equals("m") || line.equals("M")){
					String message = input.readUTF();
					for(Socket c : clientList){
						if (!c.equals(client_socket)){
							DataOutputStream output = new DataOutputStream(c.getOutputStream());
							output.writeUTF(line);
							output.writeUTF(message);
						}
					}
					
				}
				if (line.equals("f") || line.equals("F")){
					String name = input.readUTF();
					String fileName = input.readUTF();
					String requesterName = input.readUTF();
					for(Socket c : clientList){
						if(!c.equals(client_socket)){
							DataOutputStream output = new DataOutputStream(c.getOutputStream());
							output.writeUTF(line);
							output.writeUTF(name);
							output.writeUTF(fileName);
							output.writeUTF(requesterName);
						}
					}
				}
				if (line.equals("x") || line.equals("X")){
					for(Socket c: clientList){
						if(!c.equals(client_socket)){
							DataOutputStream output = new DataOutputStream(c.getOutputStream());
							output.writeUTF(line);
							output.writeUTF(clientWithName.get(client_socket) + " has left chat");
						}
						else{
							DataOutputStream output = new DataOutputStream(c.getOutputStream());
							output.writeUTF(line);
							output.writeUTF("Leave chat");
						}
					}
					clientList.remove(client_socket);
					clientWithName.remove(client_socket);
				}
				if (line.equals("Sending data for requested file")){
					String requesterName = input.readUTF();
					String filename = input.readUTF();
					File oldFile = new File(filename);
					if(oldFile.exists()){
						oldFile.delete();
					}
					FileOutputStream createdFile = new FileOutputStream(filename);
					int filesize = input.readInt();
					int count;
					int i = 0;
					while(i < filesize){
						count = input.read(); 
						createdFile.write(count);
						i++;
					}
					createdFile.close();
					
					File nFile = new File(filename);
					FileInputStream fileStream = new FileInputStream(nFile);
					byte[] fileData = new byte[(int)nFile.length()];
					BufferedInputStream holdData = new BufferedInputStream(fileStream);
					holdData.read(fileData, 0, fileData.length);
					for (Socket c: clientList){
						if(clientWithName.get(c).equals(requesterName)){
							DataOutputStream output = new DataOutputStream(c.getOutputStream());
							output.writeUTF(line);
							output.writeUTF(filename);
							output.writeInt((int)nFile.length());
							output.write(fileData, 0, fileData.length);
						}
					}
				}
			}
			client_socket.close();
		}catch (Exception e){
		}
	}
}