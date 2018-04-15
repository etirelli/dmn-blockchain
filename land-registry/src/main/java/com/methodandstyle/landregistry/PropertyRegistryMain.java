package com.methodandstyle.landregistry;

import com.methodandstyle.landregistry.contracts.PropertyRegistry;
import com.methodandstyle.landregistry.utils.Account;
import com.methodandstyle.landregistry.utils.Web3jConstants;
import com.methodandstyle.landregistry.utils.Web3jUtils;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNResult;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.DMNServicesClient;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.utils.Async;
import org.web3j.utils.Numeric;
import rx.Observable;
import rx.Subscription;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import static com.methodandstyle.landregistry.utils.Web3jUtils.etherToWei;

public class PropertyRegistryMain {

    private static final String KIE_SERVER_URL = "http://localhost:8080/kie-server/services/rest/server";
    private static final String KIE_SERVER_USER = "kieserver";
    private static final String KIE_SERVER_PASSWORD = "kieserver1!";
    private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;

    private Web3j web3j = null;
    private String clientUrl = null;

    private CountDownLatch latch = new CountDownLatch(1);

    public PropertyRegistryMain() {
        String ip = Web3jConstants.CLIENT_IP;
        String port = Web3jConstants.CLIENT_PORT;
        clientUrl = String.format("http://%s:%s", ip, port);
        web3j = Web3j.build(new HttpService(clientUrl), 2000, Async.defaultExecutorService());

        Web3ClientVersion client = null;
        try {
            client = web3j
                    .web3ClientVersion()
                    .send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Connected to " + client.getWeb3ClientVersion() + "\n");
    }

    public void run() {
        try {
            addFunds();
            PropertyRegistry registry = deployContract();
            Subscription subscription = subscribeOracle(registry);
            waitForEnter();
            initiateSale( registry );
            waitForEnter();
            submitBuyerInformation( registry );
            waitForEnter();
            finalizeSale( registry );
            unsubscribeOracle( registry, subscription );
            waitForEnter();
            killContract( registry );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitForEnter() {
        System.out.print("Press <enter> to continue...");
        Scanner scanner = new Scanner( System.in );
        scanner.nextLine();
    }

    private void addFunds() throws Exception {
        initAccount( Account.SELLER, etherToWei( BigDecimal.valueOf( 10 ) )  );
        initAccount( Account.BUYER, etherToWei( BigDecimal.valueOf( 10000 ) )  );
        initAccount( Account.CITY, etherToWei( BigDecimal.valueOf( 10 ) )  );
        initAccount( Account.PROVINCE, etherToWei( BigDecimal.valueOf( 10 ) )  );
        initAccount( Account.INSURER, etherToWei( BigDecimal.valueOf( 10 ) )  );
        printBalance( "init", Account.GOVERNMENT );
    }

    private void initAccount( Account account, BigInteger initialBalance ) throws Exception {
        System.out.println("Adding funds to account: "+account.toString());
        BigInteger currentBalance = Web3jUtils.getBalanceWei(web3j, account.ADDRESS);
        if( currentBalance.compareTo(initialBalance) < 0) {
            Web3jUtils.transferFromCoinbaseAndWait(web3j, account.ADDRESS, initialBalance.subtract(currentBalance));
        }
        printBalance("init", account );
        System.out.println();
    }

    private void printBalance(String info, Account account) throws Exception {
        System.out.println( account.toString() + " account balance (" + info + "): " + account.ADDRESS + " = " + Web3jUtils.weiToEther(Web3jUtils.getBalanceWei(web3j, account.ADDRESS)));
    }

    private PropertyRegistry deployContract() throws Exception {
        System.out.println("-- Deploying contract PropertyRegistry");

        PropertyRegistry contract = PropertyRegistry
                .deploy(web3j,
                        Account.GOVERNMENT.CREDENTIALS,
                        Web3jConstants.GAS_PRICE,
                        Web3jConstants.GAS_LIMIT_CONTRACT_TX,
                        Account.SELLER.ADDRESS,     // seller wallet
                        Account.CITY.ADDRESS,       // city wallet
                        Account.PROVINCE.ADDRESS,   // province wallet
                        Account.INSURER.ADDRESS,    // insurer wallet
                        "123456789",                // registration number
                        "99 Lakeshore",                 // street address
                        "Toronto",                              // city
                        "ON"                                 // province
                        )
                .sendAsync()
                .get();

        // get tx receipt
        TransactionReceipt txReceipt = contract
                .getTransactionReceipt()
                .get();

        // get tx hash and tx fees
        String deployHash = txReceipt.getTransactionHash();
        BigInteger deployFees = txReceipt
                .getCumulativeGasUsed()
                .multiply(Web3jConstants.GAS_PRICE);

        System.out.println("Deploy hash: " + deployHash);
        System.out.println("Deploy fees: " + Web3jUtils.weiToEther(deployFees));

        String contractAddress = contract.getContractAddress();
        System.out.println("Contract address: " + contractAddress);
        System.out.println("Contract address balance (initial): " + Web3jUtils.getBalanceWei(web3j, contractAddress));
        printBalance("after deploy", Account.GOVERNMENT );
        System.out.println();

        return contract;
    }

    private Subscription subscribeOracle(final PropertyRegistry registry) {
        Observable<PropertyRegistry.CalculateTaxesEventResponse> events = registry.calculateTaxesEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST);
        Subscription subscription = web3j.ethLogObservable(new EthFilter()).subscribe(l -> {
                    try {
                        System.out.println("LOG: " + l);
                        EventValues eventValues = parseLogEvent(l);
                        PropertyRegistry.CalculateTaxesEventResponse typedResponse = parseResponse(eventValues);
                        System.out.println(typedResponse);
                        DMNResponse dmnResponse = invokeDMNService(typedResponse);
                        setTaxes( registry, dmnResponse );
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                },
                Throwable::printStackTrace);
        return subscription;
    }

    private EventValues parseLogEvent(Log l) {
        final Event event = new Event("CalculateTaxes",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {
                }, new TypeReference<Address>() {
                }),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {
                }, new TypeReference<Utf8String>() {
                }, new TypeReference<Utf8String>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Bool>() {
                }, new TypeReference<Uint256>() {
                }, new TypeReference<Bool>() {
                }, new TypeReference<Bool>() {
                }));
        return Contract.staticExtractEventParameters(event, l);
    }

    private PropertyRegistry.CalculateTaxesEventResponse parseResponse(EventValues eventValues) {
        PropertyRegistry.CalculateTaxesEventResponse typedResponse = new PropertyRegistry.CalculateTaxesEventResponse();
        typedResponse._seller = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse._buyer = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse._registrationNumber = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse._province = (String) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse._city = (String) eventValues.getNonIndexedValues().get(2).getValue();
        typedResponse._salePrice = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
        typedResponse._downpayment = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
        typedResponse._optForInsurance = (Boolean) eventValues.getNonIndexedValues().get(5).getValue();
        typedResponse._buyerAge = (BigInteger) eventValues.getNonIndexedValues().get(6).getValue();
        typedResponse._buyerCanadianCitizen = (Boolean) eventValues.getNonIndexedValues().get(7).getValue();
        typedResponse._buyerFirstTime = (Boolean) eventValues.getNonIndexedValues().get(8).getValue();
        return typedResponse;
    }

    private DMNResponse invokeDMNService(PropertyRegistry.CalculateTaxesEventResponse event) {
        KieServicesConfiguration conf = KieServicesFactory.newRestConfiguration(KIE_SERVER_URL, KIE_SERVER_USER, KIE_SERVER_PASSWORD);
        conf.setMarshallingFormat(FORMAT);
        KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
        DMNServicesClient dmnClient = kieServicesClient.getServicesClient(DMNServicesClient.class);

        DMNContext dmnContext = dmnClient.newContext();
        Map<String,Object> property = new HashMap<>();
        property.put("Province", event._province );
        property.put("City", event._city );

        Map<String,Object> buyer = new HashMap<>();
        buyer.put("Canadian Citizen", event._buyerCanadianCitizen );
        buyer.put("First Time Buyer", event._buyerFirstTime );
        buyer.put("Age", event._buyerAge );

        Map<String,Object> mortgage = new HashMap<>();
        mortgage.put("Purchase Price", event._salePrice.doubleValue() / 100 );
        mortgage.put("Down Payment", event._downpayment.doubleValue() / 100 );
        mortgage.put("Insurance", event._optForInsurance );

        dmnContext.set("Property", property);
        dmnContext.set("Buyer", buyer);
        dmnContext.set("Mortgage", mortgage);

        String containerId = "land-registry_1.0.0";
        ServiceResponse<DMNResult> serverResp = dmnClient.evaluateAll(containerId, dmnContext);

        DMNResult dmnResult = serverResp.getResult();
        DMNContext context = dmnResult.getContext();

        Map<String,Number> ret = (Map<String, Number>) context.get("Real Estate Taxes");

        DMNResponse dmnResponse = new DMNResponse(
                (long) (ret.get("Toronto Tax").doubleValue() * 100),
                (long) (ret.get("Ontario Tax").doubleValue() * 100),
                (long) (ret.get("Insurance Premium").doubleValue() * 100)
        );

        System.out.println("DMN results = " + dmnResponse);
        return dmnResponse;
    }

    private static class DMNResponse {
        public final long cityTaxes;
        public final long provinceTaxes;
        public final long insurancePremium;

        public DMNResponse( long city, long province, long insurance ) {
            this.cityTaxes = city;
            this.provinceTaxes = province;
            this.insurancePremium = insurance;
        }

        @Override
        public String toString() {
            return "DMNResponse{" +
                    "cityTaxes=" + cityTaxes +
                    ", provinceTaxes=" + provinceTaxes +
                    ", insurancePremium=" + insurancePremium +
                    '}';
        }
    }

    private void unsubscribeOracle(PropertyRegistry registry, Subscription subscription) {
        subscription.unsubscribe();
    }

    private void initiateSale(PropertyRegistry contract) throws Exception {
        System.out.println("-- Call initiateSale()");
        System.out.println("Contract state before: "+contract.state().send());

        Function initiate = new Function( "initiateSale",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(Account.BUYER.ADDRESS),
                new org.web3j.abi.datatypes.generated.Uint256( 500_000 * 100 ),
                new org.web3j.abi.datatypes.generated.Uint256( 1_000 * 100 ) ),
                Arrays.asList( TypeReference.create( org.web3j.abi.datatypes.Bool.class ) ) );

        String encodedFunction = FunctionEncoder.encode(initiate);
        BigInteger nonce = Web3jUtils.getNonce( web3j, Account.SELLER.ADDRESS );

        RawTransaction rawTransaction  = RawTransaction.createTransaction(
                nonce, Web3jConstants.GAS_PRICE, Web3jConstants.GAS_LIMIT_CONTRACT_TX,
                contract.getContractAddress(), encodedFunction );

        // sign & send our transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Account.SELLER.CREDENTIALS );
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction txResponse = web3j.ethSendRawTransaction(hexValue).send();

        if( txResponse.hasError() ) {
            System.out.println( "ERROR: " + txResponse.getError().getMessage() );
        } else {
            String transactionHash = txResponse.getTransactionHash();
            TransactionReceipt transactionReceipt = Web3jUtils.waitForReceipt(web3j, transactionHash);
            System.out.println("initiateSale() hash: " + transactionReceipt.getTransactionHash());
        }
        printBalance("after initiateSale", Account.SELLER);
        System.out.println("Contract state after: "+contract.state().send());
        System.out.println();
    }

