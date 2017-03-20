import com.bjike.goddess.common.api.exception.SerException;
import com.bjike.goddess.redis.client.RedisClient;
import message_code.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * @Author: [liguiqin]
 * @Date: [2017-03-17 11:13]
 * @Description: [ ]
 * @Version: [1.0.0]
 * @Copy: [com.bjike]
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class RedisTest {
    @Autowired
    private RedisClient redisClinet;

    @Test
    public void save() throws SerException {
        redisClinet.save("name", "liguiqin");
        System.out.println(redisClinet.exists("name"));
        redisClinet.append("name", "123");
    }

    @Test
    public void get() throws SerException {
        System.out.println(redisClinet.get("name"));
    }

    @Test
    public void listTest() throws SerException {
        List<String> emails = new ArrayList<>(5);
        for (int i = 1; i <= 5; i++) {
            emails.add("aa" + i + "@qq.com");
        }
        redisClinet.saveList("emails", emails);
        System.out.println(redisClinet.getList("emails", 2, 4));
        System.out.println(redisClinet.getList("emails"));
        redisClinet.remove("emails");
        System.out.println(redisClinet.getList("emails"));
    }


    @Test
    public void mapList() throws SerException {
        Map<String, String> map = new HashMap<>(5);
        for (int i = 1; i <= 5; i++) {
            map.put("count" + i, "li" + i + "name");
        }
        redisClinet.saveMap("username", map);
        System.out.println(redisClinet.getMap("username", "count2", "count3"));
        System.out.println(redisClinet.mapKeys("username"));
        System.out.println(redisClinet.mapValues("username"));
        System.out.println(redisClinet.mapLength("username"));
        redisClinet.removeMap("username", "count2");
        System.out.println(redisClinet.getMap("username", "count2", "count3"));

    }

    @Test
    public void setList() throws SerException {
        Set<String> phones = new HashSet<>(5);
        for (int i = 1; i <= 5; i++) {
            phones.add("1345791024" + i);
        }
        redisClinet.saveSet("phones", phones);
        System.out.println(redisClinet.getSet("phones"));
        System.out.println(redisClinet.setLength("phones"));
        redisClinet.removeSet("phones", "13457910241");
        System.out.println(redisClinet.getSet("phones"));

    }

}