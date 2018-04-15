package com.methodandstyle.landregistry.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.2.0.
 */
public class PropertyRegistry extends Contract {
    private static final String BINARY = "606060405234156200001057600080fd5b604051620011193803806200111983398101604052808051919060200180519190602001805191906020018051919060200180518201919060200180518201919060200180518201919060200180516000805460a060020a60ff0219600160a060020a03338116600160a060020a031993841617919091169092556001805482168d84161790556002805482168c84161790556003805482168b8416179055600480549091169189169190911790559091019050608060405190810160405280858152602001848152602001838152602001828152506005600082015181908051620001019291602001906200016a565b506020820151816001019080516200011e9291602001906200016a565b506040820151816002019080516200013b9291602001906200016a565b50606082015181600301908051620001589291602001906200016a565b5090505050505050505050506200020f565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620001ad57805160ff1916838001178555620001dd565b82800160010185558215620001dd579182015b82811115620001dd578251825591602001919060010190620001c0565b50620001eb929150620001ef565b5090565b6200020c91905b80821115620001eb5760008155600101620001f6565b90565b610efa806200021f6000396000f3006060604052600436106100f05763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166311bc7bc781146100f557806341c0e1b5146101245780634fabd91a1461013957806358a687ec146101ee57806358ded57a1461020a57806366440d7114610235578063728f8eea14610441578063aa6d1dbc14610478578063b575b4f01461048b578063bb253e391461049e578063c19d93fb146104c3578063c6e6ab2a146104fa578063da4017c814610569578063e7e10490146105b2578063e8a2036f146105c5578063f3710e72146105e1578063f446c38f146105f4575b600080fd5b341561010057600080fd5b610108610607565b604051600160a060020a03909116815260200160405180910390f35b341561012f57600080fd5b610137610616565b005b341561014457600080fd5b61014c610658565b60405160208101849052821515604082015281151560608201526080808252855460026000196101006001841615020190911604908201819052819060a0820190879080156101dc5780601f106101b1576101008083540402835291602001916101dc565b820191906000526020600020905b8154815290600101906020018083116101bf57829003601f168201915b50509550505050505060405180910390f35b6101f6610671565b604051901515815260200160405180910390f35b341561021557600080fd5b61022360043560243561086f565b60405190815260200160405180910390f35b341561024057600080fd5b61024861088e565b6040516080808252855460026000196101006001841615020190911604908201819052819060208201906040830190606084019060a08501908a9080156102d05780601f106102a5576101008083540402835291602001916102d0565b820191906000526020600020905b8154815290600101906020018083116102b357829003601f168201915b50508581038452885460026000196101006001841615020190911604808252602090910190899080156103445780601f1061031957610100808354040283529160200191610344565b820191906000526020600020905b81548152906001019060200180831161032757829003601f168201915b50508581038352875460026000196101006001841615020190911604808252602090910190889080156103b85780601f1061038d576101008083540402835291602001916103b8565b820191906000526020600020905b81548152906001019060200180831161039b57829003601f168201915b505085810382528654600260001961010060018416150201909116048082526020909101908790801561042c5780601f106104015761010080835404028352916020019161042c565b820191906000526020600020905b81548152906001019060200180831161040f57829003601f168201915b50509850505050505050505060405180910390f35b341561044c57600080fd5b610454610899565b60405180848152602001838152602001828152602001935050505060405180910390f35b341561048357600080fd5b6102236108a5565b341561049657600080fd5b61010861092b565b34156104a957600080fd5b6101f6600160a060020a036004351660243560443561093a565b34156104ce57600080fd5b6104d6610a1d565b604051808260038111156104e657fe5b60ff16815260200191505060405180910390f35b341561050557600080fd5b6101f660046024813581810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284375094965050843594602081013515159450604081013515159350606081013592506080013515159050610a2d565b341561057457600080fd5b61057c610d36565b6040518084600160a060020a0316600160a060020a03168152602001838152602001828152602001935050505060405180910390f35b34156105bd57600080fd5b610137610d4e565b34156105d057600080fd5b6101f6600435602435604435610d89565b34156105ec57600080fd5b610108610e15565b34156105ff57600080fd5b610108610e24565b600254600160a060020a031681565b60005433600160a060020a0390811691161461063157600080fd5b60005433600160a060020a039081169116141561065657600054600160a060020a0316ff5b565b600d54600e54600c919060ff8082169161010090041684565b6009546000908190819081908190819033600160a060020a0390811691161461069957600080fd5b600360005460a060020a900460ff1660038111156106b357fe5b146106bd57600080fd5b600a54600b546106cd919061086f565b600f54600b549196506106df9161086f565b601054600b549195506106f19161086f565b601154600b549194506107039161086f565b91508183858701010190508034101561071b57600080fd5b6000841180156107505750600354600160a060020a031684156108fc0285604051600060405180830381858888f19350505050155b1561075a57600080fd5b60008311801561078f5750600254600160a060020a031683156108fc0284604051600060405180830381858888f19350505050155b1561079957600080fd5b6000821180156107ce5750600454600160a060020a031682156108fc0283604051600060405180830381858888f19350505050155b156107d857600080fd5b60008511801561080d5750600154600160a060020a031685156108fc0286604051600060405180830381858888f19350505050155b1561081757600080fd5b6009546001805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a039092169190911781556000805474ff00000000000000000000000000000000000000001916905595505b505050505090565b60008183670de0b6b3a76400000281151561088657fe5b049392505050565b600560066007600884565b600f5460105460115483565b60008080808080600360005460a060020a900460ff1660038111156108c657fe5b146108d45760009550610867565b600a54600b546108e4919061086f565b600f54600b549196506108f69161086f565b600f54600b549195506109089161086f565b600f54600b5491945061091a9161086f565b949093019091019092019392505050565b600354600160a060020a031681565b60015460009033600160a060020a0390811691161461095857600080fd5b6000805460a060020a900460ff16600381111561097157fe5b1461097e57506000610a16565b60606040519081016040908152600160a060020a038616825260208201859052810183905260098151815473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a039190911617815560208201518160010155604082015160029091015550600080546001919074ff0000000000000000000000000000000000000000191660a060020a835b0217905550600190505b9392505050565b60005460a060020a900460ff1681565b60095460009033600160a060020a03908116911614610a4b57600080fd5b600160005460a060020a900460ff166003811115610a6557fe5b14610a7257506000610d2c565b6080604051908101604090815288825260208201889052861515908201528415156060820152600c815181908051610aae929160200190610e33565b5060208201518160010155604082015160028201805460ff19169115159190911790556060820151600290910180549115156101000261ff001990921691909117905550600954600154600a54600160a060020a039283169291909116907f8f09e68952219a6f4b157fd6e090b095dbc5dab1dc076527a10ca75657ece9d69060059060089060079089898e8e8e604051606081018790526080810186905284151560a082015260c0810184905282151560e0820152811515610100808301919091526101208083528b54600260018216159093026000190116919091049082018190528190602082019060408301906101408401908e908015610bf35780601f10610bc857610100808354040283529160200191610bf3565b820191906000526020600020905b815481529060010190602001808311610bd657829003601f168201915b505084810383528c54600260001961010060018416150201909116048082526020909101908d908015610c675780601f10610c3c57610100808354040283529160200191610c67565b820191906000526020600020905b815481529060010190602001808311610c4a57829003601f168201915b505084810382528b54600260001961010060018416150201909116048082526020909101908c908015610cdb5780601f10610cb057610100808354040283529160200191610cdb565b820191906000526020600020905b815481529060010190602001808311610cbe57829003601f168201915b50509c5050505050505050505050505060405180910390a3506000805474ff000000000000000000000000000000000000000019167402000000000000000000000000000000000000000017905560015b9695505050505050565b600954600a54600b54600160a060020a039092169183565b60015433600160a060020a03908116911614610d6957600080fd5b6000805474ff000000000000000000000000000000000000000019169055565b6000600260005460a060020a900460ff166003811115610da557fe5b14610db257506000610a16565b60606040519081016040908152858252602082018590528101839052600f8151815560208201518160010155604082015160029091015550600080546003919074ff0000000000000000000000000000000000000000191660a060020a83610a0c565b600454600160a060020a031681565b600154600160a060020a031681565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610e7457805160ff1916838001178555610ea1565b82800160010185558215610ea1579182015b82811115610ea1578251825591602001919060010190610e86565b50610ead929150610eb1565b5090565b610ecb91905b80821115610ead5760008155600101610eb7565b905600a165627a7a72305820d88b8e3068d684611215350f5d41e8cf42bab972ebb235ae80a2113ac6ad26430029";

