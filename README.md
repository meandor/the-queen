# The Queen
[![Build Status](https://travis-ci.org/meandor/the-queen.svg?branch=master)](https://travis-ci.org/meandor/the-queen)

> Known for her sense of duty and her devotion to a life of service, she has been an important figurehead [...]
>
> -- https://www.royal.uk/her-majesty-the-queen

The Queen is a authentication and authorization authority. The Queen authorizes you and 
will issue your passport for other services to grant you entree. It can therefore be used
as a [SSO](https://en.wikipedia.org/wiki/Single_sign-on) service.

The authentication process is Kerberos 5 compliant and uses sha-256 encryption/decryption.

All parts containing cryptographic methods are carefully selected and according to the latest (2018)
guidelines of the BSI (Bundesamt für Sicherheit in der Informationstechnik).

The authorization process is OAuth2 compliant. It can be used to authorize other services and
web apps to access personal data (user data) from the queen. 

## Links
* [Kerberos Workflow](https://www.kerberos.org/software/tutorial.html)
* [OAuth2 Overview](https://oauth.net/2/)
* [BSI Guidelines (german)](https://www.bsi.bund.de/SharedDocs/Downloads/DE/BSI/Publikationen/TechnischeRichtlinien/TR02102/BSI-TR-02102.pdf?__blob=publicationFile)
