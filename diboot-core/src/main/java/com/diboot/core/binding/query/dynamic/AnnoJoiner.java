/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.core.binding.query.dynamic;

import com.baomidou.mybatisplus.annotation.TableField;
import com.diboot.core.binding.parser.ParserCache;
import com.diboot.core.binding.query.BindQuery;
import com.diboot.core.binding.query.Comparison;
import com.diboot.core.util.S;
import com.diboot.core.util.V;
import lombok.Getter;
import lombok.Setter;

import javax.lang.model.type.NullType;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * BindQuery注解连接器
 * @author Mazc@dibo.ltd
 * @version v2.0
 * @date 2020/04/16
 */
@Getter @Setter
public class AnnoJoiner implements Serializable {
    private static final long serialVersionUID = 5998965277333389063L;

    public AnnoJoiner(Field field, BindQuery query){
        this.fieldName = field.getName();
        this.comparison = query.comparison();
        // 列名
        if (V.notEmpty(query.field())) {
            this.columnName = S.toSnakeCase(query.field());
        }
        else if (field.isAnnotationPresent(TableField.class)) {
            this.columnName = field.getAnnotation(TableField.class).value();
        }
        if(V.isEmpty(this.columnName)){
            this.columnName = S.toSnakeCase(field.getName());
        }
        // join 表名
        if(!NullType.class.equals(query.entity())){
            this.join = ParserCache.getEntityTableName(query.entity());
        }
        // 条件
        if(V.notEmpty(query.condition())){
            this.condition = query.condition();
        }
    }

    private Comparison comparison;

    private String fieldName;

    private String columnName;

    private String join;

    /**
     * 别名
     */
    private String alias;

    private String condition;

    /**
     * 获取On条件
     * @return
     */
    public String getOnSegment(){
        if(V.notEmpty(condition)){
            return JoinConditionParser.parseJoinCondition(this.condition, this.alias);
        }
        return null;
    }
}
