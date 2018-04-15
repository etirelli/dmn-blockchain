package com.methodandstyle.landregistry.utils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

public enum Account {

    GOVERNMENT("0xdfd0dbc3379c1fa8cdd4f5f884267c302c8c243e11a8c387b06d0e16be5d8e15",
            "0x40a9639bd950e1b04a6f0996a97fdc32efca0406f0b81c91605f2dad7683a3bc4773bfc830e6e6aea549f15086215250e01bae5e8e4dc399827dfc0f48f2a6b0"),
    SELLER("0xa56893bd1b94e50de68ade3ae1dfcd01bf59051889bf49c26971fca054ec0b41",
            "0x69b8d16df563f94ad573cf7c53db61b14d62b506dadc9574fb98d43a4219eb5f3e9d697a554a6839a755f515ce994a498d7370082a295604f55792f886a2ccc1"),
    BUYER("0x2ed4bc359b1a1725af4c4b2c4dcf495df140cd2ccbf1372f463fa5fd90b4adbc",
            "0x513cb77eed746e596d74798ba20aeb48d23c5602ee18b21f683b2a01b67cb5faf5d109160e5078112657b2093ec65f4de50436bca4658bfc457b949581ef479c"),
    CITY("0x5b0f7b86617ffd511692c9009b9acb6465da569d4292da1f0e33b72db5a33448",
            "0x5c0d54ce3ae0ab8ac4726c6594352d2310533697e7a4a9ec0f6369e6f6fbfd6a8e22637a78c20923163908b39557826d3a78689a95f80c5fd4d580cb6ece22ff"),
    PROVINCE("0x35131f3b56fee9cf8a1732c2755f285c1013b61c901642601f57ce6a1c7e6e93",
            "0xf7f8c31ac3e5b434fa841fca91ce5726aa5fb75240a68e00a4d067dc01cb961c2516951cf0ae4fadf165e90df423fe74b8264b6d1463995d72860c74e64eda8c"),
    INSURER("0xb45900f319b193b54394fc84c0ba72e966316ea53af2f2670df325cd9150c38e",
            "0x32fc3d6703bd61f6589451c41a9df5798de03f71aeeaaf08fe73628afb6f8845fca6832538a08c0a29c560f36ed3467da3502ad948489a3c930743acefb8ccb9");

    public final String PRIVATE_KEY;
    public final String PUBLIC_KEY;
    public final ECKeyPair KEY_PAIR;
    public final Credentials CREDENTIALS;
    public final String ADDRESS;

    Account( String privateKey, String publicKey ) {
        PRIVATE_KEY = privateKey;
        PUBLIC_KEY = publicKey;
        KEY_PAIR = new ECKeyPair(Numeric.toBigInt(PRIVATE_KEY), Numeric.toBigInt(PUBLIC_KEY));
        CREDENTIALS = Credentials.create(KEY_PAIR);
        ADDRESS = CREDENTIALS.getAddress();
    }


}
