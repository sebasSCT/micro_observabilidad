package main

import (
	"github.com/gin-gonic/gin"
	"net/http"
)

type NotificationRequest struct {
	Email   string `json:"email"`
	Message string `json:"message"`
}

func SendNotification(c *gin.Context) {
	var request NotificationRequest

	if err := c.ShouldBindJSON(&request); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	err := SaveNotification(request.Email, request.Message)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to save notification"})
		return
	}

	err = SendEmail(request.Email, request.Message) // Implementación del envío de correo
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to send email"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"status": "Notification sent successfully!"})
}
