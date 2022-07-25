package com.fanzibang.community.controller;

import com.fanzibang.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Validated
@RestController
@RequestMapping("/data")
public class DataController {

    @Autowired
    private DataService dataService;

    @GetMapping("/uv")
    public Long getUV(@Valid @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
                      @Valid @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        return dataService.getUV(startTime, endTime);
    }

    @GetMapping("/dau")
    public Long getDAU(@Valid @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
                       @Valid @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        return dataService.getDAU(startTime, endTime);
    }
}
