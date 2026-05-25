package ${package.Service};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
* <p>
    * ${table.comment!} 服务类 (无接口版)
    * </p>
*
* @author ${author}
* @since ${date}
*/
@Slf4j
@Service
public class ${table.serviceName} extends ServiceImpl<${table.mapperName}, ${entity}> {
// 这里可以直接写你的业务逻辑，已经自动注入了 baseMapper
}