import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;

import java.util.HashMap;
import java.util.logging.Level;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Poachie {


	public static void main(String[] args) throws IOException{
		final TS3Config config = new TS3Config();
		final TS3Query query;
		final TS3Api api;
		final int clientId;
		final int defaultChannel;
		final int functionChannelID;
		final HashMap<ChannelProperty, String> properties = new HashMap<>();
		FileReader configFile = new FileReader("config.txt");
		BufferedReader configReader = new BufferedReader(configFile);
		String host, username, password;

		host = configReader.readLine().substring(5);
		username = configReader.readLine().substring(9);
		password = configReader.readLine().substring(9);
		functionChannelID = Integer.parseInt(configReader.readLine().substring(10));
		
		config.setHost(host);
		config.setDebugLevel(Level.ALL);
		config.setFloodRate(FloodRate.UNLIMITED);
		
		query = new TS3Query(config);
		query.connect();

		
		api = query.getApi();
		api.login(username, password);
		api.selectVirtualServerById(1);
		api.setNickname("Poachie");
		api.sendChannelMessage("Poachie is online.");
		
		properties.put(ChannelProperty.CHANNEL_FLAG_TEMPORARY, "1");
		properties.put(ChannelProperty.CPID, String.valueOf(functionChannelID));
		properties.put(ChannelProperty.CHANNEL_CODEC_QUALITY, "10");

		// Get our own client ID by running the "whoami" command
		clientId = api.whoAmI().getId();
		defaultChannel = api.whoAmI().getChannelId();

		// Listen to chat in the channel the query is currently in
		// As we never changed the channel, this will be the default channel of the server
		api.registerEvent(TS3EventType.CHANNEL, 0);
		//add listeners
		api.addTS3Listeners(new TS3EventAdapter() {
			@Override
			public void onClientMoved(ClientMovedEvent e) {
				if(e.getTargetChannelId()==functionChannelID){
					int newChannel = api.createChannel(api.getClientInfo(e.getClientId()).getNickname() + "'s Channel", properties);
					api.moveClient(e.getClientId(), newChannel);
					api.setClientChannelGroup(5, newChannel, api.getClientInfo(e.getClientId()).getDatabaseId());
					api.moveClient(clientId, defaultChannel);
				}
			}
		});
}
}
