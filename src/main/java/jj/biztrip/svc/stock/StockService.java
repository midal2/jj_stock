package jj.biztrip.svc.stock;

import jj.biztrip.batch.krx.model.StockInfo;
import jj.biztrip.comm.BizService;
import jj.biztrip.comm.BizServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/getAllInfo")
    public List<Map<String, Object>> getAllInfo() {

        Map<String, Object> resultMap =  bizService.send("http://asp1.krx.co.kr/servlet/krx.asp.XMLSise?code=053580","", BizServiceType.XML, "");
        System.out.println("resultMap:" + resultMap);

        List<Map<String, Object>> stockInfos = new LinkedList<>();
        stockInfos.add(resultMap);



//        stockInfos = stockDAO.getAllStockInfo();

        return stockInfos;
    }
}
