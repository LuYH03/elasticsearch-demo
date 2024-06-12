package cn.itcast.hotel;

import cn.itcast.hotel.service.IHotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * @Author: YHan
 * @Date: 2024/6/4 12:39
 * @Description:
 **/
@SpringBootTest
public class HotelApplicationTest {

    @Autowired
    private IHotelService hotelService;
 /*   @Test
    void filters_HotelService() {
        Map<String, List<String>> map = hotelService.filters();
        System.out.println(map);
    }*/

}
