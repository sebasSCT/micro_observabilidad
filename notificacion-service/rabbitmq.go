package main

import (
	"encoding/json"
	"github.com/streadway/amqp"
	"log"
	"os"
)

// Estructura del mensaje que se recibirá
type Message struct {
	Action string `json:"action"`
	Email  string `json:"email"`
}

// Conectar a RabbitMQ
func ConnectRabbitMQ() *amqp.Connection {

	rabbitmqHost := os.Getenv("RABBITMQ_HOST")
	rabbitmqUser := os.Getenv("RABBITMQ_USER")
	rabbitmqPass := os.Getenv("RABBITMQ_PASS")
	rabbitmqPort := os.Getenv("RABBITMQ_PORT")

	//"amqp://user:password@localhost:5672/"

	uri := "amqp://" + rabbitmqUser + ":" + rabbitmqPass + "@" + rabbitmqHost + ":" + rabbitmqPort + "/"

	conn, err := amqp.Dial(uri)
	if err != nil {
		log.Fatal("Failed to connect to RabbitMQ:", err)
	}
	return conn
}

// Consumir mensajes desde RabbitMQ
func ConsumeFromQueue(conn *amqp.Connection) {
	ch, err := conn.Channel()
	if err != nil {
		log.Fatal(err)
	}
	defer ch.Close()

	msgs, err := ch.Consume(
		"user_queue", // Nombre de la cola
		"",           // Consumidor
		true,         // Auto-ack
		false,        // Exclusivo
		false,        // No-wait
		false,        // Sin argumentos
		nil,
	)
	if err != nil {
		log.Fatal(err)
	}

	log.Println("Waiting for messages...")

	// Procesar los mensajes de la cola
	for d := range msgs {
		log.Printf("Received a message: %s", d.Body)

		var message Message
		err := json.Unmarshal(d.Body, &message)
		if err != nil {
			log.Println("Error decoding message:", err)
			continue
		}

		switch message.Action {
		case "create":
			CreateUserInDB(message.Email)
		case "update":
			UpdateUserInDB(message.Email)
		case "delete":
			DeleteUserFromDB(message.Email)
		default:
			log.Println("Unknown action:", message.Action)
		}
	}
}
