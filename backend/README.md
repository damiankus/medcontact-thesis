### Sposób uruchomienia części backendowej (Heroku)
W celu uruchomienia backendu na serwerze Heroku należy wykonać kilka kroków:
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