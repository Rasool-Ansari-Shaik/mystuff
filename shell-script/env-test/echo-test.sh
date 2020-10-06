echo "parsing..."
cat .env

set -o allexport
source .env
set +o allexport
#export $(grep -v '^#' .env | xargs -0)
echo $name
echo $age
echo $place