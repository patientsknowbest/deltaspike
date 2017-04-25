/*
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
package org.apache.deltaspike.core.impl.config;

import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.spi.config.ConfigSource;

import java.util.ArrayList;
import java.util.List;

/**
 * JMX MBean for DeltaSpike
 */
public class DeltaSpikeConfigMBean
{
    private final ClassLoader appConfigClassLoader;

    public DeltaSpikeConfigMBean(ClassLoader appConfigClassLoader)
    {
        this.appConfigClassLoader = appConfigClassLoader;
    }

    public List<String> getConfigSources()
    {
        ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(appConfigClassLoader);
            List<String> configSourceInfo = new ArrayList<String>();
            ConfigSource[] configSources = ConfigResolver.getConfigSources();
            for (ConfigSource configSource : configSources)
            {
                configSourceInfo.add(Integer.toString(configSource.getOrdinal()) +
                        " - " + configSource.getConfigName());
            }

            return configSourceInfo;
        }
        finally
        {
            // set back the original TCCL
            Thread.currentThread().setContextClassLoader(originalCl);
        }
    }


}