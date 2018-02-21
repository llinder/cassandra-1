/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cassandra.index.sasi.analyzer;

import java.util.Map;

/** Simple tokenizer based on a specified delimiter (rather than whitespace).
 */
public class DelimiterTokenizingOptions
{
    public static final String DELIMITER = "delimiter";
    public static final String CASE_SENSITIVE = "case_sensitive";

    private final String delimiter;
    private final boolean caseSensitive;

    private DelimiterTokenizingOptions(String delimiter, boolean caseSensitive)
    {
        this.delimiter = delimiter;
        this.caseSensitive = caseSensitive;
    }

    String getDelimiter()
    {
        return delimiter;
    }

    boolean isCaseSensitive()
    {
        return caseSensitive;
    }

    private static class OptionsBuilder
    {
        private String delimiter = "░";
        private boolean caseSensitive = true;

        public DelimiterTokenizingOptions build()
        {
            return new DelimiterTokenizingOptions(delimiter, caseSensitive);
        }
    }

    static DelimiterTokenizingOptions buildFromMap(Map<String, String> optionsMap)
    {
        OptionsBuilder optionsBuilder = new OptionsBuilder();

        for (Map.Entry<String, String> entry : optionsMap.entrySet())
        {
            switch (entry.getKey())
            {
                case DELIMITER:
                {
                    optionsBuilder.delimiter = entry.getValue();
                    break;
                }
                case CASE_SENSITIVE:
                {
                    optionsBuilder.caseSensitive = Boolean.parseBoolean(entry.getValue());
                    break;
                }
            }
        }
        return optionsBuilder.build();
    }
}
