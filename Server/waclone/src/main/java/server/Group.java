package server;

import java.util.ArrayList;
import java.util.UUID;

import javax.swing.text.AbstractDocument.Content;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;

import server.GlobalVariables.RequestType;

public class Group {
    private String groupId;
    private String groupName;
    private ArrayList<String> members;

    public Group(String groupName){
        this.groupId = UUID.randomUUID().toString();
        this.groupName = groupName;
        this.members = new ArrayList<>();
        while(GlobalVariables.groupCollection.countDocuments(Filters.eq("groupId", this.groupId)) > 0) {
            this.groupId = UUID.randomUUID().toString();
        }
        GlobalVariables.groupCollection.insertOne(toDocument());
    }

    public Group(Document doc){
        this.groupId = (String)doc.get("groupId");
        this.groupName = (String)doc.get("groupName");
        System.out.println(doc.get("members"));
        this.members = (ArrayList<String>)doc.get("members");
    }

    public Document toDocument(){
        Document obj = new Document().append("groupId", groupId).append("groupName", groupName).append("members", members);
        return obj;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public void addMember(String memberId) {
        members.add(memberId);
        
        // Update in DB
        GlobalVariables.groupCollection.updateOne(Filters.eq("groupId", groupId), Updates.addToSet("members", memberId));

        for(String member: members){ // Send ACK to all other grp members
            if(member.equals(memberId)) continue;
            Request ackRequest = new Request(RequestType.NewGroupMemberAdded, GlobalVariables.serverId, member, memberId, "NULL"); 
            GlobalVariables.sendMessageTo(member, ackRequest);
        }
        // Send ACK to added member
        Request request = new Request(RequestType.AddedToGroup, GlobalVariables.serverId, memberId, groupId, "NULL");
        GlobalVariables.sendMessageTo(memberId, request);
    }

    public void removeMember(String memberId){
        members.remove(memberId);

        // Update in DB
        if(members.size() > 0)
            GlobalVariables.groupCollection.updateOne(Filters.eq("groupId", groupId), Updates.pull("members", memberId));
        else    
            GlobalVariables.groupCollection.deleteOne(Filters.eq("groupId", groupId));


        for(String member: members){ // Send ACK to all other grp members
            if(member.equals(memberId)) continue;
            Request ackRequest = new Request(RequestType.GroupMemberRemoved, GlobalVariables.serverId, member, memberId, "NULL"); 
            GlobalVariables.sendMessageTo(member, ackRequest);
        }

        // Send ACK to removed member
        Request request = new Request(RequestType.RemovedFromGroup, GlobalVariables.serverId, memberId, groupId, "NULL");
        GlobalVariables.sendMessageTo(memberId, request);
    }

    @Override
    public String toString() {
        return "Group [groupId=" +groupId+ ", groupName=" + groupName + ", members=" + String.join(", ", members) + "]";
    }
}
