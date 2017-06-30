//Client-side program

//run as:
// java ChatClientFile -l <listening port number> -p <connect server port>


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class ChatClient{
	
	public static void main(String[] args){
		if(args.length == 2){
			if (args[0].equals("-l")){
			int portNum = Integer.valueOf(args[1]);
			try{
				Socket client = new Socket(InetAddress.getLoopbackAddress(), portNum);
				BufferedReader messenger = new BufferedReader(new
						InputStreamReader(System.in));
				DataOutputStream output = new DataOutputStream(client.getOutputStream());
				ChatListenFile cl = new ChatListenFile(client);
				Thread t1 = new Thread(cl);
				t1.start();
				System.out.println("What is your name?");
				String username = messenger.readLine();
				cl._name = username;
				output.writeUTF("New client's name is:");
				output.writeUTF(username);
				
				String line = "Placeholder";
				
				while(true){
					System.out.println("Enter an option: ('m', 'f', 'x'):");
					System.out.println("(M)essege (send)");
					System.out.println("(F)ile (request)");
					System.out.println("E(X)it");
					
					
					line = messenger.readLine();
					if(line.equals("m") || line.equals("M")){
						System.out.println("Enter message:");
						String message = messenger.readLine();
						output.writeUTF(line);
						output.writeUTF(username + ": " + message);
						
					}
					if(line.equals("f") || line.equals("F")){
						System.out.println("Who owns the file?");
						String name = messenger.readLine();
						System.out.println("Which file do you want?");
						String fileName = messenger.readLine();
						File nFile = new File(fileName);
						if(nFile.exists()){
							System.out.println("Already have file " + fileName);
						}
						else{
							output.writeUTF(line);
							output.writeUTF(name);
							output.writeUTF(fileName);
							output.writeUTF(username);
						}
					}
					if(line.equals("x") || line.equals("X")){
						Thread.sleep(1000);
						output.writeUTF(line);
						System.out.println("Closing your sockets...goodbye");
						t1.join();
						break;
					}
				}
				
				client.close();
			}catch (Exception e){	
			}
			}
		}else{
			System.out.println("Missing, wrong, or too many arguments");
		}
	}
}