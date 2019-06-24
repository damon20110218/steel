package cn.four.steel.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.four.steel.bean.from.FrontOut;
import cn.four.steel.bean.to.MainOut;
import cn.four.steel.bean.to.SingleOut;
import cn.four.steel.service.SteelOutService;

@RestController
public class SteelOutController {
	private static Logger logger = LoggerFactory.getLogger(SteelOutController.class);
	@Autowired
	private SteelOutService steelOutService;

	@RequestMapping(value = "/out/update", method = RequestMethod.POST)
	public String orderUpdate(@RequestBody JSONObject jsonParam, HttpServletRequest request) {
		try {
			JSONArray array = jsonParam.getJSONArray("out");
			if (array != null && array.size() != 0) {
				List<FrontOut> outs = new ArrayList<>();
				for (int i = 0; i < array.size(); i++) {
					FrontOut out = array.getJSONObject(i).toJavaObject(FrontOut.class);
					outs.add(out);
				}
				steelOutService.updateOut(outs);
			}
			return "Success";
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "failed";
		}
	}

	@RequestMapping(value = "/out/query", method = RequestMethod.POST)
	public List<MainOut> queryOut(String saleNo, String year, String month) {
		try {
			return steelOutService.queryOut(saleNo, year, month);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = "/out/show", method = RequestMethod.POST)
	public List<SingleOut> showSingleOut(String outId) {
		try {
			return steelOutService.showSingleOut(Long.valueOf(outId));
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = "/out/load", method = RequestMethod.POST)
	public List<SingleOut> loadOutBySaleNo(@RequestBody JSONObject jsonParam) {
		try {
			JSONArray array = jsonParam.getJSONArray("orderNos");
			List<String> saleNos = new ArrayList<>();
			if (array != null && array.size() != 0) {
				for (int i = 0; i < array.size(); i++) {
					saleNos.add(array.getJSONObject(i).toJavaObject(String.class));
				}
			}
			return steelOutService.loadOutByOrderNo(saleNos);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
