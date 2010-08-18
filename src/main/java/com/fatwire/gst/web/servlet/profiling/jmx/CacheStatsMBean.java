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

package com.fatwire.gst.web.servlet.profiling.jmx;

import java.io.IOException;
import java.util.Date;

public interface CacheStatsMBean {

    /**
     * @return
     * @see com.fatwire.gst.web.servlet.core.cache.RuntimeCacheStats#getClearCount()
     */
    public long getClearCount() throws IOException;

    /**
     * @return
     * @see com.fatwire.gst.web.servlet.core.cache.RuntimeCacheStats#getCreatedDate()
     */
    public Date getCreatedDate() throws IOException;

    /**
     * @return
     * @see com.fatwire.gst.web.servlet.core.cache.RuntimeCacheStats#getHits()
     */
    public long getHits() throws IOException;

    /**
     * @return
     * @see com.fatwire.gst.web.servlet.core.cache.RuntimeCacheStats#getLastFlushedDate()
     */
    public long getLastFlushedElapsed() throws IOException;

    /**
     * @return
     * @see com.fatwire.gst.web.servlet.core.cache.RuntimeCacheStats#getLastPrunedDate()
     */
    public long getLastPrunedElapsed() throws IOException;

    /**
     * @return
     * @see com.fatwire.gst.web.servlet.core.cache.RuntimeCacheStats#getMisses()
     */
    public long getMisses() throws IOException;

    /**
     * @return
     * @see com.fatwire.gst.web.servlet.core.cache.RuntimeCacheStats#getRemoveCount()
     */
    public long getRemoveCount() throws IOException;

    /**
     * @return
     * @see com.fatwire.gst.web.servlet.core.cache.RuntimeCacheStats#hasINotifyObjects()
     */
    public boolean hasINotifyObjects() throws IOException;

    /**
     * The current size of the cache
     * @return
     * @throws IOException
     */
    public long getSize() throws IOException;
    
}