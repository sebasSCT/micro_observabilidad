package main

import (
	"context"
	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"log"
	"net/http"
	"os"
)

var notificationCollection *mongo.Collection
var userCollection *mongo.Collection

// Conectar a MongoDB
func ConnectMongo() {

	mongoHost := os.Getenv("MONGO_HOST")
	mongoPort := os.Getenv("MONGO_PORT")
	mongoUser := os.Getenv("MONGO_USER")
	mongoPassword := os.Getenv("MONGO_PASSWORD")
	mongoDB := os.Getenv("MONGO_DB")

	//mongodb://root:example@localhost:27017
	uri := "mongodb://" + mongoUser + ":" + mongoPassword + "@" + mongoHost + ":" + mongoPort
	log.Println(uri)
	clientOptions := options.Client().ApplyURI(uri)
	client, err := mongo.Connect(context.TODO(), clientOptions)
	if err != nil {
		log.Fatal("Failed to connect to MongoDB:", err)
	}

	notificationCollection = client.Database(mongoDB).Collection("notificacion")
	userCollection = client.Database(mongoDB).Collection("usuarios")

}

// Crear un usuario en MongoDB
func CreateUserInDB(email string) {
	user := bson.M{"email": email}
	_, err := userCollection.InsertOne(context.Background(), user)
	if err != nil {
		log.Println("Error creating user in DB:", err)
	} else {
		log.Println("User created successfully:", email)
	}
}

// Actualizar un usuario en MongoDB
func UpdateUserInDB(email string) {
	filter := bson.M{"email": email}
	update := bson.M{"$set": bson.M{"email": email}}

	_, err := userCollection.UpdateOne(context.Background(), filter, update)
	if err != nil {
		log.Println("Error updating user in DB:", err)
	} else {
		log.Println("User updated successfully:", email)
	}
}

// Eliminar un usuario de MongoDB
func DeleteUserFromDB(email string) {
	filter := bson.M{"email": email}
	_, err := userCollection.DeleteOne(context.Background(), filter)
	if err != nil {
		log.Println("Error deleting user in DB:", err)
	} else {
		log.Println("User deleted successfully:", email)
	}
}

// Guardar una notificación en MongoDB
func SaveNotification(email string, message string) error {
	notification := bson.M{
		"_id":     primitive.NewObjectID(),
		"email":   email,
		"message": message,
	}

	_, err := notificationCollection.InsertOne(context.Background(), notification)
	if err != nil {
		log.Println("Error saving notification:", err)
		return err
	}

	log.Println("Notification saved successfully")
	return nil
}

func GetAllNotifications(c *gin.Context) {

	filter := bson.M{}

	cursor, err := notificationCollection.Find(context.Background(), filter)
	if err != nil {
		log.Println("Error fetching notifications:", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch notifications"})
		return
	}
	defer cursor.Close(context.Background())

	var notifications []bson.M
	if err = cursor.All(context.Background(), &notifications); err != nil {
		log.Println("Error parsing notifications:", err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to parse notifications"})
		return
	}
	c.JSON(http.StatusOK, notifications)
}

func GetNotification(c *gin.Context) {
	id := c.Param("id")
	notification, err := GetNotificationByID(id)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to fetch notification"})
		return
	}

	if notification == nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "Notification not found"})
		return
	}

	c.JSON(http.StatusOK, notification)
}

func GetNotificationByID(id string) (bson.M, error) {
	// Convertir el ID a ObjectID
	objID, err := primitive.ObjectIDFromHex(id)
	if err != nil {
		log.Println("Invalid ID format:", err)
		return nil, err
	}

	// Buscar el documento en la colección
	var notification bson.M
	filter := bson.M{"_id": objID}
	err = notificationCollection.FindOne(context.Background(), filter).Decode(&notification)
	if err != nil {
		if err == mongo.ErrNoDocuments {
			// Manejo del caso donde no se encuentra el documento
			return nil, nil
		}
		log.Println("Error fetching notification:", err)
		return nil, err
	}

	return notification, nil
}
