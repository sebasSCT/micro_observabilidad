# Etapa de construcción
FROM golang:1.20-alpine AS builder

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos go.mod y go.sum y descarga las dependencias
COPY go.mod go.sum ./
RUN go mod download

# Copia todo el código del proyecto
COPY . .

# Compila todos los archivos de Go en el binario
RUN go build -o /go/bin/notificacion-service ./main.go ./mongo.go ./handelrs.go ./rabbitmq.go ./email.go

# Etapa de ejecución
FROM alpine:3.18

# Copia el binario de la etapa de construcción
COPY --from=builder /go/bin/notificacion-service /usr/local/bin/notificacion-service

# Expone el puerto que utilizará la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["notificacion-service"]
