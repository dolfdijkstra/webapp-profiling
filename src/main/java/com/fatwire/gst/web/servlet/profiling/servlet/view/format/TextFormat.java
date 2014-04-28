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
package com.fatwire.gst.web.servlet.profiling.servlet.view.format;

public interface TextFormat {

    public void startSection(String name);

    public void startSubSection(String name);

    public void endSection();

    public void endSubSection();

    public void endTable();

    public void startTable();

    public void msg(String msg);

    public void startDoc(String title);

    public void endDoc();

    public void td(String... o);

    public void tr(String... o);

    public void thr(String... o);

}
