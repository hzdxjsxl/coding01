package com.example.blankingmanage.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页响应数据")
public class PageResponse<T> {
    @Schema(description = "当前页数据列表")
    private List<T> list;

    @Schema(description = "总条数", example = "100")
    private long total;

    @Schema(description = "总页数", example = "10")
    private long pages;

    @Schema(description = "当前页码", example = "1")
    private long current;

    @Schema(description = "每页大小", example = "10")
    private long size;

    // ⚡ 战术核心：提供一个静态方法，把 MP 的 Page 对象直接转成我们的 PageResponse
    // 这样 Service 层代码会极其简洁
    public static <T> PageResponse<T> of(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setList(page.getRecords());
        response.setTotal(page.getTotal());
        response.setPages(page.getPages());
        response.setCurrent(page.getCurrent());
        response.setSize(page.getSize());
        return response;
    }
}