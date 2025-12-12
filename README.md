# Library Catalog System – Max Heap Search Analysis

Course: SENG 2000 – Advanced Data Structures

Institution: East Carolina University

Status: Work in Progress (Target Completion: December 12, 2025)

---

## Overview

This project implements a library catalog system designed to analyze search frequency for books and identify the most searched title using a max heap. The objective is to demonstrate advanced data structure proficiency within a practical use case.

The system is fully containerized with Docker, including separate services for the Java application, PostgreSQL database, and pgAdmin for database inspection. This ensures a clean, repeatable environment for building, running, and testing the project.

Although this is a group project, all current technical contributions—including environment setup and containerization—have been completed by me.

---

## Purpose of the Project

This is the final project for Advanced Data Structures (SENG 2000) at East Carolina University.

The project showcases:
* Implementation of advanced data structures
* Practical containerized development with Docker
* Integration of backend logic with persistent storage
* Real-world performance testing setup

---

## Current Features
✔ Fully Containerized Development Environment

Complete Docker Compose setup including:

* Java application container
* PostgreSQL database
* pgAdmin interface for database administration
* All services communicate over a shared Docker network.
* Database persistence supported via Docker volumes.

✔ Database Structure (Base Setup)

* PostgreSQL container is operational and configured.
* Ready for table creation and bulk data population.
* pgAdmin connection successfully validated.

✔ Database Data and Tables
* Dummy data created to be added to the database
* Script created to copy csv data into tables in the database

✔ Application Scaffold

* Java runtime environment is prepared in its own container.
* Project structure is in place for integrating heap logic and database interaction.

---

## How to Run the Project
Prerequisites:

* Docker
* Docker Compose
* .env file
* pgadmin/servers.json file
* pgadmin/pgpass file
* init.sh sent to LF instead of CRLF
  
Startup:
* docker compose up -d postgres pgadmin
* docker compose build --no-cache app
* docker compose run --rm app


Once running:

* Java App: Launches inside its container.
* PostgreSQL: Accessible via configured port.
* pgAdmin: Browser interface available for database inspection.

---

## Contributions

Andrea Garrido Menacho:
* Implemented the entire Docker environment, including:
  * Postgres container
  * pgAdmin container
  * Java application container
  * Verified container communication
  * Organized project structure for future heap integration
  * Prepared workflow for performance benchmarking using multiple dataset sizes
  * Included max heap code to integrate with the rest of the project
  * Tested of all parts of the project

---
## Technologies Used

| Category      | Technology           |
| ------------- | -------------------- |
| Container     | Docker               |
| Application   | Java                 |
| Database      | Postgres             |
| DB Management | pgAdmin              |
| Dummy Data    | Python Faker Library |

---

## Pending Tasks

1. Database Initialization (init.sql) **(COMPLETED)**
  * ~~Create and populate three tables with:~~  
    * ~~100 records~~
    * ~~1,000 records~~
    * ~~10,000 records~~
  * Used for performance testing of the heap operations.

2. Max Heap Implementation (Java) **(COMPLETED)**
 * ~~Build a max heap~~
 * ~~Track search counts for books~~
 * ~~Return the most searched book efficiently~~
 * ~~Integrate with PostgreSQL to fetch/update search frequency~~

3. Front-End UI (Optional) **(NOT COMPLETED DUE TO LACK OF TIME)**
  * A lightweight UI to:
    * Execute searches
    * Display top-searched books

4. Run performance tests
  * Would improve usability and streamline demos.
