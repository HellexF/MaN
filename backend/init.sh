MYSQL_USER="root"
MYSQL_PASSWORD="fjt030815"
MYSQL_HOST="localhost"
MYSQL_PORT="3306"
DATABASE_NAME="MaN_Database"
INIT_SQL_FILE="init.sql"

# 创建数据库
echo "Creating database ${DATABASE_NAME}..."
mysql -u${MYSQL_USER} -p${MYSQL_PASSWORD} -h${MYSQL_HOST} -P${MYSQL_PORT} -e "CREATE DATABASE IF NOT EXISTS ${DATABASE_NAME} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 初始化数据库
if [ -f ${INIT_SQL_FILE} ]; then
    echo "Initializing database ${DATABASE_NAME} with ${INIT_SQL_FILE}..."
    mysql -u${MYSQL_USER} -p${MYSQL_PASSWORD} -h${MYSQL_HOST} -P${MYSQL_PORT} ${DATABASE_NAME} < ${INIT_SQL_FILE}
else
    echo "Initialization SQL file ${INIT_SQL_FILE} not found."
fi

echo "Database ${DATABASE_NAME} created and initialized."