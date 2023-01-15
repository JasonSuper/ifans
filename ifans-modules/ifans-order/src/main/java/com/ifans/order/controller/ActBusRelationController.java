package com.ifans.order.controller;

import com.ifans.common.core.util.R;
import com.ifans.order.service.ActBusRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务关联操作
 */
@RestController
@RequestMapping("/actbusrelation")
public class ActBusRelationController {

    @Autowired
    private ActBusRelationService actBusRelationService;

    @PutMapping("/updateBusBpmStatus")
    public R updateBusBpmStatus(@RequestParam("processInstanceId") String processInstanceId, @RequestParam("int bpmStatus") int bpmStatus, @RequestParam("businessKey") String businessKey) {
        int res = actBusRelationService.updateBusBpmStatus(processInstanceId, bpmStatus, businessKey);
        if (res > 0) {
            return R.ok();
        }
        return R.failed();
    }
}
