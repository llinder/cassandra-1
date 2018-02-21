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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Strings;

import org.apache.cassandra.db.marshal.AbstractType;
import org.apache.cassandra.db.marshal.AsciiType;
import org.apache.cassandra.db.marshal.UTF8Type;
import org.apache.cassandra.serializers.MarshalException;
import org.apache.cassandra.utils.ByteBufferUtil;

@Beta
public class DelimiterAnalyzer extends AbstractAnalyzer
{
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();
    
    private static final Set<AbstractType<?>> VALID_ANALYZABLE_TYPES = new HashSet<AbstractType<?>>()
    {{
            add(UTF8Type.instance);
            add(AsciiType.instance);
    }};
    
    private String delimiter;
    private boolean caseSensitive;
    private AbstractType validator;
    private final List<String> tokens = new ArrayList<>();
    private Iterator<String> iter;

    public DelimiterAnalyzer()
    {
    }

    public ByteBuffer next()
    {
        return this.validator.fromString(iter.next());
    }

    public void init(Map<String, String> options, AbstractType validator)
    {
        this.validator = validator;
        DelimiterTokenizingOptions tokenizingOptions = DelimiterTokenizingOptions.buildFromMap(options);
        delimiter = tokenizingOptions.getDelimiter();
        caseSensitive = tokenizingOptions.isCaseSensitive();
    }

    public boolean hasNext()
    {
        // check that we know how to handle the input, otherwise bail
        if (!VALID_ANALYZABLE_TYPES.contains(validator))
            return false;

        return iter.hasNext();
    }

    public void reset(ByteBuffer input)
    {
        this.tokens.clear();
        String text = validator.getString(input);

        if (null == text)
            throw new MarshalException(String.format("'null' deserialized value for %s with %s", ByteBufferUtil.bytesToHex(input), validator));

        String[] parts = normalize(text).split(delimiter);
        for (String part : parts)
        {
            if (!Strings.isNullOrEmpty(part))
            {
                tokens.add(caseSensitive ? part : part.toLowerCase(DEFAULT_LOCALE));
            }
        }
        this.iter = tokens.iterator();
    }


    public boolean isTokenizing()
    {
        return true;
    }
}