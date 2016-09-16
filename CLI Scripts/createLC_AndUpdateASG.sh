aws autoscaling create-launch-configuration --launch-configuration-name $1 --image-id $2 --instance-type $3 

aws autoscaling update-auto-scaling-group --auto-scaling-group-name $4 --launch-configuration-name $1