    private void submitBuyerInformation(PropertyRegistry contract) throws Exception {
        System.out.println("-- Call submitBuyerInformation()");
        System.out.println("Contract state before: "+contract.state().send());

        Function initiate = new Function( "submitBuyerInformation",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String( "John Smith" ),
                        new org.web3j.abi.datatypes.generated.Uint256( 35 ),
                        new org.web3j.abi.datatypes.Bool( true ),
                        new org.web3j.abi.datatypes.Bool( true ),
                        new org.web3j.abi.datatypes.generated.Uint256( 200_000 * 100 ),
                        new org.web3j.abi.datatypes.Bool( false ) ),
                Arrays.asList( TypeReference.create( org.web3j.abi.datatypes.Bool.class ) ) );

        String encodedFunction = FunctionEncoder.encode(initiate);
        BigInteger nonce = Web3jUtils.getNonce( web3j, Account.BUYER.ADDRESS );

        RawTransaction rawTransaction  = RawTransaction.createTransaction(
                nonce, Web3jConstants.GAS_PRICE, Web3jConstants.GAS_LIMIT_CONTRACT_TX,
                contract.getContractAddress(), encodedFunction );

        // sign & send our transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Account.BUYER.CREDENTIALS );
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction txResponse = web3j.ethSendRawTransaction(hexValue).send();

