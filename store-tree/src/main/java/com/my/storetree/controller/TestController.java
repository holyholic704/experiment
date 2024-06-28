package com.my.storetree.controller;

import com.my.storetree.bean.Node;
import com.my.storetree.service.ClosureTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private ClosureTableService closureTableService;

    @PostMapping("/closure/insert")
    public Boolean closureInsert(@RequestBody Node node) {
        return closureTableService.closureInsert(node);
    }

    @GetMapping("/closure/getTree")
    public List<Node> closureGetTree(@RequestParam Long nodeId) {
        return closureTableService.getTree(nodeId);
    }

    @GetMapping("/closure/getTreeAll")
    public List<Node> closureGetTree() {
        return closureTableService.getTree();
    }
}
