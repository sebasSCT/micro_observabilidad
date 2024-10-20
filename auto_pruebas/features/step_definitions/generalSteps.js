const { Given, When, Then } = require('@cucumber/cucumber');
const axios = require('axios');
const assert = require('assert');
const { isAsyncFunction } = require('util/types');

let listRequest = {};
let listResponse = {};

//Scenario: Yo como administrador deseo ver la lista de usuarios registrados

Given('Yo administrador con credenciales válidas', function () {
    listRequest = {}
});

When('invoco servicio para listar usuarios', async function (){
    try {
        const response = await axios.get('http://localhost:8084/api/general/usuarios', listRequest);
        listResponse = response.data;
    } catch (error) {
        listResponse = error.response.data;
    }
});

Then ('Obtengo la respuesta con la lista de usuarios', function (){
    // console.log(listResponse);
    assert.notEqual(listResponse.content, null);
});