        if( txResponse.hasError() ) {
            System.out.println( "ERROR: " + txResponse.getError().getMessage() );
        } else {
            String transactionHash = txResponse.getTransactionHash();
            TransactionReceipt transactionReceipt = Web3jUtils.waitForReceipt(web3j, transactionHash);
            System.out.println("submitBuyerInformation() hash: " + transactionReceipt.getTransactionHash());
        }
        printBalance("after submitBuyerInformation", Account.BUYER);
        System.out.println("Contract state after: "+contract.state().send());

        System.out.println("Waiting for event...");
        latch.await();
        System.out.println();

    }

    private void setTaxes(PropertyRegistry contract, DMNResponse response) throws Exception {
        System.out.println("-- Call setTaxAmount()");
        System.out.println("Contract state before: "+contract.state().send());

        Function initiate = new Function( "setTaxAmount",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(response.provinceTaxes),
                        new org.web3j.abi.datatypes.generated.Uint256(response.cityTaxes),
                        new org.web3j.abi.datatypes.generated.Uint256(response.insurancePremium)),
                Arrays.asList( TypeReference.create( org.web3j.abi.datatypes.Bool.class ) ) );

        String encodedFunction = FunctionEncoder.encode(initiate);
        BigInteger nonce = Web3jUtils.getNonce( web3j, Account.BUYER.ADDRESS );

        RawTransaction rawTransaction  = RawTransaction.createTransaction(
                nonce, Web3jConstants.GAS_PRICE, Web3jConstants.GAS_LIMIT_CONTRACT_TX,
                contract.getContractAddress(), encodedFunction );

        // sign & send our transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Account.BUYER.CREDENTIALS );
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction txResponse = web3j.ethSendRawTransaction(hexValue).send();

        if( txResponse.hasError() ) {
            System.out.println( "ERROR: " + txResponse.getError().getMessage() );
        } else {
            String transactionHash = txResponse.getTransactionHash();
            TransactionReceipt transactionReceipt = Web3jUtils.waitForReceipt(web3j, transactionHash);
            System.out.println("setTaxAmount() hash: " + transactionReceipt.getTransactionHash());
        }
        printBalance("after setTaxAmount", Account.BUYER);
        System.out.println("Contract state after: "+contract.state().send());
        System.out.println();
    }

    private void finalizeSale(PropertyRegistry contract) throws Exception {
        System.out.println("-- Call finalizeSale()");
        System.out.println("Contract state before: "+contract.state().send());

        printBalance("before finalizeSale", Account.SELLER);
        printBalance("before finalizeSale", Account.BUYER);
        printBalance("before finalizeSale", Account.CITY);
        printBalance("before finalizeSale", Account.PROVINCE);
        printBalance("before finalizeSale", Account.INSURER);

        BigInteger total = contract.totalAmountWei().send();

        Function initiate = new Function( "finalizeSale",
                Arrays.<Type>asList(),
                Arrays.asList( TypeReference.create( org.web3j.abi.datatypes.Bool.class ) ) );

        String encodedFunction = FunctionEncoder.encode(initiate);
        BigInteger nonce = Web3jUtils.getNonce( web3j, Account.BUYER.ADDRESS );

        RawTransaction rawTransaction  = RawTransaction.createTransaction(
                nonce, Web3jConstants.GAS_PRICE, Web3jConstants.GAS_LIMIT_CONTRACT_TX,
                contract.getContractAddress(), total, encodedFunction );

        // sign & send our transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Account.BUYER.CREDENTIALS );
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction txResponse = web3j.ethSendRawTransaction(hexValue).send();

        if( txResponse.hasError() ) {
            System.out.println( "ERROR: " + txResponse.getError().getMessage() );
        } else {
            String transactionHash = txResponse.getTransactionHash();
            TransactionReceipt transactionReceipt = Web3jUtils.waitForReceipt(web3j, transactionHash);
            System.out.println("finalizeSale() hash: " + transactionReceipt.getTransactionHash());
        }
        printBalance("after finalizeSale", Account.SELLER);
        printBalance("after finalizeSale", Account.BUYER);
        printBalance("after finalizeSale", Account.CITY);
        printBalance("after finalizeSale", Account.PROVINCE);
        printBalance("after finalizeSale", Account.INSURER);
        System.out.println("Contract state after: "+contract.state().send());

        System.out.println("Waiting for event...");
        latch.await();
        System.out.println();
    }

    private void killContract(PropertyRegistry contract) throws Exception {
        System.out.println("-- Kill contract");

        TransactionReceipt txReceipt = contract
                .kill()
                .sendAsync()
                .get();

        BigInteger killFees = txReceipt
                .getCumulativeGasUsed()
                .multiply(Web3jConstants.GAS_PRICE);

        System.out.println("Contract.kill() fee: " + Web3jUtils.weiToEther(killFees));
        printBalance("after kill", Account.GOVERNMENT );
    }



    public static void main(String[] args) throws Exception {
        new PropertyRegistryMain().run();
    }
}
