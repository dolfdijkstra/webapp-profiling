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

package com.fatwire.gst.web.servlet.profiling.logger;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleTimeDebugParser implements TimeDebugParser {

    private static final String[] STRING_NULL = new String[0];

    interface MatcherStrategy {

        boolean matches(String s) throws Exception;
    }

    private final ParserCallback callBack;

    private MatcherStrategy[] strategies;

    public SimpleTimeDebugParser(ParserCallback callback) {
        this.callBack = callback;
        strategies = new MatcherStrategy[7];
        strategies[0] = new PagePattern2MatcherStrategy();
        strategies[1] = new PagePatternMatcherStrategy();
        strategies[2] = new ElementMatcherStrategy();
        strategies[3] = new PreparedStatementMatcherStrategy();
        strategies[4] = new QueryMatcherStrategy();
        strategies[5] = new QueryDotMatcherStrategy();
        strategies[6] = new UpdateMatcherStrategy();

    }

    public void parseIt(String s) throws Exception {
        for (MatcherStrategy strategy : strategies) {
            if (strategy.matches(s))
                return;
        }

    }

    private Pattern create(String type, boolean dot) {
        return Pattern.compile("Executed " + type + " (.+?) in (\\d{1,})ms" + (dot ? "." : ""), Pattern.DOTALL);
    }

    private String[] result(Matcher m) {

        if (m.matches()) {
            MatchResult mr = m.toMatchResult();
            String[] r = new String[mr.groupCount()];
            for (int i = 0; i < mr.groupCount(); i++) {
                r[i] = mr.group(i + 1);

            }
            return r;
        }
        return STRING_NULL;
    }

    private void update(String type, long time) {
        update(type, null, time);

    }

    private void update(String type, String subType, long time) {
        this.callBack.update(type, subType, time);

    }

    class PagePattern2MatcherStrategy implements MatcherStrategy {
        private Pattern pagePattern2 = Pattern
                .compile("Execute page (.+?) Hours: (\\d{1,}) Minutes: (\\d{1,}) Seconds: (\\d{1,}):(\\d{3})");

        @Override
        public boolean matches(String s) throws Exception {
            String[] r = pageResult2(pagePattern2.matcher(s));
            if (r.length == 2) {
                update("page", r[0], Long.parseLong(r[1]));
                return true;
            }
            return false;
        }

        private String[] pageResult2(Matcher m) {
            String[] r = STRING_NULL;
            if (m.matches()) {
                MatchResult mr = m.toMatchResult();
                if (mr.groupCount() == 5) {
                    long t = Long.parseLong(mr.group(2)) * (3600000L);
                    t += Long.parseLong(mr.group(3)) * (60000L);
                    t += Long.parseLong(mr.group(4)) * (1000L);
                    t += Long.parseLong(mr.group(5));
                    r = new String[2];
                    r[0] = mr.group(1);
                    r[1] = Long.toString(t);

                }

            }
            return r;
        }

    }

    class PagePatternMatcherStrategy implements MatcherStrategy {
        private Pattern pagePattern = Pattern
                .compile("Execute time  Hours: (\\d{1,}) Minutes: (\\d{1,}) Seconds: (\\d{1,}):(\\d{3})");

        @Override
        public boolean matches(String s) throws Exception {
            long[] pr = pageResult(pagePattern.matcher(s));
            if (pr.length == 1) {
                update("page", pr[0]);
                return true;
            }

            return false;
        }

        private long[] pageResult(Matcher m) {
            long[] r = new long[0];
            if (m.matches()) {
                MatchResult mr = m.toMatchResult();
                if (mr.groupCount() == 4) {
                    long t = Long.parseLong(mr.group(1)) * (3600000L);
                    t += Long.parseLong(mr.group(2)) * (60000L);
                    t += Long.parseLong(mr.group(3)) * (1000L);
                    t += Long.parseLong(mr.group(4));
                    r = new long[1];
                    r[0] = t;

                }

            }
            return r;
        }

    }

    private class UpdateMatcherStrategy implements MatcherStrategy {
        private Pattern updatePattern = create("update statement", true);

        @Override
        public boolean matches(String s) throws Exception {
            String[] r = result(updatePattern.matcher(s));
            if (r.length == 2) {
                update("sql", r[0], Long.parseLong(r[1]));
                return true;

            }
            return false;
        }

    }

    private class QueryDotMatcherStrategy implements MatcherStrategy {
        private Pattern queryPatternWithDot = create("query", true);

        @Override
        public boolean matches(String s) throws Exception {
            String[] r = result(queryPatternWithDot.matcher(s));
            if (r.length == 2) {
                update("sql", r[0], Long.parseLong(r[1]));
                return true;

            }
            return false;
        }

    }

    private class QueryMatcherStrategy implements MatcherStrategy {
        private Pattern queryPattern = create("query", false); // select,insert,delete,update

        @Override
        public boolean matches(String s) throws Exception {
            String[] r = result(queryPattern.matcher(s));
            if (r.length == 2) {
                update("sql", r[0], Long.parseLong(r[1]));
                return true;

            }

            return false;
        }

    }

    private class PreparedStatementMatcherStrategy implements MatcherStrategy {
        private Pattern preparedStatementPattern = create("prepared statement", false);

        @Override
        public boolean matches(String s) throws Exception {
            String[] r = result(preparedStatementPattern.matcher(s));
            if (r.length == 2) {
                update("sql", r[0], Long.parseLong(r[1]));
                return true;

            }
            return false;
        }

    }

    private class ElementMatcherStrategy implements MatcherStrategy {
        private Pattern elementPattern = create("element", true);

        @Override
        public boolean matches(String s) throws Exception {
            String[] r = result(elementPattern.matcher(s));
            if (r.length == 2) {
                update("element", r[0], Long.parseLong(r[1]));
                return true;
            }

            return false;
        }

    }

}
