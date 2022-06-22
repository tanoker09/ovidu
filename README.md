# Contest Streamer (Backend)

## Развертывание проекта локально для разработки

Для локального запуска OpenVidu и СУБД используется docker-compose.
Необходимые сервисы прописаны в файле `docker-compose.yml`.

В папке `bin/dev/` есть удобные команды для запуска контейнеров:

- `bin/dev/setup` - запустит Docker-контейнеры c OpenVidu и СУБД PostgreSQL, создаст необходимую структуру БД.
- `bin/dev/destroy` - удалит все Docker-контейнеры.
- `bin/dev/run` - соберет и запустит Backend проект при помощи `maven`.
- `bin/dev/psql` - запустит `psql` консоль в СУБД.

**Важно**, чтобы порты всех сервисов были доступны на домене `localhost`.
Если ваш Docker запущен в виртуальной машине и имеет другой IP, то настройте
forwarding портов на localhost.

#### Ошибка SSL сертификата при соединении клиента с OpenVidu

OpenVidu, запущенный локально, использует самоподписанные SSL сертификаты.
Они будут блокироваться браузером до тех пор, пока вы не откроете
адрес локального OpenVidu сервера в браузере и не разрешите использовать данный сертификат.

Локальный адрес OpenVidu сервера для разработки: https://localhost:4443/


## Развертывание на production сервере

Развертывание на сервере производится вручную и состоит из шагов:

1. Развертывание и настройка OpenVidu.
2. Сборка и запуск приложения в локальном Docker контейнере.
3. Запуск СУБД PostgreSQL, создание и заливка структуры БД.

### Развертывание и настройка OpenVidu

OpenVidu нужно установить согласно [официальной инструкции](https://docs.openvidu.io/en/2.15.0/deployment/deploying-on-premises/).
OpenVidu будет запущен на сервере в Docker контейнерах.

Необходимо сделать следующие настройки (файл `/opt/openvidu/.env`):

```
DOMAIN_OR_PUBLIC_IP=yoursitednsname.ru
OPENVIDU_SECRET=YourOpenViduSecret
CERTIFICATE_TYPE=letsencrypt
LETSENCRYPT_EMAIL=your@email.ru
OPENVIDU_RECORDING=true
OPENVIDU_RECORDING_PUBLIC_ACCESS=true
OPENVIDU_RECORDING_AUTOSTOP_TIMEOUT=1200000
```

### Сборка и запуск приложения в локальном Docker контейнере

Приложение и БД PostgreSQL запускаются на сервере в Docker контейнерах согласно
[официальной инструкции](https://docs.openvidu.io/en/2.15.0/deployment/deploying-openvidu-apps/).

Мы подменяем файл `/opt/openvidu/docker-compose.override.yml` своим, в котором указаны два сервиса:
один для приложения, еще один для СУБД PostgreSQL. Пример override-файла расположен в файле
`deploy/docker-compose.override.yml` данного репозитория. При желании можно отредактировать
environment переменные в данном фале, чтобы изменить пароли доступа к
приложению dashboard (`API_PASSWORD`) и БД (`DB_PASSWORD`).

Override-файл ссылается на Docker образ `contest-streamer-app`, который необходимо собрать на сервере.
Данный образ будет включать в себя и backend, и frontend приложения. Образ собирается при
помощи файла `deploy/Dockerfile`, который расположен в данном репозитории.

Сборка образа потребует доступа к приватным репозиториям на GitHub, где хранятся исходные файлы backend и frontend приложений.
При сборке также необходимо указать URL адрес на котором будет располагаться приложение.

Пример команды для сборки образа:
```
$ docker build \
  --tag contest-streamer-app \
  --build-arg APP_URL=https://app.ru \
  --build-arg FRONTEND_REPO=https://user:password@github.com/frontend-repo \
  --build-arg BACKEND_REPO=https://user:password@github.com/backend-repo \
  --no-cache \
  .
```

Пример скрипта для сборки образа на сервере можно найти в файле `deploy/build.sh` данного репозитория.

### Запуск СУБД PostgreSQL, создание и заливка структуры БД

СУБД PostgreSQL (как и само приложение) уже развернуто в Docker контейнере
(см. файл `docker-compose.yml`). Осталось создать БД вручную и залить ее структуру. Для этого нужно:

- запустить `psql` внутри контейнера `postgres`: `docker-compose exec postgres psql -U postgres`;
- создать БД: `CREATE DATABASE contest_streamer;`;
- выполнить команды создания структуры БД из файла `src/main/resources/backup.sql` данного репозитория.

P.S. Возможно потребуется перезапустить Docker-контейнер приложения после данных операций.