    protected PropertyRegistry(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PropertyRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<CalculateTaxesEventResponse> getCalculateTaxesEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("CalculateTaxes", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<CalculateTaxesEventResponse> responses = new ArrayList<CalculateTaxesEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            CalculateTaxesEventResponse typedResponse = new CalculateTaxesEventResponse();
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
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<CalculateTaxesEventResponse> calculateTaxesEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("CalculateTaxes", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, CalculateTaxesEventResponse>() {
            @Override
            public CalculateTaxesEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                CalculateTaxesEventResponse typedResponse = new CalculateTaxesEventResponse();
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
        });
    }

    public RemoteCall<String> cityWallet() {
        Function function = new Function("cityWallet", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> kill() {
        Function function = new Function(
                "kill", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple4<String, BigInteger, Boolean, Boolean>> prospectiveBuyer() {
        final Function function = new Function("prospectiveBuyer", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}));
        return new RemoteCall<Tuple4<String, BigInteger, Boolean, Boolean>>(
                new Callable<Tuple4<String, BigInteger, Boolean, Boolean>>() {
                    @Override
                    public Tuple4<String, BigInteger, Boolean, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple4<String, BigInteger, Boolean, Boolean>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (Boolean) results.get(2).getValue(), 
                                (Boolean) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> finalizeSale(BigInteger weiValue) {
        Function function = new Function(
                "finalizeSale", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<BigInteger> convertToWei(BigInteger dollarAmount, BigInteger conversionRate) {
        Function function = new Function("convertToWei", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(dollarAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(conversionRate)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple4<String, String, String, String>> propertyAddress() {
        final Function function = new Function("propertyAddress", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteCall<Tuple4<String, String, String, String>>(
                new Callable<Tuple4<String, String, String, String>>() {
                    @Override
                    public Tuple4<String, String, String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple4<String, String, String, String>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (String) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>> taxes() {
        final Function function = new Function("taxes", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple3<BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple3<BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<BigInteger> totalAmountWei() {
        Function function = new Function("totalAmountWei", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> provinceWallet() {
        Function function = new Function("provinceWallet", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> initiateSale(String _buyer, BigInteger _salePrice, BigInteger _exchangeRate) {
        Function function = new Function(
                "initiateSale", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_buyer), 
                new org.web3j.abi.datatypes.generated.Uint256(_salePrice), 
                new org.web3j.abi.datatypes.generated.Uint256(_exchangeRate)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> state() {
        Function function = new Function("state", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> submitBuyerInformation(String _name, BigInteger _age, Boolean _canadianCitizen, Boolean _firstTimeBuyer, BigInteger _downpayment, Boolean _optForInsurance) {
        Function function = new Function(
                "submitBuyerInformation", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.generated.Uint256(_age), 
                new org.web3j.abi.datatypes.Bool(_canadianCitizen), 
                new org.web3j.abi.datatypes.Bool(_firstTimeBuyer), 
                new org.web3j.abi.datatypes.generated.Uint256(_downpayment), 
                new org.web3j.abi.datatypes.Bool(_optForInsurance)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple3<String, BigInteger, BigInteger>> prospectiveSale() {
        final Function function = new Function("prospectiveSale", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<String, BigInteger, BigInteger>>(
                new Callable<Tuple3<String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple3<String, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> cancelSale() {
        Function function = new Function(
                "cancelSale", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setTaxAmount(BigInteger _provinceTaxes, BigInteger _cityTaxes, BigInteger _mortgageInsurance) {
        Function function = new Function(
                "setTaxAmount", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_provinceTaxes), 
                new org.web3j.abi.datatypes.generated.Uint256(_cityTaxes), 
                new org.web3j.abi.datatypes.generated.Uint256(_mortgageInsurance)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> insurerWallet() {
        Function function = new Function("insurerWallet", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> propertyOwner() {
        Function function = new Function("propertyOwner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<PropertyRegistry> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _propertyOwner, String _cityWallet, String _provinceWallet, String _insurerWallet, String _registrationNumber, String _streetAddress, String _city, String _province) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_propertyOwner), 
                new org.web3j.abi.datatypes.Address(_cityWallet), 
                new org.web3j.abi.datatypes.Address(_provinceWallet), 
                new org.web3j.abi.datatypes.Address(_insurerWallet), 
                new org.web3j.abi.datatypes.Utf8String(_registrationNumber), 
                new org.web3j.abi.datatypes.Utf8String(_streetAddress), 
                new org.web3j.abi.datatypes.Utf8String(_city), 
                new org.web3j.abi.datatypes.Utf8String(_province)));
        return deployRemoteCall(PropertyRegistry.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<PropertyRegistry> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _propertyOwner, String _cityWallet, String _provinceWallet, String _insurerWallet, String _registrationNumber, String _streetAddress, String _city, String _province) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_propertyOwner), 
                new org.web3j.abi.datatypes.Address(_cityWallet), 
                new org.web3j.abi.datatypes.Address(_provinceWallet), 
                new org.web3j.abi.datatypes.Address(_insurerWallet), 
                new org.web3j.abi.datatypes.Utf8String(_registrationNumber), 
                new org.web3j.abi.datatypes.Utf8String(_streetAddress), 
                new org.web3j.abi.datatypes.Utf8String(_city), 
                new org.web3j.abi.datatypes.Utf8String(_province)));
        return deployRemoteCall(PropertyRegistry.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static PropertyRegistry load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PropertyRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static PropertyRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PropertyRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class CalculateTaxesEventResponse {
        public String _seller;

        public String _buyer;

        public String _registrationNumber;

        public String _province;

        public String _city;

        public BigInteger _salePrice;

        public BigInteger _downpayment;

        public Boolean _optForInsurance;

        public BigInteger _buyerAge;

        public Boolean _buyerCanadianCitizen;

        public Boolean _buyerFirstTime;
    }
}
