podman run -d --name redis-container -p 6379:6379 redis:latest

podman run -d --name mysql-container \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=mydb \
  -e MYSQL_USER=dbuser \
  -e MYSQL_PASSWORD=dbpassword \
  -p 3306:3306 mysql:latest