# WA-server
Database - sql
  1 table (Phone number, User Data) 
Java
Marshmellow
Android studio latest
OPEN JDK

Multiple Urls
-for connection establishing

-for messaging
Messaging Process

create 1 listening sockets and multiple client communication socket
  #Store in memory client to socket identifier 
  Processing thread - Service requests from queue
  Listening thread - Push Queue 
  
  Processing Thread:
    10
    1,7,2,3,1,3,4,5 
  
  UserInfo Thread:
  http:<ip>/getUserData?number= 
  
  returns null if not exists or returns data
  
  Login thread:
  http:<ip>/connect?number=1234567890
  
  connect returns true/false  
  
  signup - post request
     phone number and name
     return true/false

  Sender 
  Client connect start a worker thread id same as client 
  Maintain a send box for every client
  Processing Thread takes a request and adds it to coresponding send box
  
  worker thread:
    while(true)
    {
      if(send_box.size())
      {
          if(!send(send_box.pop()))
          {
            // declare offline
          }
      }
    }
  
A -> B

A - B  A-> server


CLIENT:
  Login Screen - 
  List of users - 
  Chat Activity - 
  
  List of users - 
  #Profile Activity - 
  
  Store n+1 files if n users
  1 file per user for chats
  1 file for list of users and latest message
  
  Chats file
  endl seperated each entry of message class
  
  Userlist file
  
  Start new Chat
    
Message Class
{
  String txt;
  String sender;
  String reciever;
}
