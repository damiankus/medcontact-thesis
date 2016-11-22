## Sposób uruchomienia części frontendowej (Heroku)

### Instalacja narzędzia CLI
W celu skonfigurowania serwera Heroku należy użyć narzędzia linii komend Heroku CLI. Jeśli jeszcze tego   
nie zrobiliśmy, należy je zainstalować zgodnie z instrukcjami dostępnymi pod [tym adresem](https://devcenter.heroku.com/articles/heroku-command-line).  

### Przygotowanie projektu
Zanim uruchomimy aplikację, musimy odpowiednio zmodyfikować plik __package.json__. W polu __"start"__  
zastępujemy numer portu zmienną __$PORT__. Nie należy podawać dowolnie wybranej wartości, ponieważ    
Heroku samodzielnie wybiera port dla aplikacji.  

```
"start": "http-server -p $PORT -c-1 ./app",
```

Jeśli wszystkie osoby pracujące nad projektem używają systemu operayjnego z rodziny UNIX, można w tym miejscu  
podać wartość __${PORT:-wartość portu}__ (uwaga na myślnik!). Dzięki temu nie będzie potrzeby zmiany numeru portu    
przy każdym zdalnym uruchomieniu aplikacji.  

Dodatkowo należy w pliku __app/app.js__ przypisać do stałej __REST_API__ adres, pod którym dostępna jest część    
backendowa (np. https://medcontact-api.herokuapp.com). 

```
var myApp = angular.module('myApp', ['ngRoute'])
    .constant('REST_API', "https://medcontact-api.herokuapp.com/")
    .config(['$locationProvider', '$routeProvider', '$httpProvider',
        function ($locationProvider, $routeProvider, $httpProvider) {
            $locationProvider.hashPrefix('!');
            $routeProvider.otherwise({redirectTo: '/login'});
            $httpProvider.defaults.withCredentials = true;
        }]
    )
```

### Utworzenie aplikacji Heroku
Kolejnym krokiem jest utworzenie aplikacji Heroku. W linii poleceń wykonujmy polecenie:  

```
heroku create medcontact
```

Od tej chwili aplikacja będzie widoczna w [panelu użytkownika](https://id.heroku.com/login).

### Deployment

Po zainstalowaniu narzędzia CLI należy wykonać kilka kroków:  
* dodajemy do głównego katalogu projektu plik Procfile który zawiera następującą linijkę  
  
```
web: npm start
```

* tworzymy w głównym katalogu projektu (w katalogu __frontend__, nie w nadrzędnym zawierającym backend i frontend!)  
repozytorium git i dodajmy zdalne repozytorium heroku
   
```
git init
heroku git:remote -a medcontact <- nazwa aplikacji
```

* dodajemy pliki projektu do repozytorium
  
```
// jeśli w katalogu nie ma pliku .gitignore należy wskazać tylko te pliki i foldery,
// które chcemy dodać
git commit -am "frontend deployment"

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
* https://devcenter.heroku.com/articles/deploying-nodejs


