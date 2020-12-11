package cn.junhaox.espractice.controller;

import cn.junhaox.espractice.entity.Content;
import cn.junhaox.espractice.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author WJH
 * @Description
 * @date 2020/11/10 15:46
 * @Email ibytecode2020@gmail.com
 */
@RestController
public class ContentController {

    @Autowired
    ContentService contentService;

    @CrossOrigin(origins = "*",maxAge = 3600)
    @GetMapping("/parse/{keyword}")
    public Boolean parseContent(@PathVariable("keyword") String keyword) {
        return contentService.parseContent(keyword);
    }

    @CrossOrigin(origins = "*",maxAge = 3600)
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable("keyword") String keyword,
                                            @PathVariable("pageNo") int pageNo,
                                            @PathVariable("pageSize") int pageSize) {
        return contentService.search(keyword, pageNo, pageSize);
    }


    @CrossOrigin(origins = "*",maxAge = 3600)
    @GetMapping("/getContents")
    public List<Content> getContents() {
        return contentService.getContents();
    }


}
