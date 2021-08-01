package ${package.Service};

import com.baomidou.mybatisplus.extension.service.IService;
import ${package.Entity}.${entity};

/**
 * <p>
    * ${table.comment!} 服务类
    * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
@Service
class ${table.serviceName} {

}
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {

}
</#if>
