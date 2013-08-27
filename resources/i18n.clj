{:en {:auth {:login-title "Please, log in"
             :signup-title "Sign up for retwis"
             :username-label "Username or Email"
             :password-label "Password"
             :password-check-label "Repeat password"
             :remember-me "Remember me"
             :login-submit "login"
             :signup-submit "signup"
             :signup-link "Register"
             :recover-password-link "Recover password"}
      :profile {:follow-submit "follow"
                :unfollow-submit "unfollow"
                :tweets-title "Tweets"
                :no-tweets-msg "No tweets yet."
                :followers-title "Followers"
                :no-followers-msg  "No followers yet."
                :followees-title "Followees"
                :no-followees-msg "No followees yet."}
      :msgs-info {:following "You are following {0}"}
      :msgs-error {:blank "{0} can't be blank"
                   :length:within "{0} must be from {1} characters"
                   :format "{0} has incorrect format"
                   :password {:format "Password must contain at least one number, one letter and one symbol"}
                   :inclusion "{0} must be one of {1}"
                   :username-in-use "{0} already in use"
                   :retwis-clj
                   {:model {:user
                            {:user-not-found "User unknown"
                             :incorrect-password "Incorrect password"
                             :unknown-follower "Follower unknown"
                             :unknown-followee "Followee unknown"
                             :current-is-not-follower
                             "Follower is not the current authorized user"
                             :follower-is-followee
                             "You cant follow yourself!"}}}}
      :fields {:password-check "Password check"
               :password "Password"
               :username "User name"}
      :range "{0} to {1}"
      :missing "<Missing translation: {0}>"}
 :es {:auth {:login-title "Por favor, identifíquese"
             :signup-title "Registrar nuevo usuario"
             :username-label "Nombre de usuario o email"
             :password-label "Contraseña"
             :password-check-label "Repita la contraseña"
             :remember-me "Recuerdame"
             :login-submit "conectar"
             :signup-submit "registrar"
             :signup-link "Nuevo usuario"
             :recover-password-link "Recuperar contraseña"}
      :profile {:follow-submit "seguir"
                :unfollow-submit "no seguir"
                :tweets-title "Tweets"
                :no-tweets-msg "Ningun tweet todavia."
                :followers-title "Seguidores"
                :no-followers-msg  "Ningun seguidor todavia."
                :followees-title "Seguidos"
                :no-followees-msg "Ningun seguido todavia."}
      :msgs-info {:following "Estas siguiendo a {0}"}
      :msgs-error {:blank "{0} debe estar informado/a"
                   :length:within "{0} debe tener de {1} caracteres"
                   :format "{0} tiene un formato incorrecto"
                   :inclusion "{0} debe ser uno de {1}"
                   :username-in-use "{0} ya existe"
                   :password {:format "La contraseña debe contener al menos un numero, una letra y un simbolo"}
                   :retwis-clj
                   {:model
                    {:user {:user-not-found "Usuario desconocido"
                            :incorrect-password "Contraseña incorrecta"
                            :unknown-follower "Seguidor desconocido"
                            :unknown-followee "Usuario a seguir desconocido"
                            :current-is-not-follower
                            "El seguidor no es el actual usuario autorizado"
                            :follower-is-followee
                            "¡No puedes seguirte a ti mismo!"}}}}
      :fields {:password-check "La segunda contraseña"
               :password "La contraseña"
               :username "El nombre de usuario"}
      :range "{0} a {1}"
      :missing "<Traducción desconocida: {0}>"}}
