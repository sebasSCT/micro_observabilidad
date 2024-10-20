Feature: Autenticación

  Escenarios de Autenticación de los usuarios

  Scenario: Yo como usuario registrado quiero poder iniciar sesion
    Given Yo usuario registrado inicio sesión con mis credenciales usuario "hola@gmail" y contraseña "123"
    When Invoco el sercivicio para inicio de sesion
    Then Inicio sesion correctamente
    
  Scenario: Yo como usuario deseo recibir notificacion sobre un inicio fallido de sesion
    Given Yo usuario registrado inicio sesión con mis credenciales usuario "hola@gmail" y contraseña "1234"
    When Invoco el sercivicio para inicio de sesion
    Then No puedo iniciar sesion

  Scenario: Yo como usuario no registrado quiero poder registrarme con mis datos
    Given Yo usuario no registrado envio mis datos correo "hola2@gmail", contraseña "1234", nombre "hola" y apellido "perez"
    When Invoco el servicio que permite el registro de nuevos usuarios
    Then Me registro correctamente

  Scenario: Yo como usuario no registrado deseo recibir notificacion si realizo mal el registro
    Given Yo usuario no registrado envio mis datos correo "hola2@gmail", contraseña "1234", nombre "hola" y apellido "perez"
    When Invoco el servicio que permite el registro de nuevos usuarios
    Then No puedo registrarme
