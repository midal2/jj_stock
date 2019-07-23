package jj.biztrip.websocket.stock;

import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
public class StockHandler {

    @MessageMapping("stockInfo")
    @SendTo("/topic/stockInfo")
    public Message getStockInfo(Map<String, Object> message){
        return null;
    }
}

