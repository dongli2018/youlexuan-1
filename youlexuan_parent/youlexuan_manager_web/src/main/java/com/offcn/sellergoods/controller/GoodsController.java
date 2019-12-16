package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbItem;
import com.offcn.sellergoods.service.GoodsService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * goodscontroller
 * @author senqi
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;



	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private ActiveMQQueue activeMQQueue;

	@Autowired
	private ActiveMQTopic activeMQTopic;

	@RequestMapping("getHtml")
	public void getHtml(Long id){
//		itemPageService.genItemHtml(id);
	}
	/**
	 * 返回全部列表
	 * @return
	 */
//	@RequestMapping("/findAll")
//	public List<TbGoods> findAll(){
//		return goodsService.findAll();
//
//	}
//
//
//	/**
//	 * 返回全部列表
//	 * @return
//	 */
//	@RequestMapping("/findPage")
//	public PageResult findPage(int page,int rows){
//		return goodsService.findPage(page, rows);
//	}
//
//	/**
//	 * 增加
//	 * @param goods
//	 * @return
//	 */
//	@RequestMapping("/add")
//	public Result add(@RequestBody TbGoods goods){
//		try {
//			goodsService.add(goods);
//			return new Result(true, "增加成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new Result(false, "增加失败");
//		}
//	}
//
//	/**
//	 * 修改
//	 * @param goods
//	 * @return
//	 */
//	@RequestMapping("/update")
//	public Result update(@RequestBody TbGoods goods){
//		try {
//			goodsService.update(goods);
//			return new Result(true, "修改成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new Result(false, "修改失败");
//		}
//	}
//
//	/**
//	 * 获取实体
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping("/findOne")
//	public TbGoods findOne(Long id){
//		return goodsService.findOne(id);
//	}
//
//	/**
//	 * 批量删除
//	 * @param ids
//	 * @return
//	 */
//	@RequestMapping("/delete")
//	public Result delete(Long [] ids){
//		try {
//			goodsService.delete(ids);
//			return new Result(true, "删除成功");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new Result(false, "删除失败");
//		}
//	}
//
	/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int size){
		return goodsService.findPage(goods, page, size);
	}
	//更改状态
	@RequestMapping("/uploadStatus")
	public Result uploadStatus(Long[] ids,String status){
		try {
			goodsService.uploadStatus(ids,status);
			//添加商品到sort
			if("1".equals(status)){
                List<TbItem> list = goodsService.findItemById(ids);
//                itemSearchService.importDate(list);
				//用消息中间件传递
				//list较为复杂,转为json
				String listStr = JSON.toJSONString(list);
				jmsTemplate.convertAndSend(activeMQQueue,listStr);
			}

			//静态页生成
			jmsTemplate.convertAndSend(activeMQTopic,ids);
//			for(Long id:ids){
//				itemPageService.genItemHtml(id);
//			}


			return new Result(true,"操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"操作失败");
		}
	}
}
