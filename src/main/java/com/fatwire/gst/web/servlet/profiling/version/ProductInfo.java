/*
 * Copyright 2006 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */
package com.fatwire.gst.web.servlet.profiling.version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fatwire.cs.core.util.BuildBase;

public class ProductInfo implements ProductInfoMBean {
    final static Log log = LogFactory.getLog(ProductInfo.class);

    private String productName;

    private String productJar;

    private String productVersionInfoClass;

    private String productVersion;
    
    private String shortVersion;

    /**
     * @param productName
     * @param productJar
     * @param productVersionInfoClass
     * @throws Exception 
     */
    public ProductInfo(final String productName, final String productJar,
            final String productVersionInfoClass) throws Exception {
        super();
        this.productName = productName;
        this.productJar = productJar;
        this.productVersionInfoClass = productVersionInfoClass;
        collectVersion(Class.forName(productVersionInfoClass,true,Thread.currentThread().getContextClassLoader()));
    }
    public ProductInfo(final String productName, final String productJar,
            final Class<?> clazz) throws Exception {
        super();
        this.productName = productName;
        this.productJar = productJar;
        this.productVersionInfoClass = clazz.getName();
        collectVersion(clazz);
    }

    private void collectVersion(final Class<?> clazz) throws Exception {

        String version = null;
        
        if (BuildBase.class.isAssignableFrom(clazz)) {
            final Object o = clazz.newInstance();
            if (o instanceof BuildBase) {
                final BuildBase bb = (BuildBase) o;
                version = bb.version();
                String v = bb.version();
                String[] x= v.split("\n");
                if (x.length >1){
                    shortVersion = x[x.length-1];
                    version = v.replace('\n', ' ');
                }else {
                    version=v;
                }
            }
        } else {
            log.warn(clazz + " is not a buildbase");
        }
        productVersion = version;
    }

    /**
     * @return the productJar
     */
    public String getProductJar() {
        return productJar;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @return the productVersionInfoClass
     */
    public String getProductVersionInfoClass() {
        return productVersionInfoClass;
    }

    public String getProductVersionDescription() {
        return productVersion;
    }

    public String getVersion() {
        return shortVersion;
    }

}