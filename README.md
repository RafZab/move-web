# movie-web
Film Categorization System v1.0

## RUN

We enter the deployment folder and enter the command
``docker-compose up -d``

## Documentation

Documentation and testing capabilities can be found on the
website [Swagger UI](http://localhost:8080/api/v1/movie-library/swagger-ui/index.html).

## Business requirements

## Słownik:

- **Nazwa**
  Opis
- **Film**
  Plik w dowolnym formacie będący filmem, mały film to film o rozmiarze < 200 MB
- **DigiKat**
  Usługa zewnętrzna kategoryzacji filmów. Specyfikacja w rozdziale „Specyfikacja DigiKat”

## Wymagania funkcjonalne:

- **WF1:** System zapamiętuje filmy użytkowników rejestrując, tytuł, reżysera, rok produkcji
- **WF2:** Dane z WF1 mogą być zmieniane w każdym momencie przez użytkownika
- **WF3:** System oblicza ranking wg algorytmu:

1) dla małych filmów wartość rankingu wynosi zawsze 100
2) dla filmów polskiej produkcji wartość rankingu zwiększona 200
3) dla filmów dostępnych na Netflixie wartość rankingu zmniejszona o 50
4) dla filmów wybitnych wg oceny użytkowników wartość rankingu zwiększona o 100

- **WF4:** System prezentuje użytkownikom listę filmów, z możliwością sortowania względem rankingu, rozmiaru
- **WF5:** System umożliwia pobranie filmu na dysk lokalny

## Wymagania niefunkcjonalne:

- **NFR1:** Max rozmiar filmu to 1GB
- **NFR2:** Kategoryzacja odbywa się w oparciu o komunikację z usługami
- **NFR3:** Pokrycie testami na poziomie 95%
- **NFR4:** Backend zaimplementowany w Javie, Spring-Boot
- **NFR5:** System składowania persystentnego dowolny

## Specyfikacja DigiKat

Usługa DigiKat znajduje się na serwerze w domenie digikat.pl. Dostępna jest przy użyciu protokołów HTTPS.REST.
Specyfikacja usług:

1. **Pobieranie danych filmu**
   `GET /ranking?film=`
   gdzie:

film - tytuł filmu (max 300 znaków)

Odpowiedź:

JSON składający się z następujących pozycji:

- **tytul** - tytuł filmu (wartość z parametru film), wartość tekstowa o max długości 300 znaków
- **produkcja** - znacznik produkcji, integer, dozwolone wartości:

0 - produkcja polska, wspierana przez PISF

1 - produkcja polska, pozostałe

2 - produkcja zagraniczna

- **dostepnosc** - lista, dozwolone wartości to: netflix, youtube, disney, hbo
- **ocenaUzytkwonikow** - jedna z wartości: mierny, dobry, wybitny
- **ostaniaAktualizacja** - tekst, data ostatniej aktualizacji oceny użytkowników

2. **Aktualizacja rankingu**
   `POST /ranking?film=&ocenaKrytykow=`
   gdzie:

film - tytuł filmu (max 300 znaków)

ocenaKrytyków – integer, wartość 0-100