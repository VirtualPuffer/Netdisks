package ${package.Entity};

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import org.apache.ibatis.type.JdbcType;
import java.util.Date;
<#list table.importPackages as pkg>
import ${pkg};
</#list>
<#--<#if entityLombokModel>-->
<#--import lombok.Data;-->
<#--import lombok.EqualsAndHashCode;-->
<#--import lombok.experimental.Accessors;-->
<#--</#if>-->

/**
 * <p>
    * ${table.comment!}
    * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if entityLombokModel>
<#--@Data-->
<#--<#if superEntityClass??>-->
<#--@EqualsAndHashCode(callSuper = true)-->
<#--<#else>-->
<#--@EqualsAndHashCode(callSuper = false)-->
<#--</#if>-->
<#--@Accessors(chain = true)-->
</#if>
<#if table.convert>
@TableName("${table.name}")
</#if>
<#if superEntityClass??>
public class ${entity} extends ${superEntityClass}<#if activeRecord><${entity}></#if> {
<#elseif activeRecord>
public class ${entity} extends Model<${entity}> {
<#else>
public class ${entity} implements Serializable {
</#if>


    private static final long serialVersionUID = 1L;

<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
    </#if>

    <#if field.comment!?length gt 0>
    /**
     *${field.comment}
    */
    </#if>

    <#if field.keyFlag>
    <#-- 主键 -->
        <#if field.keyIdentityFlag>
    @TableId(value = "${field.name}", type = IdType.AUTO)
        <#elseif idType??>
    @TableId(value = "${field.name}", type = IdType.${idType})
        <#elseif field.convert>
    @TableId("${field.name}")
        </#if>
    <#-- 普通字段 -->
    <#elseif field.fill??>
    <#-- -----   存在字段填充设置   ----->
        <#if field.convert>
    @TableField(value = "${field.name}")
        <#else>
    @TableField(fill = FieldFill.${field.fill})
        </#if>
    <#elseif field.convert>
        <#if field.type == "NVARCHAR2">
    @TableField(value = "${field.name}",updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.NVARCHAR)
        <#elseif field.type == "NUMBER">
    @TableField(value = "${field.name}",updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.NUMERIC)
        <#else>
    @TableField(value = "${field.name}",updateStrategy = FieldStrategy.IGNORED,jdbcType = JdbcType.${field.type})
        </#if>
    </#if>

<#-- 乐观锁注解 -->
    <#if (versionFieldName!"") == field.name>
    @Version
    </#if>
<#-- 逻辑删除注解 -->
    <#if (logicDeleteFieldName!"") == field.name>
    @TableLogic
    </#if>
    <#if field.propertyType == "LocalDateTime">
    private Date ${field.propertyName};
    <#else>
    private ${field.propertyType} ${field.propertyName};
    </#if>
</#list>
<#------------  END 字段循环遍历  ---------->

<#--<#if !entityLombokModel>-->
<#list table.fields as field>
    <#if field.propertyType == "boolean">
        <#assign getprefix="is"/>
    <#else>
        <#assign getprefix="get"/>
    </#if>
    <#if field.propertyType == "LocalDateTime">
    public Date ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
    }
    <#else>
    public ${field.propertyType} ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
    }
    </#if>
    <#if entityBuilderModel>
    public ${entity} set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
    <#else>
        <#if field.propertyType == "LocalDateTime">
    public void set${field.capitalName}(Date ${field.propertyName}) {
        <#else>
    public void set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
        </#if>
    </#if>
        this.${field.propertyName} = ${field.propertyName};
    <#if entityBuilderModel>
        return this;
    </#if>
    }

</#list>

<#if activeRecord>
    @Override
    protected Serializable pkVal() {
    <#if keyPropertyName??>
        return this.${keyPropertyName};
    <#else>
        return null;
    </#if>
    }

</#if>
<#--<#if !entityLombokModel>-->
    @Override
    public String toString() {
        return "${entity}{" +
<#list table.fields as field>
    <#if field_index==0>
            "${field.name}=" + ${field.propertyName} +
    <#else>
            ", ${field.name}=" + ${field.propertyName} +
    </#if>
</#list>
        "}";
    }
<#--</#if>-->
}