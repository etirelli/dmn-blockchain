package com.methodandstyle.landregistry;

import com.methodandstyle.landregistry.utils.Web3jConstants;
import com.methodandstyle.landregistry.utils.Web3jUtils;
import org.junit.Before;
import org.junit.Test;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class CreateAccount {
    public static Web3j web3j = null;
    public static boolean setupFailed = false;

    @Before
    public void setUp() throws Exception {
        web3j = Web3jUtils.buildHttpClient("localhost", Web3jConstants.CLIENT_PORT);

    }


    @Test
    public void testCreateAccountFromScratch() throws Exception {
        // create new private/public key pair
        ECKeyPair keyPair = Keys.createEcKeyPair();

        BigInteger publicKey = keyPair.getPublicKey();
        String publicKeyHex = Numeric.toHexStringWithPrefix(publicKey);

        BigInteger privateKey = keyPair.getPrivateKey();
        String privateKeyHex = Numeric.toHexStringWithPrefix(privateKey);

        // create credentials + address from private/public key pair
        Credentials credentials = Credentials.create(new ECKeyPair(privateKey, publicKey));
        String address = credentials.getAddress();

        // print resulting data of new account
        System.out.println("private key: '" + privateKeyHex + "'");
        System.out.println("public key: '" + publicKeyHex + "'");
        System.out.println("address: '" + address + "'\n");

        // test (1) check if it's possible to transfer funds to new address
        BigInteger amountWei = Convert.toWei("0.131313", Convert.Unit.ETHER).toBigInteger();
        transferWei(getCoinbase(), address, amountWei);

        BigInteger balanceWei = getBalanceWei(address);
        BigInteger nonce = getNonce(address);

        assertEquals("Unexpected nonce for 'to' address", BigInteger.ZERO, nonce);
        assertEquals("Unexpected balance for 'to' address", amountWei, balanceWei);

        // test (2) funds can be transferred out of the newly created account
        BigInteger txFees = Web3jConstants.GAS_LIMIT_ETHER_TX.multiply(Web3jConstants.GAS_PRICE);
        RawTransaction txRaw = RawTransaction
                .createEtherTransaction(
                        nonce,
                        Web3jConstants.GAS_PRICE,
                        Web3jConstants.GAS_LIMIT_ETHER_TX,
                        getCoinbase(),
                        amountWei.subtract(txFees));

        // sign raw transaction using the sender's credentials
        byte[] txSignedBytes = TransactionEncoder.signMessage(txRaw, credentials);
        String txSigned = Numeric.toHexString(txSignedBytes);

        // send the signed transaction to the ethereum client
        EthSendTransaction ethSendTx = web3j
                .ethSendRawTransaction(txSigned)
                .sendAsync()
                .get();

        Response.Error error = ethSendTx.getError();
        String txHash = ethSendTx.getTransactionHash();
        assertNull(error);
        assertFalse(txHash.isEmpty());

        waitForReceipt(txHash);

        assertEquals("Unexpected nonce for 'to' address", BigInteger.ONE, getNonce(address));
        assertTrue("Balance for 'from' address too large: " + getBalanceWei(address), getBalanceWei(address).compareTo(txFees) < 0 );
    }


    void ensureFunds(String address, BigInteger amountWei) throws Exception {
        BigInteger balance = getBalanceWei(address);

        if (balance.compareTo(amountWei) >= 0) {
            return;
        }

        BigInteger missingAmount = amountWei.subtract(balance);
        Web3jUtils.transferFromCoinbaseAndWait(web3j, address, missingAmount);
    }

    TransactionReceipt waitForReceipt(String transactionHash) throws Exception {
        return Web3jUtils.waitForReceipt(web3j, transactionHash);
    }

    BigInteger getBalanceWei(String address) throws Exception {
        return Web3jUtils.getBalanceWei(web3j, address);
    }

    BigDecimal toEther(BigInteger weiAmount) {
        return Web3jUtils.weiToEther(weiAmount);
    }

    String transferWei(String from, String to, BigInteger amountWei) throws Exception {
        BigInteger nonce = getNonce(from);
        Transaction transaction = Transaction.createEtherTransaction(
                from, nonce, Web3jConstants.GAS_PRICE, Web3jConstants.GAS_LIMIT_ETHER_TX, to, amountWei);

        EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).sendAsync().get();
        System.out.println("transferEther. nonce: " + nonce + " amount: " + amountWei + " to: " + to);

        String txHash = ethSendTransaction.getTransactionHash();
        waitForReceipt(txHash);

        return txHash;
    }

    BigInteger getNonce(String address) throws Exception {
        return Web3jUtils.getNonce(web3j, address);
    }

    String getCoinbase() {
        return getAccount(0);
    }

    String getAccount(int i) {
        try {
            EthAccounts accountsResponse = web3j.ethAccounts().sendAsync().get();
            List<String> accounts = accountsResponse.getAccounts();

            return accounts.get(i);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "<no address>";
        }
    }

    static String load(String filePath) throws URISyntaxException, IOException {
        URL url = CreateAccount.class.getClass().getResource(filePath);
        byte[] bytes = Files.readAllBytes(Paths.get(url.toURI()));
        return new String(bytes);
    }

}
