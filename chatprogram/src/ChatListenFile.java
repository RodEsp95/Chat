/*
 * Reads and prints messages from other clients.
 * if requested for a file is made, then the client
 * may send a copy of the file, or
 * nothing if the file does not exist.
 * Client is also notified when 
 * another client has exited the chat.
 */

import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;

public class ChatListenFile implements Runnable{
	
	Socket client_socket;
	DataInputStream input;
	DataOutputStream output;
	ArrayList<ChatListenFile> clients = new ArrayList<ChatListenFile>();
	String _name;
	
	public ChatListenFile(Socket client) throws IOException{
		client_socket = client;
	}
	
	public ChatListenFile(Socket client, ArrayList<ChatListenFile> lst){
		client_socket = client;
		clients = lst;
	}
	
	@Override
	public void run() {
		try{
		input = new DataInputStream(client_socket.getInputStream());	
		output = new DataOutputStream(client_socket.getOutputStream());
		String message = "Placeholder";
		
		while(message.length() != 0){
			message = input.readUTF();
			if(message.equals("New client has joined the chat:")){
				System.out.println(message);
				message = input.readUTF();
				System.out.println(message);
			}
			if(message.equals("m") || message.equals("M")){
				message = input.readUTF();
				System.out.println(message);
			}
			if(message.equals("f") || message.equals("F")){
				String name = input.readUTF();
				File nFile = new File(input.readUTF());
				String requesterName = input.readUTF();	
				if(name.equals(_name)){
					if(nFile.exists()){
						System.out.println("Request for file: " + nFile.getName());
						output.writeUTF("Sending data for requested file");
						output.writeUTF(requesterName);
						output.writeUTF(nFile.getName());
						
						FileInputStream fileStream = new FileInputStream(nFile);
						byte[] fileData = new byte[(int)nFile.length()];
						BufferedInputStream holdData = new BufferedInputStream(fileStream);
						holdData.read(fileData, 0, fileData.length);
						output.writeInt((int)nFile.length());
						output.write(fileData, 0, fileData.length);
					}
					else{
						System.out.println("I don't have file: "+ nFile.getName());
					}
				}
			}
			if(message.equals("Sending data for requested file")){
				String filename = input.readUTF();
				FileOutputStream newFile = new FileOutputStream(filename);
				int count;
				int i = 0;
				int filesize = input.readInt();
				while(i < filesize){
					count = input.read();
					newFile.write(count);
					i++;
				}
				newFile.close();
				System.out.println(filename + " received");
			}
			
			if(message.equals("x") || message.equals("X")){
				message = input.readUTF();
				if(message.equals("Leave chat")){
					break;
				}
				else{
					System.out.println(message);
				}
			}
		}
		client_socket.close();
		return;
		}catch(Exception e){
			System.exit(1);
		}
			
	}
	
}