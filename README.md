# groovy-rx-postgres-async-driver
[![Released Version](https://img.shields.io/badge/Version-Released-blue.svg)](https://oss.sonatype.org/content/repositories/snapshots/com/github/leosilvadev/groovy-postgres-async-driver/) [![Build Status](https://travis-ci.org/leosilvadev/groovy-rx-postgres-async-driver.svg?branch=master)](https://travis-ci.org/leosilvadev/groovy-rx-postgres-async-driver) [![Coverage Status](https://coveralls.io/repos/github/leosilvadev/groovy-rx-postgres-async-driver/badge.svg?branch=master)](https://coveralls.io/github/leosilvadev/groovy-rx-postgres-async-driver?branch=master)


Groovy wrapper for <a href="https://github.com/alaisi/postgres-async-driver">postgres-async-driver<a>

###### But why?
- Result mapping based on Templates (Map or Class)
- Execute queries with Named Parameters
- Use Date types as you want: java.util.Date, Calendar, LocalDate, LocalDateTime
- Basic Jsonb native support
- Pagination support

## TODO
- Tests
- Anything you think is useful :)

## Usage

#### Gradle
```groovy
repositories {
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots"
    }
}

dependencies {
	compile 'com.github.leosilvadev:groovy-rx-postgres-async-driver:1.0.0-SNAPSHOT'
}
```

#### Maven
```xml
<repository>
   <id>oss.sonatype.org.snapshot</id>
   <name>Oss Sonatype Snapshot</name>
   <url>https://oss.sonatype.org/content/repositories/snapshots</url>
</repository>

<dependency>
  <groupId>com.github.leosilvadev</groupId>
  <artifactId>groovy-rx-postgres-async-driver</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

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

## Transactional Methods
**All the non-transactional methods are available on PgTransaction object**

The rollback happens if there is any error inside the Observables, but you can trigger it anytime you want if needed
```groovy
def log = new MyObj(name:'whatever', age:30)
db.transaction().flatMap { final PgTransaction tx ->
	tx.insert(sqlInsert, log).flatMap({
		tx.update(sqlUpdate, paramsTwo)
	}).flatMap({
		tx.delete(sqlDelete, paramsThree)
	}).flatMap({
		tx.commit()
	})
}.onErrorReturn({
	println 'Transaction was rolled back'
}).subscribe({
	println 'Transaction OK'
})
```

## Non-Transactional Methods

### Insert
```groovy
class User {
	Long id
	String login
	String password
	LocalDate date
	LocalDateTime timestamp
	Map jsonbField
}

def sql = 'INSERT INTO Users (login, password, dateField, timestampField, jsonbField) VALUES (:login, :password, :date, :timestamp, :jsonbField)'
def jsonbObject = [any:'Attrvalue', date:new Date(), sub:[name:'UHA']]
def user = new User(login:'any', password:'any', date:LocalDate.now(), timestamp:LocalDateTime.now(), jsonbField:jsonbObject)
Single<Long> id = db.insert(sql, user)
```

### Update
```groovy
def sql = 'UPDATE Users SET password = :password WHERE login = :login'
def user = new User(login:'mylogin', password:'newpass')
Single<Long> numberOfUpdated = db.update(sql, user)
```

### Delete
```groovy
def sql = 'DELETE FROM Users WHERE login = :login'
def params = [login:'mylogin']
Single<Long> numberOfDeleted = db.delete(sql, params)
```

### Find
```groovy
def sql = "SELECT * FROM Users WHERE jsonbField ->> 'any' = 'Attrvalue'"
Observable<User> users = db.find(sql, User)
```

### Find - Paging
```groovy
def sql = 'SELECT * FROM Users WHERE login = :login ORDER BY UserID'
def params = [login:'any']
def page = 1
def itemsPerPage = 10
def paging = new PageRequest(page, itemsPerPage)
Single<Page<User>> usersPage = db.find(sql, User, params, paging)
```

### Find One
```groovy
def sql = 'SELECT * FROM Users WHERE id = :id'
def params = [id:1]
Single<User> user = db.findOne(sql, User, params)
```

### Execute
```groovy
def sql = 'DROP TABLE Users'
Single<ResultSet> result = db.execute(sql)
```
