## Sposób uruchomienia części backendowej (Heroku)

### Instalacja narzędzia CLI
W celu skonfigurowania serwera Heroku należy użyć narzędzia linii komend Heroku CLI. Jeśli jeszcze tego   
nie zrobiliśmy, należy je zainstalować zgodnie z instrukcjami dostępnymi pod [tym adresem](https://devcenter.heroku.com/articles/heroku-command-line).

### Przygotowanie projektu
Zanim uruchomimy aplikację, musimy odpowiednio zmodyfikować plik ___application.properties__. Jeśli uruchomimy   
aplikację zdalnie, musimy podać nazwę domeny pod którą będzie ona dostępna. Robimy to ustawiając wartość pola  
___general.host___.

```
general.host=https://medcontact-api.herokuapp.com/
```

Poza tym, jeśli utworzyliśmy już bazę danych dla aplikacji, należy również podać w pliku konfiguracyjnym jej dane  
dostępowe. Chodzi tutaj o następujące pola:
* spring.datasource.url= {adres, pod którym dostępna jest baza danych}
* spring.datasource.driverClassName={sterownik dla odpowiedniego dialektu}
* spring.datasource.maxActive={maksymalna liczba aktywnych połączeń}
* spring.datasource.maxIdle={maksymalna liczba połączeń nieaktywnych}
* spring.datasource.minIdle={minimalna liczba połączeń nieaktywnych}
* spring.datasource.initialSize={początkowy rozmiar puli połączeń}
* spring.datasource.removeAbandoned={czy usuwać porzucone połączenia?}

Jeśli przy uruchomieniu aplikacji pojawi się błąd mówiący o niemożności odczytania dialektu SQL bazy, 
należy dodać pole __spring.jpa.properties.hibernate.dialect__

```
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL9Dialect
```

### Utworzenie aplikacji Heroku
Kolejnym krokiem jest utworzenie aplikacji Heroku. W linii poleceń wykonujmy polecenie:

```
heroku create medcontact-api
```

Od tej chwili aplikacja będzie widoczna w [panelu użytkownika](https://id.heroku.com/login).

### Utworzenie bazy danych (Heroku)

Należy mieć na uwadze, że aplikacja uruchomiona na zewnętrznym serwerze raczej nie będzie komunikowała  
się z lokalną bazą danych. Dlatego należy także zmodyfikować stosowne dane dostępowe. W związku z tym, że  
korzystamy z serwisu Heroku, wygodnym rozwiązaniem będzie wykorzystanie udostępnionej przez niego przez tego samego   
dostawcę bazy danych PostgreSQL. Tworzymy ją wykonując polecenie

```
heroku addons:create heroku-postgresql:hobby-dev
```

Następnie modyfikujemy plik __application.properties__ w następujący sposób:

```
# UWAGA: 
# wartość ${JDBC_DATABASE_URL} jest zmienną wykorzystywaną przez Heroku
# należy podać jej nazwę dokładnie w takiej formie!

spring.datasource.url=${JDBC_DATABASE_URL}
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.maxActive=10
spring.datasource.maxIdle=5
spring.datasource.minIdle=2
spring.datasource.initialSize=5
spring.datasource.removeAbandoned=true

# Jeśli przy uruchomieniu aplikacji pojawi się błąd mówiący o niemożności 
# odczytania dialektu SQL bazy, należy dodać pole

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL9Dialect
```

### Deployment

Po zainstalowaniu narzędzia CLI należy wykonać kilka kroków:
* dodajemy do głównego katalogu projektu plik Procfile który zawiera następującą linijkę
  
```
// target/[...].jar -> archiwum, w którym mają zostać zapisane pliki .class projektu
web: java -Dserver.port=$PORT -jar target/medcontact-0.0.1-SNAPSHOT.jar
```

* tworzymy w głównym katalogu projektu (w katalogu backend, a nie w nadrzędnym zawierającym backend i frontend!)  
repozytorium git i dodajmy zdalne repozytorium heroku
   
```
git init
heroku git:remote -a medcontact
```

* dodajemy pliki projektu do repozytorium
  
```
// jeśli w katalogu nie ma pliku .gitignore należy wskazać tylko te pliki i foldery,
// które chcemy dodać
git commit -am "Wiadomość"

```
* wysyłamy pliki na serwer Heroku
  
```
git push heroku master
```

### Monitorowanie uruchomienia aplikacji
Po wysłaniu plików możemy sprawdzić stan aplikacji uruchamiając komendę
  
```
heroku logs --tail
```

### Otwarcie w przeglądarce strony głównej
Istnieje prosty sposób na otworzenie głównej strony aplikacji w przeglądarce.   
Należy w katalogu głównym projektu wykonać komendę:
  
```
heroku open
```

### Źródła
* https://devcenter.heroku.com/articles/creating-apps
* https://devcenter.heroku.com/articles/deploying-spring-boot-apps-to-heroku  
* https://devcenter.heroku.com/articles/heroku-postgresql#provisioning-the-add-on
* https://elements.heroku.com/addons/heroku-postgresql


