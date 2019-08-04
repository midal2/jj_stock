package jj.biztrip.websocket.stock;

import jj.biztrip.svc.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StockHandler {

    @Autowired
    StockService stockService;

    @MessageMapping("stocktest")
    @SendTo("/topic/stockInfo")
    public Message getStockInfo(){
        System.out.println("test!!#################!!!!!!!");

        return new Message() {
            @Override
            public Object getPayload() {
                return "payLoad";
            }

            @Override
            public MessageHeaders getHeaders() {
                Map<String, Object> map = new HashMap<>();
                map.put("test", "값!!!");

                return new MessageHeaders(map);
            }
        };
    }

    @MessageMapping("setStockDetail")
    public void setStockDetail(String stockCd){
        System.out.println("setStockDetail########!!!!!!![" + stockCd);

        stockService.setStockDetail(stockCd);
    }

}

