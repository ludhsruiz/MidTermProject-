# Midterm project: ADA BYRON BANK
This is a banking system project which allows you to use the general basic functionalities of an online banking system.

## Getting started
1. Clone 
2. Run the following code in your MySQL Workbench:
   ```sh
   CREATE DATABASE IF NOT EXISTS midterm;
   USE midtermProject;

   CREATE USER IF NOT EXISTS 'ironhacker'@'localhost' IDENTIFIED BY '1r0nh4ck3r';
   GRANT ALL PRIVILEGES ON *.* TO 'ironhacker'@'localhost';
   FLUSH PRIVILEGES;
   ```
3. Run the MidTermApplication.
4. Test the routes with Postman. 
5. Enjoy.


## Functionality

### Account 
Some functionalities only work for users having the role "ADMIN" please add this details to the workbench sql.
to create roles for certain users

```sh
INSERT INTO role (name) VALUES
("ADMIN"),
("ACCOUNT_HOLDER");

INSERT INTO user(username, password, role_id) VALUES
(" YourNameGoesHere","YourEncodedPasswordGoesHere", 1);

INSERT INTO admin(id) VALUES
(1);

 ```

I added a password util so you can create your own passwords and get your coded data to add to the database. 


### Create an account
There are three types of accounts that can be created: Savings, credit card and checking account.
Moreover, admins can also integrate third-party accounts.

#### Create an account holder 
Account holders can be created by admins through the POST-route *"/account-holders"*.<br>

```sh
{
"username": "<your input>",
"password": "<your input>",
"role": "ACCOUNT_HOLDER",
"dateOfBirth": "YYYY-MM-DD",
"primaryAddress": {
    "streetAddress": "<your input>",
    "city": "<your input>",
    "postalCode": "<your input>"
  },
"mailingAddress": {
    "streetAddress": "<your input>", (optional)
    "city": "<your input>", (optional)
    "postalCode": "<your input>" (optional)
  }
}
```

### Template to create a savings account
Saving accounts can be created by admins through the POST-route *"/savings"*.<br>


```sh
{
"balance": <your input>, (optional)
"penaltyFee":  <your input>, (optional)
"primaryOwner": { 
    "name": "<your input>" 
    }, 
"secondaryOwner": { (optional)
    "name": "<your input>" 
    }, 
"accountHolder": { 
    "id": <the given id number>
    },
"secretKey": "<your input>",
"minimumBalance": <your input>, (optional)
"interestRate": <your input> (optional)
}
```

#### Template to create a credit card account 
CreditCard accounts can be created by admins through the POST-route *"/creditcards"*.<br>


```sh
{
"balance": <your input>, (optional)
"penaltyFee":  <your input>, (optional)
"primaryOwner": { 
    "name": "<your input>" 
    }, 
"secondaryOwner": { (optional)
    "name": "<your input>" 
    }, 
"accountHolder": { 
    "id": <the given id number>
    },
"creditLimit": <your input>, (optional)
"interestRate": <your input> (optional)
}
```

#### Template to create a checking account
Checking accounts can be created by admins through the POST-route *"/checkings"*.<br>

```sh
{
"balance": <your input>, (optional)
"penaltyFee":  <your input>, (optional)
"primaryOwner": { 
    "name": "<your input>" 
    }, 
"secondaryOwner": { (optional)
    "name": "<your input>" 
    }, 
"accountHolder": { 
    "id": <the given id number>
    },
"minimumBalance": <your input>, (optional)
"monthlyMaintenanceFee": <your input> (optional)
}
```
#### Third-party accounts
Third-party accounts can be created by admins through the POST-route *"/third_party"*.<br>

```sh
{ 
"name": "<your input>",
"username": "<your input>",
"password": "<your input>",
"hashedKey": "<your input>",
"role": {
    "name": "THIRD_PARTY"
    }
}
```

### Get a list of accounts and transactions
In order to get a list of all the accounts and transactions (only available for admins),
use the GET-route:<br>
- /savings
- /creditcards
- /checkings
- /third-party
- /transactions
- /account-holders


### Get information on specific accounts and transactions
In order to get information on specific accounts and transactions,
use the GET-route:<br>
- /savings/{id}
- /creditcard/{id}
- /checking/{id}
- /third-party/{id}
- /transactions/{id}
- /account-holder/{id}

### Modify the balance of accounts
In order to modify the balance of specific accounts (only available for admins),
use the PATCH-route:<br>
- modify/savings/{id}
- modify/creditcard/{id}
- modify/checking/{id} <br>

The request body only needs to include the following information:
```sh
{
"balance": <your input>
}
```
### Transfer money for account holders
If an account holder wants to transfer money, (s)he must fill in the login data in the authorization fields
and use the PATCH-route:<br>
- /transfer/{accountType}/{value}/{owner}/{id}

*Note: "accountType" means from which account you want to send money (savings, checking or creditcard).*

### Transfer money for third parties
If a third party account holder wants to transfer money, (s)he must fill in the login data in the authorization fields,
add the hashed key in the header (with "hashedKey" as keyword) and use the PATCH-route:<br>
- /transfer/third_party <br>

with the parameters:<br>
- value
- id *(of the account recipient)*
- secretKey *(of the account recipient, which is null when sending to another third party account. In this case, you can type in "null".)*

### Interest rate
Interest rate will be added - if applicable - through the PATCH-route:<br>
- /savings/interest/{id}
- /creditcard/interest/{id}<br>

#### Other information
- If an account drops below the minimumBalance, the penaltyFee will be deducted from the balance automatically.
- The application recognizes patterns that indicate fraud and freezes the account status when potential fraud is detected.

### Database diagram
![Database diagram](../../MIDTERM/src/main/resources/Midtermproject_database_diagram.png)






GET, "/transactions/{id}               ACCOUNT_HOLDER  THIRD_PARTY ADMIN
GET, "/transactions/savings/{id}       ACCOUNT_HOLDER  ADMIN

