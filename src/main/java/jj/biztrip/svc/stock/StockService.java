package jj.biztrip.svc.stock;

import jj.biztrip.comm.BizService;
import jj.biztrip.comm.BizServiceType;
import jj.biztrip.comm.DateUtil;
import jj.biztrip.websocket.stock.StockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("stock/")
public class StockService {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

//    @Autowired
//    private StockDAO stockDAO;

    @Autowired
    private BizService<Map<String, Object>> bizService;

    @Autowired
    private StockHandler stockHandler;

    @Autowired
    private SimpMessagingTemplate template;

    @RequestMapping("/getAllInfo")
    public List<Map<String, Object>> getAllInfo() {

        Map<String, Object> resultMap =  bizService.send("http://asp1.krx.co.kr/servlet/krx.asp.XMLSise?code=053580","", BizServiceType.XML, "");
        System.out.println("resultMap:" + resultMap);

        List<Map<String, Object>> stockInfos = new LinkedList<>();
        stockInfos.add(resultMap);



//        stockInfos = stockDAO.getAllStockInfo();

        return stockInfos;
    }

    @RequestMapping("/getStockList")
    public List<Map<String, Object>> getStockList(){
        List<Map<String, Object>> stockList = new LinkedList<Map<String, Object>>();
        /**
         * 067160 아프리카
         * 053580 웹케시
         *     title: '', //주식명
         *     stockCd: '', //주식코드
         *     nowPrice: '', //현재가
         *     time: nowTime(), //현재시간
         *     differAmt: '', //등락폭
         */

        stockList.add(createMap("웹케시", "053580", 0, 0));
        stockList.add(createMap("아프리카", "067160", 0, 0));

        template.convertAndSend("/topic/stockInfo", "메롱");

        return stockList;
    }

    /**
     * 주식정보를 생성한다
     *
     * @param stockNm
     * @param stockCd
     * @param nowPrice
     * @param diffAmt
     * @return
     */
    private Map<String, Object> createMap(String stockNm, String stockCd, int nowPrice, int diffAmt) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("title", stockNm);
        resultMap.put("stockCd", stockCd);
        resultMap.put("nowPrice", nowPrice);
        resultMap.put("time", DateUtil.getDateStr("HHmmss"));
        resultMap.put("differAmt", stockNm);

        return resultMap;
    }
}
