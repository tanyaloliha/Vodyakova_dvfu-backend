#!/usr/bin/env bash

echo "--- 1. Создать тренировку ---"
curl -i -X POST http://localhost:8080/workouts -H "Content-Type: application/json" -d '{"id":"w1","name":"Yoga","type":"CARDIO","maxCapacity":2}'

echo -e "\n--- 2. Создать обычного клиента ---"
curl -i -X POST http://localhost:8080/clients -H "Content-Type: application/json" -d '{"id":"c1","name":"Ivan","subscriptionType":"STANDARD"}'

echo -e "\n--- 3. Создать премиум клиента ---"
curl -i -X POST http://localhost:8080/clients -H "Content-Type: application/json" -d '{"id":"c2","name":"Petr","subscriptionType":"PREMIUM"}'

echo -e "\n--- 4. Записать клиента на тренировку ---"
curl -i -X POST http://localhost:8080/enroll/c1/w1

echo -e "\n--- 5. Попытка записать того же клиента повторно (ОШИБКА 400) ---"
curl -i -X POST http://localhost:8080/enroll/c1/w1

echo -e "\n--- 6. Создать вторую тренировку (вместимость 1) ---"
curl -i -X POST http://localhost:8080/workouts -H "Content-Type: application/json" -d '{"id":"w2","name":"Boxing","type":"STRENGTH","maxCapacity":1}'

echo -e "\n--- 7. Записать премиум клиента на вторую тренировку (места закончатся) ---"
curl -i -X POST http://localhost:8080/enroll/c2/w2

echo -e "\n--- 8. Создать третьего клиента и попытаться записать на заполненную тренировку (ОШИБКА 400) ---"
curl -i -X POST http://localhost:8080/clients -H "Content-Type: application/json" -d '{"id":"c3","name":"Anna","subscriptionType":"STANDARD"}'
curl -i -X POST http://localhost:8080/enroll/c3/w2

echo -e "\n--- 9. Отменить запись ---"
curl -i -X DELETE http://localhost:8080/enroll/c1/w1