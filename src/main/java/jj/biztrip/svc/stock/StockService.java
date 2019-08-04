package jj.biztrip.svc.stock;

import jj.biztrip.comm.BizService;
import jj.biztrip.comm.BizServiceType;
import jj.biztrip.comm.DateUtil;
import jj.biztrip.websocket.stock.StockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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

    private String stockCd; //현재 설정된 주식코드

    private Map<String, Map<String, Object>> stockMap = new HashMap<>();

    private synchronized void addStock(String stockCd, Map<String, Object> stockInfo){
        stockMap.put(stockCd, stockInfo);
    }

    @RequestMapping("/getAllInfo")
    public List<Map<String, Object>> getAllInfo() {

        Map<String, Object> resultMap =  bizService.send("http://asp1.krx.co.kr/servlet/krx.asp.XMLSise?code=053580","", BizServiceType.XML, "");
        System.out.println("resultMap:" + resultMap);

        List<Map<String, Object>> stockInfos = new LinkedList<>();
        stockInfos.add(resultMap);



//        stockInfos = stockDAO.getAllStockInfo();

        return stockInfos;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 60000)
    public void batchStockList(){
        logger.debug("batchStockList executed");
        addStock("053580", getNowStockInfo("053580", "웹케시"));
        addStock("067160", getNowStockInfo("067160", "아프리카"));
    }

    private Map<String, Object> getNowStockInfo(String stockCd, String stockNm) {
        Map<String, Object> resultMap =  bizService.send("http://asp1.krx.co.kr/servlet/krx.asp.XMLSise?code=" + stockCd,"", BizServiceType.XML, "");
        resultMap.put("stockCd", stockCd);
        return resultMap;
    }

    @RequestMapping("/getStockList")
    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public List<Map<String, Object>> getStockList(){
        /**
         * 067160 아프리카
         * 053580 웹케시
         *     title: '', //주식명
         *     stockCd: '', //주식코드
         *     nowPrice: '', //현재가
         *     time: nowTime(), //현재시간
         *     differAmt: '', //등락폭
         */

        if (stockMap.isEmpty()) {
            return null; //내용이 없을경우 종료
        }


        List<Map<String, Object>> stockList = new LinkedList<>();
        Iterator<String> iter =  stockMap.keySet().iterator();
        while(iter.hasNext()){
            String strKey = iter.next();
            Map<String, Object> selectedMap = stockMap.get(strKey);
            Map<String, Object> tBLStockInfoMap = (Map<String, Object>)selectedMap.get("TBL_StockInfo");
            List<Map<String, Object>> tBLTimeConcludeList = (List<Map<String, Object>>)((Map<String, Object>)selectedMap.get("TBL_TimeConclude")).get("TBL_TimeConclude");

            tBLStockInfoMap.put("title", tBLStockInfoMap.get("JongName"));
            tBLStockInfoMap.put("stockCd", strKey);
            tBLStockInfoMap.put("nowPrice", tBLStockInfoMap.get("CurJuka"));
            tBLStockInfoMap.put("time", DateUtil.getDateStr("HHmmss"));
            tBLStockInfoMap.put("differAmt", tBLStockInfoMap.get("Debi"));

            stockList.add(tBLStockInfoMap);
        }
        template.convertAndSend("/topic/stockInfo", stockList);

        return stockList;
    }

    @RequestMapping("/getStockDetail")
    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public List<Map<String, Object>> getStockDetail(){
        /**
         * 067160 아프리카
         * 053580 웹케시
         *     title: '', //주식명
         *     stockCd: '', //주식코드
         *     nowPrice: '', //현재가
         *     time: nowTime(), //현재시간
         *     differAmt: '', //등락폭
         */

        if (stockMap.isEmpty()) {
            return null; //내용이 없을경우 종료
        }


        List<Map<String, Object>> stockList = new LinkedList<>();
        Iterator<String> iter =  stockMap.keySet().iterator();
        while(iter.hasNext()){
            Map<String, Object> selectedMap = stockMap.get(iter.next());
            Map<String, Object> tBLStockInfoMap = (Map<String, Object>)selectedMap.get("TBL_StockInfo");
            List<Map<String, Object>> tBLTimeConcludeList = (List<Map<String, Object>>)((Map<String, Object>)selectedMap.get("TBL_TimeConclude")).get("TBL_TimeConclude");

            tBLStockInfoMap.put("title", tBLStockInfoMap.get("JongName"));
            tBLStockInfoMap.put("stockCd", tBLStockInfoMap.get("stockCd"));
            tBLStockInfoMap.put("nowPrice", tBLStockInfoMap.get("CurJuka"));
            tBLStockInfoMap.put("time", DateUtil.getDateStr("HHmmss"));
            tBLStockInfoMap.put("differAmt", tBLStockInfoMap.get("Debi"));

            stockList.add(tBLStockInfoMap);
        }
        template.convertAndSend("/topic/stockInfo", stockList);

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

    /**
     * 현재 보고있는 주식상세정보를 설정한다
     * @param stockCd
     */
    public void setStockDetail(String stockCd) {
        this.stockCd = stockCd;
    }


    /**
     * 주식상세내역을 생성한다
     * @return
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void broadCastStockDetail(){

        System.out.println("broadCastStockDetail:" + stockCd);
        if ("".equals(stockCd)) {
            return; //내용이 없을경우 종료
        }


        if (!stockMap.keySet().contains(stockCd)){
            return;
        }

        System.out.println("broadCastStockDetail found:" + stockCd);
        Map<String, Object> stockInfoMap = stockMap.get(stockCd);
        Map<String, Object> tBLTimeConcludeMap = (Map<String, Object>) stockInfoMap.get("TBL_TimeConclude");
        List<Map<String, Object>> tBLTimeConcludeList = (List<Map<String, Object>>) tBLTimeConcludeMap.get("TBL_TimeConclude");


        List<Map<String, Object>> stockList = new LinkedList<>();
        int i=0;
        for (Map<String, Object> selectedMap : tBLTimeConcludeList) {
            selectedMap.put("title",++i);
            selectedMap.put("nowPrice",selectedMap.get("negoprice"));
            selectedMap.put("time",selectedMap.get("time"));
            selectedMap.put("differAmt",selectedMap.get("Dungrak"));
            stockList.add(selectedMap);
        }

        template.convertAndSend("/topic/stockDetail", stockList);

    }
}
