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
package org.apache.deltaspike.cdise.tck;


import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.tck.beans.CarRepair;
import org.junit.Assert;
import org.junit.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Set;

/**
 * TCK test for the {@link org.apache.deltaspike.cdise.api.CdiContainer}
 */
public class ContainerCtrlTckTest
{
    @Test
    public void testContainerBoot()
    {
        CdiContainer cc = CdiContainerLoader.getCdiContainer();
        Assert.assertNotNull(cc);

        cc.bootContainer();
        cc.startContexts();

        BeanManager bm = cc.getBeanManager();
        Assert.assertNotNull(bm);
        
        Set<Bean<?>> beans = bm.getBeans(CarRepair.class);
        Bean<?> bean = bm.resolve(beans);
        
        CarRepair carRepair = (CarRepair) bm.getReference(bean, CarRepair.class, bm.createCreationalContext(bean));
        Assert.assertNotNull(carRepair);

        Assert.assertNotNull(carRepair.getCar());
        Assert.assertNotNull(carRepair.getCar().getUsr());

        cc.stopContexts();
        cc.shutdownContainer();
    }

    @Test
    public void testSimpleContainerBoot()
    {
        CdiContainer cc = CdiContainerLoader.getCdiContainer();
        Assert.assertNotNull(cc);

        cc.start();

        BeanManager bm = cc.getBeanManager();
        Assert.assertNotNull(bm);

        Set<Bean<?>> beans = bm.getBeans(CarRepair.class);
        Bean<?> bean = bm.resolve(beans);

        CarRepair carRepair = (CarRepair) bm.getReference(bean, CarRepair.class, bm.createCreationalContext(bean));
        Assert.assertNotNull(carRepair);

        Assert.assertNotNull(carRepair.getCar());
        Assert.assertNotNull(carRepair.getCar().getUsr());

        cc.stop();
    }

    //X TODO reactivate after the update to owb 1.1.4
    //@Test

    /**
     * Stops and starts: application-, session- and request-scope.
     * <p/>
     * application-scoped instance has a ref to
     * request-scoped instance which has a ref to
     * session-scoped instance.
     * <p/>
     * If the deepest ref has the expected value, all levels in between were resetted correctly.
     */
    public void reStartContexts()
    {
        CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
        Assert.assertNotNull(cdiContainer);

        cdiContainer.start();

        BeanManager beanManager = cdiContainer.getBeanManager();
        Assert.assertNotNull(beanManager);

        Set<Bean<?>> beans = beanManager.getBeans(CarRepair.class);
        Bean<?> bean = beanManager.resolve(beans);

        CarRepair carRepair = (CarRepair)
            beanManager.getReference(bean, CarRepair.class, beanManager.createCreationalContext(bean));

        Assert.assertNotNull(carRepair);

        Assert.assertNotNull(carRepair.getCar());
        Assert.assertNotNull(carRepair.getCar().getUsr());

        carRepair.getCar().getUsr().setName("tester");
        Assert.assertEquals("tester", carRepair.getCar().getUsr().getName());

        cdiContainer.stopContexts();
        cdiContainer.stopContext(ApplicationScoped.class); //workaround for weld - see WELD-1072

        carRepair = (CarRepair)
            beanManager.getReference(bean, CarRepair.class, beanManager.createCreationalContext(bean));

        try
        {
            Assert.assertNotNull(carRepair.getCar());
            Assert.fail();
        }
        catch (ContextNotActiveException e)
        {
            //exception expected
        }

        cdiContainer.startContexts();

        carRepair = (CarRepair)
            beanManager.getReference(bean, CarRepair.class, beanManager.createCreationalContext(bean));

        Assert.assertNotNull(carRepair.getCar());
        Assert.assertNotNull(carRepair.getCar().getUsr());
        Assert.assertNull(carRepair.getCar().getUsr().getName());

        cdiContainer.stop();
    }
}
