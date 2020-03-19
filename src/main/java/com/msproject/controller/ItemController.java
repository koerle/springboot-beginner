package com.msproject.controller;

import com.msproject.controller.viewoj.ItemVO;
import com.msproject.error.BusinessException;
import com.msproject.error.EmBusinessError;
import com.msproject.response.CommonReturnType;
import com.msproject.service.ItemService;
import com.msproject.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("item")
@RequestMapping("/item")
public class ItemController extends BaseController {
    @Autowired
    private  ItemService itemService;


    @RequestMapping("/create")
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
        System.out.println(title);
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        ItemModel returnItemModel = itemService.createItem(itemModel);
        ItemVO itemVO = convertItemVOFromItemModel(returnItemModel);

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name="id") Integer id) throws BusinessException {
        try {
            ItemModel itemModel = itemService.getItemById(id);
            ItemVO itemVO = convertItemVOFromItemModel(itemModel);
            return CommonReturnType.create(itemVO);
        }catch (Exception e){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"商品不存在");
        }
    }

    @RequestMapping("/list")
    @ResponseBody
    public CommonReturnType getList(){
        List<ItemModel> itemModelList = itemService.listItem();
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }

    private ItemVO convertItemVOFromItemModel(ItemModel returnItemModel) {
        if(returnItemModel==null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(returnItemModel,itemVO);
        if(returnItemModel.getPromoModel()!=null) {
            itemVO.setPromoId(returnItemModel.getPromoModel().getPromoId());
            itemVO.setPromoPrice(returnItemModel.getPromoModel().getPromoPrice());
            itemVO.setPromoStatus(returnItemModel.getPromoModel().getStatus());
            itemVO.setStartTime(returnItemModel.getPromoModel().getStartTime().toString());
        }else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }
}
