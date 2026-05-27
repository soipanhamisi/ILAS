FROM mysql:8.4

# Configure these in Render as environment variables:
# MYSQL_ROOT_PASSWORD, MYSQL_DATABASE, MYSQL_USER, MYSQL_PASSWORD
# Attach a persistent disk to /var/lib/mysql so the database survives restarts.

EXPOSE 3306
VOLUME ["/var/lib/mysql"]

