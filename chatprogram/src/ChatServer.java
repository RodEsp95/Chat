//Server-side program

//run as:
// java ChatServerFile <listening port number>
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;

public class ChatServer{
	
	public static void main(String[] args){
		
		if(args.length == 1){
				int portNum = Integer.valueOf(args[0]);
				try{
					ServerSocket server = new ServerSocket(portNum);
					ChatConnectFile cc = new ChatConnectFile(server);
					Thread t1 = new Thread(cc);
					t1.start();
					BufferedReader message = new BufferedReader(new InputStreamReader(System.in));
					String line = "Placeholder";
					while(true){
						System.out.println("Enter an option: ('m', 'f', 'x'):");
						System.out.println("(M)essege (send)");
						System.out.println("(F)ile (request)");
						System.out.println("E(X)it");
						line = message.readLine();
						if(line.equals("m") || line.equals("M")){
							System.out.println("Enter message:");
							String m = message.readLine();
							for(Socket c : cc.clients){
								DataOutputStream output = new DataOutputStream(c.getOutputStream());
								output.writeUTF(line);
								output.writeUTF(m);
							}
						}
						if(line.equals("f") || line.equals("F")){
							System.out.println("Who owns the file?");
							String name = message.readLine();
							System.out.println("Which file do you want?");
							String fileName = message.readLine();
							for (Socket c : cc.clients){
								DataOutputStream output = new DataOutputStream(c.getOutputStream());
								output.writeUTF(line);
								output.writeUTF(name);
								output.writeUTF(fileName);
							}
						}
						if(line.equals("x") || line.equals("X")){
							System.out.println("Closing your sockets...goodbye");
							break;
						}
						
					}
					server.close();
					System.exit(0);
				}catch (Exception e){
				}
		}
		else{
			System.out.println("Missing, wrong, or too many arguments");
		}
	}
}