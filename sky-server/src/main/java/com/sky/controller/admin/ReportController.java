package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


/**
 * data statistics
 */
@Api(tags = "report relevant apis")
@Slf4j
@RestController
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @ApiOperation("turnoverStatistics")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end) {
        log.info("turnoverStatistics begin:{}, end:{}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }

    @ApiOperation("userStatistics")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end) {
        log.info("userStatistics begin:{}, end:{}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    @ApiOperation("ordersStatistics")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end) {
        log.info("orderStatistics begin:{}, end:{}", begin, end);
        return Result.success(reportService.getOrderStatistics(begin, end));
    }
}
