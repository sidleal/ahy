#Main spec issues and requirement notes.

### Ahy - A Pure Java CMS ###
"For those tired of 'browser wars' and suffering."

Our main goals:

  * Run on Jboss App Server.
  * Database Independence (with JPA).
  * Scalability (with EJB3).
  * No javascript allowed and no Browser specific-code.
  * Must have:
    1. One pure html interface (allowing sites to be found by search engines and be accessed by old devices)
    1. One rich interface, with drag n drog and everything else we deserve (JavaFX).
    1. A rich admin module, extremely easy to use.
  * Custom xml-based templates/themes.


# Installation Wizzard #

On the first run, the app must check the jboss config, ask user data and write it to:
  * deploy/mail-service.xml (smtp server, port, user and passwd)
  * properties-service.xml (static config like database type)
  * ahy-cms-ds.xml (Database connection string and authentication data)

after that, the update engine will create all tables and insert sample data.


# Working on it... #