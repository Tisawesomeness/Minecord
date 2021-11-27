package com.tisawesomeness.minecord.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NameUtils {

	// Mojang capes don't want to work, so here's a list of every single UUID with a Mojang cape
	public static final Set<String> mojangUUIDs = new HashSet<>(Arrays.asList(
			"5de530b469bf4b5c953a25819953b16f", "ee531ba1534a47209f692605dbf58a10", "ad93d7382b674b44984792918b8925f7", "34fc1bd64dbd4b37bde6919b9489ed63",
			"17d349da73ff4e6d94de49d64258d39e", "e0a4a12dda4c40fd80096725ed21bfb1", "89893e8f920e40a9947afbfdb93effd5", "898ed21379b249f98602d1bb03d19ba2",
			"0b8b22458018456c945d4282121e1b1e", "3ce72668b8f9497fbfb4809e47365e17", "f96f3d63fc7f46a7964386eb0b3d66cb", "c3ddaf580e9849038f13ac91379b2fec",
			"63b31801739e40579c7a88482c3aacc8", "61699b2ed3274a019f1e0ea8c3f06bc6", "ed4da3aba7e14b44b91322eb38fdae4b", "eb6449ffa3d14fe38e68cec0bf2fc11a",
			"e9cd1af9615240d7b9574969094e077c", "6bef7b0487cf40458cd1fe1ef23c427d", "3dba96d4cb25458d83229e7f02cc1ed0", "a7ec08830ab449b3acc4cdcf9ab44cf4",
			"4303c9f77f8842d188565c6be3bd413d", "e6b5c088068044df9e1b9bf11792291b", "4e1256f5c48d4e4b8dadf248108ee2ee", "e3aee364f38e42caa89e5effa8ddbbbd",
			"1be04a48aaf64eeaaf63845f321bf7af", "22cc6d73101f4e1981cc9a985f0aa364", "9a22a8e0ba84427698b2fcce2e40eb02", "853c80ef3c3749fdaa49938b674adae6",
			"e540ac6339ee482baba205524d8d7635", "d8f9a4340f2d415f9acfcd70341c75ec", "4f843993cd424cf59f9ba46237fade57", "7125ba8b1c864508b92bb5c042ccfe2b",
			"6a085b2c19fb4986b453231aa942bbec", "d787563cef8b47208654e7f74c4fb0a2", "975836b98c114424a08cb6cc3d8b146b", "8ecf8beb720c442c911ac9744e65bd9d",
			"b05881186e75410db2db4d3066b223f7", "1c0945aa6853492593f292d3c1693f20", "a63d3a672e96461e98b138b2ae2e2a06", "c3021db0192c41dfbc8562f7ec51d7ef",
			"c9b54008fd8047428b238787b5f2401c", "61887403c7994288b680d7d5859a20fe", "f0fe3b509fee43e2a1656c4526ac6473", "82f0dda5507749c29144b3ca655befc1",
			"0da56a7e1a0d4cd3845dfc2be2559142", "f8cdb6839e9043eea81939f85d9c5d69", "364beb7e65064a8d8e50569c4ab97eea", "069a79f444e94726a5befca90e38aaf5",
			"ff8f21675ba04d33a84eed78f613d890", "4b870d901d2c4bd3a647846757930e2e", "36e409d4a16f471d84a0c2d3b87824ad", "aaccfbd41145454791e9c3370a4303a7",
			"0be6054508c8498c8015e9d861f8ce10", "0535d36faee643989355a942b4a7f66b", "615d08470ddd4bc9a410355a79cdd519", "696a82ce41f44b51aa31b8709b8686f0",
			"6239656ec315456c82b003443cdfb16d", "874e749df3224733b49ab1de95b06048", "9c2ac9585de945a88ca14122eb4c0b9e", "bf2ae63b9cd7406eb3c3d3a3a996cb0f",
			"1c1bd09a6a0f4928a7914102a35d2670", "a5d04fe969ea4f12bf2b7f52172e839b", "4e7592ba958644509659e24eb84064ef", "6aa836ce053243faa52094efc50fe48c",
			"93ba87580abd4a3ea009d812d0d22d13", "be1fab1b23484bf9a8d527d94a35efa6", "d7155343ffae4c8bb654aaecfaa29ed2", "b9583ca43e64488a9c8c4ab27e482255",
			"91f8e3e17c464cd79ff9925c9abd8a3d", "3c87f35150c74af992b4de83c34a109a", "21d2c289b75f45139f4f85cc9e8c271b", "235101390b124101bbcf686fea2e5559",
			"c30c16fa05e247519f322b6dca5026b3", "2a7eee3d6a29498a85cfb53867d95fd7"));
	
	public static final String uuidRegex = "[a-f0-9]{32}|[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";
	public static final String playerRegex = "[0-9A-Za-z_]{1,16}";

	/**
	 * Gets a playername from a UUID
	 * @param uuid A UUID with or without dashes
	 */
	public static String getName(String uuid) {
		String url = "https://api.mojang.com/user/profiles/" + uuid + "/names";
		String request = RequestUtils.get(url);
		if (request == null) {
			return null;
		}
		JSONArray names = new JSONArray(request);
		return names.getJSONObject(names.length() - 1).getString("name");
	}
	
	/**
	 * Gets a UUID from a playername
	 */
	public static String getUUID(String playername) {
		return getUUIDInternal(playername);
	}
	
	/**
	 * Gets a UUID from a playername and Unix timestamp
	 */
	public static String getUUID(String playername, long timestamp) {
		return getUUIDInternal(playername + "?at=" + timestamp);
	}
	
	private static String getUUIDInternal(String query) {
		String url = "https://api.mojang.com/users/profiles/minecraft/" + query;
		String request = RequestUtils.get(url);
		if (request == null) {
			return null;
		}

		JSONObject response = new JSONObject(request);
		if (response.has("error")) {
			String error = response.getString("error");
			String errorMessage = response.getString("errorMessage");
			return error + ": " + errorMessage;
		}
		
		return response.getString("id");
	}
	
	/**
	 * Adds dashes to a UUID
	 */
	public static String formatUUID(String uuid) {
		return uuid.replaceFirst(
			"([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)",
			"$1-$2-$3-$4-$5"
		);
	}

}
