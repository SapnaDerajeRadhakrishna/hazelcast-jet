/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.jet.sql.impl;

import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;

import static com.hazelcast.jet.sql.impl.OptUtils.CONVENTION_LOGICAL;
import static com.hazelcast.jet.sql.impl.OptUtils.CONVENTION_PHYSICAL;

public final class JetTableInsertPhysicalRule extends ConverterRule {
    public static final RelOptRule INSTANCE = new JetTableInsertPhysicalRule();

    private JetTableInsertPhysicalRule() {
        super(JetTableInsertLogicalRel.class, CONVENTION_LOGICAL, CONVENTION_PHYSICAL, JetTableInsertPhysicalRule.class.getSimpleName());
    }

    @Override
    public RelNode convert(RelNode rel) {
        JetTableInsertLogicalRel tableModify = (JetTableInsertLogicalRel) rel;
        if (!tableModify.isInsert()) {
            // we only support INSERT statements
            return null;
        }

        return new JetTableInsertPhysicalRel(
                tableModify.getCluster(),
                OptUtils.toPhysicalConvention(tableModify.getTraitSet()),
                tableModify.getTable(),
                tableModify.getCatalogReader(),
                OptUtils.toPhysicalInput(tableModify.getInput()),
                tableModify.getOperation(),
                tableModify.getUpdateColumnList(),
                tableModify.getSourceExpressionList(),
                tableModify.isFlattened()
        );
    }
}