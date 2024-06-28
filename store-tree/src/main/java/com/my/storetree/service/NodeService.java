package com.my.storetree.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.my.storetree.bean.Node;
import com.my.storetree.mapper.NodeMapper;
import org.springframework.stereotype.Service;

@Service
public class NodeService extends ServiceImpl<NodeMapper, Node> {
}
