package com.my.storetree.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.my.storetree.bean.Node;
import com.my.storetree.bean.NodeRelation;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface NodeRelationMapper extends BaseMapper<NodeRelation> {

    @Select("SELECT A.descendant id, B.`name` FROM node_relation A LEFT JOIN node B ON A.ancestor = B.id WHERE ancestor = #{ancestor} AND distance = 0")
    @Results({
            @Result(property = "id", column = "id", id = true),
            @Result(property = "children", column = "id", javaType = List.class, many = @Many(select = "com.my.storetree.mapper.NodeRelationMapper.getChildren"))
    })
    List<Node> getTree(Long ancestor);

    @Select("SELECT A.descendant id, B.`name` FROM node_relation A LEFT JOIN node B ON A.descendant = B.id WHERE ancestor = #{ancestor} AND distance = 1")
    @Results({
            @Result(property = "id", column = "id", id = true),
            @Result(property = "children", column = "id", javaType = List.class, many = @Many(select = "com.my.storetree.mapper.NodeRelationMapper.getChildren"))
    })
    List<Node> getChildren(Long ancestor);
}
