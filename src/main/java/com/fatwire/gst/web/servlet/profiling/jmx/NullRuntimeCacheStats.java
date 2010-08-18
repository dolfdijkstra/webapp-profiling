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

import java.util.Date;

import com.fatwire.cs.core.cache.RuntimeCacheStats;

public class NullRuntimeCacheStats implements RuntimeCacheStats {

    public long getClearCount() {
        return 0;
    }

    public Date getCreatedDate() {
        return new Date(0);
    }

    public long getHits() {
        return 0;
    }

    public Date getLastFlushedDate() {
        return new Date(0);
    }

    public Date getLastPrunedDate() {
        return new Date(0);    }

    public long getMisses() {
        return 0;
    }

    public long getRemoveCount() {
        return 0;
    }

    public boolean hasINotifyObjects() {
        return false;
    }

}
