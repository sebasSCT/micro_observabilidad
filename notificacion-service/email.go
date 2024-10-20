package main

import (
	"fmt"
	"log"
	"net/smtp"
)

func SendEmail(to string, message string) error {
	from := "az0031456@gmail.com"
	password := "oecoibbxwzbfifom"

	smtpHost := "smtp.gmail.com"
	smtpPort := "587"

	msg := []byte(fmt.Sprintf("To: %s\r\nSubject: Notificación\r\n\r\n%s", to, message))

	auth := smtp.PlainAuth("", from, password, smtpHost)
	err := smtp.SendMail(smtpHost+":"+smtpPort, auth, from, []string{to}, msg)
	if err != nil {
		log.Println("Failed to send email:", err)
		return err
	}

	log.Println("Email sent successfully to:", to)
	return nil
}
