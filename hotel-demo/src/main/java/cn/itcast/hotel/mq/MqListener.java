package cn.itcast.hotel.mq;

import cn.itcast.hotel.constant.MqConstant;
import cn.itcast.hotel.service.IHotelService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: YHan
 * @Date: 2024/6/5 05:31
 * @Description:
 **/
@Component
public class MqListener {

    @Autowired
    private IHotelService iHotelService;
    /**
     * 监听酒店新增或修改的业务
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(MqConstant.HOTEL_INSERT_QUEUE),
            exchange = @Exchange(MqConstant.HOTEL_EXCHANGE),
            key = {MqConstant.HOTEL_INSERT_KEY}
    ))
    public void listenHotelInsertOrUpdate(Long id) {
        iHotelService.insertById(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(MqConstant.HOTEL_DELETE_QUEUE),
            exchange = @Exchange(MqConstant.HOTEL_EXCHANGE),
            key = {MqConstant.HOTEL_DELETE_KEY}
    ))
    public void listenHotelDelete(Long id) {
        iHotelService.deleteById(id);
    }
}
