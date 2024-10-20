package main

import (
	"github.com/gin-gonic/gin"
	"log"
	"os"
	"os/signal"
	"syscall"
)

func main() {
	// Conectar a MongoDB
	ConnectMongo()

	// Conectar a RabbitMQ
	conn := ConnectRabbitMQ()
	defer conn.Close()

	// Configurar API para notificaciones
	r := gin.Default()
	r.POST("/send", SendNotification)
	r.GET("/notifications", GetAllNotifications)

	// Iniciar el servidor en un goroutine
	go func() {
		if err := r.Run(":8080"); err != nil {
			log.Fatalf("Error starting server: %s", err)
		}
	}()

	// Consumir mensajes desde RabbitMQ
	go ConsumeFromQueue(conn)

	// Esperar señal de finalización
	signalChan := make(chan os.Signal, 1)
	signal.Notify(signalChan, os.Interrupt, syscall.SIGTERM)
	<-signalChan

	log.Println("Shutting down gracefully...")
}
