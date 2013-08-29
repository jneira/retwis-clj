# retwis-clj

This project follow the convention of other example apps to test and
play with the [redis](http://redis.io/) no-sql datastore:
http://redis.io/topics/twitter-clone

I chose the [recommended](http://redis.io/clients) clojure library to
access redis: [carmine](https://github.com/ptaoussanis/carmine).

I used
[a leiningen template](https://github.com/eprunier/lein-webapp-template)
to generate the initial app. The template is based on [compojure](https://github.com/weavejester/compojure),
[stencil](https://github.com/davidsantiago/stencil) (clojure lib for mustache html templates), twitter bootstrap
and jquery.

## Internacionalization and validation

I added [tower](https://github.com/ptaoussanis/tower) to handle i18n 
and [validateur](https://github.com/michaelklishin/validateur) to
validate domain data.

I helped to add support for i18n in validateur via an optional
function callback to build the error message. In this app the callback
is translate-error:

```clojure
(ns retwis-clj.view.common ...)

(defn format-error-args [type [f s & more :as args]]
  (case type
    :length:within [(i18n/t :range (first f) (last f))]
    :inclusion [(apply str (interpose \, f))]
    args))s

(defn translate-error
  ([type] (translate-error type {} []))
  ([type m attr & args]
     (let [field (i18n/with-scope :fields (i18n/t attr))
           args (format-error-args type args)
           attr+type (keyword (str (name attr) "/" (name type)))]
       (i18n/with-scope :msgs-error
         (apply i18n/t [attr+type type] field args)))))
```
The function search in a dictionary (a clojure map) the
field translated and pass it with other args to the message retrieved
by attributed and type of validation and secondary only by type:
```clojure
{:en  ...
      :msgs-error {
          ...
          :length:within "{0} must be from {1} characters"
          :format "{0} has incorrect format"
          :password {:format "Password must contain at least one number, one letter and one symbol"}
          ...}
      :fields {
          :username "User name"
          :password "Password"
          ...}
      :range "{0} to {1}"
      ...}
```
Example of use:
```clojure
retwis-clj.view.common> (translate-error :length:within {:password "short"} :password (range 7 15))
"Password must be from 7 to 14 characters"
retwis-clj.view.common> (translate-error :format {:username "invalid"} :username #"pattern")
"User name has incorrect format"
retwis-clj.view.common> (translate-error :format {:password "invalid"} :password #"pattern")
"Password must contain at least one number, one letter and one symbol"
```
For details of that function usage with validateur read its relevant doc strings and
  [changelog](https://github.com/michaelklishin/validateur/blob/master/ChangeLog.md#optional-function-callback-to-parametrize-the-construction-of-messages).

The i18n boilerplate could be hidden in the layout or as a ring
middleware. I don't know if it exists already. Same for validate form input.

## Live instance
The app is deployed in heroku: http://retwis-clj.herokuapp.com 

## Usage
Launch the application by issuing one of the following commands:

```shell
lein run <port>
lein ring server
```

You can generate a standalone jar and run it:

```shell   
lein uberjar
java -jar target/retwis-clj-0.1.0-SNAPSHOT-standalone.jar
```

You can also generate a war to deploy on a server like Tomcat, Jboss...

```shell
lein ring uberwar
```

## License

Distributed under the Eclipse Public License, the same as Clojure.
