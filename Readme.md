# Gossip TTL Program
The program works on L2 with MAC-address. I user lib pcap4j for frames interaction. 

Program sends and recieves two types of messages: gossips form other such programs; message that another host was swithced on and wants to share gossips. 

Messages send with TTL (not ttl from L3, I developered my own implementation for L2) to N (determined for each host) random from local db.

When host recieves a message, it share it if ttl > 1.

The program works with unicast, multicast and broadcast addresses.

UI:
![image](https://raw.githubusercontent.com/manturovDan/GossipTTL/master/dumps/screen.png)
