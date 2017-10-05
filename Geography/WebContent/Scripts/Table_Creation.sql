Create table Country(Id INT NOT NULL AUTO_INCREMENT,CountryName varchar(200),CountryCode varchar(20),CurrencyName varchar(20),CurrencyCode varchar(20),Latitude varchar(20),Longitude varchar(20),PRIMARY KEY (Id));

Create table State(Id INT NOT NULL AUTO_INCREMENT,CountryId INT,StateName varchar(200),Latitude varchar(20),Longitude varchar(20),PRIMARY KEY (Id),FOREIGN KEY (CountryID) REFERENCES Country(Id));

Create table City(Id INT NOT NULL AUTO_INCREMENT,StateId INT,CityName varchar(200),Latitude varchar(20),Longitude varchar(20),STDCode varchar(20),PRIMARY KEY (Id),FOREIGN KEY (StateID) REFERENCES State(Id));

Create table Area(Id INT NOT NULL AUTO_INCREMENT,CityId INT,AreaName varchar(200),Latitude varchar(20),Longitude varchar(20),STDCode varchar(20),ZIP varchar(20),PRIMARY KEY (Id),FOREIGN KEY (CityID) REFERENCES City(Id));



