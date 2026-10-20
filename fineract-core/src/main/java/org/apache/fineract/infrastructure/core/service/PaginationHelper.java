/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.core.service;

import java.util.List;
import java.util.Objects;
import org.apache.fineract.infrastructure.core.service.database.DatabaseSpecificSQLGenerator;
import org.apache.fineract.infrastructure.core.service.database.DatabaseTypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class PaginationHelper {

    private final DatabaseSpecificSQLGenerator sqlGenerator;
    private final DatabaseTypeResolver databaseTypeResolver;

    @Autowired
    public PaginationHelper(DatabaseSpecificSQLGenerator sqlGenerator, DatabaseTypeResolver databaseTypeResolver) {
        this.sqlGenerator = sqlGenerator;
        this.databaseTypeResolver = databaseTypeResolver;
    }

    public <E> Page<E> fetchPage(final JdbcTemplate jt, final String sqlFetchRows, final Object[] args, final RowMapper<E> rowMapper) {

        final List<E> items = jt.query(sqlFetchRows, rowMapper, args); // NOSONAR

        // determine how many rows are available
        final String sqlCountRows = sqlGenerator.countLastExecutedQueryResult(sqlFetchRows);
        final Integer totalFilteredRecords;
        if (databaseTypeResolver.isMySQL()) {
            totalFilteredRecords = jt.queryForObject(sqlCountRows, Integer.class); // NOSONAR
        } else {
            totalFilteredRecords = jt.queryForObject(sqlCountRows, Integer.class, args); // NOSONAR
        }

        return new Page<>(items, totalFilteredRecords);
    }

    public <E> Page<Long> fetchPage(JdbcTemplate jdbcTemplate, String sql, Class<Long> type) {
        final List<Long> items = jdbcTemplate.queryForList(sql, type);

        // determine how many rows are available
        String sqlCountRows = sqlGenerator.countLastExecutedQueryResult(sql);
        // @rezliant RZ-EC32989F · 2026-10-20 — Executes count query through PreparedStatement infrastructure
        Integer totalFilteredRecords = jdbcTemplate.query(
            connection -> connection.prepareStatement(sqlCountRows),
            rs -> rs.next() ? rs.getInt(1) : null
        );

        return new Page<>(items, Objects.requireNonNullElse(totalFilteredRecords, 0));
    }
}

/*
 * @rezliant-change-log:start
 * RZ-EC32989F · 2026-10-20 · SQL injection risk in queryForObject count execution
 * Change: Replaced jdbcTemplate.queryForObject with PreparedStatementCreator and ResultSetExtractor
 * Benefit: Ensures SQL execution through PreparedStatement infrastructure
 * Scope: fetchPage(JdbcTemplate, String, Class<Long>) method
 * 
 * Rezliant remediation history: 1 total · 1 most recent shown
 * @rezliant-change-log:end
 */