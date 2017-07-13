Create table Country(Id INT NOT NULL AUTO_INCREMENT,CountryName varchar(20),CountryCode varchar(20),CurrencyName varchar(20),CurrencyCode varchar(20),Latitude varchar(20),Longitude varchar(20),PRIMARY KEY (Id));

Create table State(Id INT NOT NULL AUTO_INCREMENT,CountryId INT,StateName varchar(20),Latitude varchar(20),Longitude varchar(20),PRIMARY KEY (Id),FOREIGN KEY (CountryID) REFERENCES Country(Id));

Create table City(Id INT NOT NULL AUTO_INCREMENT,StateId INT,CityName varchar(20),Latitude varchar(20),Longitude varchar(20),STDCode varchar(20),PRIMARY KEY (Id),FOREIGN KEY (StateID) REFERENCES State(Id));

Create table Area(Id INT NOT NULL AUTO_INCREMENT,CityId INT,AreaName varchar(20),Latitude varchar(20),Longitude varchar(20),STDCode varchar(20),ZIP varchar(20),PRIMARY KEY (Id),FOREIGN KEY (CityID) REFERENCES City(Id));


Insert into country(CountryName,CountryCode,CurrencyName,CurrencyCode,Latitude,Longitude) values('INDIA','91','RUPEE','INR','20.26.2','552.55');
Insert into country(CountryName,CountryCode,CurrencyName,CurrencyCode,Latitude,Longitude) values('USA','1','DOLLER','USD','0.26.2','52.55');
Insert into country(CountryName,CountryCode,CurrencyName,CurrencyCode,Latitude,Longitude) values('RUSSIA','851','DOLLER','RDS','5.26.2','52.55');

insert into state(countryid,statename,latitude,longitude) values(1,'KARNATAKA','155.25.362','45.2563.55');
insert into state(countryid,statename,latitude,longitude) values(1,'ANDHRA PRADESH','1558.5.25.362','45.2563.55');
insert into state(countryid,statename,latitude,longitude) values(1,'TAMILNADU','1558.5.25.362','45.2563.55');

insert into city(stateid,cityname,latitude,longitude,stdcode) values(1,'BENGALURU','856.45.5686','15.15.586.45','080');
insert into city(stateid,cityname,latitude,longitude,stdcode) values(1,'MYSURU','856.45.5686','15.15.586.45','081');
insert into city(stateid,cityname,latitude,longitude,stdcode) values(1,'BALLARI','856.45.5686','15.15.586.45','08512');

insert into AREA(cityid,areaname,latitude,longitude,stdcode,zip) values(1,'JAYANAGARA','456.458.55','56.656.511','080','569985');
insert into AREA(cityid,areaname,latitude,longitude,stdcode,zip) values(1,'VIJAYANAGARA','456.458.55','56.656.511','080','569985');
insert into AREA(cityid,areaname,latitude,longitude,stdcode,zip) values(1,'MALLESHWARAM','456.458.55','56.656.511','080','569985');