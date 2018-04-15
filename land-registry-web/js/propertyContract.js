var RegistryContract = web3.eth.contract([
       {
       "anonymous": false,
       "inputs": [
        {
          "indexed": true,
          "name": "_seller",
          "type": "address"
        },
        {
          "indexed": true,
          "name": "_buyer",
          "type": "address"
        },
        {
          "indexed": false,
          "name": "_registrationNumber",
          "type": "string"
        },
        {
          "indexed": false,
          "name": "_province",
          "type": "string"
        },
        {
          "indexed": false,
          "name": "_city",
          "type": "string"
        },
        {
          "indexed": false,
          "name": "_salePrice",
          "type": "uint256"
        },
        {
          "indexed": false,
          "name": "_downpayment",
          "type": "uint256"
        },
        {
          "indexed": false,
          "name": "_optForInsurance",
          "type": "bool"
        },
        {
          "indexed": false,
          "name": "_buyerAge",
          "type": "uint256"
        },
        {
          "indexed": false,
          "name": "_buyerCanadianCitizen",
          "type": "bool"
        },
        {
          "indexed": false,
          "name": "_buyerFirstTime",
          "type": "bool"
        }
       ],
       "name": "CalculateTaxes",
       "type": "event"
       },
       {
       "constant": false,
       "inputs": [],
       "name": "cancelSale",
       "outputs": [],
       "payable": false,
       "stateMutability": "nonpayable",
       "type": "function"
       },
       {
       "constant": false,
       "inputs": [],
       "name": "finalizeSale",
       "outputs": [
        {
          "name": "success",
          "type": "bool"
        }
       ],
       "payable": true,
       "stateMutability": "payable",
       "type": "function"
       },
       {
       "constant": false,
       "inputs": [
        {
          "name": "_buyer",
          "type": "address"
        },
        {
          "name": "_salePrice",
          "type": "uint256"
        },
        {
          "name": "_exchangeRate",
          "type": "uint256"
        }
       ],
       "name": "initiateSale",
       "outputs": [
        {
          "name": "success",
          "type": "bool"
        }
       ],
       "payable": false,
       "stateMutability": "nonpayable",
       "type": "function"
       },
       {
       "constant": false,
       "inputs": [],
       "name": "kill",
       "outputs": [],
       "payable": false,
       "stateMutability": "nonpayable",
       "type": "function"
       },
       {
       "constant": false,
       "inputs": [
        {
          "name": "_provinceTaxes",
          "type": "uint256"
        },
        {
          "name": "_cityTaxes",
          "type": "uint256"
        },
        {
          "name": "_mortgageInsurance",
          "type": "uint256"
        }
       ],
       "name": "setTaxAmount",
       "outputs": [
        {
          "name": "success",
          "type": "bool"
        }
       ],
       "payable": false,
       "stateMutability": "nonpayable",
       "type": "function"
       },
       {
       "constant": false,
       "inputs": [
        {
          "name": "_name",
          "type": "string"
        },
        {
          "name": "_age",
          "type": "uint256"
        },
        {
          "name": "_canadianCitizen",
          "type": "bool"
        },
        {
          "name": "_firstTimeBuyer",
          "type": "bool"
        },
        {
          "name": "_downpayment",
          "type": "uint256"
        },
        {
          "name": "_optForInsurance",
          "type": "bool"
        }
       ],
       "name": "submitBuyerInformation",
       "outputs": [
        {
          "name": "success",
          "type": "bool"
        }
       ],
       "payable": false,
       "stateMutability": "nonpayable",
       "type": "function"
       },
       {
       "inputs": [
        {
          "name": "_propertyOwner",
          "type": "address"
        },
        {
          "name": "_cityWallet",
          "type": "address"
        },
        {
          "name": "_provinceWallet",
          "type": "address"
        },
        {
          "name": "_insurerWallet",
          "type": "address"
        },
        {
          "name": "_registrationNumber",
          "type": "string"
        },
        {
          "name": "_streetAddress",
          "type": "string"
        },
        {
          "name": "_city",
          "type": "string"
        },
        {
          "name": "_province",
          "type": "string"
        }
       ],
       "payable": false,
       "stateMutability": "nonpayable",
       "type": "constructor"
       },
       {
       "constant": true,
       "inputs": [],
       "name": "cityWallet",
       "outputs": [
        {
          "name": "",
          "type": "address"
        }
       ],
       "payable": false,
       "stateMutability": "view",
       "type": "function"
       },
       {
       "constant": true,
       "inputs": [
        {
          "name": "dollarAmount",
          "type": "uint256"
        },
        {
          "name": "conversionRate",
          "type": "uint256"
        }
       ],
       "name": "convertToWei",
       "outputs": [
        {
          "name": "convertedAmount",
          "type": "uint256"
        }
       ],
       "payable": false,
       "stateMutability": "pure",
       "type": "function"
       },
       {
       "constant": true,
       "inputs": [],
       "name": "insurerWallet",
       "outputs": [
        {
          "name": "",
          "type": "address"
        }
       ],
       "payable": false,
       "stateMutability": "view",
       "type": "function"
       },
       {
       "constant": true,
       "inputs": [],
       "name": "propertyAddress",
       "outputs": [
        {
          "name": "registrationNumber",
          "type": "string"
        },
        {
          "name": "streetAddress",
          "type": "string"
        },
        {
          "name": "city",
          "type": "string"
        },
        {
          "name": "province",
          "type": "string"
        }
       ],
       "payable": false,
       "stateMutability": "view",
       "type": "function"
       },
       {
       "constant": true,
       "inputs": [],
       "name": "propertyOwner",
       "outputs": [
        {
          "name": "",
          "type": "address"
        }
       ],
       "payable": false,
       "stateMutability": "view",
       "type": "function"
       },
       {
       "constant": true,
       "inputs": [],
       "name": "prospectiveBuyer",
       "outputs": [
        {
          "name": "name",
          "type": "string"
        },
        {
          "name": "age",
          "type": "uint256"
        },
        {
          "name": "canadianCitizen",
          "type": "bool"
        },
        {
          "name": "firstTimeBuyer",
          "type": "bool"
        }
       ],
       "payable": false,
       "stateMutability": "view",
       "type": "function"
       },
       {
       "constant": true,
       "inputs": [],
       "name": "prospectiveSale",
       "outputs": [
        {
          "name": "buyer",
          "type": "address"
        },
        {
          "name": "salePrice",
          "type": "uint256"
        },
        {
          "name": "exchangeRate",
          "type": "uint256"
        }
       ],
       "payable": false,
       "stateMutability": "view",
       "type": "function"
       },
       {
       "constant": true,
       "inputs": [],
       "name": "provinceWallet",
       "outputs": [
        {
          "name": "",
          "type": "address"
        }
       ],
       "payable": false,
       "stateMutability": "view",
       "type": "function"
       },
       {
       "constant": true,
       "inputs": [],
       "name": "state",
       "outputs": [
        {
          "name": "",
          "type": "uint8"
        }
       ],
       "payable": false,
       "stateMutability": "view",
       "type": "function"
       },
       {
       "constant": true,
       "inputs": [],
       "name": "taxes",
       "outputs": [
        {
          "name": "provinceTaxes",
          "type": "uint256"
        },
        {
          "name": "cityTaxes",
          "type": "uint256"
        },
        {
          "name": "mortgageInsurance",
          "type": "uint256"
        }
       ],
       "payable": false,
       "stateMutability": "view",
       "type": "function"
       },
       {
       "constant": true,
       "inputs": [],
       "name": "totalAmountWei",
       "outputs": [
        {
          "name": "totalWei",
          "type": "uint256"
        }
       ],
       "payable": false,
       "stateMutability": "view",
       "type": "function"
       }
   ]);
