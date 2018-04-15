package com.methodandstyle.landregistry.utils;

import java.math.BigInteger;

/**
 * Utility class to interact with the Ethereum network.
 *
 * Originally created by Matthias Zimmermann
 * Available at: https://github.com/matthiaszimmermann/web3j_demo
 *
 * Used under Apache v2 license
 */
public class Web3jConstants {

	public static final String CLIENT_IP = "localhost";
	public static final String CLIENT_PORT = "8545";

	// see https://www.reddit.com/r/ethereum/comments/5g8ia6/attention_miners_we_recommend_raising_gas_limit/
	public static final BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);
	
	// http://ethereum.stackexchange.com/questions/1832/cant-send-transaction-exceeds-block-gas-limit-or-intrinsic-gas-too-low
	public static final BigInteger GAS_LIMIT_ETHER_TX = BigInteger.valueOf(21_000);
	public static final BigInteger GAS_LIMIT_CONTRACT_TX = BigInteger.valueOf(4_000_000L);

	public static final int CONFIRMATION_ATTEMPTS = 40;
	public static final int SLEEP_DURATION = 1000;
}
