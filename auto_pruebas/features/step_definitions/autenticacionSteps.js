const { Given, When, Then } = require('@cucumber/cucumber');
const axios = require('axios');
const assert = require('assert');

let loginRequest = {};
let loginResponse = {};

//Scenario: Yo como usuario registrado quiero poder iniciar sesion

Given('Yo usuario registrado inicio sesión con mis credenciales usuario {string} y contraseña {string}', function (email, password) {
    loginRequest = {
        email: email,
        password: password
    };
});

When('Invoco el sercivicio para inicio de sesion', async function () {
    try {
        const response = await axios.post('http://localhost:8084/api/auth/usuarios/login', loginRequest);
        loginResponse = response.data;
    } catch (error) {
        loginResponse = error.response.data;
    }
});

Then('Inicio sesion correctamente', function () {
    // console.log(loginResponse);
    assert.equal(loginResponse.error, false);
});

//Scenario: Yo como usuario deseo recibir notificacion sobre un inicio fallido de sesion

Then('No puedo iniciar sesion', function () {
    // console.log(loginResponse);
    assert.strictEqual(loginResponse.error, true);
});


// Scenario: Yo como usuario no registrado quiero poder registrarme con mis datos

Given('Yo usuario no registrado envio mis datos correo {string}, contraseña {string}, nombre {string} y apellido {string}',
     function (email, password, name, lastname) {
    loginRequest = {
        email: email,
        password: password,
        nombre: name,
        apellido: lastname   
    };
});

When ('Invoco el servicio que permite el registro de nuevos usuarios', async function (){
    try {
        const response = await axios.post('http://localhost:8084/api/auth/usuarios', loginRequest);
        loginResponse = response.data;
    } catch (error) {
        loginResponse = error.response.data;
    }
});

Then('Me registro correctamente', function () {
    // console.log(loginResponse);
    assert.strictEqual(loginResponse.error, false);
});

//Scenario: Yo como usuario no registrado deseo recibir notificacion si realizo mal el registro

Then('No puedo registrarme', function () {
    // console.log(loginResponse);
    assert.strictEqual(loginResponse.error, true);
});