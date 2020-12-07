# WA-server
Database - sql
  1 table (Phone number, User Data) 
Java
Marshmellow
Android studio latest
OPEN JDK

Two Urls
1st for connection establishing
Login Process
  http:<ip>/connect?number=1234567890
  
  connect returns true/false  
  
  signup - post request
     phone number and name
     return true/false

2nd for sockets
Messaging Process

Netty create 1 listening sockets and multiple client communication socket
  #Store in memory client to socket identifier 
  Processing thread - Service requests from queue
  Listening thread - Push Queue 
  
  Processing Thread:
    10
    1,7,2,3,1,3,4,5 
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
  
APP:
  Login Screen - 
  
A -> B

A - B  A-> server



  
