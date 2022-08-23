# CrossChat
一个跨服务器 & QQ 聊天的BC插件   

![](https://s1.328888.xyz/2022/08/23/biYjg.png)
1. ### 功能  
- 通过CQHTTP将服务器聊天和QQ群聊桥接
- 玩家的加入，退出等事件的自定义提示
- 通过QQ群获取玩家列表 (计划)
- 聊天禁言 (计划)
2. ### 配置文件
Cross Chat/config.yml    
````
join: "§8[§f§2+§f§8]§b %s"       #你的自定义玩家加入提示，其中%s代表玩家的名字
left: "§8[§f§2-§f§8]§b %s"       #你的自定义玩家退出提示，其中%s代表玩家的名字
change: "§b%s %2$s §8->§f %3$s"  #玩家切换服务器提示，其中%s代表玩家的名字, %2$s代表玩家原先的服务器, %3$s代表玩家现在的服务器
chat: "§7[%2$s§7] %s: %3$s"      #跨服聊天提示, 其中%s代表玩家的名字，%2$s代表玩家所处的服务器或者qq群, %3$s是玩家说的话

port: 8090                       #Websocket服务器的开放端口，应当和CQHTTP的逆向WS端口相同
simple_msg: true                 #是否在跨服聊天中隐藏所有CQ码，如聊天回复，图片，红包等
filter:                          #满足其中一条正则表达式的对话将被过滤
  - "!!.*"
  - "/.*"
groups:                          #QQ群列表, 前面是Q群的显示名称，后面是Q群ID
  §9QQ§f: 1145149528
````
Go CQHTTP/config.yml (部分)
````
servers:
  - ws-reverse:
      universal: ws://127.0.0.1:8090             #只需要更改冒号后面的端口，应和配置文件中相通
      api: ws://your_websocket_api.server        #以下忽略，保持不变即可
      event: ws://your_websocket_event.server
      reconnect-interval: 3000
      middlewares:
        <<: *default
````
