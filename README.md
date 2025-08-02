# movie-web

Film Categorization System v1.0

## RUN

We enter the deployment folder and enter the command
``docker-compose up -d``

## Documentation

Documentation and testing capabilities can be found on the
website [Swagger UI](http://localhost:8080/api/v1/movie-library/swagger-ui/index.html).

### User for test:

| Login:    | r.s.zaborowski@gmail.com |
|:----------|:-------------------------|
| Password: | password                 | 

## Business requirements

In order to facilitate the implementation of the recruitment task,
a simple baseAuth mechanism (verification based on login: email and password: password)
was used for user authorization. However, it should be noted that in a real production
application, an external system such as Keycloak or another dedicated user identity
and access management service would be used for authorization.

### Glossary:

- **Title**
  Description
- **Film**
  A file in any format that is a movie; a small film is a movie with size < 200 MB
- **DigiKat**
  An external movie categorization service. Specification in the chapter “DigiKat Specification”

### Functional Requirements:

- **FR1:** The system stores users’ movies by registering the title, director, and production year
- **FR2:** Data from FR1 can be changed by the user at any time
- **FR3:** The system calculates ranking according to the algorithm:

1) For small movies, the ranking value is always 100
2) For Polish production movies, the ranking value is increased by 200
3) For movies available on Netflix, the ranking value is decreased by 50
4) For outstanding movies according to user ratings, the ranking value is increased by 100

- **FR4:** The system presents users with a list of movies, with the option to sort by ranking or size
- **FR5:** The system allows downloading the movie to local disk

### Non-Functional Requirements:

- **NFR1:** Maximum movie size is 1GB
- **NFR2:** Categorization is performed based on communication with services
- **NFR3:** Test coverage at the level of 95%
- **NFR4:** Backend implemented in Java, Spring Boot
- **NFR5:** Persistent storage system is arbitrary

### DigiKat Specification

The DigiKat service is located on a server in the digikat.pl domain. It is available via HTTPS REST protocols. Service
specification:

1. **Getting movie data**
   `GET /ranking?film=`
   where:

film – movie title (max 300 characters)

Response:

JSON consisting of the following fields:

- **title** - movie title (value from film parameter), text value max 300 characters
- **production** - production marker, integer, allowed values:

0 - Polish production supported by PISF

1 - Polish production, others

2 - foreign production

- **availability** - list, allowed values: netflix, youtube, disney, hbo
- **userRating** - one of the values: poor, good, outstanding
- **lastUpdate** - text, date of last user rating update

2. **Updating ranking**
   `POST /ranking?film=&criticRating=`
   where:

film – movie title (max 300 characters)

criticRating – integer, value 0–100