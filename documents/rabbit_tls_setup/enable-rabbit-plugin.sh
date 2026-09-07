sudo docker compose exec rabbitmq rabbitmq-plugins enable rabbitmq_auth_mechanism_ssl
#sudo docker compose exec rabbitmq rabbitmqctl delete_user client
sudo docker compose exec rabbitmq rabbitmqctl add_user client temporary_placeholder_pw
sudo docker compose exec rabbitmq rabbitmqctl clear_password client
sudo docker compose exec rabbitmq rabbitmqctl set_permissions -p / client ".*" ".*" ".*"
sudo docker compose restart rabbitmq