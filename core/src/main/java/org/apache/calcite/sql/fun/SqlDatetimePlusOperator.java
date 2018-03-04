/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.sql.fun;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.type.InferTypes;
import org.apache.calcite.sql.type.IntervalSqlType;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.ReturnTypes;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlMonotonicity;

/**
 * Operator that adds an INTERVAL to a DATETIME.
 */
public class SqlDatetimePlusOperator extends SqlSpecialOperator {
  //~ Constructors -----------------------------------------------------------

  SqlDatetimePlusOperator() {
    super("+", SqlKind.PLUS, 40, true, ReturnTypes.ARG2_NULLABLE,
        InferTypes.FIRST_KNOWN, OperandTypes.MINUS_DATE_OPERATOR);
  }

  //~ Methods ----------------------------------------------------------------

  @Override public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
    final RelDataTypeFactory typeFactory = opBinding.getTypeFactory();
    final RelDataType leftType = opBinding.getOperandType(0);
    final IntervalSqlType unitType =
        (IntervalSqlType) opBinding.getOperandType(1);
    switch (unitType.getIntervalQualifier().getStartUnit()) {
    case HOUR:
    case MINUTE:
    case SECOND:
    case MILLISECOND:
    case MICROSECOND:
      return typeFactory.createTypeWithNullability(
          typeFactory.createSqlType(SqlTypeName.TIMESTAMP),
          leftType.isNullable() || unitType.isNullable());
    default:
      return leftType;
    }
  }


  public SqlSyntax getSyntax() {
    return SqlSyntax.SPECIAL;
  }

  public void unparse(
      SqlWriter writer,
      SqlCall call,
      int leftPrec,
      int rightPrec) {
    writer.getDialect().unparseSqlDatetimeArithmetic(
        writer, call, SqlKind.PLUS, leftPrec, rightPrec);
  }

  @Override public SqlMonotonicity getMonotonicity(SqlOperatorBinding call) {
    return SqlStdOperatorTable.PLUS.getMonotonicity(call);
  }
}

// End SqlDatetimePlusOperator.java
