package cn.junhaox.espractice.service;

import cn.junhaox.espractice.entity.Content;
import cn.junhaox.espractice.utils.EnumThreadPool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author WJH
 * @Description
 * @date 2020/12/9 16:07
 * @Email ibytecode2020@gmail.com
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaListener implements CommandLineRunner {

    private final ContentService contentService;
    private final ThreadPoolExecutor poolExecutor = EnumThreadPool.THREAD_POOL.getInstance();
    private final ThreadFactory factory = EnumThreadPool.THREAD_POOL.getThreadFactory();

    private void listener() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "bigdata01:9092,bigdata03:9092,bigdata05:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "Www");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("example"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            writeToES(records);
        }
    }

    private void writeToES(ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {
            log.info("kafka中的数据：{}", record);
            String value = record.value();
            JSONObject jsonObject = JSON.parseObject(value, JSONObject.class);
            String type = (String) jsonObject.get("type");
            JSONArray data = jsonObject.getJSONArray("data");
            switch (type) {
                case "UPDATE": {
                    updateToES(data);
                    break;
                }
                case "INSERT": {
                    addToES(data);
                    break;
                }
                case "DELETE": {
                    deleteToES(data);
                    break;
                }
                default: {
                    break;
                }
            }

        }
    }

    private void deleteToES(JSONArray data) {
        System.out.println(data);
    }

    private void addToES(JSONArray data) {
        Content content = null;
        List<Content> contentList = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            content = new Content(jsonObject.getInteger("id"),
                    jsonObject.getString("title"),
                    jsonObject.getString("price"),
                    jsonObject.getString("img"));
            contentList.add(content);
            log.info("content:{}", content);
        }
        Boolean aBoolean = contentService.addContentToES(contentList);
        if (aBoolean) {
            log.info("添加成功");
        } else {
            log.warn("添加失败");
        }
    }

    private void updateToES(JSONArray data) {
        System.out.println(data);
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("启动执行....");
        poolExecutor.execute(factory.newThread(this::listener));
    }
}
