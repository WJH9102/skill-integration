package cn.junhaox.espractice.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author WJH
 * @Description
 * @date 2020/11/10 15:26
 * @Email ibytecode2020@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("content")
public class Content {
    @JSONField(serialize = false)
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("title")
    private String title;
    @TableField("price")
    private String price;
    @TableField("img")
    private String img;
}
