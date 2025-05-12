# Java - gRPC - OTel application
Thursdays at 8:45 AM, 2025

## Authors
- Mateusz Waga
- Witold Strzeboński
- Jacek Urbanowicz
- Mateusz Więcek

## Introduction
The goal is to demonstrate the use of microservices in Java communicating via gRPC, with full observability enabled through OpenTelemetry and Grafana. The application is based on a ready-made, [open-source microservices project](https://nikhilm.com/blogs/grpc-in-action---example-using-java-microservices), extended and instrumented to meet observability and infrastructure criteria.

The selected application, titled "Movie Finder", showcases the use of gRPC communication between microservices to build a lightweight and efficient movie recommendation system. It consists of four distinct services that collaborate to deliver personalized recommendations based on user preferences. The services communicate using different gRPC patterns (unary, server/client streaming, and bidirectional streaming), which makes the project a valuable case study for learning both gRPC and microservice architecture in practice.

The project also integrates observability tools such as OpenTelemetry for tracing, metrics and logs, and Grafana for visualization, following current best practices for monitoring distributed systems.

## Technology stack
The project uses the following technologies:
- **Java** – primary programming language for implementing microservices
- **gRPC** – a modern, high-performance Remote Procedure Call (RPC) framework that enables efficient inter-service communication
- **Protocol Buffers (Protobuf)** – a language-neutral, platform-neutral extensible mechanism for serializing structured data; used to define service contracts in gRPC
- **Docker** – used for containerizing services, ensuring portability and consistency across environments
- **Kubernetes** – used to orchestrate the deployment of containerized services in production-like environments
- **OpenTelemetry** – provides a standard way to collect, process, and export telemetry data such as traces, metrics, and logs
- **Grafana** – a visualization platform used to display telemetry data and monitor the health and performance of services

The project demonstrates all four types of gRPC communication:
- **Unary RPC** – One request followed by one response.
- **Server-side streaming RPC** – one request followed by a stream of responses
- **Client-side streaming RPC** – a stream of requests followed by one response
- **Bidirectional streaming RPC** – both client and server send a stream of messages

## Case study concept description
![image](https://github.com/user-attachments/assets/aea441e9-8146-4554-a49c-ef4d6ce37023)
