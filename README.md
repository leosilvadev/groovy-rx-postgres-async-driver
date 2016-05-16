# groovy-rx-postgres-async-driver [![Build Status](https://travis-ci.org/leosilvadev/groovy-rx-postgres-async-driver.svg?branch=master)](https://travis-ci.org/leosilvadev/groovy-rx-postgres-async-driver) [![Coverage Status](https://coveralls.io/repos/github/leosilvadev/groovy-rx-postgres-async-driver/badge.svg?branch=master)](https://coveralls.io/github/leosilvadev/groovy-rx-postgres-async-driver?branch=master)

Groovy wrapper for <a href="https://github.com/alaisi/postgres-async-driver">postgres-async-driver<a>, adding utils methods and clue, based only on the driver's rx methods

###### But why?
- Execute queries with named parameters
- Use Date types as you want: java.util.Date, Calendar, LocalDate, LocalDateTime

## TODO
- Transactions
- Tests
- Any Clue for paging
- Anything you think is useful :)
- Jsonb support

### Connecting
```groovy
def db = new PgDb([
	hostname: "localhost",
	port: 5432,
	database: "glogger-test",
	username: "dev",
	password: "dev",
	poolSize: 20
])
```

#### All the following methods return an rxjava Observable

### Insert
```groovy
def sql = 'INSERT INTO Users (login, password, dateField, timestampField) VALUES (:login, :password, :date, :timestamp)'
def params = [login:'any', password:'any', date:LocalDate.now(), timestamp:LocalDateTime.now()]
db.insert(sql, params).subscribe({ id -> println id })
```

### Update
```groovy
def sql = 'UPDATE Users SET password = :password WHERE login = :login'
def params = [login:'mylogin', password:'newpass']
db.update(sql, params).subscribe({ numOfUpdated -> println numOfUpdated })
```

### Delete
```groovy
def sql = 'DELETE FROM Users WHERE login = :login'
def params = [login:'mylogin']
db.delete(sql, params).subscribe({ numOfDeleted -> println numOfDeleted })
```

### Find
```groovy
def sql = 'SELECT * FROM Users'
def template = [id:Long, login:String]
db.find(sql, template).subscribe({ users -> println users })
```

### Find One
```groovy
def sql = 'SELECT * FROM Users WHERE id = :id'
def template = [id:Long, login:String]
def params = [id:1]
db.findOne(sql, template, params).subscribe({ user -> println user })
```

### Execute
```groovy
def sql = 'DROP TABLE Users'
db.execute(sql).subscribe({ result -> println result })
```
