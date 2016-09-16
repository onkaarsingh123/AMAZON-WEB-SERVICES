aws autoscaling create-auto-scaling-group --auto-scaling-group-name $1 --launch-configuration-name $2 --min-size 1 --max-size 2 --vpc-zone-identifier $3  
