pragma solidity ^0.4.19;

contract mortal {
    /* Define variable owner of the type address. This is effectively the
    government agency responsible for registering the property. */
    address owner;

    modifier onlyOwner {
        require(msg.sender == owner);
        _;
    }

    /* this function is executed at initialization and sets the owner of the contract */
    function mortal() public { owner = msg.sender; }

    /* Function to recover the funds on the contract */
    function kill() public onlyOwner { if (msg.sender == owner) selfdestruct(owner); }
}

contract PropertyRegistry is mortal {

    /* Possible states for the contract */
    enum State { OWNED, SALE_INITIATED, CALCULATING_TAXES, WAITING_FINALIZATION }

    struct PropertyAddress {
      string registrationNumber;
      string streetAddress;
      string city;
      string province;
    }

    struct ProspectiveSale {
      address buyer;
      // Solidity still doesn't fully support floating point numbers,
      // so this is stored in CAD$ times 100
      uint256 salePrice;
      uint256 exchangeRate;
    }

    struct ProspectiveBuyer {
      string name;
      uint age;
      bool canadianCitizen;
      bool firstTimeBuyer;
    }

    struct Taxes {
      uint256 provinceTaxes;
      uint256 cityTaxes;
      uint256 mortgageInsurance;
    }

    modifier onlyPropertyOwner {
        require(msg.sender == propertyOwner);
        _;
    }

    modifier onlyPropertyBuyer {
        require(msg.sender == prospectiveSale.buyer);
        _;
    }

    event CalculateTaxes( address indexed _seller, address indexed _buyer,
                          string _registrationNumber, string _province, string _city,
                          uint256 _salePrice, uint256 _downpayment, bool _optForInsurance,
                          uint256 _buyerAge, bool _buyerCanadianCitizen, bool _buyerFirstTime );

    State public state;
    address public propertyOwner;
    address public cityWallet;
    address public provinceWallet;
    address public insurerWallet;

    PropertyAddress public propertyAddress;

    ProspectiveSale public prospectiveSale;
    ProspectiveBuyer public prospectiveBuyer;
    Taxes public taxes;

    function PropertyRegistry( address _propertyOwner, address _cityWallet, address _provinceWallet,
                               address _insurerWallet,
                               string _registrationNumber, string _streetAddress, string _city,
                               string _province ) public {
        state = State.OWNED;
        propertyOwner = _propertyOwner;
        cityWallet = _cityWallet;
        provinceWallet = _provinceWallet;
        insurerWallet = _insurerWallet;
        propertyAddress = PropertyAddress( _registrationNumber, _streetAddress, _city, _province );
    }

    function cancelSale() public onlyPropertyOwner {
        state = State.OWNED;
    }

    function initiateSale( address _buyer, uint256 _salePrice, uint256 _exchangeRate ) public onlyPropertyOwner returns (bool success) {
        // initiate the sale process
        if( state != State.OWNED ) return false;
        prospectiveSale = ProspectiveSale( _buyer, _salePrice, _exchangeRate );
        state = State.SALE_INITIATED;
        return true;
    }

    function submitBuyerInformation( string _name, uint _age, bool _canadianCitizen, bool _firstTimeBuyer,
                                     uint256 _downpayment, bool _optForInsurance ) public onlyPropertyBuyer returns (bool success) {
        // submits the buyer information for calculation of taxes
        if( state != State.SALE_INITIATED ) return false;
        prospectiveBuyer = ProspectiveBuyer( _name, _age, _canadianCitizen, _firstTimeBuyer );

        CalculateTaxes( propertyOwner, prospectiveSale.buyer,
                        propertyAddress.registrationNumber, propertyAddress.province, propertyAddress.city,
                        prospectiveSale.salePrice, _downpayment, _optForInsurance,
                        _age, _canadianCitizen, _firstTimeBuyer );
        state = State.CALCULATING_TAXES;
        return true;
    }

    function setTaxAmount( uint256 _provinceTaxes, uint256 _cityTaxes, uint256 _mortgageInsurance ) public returns (bool success) {
        // sets the values of taxes for the sale
        if( state != State.CALCULATING_TAXES ) return false;
        taxes = Taxes( _provinceTaxes, _cityTaxes, _mortgageInsurance );
        state = State.WAITING_FINALIZATION;
        return true;
    }

    function finalizeSale( ) public payable onlyPropertyBuyer returns (bool success) {
        // finalizes the payment and transfer of property
        if( state != State.WAITING_FINALIZATION ) revert();
        uint256 salePriceWei = convertToWei( prospectiveSale.salePrice, prospectiveSale.exchangeRate );
        uint256 provinceWei = convertToWei( taxes.provinceTaxes, prospectiveSale.exchangeRate );
        uint256 cityWei = convertToWei( taxes.cityTaxes, prospectiveSale.exchangeRate );
        uint256 insuranceWei = convertToWei( taxes.mortgageInsurance, prospectiveSale.exchangeRate );
        uint256 totalWei = salePriceWei + provinceWei + cityWei + insuranceWei;

        if( msg.value < totalWei ) revert();
        if( provinceWei > 0 && ! provinceWallet.send( provinceWei ) ) revert();
        if( cityWei > 0 && ! cityWallet.send( cityWei ) ) revert();
        if( insuranceWei > 0 && ! insurerWallet.send( insuranceWei ) ) revert();
        if( salePriceWei > 0 && ! propertyOwner.send( salePriceWei ) ) revert();

        propertyOwner = prospectiveSale.buyer;
        state = State.OWNED;

        return true;
    }

    function convertToWei(uint256 dollarAmount, uint256 conversionRate) public pure returns (uint convertedAmount) {
      return dollarAmount * 1e18 / conversionRate ;
    }

    function totalAmountWei() public view returns (uint totalWei) {
        if( state != State.WAITING_FINALIZATION ) return 0;
        uint256 salePriceWei = convertToWei( prospectiveSale.salePrice, prospectiveSale.exchangeRate );
        uint256 provinceWei = convertToWei( taxes.provinceTaxes, prospectiveSale.exchangeRate );
        uint256 cityWei = convertToWei( taxes.provinceTaxes, prospectiveSale.exchangeRate );
        uint256 insuranceWei = convertToWei( taxes.provinceTaxes, prospectiveSale.exchangeRate );
        uint256 total = salePriceWei + provinceWei + cityWei + insuranceWei;
        return total;
    }
}

