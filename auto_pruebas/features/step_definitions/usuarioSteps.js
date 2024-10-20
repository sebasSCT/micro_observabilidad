const { Given, When, Then } = require('@cucumber/cucumber');
const axios = require('axios');
const assert = require('assert');

let loginRequest = {};
let loginResponse = {};
let searchResponse = {};

Given('Soy un usuario registrado con credenciales, correo {string} y contraseña {string}, mi codigo es {string}', 
    function (email, password, id) {
    loginRequest = {
        codigo: id,
        email: email,
        password: password
    };
});

When('Invoco el servicio de consulta de usuario', async function (){
    
    try {
        const login = {email: loginRequest.email, password: loginRequest.password};
        const response = await axios.post('http://localhost:8084/api/auth/usuarios/login', login);
        loginResponse = response.data;
        const token = loginResponse.respuesta.token;
        const headers = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };
        const response1 = await axios.get(`http://localhost:8084/api/usuarios/${loginRequest.codigo}`, headers);
        searchResponse = response1.data;
    } catch (error) {
        loginResponse = error.response.data;
        searchResponse = error.response.data;
    };
    
});

Then ('Obtengo correctamente los datos', function (){
    // console.log(searchResponse);
    assert.notEqual(searchResponse.respuesta, null);
});

//Sceneario 2

Given('Soy usuario con las credenciales, correo {string} y contraseña {string}, deseo poder cambiar los datos, codigo {string}, correo {string}, nombre {string} y apellido {string}',
    function(email, password, id, new_email, name, lastname) {
        loginRequest = {
            codigo: id,
            email: email,
            password: password
        };
        updateRequest = {
            codigo: id,
            email: new_email,
            nombre: name,
            apellido: lastname
        };
    });

When('Invoco el servicio de actualizar perfil de usuario', async function (){

    try {
        const login = {email: loginRequest.email, password: loginRequest.password};
        const response = await axios.post('http://localhost:8084/api/auth/usuarios/login', login);
        loginResponse = response.data;
        const token = loginResponse.respuesta.token;
        const headers = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };
        const response1 = await axios.put(`http://localhost:8084/api/usuarios/${loginRequest.codigo}`, updateRequest, headers);
        searchResponse = response1.data;
    } catch (error) {
        loginResponse = error.response.data;
        searchResponse = error.response.data;
    };
    
});

Then ('Los datos del perfil se actualizan correctamente', function (){
    // console.log(searchResponse);
    assert.strictEqual(searchResponse.error, false);
});

//Scenario 3

Given('Soy usuario con las credenciales, correo {string} y contraseña {string}, deseo eliminar el perfil con codigo {string}', 
    function (email, password, id) {
    loginRequest = {
        codigo: id,
        email: email,
        password: password
    };
});

When('Invoco el serivicio para eliminar el usuario', async function (){

    try {
        const login = {email: loginRequest.email, password: loginRequest.password};
        const response = await axios.post('http://localhost:8084/api/auth/usuarios/login', login);
        loginResponse = response.data;
        const token = loginResponse.respuesta.token;
        const headers = {
            headers: {
                Authorization: `Bearer ${token}`
            }
        };
        const response1 = await axios.delete(`http://localhost:8084/api/usuarios/${loginRequest.codigo}`, headers);
        searchResponse = response1.data;
    } catch (error) {
        loginResponse = error.response.data;
        searchResponse = error.response.data;
    };
    
});

Then ('Elimino el perfil de usuario correctamente', function (){
    // console.log(searchResponse);
    assert.strictEqual(searchResponse.error, false);
});