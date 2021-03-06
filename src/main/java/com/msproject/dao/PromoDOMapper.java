package com.msproject.dao;

import com.msproject.dataobject.PromoDO;

public interface PromoDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Mar 18 11:08:43 CST 2020
     */
    int deleteByPrimaryKey(Integer promoId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Mar 18 11:08:43 CST 2020
     */
    int insert(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Mar 18 11:08:43 CST 2020
     */
    int insertSelective(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Mar 18 11:08:43 CST 2020
     */
    PromoDO selectByPrimaryKey(Integer promoId);
    PromoDO selectByItemId(Integer itemId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Mar 18 11:08:43 CST 2020
     */
    int updateByPrimaryKeySelective(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Wed Mar 18 11:08:43 CST 2020
     */
    int updateByPrimaryKey(PromoDO record);
}