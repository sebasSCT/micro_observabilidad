Feature: Operaciones generales

    Controlador para operaciones generales

    Scenario: Yo como administrador deseo ver la lista de usuarios registrados
        Given Yo administrador con credenciales válidas
        When invoco servicio para listar usuarios
        Then Obtengo la respuesta con la lista de usuarios