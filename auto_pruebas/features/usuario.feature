Feature: Operaciones del usuario

 Escenarios propios de modificación por parte del usuario

    Scenario: Yo como usuario registrado deseo poder consultar mi informacion guardada
        Given Soy un usuario registrado con credenciales, correo "hola@gmail" y contraseña "123", mi codigo es "66e979413878b45a690c7208"
        When Invoco el servicio de consulta de usuario
        Then Obtengo correctamente los datos

    Scenario: Yo como usuario deseo actualizar mis datos
        Given Soy usuario con las credenciales, correo "hola@gmail" y contraseña "123", deseo poder cambiar los datos, codigo "66e979413878b45a690c7208", correo "hola@gmail", nombre "hola" y apellido "quetaro"
        When Invoco el servicio de actualizar perfil de usuario
        Then Los datos del perfil se actualizan correctamente

    Scenario: Yo como usuario deseo poder eliminar mi cuenta
        Given Soy usuario con las credenciales, correo "borrar@gmail" y contraseña "123", deseo eliminar el perfil con codigo "66e977c83878b45a690c7206"
        When Invoco el serivicio para eliminar el usuario
        Then Elimino el perfil de usuario correctamente