package com.msproject.service;

import com.msproject.error.BusinessException;
import com.msproject.service.model.ItemModel;

import java.util.List;

public interface ItemService {
    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    //减库存
    Boolean decreaseItemStock(Integer itemId, Integer amount);

    //增加销量
    void increaseSales(Integer itemId, Integer amount);
}
