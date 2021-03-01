<?php
use Workerman\Connection\AsyncTcpConnection;
use Workerman\Worker;
require_once __DIR__ . '/Autoloader.php';
$users = [];//用户连接数组
$users_acc = [];//用户账号数组
$power_data=[];//当前数据数组
$drawing_key;//当前拥有绘画权限用的key
$power_key = null;

$power_data_count=0;
// 创建一个Worker监听8088端口，使用websocket协议通讯
$ws_worker = new Worker("websocket://0.0.0.0:8089");

// 启动1个进程对外提供服务
$ws_worker->count = 1;
$process = 0;
// 当收到客户端发来的连接之后
function is_drawing($key)
{
    global $drawing_Key;
    return $key==$drawing_Key;
};
$ws_worker->onConnect=function($connection){
    $_key = $connection->getRemoteIp() . $connection->getRemotePort();
    global $users;
    global $curdata;
    global $power_data;
    if (!isset($users[$_key])) {
        $users[$_key] = $connection;
      
    }
};
// 当收到客户端发来的数据后
$ws_worker->onMessage = function ($connection, $data) {
    $_key = $connection->getRemoteIp() . $connection->getRemotePort();
    global $users;
    global $curdata;
    global $users_acc;
    global $drawing_Key;
    global $power_key;
    global $power_data;
    global $power_data_count;
    echo "接受数据:\n";
    if(preg_match("/^account:(.*?)/isU", $data,$msgset ))
    {
        echo "account \n";
        if (!isset($users[$_key])) {
            $users[$_key] = $connection;
       
        }
        
       
        $users_acc[$_key] = $msgset[1];
        
        if($msgset[1]=="power")
        {
            echo "超级用户登录了\n";
            $power_key=$_key;
        }
        else {
            echo $msgset[1];
            echo "\n";
            
            foreach($power_data as $value)
            {$connection->send($value);}
            if ($power_key != null){
                $users[$power_key]->send("connect_user:" . $msgset[1]);
            }
            
        }
    }
    elseif(preg_match("/^request:(.*?)/isU", $data,$msgset ))
    {
        echo $users_acc[$_key];
        echo "\n";
        echo "请求了权限\n";
        $users[$power_key]->send("ask_user:" . $users_acc[$_key]);
    }
    elseif(preg_match("/^data:(.*?)/isU", $data,$msgset ))
    {
        echo "data \n";
      
        if($_key==$drawing_Key||$_key==$power_key)
        {   
            $curdata=$msgset[1];
            $power_data[$power_data_count]=$data;
            $power_data_count++;
            foreach ($users as $key => $user) {
                if ($key != $_key) {
                    $user->send($data);
                }
            }
        }
    }
    elseif(preg_match("/^response_user:(.*?)/isU", $data,$msgset ))
    {
        
        echo $msgset[1];
        echo "\n";
        echo "获得权限\n";
        foreach ($users_acc as $key => $username) {
            if ($username != $msgset[1]) {
                $users[$key]->send("response:0");
            }
            else {
                $drawing_Key=$key;
                $users[$key]->send("response:1");
            }
        }

    }
    elseif(preg_match("/^close!(.*?)/isU", $data,$msgset ))
    {
        if($users_acc[$_key]=="power")
        {
            echo "power closed! \n";
            $power_data=array();
            $power_data_count=0;
        }
        else{
            echo "a user closed!";
            $users[$power_key]->send("connection_closed:" . $users_acc[$_key]);    

        }
 
    }

};

    

    // 设置连接的onClose回调
    $connection->onClose = function ($connection) // 客户端主动关闭
{
        $_key = $connection->getRemoteIp() . $connection->getRemotePort();
        global $users;
        global $power_key;
        global $users_acc;
        global $power_data;
        global $power_data_count;

        if (isset($users[$_key])) {

            unset($users[$_key]);
            unset($users_acc[$_key]);
        }
        

        echo "connection closed\n";
    };


// 运行worker
Worker::runAll();
