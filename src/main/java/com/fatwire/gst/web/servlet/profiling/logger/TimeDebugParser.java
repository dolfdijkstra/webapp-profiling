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

public class TimeDebugParser {

    public interface ParserCallback {
        void update(String type, String subType, long time);
    }

    private Pattern pagePattern = Pattern
            .compile("Execute time  Hours: (\\d{1,}) Minutes: (\\d{1,}) Seconds: (\\d{1,}):(\\d{3})");

    private Pattern pagePattern2 = Pattern
            .compile("Execute page (.+?) Hours: (\\d{1,}) Minutes: (\\d{1,}) Seconds: (\\d{1,}):(\\d{3})");

    private Pattern elementPattern = create("element", true);

    private Pattern preparedStatementPattern = create("prepared statement",
            false);

    private Pattern queryPattern = create("query", false); // select,insert,delete,update

    private Pattern updatePattern = create("update statement", true);

    private Pattern queryPatternWithDot = create("query", true);

    private final ParserCallback callBack;

    public TimeDebugParser(ParserCallback callback) {
        this.callBack = callback;
    }

    private String getStatementType(String s) {

        int t = s == null ? -1 : s.trim().indexOf(" ");
        if (t != -1) {
            return s.substring(0, t).toLowerCase();
        }
        return "unknown";
    }

    private Pattern create(String type, boolean dot) {
        return Pattern.compile("Executed " + type + " (.+?) in (\\d{1,})ms"
                + (dot ? "." : ""), Pattern.DOTALL);
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

    private String[] pageResult2(Matcher m) {
        String[] r = new String[0];
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

    private String[] result(Matcher m) {
        String[] r = new String[0];
        if (m.matches()) {
            MatchResult mr = m.toMatchResult();
            r = new String[mr.groupCount()];
            for (int i = 0; i < mr.groupCount(); i++) {
                r[i] = mr.group(i + 1);

            }
        }
        return r;
    }

    public void parseIt(String s) throws Exception {
        String[] r = this.pageResult2(pagePattern2.matcher(s));
        if (r.length == 2) {
            update("page", r[0], Long.parseLong(r[1]));
            return;
        }

        long[] pr = this.pageResult(pagePattern.matcher(s));
        if (pr.length == 1) {
            update("page", pr[0]);
            return;
        }

        r = result(elementPattern.matcher(s));
        if (r.length == 2) {
            update("element", r[0], Long.parseLong(r[1]));
            return;
        }
        r = result(preparedStatementPattern.matcher(s));
        if (r.length == 2) {
            update("sql", getStatementType(r[0]), Long.parseLong(r[1]));
            return;

        }
        r = result(queryPattern.matcher(s));
        if (r.length == 2) {
            update("sql", getStatementType(r[0]), Long.parseLong(r[1]));
            return;

        }
        r = result(queryPatternWithDot.matcher(s));
        if (r.length == 2) {
            update("sql", getStatementType(r[0]), Long.parseLong(r[1]));
            return;

        }
        r = result(updatePattern.matcher(s));
        if (r.length == 2) {
            update("sql", getStatementType(r[0]), Long.parseLong(r[1]));
            return;

        }

    }

    private void update(String type, long time) {
        update(type, null, time);

    }

    private void update(String type, String subType, long time) {
        this.callBack.update(type, subType, time);

    }

}
