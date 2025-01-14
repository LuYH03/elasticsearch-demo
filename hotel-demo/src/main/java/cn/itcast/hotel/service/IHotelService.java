package cn.itcast.hotel.service;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IHotelService extends IService<Hotel> {
    List<String> suggestions(String prefix);

    PageResult search(RequestParams params);

    Map<String, List<String>> filters(RequestParams params);

    void insertById(Long id);

    void deleteById(Long id);
}
