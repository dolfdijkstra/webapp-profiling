/*
 * Copyright (C) 2006 Dolf Dijkstra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fatwire.gst.web.servlet.profiling.servlet.view;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fatwire.gst.web.servlet.profiling.servlet.CounterHistory;
import com.fatwire.gst.web.servlet.profiling.servlet.CounterHistoryBigInteger;
import com.fatwire.gst.web.servlet.profiling.servlet.jmx.ResponseTimeStatistic;
import com.fatwire.gst.web.servlet.profiling.servlet.view.format.HTMLFormat;
import com.fatwire.gst.web.servlet.profiling.servlet.view.format.TextFormat;

public final class CounterHtmlView extends BaseCounterView {

    public CounterHtmlView(final ResponseTimeStatistic root, final boolean on, final int concurrency,
            final CounterHistory counterHistory, final CounterHistoryBigInteger timeHistory) {
        super(root, on, concurrency, counterHistory, timeHistory);
    }

    @Override
    public void render(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=\"UTF-8\"");
        response.setCharacterEncoding("UTF-8");
        sendRefresh(request, response);
        final PrintWriter writer = response.getWriter();
        final TextFormat html = new HTMLFormat(writer);

        renderDocument(html);

    }

}
