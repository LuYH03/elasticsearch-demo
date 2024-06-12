package cn.itcast.hotel.controller;

import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: YHan
 * @Date: 2024/6/3 02:27
 * @Description:
 **/
@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private IHotelService iHotelService;

    @PostMapping("/list")
    public PageResult search(@RequestBody RequestParams params) {
        return iHotelService.search(params);
    }

    @GetMapping("/filters")
    public Map<String, List<String>> getFilters(@RequestBody RequestParams params) {
        return iHotelService.filters(params);
    }

    @GetMapping("/suggestion")
    public List<String> suggestions(@RequestParam("key") String prefix) {
        return iHotelService.suggestions(prefix);
    }

}
