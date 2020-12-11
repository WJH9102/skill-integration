package cn.junhaox.espractice;

import cn.junhaox.espractice.entity.Content;
import cn.junhaox.espractice.entity.User;
import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class EsPracticeApplicationTests {

	private static final String INDEX = "java_www_index";

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Test
	void contextLoads() {
	}

	@Test
	void createIndex() throws IOException {
		CreateIndexRequest request = new CreateIndexRequest(INDEX);
		CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
		System.out.println(response);
	}

	@Test
	void existsIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest("java_www_index");
		boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println("exists = " + exists);
	}

	@Test
	void deleteIndex() throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("java_www_index");
		AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
		System.out.println("exists = " + response.isAcknowledged());
	}

	@Test
	void addDocument() throws Exception {
		User user = new User("张三", 25);
		IndexRequest request = new IndexRequest(INDEX);
		request.id("1");
		request.timeout(TimeValue.timeValueSeconds(1));
		String userJson = JSON.toJSONString(user);
		request.source(userJson, XContentType.JSON);

		IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
		System.out.println(response.toString());
		System.out.println(response.status());

	}

	@Test
	void addDocument1() throws Exception {
		Content content = new Content(1, "java编程思想", "100￥", "https://cdn.yuque.com/20201922932439.img");
		IndexRequest request = new IndexRequest("jd_goods");
		request.timeout(TimeValue.timeValueSeconds(1));
		String userJson = JSON.toJSONString(content);
		request.source(userJson, XContentType.JSON);
		IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
		System.out.println(response.toString());
		System.out.println(response.status());

	}

}
