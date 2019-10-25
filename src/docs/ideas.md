
## Future features

- Support more data types;
  tinyint, tinyblob, blob, mediumblob, smallint, boolean, mediumint, integer, decimal,
  dec, numeric, fixed, float, double, double precision, char, binary, char byte, 
  varbinary, tinytext, mediumtext, longtext, json, enum, set, date, time, datetime,
  year
- Support other databases;
  PostgreSQL, H2, HSQL, MS SQL, Oracle, etc.
- Support in memory databases and migration scripts
- Other names
  - Business, Government, Movie, TV, Plant, Mineral
  - any other names
- Fix the primary key bug
- Add switch for schema configuration
- Other data types:
  - BIC, CountryCode, TLD, radio alphabet, IBAN, 
    Creditcard, postal code, VIN, BIN (Bank Identification Number)
    IMEI, ISBN, latitude, longitude
- Use more common domains than @example.com
- Combine multiple columns
  - person name with email address
  - credit card with expire and ccv
  - address, street, city, county, state, country
- Choose a language
- Avatar generator
  https://www.fakepersongenerator.com/user-face-generator
- Username generator
- Use transcripts for movies as lorum ipsum
- Use movie songs as lorum ipsum
- Use poems as lorum ipsum
- Be tolerant of already used comments for columns
  Only use the comment if is says one of these:
  - 'filldb pattern \[regular expression]'
  - 'filldb enum one,two,three,four'
  - 'filldb ipsum name-of-ipsum'
- Custom generators
